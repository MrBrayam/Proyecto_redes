package api.proyecto.redes.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Registra cancelaciones de viajes
 */
@Entity
@Table(name = "cancelaciones")
public class Cancelacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cancelacion")
    private Long idCancelacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "viaje_id", nullable = false)
    private Viaje viaje;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario canceladoPor;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TipoCancelacion tipoCancelacion;

    @Column(length = 255)
    private String motivo;

    @Column(precision = 10, scale = 2)
    private BigDecimal monto;

    @Column(name = "creado_en", insertable = false, updatable = false)
    private LocalDateTime creadoEn;

    public Cancelacion() {
    }

    public Cancelacion(Viaje viaje, Usuario canceladoPor, TipoCancelacion tipoCancelacion, String motivo, BigDecimal monto) {
        this.viaje = viaje;
        this.canceladoPor = canceladoPor;
        this.tipoCancelacion = tipoCancelacion;
        this.motivo = motivo;
        this.monto = monto;
    }

    // Getters y Setters
    public Long getIdCancelacion() {
        return idCancelacion;
    }

    public void setIdCancelacion(Long idCancelacion) {
        this.idCancelacion = idCancelacion;
    }

    public Viaje getViaje() {
        return viaje;
    }

    public void setViaje(Viaje viaje) {
        this.viaje = viaje;
    }

    public Usuario getCanceladoPor() {
        return canceladoPor;
    }

    public void setCanceladoPor(Usuario canceladoPor) {
        this.canceladoPor = canceladoPor;
    }

    public TipoCancelacion getTipoCancelacion() {
        return tipoCancelacion;
    }

    public void setTipoCancelacion(TipoCancelacion tipoCancelacion) {
        this.tipoCancelacion = tipoCancelacion;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public BigDecimal getMonto() {
        return monto;
    }

    public void setMonto(BigDecimal monto) {
        this.monto = monto;
    }

    public LocalDateTime getCreadoEn() {
        return creadoEn;
    }

    public void setCreadoEn(LocalDateTime creadoEn) {
        this.creadoEn = creadoEn;
    }

    public enum TipoCancelacion {
        PASAJERO, CONDUCTOR, SISTEMA
    }
}
