// app.js - Organique Inventory ERP Dashboard Controller

const API_BASE = window.location.origin.includes('localhost') && !window.location.origin.includes('8080') ? 'http://localhost:8080/api' : 'https://inventory-management-api-jne8.onrender.com/api';

// Estado global de la UI
let productos = [];
let compras = [];
let selectedProductIds = [];

// Inicialización al cargar la página
document.addEventListener("DOMContentLoaded", () => {
    inicializarTabs();
    inicializarModal();
    inicializarFormularios();
    cargarDatos();
});

// 1. Gestión de Pestañas (Tabs)
function inicializarTabs() {
    const menuItems = document.querySelectorAll(".menu-item");
    menuItems.forEach(item => {
        item.addEventListener("click", () => {
            // Activar botón de menú
            menuItems.forEach(i => i.classList.remove("active"));
            item.classList.add("active");

            // Mostrar contenido correspondiente
            const tabId = item.dataset.tab;
            document.querySelectorAll(".tab-content").forEach(content => {
                content.classList.remove("active");
            });
            document.getElementById(`tab-${tabId}`).classList.add("active");

            // Recargar datos si es necesario
            if (tabId === 'dashboard') {
                actualizarDashboard();
            } else if (tabId === 'inventario') {
                renderTablaInventario();
            } else if (tabId === 'ventas') {
                renderSeleccionProductosVenta();
            } else if (tabId === 'historial') {
                renderTablaHistorial();
            }
        });
    });
}

// 2. Controladores del Modal de Alta de Productos
function inicializarModal() {
    const modal = document.getElementById("add-product-modal");
    const btnOpen = document.getElementById("btn-open-add-modal");
    const btnClose = document.getElementById("btn-close-modal");
    const btnCancel = document.getElementById("btn-cancel-modal");
    const selectTipo = document.getElementById("prod-tipo");
    const groupGarantia = document.getElementById("group-garantia");
    const groupTalla = document.getElementById("group-talla");

    btnOpen.addEventListener("click", () => {
        modal.classList.add("visible");
    });

    const cerrarModal = () => {
        modal.classList.remove("visible");
        document.getElementById("add-product-form").reset();
        // Mostrar garantía por defecto al limpiar
        groupGarantia.style.display = "flex";
        groupTalla.style.display = "none";
    };

    btnClose.addEventListener("click", cerrarModal);
    btnCancel.addEventListener("click", cerrarModal);

    // Conmutar campos según tipo de producto
    selectTipo.addEventListener("change", (e) => {
        if (e.target.value === "ELECTRONICO") {
            groupGarantia.style.display = "flex";
            groupTalla.style.display = "none";
        } else {
            groupGarantia.style.display = "none";
            groupTalla.style.display = "flex";
        }
    });
}

// 3. Cargar Datos desde el Backend
async function cargarDatos() {
    try {
        const resProd = await fetch(`${API_BASE}/productos`);
        if (resProd.ok) {
            productos = await resProd.json();
        }

        const resComp = await fetch(`${API_BASE}/compras`);
        if (resComp.ok) {
            compras = await resComp.json();
        }

        actualizarDashboard();
    } catch (err) {
        console.error("Error al conectar con la API de Spring Boot:", err);
        alert("No se pudo conectar con el servidor Spring Boot (¿está corriendo en " + API_BASE + "?)");
    }
}

// 4. Actualizar Vista del Dashboard
function actualizarDashboard() {
    // Calcular KPIs
    const totalProductos = productos.length;
    const stockCritico = productos.filter(p => p.stock < 10).length;
    const totalFacturado = compras.reduce((sum, c) => sum + c.total, 0);
    const valorInventario = productos.reduce((sum, p) => sum + (p.precioBase * p.stock), 0);

    // Renderizar en el DOM
    document.getElementById("kpi-total-productos").textContent = totalProductos;
    document.getElementById("kpi-stock-critico").textContent = stockCritico;
    document.getElementById("kpi-ventas-totales").textContent = `$${totalFacturado.toLocaleString('es-AR', { minimumFractionDigits: 2 })}`;
    document.getElementById("kpi-valor-inventario").textContent = `$${valorInventario.toLocaleString('es-AR', { minimumFractionDigits: 2 })}`;

    // Resaltar KPI de Stock Crítico si es mayor a cero
    const cardAlert = document.getElementById("card-alert-stock");
    if (stockCritico > 0) {
        cardAlert.classList.add("alert");
    } else {
        cardAlert.classList.remove("alert");
    }

    // Dibujar gráficos en Canvas
    renderGraficoDistribucion();
    renderGraficoBarrasStock();
}

