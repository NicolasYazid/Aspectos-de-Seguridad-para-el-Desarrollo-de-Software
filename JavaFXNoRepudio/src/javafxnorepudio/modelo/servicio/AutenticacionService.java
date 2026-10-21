/*
 * Copyright © 2026 Nicolás Cruz.
 * Todos los derechos reservados.
 *
 * Este software es de uso académico y privado.
 * Fecha de creación: 25 de septiembre del 2026
 */
package javafxnorepudio.modelo.servicio;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafxnorepudio.modelo.pojo.Sesion;
import javafxnorepudio.modelo.repositorio.AutenticacionRepository;
import javafxnorepudio.utilidades.MfaData;
import javafxnorepudio.utilidades.SecurityUtil;
import javafxnorepudio.utilidades.Utilidades;

/**
 * Aquí van las reglas de negocio Las reglas de negocio no van en el
 * controlador, los controllers
 *
 * EN CLIENTE SRVIDOR ¿LOS CÓDIGOS QUE NUNCA FUERON INGRESADOS DÓNDE SE
 * GUARDARÁN? El controller solo se encagará de la vista
 *
 * @author yazid
 */
public class AutenticacionService {

    private final AutenticacionRepository repositorio;
    private final Map<String, MfaData> codigosPendientes;

    // CÓDIGO: CACHÉ EN MIDDLE WARE PARA CLIENTE-SERVIDOR
    public AutenticacionService() {
        this.codigosPendientes = new LinkedHashMap<>();
        this.repositorio = new AutenticacionRepository();
    }
    
    /*
    public Sesion iniciarSesion(String usuario, String password) throws SQLException {
        System.out.println(">>> iniciarSesion llamado con usuario: " + usuario);
        Sesion sesion = new Sesion();
        if (usuario == null || password == null || usuario.trim().isEmpty()) {
            sesion.setError(true);
            sesion.setMensaje("Los campos no presentan el formato adecuado");
            return sesion;
        }

        try {
            String passwordHash = SecurityUtil.generarCifradoSHA256(password);
            sesion = repositorio.verificarUsuarioCredenciales(usuario, passwordHash);
            System.out.println(">>> error=" + sesion.isError() + " mensaje=" + sesion.getMensaje()
                    + " usuario=" + sesion.getUsuario());

            if (!sesion.isError()) {
                String codigoMfa = Utilidades.generarCodigo();
                System.out.println(">>> Código MFA: " + codigoMfa);   // sale siempre que las credenciales sean válidas
                codigosPendientes.put(usuario, new MfaData(codigoMfa));
                enviarCorreoMFA(sesion.getUsuario().getCorreo(), codigoMfa);
            }
            return sesion;
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException ex) {
            ex.printStackTrace();
            throw new SQLException("Error al intentar verificar la información.");
        }
    }*/

    /*
    public Sesion iniciarSesion(String usuario, String password) throws SQLException {
        Sesion sesion = new Sesion();
        if (usuario == null || password == null || usuario.trim().isEmpty()) {
            sesion.setError(true);
            sesion.setMensaje("Los campos no presentan el formato adecuado");
            return sesion;
        }

        try {
            String passwordHash = SecurityUtil.generarCifradoSHA256(password);
            sesion = repositorio.verificarUsuarioCredenciales(usuario, passwordHash);
            if (!sesion.isError()) {
                String codigoMfa = Utilidades.generarCodigo();
                codigosPendientes.put(usuario, new MfaData(codigoMfa));
                enviarCorreoMFA(sesion.getUsuario().getCorreo(), codigoMfa);
            }
            return sesion;
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException excepcionDeCifrado) {
            throw new SQLException("Error al intentar verificar la información.");
        }
    }*/
    
    public Sesion iniciarSesion(String usuario, String password) throws SQLException {
        try {
            Sesion sesion = new Sesion();
            if (usuario == null || password == null || usuario.trim().isEmpty()) {
                sesion.setError(true);
                sesion.setMensaje("Los campos no representan el formato adecuado"); // Esto es un flujo alterno
                return sesion;
            }
            String passwordHash = SecurityUtil.generarCifradoSHA256(password);

            // El profesor tiene claro y es conciente de que un fallo en el cifrado no es una SQL Exception,
            // pero lo usamos para agrupar las excepciones en una sola
            sesion = repositorio.verificarUsuarioCredenciales(usuario, passwordHash);
            if (!sesion.isError()) {
                String codigoMfa = Utilidades.generarCodigo();
                codigosPendientes.put(usuario, new MfaData(codigoMfa));
                enviarCorreoMFA(sesion.getUsuario().getCorreo(), codigoMfa);
            }
            return sesion;
            // Excepciones: pero usar el padre de exepciones o usar ambas
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException ex) {
            throw new SQLException("Error al intentar verificar la información.");
        }
    }
    
    public Sesion confirmarCodigo(String usuario, String codigo) throws SQLException {
        Sesion sesion = new Sesion();
        MfaData verificacionCodigo = codigosPendientes.get(usuario);

        if (verificacionCodigo == null) {
            sesion.setError(true);
            sesion.setMensaje("Lo sentimos hubo un problema al intentar verificar el código, por favor vuelva a realizar el proceso.");
            return sesion;
        }

        if (verificacionCodigo.getCodigo().equals(codigo) && !verificacionCodigo.estaExpirado()) {
            codigosPendientes.remove(usuario);
            sesion = repositorio.buscarUsuarioPorUsername(usuario);
        } else if (!(verificacionCodigo.getCodigo().equals(codigo))) {
            sesion.setError(true);
            sesion.setMensaje("El código ingresado es incorrecto, por favor verifique lo que ha ingresado.");
        } else {
            sesion.setError(true);
            sesion.setMensaje("Lo sentimos el código ingresado ha expirado, por favor repita el proceso de nuevo.");
            codigosPendientes.remove(usuario);
        }
        return sesion;
    }

    private void enviarCorreoMFA(String correo, String codigo) {
        System.out.println("Correo destino: [" + correo + "]");
        System.out.println("Código: " + codigo);
        EmailService email = new EmailService();
        new Thread(() -> {
            email.enviarCorreoMfa(correo, codigo);
        }).start();
    }
}
