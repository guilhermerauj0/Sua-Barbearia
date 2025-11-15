package com.barbearia.domain.entities;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes para as subclasses de Servico.
 * 
 * Testa o polimorfismo e a herança das subclasses de serviço.
 * Cada subclasse deve retornar seu tipo específico através do método getTipoServico().
 * 
 * Conceitos de POO testados:
 * - Herança: Todas as subclasses herdam de Servico
 * - Polimorfismo: Cada subclasse implementa getTipoServico() de forma diferente
 * - Abstração: Servico é uma classe abstrata
 */
@DisplayName("Subclasses de Servico - Testes de Polimorfismo")
class ServicoSubclassesTest {

    private LocalDateTime agora;

    @BeforeEach
    void setUp() {
        agora = LocalDateTime.now();
    }

    @Test
    @DisplayName("ServicoCorte deve retornar tipo CORTE")
    void servicoCorteDeveTipo() {
        // Arrange & Act
        ServicoCorte servico = new ServicoCorte(
                1L, "Corte Clássico", "Corte tradicional", BigDecimal.valueOf(50.00), 
                30, 1L, true, agora, agora
        );

        // Assert
        assertEquals("CORTE", servico.getTipoServico());
        assertEquals("Corte Clássico", servico.getNome());
        assertEquals(BigDecimal.valueOf(50.00), servico.getPreco());
    }

    @Test
    @DisplayName("ServicoBarba deve retornar tipo BARBA")
    void servicoBarbaTipo() {
        // Arrange & Act
        ServicoBarba servico = new ServicoBarba(
                2L, "Barba Perfeita", "Barba com navalha", BigDecimal.valueOf(35.00), 
                20, 1L, true, agora, agora
        );

        // Assert
        assertEquals("BARBA", servico.getTipoServico());
        assertEquals("Barba Perfeita", servico.getNome());
        assertEquals(BigDecimal.valueOf(35.00), servico.getPreco());
    }

    @Test
    @DisplayName("ServicoManicure deve retornar tipo MANICURE")
    void servicoManicureTipo() {
        // Arrange & Act
        ServicoManicure servico = new ServicoManicure(
                3L, "Manicure Completa", "Manicure com esmaltação", BigDecimal.valueOf(40.00), 
                45, 1L, true, agora, agora
        );

        // Assert
        assertEquals("MANICURE", servico.getTipoServico());
        assertEquals("Manicure Completa", servico.getNome());
    }

    @Test
    @DisplayName("ServicoSobrancelha deve retornar tipo SOBRANCELHA")
    void servicoSobracelhaTipo() {
        // Arrange & Act
        ServicoSobrancelha servico = new ServicoSobrancelha(
                4L, "Design de Sobrancelha", "Sobrancelha com design", BigDecimal.valueOf(25.00), 
                15, 1L, true, agora, agora
        );

        // Assert
        assertEquals("SOBRANCELHA", servico.getTipoServico());
        assertEquals("Design de Sobrancelha", servico.getNome());
    }

    @Test
    @DisplayName("ServicoColoracao deve retornar tipo COLORACAO")
    void servicoColoracaoTipo() {
        // Arrange & Act
        ServicoColoracao servico = new ServicoColoracao(
                5L, "Coloração Premium", "Coloração com produtos importados", BigDecimal.valueOf(120.00), 
                90, 1L, true, agora, agora
        );

        // Assert
        assertEquals("COLORACAO", servico.getTipoServico());
        assertEquals("Coloração Premium", servico.getNome());
    }

    @Test
    @DisplayName("ServicoTratamentoCapilar deve retornar tipo TRATAMENTO_CAPILAR")
    void servicoTratamentoCapilarTipo() {
        // Arrange & Act
        ServicoTratamentoCapilar servico = new ServicoTratamentoCapilar(
                6L, "Hidratação Profunda", "Hidratação com mask premium", BigDecimal.valueOf(60.00), 
                60, 1L, true, agora, agora
        );

        // Assert
        assertEquals("TRATAMENTO_CAPILAR", servico.getTipoServico());
        assertEquals("Hidratação Profunda", servico.getNome());
    }

    @Test
    @DisplayName("Deve testar polimorfismo com array de Servicos")
    void deveTestarPolimorfismoComArrayDeServicos() {
        // Arrange
        Servico[] servicos = new Servico[]{
                new ServicoCorte(1L, "Corte", "Desc", BigDecimal.valueOf(50), 30, 1L, true, agora, agora),
                new ServicoBarba(2L, "Barba", "Desc", BigDecimal.valueOf(35), 20, 1L, true, agora, agora),
                new ServicoManicure(3L, "Manicure", "Desc", BigDecimal.valueOf(40), 45, 1L, true, agora, agora),
                new ServicoSobrancelha(4L, "Sobrancelha", "Desc", BigDecimal.valueOf(25), 15, 1L, true, agora, agora),
                new ServicoColoracao(5L, "Coloração", "Desc", BigDecimal.valueOf(120), 90, 1L, true, agora, agora),
                new ServicoTratamentoCapilar(6L, "Tratamento", "Desc", BigDecimal.valueOf(60), 60, 1L, true, agora, agora)
        };

        String[] tiposEsperados = {"CORTE", "BARBA", "MANICURE", "SOBRANCELHA", "COLORACAO", "TRATAMENTO_CAPILAR"};

        // Act & Assert
        for (int i = 0; i < servicos.length; i++) {
            assertEquals(tiposEsperados[i], servicos[i].getTipoServico(),
                    "Tipo de serviço incorreto para índice " + i);
        }
    }

    @Test
    @DisplayName("Deve encapsular atributos corretamente")
    void deveEncapsulatarAtributosCorretamente() {
        // Arrange
        ServicoCorte servico = new ServicoCorte();
        BigDecimal preco = BigDecimal.valueOf(75.50);
        String nome = "Corte com Design";

        // Act
        servico.setNome(nome);
        servico.setPreco(preco);
        servico.setDuracao(40);
        servico.setBarbeariaId(2L);
        servico.setAtivo(true);

        // Assert
        assertEquals(nome, servico.getNome());
        assertEquals(preco, servico.getPreco());
        assertEquals(40, servico.getDuracao());
        assertEquals(2L, servico.getBarbeariaId());
        assertTrue(servico.isAtivo());
    }

    @Test
    @DisplayName("Construtor vazio deve criar instância válida")
    void construtorVazioDevelCriarInstanciaValida() {
        // Arrange & Act
        ServicoCorte servico = new ServicoCorte();

        // Assert
        assertNotNull(servico);
        assertEquals("CORTE", servico.getTipoServico());
        assertNull(servico.getId());
        assertNull(servico.getNome());
    }
}
