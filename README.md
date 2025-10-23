# 🛍️ Sistema de Gestión de Inventario para Tienda en Línea

Una tienda en línea especializada en productos electrónicos y ropa ha decidido modernizar su sistema para gestionar el inventario, optimizar las ventas y brindar mejores promociones a sus clientes. Este sistema permitirá realizar tareas básicas de gestión, implementar estrategias de personalización y análisis, fundamentales para el crecimiento de la tienda.

---

## **🏗️ Capa Modelo**

### **🛒 Clase Abstracta: Producto**
- **Atributos**:
  - `private static int id_incremental = 1`: incremental el id automaticamente de 1 en 1 iniciando desde 1.
  - `int id`: Identificador único (auto-incremental).
  - `String nombre`: Nombre del producto (mínimo 3 caracteres).
  - `double precioBase`: Precio base del producto (mayor a 0).

- **Métodos**:
  - Getters and Setters de todos los atributos.  
  - `abstract double calcularPrecioFinal()`: Calcula el precio final según el tipo de producto.
  - `String mostrarResumen()`: Devuelve el ID, nombre y precio final.
  - `toString`: de los atributos.  

### **🔌💻 Clase: Electrónico (Subclase de Producto)**
- **Atributos**:
  - `int garantia`: Duración de la garantía (en meses). Debe ser mayor o igual a 0.

- **Métodos**:
  - `@Override calcularPrecioFinal()`: Incrementa un 20% del precio base si la garantía supera los 12 meses.
  - `toString`: de los atributos.  
### **👗👔 Clase: Ropa (Subclase de Producto)**
- **Atributos**:
  - `String talla`: Talla de la prenda (“S”, “M”, “L”, “XL”). No puede ser nulo ni vacío.

- **Métodos**:
  - `@Override calcularPrecioFinal()`: Incrementa un 10% si la talla es "XL".
  - `toString`: de los atributos.  

### **🧾 Clase: Compra**
- **Atributos**:
  - `private static int idCompra_incremental = 1`: incremental el id automaticamente de 1 en 1 iniciando desde 1.
  - `int idCompra`: ID único de la compra.
  - `String cliente`: Nombre del cliente que realizó la compra.
  - `List<Producto> productos`: Lista de productos comprados.
  - `double total`: Precio total de la compra.

- **Métodos**:
  - `void agregarProducto(Producto producto)`: Agrega un producto a la lista de la compra y actualiza el total.
  - `void calcularTotal()`: Calcula el total sumando los precios finales de los productos.
  - `String mostrarDetalle()`: Muestra los detalles de la compra.
  - `toString`: de los atributos.  

---

## **⚙️ Capa Negocio**

### **🏬 Clase: GestorTienda**
- **Atributos**:
  - `Repository<Producto> productoRepository`: Repositorio para gestionar productos.
  - `Repository<Compra> compraRepository`: Repositorio para gestionar compras.

- **Métodos**:
  - `void agregarProducto(Producto producto)`: Agrega un producto al repositorio, validando sus datos.
  - `Optional<Producto> buscarProductoPorId(int id)`: Busca un producto por su ID.
  - `void realizarCompra(Compra compra)`: Registra una compra, valida los productos, calcula el total y la guarda.
  - `double calcularIngresos()`: Calcula los ingresos totales usando `stream()`.
  - `List<Producto> filtrarProductos(Predicate<Producto> criterio)`: Filtra productos según un criterio.
  - `void aplicarDescuento(Function<Producto, Double> descuento)`: Aplica descuentos dinámicos.
  - `List<Producto> listarProductos()`: retorna la lista de los productos.
  - `List<Compra> listarcompras()`: retorna la lista de las compras.

---

## **💾 Capa Persistencia**

### **📂 Interfaz Genérica: Repository<T>**
- **Métodos**:
  - `void add(T entity)`: Agrega una entidad.
  - `Optional<T> findById(int id)`: Busca una entidad por ID.
  - `List<T> findAll()`: Devuelve todas las entidades.

### **📦 Clase: ProductoRepository**
- Gestiona productos y su persistencia en un archivo binario.
- **Atributos**:
  - `List<Producto> productos`: Lista que almacena los productos.
  - `GestorPersistencia<Producto> gestorPersistencia`: Objeto para la persistencia de productos.
  - `int ultimoId`: Último ID asignado.

### **🛒 Clase: CompraRepository**
- Gestiona compras y su persistencia en un archivo binario.
- **Atributos**:
  - `List<Compra> compras`: Lista de compras realizadas.
  - `GestorPersistencia<Compra> gestorPersistencia`: Objeto para la persistencia de compras.

### **🗂️ Clase Genérica: GestorPersistencia<T extends Serializable>**
- **Atributos**:
  - `String archivo`: Nombre del archivo para almacenar datos.
- **Métodos**:
  - `void guardar(List<T> lista)`: Serializa y guarda una lista de objetos.
  - `List<T> cargar()`: Deserializa los objetos del archivo.
