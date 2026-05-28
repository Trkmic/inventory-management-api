package com.organique.erp;

import com.organique.erp.model.Compra;
import com.organique.erp.model.Electronico;
import com.organique.erp.model.Producto;
import com.organique.erp.model.Ropa;
import com.organique.erp.repository.CompraRepository;
import com.organique.erp.repository.ProductoRepository;
import com.organique.erp.service.InventarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ErpApplicationTests {

	@Autowired
	private ProductoRepository productoRepository;

	@Autowired
	private CompraRepository compraRepository;

	@Autowired
	private InventarioService inventarioService;

	@BeforeEach
	void cleanDatabase() {
		compraRepository.deleteAll();
		productoRepository.deleteAll();
	}

	@Test
	void contextLoads() {
		// Verifica que el contexto de Spring cargue correctamente
	}

	@Test
	void testElectronicoCalculoPrecioFinal() {
		// Electrónico con garantía corta (<= 12 meses) -> Sin recargo
		Electronico eCorto = new Electronico("Teclado Bluetooth", 5000.0, 10, 6);
		assertEquals(5000.0, eCorto.calcularPrecioFinal(), "El precio final no debería tener recargos");

		// Electrónico con garantía extendida (> 12 meses) -> +20% recargo
		Electronico eLargo = new Electronico("Laptop Pro", 100000.0, 5, 24);
		assertEquals(120000.0, eLargo.calcularPrecioFinal(), "Debería aplicarse un 20% de recargo por garantía extendida");
	}

	@Test
	void testRopaCalculoPrecioFinal() {
		// Ropa talle M -> Sin recargo
		Ropa rMediana = new Ropa("Pantalón Jean", 10000.0, 10, "M");
		assertEquals(10000.0, rMediana.calcularPrecioFinal(), "El precio final no debería tener recargos");

		// Ropa talle XL -> +10% recargo
		Ropa rGrande = new Ropa("Campera Abrigada", 20000.0, 5, "XL");
		assertEquals(22000.0, rGrande.calcularPrecioFinal(), "Debería aplicarse un 10% de recargo por talle XL");
	}

	@Test
	void testValidacionesProducto() {
		// Validar nombre menor a 3 caracteres
		assertThrows(IllegalArgumentException.class, () -> {
			new Ropa("Rem", 500.0, 5, "M");
		}, "Debería fallar si el nombre tiene menos de 3 caracteres");

		// Validar precio base menor a 1
		assertThrows(IllegalArgumentException.class, () -> {
			new Electronico("Cargador Rápido", 0.5, 5, 12);
		}, "Debería fallar si el precio base es menor a 1");
	}

	@Test
	@Transactional
	void testRegistrarCompraLogicaNegocio() {
		// Registrar productos para stock
		Producto p1 = productoRepository.save(new Electronico("Cámara Web Full HD", 8000.0, 5, 12));
		Producto p2 = productoRepository.save(new Ropa("Remera Básica Blanca", 3000.0, 10, "XL")); // Talle XL -> 3000 * 1.1 = 3300

		// Ejecutar compra
		Compra compra = inventarioService.registrarCompra("Ignacio Trkmic", List.of(p1.getId(), p2.getId()));

		assertNotNull(compra.getIdCompra(), "La compra debería tener un ID persistido");
		assertEquals("Ignacio Trkmic", compra.getCliente());
		assertEquals(2, compra.getProductos().size());
		
		// Total esperado: 8000 + 3300 = 11300
		assertEquals(11300.0, compra.getTotal(), "El total de la compra no coincide con el cálculo esperado");

		// Verificar reducción de stock
		Producto p1Actualizado = productoRepository.findById(p1.getId()).orElseThrow();
		Producto p2Actualizado = productoRepository.findById(p2.getId()).orElseThrow();
		
		assertEquals(4, p1Actualizado.getStock(), "El stock del producto 1 debería reducirse a 4");
		assertEquals(9, p2Actualizado.getStock(), "El stock del producto 2 debería reducirse a 9");
	}

	@Test
	void testRegistrarCompraStockInsuficiente() {
		// Registrar producto sin stock
		Producto sinStock = productoRepository.save(new Ropa("Zapatillas Deportivas", 15000.0, 0, "S"));

		assertThrows(IllegalStateException.class, () -> {
			inventarioService.registrarCompra("Pedro", List.of(sinStock.getId()));
		}, "Debería fallar la transacción por stock insuficiente");
	}
}
