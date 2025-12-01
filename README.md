# Vinylstore

## 1. Nombre del proyecto
Vinylstore

## 2. Integrantes
- Javier Tapia
- Sergio Del Campo

## 3. Funcionalidades
- **Autenticación de usuarios**: Login y registro de usuarios con validación de campos
- **Gestión de productos**: Visualización de catálogo de vinilos con filtrado por género
- **Detalles de producto**: Vista detallada de cada vinilo con información completa
- **Carrito de compras**: Agregar productos al carrito, modificar cantidades y eliminar items
- **Gestión de pedidos**: Confirmación de pedidos y reducción automática de stock
- **Historial de pedidos**: Visualización del historial de compras del usuario
- **Perfil de usuario**: Información del usuario y opciones de navegación
- **Panel de administración**: Gestión completa de productos (crear, editar, eliminar) para usuarios administradores
- **Base de datos local**: Almacenamiento local con Room Database
- **Carga de imágenes**: Visualización de imágenes de productos desde URLs

## 4. Endpoints utilizados (API externa y microservicio)

### 4.1. Microservicio Backend
**URL Base:** `http://201.188.130.203:8080/`

#### Autenticación (`/api/auth`)
- `POST /api/auth/register` - Registro de usuarios
- `POST /api/auth/login` - Inicio de sesión
- `POST /api/auth/logout` - Cerrar sesión
- `GET /api/auth/profile/{userId}` - Obtener perfil de usuario
- `PUT /api/auth/profile/{userId}` - Actualizar perfil de usuario

#### Productos (`/api/products`)
- `GET /api/products` - Obtener todos los productos (con filtro opcional por género)
- `GET /api/products/{id}` - Obtener producto por ID
- `POST /api/products` - Crear nuevo producto (admin)
- `PUT /api/products/{id}` - Actualizar producto (admin)
- `DELETE /api/products/{id}` - Eliminar producto (admin)
- `PUT /api/products/{id}/stock` - Actualizar stock de producto

#### Carrito (`/api/cart`)
- `GET /api/cart/{userId}` - Obtener carrito del usuario
- `POST /api/cart/{userId}/items` - Agregar item al carrito
- `PUT /api/cart/{userId}/items/{itemId}` - Actualizar item del carrito
- `DELETE /api/cart/{userId}/items/{itemId}` - Eliminar item del carrito
- `DELETE /api/cart/{userId}` - Vaciar carrito
- `GET /api/cart/{userId}/total` - Obtener total del carrito

#### Pedidos (`/api/orders`)
- `GET /api/orders/my-orders` - Obtener pedidos del usuario actual
- `GET /api/orders` - Obtener todos los pedidos (admin)
- `GET /api/orders/estado/{estado}` - Obtener pedidos por estado
- `POST /api/orders` - Crear nuevo pedido

### 4.2. API Externa - Last.fm
**URL Base:** `https://ws.audioscrobbler.com/`

#### Música (`/2.0/`)
- `GET /2.0/?method=chart.gettoptracks` - Obtener tracks más populares
- `GET /2.0/?method=tag.gettoptracks` - Obtener tracks por género/tag


## 5. Pasos para ejecutar
1. Asegúrate de tener Android Studio instalado (versión recomendada: última versión estable)
2. Clona o descarga el repositorio
3. Abre el proyecto en Android Studio
4. Espera a que Gradle sincronice las dependencias
5. Conecta un dispositivo Android o inicia un emulador
6. Ejecuta la aplicación haciendo clic en el botón "Run" o presionando Shift+F10
7. La aplicación se instalará y ejecutará en el dispositivo/emulador

**Requisitos mínimos:**
- Android SDK mínimo: API 24 (Android 7.0)
- Android SDK objetivo: API 36
- Java 11 o superior

## 6. Captura del APK firmado y .jks
