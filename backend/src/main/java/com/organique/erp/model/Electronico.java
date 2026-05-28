package com.organique.erp.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("ELECTRONICO")
public class Electronico extends Producto {

    private int garantia; // En meses

    public Electronico() {
        super();
    }

    public Electronico(String nombre, double precioBase, int stock, int garantia) {
        super(nombre, precioBase, stock);
        setGarantia(garantia);
    }

    public int getGarantia() {
        return garantia;
    }

    public void setGarantia(int garantia) {
        if (garantia < 0) {
            throw new IllegalArgumentException("La garantía no puede ser un número negativo");
        }
        this.garantia = garantia;
    }

    @Override
    public double calcularPrecioFinal() {
        double precioFinal = getPrecioBase();
        if (garantia > 12) {
            precioFinal *= 1.20; // 20% de recargo por garantía extendida
        }
        return precioFinal;
    }

    @Override
    public String toString() {
        return "Electronico{" +
                "id=" + getId() +
                ", nombre='" + getNombre() + '\'' +
                ", precioBase=" + getPrecioBase() +
                ", stock=" + getStock() +
                ", garantia=" + garantia +
                '}';
    }
}
