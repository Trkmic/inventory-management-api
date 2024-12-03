package Persistencia;

import Model.Producto;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

public class ProductoRepository implements Repository<Producto>{
    /*
    Repositorio encargado de los productos, 
    el cual tiene como atributos una lista de productos,
    un gestor de persistencia con su archivo pasado por parametro
    y el ultimoid.
    */
    
    
    private List<Producto> productos = new ArrayList<>();
    private GestorPersistencia<Producto> gestorPersistencia;
    private int ultimoId;

    public ProductoRepository(String archivo) {
        this.gestorPersistencia = new GestorPersistencia<>(archivo);
        this.ultimoId = ultimoId;
    }
    
    
    
    /*
    Getters and setters de los atributos
    */
    
    public List<Producto> getProductos() {
        return productos;
    }

    public void setProductos(List<Producto> productos) {
        this.productos = productos;
    }

    public GestorPersistencia<Producto> getGestorPersistencia() {
        return gestorPersistencia;
    }

    public void setGestorPersistencia(GestorPersistencia<Producto> gestorPersistencia) {
        this.gestorPersistencia = gestorPersistencia;
    }

    public int getUltimoId() {
        return ultimoId;
    }

    public void setUltimoId(int ultimoId) {
        this.ultimoId = ultimoId;
    }
    
    @Override
    public void add(Producto producto) {
    /*
    Agregar un nuevo producto solo si el producto pasado por parametros no es nulo.
    Se agrega, adquiere el ultimo id +1 y se vuelve a guardar la lista en el gestor con el ultimo producto agregado.
    */        
        if(producto != null){
            productos.add(producto);
            producto.setId(++ultimoId);
            gestorPersistencia.guardar(productos);
        }
    }

    @Override
    public Optional<Producto> s(int id) {
    /*
    Se encuentra una coincidencia entre los id de la lista y el id pasado por parametros.
    Se utiliza pasar la lista a un flujo para poder manipularlo y devolver la primer coincidencia
    */        
        return productos.stream()
                        .filter(producto -> producto.getId() == id)
                        .findFirst();
        
    }

    @Override
    public List<Producto> findAll() {
    /*
    Simplemente retorna la lista entera, se crea un nuevo ArrayList para que la lista original no se manipulada.
    */        
        return new ArrayList<>(productos);
    }

}
