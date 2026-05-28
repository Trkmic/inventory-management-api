package com.organique.erp.controller;

import com.organique.erp.model.Compra;
import com.organique.erp.model.Producto;
import com.organique.erp.service.GeminiAIService;
import com.organique.erp.service.InventarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ai")
@CrossOrigin(origins = "*") // Permitir CORS
public class AIController {

    @Autowired
    private InventarioService inventarioService;

    @Autowired
    private GeminiAIService geminiAIService;

    // Obtener reporte predictivo de abastecimiento por IA
    @GetMapping("/forecast")
    public ResponseEntity<String> obtenerReporteIA() {
        List<Producto> productos = inventarioService.obtenerTodosLosProductos();
        List<Compra> compras = inventarioService.obtenerTodasLasCompras();

        String reporte = geminiAIService.generarReportePredictivo(productos, compras);
        return ResponseEntity.ok(reporte);
    }
}
