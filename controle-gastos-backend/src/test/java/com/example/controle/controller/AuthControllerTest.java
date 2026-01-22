package com.example.controle.controller;

import com.example.controle.model.dto.AuthResponseDTO;
import com.example.controle.model.dto.LoginRequestDTO;
import com.example.controle.model.dto.RegisterRequestDTO;
import com.example.controle.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    @Test
    void deveRegistrarUsuarioComSucesso() throws Exception {
        RegisterRequestDTO request = new RegisterRequestDTO();
        request.setNome("João Silva");
        request.setEmail("joao@email.com");
        request.setSenha("senha123");

        AuthResponseDTO response = new AuthResponseDTO();
        response.setToken("token-jwt-teste");
        response.setEmail("joao@email.com");
        response.setNome("João Silva");

        when(authService.register(any(RegisterRequestDTO.class))).thenReturn(response);

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").value("token-jwt-teste"))
                .andExpect(jsonPath("$.email").value("joao@email.com"))
                .andExpect(jsonPath("$.nome").value("João Silva"));
    }

    @Test
    void deveFazerLoginComSucesso() throws Exception {
        LoginRequestDTO request = new LoginRequestDTO();
        request.setEmail("joao@email.com");
        request.setSenha("senha123");

        AuthResponseDTO response = new AuthResponseDTO();
        response.setToken("token-jwt-teste");
        response.setEmail("joao@email.com");
        response.setNome("João Silva");

        when(authService.login(any(LoginRequestDTO.class))).thenReturn(response);

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.email").value("joao@email.com"));
    }

    @Test
    void deveRetornarErroQuandoEmailInvalido() throws Exception {
        RegisterRequestDTO request = new RegisterRequestDTO();
        request.setNome("João Silva");
        request.setEmail("email-invalido");
        request.setSenha("senha123");

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deveRetornarErroQuandoSenhaCurta() throws Exception {
        RegisterRequestDTO request = new RegisterRequestDTO();
        request.setNome("João Silva");
        request.setEmail("joao@email.com");
        request.setSenha("123");

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
