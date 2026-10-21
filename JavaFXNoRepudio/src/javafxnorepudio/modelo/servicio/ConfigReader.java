/*
 * Copyright © 2026 Nicolás Cruz && Isaac Vazqués.
 * Todos los derechos reservados.
 *
 * Este software es de uso académico y privado.
 * Fecha de creación: 15 de octubre del 2026
 */
package javafxnorepudio.modelo.servicio;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 *
 * @author yazid
 */
public class ConfigReader {
    
    private static final Properties properties = new Properties();
    
    static {
        try {
            InputStream input = ConfigReader.class.getResourceAsStream("cliente_banco.properties");
            if (input == null) {
                throw new RuntimeException("ERROR: No se encontro el archivo cliente_banco.properties");
            }
            properties.load(input);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static String getProperty(String key) {
        return properties.getProperty(key);
    }    
}
