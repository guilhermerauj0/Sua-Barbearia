package com.barbearia.application.services;

import com.barbearia.adapters.mappers.FuncionarioMapper;
import com.barbearia.application.dto.FuncionarioRequestDto;
import com.barbearia.application.dto.FuncionarioResponseDto;
import com.barbearia.domain.enums.TipoPerfil;
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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Testes unitários para FuncionarioService.
 * 
 * Testa todas as validações e regras de negócio da criação e listagem de funcionários.
 */
@SuppressWarnings("null")
@ExtendWith(MockitoExtension.class)
@DisplayName("FuncionarioService - Testes Unitários")
class FuncionarioServiceTest {

    @Mock
    private FuncionarioRepository funcionarioRepository;

    @Mock
    private FuncionarioMapper funcionarioMapper;

    @InjectMocks
    private FuncionarioService funcionarioService;

    private FuncionarioRequestDto requestValido;
    private JpaFuncionario funcionarioBarbeiro;
    private FuncionarioResponseDto responseDto;
    private Long barbeariaId;

    @BeforeEach
    void setUp() {
        barbeariaId = 1L;

        // Request válido
        requestValido = new FuncionarioRequestDto(
            "Carlos Silva",
            "carlos.silva@email.com",
            "(11) 98765-4321",
            TipoPerfil.BARBEIRO
        );

        // Entidade funcionário
        funcionarioBarbeiro = new JpaFuncionario();
        funcionarioBarbeiro.setId(1L);
        funcionarioBarbeiro.setBarbeariaId(barbeariaId);
        funcionarioBarbeiro.setNome("Carlos Silva");
        funcionarioBarbeiro.setEmail("carlos.silva@email.com");
        funcionarioBarbeiro.setTelefone("(11) 98765-4321");
        funcionarioBarbeiro.setPerfilType(TipoPerfil.BARBEIRO);
        funcionarioBarbeiro.setAtivo(true);
        funcionarioBarbeiro.setDataCriacao(LocalDateTime.now());
        funcionarioBarbeiro.setDataAtualizacao(LocalDateTime.now());

        // Response DTO
        responseDto = new FuncionarioResponseDto(
            1L,
            barbeariaId,
            "Carlos Silva",
            "carlos.silva@email.com",
            "(11) 98765-4321",
            TipoPerfil.BARBEIRO,
            "BARBEIRO",
            "Cortes de cabelo e barba",
            true,
            LocalDateTime.now(),
            LocalDateTime.now()
        );
    }

    // ==================== TESTES DE CRIAÇÃO ====================

    @Test
    @DisplayName("Deve criar funcionário com sucesso quando dados válidos")
    void deveCriarFuncionarioComSucesso() {
        // Arrange
        when(funcionarioRepository.existsByEmailAndBarbeariaId(
            requestValido.email(), barbeariaId)).thenReturn(false);
        when(funcionarioMapper.toEntityFromDto(requestValido, barbeariaId))
            .thenReturn(funcionarioBarbeiro);
        when(funcionarioRepository.save(any(JpaFuncionario.class)))
            .thenReturn(funcionarioBarbeiro);
        when(funcionarioMapper.toResponseDto(funcionarioBarbeiro))
            .thenReturn(responseDto);

        // Act
        FuncionarioResponseDto resultado = funcionarioService.criarFuncionario(
            requestValido, barbeariaId);

        // Assert
        assertNotNull(resultado);
        assertEquals("Carlos Silva", resultado.nome());
        assertEquals("carlos.silva@email.com", resultado.email());
        assertEquals(TipoPerfil.BARBEIRO, resultado.perfilType());
        assertTrue(resultado.ativo());
        verify(funcionarioRepository).existsByEmailAndBarbeariaId(
            requestValido.email(), barbeariaId);
        verify(funcionarioRepository).save(any(JpaFuncionario.class));
        verify(funcionarioMapper).toResponseDto(funcionarioBarbeiro);
    }

    @Test
    @DisplayName("Deve lançar exceção quando email já existe para a mesma barbearia")
    void deveLancarExcecaoQuandoEmailDuplicado() {
        // Arrange
        when(funcionarioRepository.existsByEmailAndBarbeariaId(
            requestValido.email(), barbeariaId)).thenReturn(true);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> funcionarioService.criarFuncionario(requestValido, barbeariaId)
        );

