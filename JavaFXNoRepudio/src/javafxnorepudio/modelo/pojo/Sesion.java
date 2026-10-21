/*
 * Copyright © 2026 Nicolás Cruz.
 * Todos los derechos reservados.
 *
 * Este software es de uso académico y privado.
 * Fecha de creación: 2 de octubre del 2026
 */
package javafxnorepudio.modelo.pojo;

/**
 *
 * @author yazid
 */
public class Sesion {
    
    private boolean error;
    private String mensaje;
    private Usuario usuario;
    
    public Sesion() {
    }
    
    public Sesion(boolean error, String mensaje, Usuario usuario) {
        this.error = error;
        this.mensaje = mensaje;
        this.usuario = usuario;
    }

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }    
}
