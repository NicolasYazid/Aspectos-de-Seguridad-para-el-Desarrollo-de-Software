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
import javafxnorepudio.utilidades.SecurityUtil;

/**
 *
 * @author yazid
 */
public class TransferenciaService {
    
    private final String IP_BANCO = "127.0.0.1";
    private final int PUERTO = 5005;
    
    public void ejecutarTransferenciaSocket(String cuentaOrigen, String cuentaDestino, String monto) {
        System.out.println("[CLIENTE] Preparando transferencia para enviarlo al servidor.");
        PeticionTransferencia peticion = new PeticionTransferencia();
        peticion.setCuentaOrigen(cuentaOrigen);
        peticion.setCuentaDestino(cuentaDestino);
        peticion.setMonto(Double.parseDouble(monto));
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
        } catch (IOException e) {
            System.err.println("[CLIENTE] Error de conexión: " + e.getMessage());
        }
    }
}
