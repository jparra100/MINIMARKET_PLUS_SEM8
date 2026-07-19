# Guía de evidencias y video EFT

Esta guía organiza únicamente las evidencias solicitadas en la pauta. No reemplaza el informe institucional ni el video de Kaltura.

## Preparación de la demostración

Abrir PowerShell en la carpeta del proyecto:

```powershell
$env:DEMO_DATA_ENABLED="true"
.\mvnw.cmd spring-boot:run
```

Abrir Swagger UI:

```text
http://localhost:8080/swagger-ui/index.html
```

En otra consola, dejar preparado:

```powershell
.\mvnw.cmd clean verify
start target\site\jacoco\index.html
```

Antes de grabar, comprobar que el repositorio sea público y que `main` contenga los últimos commits.

## Capturas para el informe

Tomar capturas legibles, sin recortar el código de respuesta ni la ruta ejecutada.

- [ ] Repositorio público con historial de commits.
- [ ] Swagger UI mostrando los grupos de endpoints.
- [ ] Login correcto y token JWT.
- [ ] Botón Authorize con el esquema Bearer.
- [ ] Acceso sin token con respuesta `401`.
- [ ] Acceso con rol insuficiente con respuesta `403`.
- [ ] Stock del mismo producto en distintas sucursales.
- [ ] Movimiento de inventario y cambio de cantidad.
- [ ] Orden de compra creada al llegar al stock mínimo.
- [ ] Recepción de la orden y stock actualizado.
- [ ] Promoción activa y precio calculado.
- [ ] Pedido con retiro o despacho y stock descontado.
- [ ] Reporte de productos más y menos vendidos.
- [ ] Respuesta con enlaces `_links` de HATEOAS.
- [ ] Resultado de 33 pruebas sin fallos.
- [ ] Resumen del reporte JaCoCo.

Si se agregan pruebas después de esta versión, actualizar en el informe el número de pruebas y los porcentajes con los valores reales del último `clean verify`.

## Guion de video Kaltura — 8 a 9 minutos

La pauta exige entre 7 y 10 minutos y participación de todos los integrantes.

### 0:00–0:40 — Presentación

- Presentar a todos los integrantes.
- Indicar que el proyecto corresponde a Minimarket Plus, EFT forma C.
- Resumir el objetivo: operación centralizada de varias sucursales con seguridad JWT.

### 0:40–1:30 — Estructura y repositorio

- Mostrar el repositorio público y el historial de commits.
- Explicar brevemente las capas `controller`, `service`, `repository`, `entity` y `security`.
- Mencionar Java 17, Spring Boot, H2, OpenAPI y HATEOAS.

### 1:30–2:40 — Seguridad

- Abrir Swagger y ejecutar `/api/auth/login`.
- Autorizar con el JWT.
- Mostrar un `401` sin token y un `403` con rol insuficiente.
- Explicar `ADMIN`, `CAJERO` y `CLIENTE`, BCrypt y expiración del token.

### 2:40–4:25 — Inventario y órdenes

- Mostrar sucursales y disponibilidad del producto por sucursal.
- Registrar un movimiento de inventario.
- Llevar el stock al mínimo y consultar la orden automática.
- Recibir la orden y comprobar que el stock aumenta.

### 4:25–5:50 — Promociones y pedidos

- Mostrar una promoción vigente y el precio con descuento.
- Como CLIENTE, agregar un producto al carrito.
- Crear pedido para retiro o despacho.
- Mostrar total, detalles y descuento de stock.

### 5:50–6:35 — Reporte de rotación

- Como ADMIN, ejecutar `/api/reportes/rotacion-productos`.
- Identificar el producto más vendido y el menos vendido.
- Mostrar que un rol sin permiso obtiene `403`.

### 6:35–7:20 — OpenAPI y HATEOAS

- Recorrer los grupos documentados en Swagger.
- Mostrar el esquema Bearer y una respuesta con `_links`.
- Explicar cómo un enlace lleva a stock, proveedor, producto u otra operación relacionada.

### 7:20–8:15 — Pruebas

- Ejecutar o mostrar el resultado de `mvnw.cmd clean verify`.
- Indicar 33 pruebas sin fallos.
- Abrir JaCoCo y mostrar el resumen de cobertura.
- Nombrar pruebas de seguridad, inventario, órdenes, promociones, pedidos y reportes.

### 8:15–8:50 — Conclusiones

- Resumir que las funciones de la forma C están integradas.
- Destacar la protección por roles y la actualización transaccional del stock.
- Indicar el enlace del repositorio y cerrar con todos los integrantes.

## Reparto entre integrantes

Completar antes de grabar para asegurar participación visible:

| Integrante | Parte del video |
|---|---|
| `[NOMBRE 1]` | Presentación, estructura y repositorio |
| `[NOMBRE 2]` | Seguridad, inventario y órdenes |
| `[NOMBRE 3]` | Promociones, pedidos, pruebas y conclusiones |

Ajustar las filas al número real de integrantes, manteniendo intervención de todos.

## Revisión final de entrega

- [ ] Informe usando el formato institucional de AVA.
- [ ] Nombres, sección, docente y fecha completos.
- [ ] Capturas reales insertadas y explicadas.
- [ ] Informe exportado a PDF.
- [ ] Video dura entre 7 y 10 minutos.
- [ ] Todos los integrantes participan en el video.
- [ ] Video cargado en Kaltura.
- [ ] Enlace Kaltura agregado al informe o entrega según las instrucciones del curso.
- [ ] Repositorio GitHub público y actualizado.
- [ ] `mvnw.cmd clean verify` termina sin fallos.
