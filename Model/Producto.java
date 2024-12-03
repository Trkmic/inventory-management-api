package Model;

import java.io.Serializable;

public abstract class Producto implements Serializable {
    /*
    Atributos:
    id_incremental: es el que incrementa el valor automaticamente en el constructor.
    id: el id del producto.
    nombre: nombre del producto. Exception, debe ser mayor a 3 caracteres.
    precioBase: precio base del producto. Exception, debe ser mayor a 0.
    */    
    
    private static int id_incremental = 1;
    private int id;
    private String nombre;
    private double precioBase;

    public Producto(String nombre, double precioBase) {
        this.id = id_incremental++;
        if(nombre.trim().isEmpty() || nombre == null || nombre.length() < 3){
            throw new IllegalArgumentException("el nombre debe tener minimo 3 caracteres");
        }
        this.nombre = nombre;
        if(precioBase < 1){
            throw new IllegalArgumentException("El precio base debe ser mayor a 0");
        }
        this.precioBase = precioBase;
    }
    
    /*
    Getters and Setters
    */        
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        if(nombre.trim().isEmpty() || nombre == null || nombre.length() < 3){
            throw new IllegalArgumentException("el nombre debe tener minimo 3 caracteres");
        }
        this.nombre = nombre;
    }

    public double getPrecioBase() {
        return precioBase;
    }

    public void setPrecioBase(double precioBase) {
        if(precioBase < 1){
            throw new IllegalArgumentException("El precio base debe ser mayor a 0");
        }
        this.precioBase = precioBase;
    }
    
    /*
    Metodo abstracto que calcula el precio final en ropa y electronico.
    */    
    public abstract double calcularPrecioFinal();
    
    public String mostrarResumen(){
        /*
        Devuelve un resumen del producto.
        */    
        return "PRODUCTO: " +
                " ID: " + id +
                ", Nombre: " + nombre +
                " y precio final: " + precioBase;       
    
    }
    
    @Override
    public String toString() {
        /*
        toString de todos los atributos.
        */    
        return "Producto{" + "id=" + id + ", nombre=" + nombre + ", precioBase=" + precioBase + '}';
    }
    
    
    
    
}