// 5. Renderizar Gráfico de Distribución (Donut Chart)
function renderGraficoDistributionMock() {
    const canvas = document.getElementById("chart-distribution");
    if (!canvas) return;
    const ctx = canvas.getContext("2d");
    ctx.clearRect(0, 0, canvas.width, canvas.height);
}

function renderGraficoDistribucion() {
    const canvas = document.getElementById("chart-distribution");
    if (!canvas) return;
    const ctx = canvas.getContext("2d");

    // Limpiar canvas
    ctx.clearRect(0, 0, canvas.width, canvas.height);

    // Contar categorías
    let electronicos = 0;
    let ropas = 0;

    productos.forEach(p => {
        if (p.garantia !== undefined) electronicos += p.stock;
        else ropas += p.stock;
    });

    const total = electronicos + ropas;
    if (total === 0) {
        ctx.fillStyle = "#9ca3af";
        ctx.font = "14px Inter";
        ctx.fillText("Sin productos", 60, 110);
        return;
    }

    // Centro del gráfico
    const centerX = canvas.width / 2;
    const centerY = canvas.height / 2;
    const radius = 70;
    const thickness = 20;

    // Calcular ángulos
    const angleEl = (electronicos / total) * 2 * Math.PI;
    const angleRop = (ropas / total) * 2 * Math.PI;

    // Dibujar Electrónicos (Emerald)
    ctx.beginPath();
    ctx.arc(centerX, centerY, radius, 0, angleEl);
    ctx.strokeStyle = "#10b981";
    ctx.lineWidth = thickness;
    ctx.stroke();

    // Dibujar Ropa (Indigo)
    ctx.beginPath();
    ctx.arc(centerX, centerY, radius, angleEl, angleEl + angleRop);
    ctx.strokeStyle = "#6366f1";
    ctx.lineWidth = thickness;
    ctx.stroke();

    // Dibujar texto central
    ctx.fillStyle = "#f9fafb";
    ctx.font = "bold 20px Outfit";
    ctx.textAlign = "center";
    ctx.textBaseline = "middle";
    ctx.fillText(total.toString(), centerX, centerY - 6);

    ctx.fillStyle = "#9ca3af";
    ctx.font = "10px Inter";
    ctx.fillText("UNIDADES", centerX, centerY + 14);

    // Leyendas
    ctx.textAlign = "left";
    ctx.font = "11px Inter";

    // Emerald Box
    ctx.fillStyle = "#10b981";
    ctx.fillRect(10, canvas.height - 30, 12, 12);
    ctx.fillStyle = "#f9fafb";
    ctx.fillText(`Electrónico (${electronicos} u)`, 28, canvas.height - 20);

    // Indigo Box
    ctx.fillStyle = "#6366f1";
    ctx.fillRect(centerX + 10, canvas.height - 30, 12, 12);
    ctx.fillStyle = "#f9fafb";
    ctx.fillText(`Ropa (${ropas} u)`, centerX + 28, canvas.height - 20);
}

