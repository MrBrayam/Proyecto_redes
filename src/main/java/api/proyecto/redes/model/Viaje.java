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

@Entity
@Table(name = "viajes")
public class Viaje {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_viaje")
    private Long idViaje;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pasajero_id", nullable = false)
    private Usuario pasajero;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conductor_id")
    private Conductor conductor;

    @Column(name = "origen_lat", nullable = false, precision = 10, scale = 7)
    private BigDecimal origenLat;

    @Column(name = "origen_lng", nullable = false, precision = 10, scale = 7)
    private BigDecimal origenLng;

    @Column(name = "destino_lat", nullable = false, precision = 10, scale = 7)
    private BigDecimal destinoLat;

    @Column(name = "destino_lng", nullable = false, precision = 10, scale = 7)
    private BigDecimal destinoLng;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private EstadoViaje estado;

    @Column(precision = 10, scale = 2)
    private BigDecimal precio;

    @Column(name = "creado_en", insertable = false, updatable = false)
    private LocalDateTime creadoEn;

    @Column(name = "actualizado_en", insertable = false, updatable = false)
    private LocalDateTime actualizadoEn;

    public Long getIdViaje() {
        return idViaje;
    }

    public void setIdViaje(Long idViaje) {
        this.idViaje = idViaje;
    }

    public Usuario getPasajero() {
        return pasajero;
    }

    public void setPasajero(Usuario pasajero) {
        this.pasajero = pasajero;
    }

    public Conductor getConductor() {
        return conductor;
    }

    public void setConductor(Conductor conductor) {
        this.conductor = conductor;
    }

    public BigDecimal getOrigenLat() {
        return origenLat;
    }

    public void setOrigenLat(BigDecimal origenLat) {
        this.origenLat = origenLat;
    }

    public BigDecimal getOrigenLng() {
        return origenLng;
    }

    public void setOrigenLng(BigDecimal origenLng) {
        this.origenLng = origenLng;
    }

    public BigDecimal getDestinoLat() {
        return destinoLat;
    }

    public void setDestinoLat(BigDecimal destinoLat) {
        this.destinoLat = destinoLat;
    }

    public BigDecimal getDestinoLng() {
        return destinoLng;
    }

    public void setDestinoLng(BigDecimal destinoLng) {
        this.destinoLng = destinoLng;
    }

    public EstadoViaje getEstado() {
        return estado;
    }

    public void setEstado(EstadoViaje estado) {
        this.estado = estado;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }

    public LocalDateTime getCreadoEn() {
        return creadoEn;
    }

    public void setCreadoEn(LocalDateTime creadoEn) {
        this.creadoEn = creadoEn;
    }

    public LocalDateTime getActualizadoEn() {
        return actualizadoEn;
    }

    public void setActualizadoEn(LocalDateTime actualizadoEn) {
        this.actualizadoEn = actualizadoEn;
    }
}
