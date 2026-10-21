/*
 * Copyright © 2026 Nicolás Cruz.
 * Todos los derechos reservados.
 *
 * Este software es de uso académico y privado.
 * Fecha de creación: 25 de septiembre del 2026
 */
package javafxnorepudio.utilidades;

import java.security.SecureRandom;
import javafx.scene.control.Alert;

/**
 *
 * @author yazid
 */
public class Utilidades {
    public static void mostrarAlertaSimple(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
    
    public static String generarCodigo() {
        int codigo = 100000 + new SecureRandom().nextInt(900000);
        return String.valueOf(codigo);
    }
    
    public static String ocultarCorreoParcial(String correo) {
        if ((correo == null) || (!correo.contains("@"))) { // esta no es una validación, es solo para saber si podemos trabajarlo
            return correo;
        }
        String[] partes = correo.split("@", 2);
        String usuario = partes[0];
        String dominio = partes[1];
        int longitudUsuario = usuario.length();
        if (longitudUsuario <= 3) {
            return usuario + "@" + dominio;
        }
        
        String ultimasLetras = usuario.substring(longitudUsuario - 3);
        StringBuilder asteriscos = new StringBuilder();
        int cantidadAsteriscos = longitudUsuario - 3;
        for (int i = 0; i < cantidadAsteriscos; i++) {
            asteriscos.append("*");
        }
        return asteriscos + ultimasLetras + "@" + dominio;
    }
}
