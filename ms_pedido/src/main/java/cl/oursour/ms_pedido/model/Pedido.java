package cl.oursour.ms_pedido.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "pedido")
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idPedido;

    // Usuario que compra: vive en MS-Usuario, aquí solo se guarda el id
    private Long idUsuario;

    // Dirección de despacho (tabla direccion_despacho de este mismo microservicio)
    private Long idDireccion;

    private int subtotal;
    private int costoDespacho;
    private int montoTotal;

    // PENDIENTE_PAGO, SOLICITADO, EN_PREPARACION o PAGO_RECHAZADO
    private String estado = "PENDIENTE_PAGO";

    private LocalDateTime fechaCreacion = LocalDateTime.now();

    @JsonManagedReference
    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PedidoProducto> productos = new ArrayList<>();

    public Pedido() {
    }

}
