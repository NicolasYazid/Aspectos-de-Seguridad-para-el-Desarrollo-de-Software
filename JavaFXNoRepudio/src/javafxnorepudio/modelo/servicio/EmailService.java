package javafxnorepudio.modelo.servicio;

import java.util.Properties;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 *
 * @author raymon
 */
public class EmailService {
    
    private final String remitente = "yazidnicolasch@gmail.com";
    private final String password = "ucwc pbzh nlmo dccr";
    
    public void enviarCorreoMfa(String destinatario, String codigoMfa) {
        // 1. Configurar las propiedades del servidor SMTP de Gmail
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.ssl.protocols", "TLSv1.2"); 
        // 2. Crear la sesión con autenticación
        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(remitente, password);
            }
        });
        try {
            // 3. Construir el mensaje
            Message mensaje = new MimeMessage(session);
            mensaje.setFrom(new InternetAddress(remitente, "Verificación Banco 5T"));
            mensaje.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destinatario));
            mensaje.setSubject("Tu código de verificación del Banco del Bienestar 5T");
            // 4. Inyectar el código en la plantilla del HTML
            String contenidoHtml = generarPlantillaHtml(codigoMfa);
            mensaje.setContent(contenidoHtml, "text/html; charset=utf-8");
            // 5. Enviar del correo
            Transport.send(mensaje);
            System.out.println("Correo enviado exitosamente a: " + destinatario);
        } catch (Exception e) {
            System.err.println("Error al enviar el correo: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private String generarPlantillaHtml(String codigo) {
        return "<div style='font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; border: 1px solid #e0e0e0; border-radius: 8px; overflow: hidden;'>"
             + "    <div style='background-color: #2a75d3; padding: 20px; text-align: center;'>"
             + "        <h2 style='color: white; margin: 0;'>Banco del Bienestar 5T</h2>"
             + "    </div>"
             + "    <div style='padding: 30px; text-align: center; color: #333333;'>"
             + "        <p style='font-size: 16px;'>Estimado(a) colaborador(a),</p>"
             + "        <p style='font-size: 16px;'>Se ha solicitado un inicio de sesión en tu cuenta. Usa el siguiente código para completar el proceso:</p>"
             + "        <div style='margin: 30px 0;'>"
             + "            <span style='font-size: 32px; font-weight: bold; letter-spacing: 8px; color: #2a75d3; background-color: #f5f7fa; padding: 15px 25px; border-radius: 8px;'>" 
             +                  codigo 
             + "            </span>"
             + "        </div>"
             + "        <p style='font-size: 14px; color: #777777;'>Este código es válido por 2 minutos. Si no fuiste tú, ignora este correo.</p>"
             + "    </div>"
             + "    <div style='background-color: #f5f7fa; padding: 15px; text-align: center; font-size: 12px; color: #aaaaaa;'>"
             + "        © 2026 Banco del Bienestar 5T. Este es un mensaje automatizado."
             + "    </div>"
             + "</div>";
    }
}
