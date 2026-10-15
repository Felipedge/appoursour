package cl.oursour.ms_metricas.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "busqueda")
public class Busqueda {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_busqueda")
    private Long idBusqueda;

    @Column(name = "id_usuario", nullable = false)
    private Long idUsuario;

    @Column(name = "fecha_busqueda", nullable = false)
    private LocalDateTime fechaBusqueda;

    @OneToMany(mappedBy = "busqueda", cascade = CascadeType.ALL)
    private List<BusquedaIngrediente> ingredientes = new ArrayList<>();

    protected Busqueda() {
    }

    public Busqueda(Long idUsuario) {
        this.idUsuario = idUsuario;
        this.fechaBusqueda = LocalDateTime.now();
    }

    public void agregarIngrediente(Long idIngrediente) {
        ingredientes.add(new BusquedaIngrediente(this, idIngrediente));
    }

    public Long getIdBusqueda() {
        return idBusqueda;
    }
}
