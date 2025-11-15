package com.barbearia.application.services;

import com.barbearia.application.dto.BarbeariaRequestDto;
import com.barbearia.application.dto.BarbeariaResponseDto;
import com.barbearia.application.dto.BarbeariaListItemDto;
import com.barbearia.application.dto.ServicoDto;
import com.barbearia.domain.enums.TipoDocumento;
import com.barbearia.infrastructure.persistence.entities.JpaBarbearia;
import com.barbearia.infrastructure.persistence.entities.JpaServico;
import com.barbearia.infrastructure.persistence.entities.JpaServicoCorte;
import com.barbearia.infrastructure.persistence.entities.JpaServicoBarba;
import com.barbearia.infrastructure.persistence.repositories.BarbeariaRepository;
import com.barbearia.infrastructure.persistence.repositories.ServicoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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
    private ServicoRepository servicoRepository;

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

    @Test
    @DisplayName("Deve listar barbearias ativas com sucesso")
    void deveListarBarbeariasAtivasComSucesso() {
        // Arrange
        JpaBarbearia barbearia1 = new JpaBarbearia();
        barbearia1.setId(1L);
        barbearia1.setNome("Barbearia Premium");
        barbearia1.setNomeFantasia("Premium Cuts");
        barbearia1.setEndereco("Rua A, 100");
        barbearia1.setTelefone("11987654321");
        barbearia1.setEmail("premium@email.com");
        barbearia1.setAtivo(true);

        JpaBarbearia barbearia2 = new JpaBarbearia();
        barbearia2.setId(2L);
        barbearia2.setNome("Barbearia Central");
        barbearia2.setNomeFantasia("Central Barber");
        barbearia2.setEndereco("Rua B, 200");
        barbearia2.setTelefone("11987654322");
        barbearia2.setEmail("central@email.com");
        barbearia2.setAtivo(true);

        when(barbeariaRepository.findByAtivoTrue()).thenReturn(Arrays.asList(barbearia1, barbearia2));

        // Act
        List<BarbeariaListItemDto> resultado = barbeariaService.listarBarbearias();

        // Assert
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertEquals("Barbearia Premium", resultado.get(0).getNome());
        assertEquals("Premium Cuts", resultado.get(0).getNomeFantasia());
        assertEquals(0.0, resultado.get(0).getAvaliacaoMedia());
        assertEquals("Barbearia Central", resultado.get(1).getNome());
        
        verify(barbeariaRepository, times(1)).findByAtivoTrue();
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando não há barbearias ativas")
    void deveRetornarListaVaziaQuandoNaoHaBarbeariasAtivas() {
        // Arrange
        when(barbeariaRepository.findByAtivoTrue()).thenReturn(Collections.emptyList());

        // Act
        List<BarbeariaListItemDto> resultado = barbeariaService.listarBarbearias();

        // Assert
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
        verify(barbeariaRepository, times(1)).findByAtivoTrue();
    }

    @Test
    @DisplayName("Deve listar serviços de uma barbearia ativa com sucesso")
    void deveListarServicosDeUmaBarbeariaAtivaComSucesso() {
        // Arrange
        Long barbeariaId = 1L;
        
        JpaBarbearia barbearia = new JpaBarbearia();
        barbearia.setId(barbeariaId);
        barbearia.setNome("Barbearia Premium");
        barbearia.setAtivo(true);

        JpaServico servico1 = new JpaServicoCorte();
        servico1.setId(1L);
        servico1.setNome("Corte de Cabelo");
        servico1.setDescricao("Corte clássico");
        servico1.setPreco(BigDecimal.valueOf(50.00));
        servico1.setDuracao(30);
        servico1.setAtivo(true);

        JpaServico servico2 = new JpaServicoBarba();
        servico2.setId(2L);
        servico2.setNome("Corte + Barba");
        servico2.setDescricao("Combo completo");
        servico2.setPreco(BigDecimal.valueOf(80.00));
        servico2.setDuracao(45);
        servico2.setAtivo(true);

        when(barbeariaRepository.findById(barbeariaId)).thenReturn(java.util.Optional.of(barbearia));
        when(servicoRepository.findByBarbeariaIdAndAtivoTrue(barbeariaId))
                .thenReturn(Arrays.asList(servico1, servico2));

        // Act
        List<ServicoDto> resultado = barbeariaService.listarServicosPorBarbearia(barbeariaId);

        // Assert
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertEquals("Corte de Cabelo", resultado.get(0).getNome());
        assertEquals(BigDecimal.valueOf(50.00), resultado.get(0).getPreco());
        assertEquals("Corte + Barba", resultado.get(1).getNome());
        
        verify(barbeariaRepository, times(1)).findById(barbeariaId);
        verify(servicoRepository, times(1)).findByBarbeariaIdAndAtivoTrue(barbeariaId);
    }

    @Test
    @DisplayName("Deve lançar exceção quando barbearia não encontrada")
    void deveLancarExcecaoQuandoBarbeariaEhNaoEncontrada() {
        // Arrange
        Long barbeariaId = 999L;
        when(barbeariaRepository.findById(barbeariaId)).thenReturn(java.util.Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> barbeariaService.listarServicosPorBarbearia(barbeariaId)
        );
        
        assertEquals("Barbearia não encontrada", exception.getMessage());
        verify(barbeariaRepository, times(1)).findById(barbeariaId);
        verify(servicoRepository, never()).findByBarbeariaIdAndAtivoTrue(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando barbearia está inativa")
    void deveLancarExcecaoQuandoBarbeariaEstaInativa() {
        // Arrange
        Long barbeariaId = 1L;
        
        JpaBarbearia barbearia = new JpaBarbearia();
        barbearia.setId(barbeariaId);
        barbearia.setNome("Barbearia Inativa");
        barbearia.setAtivo(false);

        when(barbeariaRepository.findById(barbeariaId)).thenReturn(java.util.Optional.of(barbearia));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> barbeariaService.listarServicosPorBarbearia(barbeariaId)
        );
        
        assertEquals("Barbearia está inativa", exception.getMessage());
        verify(barbeariaRepository, times(1)).findById(barbeariaId);
        verify(servicoRepository, never()).findByBarbeariaIdAndAtivoTrue(any());
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando barbearia não tem serviços ativos")
    void deveRetornarListaVaziaQuandoBarbeariaEhSemServicosAtivos() {
        // Arrange
        Long barbeariaId = 1L;
        
        JpaBarbearia barbearia = new JpaBarbearia();
        barbearia.setId(barbeariaId);
        barbearia.setNome("Barbearia Sem Serviços");
        barbearia.setAtivo(true);

        when(barbeariaRepository.findById(barbeariaId)).thenReturn(java.util.Optional.of(barbearia));
        when(servicoRepository.findByBarbeariaIdAndAtivoTrue(barbeariaId))
                .thenReturn(Collections.emptyList());

        // Act
        List<ServicoDto> resultado = barbeariaService.listarServicosPorBarbearia(barbeariaId);

        // Assert
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
        verify(servicoRepository, times(1)).findByBarbeariaIdAndAtivoTrue(barbeariaId);
    }

    @Test
    @DisplayName("Deve criar serviço com sucesso")
    void deveCriarServicoComSucesso() {
        // Arrange
        Long barbeariaId = 1L;
        
        JpaBarbearia barbearia = new JpaBarbearia();
        barbearia.setId(barbeariaId);
        barbearia.setNome("Barbearia Teste");
        barbearia.setAtivo(true);

        com.barbearia.application.dto.ServicoRequestDto requestDto = 
                new com.barbearia.application.dto.ServicoRequestDto();
        requestDto.setNome("Corte Degradê");
        requestDto.setDescricao("Corte com degradê total");
        requestDto.setPreco(BigDecimal.valueOf(60.00));
        requestDto.setDuracao(40);
        requestDto.setTipoServico("CORTE"); // TIPO OBRIGATÓRIO

        when(barbeariaRepository.findById(barbeariaId)).thenReturn(java.util.Optional.of(barbearia));
        when(servicoRepository.save(any(JpaServico.class))).thenAnswer(invocation -> {
            JpaServico servico = invocation.getArgument(0);
            servico.setId(1L);
            return servico;
        });

        // Act
        com.barbearia.application.dto.ServicoDto resultado = 
                barbeariaService.criarServico(barbeariaId, requestDto);

        // Assert
        assertNotNull(resultado);
        assertEquals("Corte Degradê", resultado.getNome());
        assertEquals("Corte com degradê total", resultado.getDescricao());
        assertEquals(BigDecimal.valueOf(60.00), resultado.getPreco());
        assertEquals(40, resultado.getDuracao());
        assertEquals("CORTE", resultado.getTipoServico());
        assertTrue(resultado.isAtivo());
        
        verify(barbeariaRepository, times(1)).findById(barbeariaId);
        verify(servicoRepository, times(1)).save(any(JpaServico.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando criar serviço sem tipoServico")
    void deveLancarExcecaoQuandoCriarServicoSemTipo() {
        // Arrange
        Long barbeariaId = 1L;
        
        JpaBarbearia barbearia = new JpaBarbearia();
        barbearia.setId(barbeariaId);
        barbearia.setAtivo(true);

        com.barbearia.application.dto.ServicoRequestDto requestDto = 
                new com.barbearia.application.dto.ServicoRequestDto();
        requestDto.setNome("Corte de Cabelo");
        requestDto.setPreco(BigDecimal.valueOf(50.00));
        requestDto.setDuracao(30);
        // tipoServico NÃO foi definido

        // Act & Assert
        assertThrows(IllegalArgumentException.class,
                () -> barbeariaService.criarServico(barbeariaId, requestDto),
                "Deve lançar exceção quando tipoServico não é informado"
        );
    }

    @Test
    @DisplayName("Deve lançar exceção quando criar serviço com tipoServico inválido")
    void deveLancarExcecaoQuandoCriarServicoComTipoInvalido() {
        // Arrange
        Long barbeariaId = 1L;
        
        com.barbearia.application.dto.ServicoRequestDto requestDto = 
                new com.barbearia.application.dto.ServicoRequestDto();
        requestDto.setNome("Serviço Estranho");
        requestDto.setPreco(BigDecimal.valueOf(50.00));
        requestDto.setDuracao(30);
        requestDto.setTipoServico("TIPO_INEXISTENTE"); // Tipo inválido

        // Act & Assert
        assertThrows(IllegalArgumentException.class,
                () -> barbeariaService.criarServico(barbeariaId, requestDto),
                "Deve lançar exceção quando tipoServico é inválido"
        );
    }

    @Test
    @DisplayName("Deve lançar exceção quando criar serviço com dados inválidos")
    void deveLancarExcecaoQuandoCriarServicoComDadosInvalidos() {
        // Arrange
        Long barbeariaId = 1L;
        
        com.barbearia.application.dto.ServicoRequestDto requestDto = 
                new com.barbearia.application.dto.ServicoRequestDto();
        requestDto.setNome(""); // Nome inválido
        requestDto.setPreco(BigDecimal.valueOf(50.00));
        requestDto.setDuracao(30);
        requestDto.setTipoServico("CORTE");

        // Act & Assert
        assertThrows(
                IllegalArgumentException.class,
                () -> barbeariaService.criarServico(barbeariaId, requestDto)
        );
        verify(barbeariaRepository, never()).findById(any());
        verify(servicoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando criar serviço com preço zero")
    void deveLancarExcecaoQuandoCriarServicoComPrecoZero() {
        // Arrange
        Long barbeariaId = 1L;
        
        com.barbearia.application.dto.ServicoRequestDto requestDto = 
                new com.barbearia.application.dto.ServicoRequestDto();
        requestDto.setNome("Corte de Cabelo");
        requestDto.setPreco(BigDecimal.ZERO); // Preço inválido
        requestDto.setDuracao(30);
        requestDto.setTipoServico("CORTE");

        // Act & Assert
        assertThrows(
                IllegalArgumentException.class,
                () -> barbeariaService.criarServico(barbeariaId, requestDto)
        );
    }

    @Test
    @DisplayName("Deve lançar exceção quando barbearia não encontrada ao criar serviço")
    void deveLancarExcecaoQuandoBarbeariaEhNaoEncontradaAoCriarServico() {
        // Arrange
        Long barbeariaId = 999L;
        
        com.barbearia.application.dto.ServicoRequestDto requestDto = 
                new com.barbearia.application.dto.ServicoRequestDto();
        requestDto.setNome("Corte de Cabelo");
        requestDto.setPreco(BigDecimal.valueOf(50.00));
        requestDto.setDuracao(30);
        requestDto.setTipoServico("CORTE");

        when(barbeariaRepository.findById(barbeariaId)).thenReturn(java.util.Optional.empty());

        // Act & Assert
        assertThrows(
                IllegalArgumentException.class,
                () -> barbeariaService.criarServico(barbeariaId, requestDto)
        );
        verify(servicoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando barbearia inativa tentar criar serviço")
    void deveLancarExcecaoQuandoBarbeariaInativaTentarCriarServico() {
        // Arrange
        Long barbeariaId = 1L;
        
        JpaBarbearia barbearia = new JpaBarbearia();
        barbearia.setId(barbeariaId);
        barbearia.setNome("Barbearia Inativa");
        barbearia.setAtivo(false);

        com.barbearia.application.dto.ServicoRequestDto requestDto = 
                new com.barbearia.application.dto.ServicoRequestDto();
        requestDto.setNome("Corte de Cabelo");
        requestDto.setPreco(BigDecimal.valueOf(50.00));
        requestDto.setDuracao(30);
        requestDto.setTipoServico("CORTE");

        when(barbeariaRepository.findById(barbeariaId)).thenReturn(java.util.Optional.of(barbearia));

        // Act & Assert
        assertThrows(
                IllegalArgumentException.class,
                () -> barbeariaService.criarServico(barbeariaId, requestDto)
        );
        verify(servicoRepository, never()).save(any());
    }
}
