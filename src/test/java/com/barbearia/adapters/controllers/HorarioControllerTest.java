package com.barbearia.adapters.controllers;

import com.barbearia.application.dto.HorarioFuncionamentoRequestDto;
import com.barbearia.application.dto.HorarioFuncionamentoResponseDto;
import com.barbearia.application.security.JwtService;
import com.barbearia.application.services.HorarioGestaoService;
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

import java.time.LocalTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("HorarioController - Testes")
class HorarioControllerTest {

    @Mock
    private HorarioGestaoService horarioGestaoService;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private HorarioController controller;

    private HttpServletRequest mockRequest;

    @BeforeEach
    void setUp() {
        mockRequest = mock(HttpServletRequest.class);
    }

    @Test
    @DisplayName("GET /funcionario/{id} - Deve listar horários com sucesso")
    void deveListarHorariosFuncionario() {
        // Arrange
        when(horarioGestaoService.listarHorariosFuncionario(anyLong()))
                .thenReturn(Collections.emptyList());

        // Act
        ResponseEntity<List<HorarioFuncionamentoResponseDto>> response = controller.listarHorariosFuncionario(1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<HorarioFuncionamentoResponseDto> body = response.getBody();
        assertNotNull(body);
        assertTrue(body.isEmpty());
    }

    @Test
    @DisplayName("POST /funcionario/{id} - Deve salvar horário com sucesso")
    void deveSalvarHorarioFuncionario() {
        // Arrange
        HorarioFuncionamentoRequestDto requestDto = new HorarioFuncionamentoRequestDto();
        requestDto.setDiaSemana(1);
        requestDto.setHoraAbertura(LocalTime.of(9, 0));
        requestDto.setHoraFechamento(LocalTime.of(18, 0));

        HorarioFuncionamentoResponseDto responseDto = new HorarioFuncionamentoResponseDto();
        responseDto.setId(1L);

        when(mockRequest.getHeader("Authorization")).thenReturn("Bearer token");
        when(jwtService.extractClaim(anyString(), eq("userId"))).thenReturn(1L);
        when(horarioGestaoService.salvarHorarioFuncionario(anyLong(), anyLong(), any()))
                .thenReturn(responseDto);

        // Act
        ResponseEntity<?> response = controller.salvarHorarioFuncionario(1L, requestDto, mockRequest);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    @DisplayName("POST /funcionario/{id} - Deve retornar 401 se token inválido")
    void deveRetornar401SeTokenInvalido() {
        // Arrange
        when(mockRequest.getHeader("Authorization")).thenReturn(null);

        // Act
        ResponseEntity<?> response = controller.salvarHorarioFuncionario(1L, new HorarioFuncionamentoRequestDto(),
                mockRequest);

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }
}
