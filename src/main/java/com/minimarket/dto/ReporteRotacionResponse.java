package com.minimarket.dto;

import java.util.List;

public record ReporteRotacionResponse(
        RotacionProductoResponse masVendido,
        RotacionProductoResponse menosVendido,
        List<RotacionProductoResponse> productos) {
}
