package com.barbearia.infrastructure.persistence.repositories;

import com.barbearia.domain.enums.TipoDocumento;
import com.barbearia.infrastructure.persistence.entities.JpaBarbearia;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes de integração para BarbeariaRepository.
 * 
 * Testa operações de banco de dados e consultas personalizadas.
 */
@SuppressWarnings("null")
@DataJpaTest
@ActiveProfiles("test")
@DisplayName("BarbeariaRepository - Testes de Integração")
class BarbeariaRepositoryTest {

    @Autowired
    private BarbeariaRepository barbeariaRepository;

    private JpaBarbearia barbeariaCPF;
    private JpaBarbearia barbeariaCNPJ;

    @BeforeEach
    void setUp() {
        barbeariaRepository.deleteAll();

        // Barbearia com CPF
        barbeariaCPF = new JpaBarbearia();
        barbeariaCPF.setNome("Maria Santos");
        barbeariaCPF.setEmail("maria.santos@email.com");
        barbeariaCPF.setSenha("senhaHasheada123");
        barbeariaCPF.setTelefone("11987654321");
        barbeariaCPF.setNomeFantasia("Barbearia Elegance");
        barbeariaCPF.setTipoDocumento(TipoDocumento.CPF);
        barbeariaCPF.setDocumento("12345678909");
        barbeariaCPF.setEndereco("Rua das Flores, 123 - São Paulo/SP");
        barbeariaCPF.setAtivo(true);

        // Barbearia com CNPJ
        barbeariaCNPJ = new JpaBarbearia();
        barbeariaCNPJ.setNome("Carlos Oliveira");
        barbeariaCNPJ.setEmail("carlos.oliveira@email.com");
        barbeariaCNPJ.setSenha("senhaHasheada456");
        barbeariaCNPJ.setTelefone("21998765432");
        barbeariaCNPJ.setNomeFantasia("Barbearia Premium");
        barbeariaCNPJ.setTipoDocumento(TipoDocumento.CNPJ);
        barbeariaCNPJ.setDocumento("11222333000181");
        barbeariaCNPJ.setEndereco("Av. Principal, 500 - Rio de Janeiro/RJ");
        barbeariaCNPJ.setAtivo(true);
    }

    @Test
    @DisplayName("Deve salvar barbearia com CPF no banco de dados")
    void deveSalvarBarbeariaComCPF() {
        // Act
        JpaBarbearia saved = barbeariaRepository.save(barbeariaCPF);

        // Assert
        assertNotNull(saved.getId());
        assertEquals("Maria Santos", saved.getNome());
        assertEquals("maria.santos@email.com", saved.getEmail());
        assertEquals(TipoDocumento.CPF, saved.getTipoDocumento());
        assertEquals("12345678909", saved.getDocumento());
        assertNotNull(saved.getDataCriacao());
    }

    @Test
    @DisplayName("Deve salvar barbearia com CNPJ no banco de dados")
    void deveSalvarBarbeariaComCNPJ() {
        // Act
        JpaBarbearia saved = barbeariaRepository.save(barbeariaCNPJ);

        // Assert
        assertNotNull(saved.getId());
        assertEquals("Carlos Oliveira", saved.getNome());
        assertEquals("carlos.oliveira@email.com", saved.getEmail());
        assertEquals(TipoDocumento.CNPJ, saved.getTipoDocumento());
        assertEquals("11222333000181", saved.getDocumento());
    }

    @Test
    @DisplayName("Deve encontrar barbearia por email")
    void deveEncontrarBarbeariaPorEmail() {
        // Arrange
        barbeariaRepository.save(barbeariaCPF);

        // Act
        Optional<JpaBarbearia> found = barbeariaRepository.findByEmail("maria.santos@email.com");

        // Assert
        assertTrue(found.isPresent());
        assertEquals("Maria Santos", found.get().getNome());
        assertEquals("Barbearia Elegance", found.get().getNomeFantasia());
    }

    @Test
    @DisplayName("Deve retornar vazio quando email não existe")
    void deveRetornarVazioQuandoEmailNaoExiste() {
        // Act
        Optional<JpaBarbearia> found = barbeariaRepository.findByEmail("naoexiste@email.com");

        // Assert
        assertFalse(found.isPresent());
    }

    @Test
    @DisplayName("Deve verificar se email existe")
    void deveVerificarSeEmailExiste() {
        // Arrange
        barbeariaRepository.save(barbeariaCPF);

        // Act & Assert
        assertTrue(barbeariaRepository.existsByEmail("maria.santos@email.com"));
        assertFalse(barbeariaRepository.existsByEmail("naoexiste@email.com"));
    }

