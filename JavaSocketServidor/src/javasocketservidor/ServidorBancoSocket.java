/*
 * Copyright © 2026 Nicolás Cruz.
 * Todos los derechos reservados.
 *
 * Este software es de uso académico y privado.
 * Fecha de creación: 15 de octubre del 2026
 */
package javasocketservidor;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.regex.Pattern;
import javasocketservidor.modelo.ConfigReader;
import javasocketservidor.modelo.dto.PeticionTransferencia;
import javasocketservidor.utilidades.SecurityUtil;

/**
 *
 * @author yazid
 */
public class ServidorBancoSocket {
    
    private static final int PUERTO = 5005;
    private static final Pattern ACCESS_KEY_ID_VALIDO = Pattern.compile("^[A-Za-z0-9_]{1,64}$");
    
    public static void main(String[] args) {
        System.out.println("[BANCO 5T] Iniciando servidor de transferencias en el puerto " + PUERTO);
        try (ServerSocket serverSocket = new ServerSocket(PUERTO)) {
            while (true) {
                System.out.println("[BANCO 5T] Esperando conexiones de los clientes...");
                Socket socketCliente = serverSocket.accept();
                System.out.println("[BANCO 5T] Cliente conectado desde la IP: " + socketCliente.getInetAddress()); // me da la dirección IP del servidor
                manejarPeticion(socketCliente);
            }
        } catch (IOException e) {
            System.out.println("[BANCO 5T] Error en el servidor" + e.getMessage());
        }
    }
    
    private static void manejarPeticion(Socket socket) {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true)
                ) {
            String datosRecibidos = in.readLine();
            System.out.println("[BANCO 5T] Payload JSON recibido: " + datosRecibidos);
            String respuesta;
            try {
                respuesta = procesarPeticion(datosRecibidos);
            } catch (Exception e) {
                System.err.println("[BANCO 5T] Error interno: " + e);
                respuesta = "500 INTERNAL SERVER ERROR: Error interno del servidor.";
            }
            out.println(respuesta);
        } catch (IOException e) {
            System.err.println("[BANCO 5T] Error al procesar la solicitud...");
        }
    }

    // Devuelve la respuesta del banco con el formato "<código> <texto>"
    private static String procesarPeticion(String datosRecibidos) {
        if ((datosRecibidos == null) || (datosRecibidos.isEmpty())) {
            return "400 BAD REQUEST: Formato JSON incorrecto.";
        }
        PeticionTransferencia peticion;
        try {
            peticion = new Gson().fromJson(datosRecibidos, PeticionTransferencia.class);
        } catch (JsonParseException | NumberFormatException e) {
            System.out.println("[BANCO 5T] RECHAZADO: Error de serialización de datos");
            return "400 BAD REQUEST: Error de serialización de datos.";
        }
        if (peticion == null || peticion.getFirmaCriptografica() == null) {
            System.out.println("[BANCO 5T] RECHAZADO: Petición incompleta");
            return "400 BAD REQUEST: Error de serialización de datos.";
        }
        String accessKeyId = peticion.getAccessKeyId();
        System.out.println("[BANCO 5T] Verificando identidad de: " + accessKeyId);
        String llaveSecreta = null;
        if (accessKeyId != null && ACCESS_KEY_ID_VALIDO.matcher(accessKeyId).matches()) {
            llaveSecreta = ConfigReader.getProperty("cliente." + accessKeyId);
        }
        if (llaveSecreta == null) {
            System.out.println("[BANCO 5T] RECHAZADO: Cliente desconocido");
            return "401 NO AUTORIZADO: Access Key ID inválido.";
        }
        String datosRaw = peticion.obtenerDatosFirma();
        String firmaCalculadaBanco = SecurityUtil.generarFirmaHMAC(datosRaw, llaveSecreta);
        System.out.println("[BANCO 5T] Firma recibida: " + peticion.getFirmaCriptografica());
        System.out.println("[BANCO 5T] Firma calculada: " + firmaCalculadaBanco);
        if (firmaCalculadaBanco.equals(peticion.getFirmaCriptografica())) {
            System.out.println("[BANCO 5T] Firma valida, ejecutando transferencia por $" + peticion.getMonto());
            return "200 OK: Transferencia completada con éxito.";
        }
        System.out.println("[BANCO 5T] ALERTA: La firma no coincide, posible Riesgo de manipulación (tampering)");
        return "403 FORBIDDEN: Firma digital inválida.";
    }
}
