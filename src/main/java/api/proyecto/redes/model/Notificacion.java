package api.proyecto.redes.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "notificaciones")
public class Notificacion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idNotificacion;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(nullable = false)
    private String titulo;

    @Column(columnDefinition = "TEXT")
    private String mensaje;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TipoNotificacion tipo; // VIAJE_ACEPTADO, VIAJE_CANCELADO, CONDUCTOR_CERCANO, etc.

    @Column(nullable = false)
    private Boolean leida = false;

    @Column(name = "id_viaje")
    private Long idViaje; // Referencia al viaje relacionado (opcional)

    @Column(nullable = false)
    private LocalDateTime creadoEn = LocalDateTime.now();

    @Column(name = "leido_en")
    private LocalDateTime leidoEn;

    // Constructores
    public Notificacion() {}

    public Notificacion(Usuario usuario, String titulo, String mensaje, TipoNotificacion tipo) {
        this.usuario = usuario;
        this.titulo = titulo;
        this.mensaje = mensaje;
        this.tipo = tipo;
        this.leida = false;
        this.creadoEn = LocalDateTime.now();
    }

    public Notificacion(Usuario usuario, String titulo, String mensaje, TipoNotificacion tipo, Long idViaje) {
        this(usuario, titulo, mensaje, tipo);
        this.idViaje = idViaje;
    }

    // Getters y Setters
    public Long getIdNotificacion() { return idNotificacion; }
    public void setIdNotificacion(Long idNotificacion) { this.idNotificacion = idNotificacion; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getMensaje() { return mensaje; }
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }

    public TipoNotificacion getTipo() { return tipo; }
    public void setTipo(TipoNotificacion tipo) { this.tipo = tipo; }

    public Boolean getLeida() { return leida; }
    public void setLeida(Boolean leida) { this.leida = leida; }

    public Long getIdViaje() { return idViaje; }
    public void setIdViaje(Long idViaje) { this.idViaje = idViaje; }

    public LocalDateTime getCreadoEn() { return creadoEn; }
    public void setCreadoEn(LocalDateTime creadoEn) { this.creadoEn = creadoEn; }

    public LocalDateTime getLeidoEn() { return leidoEn; }
    public void setLeidoEn(LocalDateTime leidoEn) { this.leidoEn = leidoEn; }

    // Método para marcar como leída
    public void marcarComoLeida() {
        this.leida = true;
        this.leidoEn = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "Notificacion{" +
                "idNotificacion=" + idNotificacion +
                ", usuario=" + usuario.getIdUsuario() +
                ", titulo='" + titulo + '\'' +
                ", tipo=" + tipo +
                ", leida=" + leida +
                ", creadoEn=" + creadoEn +
                '}';
    }
}