// 6. Renderizar Gráfico de Barras de Stock
function renderGraficoBarrasStock() {
    const canvas = document.getElementById("chart-stock-bars");
    if (!canvas) return;
    const ctx = canvas.getContext("2d");

    ctx.clearRect(0, 0, canvas.width, canvas.height);

    if (productos.length === 0) {
        ctx.fillStyle = "#9ca3af";
        ctx.font = "14px Inter";
        ctx.fillText("Sin stock cargado", 100, 110);
        return;
    }

    // Limitar a los 6 productos con stock más bajo para el gráfico
    const topProd = [...productos].sort((a, b) => a.stock - b.stock).slice(0, 6);

    const margin = 30;
    const chartHeight = canvas.height - 60;
    const barWidth = 32;
    const gap = (canvas.width - margin * 2 - (barWidth * topProd.length)) / (topProd.length - 1 || 1);

    // Encontrar stock máximo para escala
    const maxStock = Math.max(...topProd.map(p => p.stock), 10);

    topProd.forEach((p, index) => {
        const x = margin + index * (barWidth + gap);
        const ratio = p.stock / maxStock;
        const height = ratio * chartHeight;
        const y = canvas.height - 40 - height;

        // Color de barra según stock crítico
        if (p.stock < 5) ctx.fillStyle = "#ef4444"; // Crítico - Rose
        else if (p.stock < 10) ctx.fillStyle = "#f59e0b"; // Medio - Amber
        else ctx.fillStyle = "#10b981"; // Óptimo - Emerald

        // Dibujar barra con bordes redondeados arriba
        ctx.beginPath();
        ctx.roundRect(x, y, barWidth, height, [4, 4, 0, 0]);
        ctx.fill();

        // Número de stock sobre la barra
        ctx.fillStyle = "#f9fafb";
        ctx.font = "bold 10px Inter";
        ctx.textAlign = "center";
        ctx.fillText(p.stock.toString(), x + barWidth / 2, y - 8);

        // Nombre del producto debajo (truncado)
        ctx.fillStyle = "#9ca3af";
        ctx.font = "9px Inter";
        const shortName = p.nombre.length > 8 ? p.nombre.substring(0, 6) + ".." : p.nombre;
        ctx.fillText(shortName, x + barWidth / 2, canvas.height - 20);
    });
}

// 7. Renderizar Tabla de Inventario
function renderTablaInventario() {
    const tbody = document.getElementById("inventory-table-body");
    if (!tbody) return;

    tbody.innerHTML = "";

    if (productos.length === 0) {
        tbody.innerHTML = `<tr><td colspan="8" style="text-align: center; color: var(--text-secondary); padding: 40px 0;">No hay productos registrados en el inventario.</td></tr>`;
        return;
    }

    productos.forEach(p => {
        const tr = document.createElement("tr");
        const esElectronico = p.garantia !== undefined;

        // Formatear especificación
        const spec = esElectronico ? `Garantía: ${p.garantia}m` : `Talla: ${p.talla}`;
        const catName = esElectronico ? "Electrónico" : "Ropa";
        const badgeClass = esElectronico ? "badge-electronico" : "badge-ropa";

        // Alerta de stock
        let stockHtml = `<span>${p.stock}</span>`;
        if (p.stock === 0) {
            stockHtml = `<span class="badge stock-out">Sin Stock</span>`;
        } else if (p.stock < 10) {
            stockHtml = `<span class="stock-warning">${p.stock} <i class="fa-solid fa-triangle-exclamation"></i></span>`;
        }

        tr.innerHTML = `
            <td>${p.id}</td>
            <td style="font-weight: 600; color: var(--text-primary);">${p.nombre}</td>
            <td><span class="badge ${badgeClass}">${catName}</span></td>
            <td style="font-family: monospace; font-size: 0.85rem;">${spec}</td>
            <td>${stockHtml}</td>
            <td>$${p.precioBase.toLocaleString('es-AR', { minimumFractionDigits: 2 })}</td>
            <td style="color: var(--color-primary); font-weight: 700;">$${((p.precioFinal !== undefined && p.precioFinal !== null) ? p.precioFinal : calcularPrecioFinalLocal(p)).toLocaleString('es-AR', { minimumFractionDigits: 2 })}</td>
            <td>
                <button class="btn btn-danger btn-delete" data-id="${p.id}" style="padding: 6px 12px; border-radius: 8px;">
                    <i class="fa-solid fa-trash-can"></i>
                </button>
            </td>
        `;

        // Evento botón eliminar
        tr.querySelector(".btn-delete").addEventListener("click", async (e) => {
            const id = e.currentTarget.dataset.id;
            if (confirm("¿Estás seguro de eliminar este producto del inventario?")) {
                try {
                    const res = await fetch(`${API_BASE}/productos/${id}`, {
                        method: 'DELETE'
                    });
                    if (res.ok) {
                        productos = productos.filter(prod => prod.id != id);
                        renderTablaInventario();
                        actualizarDashboard();
                    } else {
                        const errMsg = await res.text();
                        alert(`Error al eliminar: ${errMsg}`);
                    }
                } catch (err) {
                    alert("Ocurrió un error al conectar con la API.");
                }
            }
        });

        tbody.appendChild(tr);
    });
}

