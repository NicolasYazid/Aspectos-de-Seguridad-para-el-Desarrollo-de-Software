/*
 * Copyright © 2026 Nicolás Cruz.
 * Todos los derechos reservados.
 *
 * Este software es de uso académico y privado.
 * Fecha de creación: 25 de septiembre del 2026
 */
package javafxnorepudio.modelo;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

// Esto no es el patrón de diseño Singleton
/**
 *
 * @author yazid
 */
public class ConexionBD {
    
    private static final Properties properties = new Properties();
    
    // Esto es un bloque estático
    static {
        try {
            InputStream input = ConexionBD.class.getResourceAsStream("database.properties");
            if (input == null) {
                throw new RuntimeException("ERROR: No se encontro el archivo database.properties en el classpath");
            }
            properties.load(input);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static Connection obtenerConexion() throws SQLException {
        try {
            Class.forName(properties.getProperty("db.driver"));
            return DriverManager.getConnection(properties.getProperty("db.url"),
                    properties.getProperty("db.user"),
                    properties.getProperty("db.password"));
        } catch (ClassNotFoundException ex) {
            throw new SQLException("No se encontró el driver de MySQL: " + ex.getMessage());
        }
    }
    
}
