/*
 * Copyright © 2026 Nicolás Cruz.
 * Todos los derechos reservados.
 *
 * Este software es de uso académico y privado.
 * Fecha de creación: 25 de septiembre del 2026
 */
package javafxnorepudio.controlador;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafxnorepudio.JavaFXNoRepudio;
import javafxnorepudio.modelo.pojo.Sesion;
import javafxnorepudio.modelo.servicio.AutenticacionService;
import javafxnorepudio.utilidades.Utilidades;

/**
 * FXML Controller class
 *
 * @author yazid
 */
public class FXMLInicioSesionController implements Initializable {

    @FXML
    private TextField tfPassword;
    @FXML
    private Label lblErrorUserName;
    @FXML
    private Label lblErrorPassword;
    @FXML
    private TextField tfUserName;
    @FXML
    private Label lblCorreoCodigo;
    @FXML
    private TextField tfCodigo;
    @FXML
    private Label lblErrorCorreo;
    @FXML
    private VBox vbCredenciales;
    @FXML
    private VBox vbCodigo;
    
    // Service de negocio - se inicializa fuera de los métodos que los usa porque el código vive en memoria
    // va a ser compartidos por dos metodos
    private AutenticacionService servicioAutenticacion = new AutenticacionService();
    String username;
    @FXML
    private Label lblErrorCodigo;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        mostrarVistaCredenciales(true);
    }

    @FXML
    private void btnSignIn(ActionEvent event) { // handleBotonIngresar
        String username = tfUserName.getText();
        String password = tfPassword.getText();

        if (sonCamposValidos(username, password)) {
            verificarSesion(username, password);
        }
    }

    private void verificarSesion(String username, String password) {
        try {
            Sesion sesion = servicioAutenticacion.iniciarSesion(username, password);
            if (!sesion.isError()) {
                this.username = username;
                mostrarVistaCredenciales(false);
                lblCorreoCodigo.setText("Ingresa el código de verificación que ha sido enviado a tu correo electrónico:" +
                        Utilidades.ocultarCorreoParcial(sesion.getUsuario().getCorreo()));
               mostrarVistaCredenciales(false);
                // Utilidades.mostrarAlertaSimple("Credenciales correctas", "Bienvenido(a)", Alert.AlertType.INFORMATION);
                // irPantallaSiguiente(); // <-- Invocación del método
            } else {
                Utilidades.mostrarAlertaSimple("Error de validación",
                        sesion.getMensaje(), Alert.AlertType.WARNING);
            }
        } catch (SQLException ex) {
            Utilidades.mostrarAlertaSimple("ERROR",
                    ex.getMessage(), Alert.AlertType.ERROR);
        }
    }
    
    @FXML
    private void handleButtonVerificarCodigo(ActionEvent event) {
        String codigo = tfCodigo.getText();
        lblErrorCodigo.setText("");
        if (codigo.isEmpty() || codigo.trim().isEmpty()) {
            lblErrorCodigo.setText("Debes ingresar el código para su verificación.");
        } else {
            verificarCodigo(this.username, codigo);
        }
    }
    
    private void verificarCodigo(String username, String codigo) {
        try {
            Sesion sesion = servicioAutenticacion.confirmarCodigo(username, codigo);
            if (!(sesion.isError())) {
                Utilidades.mostrarAlertaSimple("Código verificado correctamente", "Bienvenido(a)" +
                        sesion.getUsuario().getNombre() + "" +
                        sesion.getUsuario().getApellidoPaterno() + "" +
                        sesion.getUsuario().getApellidoMaterno(),
                        Alert.AlertType.CONFIRMATION);
                irPantallaSiguiente();
            } else {
                Utilidades.mostrarAlertaSimple("Error de validación de código",
                        sesion.getMensaje(),
                        Alert.AlertType.WARNING);
                if (!sesion.getMensaje().contains("incorrecto")) {
                    mostrarVistaCredenciales(true);
                }
            }
        } catch (SQLException ex) {
            Utilidades.mostrarAlertaSimple("ERROR",
                    ex.getMessage(), Alert.AlertType.ERROR);
            mostrarVistaCredenciales(true);
        }
    }
    
    /*
    // Los casos de uso no son ventanas, son acciones.
    private void verificarSesion(String username, String password) {
        AutenticacionService servicioAutenticacion = new AutenticacionService();
        try {
            Sesion sesion = servicioAutenticacion.iniciarSesion(username, password);
            if (!sesion.isError()) {
                Utilidades.mostrarAlertaSimple("Credenciales correctas", "Bienvenido(a)", Alert.AlertType.NONE);

            } else {
                Utilidades.mostrarAlertaSimple("Error de validación",
                        sesion.getMensaje(), Alert.AlertType.WARNING);
            }
        } catch (SQLException ex) {
            Utilidades.mostrarAlertaSimple("ERROR",
                    ex.getMessage(), Alert.AlertType.ERROR);
        }
    }
     */
    private boolean sonCamposValidos(String username, String password) {
        boolean esValido = true;
        lblErrorUserName.setText("");
        lblErrorPassword.setText("");
        if (username.isEmpty()) {
            lblErrorUserName.setText("Nombre de usuario requerido");
            esValido = false;
        }
        if (password.isEmpty()) {
            lblErrorPassword.setText("La contraseña es requerida");
            esValido = false;
        }

        return esValido;
    }

    private void irPantallaSiguiente() {
        try {
            Parent vista = FXMLLoader.load(JavaFXNoRepudio.class.getResource("vista/FXMLGestionTransferencia.fxml"));
            Scene escena = new Scene(vista);
            Stage stage = (Stage) tfUserName.getScene().getWindow();
            stage.setScene(escena);
            stage.setTitle("Transferencias");
            stage.show();
        } catch (IOException ex) {
            Logger.getLogger(FXMLInicioSesionController.class.getName()).log(Level.SEVERE, null, ex);
            Utilidades.mostrarAlertaSimple("Error al cargar pantalla",
                    "No se pudo cargar la vista de transferencias: " + ex.getMessage(),
                    Alert.AlertType.ERROR);
        }
    }

    /* // TODO
    private void irPantallaSiguiente() {
        try {
            Parent vista = FXMLLoader.load(JavaFXNoRepudio).class.getResource("vista/FXMLGestionTransferencia.fxml");
            Scene escena = new Scene(vista);
            Stage stage = tfUserName.getScene().getWindow();
            stage.setScene(escena);
            stage.setTitle("Transferencias");
            stage.show();
        } catch () {

        }
    }*/

    @FXML
    private void tfCodigo(ActionEvent event) {
        
    }
    
    private void mostrarVistaCredenciales(boolean vistaCredenciales) { // Depende del booleano que sea ingresado
        vbCredenciales.setVisible(vistaCredenciales);
        vbCredenciales.setManaged(vistaCredenciales); // las pantallas existen en memoria, pero esta los renderiza
        vbCodigo.setVisible(!vistaCredenciales);
        vbCodigo.setManaged(!vistaCredenciales);
        tfUserName.clear();
        tfPassword.clear();
        tfCodigo.clear();
    }
}
