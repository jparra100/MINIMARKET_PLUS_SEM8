package com.minimarket.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = "app.demo-data.enabled=true")
@AutoConfigureMockMvc
class OpenApiHateoasIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void publicaDocumentacionConJwtYRutasPrincipales() throws Exception {
        String response = mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        JsonNode api = objectMapper.readTree(response);
        JsonNode bearer = api.path("components").path("securitySchemes").path("bearerAuth");
        JsonNode paths = api.path("paths");

        assertEquals("http", bearer.path("type").asText());
        assertEquals("bearer", bearer.path("scheme").asText());
        assertTrue(paths.has("/api/auth/login"));
        assertTrue(paths.has("/api/stock/producto/{productoId}"));
        assertTrue(paths.has("/api/ordenes-compra/{id}/recibir"));
        assertTrue(paths.has("/api/pedidos"));
        assertTrue(paths.has("/api/reportes/rotacion-productos"));
    }

    @Test
    void sucursalIncluyeEnlacesDeNavegacion() throws Exception {
        String token = loginAdministrador();

        mockMvc.perform(post("/api/sucursales")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Sucursal HATEOAS\",\"direccion\":\"Calle de prueba 123\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$._links.self.href").exists())
                .andExpect(jsonPath("$._links.sucursales.href").exists())
                .andExpect(jsonPath("$._links.stock.href").exists());
    }

    private String loginAdministrador() throws Exception {
        String response = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"admin\",\"password\":\"Admin123!\"}"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(response).get("token").asText();
    }
}
