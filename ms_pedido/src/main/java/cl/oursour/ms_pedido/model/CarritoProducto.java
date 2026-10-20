package cl.oursour.ms_pedido.model;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;


@Entity
@Table(name = "carrito_producto")
public class CarritoProducto {

    @EmbeddedId
    private CarritoProductoId id = new CarritoProductoId();

    private int cantidad;

    @MapsId("idCarrito")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_carrito")
    private Carrito carrito;

    public CarritoProducto() {
    }

    public CarritoProductoId getId() {
        return id;
    }

    public void setId(CarritoProductoId id) {
        this.id = id;
    }

    public Long getIdProducto() {
        return id.getIdProducto();
    }

    public void setIdProducto(Long idProducto) {
        id.setIdProducto(idProducto);
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public Carrito getCarrito() {
        return carrito;
    }

    public void setCarrito(Carrito carrito) {
        this.carrito = carrito;
    }

}
