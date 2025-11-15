package com.barbearia.infrastructure.persistence.repositories;

import com.barbearia.infrastructure.persistence.entities.JpaProfissionalServico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositório para ProfissionalServico (relação many-to-many entre Funcionario e Servico).
 */
@Repository
public interface ProfissionalServicoRepository extends JpaRepository<JpaProfissionalServico, Long> {
    
    /**
     * Encontra todos os serviços de um profissional (apenas ativos).
     */
    @Query("SELECT ps FROM JpaProfissionalServico ps WHERE ps.funcionarioId = :funcionarioId AND ps.ativo = true")
    List<JpaProfissionalServico> findServicosByFuncionarioIdAtivo(@Param("funcionarioId") Long funcionarioId);
    
    /**
     * Encontra todos os profissionais que prestam um serviço específico (apenas ativos).
     */
    @Query("SELECT ps FROM JpaProfissionalServico ps WHERE ps.servicoId = :servicoId AND ps.ativo = true")
    List<JpaProfissionalServico> findFuncionariosByServicoIdAtivo(@Param("servicoId") Long servicoId);
    
    /**
     * Verifica se um profissional pode prestar um serviço específico.
     */
    @Query("SELECT COUNT(ps) > 0 FROM JpaProfissionalServico ps WHERE ps.funcionarioId = :funcionarioId AND ps.servicoId = :servicoId AND ps.ativo = true")
    boolean canPrestarServico(@Param("funcionarioId") Long funcionarioId, @Param("servicoId") Long servicoId);
    
    /**
     * Encontra a relação entre um profissional e um serviço específico (apenas ativos).
     */
    @Query("SELECT ps FROM JpaProfissionalServico ps WHERE ps.funcionarioId = :funcionarioId AND ps.servicoId = :servicoId AND ps.ativo = true")
    Optional<JpaProfissionalServico> findByFuncionarioIdAndServicoIdAtivo(@Param("funcionarioId") Long funcionarioId, @Param("servicoId") Long servicoId);
    
    /**
     * Encontra a relação entre um profissional e um serviço específico (ignora status ativo).
     */
    @Query("SELECT ps FROM JpaProfissionalServico ps WHERE ps.funcionarioId = :funcionarioId AND ps.servicoId = :servicoId")
    Optional<JpaProfissionalServico> findByFuncionarioIdAndServicoId(@Param("funcionarioId") Long funcionarioId, @Param("servicoId") Long servicoId);
}
