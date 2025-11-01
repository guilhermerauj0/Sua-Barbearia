package com.barbearia.application.services;

import com.barbearia.application.dto.LoginRequestDto;
import com.barbearia.application.dto.LoginResponseDto;
import com.barbearia.application.security.JwtService;
import com.barbearia.infrastructure.persistence.entities.JpaCliente;
import com.barbearia.infrastructure.persistence.repositories.ClienteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Testes unitários para AuthService.
 */
@SuppressWarnings("null")
@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService - Testes Unitários")
class AuthServiceTest {

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private JwtService jwtService;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    private LoginRequestDto loginRequest;
    private JpaCliente cliente;

    @BeforeEach
    void setUp() {
        loginRequest = new LoginRequestDto("joao@email.com", "Senha@123");
        
        cliente = new JpaCliente();
        cliente.setId(1L);
        cliente.setNome("João Silva");
        cliente.setEmail("joao@email.com");
        cliente.setSenha("$2a$10$hashedPassword"); // Senha com hash
        cliente.setTelefone("11987654321");
        cliente.setRole("CLIENTE");
    }

    @Test
    @DisplayName("Deve realizar login com sucesso quando credenciais válidas")
    void deveRealizarLoginComSucesso() {
        // Arrange
        when(clienteRepository.findByEmail("joao@email.com"))
                .thenReturn(Optional.of(cliente));
        when(passwordEncoder.matches("Senha@123", cliente.getSenha()))
                .thenReturn(true);
        when(jwtService.generateToken(anyMap(), eq("joao@email.com")))
                .thenReturn("fake-jwt-token");
        when(jwtService.getExpirationTime()).thenReturn(3600000L);

        // Act
        LoginResponseDto response = authService.login(loginRequest);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.token()).isEqualTo("fake-jwt-token");
        assertThat(response.tipo()).isEqualTo("Bearer");
        assertThat(response.userId()).isEqualTo(1L);
        assertThat(response.nome()).isEqualTo("João Silva");
        assertThat(response.email()).isEqualTo("joao@email.com");
        assertThat(response.role()).isEqualTo("CLIENTE");
        assertThat(response.expiresIn()).isEqualTo(3600000L);

        verify(clienteRepository).findByEmail("joao@email.com");
        verify(passwordEncoder).matches("Senha@123", cliente.getSenha());
        verify(jwtService).generateToken(anyMap(), eq("joao@email.com"));
    }

    @Test
    @DisplayName("Deve lançar exceção quando email não existe")
    void deveLancarExcecaoQuandoEmailNaoExiste() {
        // Arrange
        when(clienteRepository.findByEmail("joao@email.com"))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> authService.login(loginRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Credenciais inválidas");

        verify(clienteRepository).findByEmail("joao@email.com");
        verify(passwordEncoder, never()).matches(anyString(), anyString());
        verify(jwtService, never()).generateToken(anyMap(), anyString());
    }

    @Test
    @DisplayName("Deve lançar exceção quando senha está incorreta")
    void deveLancarExcecaoQuandoSenhaIncorreta() {
        // Arrange
        when(clienteRepository.findByEmail("joao@email.com"))
                .thenReturn(Optional.of(cliente));
        when(passwordEncoder.matches("Senha@123", cliente.getSenha()))
                .thenReturn(false);

        // Act & Assert
        assertThatThrownBy(() -> authService.login(loginRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Credenciais inválidas");

        verify(clienteRepository).findByEmail("joao@email.com");
        verify(passwordEncoder).matches("Senha@123", cliente.getSenha());
        verify(jwtService, never()).generateToken(anyMap(), anyString());
    }

    @Test
    @DisplayName("Deve normalizar email para lowercase antes de buscar")
    void deveNormalizarEmailParaLowercase() {
        // Arrange
        LoginRequestDto requestComEmailMaiusculo = new LoginRequestDto("JOAO@EMAIL.COM", "Senha@123");
        
        when(clienteRepository.findByEmail("joao@email.com"))
                .thenReturn(Optional.of(cliente));
        when(passwordEncoder.matches("Senha@123", cliente.getSenha()))
                .thenReturn(true);
        when(jwtService.generateToken(anyMap(), eq("joao@email.com")))
                .thenReturn("fake-jwt-token");
        when(jwtService.getExpirationTime()).thenReturn(3600000L);

        // Act
        LoginResponseDto response = authService.login(requestComEmailMaiusculo);

        // Assert
        assertThat(response).isNotNull();
        verify(clienteRepository).findByEmail("joao@email.com");
    }

    @Test
    @DisplayName("Deve incluir claims corretos no token JWT")
    void deveIncluirClaimsCorretosNoToken() {
        // Arrange
        when(clienteRepository.findByEmail("joao@email.com"))
                .thenReturn(Optional.of(cliente));
        when(passwordEncoder.matches("Senha@123", cliente.getSenha()))
                .thenReturn(true);
        when(jwtService.generateToken(anyMap(), eq("joao@email.com")))
                .thenReturn("fake-jwt-token");
        when(jwtService.getExpirationTime()).thenReturn(3600000L);

        // Act
        authService.login(loginRequest);

        // Assert
        verify(jwtService).generateToken(argThat(claims -> {
            Map<String, Object> claimsMap = (Map<String, Object>) claims;
            return claimsMap.get("userId").equals(1L) &&
                   claimsMap.get("role").equals("CLIENTE") &&
                   claimsMap.get("nome").equals("João Silva");
        }), eq("joao@email.com"));
    }
}
