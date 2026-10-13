package cl.oursour.ms_pedido.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity 
@Table(name = "direccion_despacho")
public class DireccionDespacho {
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long idUsuario;

    private String calle;
    private String numero;
    private String comuna;
    private String ciudad;
    private String referencia;

    public DireccionDespacho() {
    }
    

    
}
