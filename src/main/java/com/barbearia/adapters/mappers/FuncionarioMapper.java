package com.barbearia.adapters.mappers;

import com.barbearia.application.dto.FuncionarioRequestDto;
import com.barbearia.application.dto.FuncionarioResponseDto;
import com.barbearia.domain.entities.*;
import com.barbearia.infrastructure.persistence.entities.*;
import org.springframework.stereotype.Component;

/**
 * Mapper para conversão entre diferentes representações de Funcionario.
 * 
 * Suporta conversão polimórfica de todos os tipos de profissionais:
 * - BARBEIRO -> FuncionarioBarbeiro / JpaFuncionarioBarbeiro
 * - MANICURE -> FuncionarioManicure / JpaFuncionarioManicure
 * - ESTETICISTA -> FuncionarioEsteticista / JpaFuncionarioEsteticista
 * - COLORISTA -> FuncionarioColorista / JpaFuncionarioColorista
 * 
 * @author Sua Barbearia Team
 */
@Component
public class FuncionarioMapper {
    
    /**
     * Converte JpaFuncionario (do banco) para Funcionario de domínio.
     * Realiza conversão polimórfica baseada na profissão.
     */
    public static Funcionario toDomain(JpaFuncionario jpaFuncionario) {
        if (jpaFuncionario == null) {
            return null;
        }
        
        Funcionario funcionario = null;
        
        if (jpaFuncionario instanceof JpaFuncionarioBarbeiro) {
            funcionario = new FuncionarioBarbeiro(
                jpaFuncionario.getId(),
                jpaFuncionario.getNome(),
                jpaFuncionario.getEmail(),
                jpaFuncionario.getTelefone(),
                jpaFuncionario.getBarbeariaId(),
                jpaFuncionario.isAtivo()
            );
        } else if (jpaFuncionario instanceof JpaFuncionarioManicure) {
            funcionario = new FuncionarioManicure(
                jpaFuncionario.getId(),
                jpaFuncionario.getNome(),
                jpaFuncionario.getEmail(),
                jpaFuncionario.getTelefone(),
                jpaFuncionario.getBarbeariaId(),
                jpaFuncionario.isAtivo()
            );
        } else if (jpaFuncionario instanceof JpaFuncionarioEsteticista) {
            funcionario = new FuncionarioEsteticista(
                jpaFuncionario.getId(),
                jpaFuncionario.getNome(),
                jpaFuncionario.getEmail(),
                jpaFuncionario.getTelefone(),
                jpaFuncionario.getBarbeariaId(),
                jpaFuncionario.isAtivo()
            );
        } else if (jpaFuncionario instanceof JpaFuncionarioColorista) {
            funcionario = new FuncionarioColorista(
                jpaFuncionario.getId(),
                jpaFuncionario.getNome(),
                jpaFuncionario.getEmail(),
                jpaFuncionario.getTelefone(),
                jpaFuncionario.getBarbeariaId(),
                jpaFuncionario.isAtivo()
            );
        }
        
        if (funcionario != null && jpaFuncionario.getDataCriacao() != null) {
            funcionario.setDataCriacao(jpaFuncionario.getDataCriacao());
        }
        if (funcionario != null && jpaFuncionario.getDataAtualizacao() != null) {
            funcionario.setDataAtualizacao(jpaFuncionario.getDataAtualizacao());
        }
        
        return funcionario;
    }
    
    /**
     * Converte Funcionario de domínio para JpaFuncionario.
     * Realiza conversão polimórfica baseada no tipo de profissional.
     */
    public static JpaFuncionario toJpaEntity(Funcionario funcionario) {
        if (funcionario == null) {
            return null;
        }
        
        JpaFuncionario jpaFuncionario = null;
        
        if (funcionario instanceof FuncionarioBarbeiro) {
            jpaFuncionario = new JpaFuncionarioBarbeiro();
        } else if (funcionario instanceof FuncionarioManicure) {
            jpaFuncionario = new JpaFuncionarioManicure();
        } else if (funcionario instanceof FuncionarioEsteticista) {
            jpaFuncionario = new JpaFuncionarioEsteticista();
        } else if (funcionario instanceof FuncionarioColorista) {
            jpaFuncionario = new JpaFuncionarioColorista();
        }
        
        if (jpaFuncionario != null) {
            jpaFuncionario.setId(funcionario.getId());
            jpaFuncionario.setNome(funcionario.getNome());
            jpaFuncionario.setEmail(funcionario.getEmail());
            jpaFuncionario.setTelefone(funcionario.getTelefone());
            jpaFuncionario.setBarbeariaId(funcionario.getBarbeariaId());
            jpaFuncionario.setAtivo(funcionario.isAtivo());
            if (funcionario.getDataCriacao() != null) {
                jpaFuncionario.setDataCriacao(funcionario.getDataCriacao());
            }
            if (funcionario.getDataAtualizacao() != null) {
                jpaFuncionario.setDataAtualizacao(funcionario.getDataAtualizacao());
            }
        }
        
        return jpaFuncionario;
    }

    /**
     * Converte JpaFuncionario para FuncionarioResponseDto.
     */
    public FuncionarioResponseDto toResponseDto(JpaFuncionario jpaFuncionario) {
        if (jpaFuncionario == null) {
            return null;
        }

        return new FuncionarioResponseDto(
            jpaFuncionario.getId(),
            jpaFuncionario.getBarbeariaId(),
            jpaFuncionario.getNome(),
            jpaFuncionario.getEmail(),
            jpaFuncionario.getTelefone(),
            jpaFuncionario.getProfissao(),
            jpaFuncionario.isAtivo(),
            jpaFuncionario.getDataCriacao(),
            jpaFuncionario.getDataAtualizacao()
        );
    }

    /**
     * Cria uma instância de JpaFuncionario baseada no tipo de profissão.
     */
    public JpaFuncionario toEntityFromDto(FuncionarioRequestDto dto, Long barbeariaId) {
        if (dto == null) {
            return null;
        }

        JpaFuncionario funcionario = createFuncionarioByProfissao(dto.profissao());
        
        funcionario.setBarbeariaId(barbeariaId);
        funcionario.setNome(dto.nome());
        funcionario.setEmail(dto.email());
        funcionario.setTelefone(dto.telefone());
        funcionario.setAtivo(true);

        return funcionario;
    }

    /**
     * Cria a instância correta de funcionário baseada na profissão.
     */
    private JpaFuncionario createFuncionarioByProfissao(String profissao) {
        return switch (profissao.toUpperCase()) {
            case "BARBEIRO" -> new JpaFuncionarioBarbeiro();
            case "MANICURE" -> new JpaFuncionarioManicure();
            case "ESTETICISTA" -> new JpaFuncionarioEsteticista();
            case "COLORISTA" -> new JpaFuncionarioColorista();
            default -> throw new IllegalArgumentException("Profissão inválida: " + profissao + ". Valores aceitos: BARBEIRO, MANICURE, ESTETICISTA, COLORISTA");
        };
    }
}
