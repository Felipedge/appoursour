package cl.oursour.ms_recetas.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class DetalleRecetaId implements Serializable {

    @Column(name = "id_receta")
    private Long idReceta;

    @Column(name = "id_ingrediente")
    private Long idIngrediente;

    public DetalleRecetaId() {
    }

    public DetalleRecetaId(Long idReceta, Long idIngrediente) {
        this.idReceta = idReceta;
        this.idIngrediente = idIngrediente;
    }

    public Long getIdReceta() {
        return idReceta;
    }

    public void setIdReceta(Long idReceta) {
        this.idReceta = idReceta;
    }

    public Long getIdIngrediente() {
        return idIngrediente;
    }

    public void setIdIngrediente(Long idIngrediente) {
        this.idIngrediente = idIngrediente;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DetalleRecetaId otro)) {
            return false;
        }
        return Objects.equals(idReceta, otro.idReceta) && Objects.equals(idIngrediente, otro.idIngrediente);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idReceta, idIngrediente);
    }
}
