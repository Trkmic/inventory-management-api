package Persistencia;

import Model.Compra;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CompraRepository implements Repository<Compra> {
    /*
    Atributos:
    Lista de compras donde estan almacenadas todas la compras.
    Gestor de persistencia el cual gestionara las compras, creara y guardara el archivo. 
    */    
    private List<Compra> compras = new ArrayList<>();
    private GestorPersistencia<Compra> gestorPersistencia;

    public CompraRepository(String archivo) {
        this.gestorPersistencia = new GestorPersistencia<>(archivo);
    }
    
    /*
    Getters and setters de los atributos
    */
    
    public List<Compra> getCompras() {
        return compras;
    }

    public void setCompras(List<Compra> compras) {
        this.compras = compras;
    }

    public GestorPersistencia<Compra> getGestorPersistencia() {
        return gestorPersistencia;
    }

    public void setGestorPersistencia(GestorPersistencia<Compra> gestorPersistencia) {
        this.gestorPersistencia = gestorPersistencia;
    }
    
    
    
    @Override
    public void add(Compra compra) {
    /*
    Agregar un nueva compra solo si la compra pasada por parametros no es nulo.
    Se agrega y se vuelve a guardar la lista en el gestor con el ultima compra agregada.
    */    
        if(compra != null){
            compras.add(compra);
            gestorPersistencia.guardar(compras);
        }
    }

    @Override
    public Optional<Compra> s(int id) {
    /*
    Se encuentra una coincidencia entre los id de la lista y el id pasado por parametros.
    Se utiliza pasar la lista a un flujo para poder manipularlo y devolver la primer coincidencia
    */    
        
        return compras.stream()
                        .filter(compra -> compra.getIdCompra() == id)
                        .findFirst();
    }

    @Override
    public List<Compra> findAll() {
    /*
    Simplemente retorna la lista entera, se crea un nuevo ArrayList para que la lista original no se manipulada.
    */        
        return new ArrayList<>(compras);
    }
    
    
    
    
    
}
