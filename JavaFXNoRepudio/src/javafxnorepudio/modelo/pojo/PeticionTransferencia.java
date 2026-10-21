/*
 * Copyright © 2026 Nicolás Cruz && Isaac Vazqués.
 * Todos los derechos reservados.
 *
 * Este software es de uso académico y privado.
 * Fecha de creación: 15 de octubre del 2026
 */
package javafxnorepudio.modelo.pojo;

/**
 *
 * @author yazid
 */
public class PeticionTransferencia {
    private String accessKeyId;
    private String firmaCriptografica;
    private String cuentaOrigen;
    private String cuentaDestino;
    private double monto;
    
    public PeticionTransferencia() {
    }

    public PeticionTransferencia(String accessKeyId, String firmaCriptografica, String cuentaOrigen, String cuentaDestino, double monto) {
        this.accessKeyId = accessKeyId;
        this.firmaCriptografica = firmaCriptografica;
        this.cuentaOrigen = cuentaOrigen;
        this.cuentaDestino = cuentaDestino;
        this.monto = monto;
    }

    public String getAccessKeyId() {
        return accessKeyId;
    }

    public void setAccessKeyId(String accessKeyId) {
        this.accessKeyId = accessKeyId;
    }

    public String getFirmaCriptografica() {
        return firmaCriptografica;
    }

    public void setFirmaCriptografica(String firmaCriptografica) {
        this.firmaCriptografica = firmaCriptografica;
    }

    public String getCuentaOrigen() {
        return cuentaOrigen;
    }

    public void setCuentaOrigen(String cuentaOrigen) {
        this.cuentaOrigen = cuentaOrigen;
    }

    public String getCuentaDestino() {
        return cuentaDestino;
    }

    public void setCuentaDestino(String cuentaDestino) {
        this.cuentaDestino = cuentaDestino;
    }

    public double getMonto() {
        return monto;
    }

    public void setMonto(double monto) {
        this.monto = monto;
    }
    
    public String obtenerDatosFirma() {
        // String .format("%s | %s | %s", cuentaOrigen, cuentaDestino, monto);
        return cuentaOrigen + " | " + cuentaDestino + " | " + monto;
    }   
}
