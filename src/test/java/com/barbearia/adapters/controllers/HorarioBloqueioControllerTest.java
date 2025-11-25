package com.barbearia.adapters.controllers;

import com.barbearia.application.dto.HorarioBloqueadoRequestDto;
import com.barbearia.application.dto.HorarioBloqueadoResponseDto;
import com.barbearia.application.security.JwtService;
import com.barbearia.application.services.HorarioBloqueioService;
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

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("HorarioBloqueioController - Testes")
class HorarioBloqueioControllerTest {

    @Mock
    private HorarioBloqueioService horarioBloqueioService;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private HorarioBloqueioController controller;

    private HttpServletRequest mockRequest;

    @BeforeEach
    void setUp() {
        mockRequest = mock(HttpServletRequest.class);
    }

    @Test
    @DisplayName("POST /bloqueios - Deve criar bloqueio com sucesso")
    void deveCriarBloqueioComSucesso() {
        // Arrange
        HorarioBloqueadoRequestDto requestDto = new HorarioBloqueadoRequestDto();
        requestDto.setData(LocalDate.now());
        requestDto.setHorarioInicio(LocalTime.of(14, 0));
        requestDto.setHorarioFim(LocalTime.of(15, 0));

        HorarioBloqueadoResponseDto responseDto = new HorarioBloqueadoResponseDto();
        responseDto.setId(1L);

        when(mockRequest.getHeader("Authorization")).thenReturn("Bearer token");
        when(jwtService.extractClaim(anyString(), eq("userId"))).thenReturn(1L);
        when(horarioBloqueioService.bloquearHorario(anyLong(), anyLong(), any()))
                .thenReturn(responseDto);

        // Act
        ResponseEntity<?> response = controller.criarBloqueio(1L, 1L, requestDto, mockRequest);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    @DisplayName("POST /bloqueios - Deve retornar 401 se token inválido")
    void deveRetornar401SeTokenInvalido() {
        // Arrange
        when(mockRequest.getHeader("Authorization")).thenReturn(null);

        // Act
        ResponseEntity<?> response = controller.criarBloqueio(1L, 1L, new HorarioBloqueadoRequestDto(), mockRequest);

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    @DisplayName("GET /bloqueios - Deve listar bloqueios com sucesso")
    void deveListarBloqueiosComSucesso() {
        // Arrange
        when(horarioBloqueioService.listarBloqueios(anyLong(), anyLong()))
                .thenReturn(Collections.emptyList());

        // Act
        ResponseEntity<List<HorarioBloqueadoResponseDto>> response = controller.listarBloqueios(1L, 1L, null, null);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<HorarioBloqueadoResponseDto> body = response.getBody();
        assertNotNull(body);
        assertTrue(body.isEmpty());
    }

    @Test
    @DisplayName("DELETE /bloqueios/{id} - Deve remover bloqueio com sucesso")
    void deveRemoverBloqueioComSucesso() {
        // Act
        ResponseEntity<?> response = controller.removerBloqueio(1L, 1L, 10L);

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(horarioBloqueioService).removerBloqueio(1L, 10L);
    }
}
