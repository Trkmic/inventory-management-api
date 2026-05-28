package com.organique.erp.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.organique.erp.model.Compra;
import com.organique.erp.model.Producto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class GeminiAIService {

    @Value("${api.gemini.key:}")
    private String geminiApiKey;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public String generarReportePredictivo(List<Producto> productos, List<Compra> compras) {
        // Intentar obtener la API Key de las variables de entorno si la propiedad está
        // vacía
        String key = geminiApiKey;
        if (key == null || key.trim().isEmpty()) {
            key = System.getenv("GEMINI_API_KEY");
        }

        if (key == null || key.trim().isEmpty() || key.equals("YOUR_KEY_HERE")) {
            return generarReporteMockLocal(productos, compras);
        }

        try {
            // Construir la descripción de los productos actuales
            String productosStr = productos.stream()
                    .map(p -> String.format("- ID: %d | %s | Stock: %d | Precio U.: $%.2f | Tipo: %s",
                            p.getId(), p.getNombre(), p.getStock(), p.calcularPrecioFinal(),
                            p.getClass().getSimpleName()))
                    .collect(Collectors.joining("\n"));

            // Construir la descripción de las transacciones históricas
            String comprasStr = compras.isEmpty()
                    ? "No hay transacciones registradas aún."
                    : compras.stream()
                            .map(c -> String.format("- Factura N° %d | Cliente: %s | Total: $%.2f | Ítems: [%s]",
                                    c.getIdCompra(), c.getCliente(), c.getTotal(),
                                    c.getProductos().stream().map(Producto::getNombre)
                                            .collect(Collectors.joining(", "))))
                            .collect(Collectors.joining("\n"));

            // Construir el Prompt rico en contexto
            String promptText = "Eres un analista de inventarios y cadena de suministro inteligente.\n\n" +
                    "Analiza el siguiente catálogo de inventario actual y las compras históricas:\n\n" +
                    "### INVENTARIO ACTUAL:\n" + productosStr + "\n\n" +
                    "### HISTORIAL DE VENTAS:\n" + comprasStr + "\n\n" +
                    "Genera un reporte de abastecimiento predictivo y analítico en español. " +
                    "El reporte debe incluir:\n" +
                    "1. ⚠️ **Alertas de Stock Crítico**: Lista de productos con stock bajo (menor a 10 unidades) o agotados. Evita usar el símbolo '<' (menor que) en tu respuesta, escribe siempre 'menor a' en letras.\n"
                    +
                    "2. 🔥 **Tendencias de Demanda**: Productos más vendidos según el historial de ventas.\n" +
                    "3. 📦 **Recomendaciones de Reposición**: Cantidad sugerida a comprar para reponer stock para los próximos 30 días, detallando si conviene comprar más Ropa o Electrónicos según el movimiento.\n"
                    +
                    "4. 💡 **Estrategias**: Un consejo de negocio para optimizar el capital de trabajo (evitar sobre-stock o quiebres).\n\n"
                    +
                    "Formatea el reporte con un estilo visual excelente en Markdown, usando emojis y una tabla comparativa de reposición sugerida.";

            // Configurar petición HTTP
            String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key="
                    + key;

            // Cuerpo de la petición en JSON estructurado para Gemini
            Map<String, Object> textPart = new HashMap<>();
            textPart.put("text", promptText);

            Map<String, Object> parts = new HashMap<>();
            parts.put("parts", List.of(textPart));

            Map<String, Object> contents = new HashMap<>();
            contents.put("contents", List.of(parts));

            Map<String, Object> generationConfig = new HashMap<>();
            generationConfig.put("maxOutputTokens", 800);
            generationConfig.put("temperature", 0.4);
            contents.put("generationConfig", generationConfig);

            String requestBody = objectMapper.writeValueAsString(contents);

            HttpClient client = HttpClient.newBuilder()
                    .connectTimeout(Duration.ofSeconds(10))
                    .build();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                JsonNode root = objectMapper.readTree(response.body());
                JsonNode candidate = root.path("candidates").get(0);
                JsonNode textNode = candidate.path("content").path("parts").get(0).path("text");
                return textNode.asText();
            } else {
                System.err.println(
                        "Gemini API Error, Status Code: " + response.statusCode() + ", Body: " + response.body());
                return generarReporteMockLocal(productos, compras) + "\n\n*(Nota: El servidor de Gemini devolvió error "
                        + response.statusCode() + ", se cargó el análisis local de respaldo)*";
            }

        } catch (Exception e) {
            System.err.println("Fallo al conectar con la API de Gemini: " + e.getMessage());
            return generarReporteMockLocal(productos, compras)
                    + "\n\n*(Nota: No se pudo conectar con Gemini API, se cargó el análisis local de respaldo)*";
        }
    }

    // Genera un reporte analítico basado en reglas estadísticas locales si no hay
    // API Key o falla la conexión
    private String generarReporteMockLocal(List<Producto> productos, List<Compra> compras) {
        StringBuilder sb = new StringBuilder();
        sb.append("# 📊 Reporte Predictivo de Stock (Procesamiento Local)\n\n");
        sb.append(
                "Este reporte fue generado de forma autónoma por el motor local de análisis debido a que no se configuró o falló la conexión con la clave API de Gemini.\n\n");

        // 1. Alertas de Stock Crítico
        sb.append("## ⚠️ Alertas de Stock Crítico (< 10 unidades)\n");
        List<Producto> stockBajo = productos.stream().filter(p -> p.getStock() < 10).collect(Collectors.toList());
        if (stockBajo.isEmpty()) {
            sb.append("✅ ¡Excelente! Todos los productos cuentan con niveles de stock óptimos (>= 10 unidades).\n\n");
        } else {
            sb.append("Se detectaron los siguientes artículos con existencias críticas:\n");
            for (Producto p : stockBajo) {
                sb.append(String.format(
                        "- **%s**: Solo quedan **%d** unidades en inventario (Stock mínimo sugerido: 10).\n",
                        p.getNombre(), p.getStock()));
            }
            sb.append("\n");
        }

        // 2. Análisis de Demanda
        sb.append("## 🔥 Tendencias de Demanda\n");
        if (compras.isEmpty()) {
            sb.append(
                    "📈 Aún no se registran compras históricas. Las recomendaciones se basan puramente en el stock actual de catálogo.\n\n");
        } else {
            Map<String, Long> conteoVentas = compras.stream()
                    .flatMap(c -> c.getProductos().stream())
                    .collect(Collectors.groupingBy(Producto::getNombre, Collectors.counting()));

            sb.append("Productos con mayor índice de rotación en caja:\n");
            conteoVentas.entrySet().stream()
                    .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
                    .limit(3)
                    .forEach(e -> sb
                            .append(String.format("- **%s**: Vendido **%d** veces.\n", e.getKey(), e.getValue())));
            sb.append("\n");
        }

        // 3. Tabla de Reposición Sugerida
        sb.append("## 📦 Plan de Compra Sugerido (Próximos 30 días)\n");
        sb.append("| ID | Producto | Categoría | Stock Actual | Compra Sugerida | Prioridad |\n");
        sb.append("|---|---|---|---|---|---|\n");
        for (Producto p : productos) {
            int stock = p.getStock();
            int sugerido = 0;
            String prioridad = "Baja";

            if (stock == 0) {
                sugerido = 25;
                prioridad = "🔴 CRÍTICA";
            } else if (stock < 5) {
                sugerido = 20;
                prioridad = "🔴 ALTA";
            } else if (stock < 10) {
                sugerido = 12;
                prioridad = "🟡 MEDIA";
            } else if (stock < 15) {
                sugerido = 5;
                prioridad = "🟢 BAJA";
            }

            if (sugerido > 0) {
                String cat = p.getClass().getSimpleName();
                sb.append(String.format("| %d | %s | %s | %d | **+%d u.** | %s |\n", p.getId(), p.getNombre(), cat,
                        stock, sugerido, prioridad));
            }
        }
        sb.append("\n");

        // 4. Recomendaciones Estratégicas
        sb.append("## 💡 Estrategia de Logística Sugerida\n");
        sb.append(
                "- **Electrónicos**: Dado el alto costo de adquisición y el recargo del 20% en garantías extendidas de más de 12 meses, se sugiere una estrategia de **Just-in-Time (JIT)** para los artículos de alta gama para no congelar capital de trabajo.\n");
        sb.append(
                "- **Ropa**: El talle **XL** tiene recargo del 10% y suele tener alta rotación. Se aconseja mantener un colchón de stock de seguridad de +5 unidades en este talle para evitar quiebres de venta.\n");

        return sb.toString();
    }
}
