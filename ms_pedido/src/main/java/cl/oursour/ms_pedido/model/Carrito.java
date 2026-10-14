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
    @SuppressWarnings("unused")
    private long idUsuario;

    @SuppressWarnings("unused")
    private LocalDateTime fechaCreacion = LocalDateTime.now();


    @JsonManagedReference
    @OneToMany(mappedBy = "carrito", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CarritoProducto> productos = new ArrayList<>();


    public Carrito() {
    }

}
