package cl.oursour.ms_metricas.model;

import jakarta.persistence.*;

import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "busqueda_ingrediente")
public class BusquedaIngrediente {

    @Embeddable
    public static class Id implements Serializable {

        @Column(name = "id_busqueda")
        private Long idBusqueda;

        @Column(name = "id_ingrediente")
        private Long idIngrediente;

        protected Id() {
        }

        public Id(Long idBusqueda, Long idIngrediente) {
            this.idBusqueda = idBusqueda;
            this.idIngrediente = idIngrediente;
        }

        public Long getIdIngrediente() {
            return idIngrediente;
        }

        @Override
        public boolean equals(Object o) {
            return o instanceof Id otro
                    && Objects.equals(idBusqueda, otro.idBusqueda)
                    && Objects.equals(idIngrediente, otro.idIngrediente);
        }

        @Override
        public int hashCode() {
            return Objects.hash(idBusqueda, idIngrediente);
        }
    }

    @EmbeddedId
    private Id id;

    @MapsId("idBusqueda")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_busqueda")
    private Busqueda busqueda;

    protected BusquedaIngrediente() {
    }

    BusquedaIngrediente(Busqueda busqueda, Long idIngrediente) {
        this.id = new Id(busqueda.getIdBusqueda(), idIngrediente);
        this.busqueda = busqueda;
    }

    public Id getId() {
        return id;
    }
}
