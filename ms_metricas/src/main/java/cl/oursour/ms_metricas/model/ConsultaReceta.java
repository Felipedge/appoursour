package cl.oursour.ms_metricas.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

/** Tabla consulta_receta: cada detalle de receta abierto. id_usuario e id_receta sin FK (otros servicios). */
@Entity
@Table(name = "consulta_receta")
public class ConsultaReceta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_consulta")
    private Long idConsulta;

    @Column(name = "id_usuario", nullable = false)
    private Long idUsuario;

    @Column(name = "id_receta", nullable = false)
    private Long idReceta;

    @Column(name = "fecha_consulta", nullable = false)
    private LocalDateTime fechaConsulta;

    protected ConsultaReceta() {
    }

    public ConsultaReceta(Long idUsuario, Long idReceta) {
        this.idUsuario = idUsuario;
        this.idReceta = idReceta;
        this.fechaConsulta = LocalDateTime.now();
    }

    public Long getIdConsulta() {
        return idConsulta;
    }
}
