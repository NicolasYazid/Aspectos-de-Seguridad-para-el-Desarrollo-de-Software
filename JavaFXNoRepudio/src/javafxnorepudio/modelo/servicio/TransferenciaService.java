/*
 * Copyright © 2026 Nicolás Cruz && Isaac Vazqués.
 * Todos los derechos reservados.
 *
 * Este software es de uso académico y privado.
 * Fecha de creación: 16 de octubre del 2026
 */
package javafxnorepudio.modelo.servicio;

import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import javafxnorepudio.modelo.pojo.PeticionTransferencia;
import javafxnorepudio.modelo.pojo.RespuestaBanco;
import javafxnorepudio.utilidades.SecurityUtil;

/**
 *
 * @author yazid
 */
public class TransferenciaService {
    
    private final String IP_BANCO = "127.0.0.1";
    private final int PUERTO = 5005;
    
    public RespuestaBanco ejecutarTransferenciaSocket(String cuentaOrigen, String cuentaDestino, String monto) {
        System.out.println("[CLIENTE] Preparando transferencia para enviarlo al servidor.");
        PeticionTransferencia peticion = new PeticionTransferencia();
        peticion.setCuentaOrigen(cuentaOrigen);
        peticion.setCuentaDestino(cuentaDestino);
        try {
            peticion.setMonto(Double.parseDouble(monto));
        } catch (NumberFormatException e) {
            return new RespuestaBanco(400, "El monto ingresado no es un número válido.");
        }
        String datosRaw = peticion.obtenerDatosFirma();
        String clienteId = ConfigReader.getProperty("cliente.id");
        
        // TODO poner como objeto de una sola lectura
        String clienteSecret = ConfigReader.getProperty("cliente.secret");
        String firma = SecurityUtil.generarFirmaHMAC(datosRaw, clienteSecret);
        peticion.setAccessKeyId(clienteId);
        peticion.setFirmaCriptografica(firma);
        
        try (Socket socket = new Socket(IP_BANCO, PUERTO);
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in =
                        new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            String payloadJSON = new Gson().toJson(peticion);
            out.println(payloadJSON);
            String respuesta = in.readLine();
            System.out.println("[CLIENTE] Respuesta del banco: " + respuesta);
            return interpretarRespuesta(respuesta);
        } catch (IOException e) {
            System.err.println("[CLIENTE] Error de conexión: " + e.getMessage());
            return new RespuestaBanco(0, "No fue posible conectar con el banco.");
        }
    }

    // El banco responde "<código> <texto>", por ejemplo "401 NO AUTORIZADO: ..."
    private RespuestaBanco interpretarRespuesta(String respuesta) {
        if (respuesta == null || !respuesta.matches("^\\d{3}\\b.*")) {
            return new RespuestaBanco(0, "El banco no envió una respuesta válida.");
        }
        int codigo = Integer.parseInt(respuesta.substring(0, 3));
        String mensaje = respuesta.substring(3).replaceFirst("^[\\s:]+", "");
        return new RespuestaBanco(codigo, mensaje);
    }
}
