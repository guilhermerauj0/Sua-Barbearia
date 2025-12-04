package com.barbearia.adapters.controllers;

import com.barbearia.application.dto.FuncionarioLinkRequestDto;
import com.barbearia.application.dto.FuncionarioLinkResponseDto;
import com.barbearia.application.security.JwtService;
import com.barbearia.application.services.ProfissionalLinkService;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProfissionalLinkController - Testes")
class ProfissionalLinkControllerTest {

    @Mock
    private ProfissionalLinkService profissionalLinkService;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private ProfissionalLinkController controller;

    private HttpServletRequest mockRequest;

    @BeforeEach
    void setUp() {
        mockRequest = mock(HttpServletRequest.class);
    }

    @Test
    @DisplayName("POST /link-acesso - Deve gerar link com sucesso")
    void deveGerarLinkAcesso() {
        // Arrange
        FuncionarioLinkResponseDto responseDto = new FuncionarioLinkResponseDto(
                1L, "João", "link", true, null, null);

        when(mockRequest.getHeader("Authorization")).thenReturn("Bearer token");
        when(jwtService.extractClaim(anyString(), eq("userId"))).thenReturn(1L);
        when(profissionalLinkService.gerarLinkAcesso(anyLong(), anyLong(), any()))
                .thenReturn(responseDto);

        // Act
        ResponseEntity<?> response = controller.gerarLinkAcesso(1L, new FuncionarioLinkRequestDto(), mockRequest);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    @DisplayName("GET /link-acesso - Deve consultar status com sucesso")
    void deveConsultarStatusLink() {
        // Arrange
        FuncionarioLinkResponseDto responseDto = new FuncionarioLinkResponseDto(
                1L, "João", "link", true, null, null);

        when(mockRequest.getHeader("Authorization")).thenReturn("Bearer token");
        when(jwtService.extractClaim(anyString(), eq("userId"))).thenReturn(1L);
        when(profissionalLinkService.consultarStatusLink(anyLong(), anyLong()))
                .thenReturn(responseDto);

        // Act
        ResponseEntity<?> response = controller.consultarStatusLink(1L, mockRequest);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @DisplayName("PATCH /link-acesso/desativar - Deve desativar link com sucesso")
    void deveDesativarLink() {
        // Arrange
        FuncionarioLinkResponseDto responseDto = new FuncionarioLinkResponseDto(
                1L, "João", "link", false, null, null);

        when(mockRequest.getHeader("Authorization")).thenReturn("Bearer token");
        when(jwtService.extractClaim(anyString(), eq("userId"))).thenReturn(1L);
        when(profissionalLinkService.consultarStatusLink(anyLong(), anyLong()))
                .thenReturn(responseDto);

        // Act
        ResponseEntity<?> response = controller.desativarLink(1L, mockRequest);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(profissionalLinkService).desativarLink(1L, 1L);
    }
}
