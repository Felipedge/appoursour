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

    public Long getIdPedido() {
        return idPedido;
    }

    public void setIdPedido(Long idPedido) {
        this.idPedido = idPedido;
    }

    public Long getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Long idUsuario) {
        this.idUsuario = idUsuario;
    }

    public Long getIdDireccion() {
        return idDireccion;
    }

    public void setIdDireccion(Long idDireccion) {
        this.idDireccion = idDireccion;
    }

    public int getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(int subtotal) {
        this.subtotal = subtotal;
    }

    public int getCostoDespacho() {
        return costoDespacho;
    }

    public void setCostoDespacho(int costoDespacho) {
        this.costoDespacho = costoDespacho;
    }

    public int getMontoTotal() {
        return montoTotal;
    }

    public void setMontoTotal(int montoTotal) {
        this.montoTotal = montoTotal;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public List<PedidoProducto> getProductos() {
        return productos;
    }

    public void setProductos(List<PedidoProducto> productos) {
        this.productos.clear();
        if (productos != null) {
            productos.forEach(this::addProducto);
        }
    }

    public void addProducto(PedidoProducto p) {
        p.setPedido(this);
        this.productos.add(p);
    }

    public void removeProducto(PedidoProducto p) {
        p.setPedido(null);
        this.productos.remove(p);
    }

}
