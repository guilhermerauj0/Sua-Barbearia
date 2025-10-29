package com.barbearia.infrastructure.persistence.repositories;

import com.barbearia.infrastructure.persistence.entities.JpaCliente;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Testes de integração para ClienteRepository usando banco H2 em memória.
 * Testa as operações de persistência sem mockar o banco.
 */
@DataJpaTest
@ActiveProfiles("test")
@DisplayName("ClienteRepository - Testes de Integração")
class ClienteRepositoryTest {

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private TestEntityManager entityManager;

    private JpaCliente clienteTeste;

    @BeforeEach
    void setUp() {
        clienteTeste = new JpaCliente();
        clienteTeste.setNome("João Silva");
        clienteTeste.setEmail("joao@email.com");
        clienteTeste.setSenha("$2a$10$hashedPassword");
        clienteTeste.setTelefone("11987654321");
        clienteTeste.setRole("CLIENTE");
        clienteTeste.setAtivo(true);
    }

    @Test
    @DisplayName("Deve salvar cliente no banco de dados")
    void deveSalvarCliente() {
        // Act
        JpaCliente clienteSalvo = clienteRepository.save(clienteTeste);

        // Assert
        assertThat(clienteSalvo.getId()).isNotNull();
        assertThat(clienteSalvo.getNome()).isEqualTo("João Silva");
        assertThat(clienteSalvo.getEmail()).isEqualTo("joao@email.com");
        assertThat(clienteSalvo.getTelefone()).isEqualTo("11987654321");
        assertThat(clienteSalvo.isAtivo()).isTrue();
    }

    @Test
    @DisplayName("Deve buscar cliente por email")
    void deveBuscarClientePorEmail() {
        // Arrange
        entityManager.persist(clienteTeste);
        entityManager.flush();

        // Act
        Optional<JpaCliente> clienteEncontrado = clienteRepository.findByEmail("joao@email.com");

        // Assert
        assertThat(clienteEncontrado).isPresent();
        assertThat(clienteEncontrado.get().getNome()).isEqualTo("João Silva");
        assertThat(clienteEncontrado.get().getEmail()).isEqualTo("joao@email.com");
    }

    @Test
    @DisplayName("Deve retornar vazio quando email não existe")
    void deveRetornarVazioQuandoEmailNaoExiste() {
        // Act
        Optional<JpaCliente> clienteEncontrado = clienteRepository.findByEmail("naoexiste@email.com");

        // Assert
        assertThat(clienteEncontrado).isEmpty();
    }

    @Test
    @DisplayName("Deve verificar se email existe")
    void deveVerificarSeEmailExiste() {
        // Arrange
        entityManager.persist(clienteTeste);
        entityManager.flush();

        // Act
        boolean existe = clienteRepository.existsByEmail("joao@email.com");

        // Assert
        assertThat(existe).isTrue();
    }

    @Test
    @DisplayName("Deve retornar false quando email não existe")
    void deveRetornarFalseQuandoEmailNaoExiste() {
        // Act
        boolean existe = clienteRepository.existsByEmail("naoexiste@email.com");

        // Assert
        assertThat(existe).isFalse();
    }

    @Test
    @DisplayName("Deve buscar cliente por telefone")
    void deveBuscarClientePorTelefone() {
        // Arrange
        entityManager.persist(clienteTeste);
        entityManager.flush();

        // Act
        Optional<JpaCliente> clienteEncontrado = clienteRepository.findByTelefone("11987654321");

        // Assert
        assertThat(clienteEncontrado).isPresent();
        assertThat(clienteEncontrado.get().getTelefone()).isEqualTo("11987654321");
    }

    @Test
    @DisplayName("Deve verificar se telefone existe")
    void deveVerificarSeTelefoneExiste() {
        // Arrange
        entityManager.persist(clienteTeste);
        entityManager.flush();

        // Act
        boolean existe = clienteRepository.existsByTelefone("11987654321");

        // Assert
        assertThat(existe).isTrue();
    }

