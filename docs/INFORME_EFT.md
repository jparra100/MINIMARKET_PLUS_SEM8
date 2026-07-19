# Informe EFT — Minimarket Plus

> Borrador de contenido. Debe copiarse al formato institucional disponible en AVA, completar los datos del equipo, insertar las capturas reales y exportarse a PDF.

## Portada

- Asignatura: PBY2202
- Evaluación: EFT semana 9, forma C
- Proyecto: Minimarket Plus
- Integrantes: `[COMPLETAR]`
- Sección: `[COMPLETAR]`
- Docente: `[COMPLETAR]`
- Fecha: `[COMPLETAR]`
- Repositorio público: <https://github.com/jparra100/MINIMARKET_PLUS_SEM8>

## 1. Introducción

Minimarket Plus es un backend REST para centralizar la operación de un minimarket con varias sucursales. La actualización EFT incorpora inventario por sucursal, órdenes de compra automáticas, promociones, pedidos en línea, reporte de rotación y seguridad basada en JWT y roles.

La aplicación utiliza Java 17, Spring Boot, Spring Data JPA, Spring Security, HATEOAS, springdoc-openapi y H2. Las operaciones se separan en controladores, servicios y repositorios, manteniendo la lógica de negocio fuera de la capa HTTP.

## 2. Funcionalidades implementadas

| Requisito forma C | Implementación | Evidencia sugerida |
|---|---|---|
| Inventario centralizado por sucursal | `Sucursal`, `StockSucursal` y movimientos de `Inventario` | Consultar `/api/stock` y `/api/stock/sucursal/{id}` |
| Stock en tiempo real | Cada entrada, salida, pedido o recepción modifica el stock de la sucursal | Mostrar stock antes y después de un movimiento |
| Orden automática al llegar al mínimo | `InventarioServiceImpl` crea una orden pendiente y evita duplicados | Dejar stock en el mínimo y consultar `/api/ordenes-compra` |
| Productos más y menos vendidos | Consulta agregada y `ReporteServiceImpl` | Ejecutar `/api/reportes/rotacion-productos` como ADMIN |
| Disponibilidad por sucursal | Búsqueda por producto en `StockSucursalRepository` | Ejecutar `/api/stock/producto/{productoId}` |
| Pedido con retiro o despacho | `CrearPedidoRequest`, `TipoEntrega` y `VentaServiceImpl` | Crear un pedido desde el carrito con ambas modalidades |
| Ofertas centralizadas | `PromocionServiceImpl` selecciona el mejor descuento vigente | Consultar `/api/promociones/precio/{productoId}` |
| Registro y autenticación | `/api/auth/register` y `/api/auth/login` | Registrar cliente y obtener JWT |
| Roles diferenciados | `ADMIN`, `CAJERO` y `CLIENTE` en `SecurityConfig` | Comparar respuestas 200, 403 y 401 |

La creación del pedido se ejecuta como una operación transaccional: valida el carrito, la sucursal y la modalidad de entrega; calcula el precio promocional; comprueba el stock; registra venta y detalles; descuenta inventario; y finalmente vacía el carrito.

## 3. Configuración de seguridad

La API usa un esquema sin sesión. El flujo es:

1. El usuario se registra o inicia sesión.
2. `AuthService` valida la contraseña cifrada con BCrypt.
3. `JwtUtil` genera un token firmado con una hora de duración.
4. `JwtAuthenticationFilter` lee `Authorization: Bearer <token>` en cada solicitud.
5. Spring Security carga el usuario y aplica los permisos de `SecurityConfig`.

Los archivos principales son:

- `security/config/SecurityConfig.java`: rutas públicas, autenticadas y restringidas por rol.
- `security/filter/JwtAuthenticationFilter.java`: validación del token por solicitud.
- `security/util/JwtUtil.java`: generación y validación del JWT.
- `security/service/AuthService.java`: registro, login y BCrypt.
- `config/OpenApiConfig.java`: esquema Bearer visible en Swagger.

### Protección aplicada

| Información u operación | Protección |
|---|---|
| Datos personales y usuarios | Sólo `ADMIN` administra `/api/usuarios/**` |
| Stock y movimientos | Consulta para personal autorizado; modificación sólo `ADMIN` |
| Precios y promociones | Consulta autenticada; modificación sólo `ADMIN` |
| Ventas y transacciones | Sólo `ADMIN` y `CAJERO` |
| Reporte de rotación | Sólo `ADMIN` |
| Carrito y pedidos | Usuario autenticado y asociado a su propio nombre de usuario |

Las cuentas de demostración sólo se crean al activar `DEMO_DATA_ENABLED=true`. La consola H2 también está desactivada por defecto. La clave JWT se puede externalizar mediante `JWT_SECRET`.

### Evidencias de seguridad

