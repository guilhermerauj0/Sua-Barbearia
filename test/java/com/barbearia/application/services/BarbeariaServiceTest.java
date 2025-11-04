package com.barbearia.application.services;

import com.barbearia.application.dto.BarbeariaRequestDto;
import com.barbearia.application.dto.BarbeariaResponseDto;
import com.barbearia.domain.enums.TipoDocumento;
import com.barbearia.infrastructure.persistence.entities.JpaBarbearia;
import com.barbearia.infrastructure.persistence.repositories.BarbeariaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Testes unitários para BarbeariaService.
 * 
 * Testa todas as validações e regras de negócio do registro de barbearia.
 */
@SuppressWarnings("null")
@ExtendWith(MockitoExtension.class)
@DisplayName("BarbeariaService - Testes Unitários")
class BarbeariaServiceTest {

    @Mock
    private BarbeariaRepository barbeariaRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private BarbeariaService barbeariaService;

    private BarbeariaRequestDto requestValidoCPF;
    private BarbeariaRequestDto requestValidoCNPJ;

    @BeforeEach
    void setUp() {
        // Request válido com CPF
        requestValidoCPF = new BarbeariaRequestDto();
        requestValidoCPF.setNome("Maria Santos");
        requestValidoCPF.setEmail("maria.santos@email.com");
        requestValidoCPF.setSenha("SenhaForte@123");
        requestValidoCPF.setConfirmarSenha("SenhaForte@123");
        requestValidoCPF.setTelefone("(11) 98765-4321");
        requestValidoCPF.setNomeFantasia("Barbearia Elegance");
        requestValidoCPF.setTipoDocumento(TipoDocumento.CPF);
        requestValidoCPF.setDocumento("123.456.789-09");
        requestValidoCPF.setEndereco("Rua das Flores, 123 - São Paulo/SP");

        // Request válido com CNPJ
        requestValidoCNPJ = new BarbeariaRequestDto();
        requestValidoCNPJ.setNome("Carlos Oliveira");
        requestValidoCNPJ.setEmail("carlos.oliveira@email.com");
        requestValidoCNPJ.setSenha("SenhaForte@456");
        requestValidoCNPJ.setConfirmarSenha("SenhaForte@456");
        requestValidoCNPJ.setTelefone("(21) 99876-5432");
        requestValidoCNPJ.setNomeFantasia("Barbearia Premium");
        requestValidoCNPJ.setTipoDocumento(TipoDocumento.CNPJ);
        requestValidoCNPJ.setDocumento("11.222.333/0001-81");
        requestValidoCNPJ.setEndereco("Av. Principal, 500 - Rio de Janeiro/RJ");
    }

    @Test
    @DisplayName("Deve registrar barbearia com CPF válido com sucesso")
    void deveRegistrarBarbeariaComCPFComSucesso() {
        // Arrange
        when(barbeariaRepository.existsByEmail(anyString())).thenReturn(false);
        when(barbeariaRepository.existsByTipoDocumentoAndDocumento(any(), anyString())).thenReturn(false);
        when(barbeariaRepository.save(any(JpaBarbearia.class))).thenAnswer(invocation -> {
            JpaBarbearia jpa = invocation.getArgument(0);
            jpa.setId(1L);
            return jpa;
        });

        // Act
        BarbeariaResponseDto response = barbeariaService.registrarBarbearia(requestValidoCPF);

        // Assert
        assertNotNull(response);
        assertEquals("Maria Santos", response.getNome());
        assertEquals("maria.santos@email.com", response.getEmail());
        assertEquals("Barbearia Elegance", response.getNomeFantasia());
        assertEquals(TipoDocumento.CPF, response.getTipoDocumento());
        assertEquals("12345678909", response.getDocumento());
        assertEquals("BARBEARIA", response.getRole());
        assertTrue(response.isAtivo());

        verify(barbeariaRepository).existsByEmail("maria.santos@email.com");
        verify(barbeariaRepository).existsByTipoDocumentoAndDocumento(TipoDocumento.CPF, "12345678909");
        verify(barbeariaRepository).save(any(JpaBarbearia.class));
    }