// 8. Renderizar Selección de Productos para Nueva Venta
function renderSeleccionProductosVenta() {
    const grid = document.getElementById("sale-product-select-grid");
    if (!grid) return;

    grid.innerHTML = "";
    selectedProductIds = [];
    document.getElementById("sale-total-display").textContent = "$0.00";

    const disponibles = productos.filter(p => p.stock > 0);

    if (disponibles.length === 0) {
        grid.innerHTML = `<div style="grid-column: 1/-1; text-align: center; color: var(--text-secondary); padding: 30px 0;">No hay productos con stock disponible para la venta.</div>`;
        return;
    }

    disponibles.forEach(p => {
        const card = document.createElement("div");
        card.className = "product-select-card";
        card.dataset.id = p.id;

        const esElectronico = p.garantia !== undefined;
        const spec = esElectronico ? `${p.garantia}m` : `Talla ${p.talla}`;

        card.innerHTML = `
            <h4>${p.nombre}</h4>
            <div class="price">$${((p.precioFinal !== undefined && p.precioFinal !== null) ? p.precioFinal : calcularPrecioFinalLocal(p)).toLocaleString('es-AR', { minimumFractionDigits: 2 })}</div>
            <div class="stock">Stock: ${p.stock} u. (${spec})</div>
        `;

        card.addEventListener("click", () => {
            const id = parseInt(card.dataset.id);
            if (selectedProductIds.includes(id)) {
                // Deseleccionar
                selectedProductIds = selectedProductIds.filter(val => val !== id);
                card.classList.remove("selected");
            } else {
                // Seleccionar
                selectedProductIds.push(id);
                card.classList.add("selected");
            }

            // Calcular total acumulado de los productos seleccionados
            let totalVenta = 0;
            selectedProductIds.forEach(selectedId => {
                const selectedProd = productos.find(prod => prod.id === selectedId);
                if (selectedProd) {
                    // Simular precio final computado en el backend
                    totalVenta += calcularPrecioFinalLocal(selectedProd);
                }
            });
            document.getElementById("sale-total-display").textContent = `$${totalVenta.toLocaleString('es-AR', { minimumFractionDigits: 2 })}`;
        });

        grid.appendChild(card);
    });
}

// Helper para calcular precio final en el cliente antes de enviar la venta
function calcularPrecioFinalLocal(p) {
    let precio = p.precioBase;
    if (p.garantia !== undefined && p.garantia > 12) {
        precio *= 1.20;
    } else if (p.talla !== undefined && p.talla.toUpperCase() === "XL") {
        precio *= 1.10;
    }
    return precio;
}

// 9. Renderizar Tabla Historial de Ventas
function renderTablaHistorial() {
    const tbody = document.getElementById("history-table-body");
    if (!tbody) return;

    tbody.innerHTML = "";

    if (compras.length === 0) {
        tbody.innerHTML = `<tr><td colspan="5" style="text-align: center; color: var(--text-secondary); padding: 40px 0;">No se registran transacciones de venta todavía.</td></tr>`;
        return;
    }

    // Ordenar de más nueva a más vieja
    const ordenadas = [...compras].sort((a, b) => b.idCompra - a.idCompra);

    ordenadas.forEach(c => {
        const tr = document.createElement("tr");

        // Formatear fecha
        const date = new Date(c.fecha);
        const formattedDate = `${date.getDate().toString().padStart(2, '0')}/${(date.getMonth() + 1).toString().padStart(2, '0')}/${date.getFullYear()} ${date.getHours().toString().padStart(2, '0')}:${date.getMinutes().toString().padStart(2, '0')}`;

        // Concatenar nombres de productos
        const items = c.productos.map(p => p.nombre).join(", ");

        tr.innerHTML = `
            <td style="font-family: monospace; font-weight: 700; color: var(--color-primary);">#FAC-${c.idCompra}</td>
            <td>${formattedDate}</td>
            <td style="font-weight: 600; color: var(--text-primary);">${c.cliente}</td>
            <td style="max-width: 250px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap;" title="${items}">${items}</td>
            <td style="font-weight: 700; color: var(--color-primary); font-size: 0.95rem;">$${c.total.toLocaleString('es-AR', { minimumFractionDigits: 2 })}</td>
        `;

        tbody.appendChild(tr);
    });
}