        assertEquals("Já existe um funcionário com este email nesta barbearia", 
            exception.getMessage());
        verify(funcionarioRepository).existsByEmailAndBarbeariaId(
            requestValido.email(), barbeariaId);
        verify(funcionarioRepository, never()).save(any(JpaFuncionario.class));
    }

    @Test
    @DisplayName("Deve criar funcionário MANICURE com sucesso")
    void deveCriarFuncionarioManicureComSucesso() {
        // Arrange
        FuncionarioRequestDto requestManicure = new FuncionarioRequestDto(
            "Ana Costa",
            "ana.costa@email.com",
            "(11) 91234-5678",
            TipoPerfil.MANICURE
        );

        JpaFuncionario funcionarioManicure = new JpaFuncionario();
        funcionarioManicure.setId(2L);
        funcionarioManicure.setBarbeariaId(barbeariaId);
        funcionarioManicure.setNome("Ana Costa");
        funcionarioManicure.setEmail("ana.costa@email.com");
        funcionarioManicure.setTelefone("(11) 91234-5678");
        funcionarioManicure.setPerfilType(TipoPerfil.MANICURE);
        funcionarioManicure.setAtivo(true);

        FuncionarioResponseDto responseManicure = new FuncionarioResponseDto(
            2L, barbeariaId, "Ana Costa", "ana.costa@email.com", 
            "(11) 91234-5678", TipoPerfil.MANICURE, "MANICURE", 
            "Manicure e pedicure", true, 
            LocalDateTime.now(), LocalDateTime.now()
        );

        when(funcionarioRepository.existsByEmailAndBarbeariaId(
            requestManicure.email(), barbeariaId)).thenReturn(false);
        when(funcionarioMapper.toEntityFromDto(requestManicure, barbeariaId))
            .thenReturn(funcionarioManicure);
        when(funcionarioRepository.save(any(JpaFuncionario.class)))
            .thenReturn(funcionarioManicure);
        when(funcionarioMapper.toResponseDto(funcionarioManicure))
            .thenReturn(responseManicure);

        // Act
        FuncionarioResponseDto resultado = funcionarioService.criarFuncionario(
            requestManicure, barbeariaId);

        // Assert
        assertNotNull(resultado);
        assertEquals("Ana Costa", resultado.nome());
        assertEquals(TipoPerfil.MANICURE, resultado.perfilType());
        verify(funcionarioRepository).save(any(JpaFuncionario.class));
    }

    // ==================== TESTES DE LISTAGEM ====================

    @Test
    @DisplayName("Deve listar todos os funcionários ativos da barbearia")
    void deveListarFuncionariosAtivos() {
        // Arrange
        JpaFuncionario funcionarioManicure = new JpaFuncionario();
        funcionarioManicure.setId(2L);
        funcionarioManicure.setBarbeariaId(barbeariaId);
        funcionarioManicure.setNome("Ana Costa");
        funcionarioManicure.setEmail("ana.costa@email.com");
        funcionarioManicure.setTelefone("(11) 91234-5678");
        funcionarioManicure.setPerfilType(TipoPerfil.MANICURE);
        funcionarioManicure.setAtivo(true);

        List<JpaFuncionario> funcionarios = Arrays.asList(
            funcionarioBarbeiro, 
            funcionarioManicure
        );

        FuncionarioResponseDto response1 = new FuncionarioResponseDto(
            1L, barbeariaId, "Carlos Silva", "carlos.silva@email.com", 
            "(11) 98765-4321", TipoPerfil.BARBEIRO, "BARBEIRO", 
            "Cortes de cabelo e barba", true,
            LocalDateTime.now(), LocalDateTime.now()
        );

        FuncionarioResponseDto response2 = new FuncionarioResponseDto(
            2L, barbeariaId, "Ana Costa", "ana.costa@email.com", 
            "(11) 91234-5678", TipoPerfil.MANICURE, "MANICURE",
            "Manicure e pedicure", true,
            LocalDateTime.now(), LocalDateTime.now()
        );

        when(funcionarioRepository.findByBarbeariaIdAndAtivoTrue(barbeariaId))
            .thenReturn(funcionarios);
        when(funcionarioMapper.toResponseDto(funcionarioBarbeiro))
            .thenReturn(response1);
        when(funcionarioMapper.toResponseDto(funcionarioManicure))
            .thenReturn(response2);

        // Act
        List<FuncionarioResponseDto> resultado = 
            funcionarioService.listarFuncionariosDaBarbearia(barbeariaId);

        // Assert
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertEquals("Carlos Silva", resultado.get(0).nome());
        assertEquals("Ana Costa", resultado.get(1).nome());
        verify(funcionarioRepository).findByBarbeariaIdAndAtivoTrue(barbeariaId);
        verify(funcionarioMapper, times(2)).toResponseDto(any(JpaFuncionario.class));
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando barbearia não tem funcionários")
    void deveRetornarListaVaziaQuandoSemFuncionarios() {
        // Arrange
        when(funcionarioRepository.findByBarbeariaIdAndAtivoTrue(barbeariaId))
            .thenReturn(Collections.emptyList());

        // Act
        List<FuncionarioResponseDto> resultado = 
            funcionarioService.listarFuncionariosDaBarbearia(barbeariaId);

        // Assert
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
        verify(funcionarioRepository).findByBarbeariaIdAndAtivoTrue(barbeariaId);
        verify(funcionarioMapper, never()).toResponseDto(any(JpaFuncionario.class));
    }

    // ==================== TESTES DE VALIDAÇÃO DE PROFISSÃO ====================

    @Test
    @DisplayName("Deve validar profissões permitidas - BARBEIRO")
    void deveValidarProfissaoBarbeiro() {
        when(funcionarioRepository.existsByEmailAndBarbeariaId(anyString(), anyLong()))
            .thenReturn(false);
        when(funcionarioMapper.toEntityFromDto(any(), anyLong()))
            .thenReturn(funcionarioBarbeiro);
        when(funcionarioRepository.save(any()))
            .thenReturn(funcionarioBarbeiro);
        when(funcionarioMapper.toResponseDto(any(JpaFuncionario.class)))
            .thenReturn(responseDto);

        assertDoesNotThrow(() -> 
            funcionarioService.criarFuncionario(
                new FuncionarioRequestDto("Nome", "email@test.com", "11999999999", TipoPerfil.BARBEIRO),
                barbeariaId
            )
        );
    }

    @Test
    @DisplayName("Deve validar profissões permitidas - MANICURE")
    void deveValidarProfissaoManicure() {
        JpaFuncionario func = new JpaFuncionario();
        func.setPerfilType(TipoPerfil.MANICURE);
        
        when(funcionarioRepository.existsByEmailAndBarbeariaId(anyString(), anyLong()))
            .thenReturn(false);
        when(funcionarioMapper.toEntityFromDto(any(), anyLong()))
            .thenReturn(func);
        when(funcionarioRepository.save(any()))
            .thenReturn(func);
        when(funcionarioMapper.toResponseDto(any(JpaFuncionario.class)))
            .thenReturn(responseDto);

        assertDoesNotThrow(() -> 
            funcionarioService.criarFuncionario(
                new FuncionarioRequestDto("Nome", "email2@test.com", "11999999999", TipoPerfil.MANICURE),
                barbeariaId
            )
        );
    }

    @Test
    @DisplayName("Deve validar profissões permitidas - ESTETICISTA")
    void deveValidarProfissaoEsteticista() {
        JpaFuncionario func = new JpaFuncionario();
        func.setPerfilType(TipoPerfil.ESTETICISTA);
        
        when(funcionarioRepository.existsByEmailAndBarbeariaId(anyString(), anyLong()))
            .thenReturn(false);
        when(funcionarioMapper.toEntityFromDto(any(), anyLong()))
            .thenReturn(func);
        when(funcionarioRepository.save(any()))
            .thenReturn(func);
        when(funcionarioMapper.toResponseDto(any(JpaFuncionario.class)))
            .thenReturn(responseDto);

        assertDoesNotThrow(() -> 
            funcionarioService.criarFuncionario(
                new FuncionarioRequestDto("Nome", "email3@test.com", "11999999999", TipoPerfil.ESTETICISTA),
                barbeariaId
            )
        );
    }

    @Test
    @DisplayName("Deve validar profissões permitidas - COLORISTA")
    void deveValidarProfissaoColorista() {
        JpaFuncionario func = new JpaFuncionario();
        func.setPerfilType(TipoPerfil.COLORISTA);
        
        when(funcionarioRepository.existsByEmailAndBarbeariaId(anyString(), anyLong()))
            .thenReturn(false);
        when(funcionarioMapper.toEntityFromDto(any(), anyLong()))
            .thenReturn(func);
        when(funcionarioRepository.save(any()))
            .thenReturn(func);
        when(funcionarioMapper.toResponseDto(any(JpaFuncionario.class)))
            .thenReturn(responseDto);

        assertDoesNotThrow(() -> 
            funcionarioService.criarFuncionario(
                new FuncionarioRequestDto("Nome", "email4@test.com", "11999999999", TipoPerfil.COLORISTA),
                barbeariaId
            )
        );
    }
}
