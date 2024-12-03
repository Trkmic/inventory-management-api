package Model;

public class Ropa extends Producto {
    
    /*
    Atributo talla el cual se pasa la talla de la ropa. Exception, debe ser igual a S, M, L O XL.
    */        
    private String talla;
    private static int id_incremental = 1;
    private int id;

    public Ropa(String nombre, double precioBase,String talla) {
        super(nombre, precioBase);
        if(!talla.equalsIgnoreCase("S") && !talla.equalsIgnoreCase("M") && !talla.equalsIgnoreCase("L") && !talla.equalsIgnoreCase("XL") ){
            throw new IllegalArgumentException("Los talles disponibles son L, XL, M,S");
        }
        this.talla = talla;
        this.id = id_incremental++;
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
    
    public String getTalla() {
        return talla;
    }

    public void setTalla(String talla) {
        if(!talla.equalsIgnoreCase("S") || !talla.equalsIgnoreCase("M") || !talla.equalsIgnoreCase("L") || !talla.equalsIgnoreCase("XL")){
            throw new IllegalArgumentException("Los talles disponibles son L, XL, M,S");
        }
        this.talla = talla;
    }

    @Override
    public double calcularPrecioFinal() {
    /*
        Calcula el precio final realizando un incremento del 10% en el caso de que el talle sea XL.
    */            
        double precioFinal = getPrecioBase();
        if(talla.equalsIgnoreCase("XL")){
            precioFinal *=  1.10;
        }
        return precioFinal;
    }

    @Override
    public String toString() {
    /*
        ToString de Ropa.
    */            
        return "Ropa{" + "id=" + getId() + ", nombre=" + getNombre() + ", precioBase=" + getPrecioBase() + "garantia=" + "talla=" + talla + '}';
    }
    
}
