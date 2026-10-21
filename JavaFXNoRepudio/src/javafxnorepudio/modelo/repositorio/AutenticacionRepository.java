/*
 * Copyright © 2026 Nicolás Cruz.
 * Todos los derechos reservados.
 *
 * Este software es de uso académico y privado.
 * Fecha de creación: 25 de septiembre del 2026
 */
package javafxnorepudio.modelo.repositorio;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javafxnorepudio.modelo.ConexionBD;
import javafxnorepudio.modelo.pojo.Sesion;
import javafxnorepudio.modelo.pojo.Usuario;

/**
 *
 * @author yazid
 */
public class AutenticacionRepository {
    
    public Sesion verificarUsuarioCredenciales(
            String username, String passwordHashed) throws SQLException {
        Sesion sesion = new Sesion();
        
        String query = "SELECT idUsuario, correo, telefono " +
                "FROM usuarios " + 
                "WHERE username = ? AND password = ?";
        
        // Si no tiene error entra al try, sino entra al catch
        // Dentro del parentesis podemos poner asignaciones
        // Usamos un try con resources para cuando la operación depende de que más de una condición se cumpla
        try (Connection conexion = ConexionBD.obtenerConexion();
                PreparedStatement ps = conexion.prepareStatement(query)) {
            ps.setString(1, username);
            ps.setString(2, passwordHashed);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                // obtener la información
                Usuario usuario = new Usuario();
                usuario.setIdUsuario(rs.getInt("idUsuario"));
                usuario.setCorreo(rs.getString("correo"));
                usuario.setTelefono(rs.getString("telefono"));
                sesion.setError(false);
                sesion.setUsuario(usuario); // ¿ni idea de este?
            } else {
                sesion.setError(true);
                sesion.setMensaje("Usuario y/o contraseña incorrectos, verifica la información.");
            }
            
            rs.close();
            return sesion;
        } catch (SQLException e) {
            throw new SQLException("Error de conexión: " + e.getMessage());
        }
    }
    
    public Sesion buscarUsuarioPorUsername(String username) throws SQLException {
        Sesion sesion = new Sesion();
        String consulta = "SELECT idUsuario, nombre, apellidoPaterno, apellidoMaterno, username, u.idRol, rol " +
                "FROM usuarios u " + 
                "INNER JOIN roles r ON r.idRol = u.idRol " +
                "WHERE username = ?";
        try (Connection conexion = ConexionBD.obtenerConexion()) {
            PreparedStatement ps = conexion.prepareStatement(consulta);
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Usuario usuario = new Usuario();
                usuario.setIdUsuario(rs.getInt("idUsuario"));
                usuario.setNombre(rs.getString("nombre"));
                usuario.setApellidoPaterno(rs.getString("apellidoPaterno"));
                usuario.setApellidoMaterno((rs.getString("apellidoMaterno") != null) ? rs.getString("apellidoMaterno") : "");
                usuario.setUsername(rs.getString("username"));
                usuario.setIdRol(rs.getInt("idRol"));
                usuario.setRol(rs.getString("Rol"));
                sesion.setError(false);
                sesion.setUsuario(usuario);
            } else {
                sesion.setError(true);
                sesion.setMensaje("Lo sentimos el nombre de usuario no ha sido veriicado");
            }
            rs.close();
            return sesion;
        } catch (SQLException ex) {
            throw new SQLException("Error de conexión: " + ex.getMessage());
        }
    }    
}
