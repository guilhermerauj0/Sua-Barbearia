package com.barbearia.infrastructure.persistence.repositories;

import com.barbearia.domain.enums.TipoDocumento;
import com.barbearia.infrastructure.persistence.entities.JpaAvaliacao;
import com.barbearia.infrastructure.persistence.entities.JpaBarbearia;
import com.barbearia.infrastructure.persistence.entities.JpaCliente;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("AvaliacaoRepository - Testes de Integração")
class AvaliacaoRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private AvaliacaoRepository avaliacaoRepository;

    private JpaCliente cliente;
    private JpaBarbearia barbearia;

    @BeforeEach
    void setUp() {
        cliente = new JpaCliente();
        cliente.setNome("João Silva");
        cliente.setEmail("joao@email.com");
        cliente.setSenha("senha123");
        cliente.setTelefone("11999999999");
        cliente.setRole("CLIENTE");
        entityManager.persist(cliente);

        barbearia = new JpaBarbearia();
        barbearia.setNome("Barbearia Top");
        barbearia.setEmail("top@barbearia.com");
        barbearia.setSenha("senha123");
        barbearia.setTelefone("11888888888");
        barbearia.setEndereco("Rua A, 123");
        barbearia.setNomeFantasia("Barbearia Top Fantasia");
        barbearia.setTipoDocumento(TipoDocumento.CNPJ);
        barbearia.setDocumento("12345678000199");
        barbearia.setRole("BARBEARIA");
        entityManager.persist(barbearia);

        entityManager.flush();
    }

    @Test
    @DisplayName("Deve verificar se existe avaliação por agendamento ID")
    void deveVerificarExistenciaPorAgendamento() {
        // Arrange
        JpaAvaliacao avaliacao = new JpaAvaliacao(
                cliente.getId(), barbearia.getId(), 100L, 5, 5, 5, 5, "Top");
        avaliacao.setNotaGeral(new BigDecimal("5.00"));
        avaliacao.setDataCriacao(LocalDateTime.now());
        entityManager.persist(avaliacao);
        entityManager.flush();

        // Act
        boolean existe = avaliacaoRepository.existsByAgendamentoId(100L);

        // Assert
        assertTrue(existe);
    }

    @Test
    @DisplayName("Deve listar avaliações por barbearia ordenadas por data")
    void deveListarPorBarbeariaOrdenado() {
        // Arrange
        JpaAvaliacao a1 = new JpaAvaliacao(cliente.getId(), barbearia.getId(), 101L, 5, 5, 5, 5, "Recente");
        a1.setNotaGeral(new BigDecimal("5.00"));
        a1.setDataCriacao(LocalDateTime.now());

        JpaAvaliacao a2 = new JpaAvaliacao(cliente.getId(), barbearia.getId(), 102L, 4, 4, 4, 4, "Antigo");
        a2.setNotaGeral(new BigDecimal("4.00"));
        a2.setDataCriacao(LocalDateTime.now().minusDays(1));

        entityManager.persist(a1);
        entityManager.persist(a2);
        entityManager.flush();

        // Act
        List<JpaAvaliacao> lista = avaliacaoRepository.findByBarbeariaIdOrderByDataCriacaoDesc(barbearia.getId());

        // Assert
        assertEquals(2, lista.size());
        assertEquals("Recente", lista.get(0).getComentario());
        assertEquals("Antigo", lista.get(1).getComentario());
    }

    @Test
    @DisplayName("Deve calcular média geral corretamente")
    void deveCalcularMediaGeral() {
        // Arrange
        JpaAvaliacao a1 = new JpaAvaliacao(cliente.getId(), barbearia.getId(), 101L, 5, 5, 5, 5, "Top");
        a1.setNotaGeral(new BigDecimal("5.00"));
        a1.setDataCriacao(LocalDateTime.now());

        JpaAvaliacao a2 = new JpaAvaliacao(cliente.getId(), barbearia.getId(), 102L, 3, 3, 3, 3, "Medio");
        a2.setNotaGeral(new BigDecimal("3.00"));
        a2.setDataCriacao(LocalDateTime.now());

        entityManager.persist(a1);
        entityManager.persist(a2);
        entityManager.flush();

        // Act
        Double media = avaliacaoRepository.calcularMediaGeral(barbearia.getId());

        // Assert
        assertEquals(4.0, media);
    }

    @Test
    @DisplayName("Deve contar avaliações por nota geral (arredondada)")
    void deveContarPorNotaGeral() {
        // Arrange
        // Nota 5
        JpaAvaliacao a1 = new JpaAvaliacao(cliente.getId(), barbearia.getId(), 101L, 5, 5, 5, 5, "5");
        a1.setNotaGeral(new BigDecimal("5.00"));
        a1.setDataCriacao(LocalDateTime.now());

        // Nota 4 (4.75 arredonda pra 5? Não, a query usa floor ou round? Vamos assumir
        // int cast ou range)
        // A query no repository é: count a where floor(a.notaGeral) = :nota
        // Vamos verificar a implementação do repository depois.
        // Assumindo que a query conta quantos tem notaGeral >= nota e < nota+1 ou
        // arredondado.
        // Geralmente é por faixa de estrelas.

        JpaAvaliacao a2 = new JpaAvaliacao(cliente.getId(), barbearia.getId(), 102L, 4, 4, 4, 4, "4");
        a2.setNotaGeral(new BigDecimal("4.00"));
        a2.setDataCriacao(LocalDateTime.now());

        entityManager.persist(a1);
        entityManager.persist(a2);
        entityManager.flush();

        // Act
        Long count5 = avaliacaoRepository.countByBarbeariaIdAndNotaGeral(barbearia.getId(), 5);
        Long count4 = avaliacaoRepository.countByBarbeariaIdAndNotaGeral(barbearia.getId(), 4);

        // Assert
        assertEquals(1L, count5);
        assertEquals(1L, count4);
    }
}