    @Test
    @DisplayName("Deve encontrar barbearia por tipo de documento e documento")
    void deveEncontrarBarbeariaPorTipoDocumentoEDocumento() {
        // Arrange
        barbeariaRepository.save(barbeariaCPF);

        // Act
        Optional<JpaBarbearia> found = barbeariaRepository.findByTipoDocumentoAndDocumento(
            TipoDocumento.CPF, 
            "12345678909"
        );

        // Assert
        assertTrue(found.isPresent());
        assertEquals("Maria Santos", found.get().getNome());
    }

    @Test
    @DisplayName("Deve verificar se CPF existe")
    void deveVerificarSeCPFExiste() {
        // Arrange
        barbeariaRepository.save(barbeariaCPF);

        // Act & Assert
        assertTrue(barbeariaRepository.existsByTipoDocumentoAndDocumento(TipoDocumento.CPF, "12345678909"));
        assertFalse(barbeariaRepository.existsByTipoDocumentoAndDocumento(TipoDocumento.CPF, "98765432100"));
    }

    @Test
    @DisplayName("Deve verificar se CNPJ existe")
    void deveVerificarSeCNPJExiste() {
        // Arrange
        barbeariaRepository.save(barbeariaCNPJ);

        // Act & Assert
        assertTrue(barbeariaRepository.existsByTipoDocumentoAndDocumento(TipoDocumento.CNPJ, "11222333000181"));
        assertFalse(barbeariaRepository.existsByTipoDocumentoAndDocumento(TipoDocumento.CNPJ, "99888777000100"));
    }

    @Test
    @DisplayName("Não deve encontrar CPF quando buscar com CNPJ")
    void naoDeveEncontrarCPFQuandoBuscarComCNPJ() {
        // Arrange
        barbeariaRepository.save(barbeariaCPF);

        // Act
        Optional<JpaBarbearia> found = barbeariaRepository.findByTipoDocumentoAndDocumento(
            TipoDocumento.CNPJ, 
            "12345678909"
        );

        // Assert
        assertFalse(found.isPresent());
    }

    @Test
    @DisplayName("Deve encontrar barbearia por email e ativo true")
    void deveEncontrarBarbeariaPorEmailEAtivoTrue() {
        // Arrange
        barbeariaRepository.save(barbeariaCPF);

        // Act
        Optional<JpaBarbearia> found = barbeariaRepository.findByEmailAndAtivoTrue("maria.santos@email.com");

        // Assert
        assertTrue(found.isPresent());
        assertTrue(found.get().isAtivo());
    }

    @Test
    @DisplayName("Não deve encontrar barbearia inativa por email")
    void naoDeveEncontrarBarbeariaInativaPorEmail() {
        // Arrange
        barbeariaCPF.setAtivo(false);
        barbeariaRepository.save(barbeariaCPF);

        // Act
        Optional<JpaBarbearia> found = barbeariaRepository.findByEmailAndAtivoTrue("maria.santos@email.com");

        // Assert
        assertFalse(found.isPresent());
    }

    @Test
    @DisplayName("Deve garantir constraint unique em tipo_documento e documento")
    void deveGarantirConstraintUniqueEmTipoDocumentoEDocumento() {
        // Arrange
        barbeariaRepository.save(barbeariaCPF);

        JpaBarbearia duplicada = new JpaBarbearia();
        duplicada.setNome("Outro Nome");
        duplicada.setEmail("outro@email.com");
        duplicada.setSenha("outraSenha");
        duplicada.setTelefone("11999999999");
        duplicada.setNomeFantasia("Outra Barbearia");
        duplicada.setTipoDocumento(TipoDocumento.CPF);
        duplicada.setDocumento("12345678909"); // Mesmo CPF
        duplicada.setEndereco("Outro Endereço");
        duplicada.setAtivo(true);

        // Act & Assert
        assertThrows(Exception.class, () -> {
            barbeariaRepository.save(duplicada);
            barbeariaRepository.flush();
        });
    }

    @Test
    @DisplayName("Deve permitir mesmo documento com tipos diferentes")
    void devePermitirMesmoDocumentoComTiposDiferentes() {
        // Arrange
        barbeariaRepository.save(barbeariaCPF);

        JpaBarbearia outra = new JpaBarbearia();
        outra.setNome("Outro Nome");
        outra.setEmail("outro@email.com");
        outra.setSenha("outraSenha");
        outra.setTelefone("11999999999");
        outra.setNomeFantasia("Outra Barbearia");
        outra.setTipoDocumento(TipoDocumento.CNPJ);
        outra.setDocumento("12345678909"); // Mesmo número mas tipo diferente
        outra.setEndereco("Outro Endereço");
        outra.setAtivo(true);

        // Act & Assert - Não deve lançar exceção
        assertDoesNotThrow(() -> {
            barbeariaRepository.save(outra);
            barbeariaRepository.flush();
        });
    }
}
