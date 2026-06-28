package api.proyecto.redes.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transacciones")
public class Transaccion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idTransaccion;

    @ManyToOne
    @JoinColumn(name = "viaje_id", nullable = false)
    private Viaje viaje;

    @ManyToOne
    @JoinColumn(name = "usuario_pagador_id", nullable = false)
    private Usuario usuarioPagador; // Generalmente el pasajero

    @ManyToOne
    @JoinColumn(name = "metodo_pago_id", nullable = false)
    private MetodoPago metodoPago;

    @Column(nullable = false)
    private BigDecimal monto; // Total a pagar

    @Column(nullable = false)
    private BigDecimal comision; // Comisión de la plataforma (10%)

    @Column(nullable = false)
    private BigDecimal gananciaConductor; // Lo que recibe el conductor (90%)

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private EstadoTransaccion estado; // PENDIENTE, PROCESANDO, COMPLETADA, FALLIDA, REEMBOLSADA

    @Column(name = "stripe_payment_intent_id")
    private String stripePaymentIntentId; // Para tracking de Stripe

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Column(columnDefinition = "TEXT")
    private String razonFallo; // Si la transacción falla

    @Column(nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime creadoEn = LocalDateTime.now();

    @Column(name = "procesado_en")
    private LocalDateTime procesadoEn;

    @Column(name = "completado_en")
    private LocalDateTime completadoEn;

    // Constructores
    public Transaccion() {}

    public Transaccion(Viaje viaje, Usuario usuarioPagador, MetodoPago metodoPago,
                      BigDecimal monto, BigDecimal comision, BigDecimal gananciaConductor) {
        this.viaje = viaje;
        this.usuarioPagador = usuarioPagador;
        this.metodoPago = metodoPago;
        this.monto = monto;
        this.comision = comision;
        this.gananciaConductor = gananciaConductor;
        this.estado = EstadoTransaccion.PENDIENTE;
        this.creadoEn = LocalDateTime.now();
    }

    // Getters y Setters
    public Long getIdTransaccion() { return idTransaccion; }
    public void setIdTransaccion(Long idTransaccion) { this.idTransaccion = idTransaccion; }

    public Viaje getViaje() { return viaje; }
    public void setViaje(Viaje viaje) { this.viaje = viaje; }

    public Usuario getUsuarioPagador() { return usuarioPagador; }
    public void setUsuarioPagador(Usuario usuarioPagador) { this.usuarioPagador = usuarioPagador; }

    public MetodoPago getMetodoPago() { return metodoPago; }
    public void setMetodoPago(MetodoPago metodoPago) { this.metodoPago = metodoPago; }

    public BigDecimal getMonto() { return monto; }
    public void setMonto(BigDecimal monto) { this.monto = monto; }

    public BigDecimal getComision() { return comision; }
    public void setComision(BigDecimal comision) { this.comision = comision; }

    public BigDecimal getGananciaConductor() { return gananciaConductor; }
    public void setGananciaConductor(BigDecimal gananciaConductor) { this.gananciaConductor = gananciaConductor; }

    public EstadoTransaccion getEstado() { return estado; }
    public void setEstado(EstadoTransaccion estado) { this.estado = estado; }

    public String getStripePaymentIntentId() { return stripePaymentIntentId; }
    public void setStripePaymentIntentId(String stripePaymentIntentId) { this.stripePaymentIntentId = stripePaymentIntentId; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getRazonFallo() { return razonFallo; }
    public void setRazonFallo(String razonFallo) { this.razonFallo = razonFallo; }

    public LocalDateTime getCreadoEn() { return creadoEn; }
    public void setCreadoEn(LocalDateTime creadoEn) { this.creadoEn = creadoEn; }

    public LocalDateTime getProcesadoEn() { return procesadoEn; }
    public void setProcesadoEn(LocalDateTime procesadoEn) { this.procesadoEn = procesadoEn; }

    public LocalDateTime getCompletadoEn() { return completadoEn; }
    public void setCompletadoEn(LocalDateTime completadoEn) { this.completadoEn = completadoEn; }

    @Override
    public String toString() {
        return "Transaccion{" +
                "idTransaccion=" + idTransaccion +
                ", viaje=" + viaje.getIdViaje() +
                ", monto=" + monto +
                ", estado=" + estado +
                ", creadoEn=" + creadoEn +
                '}';
    }
}
