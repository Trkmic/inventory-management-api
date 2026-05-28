package com.organique.erp.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("ROPA")
public class Ropa extends Producto {

    private String talla; // S, M, L, XL

    public Ropa() {
        super();
    }

    public Ropa(String nombre, double precioBase, int stock, String talla) {
        super(nombre, precioBase, stock);
        setTalla(talla);
    }

    public String getTalla() {
        return talla;
    }

    public void setTalla(String talla) {
        if (talla == null) {
            throw new IllegalArgumentException("La talla no puede ser nula");
        }
        String cleanTalla = talla.trim().toUpperCase();
        if (!cleanTalla.equals("S") && !cleanTalla.equals("M") && !cleanTalla.equals("L") && !cleanTalla.equals("XL")) {
            throw new IllegalArgumentException("Las tallas disponibles son S, M, L, XL");
        }
        this.talla = cleanTalla;
    }

    @Override
    public double calcularPrecioFinal() {
        double precioFinal = getPrecioBase();
        if ("XL".equals(talla)) {
            precioFinal *= 1.10; // 10% de recargo por talle extra grande
        }
        return precioFinal;
    }

    @Override
    public String toString() {
        return "Ropa{" +
                "id=" + getId() +
                ", nombre='" + getNombre() + '\'' +
                ", precioBase=" + getPrecioBase() +
                ", stock=" + getStock() +
                ", talla='" + talla + '\'' +
                '}';
    }
}
