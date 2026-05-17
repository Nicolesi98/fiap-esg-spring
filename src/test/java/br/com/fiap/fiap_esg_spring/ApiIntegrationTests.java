package br.com.fiap.fiap_esg_spring;

import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ApiIntegrationTests {

    private static final String USERNAME = "admin";
    private static final String PASSWORD = "admin123";

    @Autowired
    private MockMvc mockMvc;

    @Test
    void deveExigirAutenticacaoNosEndpointsDaApi() throws Exception {
        mockMvc.perform(get("/empresas"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void devePermitirSwaggerSemAutenticacaoEDeclararBasicAuth() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.components.securitySchemes.basicAuth.type").value("http"))
                .andExpect(jsonPath("$.components.securitySchemes.basicAuth.scheme").value("basic"))
                .andExpect(jsonPath("$.security[0].basicAuth").exists());
    }

    @Test
    void deveListarDadosIniciaisComBasicAuth() throws Exception {
        mockMvc.perform(get("/empresas").with(httpBasic(USERNAME, PASSWORD)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].nome").value("EcoTech Solucoes"));
    }

    @Test
    void deveCriarAtualizarEExcluirEmpresa() throws Exception {
        String createResponse = mockMvc.perform(post("/empresas")
                        .with(httpBasic(USERNAME, PASSWORD))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nome": "Empresa Teste",
                                  "cnpj": "44.444.444/0001-44"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", endsWith("/empresas/4")))
                .andExpect(jsonPath("$.nome").value("Empresa Teste"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long id = JsonTestUtils.idFrom(createResponse);

        mockMvc.perform(put("/empresas/{id}", id)
                        .with(httpBasic(USERNAME, PASSWORD))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nome": "Empresa Atualizada",
                                  "cnpj": "55.555.555/0001-55"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Empresa Atualizada"));

        mockMvc.perform(delete("/empresas/{id}", id).with(httpBasic(USERNAME, PASSWORD)))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/empresas/{id}", id).with(httpBasic(USERNAME, PASSWORD)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Empresa nao encontrada: " + id));
    }

    @Test
    void deveValidarPayloadInvalido() throws Exception {
        mockMvc.perform(post("/empresas")
                        .with(httpBasic(USERNAME, PASSWORD))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "cnpj": "1234567890123456789"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Dados invalidos."))
                .andExpect(jsonPath("$.fields.nome").exists())
                .andExpect(jsonPath("$.fields.cnpj").exists());
    }

    @Test
    void deveFiltrarEquipamentosConsumosEAlertas() throws Exception {
        mockMvc.perform(get("/equipamentos?empresaId=1").with(httpBasic(USERNAME, PASSWORD)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].nome").value("Ar Condicionado"));

        mockMvc.perform(get("/consumos-energia?equipamentoId=2").with(httpBasic(USERNAME, PASSWORD)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].tempoOciosoMin").value(25));

        mockMvc.perform(get("/alertas?status=ABERTO").with(httpBasic(USERNAME, PASSWORD)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].tipoAlerta").value("CONSUMO_ALTO"));
    }
}