// 10. Gestión e Inicialización de Formularios
function inicializarFormularios() {
    // A. Formulario para Agregar Producto
    const formAdd = document.getElementById("add-product-form");
    formAdd.addEventListener("submit", async (e) => {
        e.preventDefault();

        const tipo = document.getElementById("prod-tipo").value;
        const nombre = document.getElementById("prod-nombre").value;
        const precio = parseFloat(document.getElementById("prod-precio").value);
        const stock = parseInt(document.getElementById("prod-stock").value);

        let endpoint = `${API_BASE}/productos/ropa`;
        let payload = { nombre, precioBase: precio, stock };

        if (tipo === "ELECTRONICO") {
            endpoint = `${API_BASE}/productos/electronico`;
            payload.garantia = parseInt(document.getElementById("prod-garantia").value);
        } else {
            payload.talla = document.getElementById("prod-talla").value;
        }

        try {
            const res = await fetch(endpoint, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(payload)
            });

            if (res.ok) {
                const guardado = await res.json();
                productos.push(guardado);

                // Cerrar modal
                document.getElementById("add-product-modal").classList.remove("visible");
                formAdd.reset();

                // Actualizar vistas
                actualizarDashboard();
                renderTablaInventario();
            } else {
                const errText = await res.text();
                alert(`Error al registrar producto: ${errText}`);
            }
        } catch (err) {
            alert("No se pudo conectar con el servidor Spring Boot.");
        }
    });

    // B. Formulario para Registrar Venta (Checkout)
    const formSale = document.getElementById("sale-form");
    formSale.addEventListener("submit", async (e) => {
        e.preventDefault();

        const cliente = document.getElementById("sale-client-name").value;

        if (selectedProductIds.length === 0) {
            alert("Por favor selecciona al menos un producto para la venta.");
            return;
        }

        const payload = {
            cliente,
            productoIds: selectedProductIds
        };

        try {
            const res = await fetch(`${API_BASE}/compras`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(payload)
            });

            if (res.ok) {
                const compraRealizada = await res.json();
                compras.push(compraRealizada);

                // Descontar stock localmente para evitar desfases antes de recargar
                selectedProductIds.forEach(id => {
                    const prod = productos.find(p => p.id === id);
                    if (prod) prod.stock--;
                });

                formSale.reset();
                selectedProductIds = [];
                document.getElementById("sale-total-display").textContent = "$0.00";

                alert(`¡Venta #${compraRealizada.idCompra} registrada con éxito!`);

                // Recargar catálogo y actualizar vistas
                await cargarDatos();
                // Ir a pestaña historial
                document.querySelector('[data-tab="historial"]').click();
            } else {
                const errText = await res.text();
                alert(`Error al registrar venta: ${errText}`);
            }
        } catch (err) {
            alert("Error al conectar con la API de Spring Boot.");
        }
    });

    // C. Petición de Reporte de IA
    const btnAI = document.getElementById("btn-run-ai-forecast");
    const aiReport = document.getElementById("ai-report-container");
    const aiPlaceholder = document.getElementById("ai-placeholder-view");

    btnAI.addEventListener("click", async () => {
        // Mostrar animación de carga en el botón
        const originalHTML = btnAI.innerHTML;
        btnAI.disabled = true;
        btnAI.innerHTML = `<i class="fa-solid fa-spinner animate-spin"></i> Consultando IA...`;

        try {
            const res = await fetch(`${API_BASE}/ai/forecast`);
            if (res.ok) {
                const markdownText = await res.text();
                console.log("Raw Markdown from backend:", markdownText);
                // Sanitizar símbolos '<' sueltos que causan bugs de renderizado HTML
                const cleanText = markdownText.replace(/</g, '&lt;');

                // Formatear markdown usando MarkedJS
                aiReport.innerHTML = marked.parse(cleanText);
                aiPlaceholder.style.display = "none";
                aiReport.style.display = "block";
            } else {
                alert("Error al procesar el pronóstico de IA del backend.");
            }
        } catch (err) {
            alert("No se pudo obtener la predicción de IA del servidor.");
        } finally {
            btnAI.disabled = false;
            btnAI.innerHTML = originalHTML;
        }
    });
}
