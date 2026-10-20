package cl.oursour.ms_recetas.model;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;


@Entity
@Table(name = "receta_ingrediente")
public class DetalleReceta {

    @EmbeddedId
    private DetalleRecetaId id = new DetalleRecetaId();

    private String dosificacion;

    @MapsId("idReceta")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_receta")
    private Receta receta;

    public DetalleReceta() {
    }

    public DetalleRecetaId getId() {
        return id;
    }

    public void setId(DetalleRecetaId id) {
        this.id = id;
    }

    public Long getIdIngrediente() {
        return id.getIdIngrediente();
    }

    public void setIdIngrediente(Long idIngrediente) {
        id.setIdIngrediente(idIngrediente);
    }

    public String getDosificacion() {
        return dosificacion;
    }

    public void setDosificacion(String dosificacion) {
        this.dosificacion = dosificacion;
    }

    public Receta getReceta() {
        return receta;
    }

    public void setReceta(Receta receta) {
        this.receta = receta;
    }
}
