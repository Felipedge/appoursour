package cl.oursour.ms_catalogo.model;

import jakarta.persistence.*;

import java.math.BigDecimal;

/** Tabla producto. activo = false es el borrado lógico. */
@Entity
@Table(name = "producto")
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_producto")
    private Long idProducto;

    @Column(name = "nombre_comercial", nullable = false, length = 150)
    private String nombreComercial;

    @Column(name = "descripcion", length = 500)
    private String descripcion;

    @Column(name = "precio_unitario", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioUnitario;

    @Column(name = "stock_simulado", nullable = false)
    private Integer stockSimulado;

    @Column(name = "activo", nullable = false)
    private Boolean activo;

    protected Producto() {
    }

    public Producto(String nombreComercial, String descripcion, BigDecimal precioUnitario, Integer stockSimulado) {
        this.nombreComercial = nombreComercial;
        this.descripcion = descripcion;
        this.precioUnitario = precioUnitario;
        this.stockSimulado = stockSimulado;
        this.activo = true;
    }
    public void descontarStock(int cantidad) {
        this.stockSimulado -= cantidad;
    }

    public Long getIdProducto() {
        return idProducto;
    }

    public String getNombreComercial() {
        return nombreComercial;
    }

    public void setNombreComercial(String nombreComercial) {
        this.nombreComercial = nombreComercial;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public BigDecimal getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(BigDecimal precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

    public Integer getStockSimulado() {
        return stockSimulado;
    }

    public void setStockSimulado(Integer stockSimulado) {
        this.stockSimulado = stockSimulado;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }
}
