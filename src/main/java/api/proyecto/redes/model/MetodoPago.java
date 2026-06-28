package api.proyecto.redes.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "metodos_pago")
public class MetodoPago {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idMetodoPago;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TipoMetodoPago tipo; // TARJETA_CREDITO, TARJETA_DEBITO, WALLET

    // Datos encriptados (números de tarjeta últimos 4 dígitos)
    @Column(nullable = false)
    private String ultimosCuatroDigitos;

    @Column(nullable = false)
    private String nombreTitular;

    // Token de Stripe para procesar pagos sin guardar datos sensibles
    @Column(name = "stripe_payment_method_id")
    private String stripePaymentMethodId;

    @Column(nullable = false)
    private Boolean predeterminado = false;

    @Column(nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime creadoEn = LocalDateTime.now();

    @Column(columnDefinition = "TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    private LocalDateTime actualizadoEn;

    // Constructores
    public MetodoPago() {}

    public MetodoPago(Usuario usuario, TipoMetodoPago tipo, String ultimosCuatroDigitos, 
                     String nombreTitular, String stripePaymentMethodId) {
        this.usuario = usuario;
        this.tipo = tipo;
        this.ultimosCuatroDigitos = ultimosCuatroDigitos;
        this.nombreTitular = nombreTitular;
        this.stripePaymentMethodId = stripePaymentMethodId;
        this.predeterminado = false;
        this.creadoEn = LocalDateTime.now();
    }

    // Getters y Setters
    public Long getIdMetodoPago() { return idMetodoPago; }
    public void setIdMetodoPago(Long idMetodoPago) { this.idMetodoPago = idMetodoPago; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    public TipoMetodoPago getTipo() { return tipo; }
    public void setTipo(TipoMetodoPago tipo) { this.tipo = tipo; }

    public String getUltimosCuatroDigitos() { return ultimosCuatroDigitos; }
    public void setUltimosCuatroDigitos(String ultimosCuatroDigitos) { this.ultimosCuatroDigitos = ultimosCuatroDigitos; }

    public String getNombreTitular() { return nombreTitular; }
    public void setNombreTitular(String nombreTitular) { this.nombreTitular = nombreTitular; }

    public String getStripePaymentMethodId() { return stripePaymentMethodId; }
    public void setStripePaymentMethodId(String stripePaymentMethodId) { this.stripePaymentMethodId = stripePaymentMethodId; }

    public Boolean getPredeterminado() { return predeterminado; }
    public void setPredeterminado(Boolean predeterminado) { this.predeterminado = predeterminado; }

    public LocalDateTime getCreadoEn() { return creadoEn; }
    public void setCreadoEn(LocalDateTime creadoEn) { this.creadoEn = creadoEn; }

    public LocalDateTime getActualizadoEn() { return actualizadoEn; }
    public void setActualizadoEn(LocalDateTime actualizadoEn) { this.actualizadoEn = actualizadoEn; }

    @Override
    public String toString() {
        return "MetodoPago{" +
                "idMetodoPago=" + idMetodoPago +
                ", usuario=" + usuario.getIdUsuario() +
                ", tipo=" + tipo +
                ", ultimosDigitos=****" + ultimosCuatroDigitos +
                ", predeterminado=" + predeterminado +
                '}';
    }
}
