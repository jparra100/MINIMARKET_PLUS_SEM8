# MINIMARKET PLUS SEM8

Backend REST para la gestión de un minimarket. El proyecto permite administrar usuarios y roles, categorías, productos, movimientos de inventario, carritos de compra, ventas y sus detalles.

## Tecnologías

- Java 17
- Spring Boot 3.4.1
- Spring Web
- Spring Data JPA
- Spring Security
- H2 Database (en memoria)
- Maven Wrapper
- JUnit 5

## Funcionalidades

- CRUD de usuarios.
- CRUD de categorías y productos.
- Registro y consulta de movimientos de inventario.
- Gestión de productos en el carrito de cada usuario.
- Registro y consulta de ventas y detalles de venta.
- Usuarios asociados a uno o más roles.
- Autenticación mediante formulario y sesión de Spring Security.
- Persistencia temporal en una base de datos H2 en memoria.

## Modelo de datos

Las entidades principales y sus relaciones son:

- `Usuario`: contiene nombre de usuario, contraseña y roles.
- `Rol`: representa las autoridades asignadas a los usuarios.
- `Categoria`: agrupa uno o más productos.
- `Producto`: contiene nombre, precio, stock y categoría.
- `Inventario`: registra entradas o salidas de un producto.
- `Carrito`: relaciona un usuario con un producto y una cantidad.
- `Venta`: pertenece a un usuario y contiene sus detalles.
- `DetalleVenta`: relaciona una venta con un producto, cantidad y precio.

## Endpoints

Salvo la ruta pública indicada, los endpoints requieren una sesión autenticada.

| Recurso | Método | Ruta | Descripción |
|---|---|---|---|
| Público | `GET` | `/public/hola` | Comprueba que la aplicación está disponible |
| Usuarios | `GET` | `/api/usuarios` | Lista los usuarios |
| Usuarios | `GET` | `/api/usuarios/{id}` | Obtiene un usuario |
| Usuarios | `POST` | `/api/usuarios` | Crea un usuario |
| Usuarios | `PUT` | `/api/usuarios/{id}` | Actualiza un usuario |
| Usuarios | `DELETE` | `/api/usuarios/{id}` | Elimina un usuario |
| Categorías | `GET`, `POST` | `/api/categorias` | Lista o crea categorías |
| Categorías | `GET`, `PUT`, `DELETE` | `/api/categorias/{id}` | Consulta, actualiza o elimina una categoría |
| Productos | `GET`, `POST` | `/api/productos` | Lista o crea productos |
| Productos | `GET`, `PUT`, `DELETE` | `/api/productos/{id}` | Consulta, actualiza o elimina un producto |
| Inventario | `GET`, `POST` | `/api/inventario` | Lista o registra movimientos |
| Inventario | `GET`, `PUT`, `DELETE` | `/api/inventario/{id}` | Consulta, actualiza o elimina un movimiento |
| Carrito | `GET`, `POST` | `/api/carrito` | Lista o agrega elementos al carrito |
| Carrito | `GET`, `PUT`, `DELETE` | `/api/carrito/{id}` | Consulta, actualiza o elimina un elemento |
| Ventas | `GET`, `POST` | `/api/ventas` | Lista o registra ventas |
| Ventas | `GET` | `/api/ventas/{id}` | Obtiene una venta |
| Detalles | `GET`, `POST` | `/api/detalle-ventas` | Lista o registra detalles de venta |
| Detalles | `GET`, `PUT`, `DELETE` | `/api/detalle-ventas/{id}` | Consulta, actualiza o elimina un detalle |

## Requisitos

- JDK 17 o superior.
- No es necesario instalar Maven: el repositorio incluye Maven Wrapper.

## Ejecución local

Clona el repositorio y entra en su carpeta:

```bash
git clone https://github.com/jparra100/MINIMARKET_PLUS_SEM8.git
cd MINIMARKET_PLUS_SEM8
```

En Windows:

```powershell
.\mvnw.cmd spring-boot:run
```

En Linux o macOS:

```bash
./mvnw spring-boot:run
```

La aplicación queda disponible en `http://localhost:8080`. La ruta pública de comprobación es:

```text
GET http://localhost:8080/public/hola
```

## Base de datos H2

La configuración actual utiliza una base de datos en memoria:

```text
URL JDBC: jdbc:h2:mem:testdb
Usuario: sa
Contraseña: vacía
```

La consola H2 está habilitada en `http://localhost:8080/h2-console`. Como los datos están en memoria, se pierden al detener la aplicación.

## Pruebas

En Windows:

```powershell
.\mvnw.cmd test
```

En Linux o macOS:

```bash
./mvnw test
```

## Estructura del proyecto

```text
src/
├── main/
│   ├── java/com/minimarket/
│   │   ├── controller/    # Endpoints REST
│   │   ├── entity/        # Entidades JPA
│   │   ├── repository/    # Acceso a datos
│   │   ├── security/      # Configuración y modelos de seguridad
│   │   └── service/       # Lógica de negocio
│   └── resources/
│       └── application.properties
└── test/java/com/minimarket/       # Pruebas automatizadas
```

## Estado actual y mejoras pendientes

Este repositorio corresponde a una versión inicial del backend. Actualmente:

- No incluye datos iniciales ni un usuario administrador creado automáticamente.
- La API usa autenticación por formulario y sesión; `JwtUtil` todavía no implementa JWT.
- Los endpoints de roles no están expuestos mediante un controlador REST.
- Las ventas solo cuentan con operaciones de creación y consulta.
- Conviene incorporar DTO, validación de entradas y manejo global de errores.
- Para producción se debe reemplazar H2 por una base persistente, externalizar credenciales y revisar la configuración de seguridad.

## Repositorio

[github.com/jparra100/MINIMARKET_PLUS_SEM8](https://github.com/jparra100/MINIMARKET_PLUS_SEM8)