- `[INSERTAR CAPTURA]` Login exitoso y token JWT.
- `[INSERTAR CAPTURA]` Respuesta `401` al acceder sin token.
- `[INSERTAR CAPTURA]` Respuesta `403` de CLIENTE o CAJERO en un recurso de ADMIN.
- `[INSERTAR CAPTURA]` Acceso `200` del ADMIN al mismo recurso.

## 4. Pruebas unitarias y de integración

La suite se ejecuta con:

```powershell
.\mvnw.cmd clean verify
```

En la última verificación local se ejecutaron 33 pruebas, sin fallos ni errores. JaCoCo generó el reporte en `target/site/jacoco/index.html`, con 59,7 % de cobertura de líneas y 95,6 % de clases alcanzadas.

| Área comprobada | Casos principales |
|---|---|
| Seguridad | Login correcto/incorrecto, registro, duplicado, token inválido y permisos por rol |
| Inventario | Entrada, salida sin stock y orden al alcanzar el mínimo |
| Órdenes de compra | Recepción y bloqueo de recepción duplicada |
| Promociones | Mejor descuento vigente, precio normal y porcentaje inválido |
| Pedidos | Precio promocional, descuento de stock, dirección obligatoria y carrito vacío |
| Reportes | Mayor/menor rotación e inventario sin ventas |
| Persistencia | Consulta H2 que incluye productos vendidos y no vendidos |
| API | Esquema Bearer, rutas OpenAPI y enlaces HATEOAS |

### Evidencias de pruebas

- `[INSERTAR CAPTURA]` Resultado final de `mvnw.cmd clean verify` con 33 pruebas.
- `[INSERTAR CAPTURA]` Portada del reporte JaCoCo.
- `[INSERTAR CAPTURA]` Una clase de servicio con líneas cubiertas.

## 5. OpenAPI y HATEOAS

Swagger UI está disponible en `http://localhost:8080/swagger-ui/index.html`. La configuración declara el esquema `bearerAuth`, permitiendo iniciar sesión, copiar el JWT y autorizar las solicitudes protegidas desde la interfaz.

Los controladores están agrupados con etiquetas y describen sus operaciones. La prueba `OpenApiHateoasIntegrationTest` verifica que el contrato publique las rutas de autenticación, stock, órdenes, pedidos y reportes.

Las representaciones incorporan enlaces `_links`. Por ejemplo, una sucursal incluye enlaces hacia sí misma, el listado de sucursales y su stock; una orden incluye el proveedor y el stock asociado; una promoción enlaza el producto y el precio vigente.

- `[INSERTAR CAPTURA]` Swagger UI con los grupos de endpoints.
- `[INSERTAR CAPTURA]` Ventana Authorize con Bearer JWT.
- `[INSERTAR CAPTURA]` Respuesta de sucursal u orden mostrando `_links`.

## 6. Integración y flujo completo

Flujo recomendado para demostrar la integración:

1. Iniciar sesión como ADMIN.
2. Crear proveedor, categoría, producto y sucursal.
3. Registrar una entrada de inventario para asociar producto y sucursal.
4. Definir el stock mínimo.
5. Crear una promoción activa.
6. Iniciar sesión como CLIENTE, agregar el producto al carrito y crear el pedido.
7. Verificar el precio promocional y el descuento de stock.
8. Llegar al stock mínimo y comprobar la orden automática.
9. Recibir la orden y verificar el aumento del stock.
10. Volver como ADMIN y consultar el reporte de rotación.

Este recorrido conecta autenticación, autorización, catálogo, inventario, promociones, pedidos, órdenes de compra y reportes.

## 7. Mejoras incorporadas

- Reemplazo de la autenticación por sesión por JWT sin estado.
- Contraseñas almacenadas con BCrypt y ocultas en las respuestas JSON.
- Validación de solicitudes y manejo centralizado de errores.
- Actualización transaccional del stock para evitar pedidos incompletos.
- Separación de stock total y stock por sucursal.
- Prevención de órdenes de compra pendientes duplicadas.
- Datos de demostración y consola H2 desactivados por defecto.
- Contraseñas y clave JWT configurables mediante variables de entorno.
- Pruebas de negocio, seguridad, persistencia, OpenAPI y HATEOAS.

## 8. Conclusiones

La actualización cubre las funciones indicadas para la forma C y conecta las operaciones críticas del minimarket bajo un mismo esquema de seguridad. La separación entre controladores, servicios y repositorios facilita mantener la lógica, mientras que las pruebas automatizadas permiten repetir la validación antes de cada entrega.

OpenAPI facilita revisar y demostrar el contrato REST. HATEOAS agrega navegación entre recursos relacionados. JWT y los roles limitan el acceso a stock, precios, ventas, reportes, transacciones y datos personales según la responsabilidad del usuario.

## 9. Anexos

- Repositorio: <https://github.com/jparra100/MINIMARKET_PLUS_SEM8>
- Swagger UI: `http://localhost:8080/swagger-ui/index.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`
- Reporte JaCoCo: `target/site/jacoco/index.html`
- Video Kaltura: `[PEGAR ENLACE AL FINALIZAR]`
