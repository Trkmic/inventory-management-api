# Guía de Despliegue en la Nube: Organique Inventory ERP

Esta guía detalla los pasos para desplegar tu sistema **ERP / Dashboard de Inventario** (Spring Boot + H2 + Frontend) en vivo de forma **100% gratuita** utilizando **Render.com**.

Como empaquetamos el frontend dentro de la carpeta `src/main/resources/static`, todo el proyecto se compilará y ejecutará como un **único servicio web** en la nube.

---

## Paso 1: Inicializar Git y Subir el Código a GitHub

1. Abre tu terminal de Git (o consola de comandos) en la carpeta del proyecto `f:\UTN FACULTAD\UTN programacion III\inventory-erp-springboot` y ejecuta:
   ```bash
   # Inicializar git
   git init

   # Agregar todos los archivos
   git add .

   # Crear commit inicial
   git commit -m "feat: initial commit with Spring Boot backend and static frontend"
   ```

2. Ve a tu cuenta de **GitHub** y crea un nuevo repositorio llamado **`inventory-erp-springboot`** (deja desmarcadas las opciones de README, .gitignore y licencia).

3. Vincula y sube el código local a tu nuevo repositorio de GitHub (reemplaza con tu usuario real):
   ```bash
   git remote add origin https://github.com/Trkmic/inventory-erp-springboot.git
   git branch -M main
   git push -u origin main
   ```

---

## Paso 2: Desplegar en Render.com mediante Docker

Dado que Render no tiene soporte directo nativo "out-of-the-box" para Java en su plan gratuito, utilizaremos el archivo `Dockerfile` que ya creamos en la subcarpeta `backend`. Esto le indica a Render cómo compilar y empaquetar el proyecto usando Docker automáticamente.

1. Inicia sesión en [Render Dashboard](https://dashboard.render.com).
2. Haz clic en el botón **New +** (arriba a la derecha) y selecciona **Web Service**.
3. Selecciona la opción **Build and deploy from a Git repository**.
4. Conecta tu repositorio de GitHub **`inventory-erp-springboot`**.
5. Configura los parámetros del servicio:
   * **Name:** `inventory-erp-springboot`
   * **Region:** Selecciona la más cercana (ej: `us-east` o `singapore`).
   * **Branch:** `main`
   * **Root Directory:** `backend` *(¡Muy importante! Indica que la configuración y el Dockerfile están en la subcarpeta backend)*.
   * **Runtime:** `Docker` *(Render detectará automáticamente el Dockerfile en la carpeta root de compilación)*.
   * **Instance Type:** `Free`
6. En la sección **Environment Variables** (Variables de Entorno), haz clic en **Add Environment Variable** para configurar la API de Gemini (opcional pero recomendado):
   * **Key:** `GEMINI_API_KEY`
   * **Value:** *(Pega tu clave de API de Gemini)*
7. Haz clic en **Create Web Service**.

---

## Paso 3: Monitoreo del Deploy
* Render comenzará a construir la imagen de Docker (descargará Maven, compilará el `.jar` de Spring Boot omitiendo tests, y preparará la máquina virtual). Esto puede tardar entre 4 y 7 minutos en completarse la primera vez.
* En cuanto veas el mensaje `Your service is live!`, copia la URL de producción provista por Render (ej: `https://inventory-erp-springboot.onrender.com`).

---

## Paso 4: Actualizar tu Portafolio Astro
Una vez que el proyecto esté en vivo, abre tu portafolio Astro en `f:\CV_nacho\paginaWeb` y actualiza el link del botón de "Ver sitio en vivo":

1. Abre el archivo `src/pages/index.astro` en la línea 74 y reemplaza el `#` por tu URL de Render:
   ```astro
   // src/pages/index.astro - Línea 74
   linkWeb: "https://inventory-erp-springboot.onrender.com",
   ```
2. Guarda el archivo y realiza un commit/push en tu repositorio de la página web:
   ```bash
   git add src/pages/index.astro
   git commit -m "chore: update live link for inventory erp project"
   git push origin main
   ```

---

## Notas del Plan Gratuito
* **H2 Database:** Al utilizar una base de datos local en archivo (`inventory_db`), el almacenamiento en el plan gratuito de Render es efímero. Esto significa que si el servidor se duerme (por 15 minutos de inactividad) o se reinicia, la base de datos volverá a su estado inicial sembrado (con los 8 productos de prueba preestablecidos). Esto es ideal para un portfolio ya que siempre estará limpio y funcional para los reclutadores.
* **Tiempo de Activación:** Al igual que el backend de Node, la primera petición tras un periodo largo de inactividad puede demorar 30-50 segundos mientras se reactiva el contenedor en Render.
