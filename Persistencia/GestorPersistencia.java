package Persistencia;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class GestorPersistencia<T extends Serializable> {
    
    /*
    Atributo encargado de pasarle el nombre del archivo con el cual se creara psoteriormente.
    */
    private String archivo;

    public GestorPersistencia(String archivo) {
    /*
    Constructor encargado de pasarle por parametros el archivo al gestor de persistencia
    */        
        
        this.archivo = archivo;
    }
    
    /*
    Getters and Setters
    */
    
    public String getArchivo() {
        return archivo;
    }

    public void setArchivo(String archivo) {
        this.archivo = archivo;
    }
    
    public void guardar(List<T> lista){
    /*
    Metodo encargado de guardar la lista pasada por paramentros en un archivo
    */        
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(archivo))) {
            oos.writeObject(lista);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Error al guardar los datos en el archivo.");
        }
    }
    
    public List<T> cargar(){
    /*
    Metodo encargado de cargar.
    */            
        File file = new File(archivo);
        if (!file.exists()) {
            return new ArrayList<>();
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(archivo))) {
            return (List<T>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException("Error al cargar los datos del archivo.");
        }
    }
    
    
}
