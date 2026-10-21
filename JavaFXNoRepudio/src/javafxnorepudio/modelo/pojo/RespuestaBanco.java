/*
 * Copyright © 2026 Nicolás Cruz.
 * Todos los derechos reservados.
 *
 * Este software es de uso académico y privado.
 */
package javafxnorepudio.modelo.pojo;

/**
 * Respuesta del banco a una transferencia: un código tipo HTTP y su mensaje.
 * El código 0 indica que no hubo respuesta del servidor.
 */
public class RespuestaBanco {

    private final int codigo;
    private final String mensaje;

    public RespuestaBanco(int codigo, String mensaje) {
        this.codigo = codigo;
        this.mensaje = mensaje;
    }

    public int getCodigo() {
        return codigo;
    }

    public String getMensaje() {
        return mensaje;
    }
}
