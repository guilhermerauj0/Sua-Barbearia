package com.barbearia.adapters.mappers;

import com.barbearia.application.dto.FuncionarioRequestDto;
import com.barbearia.application.dto.FuncionarioResponseDto;
import com.barbearia.domain.entities.*;
import com.barbearia.infrastructure.persistence.entities.*;
import org.springframework.stereotype.Component;

/**
 * Mapper para conversão entre diferentes representações de Funcionario.
 * 
 * Usa composição com perfis ao invés de herança de subclasses.
 * 
 * @author Sua Barbearia Team
 */
@Component
public class FuncionarioMapper {

    /**
     * Converte JpaFuncionario (do banco) para Funcionario de domínio.
     */
    public static Funcionario toDomain(JpaFuncionario jpaFuncionario) {
        if (jpaFuncionario == null) {
            return null;
        }

        Funcionario funcionario = new Funcionario(
                jpaFuncionario.getId(),
                jpaFuncionario.getNome(),
                jpaFuncionario.getEmail(),
                jpaFuncionario.getTelefone(),
                jpaFuncionario.getBarbeariaId(),
                jpaFuncionario.isAtivo(),
                jpaFuncionario.getPerfilType());

        if (jpaFuncionario.getDataCriacao() != null) {
            funcionario.setDataCriacao(jpaFuncionario.getDataCriacao());
        }
        if (jpaFuncionario.getDataAtualizacao() != null) {
            funcionario.setDataAtualizacao(jpaFuncionario.getDataAtualizacao());
        }

        return funcionario;
    }

    /**
     * Converte Funcionario de domínio para JpaFuncionario.
     */
    public static JpaFuncionario toJpaEntity(Funcionario funcionario) {
        if (funcionario == null) {
            return null;
        }

        JpaFuncionario jpaFuncionario = new JpaFuncionario();
        jpaFuncionario.setId(funcionario.getId());
        jpaFuncionario.setNome(funcionario.getNome());
        jpaFuncionario.setEmail(funcionario.getEmail());
        jpaFuncionario.setTelefone(funcionario.getTelefone());
        jpaFuncionario.setBarbeariaId(funcionario.getBarbeariaId());
        jpaFuncionario.setPerfilType(funcionario.getTipoPerfil());
        jpaFuncionario.setAtivo(funcionario.isAtivo());

        if (funcionario.getDataCriacao() != null) {
            jpaFuncionario.setDataCriacao(funcionario.getDataCriacao());
        }
        if (funcionario.getDataAtualizacao() != null) {
            jpaFuncionario.setDataAtualizacao(funcionario.getDataAtualizacao());
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

        // Cria Funcionario temporário para obter informações do perfil
        Funcionario funcionario = toDomain(jpaFuncionario);

        return new FuncionarioResponseDto(
                jpaFuncionario.getId(),
                jpaFuncionario.getBarbeariaId(),
                jpaFuncionario.getNome(),
                jpaFuncionario.getEmail(),
                jpaFuncionario.getTelefone(),
                jpaFuncionario.getPerfilType(),
                funcionario.getProfissao(),
                jpaFuncionario.getPerfilType().getEspecialidades(),
                jpaFuncionario.isAtivo(),
                jpaFuncionario.getDataCriacao(),
                jpaFuncionario.getDataAtualizacao());
    }

    /**
     * Converte Funcionario de domínio para FuncionarioResponseDto.
     */
    public FuncionarioResponseDto toResponseDto(Funcionario funcionario) {
        if (funcionario == null) {
            return null;
        }

        return new FuncionarioResponseDto(
                funcionario.getId(),
                funcionario.getBarbeariaId(),
                funcionario.getNome(),
                funcionario.getEmail(),
                funcionario.getTelefone(),
                funcionario.getTipoPerfil(),
                funcionario.getProfissao(),
                funcionario.getTipoPerfil().getEspecialidades(),
                funcionario.isAtivo(),
                funcionario.getDataCriacao(),
                funcionario.getDataAtualizacao());
    }

    /**
     * Cria uma instância de JpaFuncionario a partir do DTO.
     */
    public JpaFuncionario toEntityFromDto(FuncionarioRequestDto dto, Long barbeariaId) {
        if (dto == null) {
            return null;
        }

        JpaFuncionario funcionario = new JpaFuncionario();
        funcionario.setBarbeariaId(barbeariaId);
        funcionario.setNome(dto.getNome());
        funcionario.setEmail(dto.getEmail());
        funcionario.setTelefone(dto.getTelefone());
        funcionario.setPerfilType(dto.getPerfilType());
        funcionario.setAtivo(true);

        return funcionario;
    }
}
