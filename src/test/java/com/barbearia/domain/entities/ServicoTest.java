package com.barbearia.domain.entities;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes para a hierarquia de classes de Servico.
 * 
 * Testa os conceitos de POO:
 * - Abstração: Verifica que Servico não pode ser instanciada
 * - Herança: Verifica que as subclasses herdam corretamente
 * - Polimorfismo: Verifica que getTipoServico() retorna o tipo correto para cada subclasse
 * 
 * @author Sua Barbearia Team
 */
@DisplayName("Testes da Hierarquia de Servicos")
class ServicoTest {
    
    @Test
    @DisplayName("ServicoCorte deve retornar tipo CORTE")
    void testServicoCorteGetTipoServico() {
        ServicoCorte servico = new ServicoCorte(
            1L, "Corte Simples", "Corte padrão", 
            BigDecimal.valueOf(50.0), 30, 1L, true, 
            LocalDateTime.now(), LocalDateTime.now()
        );
        
        assertEquals("CORTE", servico.getTipoServico());
    }
    
    @Test
    @DisplayName("ServicoBarba deve retornar tipo BARBA")
    void testServicoBarbaGetTipoServico() {
        ServicoBarba servico = new ServicoBarba(
            2L, "Barba Completa", "Barba e contorno", 
            BigDecimal.valueOf(40.0), 25, 1L, true, 
            LocalDateTime.now(), LocalDateTime.now()
        );
        
        assertEquals("BARBA", servico.getTipoServico());
    }
    
    @Test
    @DisplayName("ServicoManicure deve retornar tipo MANICURE")
    void testServicoManicureGetTipoServico() {
        ServicoManicure servico = new ServicoManicure(
            3L, "Manicure", "Unhas das mãos", 
            BigDecimal.valueOf(30.0), 45, 1L, true, 
            LocalDateTime.now(), LocalDateTime.now()
        );
        
        assertEquals("MANICURE", servico.getTipoServico());
    }
    
    @Test
    @DisplayName("ServicoSobrancelha deve retornar tipo SOBRANCELHA")
    void testServicoSobrancelhaGetTipoServico() {
        ServicoSobrancelha servico = new ServicoSobrancelha(
            4L, "Sobrancelha", "Design de sobrancelha", 
            BigDecimal.valueOf(25.0), 20, 1L, true, 
            LocalDateTime.now(), LocalDateTime.now()
        );
        
        assertEquals("SOBRANCELHA", servico.getTipoServico());
    }
    
    @Test
    @DisplayName("ServicoColoracao deve retornar tipo COLORACAO")
    void testServicoColoracaoGetTipoServico() {
        ServicoColoracao servico = new ServicoColoracao(
            5L, "Coloração", "Coloração completa", 
            BigDecimal.valueOf(80.0), 90, 1L, true, 
            LocalDateTime.now(), LocalDateTime.now()
        );
        
        assertEquals("COLORACAO", servico.getTipoServico());
    }
    
    @Test
    @DisplayName("ServicoTratamentoCapilar deve retornar tipo TRATAMENTO_CAPILAR")
    void testServicoTratamentoCapilarGetTipoServico() {
        ServicoTratamentoCapilar servico = new ServicoTratamentoCapilar(
            6L, "Tratamento Capilar", "Hidratação profunda", 
            BigDecimal.valueOf(60.0), 60, 1L, true, 
            LocalDateTime.now(), LocalDateTime.now()
        );
        
        assertEquals("TRATAMENTO_CAPILAR", servico.getTipoServico());
    }
    
    @Test
    @DisplayName("Todas as subclasses devem herdar atributos da classe Servico")
    void testHerancaAtributos() {
        ServicoCorte servico = new ServicoCorte(
            1L, "Corte", "Descrição", 
            BigDecimal.valueOf(50.0), 30, 1L, true, 
            LocalDateTime.now(), LocalDateTime.now()
        );
        
        assertEquals(1L, servico.getId());
        assertEquals("Corte", servico.getNome());
        assertEquals("Descrição", servico.getDescricao());
        assertEquals(BigDecimal.valueOf(50.0), servico.getPreco());
        assertEquals(30, servico.getDuracao());
        assertEquals(1L, servico.getBarbeariaId());
        assertTrue(servico.isAtivo());
    }
    
    @Test
    @DisplayName("Polimorfismo: Diferentes tipos de servico implementam getTipoServico diferentemente")
    void testPolimorfismo() {
        Servico[] servicos = {
            new ServicoCorte(1L, "Corte", "", BigDecimal.ONE, 30, 1L, true, LocalDateTime.now(), LocalDateTime.now()),
            new ServicoBarba(2L, "Barba", "", BigDecimal.ONE, 25, 1L, true, LocalDateTime.now(), LocalDateTime.now()),
            new ServicoManicure(3L, "Manicure", "", BigDecimal.ONE, 45, 1L, true, LocalDateTime.now(), LocalDateTime.now()),
            new ServicoSobrancelha(4L, "Sobrancelha", "", BigDecimal.ONE, 20, 1L, true, LocalDateTime.now(), LocalDateTime.now()),
            new ServicoColoracao(5L, "Coloracao", "", BigDecimal.ONE, 90, 1L, true, LocalDateTime.now(), LocalDateTime.now()),
            new ServicoTratamentoCapilar(6L, "Tratamento", "", BigDecimal.ONE, 60, 1L, true, LocalDateTime.now(), LocalDateTime.now())
        };
        
        String[] tiposEsperados = {"CORTE", "BARBA", "MANICURE", "SOBRANCELHA", "COLORACAO", "TRATAMENTO_CAPILAR"};
        
        for (int i = 0; i < servicos.length; i++) {
            assertEquals(tiposEsperados[i], servicos[i].getTipoServico());
        }
    }
}
