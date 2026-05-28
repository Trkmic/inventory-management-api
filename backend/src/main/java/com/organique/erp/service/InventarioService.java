package com.organique.erp.service;

import com.organique.erp.model.Compra;
import com.organique.erp.model.Electronico;
import com.organique.erp.model.Producto;
import com.organique.erp.model.Ropa;
import com.organique.erp.repository.CompraRepository;
import com.organique.erp.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class InventarioService implements CommandLineRunner {

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private CompraRepository compraRepository;

    // Obtener todos los productos
    public List<Producto> obtenerTodosLosProductos() {
        return productoRepository.findAll();
    }

    // Obtener un producto por ID
    public Producto obtenerProductoPorId(Integer id) {
        return productoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado con ID: " + id));
    }

    // Registrar un nuevo producto electrónico
    public Producto guardarElectronico(String nombre, double precioBase, int stock, int garantia) {
        Electronico electronico = new Electronico(nombre, precioBase, stock, garantia);
        return productoRepository.save(electronico);
    }

    // Registrar un nuevo producto de ropa
    public Producto guardarRopa(String nombre, double precioBase, int stock, String talla) {
        Ropa ropa = new Ropa(nombre, precioBase, stock, talla);
        return productoRepository.save(ropa);
    }

    // Actualizar un producto existente
    public Producto actualizarProducto(Integer id, String nombre, double precioBase, int stock, Integer garantia,
            String talla) {
        Producto producto = obtenerProductoPorId(id);
        producto.setNombre(nombre);
        producto.setPrecioBase(precioBase);
        producto.setStock(stock);

        if (producto instanceof Electronico && garantia != null) {
            ((Electronico) producto).setGarantia(garantia);
        } else if (producto instanceof Ropa && talla != null) {
            ((Ropa) producto).setTalla(talla);
        }

        return productoRepository.save(producto);
    }

    // Eliminar un producto
    public void eliminarProducto(Integer id) {
        if (!productoRepository.existsById(id)) {
            throw new IllegalArgumentException("Producto no encontrado con ID: " + id);
        }
        productoRepository.deleteById(id);
    }

    // Obtener todas las compras
    public List<Compra> obtenerTodasLasCompras() {
        return compraRepository.findAll();
    }

    // Registrar una transacción de compra
    @Transactional
    public Compra registrarCompra(String cliente, List<Integer> productoIds) {
        if (productoIds == null || productoIds.isEmpty()) {
            throw new IllegalArgumentException("La compra debe contener al menos un producto");
        }

        Compra compra = new Compra(cliente);
        List<Producto> productosParaCompra = new ArrayList<>();

        for (Integer id : productoIds) {
            Producto prod = obtenerProductoPorId(id);
            if (prod.getStock() < 1) {
                throw new IllegalStateException("Stock insuficiente para el producto: " + prod.getNombre());
            }
            // Reducir stock en 1
            prod.setStock(prod.getStock() - 1);
            productoRepository.save(prod);

            compra.agregarProducto(prod);
        }

        compra.calcularTotal();
        return compraRepository.save(compra);
    }

    // Sembrado inicial de catálogo si la base de datos está vacía
    @Override
    public void run(String... args) throws Exception {
        if (productoRepository.count() == 0) {
            System.out.println("Sembrando catálogo inicial de productos...");

            // 4 Electrónicos
            productoRepository.save(new Electronico("Smartphone Galaxy S24 Ultra", 120000.0, 15, 12));
            productoRepository.save(new Electronico("Notebook ASUS ROG Strix", 250000.0, 8, 24)); // >12m garantía (+20%
                                                                                                  // precio final)
            productoRepository.save(new Electronico("Auriculares Inalámbricos Sony XM5", 45000.0, 20, 6));
            productoRepository.save(new Electronico("Monitor Curvo Ultrawide 34\"", 89000.0, 12, 18)); // >12m garantía
                                                                                                       // (+20% precio
                                                                                                       // final)

            // 4 Ropas
            productoRepository.save(new Ropa("Campera de Cuero Organique", 25000.0, 30, "L"));
            productoRepository.save(new Ropa("Remera de Algodón Pima", 8500.0, 50, "M"));
            productoRepository.save(new Ropa("Pantalón Cargo Negro", 18000.0, 25, "XL")); // Talle XL (+10% precio
                                                                                          // final)
            productoRepository.save(new Ropa("Buzo Hoodie Oversized", 22000.0, 40, "S"));

            System.out.println("Sembrado completado.");
        }
    }
}
