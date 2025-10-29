package com.barbearia.application.services;

import com.barbearia.application.dto.ClienteRequestDto;
import com.barbearia.application.dto.ClienteResponseDto;
import com.barbearia.infrastructure.persistence.entities.JpaCliente;
import com.barbearia.infrastructure.persistence.repositories.ClienteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Testes unitários para ClienteService usando Mockito.
 * Testa a lógica de negócio isoladamente, sem dependências externas.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ClienteService - Testes Unitários")
class ClienteServiceTest {

    @Mock
    private ClienteRepository clienteRepository;

    @InjectMocks
    private ClienteService clienteService;

    private ClienteRequestDto clienteRequestDto;

    @BeforeEach
    void setUp() {
        clienteRequestDto = new ClienteRequestDto(
                "João Silva",
                "joao@email.com",
                "Senha@123",
                "Senha@123",
                "(11) 98765-4321"
        );
    }

    @Test
    @DisplayName("Deve registrar cliente com sucesso")
    void deveRegistrarClienteComSucesso() {
        // Arrange
        when(clienteRepository.existsByEmail(anyString())).thenReturn(false);
        
        JpaCliente jpaClienteSalvo = new JpaCliente();
        jpaClienteSalvo.setId(1L);
        jpaClienteSalvo.setNome("João Silva");
        jpaClienteSalvo.setEmail("joao@email.com");
        jpaClienteSalvo.setSenha("$2a$10$hashedPassword");
        jpaClienteSalvo.setTelefone("11987654321");
        jpaClienteSalvo.setAtivo(true);
        
        when(clienteRepository.save(any(JpaCliente.class))).thenReturn(jpaClienteSalvo);

        // Act
        ClienteResponseDto response = clienteService.registrarCliente(clienteRequestDto);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getNome()).isEqualTo("João Silva");
        assertThat(response.getEmail()).isEqualTo("joao@email.com");
        assertThat(response.getTelefone()).isEqualTo("11987654321");
        assertThat(response.isAtivo()).isTrue();

        verify(clienteRepository).existsByEmail("joao@email.com");
        verify(clienteRepository).save(any(JpaCliente.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando senhas não conferem")
    void deveLancarExcecaoQuandoSenhasNaoConferem() {
        // Arrange
        ClienteRequestDto dtoComSenhasDiferentes = new ClienteRequestDto(
                "João Silva",
                "joao@email.com",
                "Senha@123",
                "Senha@456",
                "(11) 98765-4321"
        );

        // Act & Assert
        assertThatThrownBy(() -> clienteService.registrarCliente(dtoComSenhasDiferentes))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("não coincidem");

        verify(clienteRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando email já está cadastrado")
    void deveLancarExcecaoQuandoEmailJaCadastrado() {
        // Arrange
        when(clienteRepository.existsByEmail(anyString())).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> clienteService.registrarCliente(clienteRequestDto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("já cadastrado");

        verify(clienteRepository).existsByEmail("joao@email.com");
        verify(clienteRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve normalizar email para lowercase")
    void deveNormalizarEmailParaLowercase() {
        // Arrange
        ClienteRequestDto dtoComEmailMaiusculo = new ClienteRequestDto(
                "João Silva",
                "JOAO@EMAIL.COM",
                "Senha@123",
                "Senha@123",
                "(11) 98765-4321"
        );

        when(clienteRepository.existsByEmail(anyString())).thenReturn(false);
        when(clienteRepository.save(any(JpaCliente.class))).thenAnswer(invocation -> {
            JpaCliente cliente = invocation.getArgument(0);
            cliente.setId(1L);
            return cliente;
        });

        // Act
        ClienteResponseDto response = clienteService.registrarCliente(dtoComEmailMaiusculo);

        // Assert
        assertThat(response.getEmail()).isEqualTo("joao@email.com");
        verify(clienteRepository).existsByEmail("joao@email.com");
    }

    @Test
    @DisplayName("Deve remover formatação do telefone")
    void deveRemoverFormatacaoDoTelefone() {
        // Arrange
        when(clienteRepository.existsByEmail(anyString())).thenReturn(false);
        when(clienteRepository.save(any(JpaCliente.class))).thenAnswer(invocation -> {
            JpaCliente cliente = invocation.getArgument(0);
            cliente.setId(1L);
            return cliente;
        });

        // Act
        ClienteResponseDto response = clienteService.registrarCliente(clienteRequestDto);

        // Assert
        assertThat(response.getTelefone()).isEqualTo("11987654321");
        assertThat(response.getTelefone()).doesNotContain("(", ")", " ", "-");
    }

    @Test
    @DisplayName("Deve verificar se senha é criptografada")
    void deveVerificarSeCriptografaSenha() {
        // Arrange
        when(clienteRepository.existsByEmail(anyString())).thenReturn(false);
        when(clienteRepository.save(any(JpaCliente.class))).thenAnswer(invocation -> {
            JpaCliente cliente = invocation.getArgument(0);
            cliente.setId(1L);
            return cliente;
        });

        // Act
        clienteService.registrarCliente(clienteRequestDto);

        // Assert
        verify(clienteRepository).save(argThat(cliente -> {
            // Verifica se a senha foi hasheada (não é a senha original)
            return !cliente.getSenha().equals("Senha@123") && 
                   cliente.getSenha().length() > 20; // BCrypt gera hash longo
        }));
    }

    @Test
    @DisplayName("Deve criar cliente com status ativo por padrão")
    void deveCriarClienteComStatusAtivo() {
        // Arrange
        when(clienteRepository.existsByEmail(anyString())).thenReturn(false);
        when(clienteRepository.save(any(JpaCliente.class))).thenAnswer(invocation -> {
            JpaCliente cliente = invocation.getArgument(0);
            cliente.setId(1L);
            return cliente;
        });

        // Act
        ClienteResponseDto response = clienteService.registrarCliente(clienteRequestDto);

        // Assert
        assertThat(response.isAtivo()).isTrue();
        verify(clienteRepository).save(argThat(JpaCliente::isAtivo));
    }

    @Test
    @DisplayName("Deve buscar cliente por email com sucesso")
    void deveBuscarClientePorEmail() {
        // Arrange
        JpaCliente jpaCliente = new JpaCliente();
        jpaCliente.setId(1L);
        jpaCliente.setEmail("joao@email.com");
        jpaCliente.setNome("João Silva");
        
        when(clienteRepository.findByEmail(anyString())).thenReturn(java.util.Optional.of(jpaCliente));

        // Act
        ClienteResponseDto response = clienteService.buscarPorEmail("joao@email.com");

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getEmail()).isEqualTo("joao@email.com");
    }

    @Test
    @DisplayName("Deve verificar se email já está cadastrado")
    void deveVerificarEmailJaCadastrado() {
        // Arrange
        when(clienteRepository.existsByEmail(anyString())).thenReturn(true);

        // Act
        boolean resultado = clienteService.emailJaCadastrado("joao@email.com");

        // Assert
        assertThat(resultado).isTrue();
        verify(clienteRepository).existsByEmail("joao@email.com");
    }
}
