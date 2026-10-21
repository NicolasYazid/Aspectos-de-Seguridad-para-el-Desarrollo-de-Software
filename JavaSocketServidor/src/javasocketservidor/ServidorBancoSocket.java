/*
 * Copyright © 2026 Nicolás Cruz.
 * Todos los derechos reservados.
 *
 * Este software es de uso académico y privado.
 * Fecha de creación: 15 de octubre del 2026
 */
package javasocketservidor;

import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import javasocketservidor.modelo.ConfigReader;
import javasocketservidor.modelo.dto.PeticionTransferencia;
import javasocketservidor.utilidades.SecurityUtil;

/**
 *
 * @author yazid
 */
public class ServidorBancoSocket {
    
    private static final int PUERTO = 5005;
    
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
            if ((datosRecibidos != null) && (!datosRecibidos.isEmpty())) {
                PeticionTransferencia peticion = new Gson().fromJson(datosRecibidos, PeticionTransferencia.class);
                System.out.println("[BANCO 5T] Verificando identidad de: " + peticion.getAccessKeyId());
                String llaveSecreta = ConfigReader.getProperty("cliente." + peticion.getAccessKeyId());
                if (llaveSecreta == null) {
                    System.out.println("[BANCO 5T] RECHAZADO: Cliente desconocido");
                    out.println("401 NO AUTORIZADO: Cliente no reconocido");
                    return;
                }
                String datosRaw = peticion.obtenerDatosFirma();
                String firmaCalculadaBanco = SecurityUtil.generarFirmaHMAC(datosRaw, llaveSecreta);
                System.out.println("[BANCO 5T] Firma recibida: " + peticion.getFirmaCriptografica());
                System.out.println("[BANCO 5T] Firma calculada: " + firmaCalculadaBanco);
                if (firmaCalculadaBanco.equals(peticion.getFirmaCriptografica())) {
                    System.out.println("[BANCO 5T] Firma valida, ejecutando transferencia por $" + peticion.getMonto());
                    out.println("200 OK: Transferencia completada con éxito.");
                } else {
                    System.out.println("[BANCO 5T] ALERTA: La firma no coincide, posible Riesgo de manipulación (tampering)" );
                    out.println("403: FORBIDDEN: Firma digital invalida");
                }
            } else {
                out.println("400 BAD REQUEST: Formato JSON incorrecto.");
            }
        } catch (IOException e) {
            System.err.println("[BANCO 5T] Error al procesar la solicitud...");
        }
    }    
}
