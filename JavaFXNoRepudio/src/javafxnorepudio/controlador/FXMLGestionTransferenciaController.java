/*
 * Copyright © 2026 Nicolás Cruz.
 * Todos los derechos reservados.
 *
 * Este software es de uso académico y privado.
 * Fecha de creación: 11 de septiembre del 2026
 */
package javafxnorepudio.controlador;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafxnorepudio.modelo.servicio.TransferenciaService;
import javafxnorepudio.utilidades.SecurityUtil;
import javafxnorepudio.utilidades.Utilidades;

/**
 *
 * @author yazid
 */
public class FXMLGestionTransferenciaController implements Initializable {

    @FXML
    private TextField tfCuentaOrigen;
    @FXML
    private TextField tfCuentaDestino;
    @FXML
    private TextField tfMonto;
    private final String USUARIO_ACTIVO = "CAJERO_ALOPEZ";

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

    @FXML
    private void handleBotonTransferencia(ActionEvent event) {
        if (camposValidos()) {
            String cuentaOrigen = tfCuentaOrigen.getText();
            String cuentaDestino = tfCuentaDestino.getText();
            String monto = tfMonto.getText();

            // realizarTransferencia(cuentaOrigen, cuentaDestino, monto);
            realizarTransferenciaServidor(cuentaOrigen, cuentaDestino, monto);
        } else {
            Utilidades.mostrarAlertaSimple("Campos vaciós", "Para realizar una transferencia debes llenar toda la información del formulario", Alert.AlertType.WARNING);
        }
    }
    
    private void realizarTransferenciaServidor(String cuentaOrigen, String cuentaDestino, String monto) {
        TransferenciaService transferencia = new TransferenciaService();
        transferencia.ejecutarTransferenciaSocket(cuentaOrigen, cuentaDestino, monto);
    }
    
    // Recuerda que la seguridad es un elemento adicional a la lógica de negocio
    private void realizarTransferencia(String cuentaOrigen, String cuentaDestino, String monto) {
        try {
            // Define el formato deseado (ejemplo: 2026-09-18 14:30:15)
            DateTimeFormatter formateador = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String tiempoTransferencia = LocalDateTime.now().toString(); // LocalDate te da un Date, por eso usamos toString para regresar la cadena
            String datosFirmaDigital = String.format("%s-%s-%s-%s-%s-%s",
                    tiempoTransferencia,
                    USUARIO_ACTIVO, "TRANSFERENCIA",
                    cuentaOrigen, cuentaDestino, monto);

            // TODO Proceso de Transferencia (lógica de negocio)
            String firmaDigital = SecurityUtil.generarCifradoSHA256(datosFirmaDigital);
            // Proceso para bitacora
            FileWriter fw = new FileWriter("auditoria_5t.log", true); //true es para agregar contenido sobre lo que ya está
            PrintWriter pw = new PrintWriter(fw);
            String datosLog = String.format("Tiempo de operación: %s, Usuario: %s, Operación: %s, Origen: %s, Destino: %s, Monto: %s, Firma: %s",
                    tiempoTransferencia, USUARIO_ACTIVO, "TRANSFERENCIA", cuentaOrigen, cuentaDestino, monto, firmaDigital
            );
            pw.println(datosLog);
            pw.close();
            fw.close();
            //--
            Utilidades.mostrarAlertaSimple("Transferencia exitosa",
                    "La transferencia a la cuenta: " + cuentaDestino + " por $" + monto + " se ha realizado exitosamente.",
                    Alert.AlertType.INFORMATION);

            tfCuentaOrigen.setText("");
            tfCuentaDestino.setText("");
            tfMonto.setText("");

        } catch (NoSuchAlgorithmException ex) {
            Utilidades.mostrarAlertaSimple("Error en algoritmo", ex.getMessage(), Alert.AlertType.ERROR);
        } catch (UnsupportedEncodingException ex) {
            Utilidades.mostrarAlertaSimple("Error de cifrado", ex.getMessage(), Alert.AlertType.ERROR);
        } catch (IOException io) {
            Utilidades.mostrarAlertaSimple("Error en bitácora", io.getMessage(), Alert.AlertType.ERROR);
        }

    }

    private boolean camposValidos() {
        if (tfCuentaOrigen.getText().isEmpty()) {
            return false;
        }
        if (tfCuentaDestino.getText().isEmpty()) {
            return false;
        }
        if (tfMonto.getText().isEmpty()) {
            return false;
        }
        return true;
    }
}
