package Persistencia;

import java.util.List;
import java.util.Optional;

public interface Repository<T> {
     
    /*
    Interface encargada de agregar entidades(ya sea para compras como para productos
    , encontrar un id 
    y por ultimo devolver la lista completa)
    
    */
    
    
     void add(T entity);
     Optional<T> s(int id);
     List<T> findAll();

}
