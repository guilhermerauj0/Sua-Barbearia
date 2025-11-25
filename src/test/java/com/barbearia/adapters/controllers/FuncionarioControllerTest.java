package com.barbearia.adapters.controllers;

import com.barbearia.application.dto.FuncionarioResponseDto;
import com.barbearia.application.services.FuncionarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class FuncionarioControllerTest {

    @Mock
    private FuncionarioService funcionarioService;

    @InjectMocks
    private FuncionarioController funcionarioController;

    private FuncionarioResponseDto funcionarioDto;

    @BeforeEach
    void setUp() {
        funcionarioDto = new FuncionarioResponseDto(
                1L,
                1L,
                "João",
                "joao@email.com",
                "123456789",
                null,
                "Barbeiro",
                "Corte",
                true,
                null,
                null);
    }

    @Test
    @DisplayName("GET /barbearia/{id} - Deve listar funcionários com sucesso")
    void deveListarFuncionariosPorBarbearia() {
        // Arrange
        when(funcionarioService.listarFuncionariosDaBarbearia(anyLong()))
                .thenReturn(List.of(funcionarioDto));

        // Act
        ResponseEntity<List<FuncionarioResponseDto>> response = funcionarioController.listarPorBarbearia(1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<FuncionarioResponseDto> body = response.getBody();
        assertNotNull(body);
        assertFalse(body.isEmpty());
        assertEquals(1, body.size());
    }

    @Test
    @DisplayName("GET /servico/{id} - Deve listar profissionais por serviço com sucesso")
    void deveListarProfissionaisPorServico() {
        // Arrange
        when(funcionarioService.listarProfissionaisPorServicoEBarbearia(anyLong(), anyLong()))
                .thenReturn(List.of(funcionarioDto));

        // Act
        ResponseEntity<List<FuncionarioResponseDto>> response = funcionarioController.listarPorServico(1L, 1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<FuncionarioResponseDto> body = response.getBody();
        assertNotNull(body);
        assertFalse(body.isEmpty());
        assertEquals(1, body.size());
    }
}
