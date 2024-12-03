package Negocio;

import Model.Compra;
import Model.Electronico;
import Model.Producto;
import Persistencia.CompraRepository;
import Persistencia.ProductoRepository;
import Persistencia.Repository;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class GestorTienda {
    /*
    El gestor de tienda tiene dos parametros,
    los cuales son los dos repositorios de compras por un lado y por el otro de productos.
    */    
    
    
    private Repository<Producto> productoRepository;
    private Repository<Compra> compraRepository;

    public GestorTienda(String productoRepository, String compraRepository) {
        this.productoRepository = new ProductoRepository(productoRepository);
        this.compraRepository = new CompraRepository(compraRepository);
    }

    public Repository<Producto> getProductoRepository() {
        return productoRepository;
    }

    public void setProductoRepository(Repository<Producto> productoRepository) {
        this.productoRepository = productoRepository;
    }

    public Repository<Compra> getCompraRepository() {
        return compraRepository;
    }

    public void setCompraRepository(Repository<Compra> compraRepository) {
        this.compraRepository = compraRepository;
    }
    
    
    public List<Producto> listarProductos(){
        return productoRepository.findAll();
    }
    
    public List<Compra> listarcompras(){
        return compraRepository.findAll();
    }
    
    public void agregarProducto(Producto producto){
    /*
    Se agrega un nuevo producto al repositorio unicamente si no es nulo.
    */    
        if(producto != null){
            productoRepository.add(producto);
        }
    }
    
    public Optional<Producto> buscarProductoPorId(int id){
    /*
        Se busca un producto por su id utilizando flujos para poder manipularlo,
        Posteriormente filter para filtrar y se devuelve el primero que se encuentra.
    */            
        return productoRepository.findAll()
                                 .stream()
                                 .filter(producto -> producto.getId() == id)
                                 .findFirst();
    }
    
    public void realizarCompra(Compra compra){
    /*
        Se agrega al repositorio la compra pasada por paramentros unicamente si no es nula.
    */            
        if(compra != null){
            compraRepository.add(compra);
        }
    }
    
    public double calcularIngresos(){
    /*
        Se calcula el total de todos los ingresos de compras.
    */
        
        return compraRepository.findAll()
                               .stream().flatMap(c -> c.getProductos().stream())
                               .mapToDouble(Producto::getPrecioBase)
                               .sum();
    }
    
    public List<Producto> filtrarProductos(Predicate<Producto> criterio){
    /*
        Se filtran los productos segun el criterio pasado por parametro.
    */    
        return productoRepository.findAll()
                                 .stream()
                                 .filter(criterio)
                                 .collect(Collectors.toList());
    }
    
    public void aplicarDescuento(Function<Producto, Double> descuento){
    /*
        Se aplica un descuento pasado por parametros.
    */    
        for (Producto producto : productoRepository.findAll()) {
            if (producto instanceof Electronico && ((Electronico) producto).getGarantia() > 6) {
                double nuevoPrecio = descuento.apply(producto);
                producto.setPrecioBase(nuevoPrecio);
            }
        }
    }
    
    
}
