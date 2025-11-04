package com.barbearia.application.services;

import com.barbearia.application.dto.LoginRequestDto;
import com.barbearia.application.dto.LoginResponseDto;
import com.barbearia.application.security.JwtService;
import com.barbearia.infrastructure.persistence.entities.JpaCliente;
import com.barbearia.infrastructure.persistence.entities.JpaBarbearia;
import com.barbearia.infrastructure.persistence.repositories.ClienteRepository;
import com.barbearia.infrastructure.persistence.repositories.BarbeariaRepository;
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
@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService - Testes Unitários")
class AuthServiceTest {

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private BarbeariaRepository barbeariaRepository;

    @Mock
    private JwtService jwtService;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    private LoginRequestDto loginRequest;
    private JpaCliente cliente;
    private LoginRequestDto loginBarbeariaRequest;
    private JpaBarbearia barbearia;

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
        
        loginBarbeariaRequest = new LoginRequestDto("maria@email.com", "SenhaForte@123");
        
        barbearia = new JpaBarbearia();
        barbearia.setId(5L);
        barbearia.setNome("Barbearia do Zé");
        barbearia.setEmail("maria@email.com");
        barbearia.setSenha("$2a$10$hashedPasswordBarbearia");
        barbearia.setRole("BARBEARIA");
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

    // ==================== TESTES DE LOGIN DE BARBEARIA ====================

    @Test
    @DisplayName("Deve realizar login de barbearia com sucesso quando credenciais válidas")
    void deveRealizarLoginBarbeariaComSucesso() {
        // Arrange
        when(barbeariaRepository.findByEmail("maria@email.com"))
                .thenReturn(Optional.of(barbearia));
        when(passwordEncoder.matches("SenhaForte@123", barbearia.getSenha()))
                .thenReturn(true);
        when(jwtService.generateToken(anyMap(), eq("maria@email.com")))
                .thenReturn("fake-jwt-token-barbearia");
        when(jwtService.getExpirationTime()).thenReturn(3600000L);

        // Act
        LoginResponseDto response = authService.loginBarbearia(loginBarbeariaRequest);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.token()).isEqualTo("fake-jwt-token-barbearia");
        assertThat(response.tipo()).isEqualTo("Bearer");
        assertThat(response.userId()).isEqualTo(5L);
        assertThat(response.nome()).isEqualTo("Barbearia do Zé");
        assertThat(response.email()).isEqualTo("maria@email.com");
        assertThat(response.role()).isEqualTo("BARBEARIA");
        assertThat(response.expiresIn()).isEqualTo(3600000L);

        verify(barbeariaRepository).findByEmail("maria@email.com");
        verify(passwordEncoder).matches("SenhaForte@123", barbearia.getSenha());
        verify(jwtService).generateToken(anyMap(), eq("maria@email.com"));
    }

    @Test
    @DisplayName("Deve lançar exceção quando email da barbearia não existe")
    void deveLancarExcecaoQuandoEmailBarbeariaNaoExiste() {
        // Arrange
        when(barbeariaRepository.findByEmail("maria@email.com"))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> authService.loginBarbearia(loginBarbeariaRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Credenciais inválidas");

        verify(barbeariaRepository).findByEmail("maria@email.com");
        verify(passwordEncoder, never()).matches(anyString(), anyString());
        verify(jwtService, never()).generateToken(anyMap(), anyString());
    }

    @Test
    @DisplayName("Deve lançar exceção quando senha da barbearia está incorreta")
    void deveLancarExcecaoQuandoSenhaBarbeariaIncorreta() {
        // Arrange
        when(barbeariaRepository.findByEmail("maria@email.com"))
                .thenReturn(Optional.of(barbearia));
        when(passwordEncoder.matches("SenhaForte@123", barbearia.getSenha()))
                .thenReturn(false);

        // Act & Assert
        assertThatThrownBy(() -> authService.loginBarbearia(loginBarbeariaRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Credenciais inválidas");

        verify(barbeariaRepository).findByEmail("maria@email.com");
        verify(passwordEncoder).matches("SenhaForte@123", barbearia.getSenha());
        verify(jwtService, never()).generateToken(anyMap(), anyString());
    }

    @Test
    @DisplayName("Deve normalizar email da barbearia para lowercase antes de buscar")
    void deveNormalizarEmailBarbeariaParaLowercase() {
        // Arrange
        LoginRequestDto requestComEmailMaiusculo = new LoginRequestDto("MARIA@EMAIL.COM", "SenhaForte@123");
        
        when(barbeariaRepository.findByEmail("maria@email.com"))
                .thenReturn(Optional.of(barbearia));
        when(passwordEncoder.matches("SenhaForte@123", barbearia.getSenha()))
                .thenReturn(true);
        when(jwtService.generateToken(anyMap(), eq("maria@email.com")))
                .thenReturn("fake-jwt-token-barbearia");
        when(jwtService.getExpirationTime()).thenReturn(3600000L);

        // Act
        LoginResponseDto response = authService.loginBarbearia(requestComEmailMaiusculo);

        // Assert
        assertThat(response).isNotNull();
        verify(barbeariaRepository).findByEmail("maria@email.com");
    }

    @Test
    @DisplayName("Deve incluir claims corretos no token JWT da barbearia incluindo barbeariaId")
    void deveIncluirClaimsCorretosNoTokenBarbearia() {
        // Arrange
        when(barbeariaRepository.findByEmail("maria@email.com"))
                .thenReturn(Optional.of(barbearia));
        when(passwordEncoder.matches("SenhaForte@123", barbearia.getSenha()))
                .thenReturn(true);
        when(jwtService.generateToken(anyMap(), eq("maria@email.com")))
                .thenReturn("fake-jwt-token-barbearia");
        when(jwtService.getExpirationTime()).thenReturn(3600000L);

        // Act
        authService.loginBarbearia(loginBarbeariaRequest);

        // Assert
        verify(jwtService).generateToken(argThat(claims -> {
            Map<String, Object> claimsMap = (Map<String, Object>) claims;
            return claimsMap.get("userId").equals(5L) &&
                   claimsMap.get("barbeariaId").equals(5L) &&
                   claimsMap.get("role").equals("BARBEARIA") &&
                   claimsMap.get("nome").equals("Barbearia do Zé");
        }), eq("maria@email.com"));
    }
}
