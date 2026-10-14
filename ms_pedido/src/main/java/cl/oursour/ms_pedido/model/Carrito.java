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
@Table(name = "carrito")
public class Carrito {

    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long idCarrito;

    //usuario dueño del carrito vive en ms_usuario
    private long idUsuario;

    private LocalDateTime fechaCreacion = LocalDateTime.now();


    @JsonManagedReference
    @OneToMany(mappedBy = "carrito", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CarritoProducto> productos = new ArrayList<>();


    public Carrito() {
    }

    public Long getIdCarrito() {
        return idCarrito;
    }

    public void setIdCarrito(Long idCarrito) {
        this.idCarrito = idCarrito;
    }

    public Long getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Long idUsuario) {
        this.idUsuario = idUsuario;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public List<CarritoProducto> getProductos() {
        return productos;
    }

    public void setProductos(List<CarritoProducto> productos) {
        this.productos.clear();
        if (productos != null) {
            productos.forEach(this::addProducto);
        }
    }

    public void addProducto(CarritoProducto p) {
        p.setCarrito(this);
        this.productos.add(p);
    }

    public void removeProducto(CarritoProducto p) {
        p.setCarrito(null);
        this.productos.remove(p);
    }
}
