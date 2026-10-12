package cl.oursour.ms_recetas.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.GenerationType;

@Entity 
@Table(name = "receta")
public class Receta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long idReceta;

    private String titulo;
    private String tipo;
    private String pasosPreparacion;

    private long idProducto;

    public Receta() {
    }

    

}
