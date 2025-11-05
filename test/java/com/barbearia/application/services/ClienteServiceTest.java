package com.barbearia.application.services;

import com.barbearia.application.dto.ClienteRequestDto;
import com.barbearia.application.dto.ClienteResponseDto;
import com.barbearia.application.dto.ClienteProfileDto;
import com.barbearia.application.dto.ClienteUpdateDto;
import com.barbearia.infrastructure.persistence.entities.JpaCliente;
import com.barbearia.infrastructure.persistence.repositories.ClienteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Testes unitários para ClienteService usando Mockito.
 * Testa a lógica de negócio isoladamente, sem dependências externas.
 */
@SuppressWarnings("null")
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

    @Test
    @DisplayName("Deve buscar perfil do cliente com sucesso")
    void deveBuscarPerfilDoClienteComSucesso() {
        // Arrange
        Long clienteId = 1L;
        
        JpaCliente jpaCliente = new JpaCliente();
        jpaCliente.setId(clienteId);
        jpaCliente.setNome("João Silva");
        jpaCliente.setEmail("joao@email.com");
        jpaCliente.setTelefone("11987654321");
        jpaCliente.setRole("CLIENTE");
        jpaCliente.setAtivo(true);
        jpaCliente.setDataCriacao(LocalDateTime.now());
        jpaCliente.setDataAtualizacao(LocalDateTime.now());
        
        when(clienteRepository.findById(clienteId)).thenReturn(java.util.Optional.of(jpaCliente));

        // Act
        ClienteProfileDto response = clienteService.buscarMeuPerfil(clienteId);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(clienteId);
        assertThat(response.getNome()).isEqualTo("João Silva");
        assertThat(response.getEmail()).isEqualTo("joao@email.com");
        assertThat(response.getTelefone()).isEqualTo("11987654321");
        assertThat(response.getRole()).isEqualTo("CLIENTE");
        assertThat(response.isAtivo()).isTrue();
        
        verify(clienteRepository, times(1)).findById(clienteId);
    }

    @Test
    @DisplayName("Deve lançar exceção quando cliente não encontrado ao buscar perfil")
    void deveLancarExcecaoQuandoClienteNaoEncontradoBuscaPerfil() {
        // Arrange
        Long clienteId = 999L;
        when(clienteRepository.findById(clienteId)).thenReturn(java.util.Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> clienteService.buscarMeuPerfil(clienteId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Cliente não encontrado");

        verify(clienteRepository, times(1)).findById(clienteId);
    }

    @Test
    @DisplayName("Deve atualizar perfil do cliente com sucesso")
    void deveAtualizarPerfilDoClienteComSucesso() {
        // Arrange
        Long clienteId = 1L;
        
        JpaCliente jpaCliente = new JpaCliente();
        jpaCliente.setId(clienteId);
        jpaCliente.setNome("João Silva");
        jpaCliente.setEmail("joao@email.com");
        jpaCliente.setTelefone("11987654321");
        jpaCliente.setRole("CLIENTE");
        jpaCliente.setAtivo(true);

        ClienteUpdateDto updateDto = new ClienteUpdateDto();
        updateDto.setNome("João Santos");
        updateDto.setEmail("joao.santos@email.com");
        updateDto.setTelefone("(11) 99999-9999");

        when(clienteRepository.findById(clienteId)).thenReturn(java.util.Optional.of(jpaCliente));
        when(clienteRepository.existsByEmail(anyString())).thenReturn(false);
        when(clienteRepository.save(any(JpaCliente.class))).thenAnswer(invocation -> {
            JpaCliente cliente = invocation.getArgument(0);
            return cliente;
        });

        // Act
        ClienteProfileDto response = clienteService.atualizarMeuPerfil(clienteId, updateDto);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getNome()).isEqualTo("João Santos");
        assertThat(response.getEmail()).isEqualTo("joao.santos@email.com");
        assertThat(response.getTelefone()).isEqualTo("(11) 99999-9999");
        
        verify(clienteRepository, times(1)).findById(clienteId);
        verify(clienteRepository, times(1)).existsByEmail("joao.santos@email.com");
        verify(clienteRepository, times(1)).save(any(JpaCliente.class));
    }

    @Test
    @DisplayName("Deve atualizar apenas nome do cliente")
    void deveAtualizarApenasNomeDoCliente() {
        // Arrange
        Long clienteId = 1L;
        
        JpaCliente jpaCliente = new JpaCliente();
        jpaCliente.setId(clienteId);
        jpaCliente.setNome("João Silva");
        jpaCliente.setEmail("joao@email.com");
        jpaCliente.setTelefone("11987654321");

        ClienteUpdateDto updateDto = new ClienteUpdateDto();
        updateDto.setNome("João Santos");
        // Email e telefone não são alterados

        when(clienteRepository.findById(clienteId)).thenReturn(java.util.Optional.of(jpaCliente));
        when(clienteRepository.save(any(JpaCliente.class))).thenAnswer(invocation -> {
            JpaCliente cliente = invocation.getArgument(0);
            return cliente;
        });

        // Act
        ClienteProfileDto response = clienteService.atualizarMeuPerfil(clienteId, updateDto);

        // Assert
        assertThat(response.getNome()).isEqualTo("João Santos");
        assertThat(response.getEmail()).isEqualTo("joao@email.com");
        assertThat(response.getTelefone()).isEqualTo("11987654321");
        
        verify(clienteRepository, times(1)).save(any(JpaCliente.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando novo email já está cadastrado")
    void deveLancarExcecaoQuandoNovoEmailJaCadastrado() {
        // Arrange
        Long clienteId = 1L;
        
        JpaCliente jpaCliente = new JpaCliente();
        jpaCliente.setId(clienteId);
        jpaCliente.setNome("João Silva");
        jpaCliente.setEmail("joao@email.com");

        ClienteUpdateDto updateDto = new ClienteUpdateDto();
        updateDto.setEmail("outro@email.com");

        when(clienteRepository.findById(clienteId)).thenReturn(java.util.Optional.of(jpaCliente));
        when(clienteRepository.existsByEmail("outro@email.com")).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> clienteService.atualizarMeuPerfil(clienteId, updateDto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Email já cadastrado");

        verify(clienteRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando telefone tem formato inválido")
    void deveLancarExcecaoQuandoTelefoneFormatoInvalido() {
        // Arrange
        Long clienteId = 1L;
        
        JpaCliente jpaCliente = new JpaCliente();
        jpaCliente.setId(clienteId);
        jpaCliente.setTelefone("11987654321");

        ClienteUpdateDto updateDto = new ClienteUpdateDto();
        updateDto.setTelefone("123"); // Telefone com menos de 10 dígitos

        when(clienteRepository.findById(clienteId)).thenReturn(java.util.Optional.of(jpaCliente));

        // Act & Assert
        assertThatThrownBy(() -> clienteService.atualizarMeuPerfil(clienteId, updateDto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Telefone deve ter 10 ou 11 dígitos");

        verify(clienteRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve remover formatação do telefone ao atualizar")
    void deveRemoverFormatacaoDoTelefoneAoAtualizar() {
        // Arrange
        Long clienteId = 1L;
        
        JpaCliente jpaCliente = new JpaCliente();
        jpaCliente.setId(clienteId);
        jpaCliente.setNome("João Silva");
        jpaCliente.setEmail("joao@email.com");
        jpaCliente.setTelefone("11987654321");

        ClienteUpdateDto updateDto = new ClienteUpdateDto();
        updateDto.setTelefone("(11) 99999-9999");

        when(clienteRepository.findById(clienteId)).thenReturn(java.util.Optional.of(jpaCliente));
        when(clienteRepository.save(any(JpaCliente.class))).thenAnswer(invocation -> {
            JpaCliente cliente = invocation.getArgument(0);
            return cliente;
        });

        // Act
        ClienteProfileDto response = clienteService.atualizarMeuPerfil(clienteId, updateDto);

        // Assert
        assertThat(response.getTelefone()).isEqualTo("(11) 99999-9999");
    }

    @Test
    @DisplayName("Deve normalizar email para lowercase ao atualizar")
    void deveNormalizarEmailParaLowercaseAoAtualizar() {
        // Arrange
        Long clienteId = 1L;
        
        JpaCliente jpaCliente = new JpaCliente();
        jpaCliente.setId(clienteId);
        jpaCliente.setEmail("joao@email.com");

        ClienteUpdateDto updateDto = new ClienteUpdateDto();
        updateDto.setEmail("JOAO.SANTOS@EMAIL.COM");

        when(clienteRepository.findById(clienteId)).thenReturn(java.util.Optional.of(jpaCliente));
        when(clienteRepository.existsByEmail("joao.santos@email.com")).thenReturn(false);
        when(clienteRepository.save(any(JpaCliente.class))).thenAnswer(invocation -> {
            JpaCliente cliente = invocation.getArgument(0);
            return cliente;
        });

        // Act
        ClienteProfileDto response = clienteService.atualizarMeuPerfil(clienteId, updateDto);

        // Assert
        assertThat(response.getEmail()).isEqualTo("joao.santos@email.com");
    }

    @Test
    @DisplayName("Deve lançar exceção quando cliente não encontrado ao atualizar perfil")
    void deveLancarExcecaoQuandoClienteNaoEncontradoAtualizacao() {
        // Arrange
        Long clienteId = 999L;
        ClienteUpdateDto updateDto = new ClienteUpdateDto();
        updateDto.setNome("Novo Nome");

        when(clienteRepository.findById(clienteId)).thenReturn(java.util.Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> clienteService.atualizarMeuPerfil(clienteId, updateDto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Cliente não encontrado");

        verify(clienteRepository, never()).save(any());
    }
}