    @Test
    @DisplayName("Deve registrar barbearia com CNPJ válido com sucesso")
    void deveRegistrarBarbeariaComCNPJComSucesso() {
        // Arrange
        when(barbeariaRepository.existsByEmail(anyString())).thenReturn(false);
        when(barbeariaRepository.existsByTipoDocumentoAndDocumento(any(), anyString())).thenReturn(false);
        when(barbeariaRepository.save(any(JpaBarbearia.class))).thenAnswer(invocation -> {
            JpaBarbearia jpa = invocation.getArgument(0);
            jpa.setId(2L);
            return jpa;
        });

        // Act
        BarbeariaResponseDto response = barbeariaService.registrarBarbearia(requestValidoCNPJ);

        // Assert
        assertNotNull(response);
        assertEquals("Carlos Oliveira", response.getNome());
        assertEquals("carlos.oliveira@email.com", response.getEmail());
        assertEquals("Barbearia Premium", response.getNomeFantasia());
        assertEquals(TipoDocumento.CNPJ, response.getTipoDocumento());
        assertEquals("11222333000181", response.getDocumento());

        verify(barbeariaRepository).save(any(JpaBarbearia.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando senhas não conferem")
    void deveLancarExcecaoQuandoSenhasNaoConferem() {
        // Arrange
        requestValidoCPF.setConfirmarSenha("SenhasDiferentes@123");

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> barbeariaService.registrarBarbearia(requestValidoCPF)
        );

        assertEquals("Senha e confirmação de senha não coincidem", exception.getMessage());
        verify(barbeariaRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando email já está cadastrado")
    void deveLancarExcecaoQuandoEmailJaCadastrado() {
        // Arrange
        when(barbeariaRepository.existsByEmail(anyString())).thenReturn(true);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> barbeariaService.registrarBarbearia(requestValidoCPF)
        );

        assertEquals("Email já cadastrado no sistema", exception.getMessage());
        verify(barbeariaRepository).existsByEmail("maria.santos@email.com");
        verify(barbeariaRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando CPF é inválido")
    void deveLancarExcecaoQuandoCPFInvalido() {
        // Arrange
        requestValidoCPF.setDocumento("111.111.111-11"); // CPF inválido
        when(barbeariaRepository.existsByEmail(anyString())).thenReturn(false);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> barbeariaService.registrarBarbearia(requestValidoCPF)
        );

        assertEquals("CPF inválido. Verifique o número informado.", exception.getMessage());
        verify(barbeariaRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando CNPJ é inválido")
    void deveLancarExcecaoQuandoCNPJInvalido() {
        // Arrange
        requestValidoCNPJ.setDocumento("00.000.000/0000-00"); // CNPJ inválido
        when(barbeariaRepository.existsByEmail(anyString())).thenReturn(false);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> barbeariaService.registrarBarbearia(requestValidoCNPJ)
        );

        assertEquals("CNPJ inválido. Verifique o número informado.", exception.getMessage());
        verify(barbeariaRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando CPF já está cadastrado")
    void deveLancarExcecaoQuandoCPFJaCadastrado() {
        // Arrange
        when(barbeariaRepository.existsByEmail(anyString())).thenReturn(false);
        when(barbeariaRepository.existsByTipoDocumentoAndDocumento(any(), anyString())).thenReturn(true);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> barbeariaService.registrarBarbearia(requestValidoCPF)
        );

        assertEquals("CPF já cadastrado no sistema", exception.getMessage());
        verify(barbeariaRepository).existsByTipoDocumentoAndDocumento(TipoDocumento.CPF, "12345678909");
        verify(barbeariaRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando CNPJ já está cadastrado")
    void deveLancarExcecaoQuandoCNPJJaCadastrado() {
        // Arrange
        when(barbeariaRepository.existsByEmail(anyString())).thenReturn(false);
        when(barbeariaRepository.existsByTipoDocumentoAndDocumento(any(), anyString())).thenReturn(true);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> barbeariaService.registrarBarbearia(requestValidoCNPJ)
        );

        assertEquals("CNPJ já cadastrado no sistema", exception.getMessage());
        verify(barbeariaRepository).existsByTipoDocumentoAndDocumento(TipoDocumento.CNPJ, "11222333000181");
        verify(barbeariaRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve remover formatação do CPF antes de salvar")
    void deveRemoverFormatacaoDoCPFAntesDeSalvar() {
        // Arrange
        when(barbeariaRepository.existsByEmail(anyString())).thenReturn(false);
        when(barbeariaRepository.existsByTipoDocumentoAndDocumento(any(), anyString())).thenReturn(false);
        when(barbeariaRepository.save(any(JpaBarbearia.class))).thenAnswer(invocation -> {
            JpaBarbearia jpa = invocation.getArgument(0);
            assertEquals("12345678909", jpa.getDocumento()); // Verifica sem formatação
            jpa.setId(1L);
            return jpa;
        });

        // Act
        barbeariaService.registrarBarbearia(requestValidoCPF);

        // Assert
        verify(barbeariaRepository).save(any(JpaBarbearia.class));
    }

    @Test
    @DisplayName("Deve remover formatação do CNPJ antes de salvar")
    void deveRemoverFormatacaoDoCNPJAntesDeSalvar() {
        // Arrange
        when(barbeariaRepository.existsByEmail(anyString())).thenReturn(false);
        when(barbeariaRepository.existsByTipoDocumentoAndDocumento(any(), anyString())).thenReturn(false);
        when(barbeariaRepository.save(any(JpaBarbearia.class))).thenAnswer(invocation -> {
            JpaBarbearia jpa = invocation.getArgument(0);
            assertEquals("11222333000181", jpa.getDocumento()); // Verifica sem formatação
            jpa.setId(2L);
            return jpa;
        });

        // Act
        barbeariaService.registrarBarbearia(requestValidoCNPJ);

        // Assert
        verify(barbeariaRepository).save(any(JpaBarbearia.class));
    }

    @Test
    @DisplayName("Deve remover formatação do telefone antes de salvar")
    void deveRemoverFormatacaoDoTelefoneAntesDeSalvar() {
        // Arrange
        when(barbeariaRepository.existsByEmail(anyString())).thenReturn(false);
        when(barbeariaRepository.existsByTipoDocumentoAndDocumento(any(), anyString())).thenReturn(false);
        when(barbeariaRepository.save(any(JpaBarbearia.class))).thenAnswer(invocation -> {
            JpaBarbearia jpa = invocation.getArgument(0);
            assertEquals("11987654321", jpa.getTelefone()); // Verifica sem formatação
            jpa.setId(1L);
            return jpa;
        });

        // Act
        barbeariaService.registrarBarbearia(requestValidoCPF);

        // Assert
        verify(barbeariaRepository).save(any(JpaBarbearia.class));
    }
}
