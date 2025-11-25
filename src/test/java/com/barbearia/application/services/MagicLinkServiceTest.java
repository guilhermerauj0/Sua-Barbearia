package com.barbearia.application.services;

import com.barbearia.application.security.JwtService;
import com.barbearia.infrastructure.persistence.entities.JpaFuncionario;
import com.barbearia.infrastructure.persistence.repositories.FuncionarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SuppressWarnings("null")
@ExtendWith(MockitoExtension.class)
@DisplayName("MagicLinkService - Testes")
class MagicLinkServiceTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private FuncionarioRepository funcionarioRepository;

    @InjectMocks
    private MagicLinkService magicLinkService;

    private JpaFuncionario funcionario;

    @BeforeEach
    void setUp() {
        // Injeta valor da propriedade
        ReflectionTestUtils.setField(magicLinkService, "frontendUrl", "http://localhost:3000");

        funcionario = new JpaFuncionario();
        funcionario.setId(1L);
        funcionario.setNome("João");
        funcionario.setEmail("joao@teste.com");
        funcionario.setBarbeariaId(1L);
    }

    @Test
    @DisplayName("Deve gerar link de acesso com sucesso")
    void deveGerarLinkAcessoComSucesso() {
        // Arrange
        when(funcionarioRepository.findById(1L)).thenReturn(Optional.of(funcionario));
        when(jwtService.generateToken(anyMap(), anyString(), anyLong())).thenReturn("mock-token");

        // Act
        String link = magicLinkService.gerarLinkAcesso(1L, 1L);

        // Assert
        assertNotNull(link);
        assertEquals("http://localhost:3000/acesso-profissional?token=mock-token", link);
    }

    @Test
    @DisplayName("Deve lançar erro se funcionário não encontrado")
    void deveLancarErroSeFuncionarioNaoEncontrado() {
        // Arrange
        when(funcionarioRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> magicLinkService.gerarLinkAcesso(1L, 1L));
    }

    @Test
    @DisplayName("Deve lançar erro se funcionário não pertence à barbearia")
    void deveLancarErroSeFuncionarioNaoPertenceBarbearia() {
        // Arrange
        funcionario.setBarbeariaId(2L); // Outra barbearia
        when(funcionarioRepository.findById(1L)).thenReturn(Optional.of(funcionario));

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> magicLinkService.gerarLinkAcesso(1L, 1L));
    }
}
