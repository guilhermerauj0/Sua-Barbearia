package com.barbearia.application.services;

import com.barbearia.adapters.mappers.FuncionarioMapper;
import com.barbearia.application.dto.FuncionarioRequestDto;
import com.barbearia.application.dto.FuncionarioResponseDto;
import com.barbearia.infrastructure.persistence.entities.JpaFuncionario;
import com.barbearia.infrastructure.persistence.repositories.FuncionarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Serviço para gerenciamento de funcionários.
 * 
 * @author Sua Barbearia Team
 */
@Service
public class FuncionarioService {

    private final FuncionarioRepository funcionarioRepository;
    private final FuncionarioMapper funcionarioMapper;
    private final com.barbearia.infrastructure.persistence.repositories.ProfissionalServicoRepository profissionalServicoRepository;

    public FuncionarioService(FuncionarioRepository funcionarioRepository,
            FuncionarioMapper funcionarioMapper,
            com.barbearia.infrastructure.persistence.repositories.ProfissionalServicoRepository profissionalServicoRepository) {
        this.funcionarioRepository = funcionarioRepository;
        this.funcionarioMapper = funcionarioMapper;
        this.profissionalServicoRepository = profissionalServicoRepository;
    }

    /**
     * Cria um novo funcionário para a barbearia.
     */
    @SuppressWarnings("null")
    @Transactional
    public FuncionarioResponseDto criarFuncionario(FuncionarioRequestDto dto, Long barbeariaId) {
        // Verifica se já existe funcionário com mesmo email na barbearia
        if (funcionarioRepository.existsByEmailAndBarbeariaId(dto.email(), barbeariaId)) {
            throw new IllegalArgumentException("Já existe um funcionário com este email nesta barbearia");
        }

        // Cria e salva o funcionário
        JpaFuncionario funcionario = funcionarioMapper.toEntityFromDto(dto, barbeariaId);
        JpaFuncionario funcionarioSalvo = funcionarioRepository.save(funcionario);

        return funcionarioMapper.toResponseDto(funcionarioSalvo);
    }

    /**
     * Lista todos os funcionários da barbearia.
     */
    @Transactional(readOnly = true)
    public List<FuncionarioResponseDto> listarFuncionariosDaBarbearia(Long barbeariaId) {
        List<JpaFuncionario> funcionarios = funcionarioRepository.findByBarbeariaIdAndAtivoTrue(barbeariaId);

        return funcionarios.stream()
                .map(funcionarioMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    /**
     * Lista profissionais que realizam um determinado serviço.
     * 
     * @deprecated Use listarProfissionaisPorServicoEBarbearia() - este método
     *             retorna profissionais de TODAS as barbearias
     */
    @Deprecated
    @Transactional(readOnly = true)
    @SuppressWarnings("null")
    public List<FuncionarioResponseDto> listarProfissionaisPorServico(Long servicoId) {
        List<Long> funcionarioIds = profissionalServicoRepository.findFuncionariosByServicoIdAtivo(servicoId)
                .stream()
                .map(ps -> ps.getFuncionarioId())
                .collect(Collectors.toList());

        List<JpaFuncionario> funcionarios = funcionarioRepository.findAllById(funcionarioIds);

        return funcionarios.stream()
                .filter(JpaFuncionario::isAtivo)
                .map(funcionarioMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    /**
     * Lista profissionais que realizam um determinado serviço DENTRO DE UMA
     * BARBEARIA ESPECÍFICA.
     * 
     * CORRIGIDO: Agora filtra corretamente apenas profissionais da barbearia
     * selecionada.
     * 
     * @param servicoId   ID do serviço
     * @param barbeariaId ID da barbearia
     * @return Lista de profissionais da barbearia que realizam o serviço
     */
    @Transactional(readOnly = true)
    public List<FuncionarioResponseDto> listarProfissionaisPorServicoEBarbearia(Long servicoId, Long barbeariaId) {
        // 1. Busca profissionais que fazem o serviço
        List<Long> funcionarioIds = profissionalServicoRepository.findFuncionariosByServicoIdAtivo(servicoId)
                .stream()
                .map(ps -> ps.getFuncionarioId())
                .collect(Collectors.toList());

        if (funcionarioIds.isEmpty()) {
            return List.of(); // Retorna lista vazia se nenhum profissional faz o serviço
        }

        // 2. Busca funcionários E filtra por barbearia
        List<JpaFuncionario> funcionarios = funcionarioRepository.findAllById(funcionarioIds);

        return funcionarios.stream()
                .filter(JpaFuncionario::isAtivo)
                .filter(f -> f.getBarbeariaId().equals(barbeariaId)) // FILTRO CRÍTICO: apenas da barbearia
                .map(funcionarioMapper::toResponseDto)
                .collect(Collectors.toList());
    }
}
