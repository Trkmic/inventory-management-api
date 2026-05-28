package com.organique.erp.controller;

import com.organique.erp.model.Producto;
import com.organique.erp.service.InventarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/productos")
@CrossOrigin(origins = "*") // Permitir CORS de cualquier puerto (desarrollo y producción)
public class ProductoController {

    @Autowired
    private InventarioService inventarioService;

    // Obtener catálogo completo
    @GetMapping
    public ResponseEntity<List<Producto>> obtenerTodos() {
        return ResponseEntity.ok(inventarioService.obtenerTodosLosProductos());
    }

    // Obtener producto por ID
    @GetMapping("/{id}")
    public ResponseEntity<Producto> obtenerPorId(@PathVariable Integer id) {
        try {
            return ResponseEntity.ok(inventarioService.obtenerProductoPorId(id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Crear un producto Electrónico
    @PostMapping("/electronico")
    public ResponseEntity<?> crearElectronico(@RequestBody ElectronicoDTO dto) {
        try {
            Producto guardado = inventarioService.guardarElectronico(
                    dto.getNombre(),
                    dto.getPrecioBase(),
                    dto.getStock(),
                    dto.getGarantia());
            return ResponseEntity.ok(guardado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Crear un producto de Ropa
    @PostMapping("/ropa")
    public ResponseEntity<?> crearRopa(@RequestBody RopaDTO dto) {
        try {
            Producto guardado = inventarioService.guardarRopa(
                    dto.getNombre(),
                    dto.getPrecioBase(),
                    dto.getStock(),
                    dto.getTalla());
            return ResponseEntity.ok(guardado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Actualizar producto genérico
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Integer id, @RequestBody ActualizarProductoDTO dto) {
        try {
            Producto actualizado = inventarioService.actualizarProducto(
                    id,
                    dto.getNombre(),
                    dto.getPrecioBase(),
                    dto.getStock(),
                    dto.getGarantia(),
                    dto.getTalla());
            return ResponseEntity.ok(actualizado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Eliminar producto
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Integer id) {
        try {
            inventarioService.eliminarProducto(id);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // clases auxiliares para el mapeo JSON (DTOs)
    public static class ElectronicoDTO {
        private String nombre;
        private double precioBase;
        private int stock;
        private int garantia;

        // Getters & Setters
        public String getNombre() {
            return nombre;
        }

        public void setNombre(String nombre) {
            this.nombre = nombre;
        }

        public double getPrecioBase() {
            return precioBase;
        }

        public void setPrecioBase(double precioBase) {
            this.precioBase = precioBase;
        }

        public int getStock() {
            return stock;
        }

        public void setStock(int stock) {
            this.stock = stock;
        }

        public int getGarantia() {
            return garantia;
        }

        public void setGarantia(int garantia) {
            this.garantia = garantia;
        }
    }

    public static class RopaDTO {
        private String nombre;
        private double precioBase;
        private int stock;
        private String talla;

        // Getters & Setters
        public String getNombre() {
            return nombre;
        }

        public void setNombre(String nombre) {
            this.nombre = nombre;
        }

        public double getPrecioBase() {
            return precioBase;
        }

        public void setPrecioBase(double precioBase) {
            this.precioBase = precioBase;
        }

        public int getStock() {
            return stock;
        }

        public void setStock(int stock) {
            this.stock = stock;
        }

        public String getTalla() {
            return talla;
        }

        public void setTalla(String talla) {
            this.talla = talla;
        }
    }

    public static class ActualizarProductoDTO {
        private String nombre;
        private double precioBase;
        private int stock;
        private Integer garantia;
        private String talla;

        // Getters & Setters
        public String getNombre() {
            return nombre;
        }

        public void setNombre(String nombre) {
            this.nombre = nombre;
        }

        public double getPrecioBase() {
            return precioBase;
        }

        public void setPrecioBase(double precioBase) {
            this.precioBase = precioBase;
        }

        public int getStock() {
            return stock;
        }

        public void setStock(int stock) {
            this.stock = stock;
        }

        public Integer getGarantia() {
            return garantia;
        }

        public void setGarantia(Integer garantia) {
            this.garantia = garantia;
        }

        public String getTalla() {
            return talla;
        }

        public void setTalla(String talla) {
            this.talla = talla;
        }
    }
}
