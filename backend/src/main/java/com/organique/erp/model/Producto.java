package com.organique.erp.model;

import jakarta.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "productos")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "tipo_producto", discriminatorType = DiscriminatorType.STRING)
public abstract class Producto implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String nombre;

    @Column(name = "precio_base", nullable = false)
    private double precioBase;

    @Column(nullable = false)
    private int stock;

    // Constructor sin argumentos necesario para JPA/Hibernate
    public Producto() {}

    public Producto(String nombre, double precioBase, int stock) {
        setNombre(nombre);
        setPrecioBase(precioBase);
        setStock(stock);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        if (nombre == null || nombre.trim().length() < 3) {
            throw new IllegalArgumentException("El nombre debe tener mínimo 3 caracteres");
        }
        this.nombre = nombre;
    }

    public double getPrecioBase() {
        return precioBase;
    }

    public void setPrecioBase(double precioBase) {
        if (precioBase < 1) {
            throw new IllegalArgumentException("El precio base debe ser mayor a 0");
        }
        this.precioBase = precioBase;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        if (stock < 0) {
            throw new IllegalArgumentException("El stock no puede ser negativo");
        }
        this.stock = stock;
    }

    // Método abstracto para calcular precio final (específico por tipo de producto)
    public abstract double calcularPrecioFinal();

    @Override
    public String toString() {
        return "Producto{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", precioBase=" + precioBase +
                ", stock=" + stock +
                '}';
    }
}
