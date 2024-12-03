package Model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Compra implements Serializable {
    /*
    Atributos:
    idCompra: el id de las compras.
    cliente: nombre del cliente. Exception del cliente, debe contener minimo 3 caracteres.
    productos: lista de productos.
    total: total de la compra.
    
    */  
    private static int idCompra_incremental = 1;
    private int idCompra;
    private String cliente;
    private List<Producto> productos = new ArrayList<>();
    private double total;

    public Compra(String cliente) {
        this.idCompra = idCompra_incremental++;
        if(cliente.trim().isEmpty() || cliente == null){
            throw new IllegalArgumentException("el nombre del cliente no puede ser nulo o estar vacio");
        }
        this.cliente = cliente;
        this.total = total;
    }
    
    /*
    Getters and Setters
    */        
    
    public int getIdCompra() {
        return idCompra;
    }

    public void setIdCompra(int idCompra) {
        this.idCompra = idCompra;
    }

    public String getCliente() {
        return cliente;
    }

    public void setCliente(String cliente) {
        if(cliente.trim().isEmpty() || cliente == null){
            throw new IllegalArgumentException("el nombre del cliente no puede ser nulo o estar vacio");
        }
        this.cliente = cliente;
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
        
    public void agregarProducto(Producto producto){
        if(producto != null){
            productos.add(producto);
        }
    }
    
    public void calcularTotal(){
    /*
        Calcula el total de todas las compras.
    */    
        productos.stream()
                 .mapToDouble(producto -> producto.getPrecioBase())
                 .sum();
    }
    
    public String mostrarDetalle(){
    /*
        Devuelve los detalles de la compra
    */            
        return "COMPRA:" +
                "ID del la compra: " + idCompra
                + ", cliente: " + cliente;
    }

    @Override
    public String toString() {
    /*
        toString el cual devuelve todos los atributos
    */            
        return "Compra{" + "idCompra=" + idCompra + ", cliente=" + cliente + '}';
    }
    
    
    
}
