/*
 * Copyright © 2026 Nicolás Cruz.
 * Todos los derechos reservados.
 *
 * Este software es de uso académico y privado.
 * Fecha de creación: 11 de septiembre del 2026
 */
package javafxnorepudio.utilidades;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 *
 * @author yazid
 */
public class SecurityUtil {
    
    //TODO
    public static String generarCifradoSHA256(String datos)
            throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(datos.getBytes("UTF-8"));
        StringBuilder hexString = new StringBuilder();
        for(byte b : hash) {
            String hexadecimal = Integer.toHexString(0xff & b); // b es el byte de entrada y lo quiero en 0xff - es el rango basicamente
            System.out.println("Hexadecimal: " + hexadecimal);
            if (hexadecimal.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hexadecimal); // le agregamos al hexString la cadena
        }
        
        //por qué usar StringBuilder en lugar de String = que es StringBuilder diferencia con String
        return hexString.toString();
    }
    
    //para la internacionalización nos sirve, pero debe ser global, en las reglas de negocio, para que no batallemos con todos los lugares    
    public static String generarFirmaHMAC(String datos, String llaveSecreta) {
        try {
            Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKey = new SecretKeySpec(llaveSecreta.getBytes("UTF-8"), "HmacSHA256");
            sha256_HMAC.init(secretKey);
            byte[] hash = sha256_HMAC.doFinal(datos.getBytes("UTF-8"));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException("Error criptográfico: ", e);
        }
    }
}
