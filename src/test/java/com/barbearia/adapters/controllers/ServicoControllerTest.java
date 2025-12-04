package com.barbearia.adapters.controllers;

import com.barbearia.application.dto.ServicoDto;
import com.barbearia.application.dto.ServicoRequestDto;
import com.barbearia.application.security.JwtService;
import com.barbearia.application.services.BarbeariaService;
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
@DisplayName("ServicoController - Testes")
class ServicoControllerTest {

    @Mock
    private BarbeariaService barbeariaService;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private ServicoController controller;

    private HttpServletRequest mockRequest;

    @BeforeEach
    void setUp() {
        mockRequest = mock(HttpServletRequest.class);
    }

    @Test
    @DisplayName("POST /servicos - Deve criar serviço com sucesso")
    void deveCriarServico() {
        // Arrange
        ServicoDto responseDto = new ServicoDto();
        responseDto.setId(1L);
        responseDto.setNome("Corte");

        when(mockRequest.getHeader("Authorization")).thenReturn("Bearer token");
        when(jwtService.extractClaim(anyString(), eq("userId"))).thenReturn(1L);
        when(barbeariaService.criarServico(anyLong(), any())).thenReturn(responseDto);

        // Act
        ResponseEntity<?> response = controller.criarServico(new ServicoRequestDto(), mockRequest);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    @DisplayName("PUT /servicos/{id} - Deve editar serviço com sucesso")
    void deveEditarServico() {
        // Arrange
        ServicoDto responseDto = new ServicoDto();
        responseDto.setId(1L);
        responseDto.setNome("Corte Atualizado");

        when(mockRequest.getHeader("Authorization")).thenReturn("Bearer token");
        when(jwtService.extractClaim(anyString(), eq("userId"))).thenReturn(1L);
        when(barbeariaService.editarServico(anyLong(), anyLong(), any())).thenReturn(responseDto);

        // Act
        ResponseEntity<?> response = controller.editarServico(1L, new ServicoRequestDto(), mockRequest);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    @DisplayName("DELETE /servicos/{id} - Deve desativar serviço com sucesso")
    void deveDesativarServico() {
        // Arrange
        when(mockRequest.getHeader("Authorization")).thenReturn("Bearer token");
        when(jwtService.extractClaim(anyString(), eq("userId"))).thenReturn(1L);
        when(barbeariaService.desativarServico(anyLong(), anyLong())).thenReturn("Serviço desativado");

        // Act
        ResponseEntity<?> response = controller.desativarServico(1L, mockRequest);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Serviço desativado", response.getBody());
    }
}
