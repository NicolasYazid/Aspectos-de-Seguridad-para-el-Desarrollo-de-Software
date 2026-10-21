/*
 * Copyright © 2026 Nicolás Cruz.
 * Todos los derechos reservados.
 *
 * Este software es de uso académico y privado.
 * Fecha de creación: 8 de octubre del 2026
 */
package javafxnorepudio.utilidades;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 *
 * @author yazid
 */
public class MfaData {
    
    private final String codigo;
    private final LocalDateTime tiempoCreacion;
    private static final int MINUTOS_VIGENCIA = 2;
    
    public MfaData(String codigo) {
        this.codigo = codigo;
        this.tiempoCreacion = LocalDateTime.now();
    }

    public String getCodigo() {
        return codigo;
    }
    
    public boolean estaExpirado() {
        Duration duracion = Duration.between(tiempoCreacion, LocalDateTime.now());
        return duracion.toMinutes() > MINUTOS_VIGENCIA;
    }
    
    
    
}
