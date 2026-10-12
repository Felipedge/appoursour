package cl.oursour.ms_recetas.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity 
@Table(name = "detalle_receta")
public class DetalleReceta {

    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long idIngrediente;

    private int cantidad;

    

}
