package com.organique.erp.model;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "compras")
public class Compra implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_compra")
    private Integer idCompra;

    @Column(nullable = false)
    private String cliente;

    @Column(nullable = false)
    private LocalDateTime fecha;

    @Column(nullable = false)
    private double total;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "compra_productos", joinColumns = @JoinColumn(name = "id_compra"), inverseJoinColumns = @JoinColumn(name = "id_producto"))
    private List<Producto> productos = new ArrayList<>();

    public Compra() {
        this.fecha = LocalDateTime.now();
    }

    public Compra(String cliente) {
        this();
        setCliente(cliente);
    }

    public Integer getIdCompra() {
        return idCompra;
    }

    public void setIdCompra(Integer idCompra) {
        this.idCompra = idCompra;
    }

    public String getCliente() {
        return cliente;
    }

    public void setCliente(String cliente) {
        if (cliente == null || cliente.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del cliente no puede ser nulo o estar vacío");
        }
        this.cliente = cliente;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public List<Producto> getProductos() {
        return productos;
    }

    public void setProductos(List<Producto> productos) {
        this.productos = productos;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public void agregarProducto(Producto producto) {
        if (producto != null) {
            this.productos.add(producto);
        }
    }

    // Calcula el total sumando el precio final de cada producto
    public void calcularTotal() {
        this.total = this.productos.stream()
                .mapToDouble(Producto::calcularPrecioFinal)
                .sum();
    }

    @Override
    public String toString() {
        return "Compra{" +
                "idCompra=" + idCompra +
                ", cliente='" + cliente + '\'' +
                ", fecha=" + fecha +
                ", total=" + total +
                '}';
    }
}