    @Test
    @DisplayName("Deve buscar apenas clientes ativos por email")
    void deveBuscarApenasClientesAtivosPorEmail() {
        // Arrange
        entityManager.persist(clienteTeste);
        
        JpaCliente clienteInativo = new JpaCliente();
        clienteInativo.setNome("Maria Costa");
        clienteInativo.setEmail("maria@email.com");
        clienteInativo.setSenha("$2a$10$hashedPassword2");
        clienteInativo.setTelefone("11876543210");
        clienteInativo.setRole("CLIENTE");
        clienteInativo.setAtivo(false);
        entityManager.persist(clienteInativo);
        entityManager.flush();

        // Act
        Optional<JpaCliente> clienteAtivoEncontrado = clienteRepository.findByEmailAndAtivoTrue("joao@email.com");
        Optional<JpaCliente> clienteInativoNaoEncontrado = clienteRepository.findByEmailAndAtivoTrue("maria@email.com");

        // Assert
        assertThat(clienteAtivoEncontrado).isPresent();
        assertThat(clienteAtivoEncontrado.get().isAtivo()).isTrue();
        assertThat(clienteInativoNaoEncontrado).isEmpty();
    }

    @Test
    @DisplayName("Deve atualizar cliente existente")
    void deveAtualizarCliente() {
        // Arrange
        JpaCliente clienteSalvo = entityManager.persist(clienteTeste);
        entityManager.flush();
        entityManager.clear();

        // Act
        clienteSalvo.setNome("João Silva Atualizado");
        clienteSalvo.setTelefone("11999998888");
        JpaCliente clienteAtualizado = clienteRepository.save(clienteSalvo);

        // Assert
        assertThat(clienteAtualizado.getId()).isEqualTo(clienteSalvo.getId());
        assertThat(clienteAtualizado.getNome()).isEqualTo("João Silva Atualizado");
        assertThat(clienteAtualizado.getTelefone()).isEqualTo("11999998888");
    }

    @Test
    @DisplayName("Deve deletar cliente")
    void deveDeletarCliente() {
        // Arrange
        JpaCliente clienteSalvo = entityManager.persist(clienteTeste);
        entityManager.flush();
        Long clienteId = clienteSalvo.getId();

        // Act
        clienteRepository.delete(clienteSalvo);
        entityManager.flush();

        // Assert
        Optional<JpaCliente> clienteDeletado = clienteRepository.findById(clienteId);
        assertThat(clienteDeletado).isEmpty();
    }

    @Test
    @DisplayName("Deve garantir unicidade de email")
    void deveGarantirUnicidadeDeEmail() {
        // Arrange
        entityManager.persist(clienteTeste);
        entityManager.flush();

        JpaCliente clienteDuplicado = new JpaCliente();
        clienteDuplicado.setNome("Pedro Santos");
        clienteDuplicado.setEmail("joao@email.com"); // email duplicado
        clienteDuplicado.setSenha("$2a$10$hashedPassword3");
        clienteDuplicado.setTelefone("11765432109");
        clienteDuplicado.setRole("CLIENTE");
        clienteDuplicado.setAtivo(true);

        // Act & Assert - Verifica se lança exceção ao tentar inserir email duplicado
        assertThatThrownBy(() -> {
            entityManager.persist(clienteDuplicado);
            entityManager.flush();
        }).isInstanceOf(Exception.class);
    }

    @Test
    @DisplayName("Deve preencher data de criação automaticamente")
    void devePreencherDataCriacaoAutomaticamente() {
        // Act
        JpaCliente clienteSalvo = clienteRepository.save(clienteTeste);

        // Assert
        assertThat(clienteSalvo.getDataCriacao()).isNotNull();
    }

    @Test
    @DisplayName("Deve atualizar data de modificação ao alterar dados")
    void deveAtualizarDataModificacao() throws InterruptedException {
        // Arrange
        JpaCliente clienteSalvo = clienteRepository.save(clienteTeste);
        entityManager.flush();
        entityManager.clear();
        
        Thread.sleep(100); // Pequeno delay para garantir diferença de timestamp

        // Act
        clienteSalvo.setNome("Nome Atualizado");
        JpaCliente clienteAtualizado = clienteRepository.save(clienteSalvo);
        entityManager.flush();

        // Assert
        assertThat(clienteAtualizado.getDataAtualizacao()).isNotNull();
        assertThat(clienteAtualizado.getDataAtualizacao())
                .isAfterOrEqualTo(clienteAtualizado.getDataCriacao());
    }
}
