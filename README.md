# MINIMARKET PLUS SEM8

Backend REST desarrollado con Spring Boot para centralizar la operación de un minimarket con varias sucursales. Esta versión corresponde a la EFT, forma C.

## Funciones implementadas

- Inventario y disponibilidad de productos por sucursal en tiempo real.
- Generación automática de órdenes de compra cuando el stock llega al mínimo.
- Recepción de órdenes con actualización del inventario.
- Reporte de productos con mayor y menor rotación.
- Carrito y pedidos en línea para retiro en tienda o despacho a domicilio.
- Ofertas y promociones centralizadas con cálculo del precio vigente.
- Registro e inicio de sesión con JWT.
- Autorización por roles `ADMIN`, `CAJERO` y `CLIENTE`.
- Documentación OpenAPI y navegación HATEOAS.
- Pruebas unitarias, de integración y reporte de cobertura JaCoCo.

## Tecnologías

- Java 17
- Spring Boot 3.4.1
- Spring Web, Data JPA, Security y HATEOAS
- JWT con JJWT
- H2 en memoria
- springdoc-openapi / Swagger UI
- JUnit 5, Mockito y JaCoCo
- Maven Wrapper

## Ejecución local

Requisitos: JDK 17 o superior. Maven no necesita instalación porque el repositorio incluye el Wrapper.

```powershell
git clone https://github.com/jparra100/MINIMARKET_PLUS_SEM8.git
cd MINIMARKET_PLUS_SEM8
$env:DEMO_DATA_ENABLED="true"
.\mvnw.cmd spring-boot:run
```

La API queda disponible en `http://localhost:8080` y Swagger UI en:

```text
http://localhost:8080/swagger-ui/index.html
```

Los datos de demostración están desactivados por defecto. Al habilitar `DEMO_DATA_ENABLED` se crean estas cuentas:

| Usuario | Contraseña de demostración | Rol |
|---|---|---|
| `admin` | `Admin123!` | `ADMIN` |
| `cajero` | `Cajero123!` | `CAJERO` |
| `cliente` | `Cliente123!` | `CLIENTE` |

Las contraseñas se pueden cambiar con `ADMIN_PASSWORD`, `CAJERO_PASSWORD` y `CLIENTE_PASSWORD`. La clave de firma debe externalizarse mediante `JWT_SECRET` fuera del ambiente académico.

## Uso de JWT en Swagger

1. Ejecutar `POST /api/auth/login` con una cuenta de demostración.
2. Copiar el valor de `token` de la respuesta.
3. Presionar **Authorize** en Swagger UI.
4. Pegar sólo el token; Swagger agrega automáticamente el prefijo `Bearer`.
5. Ejecutar los endpoints permitidos para el rol autenticado.

También es posible crear una cuenta de cliente con `POST /api/auth/register`.

## Permisos principales

| Recurso | ADMIN | CAJERO | CLIENTE |
|---|---:|---:|---:|
| Usuarios y roles | Administrar | No | No |
| Productos | Administrar | Consultar | Consultar |
| Sucursales y stock | Administrar | Consultar | Consultar |
| Proveedores y órdenes de compra | Administrar | No | No |
| Inventario | Administrar | Consultar | No |
| Ventas y detalles | Consultar/registrar | Consultar/registrar | No |
| Promociones | Administrar | Consultar | Consultar |
| Carrito y pedidos propios | Sí | Sí | Sí |
| Reporte de rotación | Sí | No | No |

## Endpoints de la forma C

| Función | Método y ruta |
|---|---|
| Registro e inicio de sesión | `POST /api/auth/register`, `POST /api/auth/login` |
| Sucursales | `/api/sucursales` |
| Stock centralizado | `GET /api/stock` |
| Disponibilidad por producto | `GET /api/stock/producto/{productoId}` |
| Stock por sucursal | `GET /api/stock/sucursal/{sucursalId}` |
| Stock mínimo | `PATCH /api/stock/{id}/minimo?cantidad={valor}` |
| Movimientos de inventario | `GET`, `POST /api/inventario` |
| Proveedores | `/api/proveedores` |
| Órdenes automáticas | `GET /api/ordenes-compra` |
| Recepción de orden | `PATCH /api/ordenes-compra/{id}/recibir` |
| Promociones | `/api/promociones` |
| Precio promocional | `GET /api/promociones/precio/{productoId}` |
| Carrito del cliente | `GET`, `POST /api/carrito` |
| Eliminar del carrito | `DELETE /api/carrito/{id}` |
| Crear y listar pedidos propios | `GET`, `POST /api/pedidos` |
| Ventas para empleados | `GET`, `POST /api/ventas` |
| Rotación de productos | `GET /api/reportes/rotacion-productos` |

Swagger UI contiene el contrato completo, los cuerpos de solicitud y los códigos de respuesta. Las respuestas de recursos incluyen `_links` para navegar hacia operaciones relacionadas mediante HATEOAS.

## Pruebas y cobertura

Ejecutar todas las pruebas:

```powershell
.\mvnw.cmd test
```

Ejecutar las pruebas y generar el reporte JaCoCo:

```powershell
.\mvnw.cmd clean verify
start target\site\jacoco\index.html
```

La suite comprueba, entre otros casos:

- autenticación, registro, token inválido y permisos por rol;
- movimientos y stock por sucursal;
- generación y recepción de órdenes de compra;
- aplicación de promociones vigentes;
- creación de pedidos y descuento transaccional de stock;
- reporte de productos vendidos y no vendidos;
- publicación de OpenAPI y enlaces HATEOAS.

## Configuración opcional

| Variable | Uso | Valor predeterminado |
|---|---|---|
| `DEMO_DATA_ENABLED` | Crea las tres cuentas de demostración | `false` |
| `JWT_SECRET` | Clave Base64 para firmar JWT | clave académica de desarrollo |
| `H2_CONSOLE_ENABLED` | Habilita la consola H2 | `false` |
| `ADMIN_PASSWORD` | Contraseña de demostración del admin | `Admin123!` |
| `CAJERO_PASSWORD` | Contraseña de demostración del cajero | `Cajero123!` |
| `CLIENTE_PASSWORD` | Contraseña de demostración del cliente | `Cliente123!` |

Si se habilita la consola H2, queda en `http://localhost:8080/h2-console` con JDBC `jdbc:h2:mem:testdb`, usuario `sa` y contraseña vacía.

## Material de entrega

- [Borrador del informe EFT](docs/INFORME_EFT.md)
- [Guía de evidencias y video](docs/GUIA_EVIDENCIAS_Y_VIDEO.md)

El informe debe transferirse al formato institucional disponible en AVA, completar los integrantes, insertar las capturas reales y exportarse a PDF antes de la entrega.
