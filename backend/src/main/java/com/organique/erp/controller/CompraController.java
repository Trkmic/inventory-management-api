package com.organique.erp.controller;

import com.organique.erp.model.Compra;
import com.organique.erp.service.InventarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/compras")
@CrossOrigin(origins = "*") // Permitir CORS
public class CompraController {

    @Autowired
    private InventarioService inventarioService;

    // Obtener historial completo de transacciones
    @GetMapping
    public ResponseEntity<List<Compra>> obtenerTodas() {
        return ResponseEntity.ok(inventarioService.obtenerTodasLasCompras());
    }

    // Registrar una nueva transacción (compra de productos)
    @PostMapping
    public ResponseEntity<?> registrar(@RequestBody RegistrarCompraDTO dto) {
        try {
            Compra compra = inventarioService.registrarCompra(
                    dto.getCliente(),
                    dto.getProductoIds());
            return ResponseEntity.ok(compra);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // DTO para la petición de registro de compra
    public static class RegistrarCompraDTO {
        private String cliente;
        private List<Integer> productoIds;

        public String getCliente() {
            return cliente;
        }

        public void setCliente(String cliente) {
            this.cliente = cliente;
        }

        public List<Integer> getProductoIds() {
            return productoIds;
        }

        public void setProductoIds(List<Integer> productoIds) {
            this.productoIds = productoIds;
        }
    }
}
