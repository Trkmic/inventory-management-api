package Model;

public class Electronico extends Producto {
    /*
    Se pasa la garantia del producto.
    En el constructor y en el setter hay una exception de que el producto debe ser mayor a 0.
    */    
    
    private int garantia;

    public Electronico(String nombre, double precioBase, int garantia) {
        super(nombre, precioBase);
        if(garantia < 0){
            throw new IllegalArgumentException("La garantia no puede ser un numero negativo");
        }
        this.garantia = garantia;
    }
    
    /*
    Getter and Setter
    */    
    
    public int getGarantia() {
        return garantia;
    }

    public void setGarantia(int garantia) {
        if(garantia < 0){
            throw new IllegalArgumentException("La garantia no puede ser un numero negativo");
        }
        this.garantia = garantia;
    }
    
    
    
    @Override
    public double calcularPrecioFinal() {
    /*
        metodo el cual calcula el precio final,
        devuelve el precio base y en el caso de que la garantia sea mayor a 12 meses,
        se hace un incremento del 20%.
    */            
        double precioFinal = getPrecioBase();
        if(garantia > 12){
            precioFinal *= 1.20;
        }
        return precioFinal;
    }

    @Override
    public String toString() {
    /*
        toString de todos los atributos de la clase padre y su hija.
    */            
        return "Electronico{" + "id=" + getId() + ", nombre=" + getNombre() + ", precioBase=" + getPrecioBase() + "garantia=" + garantia + '}';
    }
    
    
}
