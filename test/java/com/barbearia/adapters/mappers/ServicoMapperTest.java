package com.barbearia.adapters.mappers;

import com.barbearia.application.dto.ServicoDto;
import com.barbearia.domain.entities.*;
import com.barbearia.infrastructure.persistence.entities.JpaServico;
import com.barbearia.infrastructure.persistence.entities.JpaServicoBarba;
import com.barbearia.infrastructure.persistence.entities.JpaServicoCorte;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes para ServicoMapper.
 * 
 * Testa as conversões entre JpaServico, Servico de domínio e ServicoDto,
 * com foco no polimorfismo e mapeamento de tipos de serviço.
 */
@DisplayName("ServicoMapper - Testes de Mapeamento")
class ServicoMapperTest {

    private LocalDateTime agora;

    @BeforeEach
    void setUp() {
        agora = LocalDateTime.now();
    }

    @Test
    @DisplayName("Deve converter JpaServico para ServicoDto com tipo CORTE")
    void deveConverterJpaServicoParaDtoComTipoCorte() {
        // Arrange
        JpaServico jpaServico = new JpaServicoCorte();
        jpaServico.setId(1L);
        jpaServico.setNome("Corte Clássico");
        jpaServico.setDescricao("Corte tradicional");
        jpaServico.setPreco(BigDecimal.valueOf(50.00));
        jpaServico.setDuracao(30);
        jpaServico.setBarbeariaId(1L);
        jpaServico.setAtivo(true);
        jpaServico.setDataCriacao(agora);
        jpaServico.setDataAtualizacao(agora);

        // Act
        ServicoDto dto = ServicoMapper.toDto(jpaServico);

        // Assert
        assertNotNull(dto);
        assertEquals(1L, dto.getId());
        assertEquals("Corte Clássico", dto.getNome());
        assertEquals("Corte tradicional", dto.getDescricao());
        assertEquals(BigDecimal.valueOf(50.00), dto.getPreco());
        assertEquals(30, dto.getDuracao());
        assertEquals(1L, dto.getBarbeariaId());
        assertTrue(dto.isAtivo());
        assertEquals("CORTE", dto.getTipoServico());
    }

    @Test
    @DisplayName("Deve converter JpaServico para ServicoDto com tipo BARBA")
    void deveConverterJpaServicoParaDtoComTipoBarba() {
        // Arrange
        JpaServico jpaServico = new JpaServicoBarba();
        jpaServico.setId(2L);
        jpaServico.setNome("Barba Perfeita");
        jpaServico.setPreco(BigDecimal.valueOf(35.00));
        jpaServico.setDuracao(20);
        jpaServico.setBarbeariaId(1L);
        jpaServico.setAtivo(true);

        // Act
        ServicoDto dto = ServicoMapper.toDto(jpaServico);

        // Assert
        assertNotNull(dto);
        assertEquals("BARBA", dto.getTipoServico());
        assertEquals("Barba Perfeita", dto.getNome());
        assertEquals(BigDecimal.valueOf(35.00), dto.getPreco());
    }

    @Test
    @DisplayName("Deve converter JpaServico para subclasse correta de Servico - CORTE")
    void deveConverterJpaServicoParaServicoCorte() {
        // Arrange
        JpaServico jpaServico = new JpaServicoCorte();
        jpaServico.setId(1L);
        jpaServico.setNome("Corte Clássico");
        jpaServico.setDescricao("Corte tradicional");
        jpaServico.setPreco(BigDecimal.valueOf(50.00));
        jpaServico.setDuracao(30);
        jpaServico.setBarbeariaId(1L);
        jpaServico.setAtivo(true);
        jpaServico.setDataCriacao(agora);
        jpaServico.setDataAtualizacao(agora);

        // Act
        Servico servico = ServicoMapper.toDomain(jpaServico);

        // Assert
        assertNotNull(servico);
        assertInstanceOf(ServicoCorte.class, servico);
        assertEquals("CORTE", servico.getTipoServico());
        assertEquals("Corte Clássico", servico.getNome());
        assertEquals(BigDecimal.valueOf(50.00), servico.getPreco());
        assertEquals(1L, servico.getId());
    }

