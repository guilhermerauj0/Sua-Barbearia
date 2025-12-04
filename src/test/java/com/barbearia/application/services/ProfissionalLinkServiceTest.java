package com.barbearia.application.services;

import com.barbearia.application.dto.FuncionarioLinkResponseDto;
import com.barbearia.infrastructure.persistence.entities.JpaFuncionario;
import com.barbearia.infrastructure.persistence.repositories.FuncionarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@SuppressWarnings("null")
@ExtendWith(MockitoExtension.class)
@DisplayName("ProfissionalLinkService - Testes")
class ProfissionalLinkServiceTest {

    @Mock
    private FuncionarioRepository funcionarioRepository;

    @InjectMocks
    private ProfissionalLinkService service;

    private JpaFuncionario funcionario;

    @BeforeEach
    void setUp() {
        funcionario = new JpaFuncionario();
        funcionario.setId(1L);
        funcionario.setNome("João");
        funcionario.setBarbeariaId(1L);
    }

    @Test
    @DisplayName("Deve gerar link de acesso com sucesso")
    void deveGerarLinkAcesso() {
        // Arrange
        when(funcionarioRepository.findById(1L)).thenReturn(Optional.of(funcionario));
        when(funcionarioRepository.save(any(JpaFuncionario.class))).thenReturn(funcionario);

        // Act
        FuncionarioLinkResponseDto response = service.gerarLinkAcesso(1L, 1L, null);

        // Assert
        assertNotNull(response);
        assertNotNull(funcionario.getAccessToken());
        assertTrue(funcionario.getTokenAtivo());
        assertTrue(response.getLinkAcesso().contains(funcionario.getAccessToken()));
    }

    @Test
    @DisplayName("Deve validar token com sucesso")
    void deveValidarToken() {
        // Arrange
        String token = "valid-token";
        when(funcionarioRepository.findByTokenValidoComExpiracao(eq(token), any(LocalDateTime.class)))
                .thenReturn(Optional.of(funcionario));

        // Act
        JpaFuncionario resultado = service.validarToken(token);

        // Assert
        assertNotNull(resultado);
        assertEquals(funcionario.getId(), resultado.getId());
    }

    @Test
    @DisplayName("Deve lançar erro se token inválido")
    void deveLancarErroSeTokenInvalido() {
        // Arrange
        String token = "invalid-token";
        when(funcionarioRepository.findByTokenValidoComExpiracao(eq(token), any(LocalDateTime.class)))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> service.validarToken(token));
    }

    @Test
    @DisplayName("Deve desativar link com sucesso")
    void deveDesativarLink() {
        // Arrange
        when(funcionarioRepository.findById(1L)).thenReturn(Optional.of(funcionario));

        // Act
        service.desativarLink(1L, 1L);

        // Assert
        assertFalse(funcionario.getTokenAtivo());
        verify(funcionarioRepository).save(funcionario);
    }
}
