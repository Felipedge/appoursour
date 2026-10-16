package cl.oursour.ms_pago.model;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "pago")
public class Pago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idPago;

    
    private Long idPedido;

    private int monto;
    private String metodo; 

    
    private String estado = "APROBADO";

    private LocalDateTime fechaPago = LocalDateTime.now();

    public Pago() {
    }
}