    @Test
    @DisplayName("Deve converter JpaServico para subclasse correta de Servico - BARBA")
    void deveConverterJpaServicoParaServicoBarba() {
        // Arrange
        JpaServico jpaServico = new JpaServicoBarba();
        jpaServico.setId(2L);
        jpaServico.setNome("Barba Premium");
        jpaServico.setPreco(BigDecimal.valueOf(45.00));
        jpaServico.setDuracao(25);
        jpaServico.setBarbeariaId(1L);
        jpaServico.setAtivo(true);

        // Act
        Servico servico = ServicoMapper.toDomain(jpaServico);

        // Assert
        assertNotNull(servico);
        assertInstanceOf(ServicoBarba.class, servico);
        assertEquals("BARBA", servico.getTipoServico());
    }

    @Test
    @DisplayName("Deve retornar null quando JpaServico é null no toDto")
    void deveRetornarNullQuandoJpaServicoEhNullNoToDto() {
        // Act
        ServicoDto dto = ServicoMapper.toDto(null);

        // Assert
        assertNull(dto);
    }

    @Test
    @DisplayName("Deve retornar null quando JpaServico é null no toDomain")
    void deveRetornarNullQuandoJpaServicoEhNullNoToDomain() {
        // Act
        Servico servico = ServicoMapper.toDomain(null);

        // Assert
        assertNull(servico);
    }

    @Test
    @DisplayName("Deve preservar todos os atributos na conversão para DTO")
    void devePreservarTodosAtributosNaConversaoParaDto() {
        // Arrange
        JpaServico jpaServico = new JpaServicoCorte();
        jpaServico.setId(5L);
        jpaServico.setNome("Corte Fade");
        jpaServico.setDescricao("Corte fade moderno");
        jpaServico.setPreco(BigDecimal.valueOf(60.00));
        jpaServico.setDuracao(40);
        jpaServico.setBarbeariaId(3L);
        jpaServico.setAtivo(false);

        // Act
        ServicoDto dto = ServicoMapper.toDto(jpaServico);

        // Assert
        assertEquals(5L, dto.getId());
        assertEquals("Corte Fade", dto.getNome());
        assertEquals("Corte fade moderno", dto.getDescricao());
        assertEquals(BigDecimal.valueOf(60.00), dto.getPreco());
        assertEquals(40, dto.getDuracao());
        assertEquals(3L, dto.getBarbeariaId());
        assertFalse(dto.isAtivo());
        assertEquals("CORTE", dto.getTipoServico());
    }

    @Test
    @DisplayName("Deve preservar todos os atributos na conversão para domínio")
    void devePreservarTodosAtributosNaConversaoParaDominio() {
        // Arrange
        JpaServico jpaServico = new JpaServicoCorte();
        jpaServico.setId(5L);
        jpaServico.setNome("Corte Fade");
        jpaServico.setDescricao("Corte fade moderno");
        jpaServico.setPreco(BigDecimal.valueOf(60.00));
        jpaServico.setDuracao(40);
        jpaServico.setBarbeariaId(3L);
        jpaServico.setAtivo(false);
        jpaServico.setDataCriacao(agora);
        jpaServico.setDataAtualizacao(agora);

        // Act
        Servico servico = ServicoMapper.toDomain(jpaServico);

        // Assert
        assertEquals(5L, servico.getId());
        assertEquals("Corte Fade", servico.getNome());
        assertEquals("Corte fade moderno", servico.getDescricao());
        assertEquals(BigDecimal.valueOf(60.00), servico.getPreco());
        assertEquals(40, servico.getDuracao());
        assertEquals(3L, servico.getBarbeariaId());
        assertFalse(servico.isAtivo());
        assertEquals(agora, servico.getDataCriacao());
        assertEquals(agora, servico.getDataAtualizacao());
    }
}
