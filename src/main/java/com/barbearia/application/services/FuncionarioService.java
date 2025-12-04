package com.barbearia.application.services;

import com.barbearia.adapters.mappers.FuncionarioMapper;
import com.barbearia.application.dto.FuncionarioRequestDto;
import com.barbearia.application.dto.FuncionarioResponseDto;
import com.barbearia.infrastructure.persistence.entities.JpaFuncionario;
import com.barbearia.infrastructure.persistence.repositories.FuncionarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.barbearia.adapters.mappers.ServicoMapper;
import com.barbearia.application.dto.ServicoDto;
import com.barbearia.infrastructure.persistence.entities.JpaProfissionalServico;
import com.barbearia.infrastructure.persistence.entities.JpaServico;
import com.barbearia.infrastructure.persistence.repositories.ServicoRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;

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
    private final ServicoRepository servicoRepository;

    public FuncionarioService(FuncionarioRepository funcionarioRepository,
            FuncionarioMapper funcionarioMapper,
            com.barbearia.infrastructure.persistence.repositories.ProfissionalServicoRepository profissionalServicoRepository,
            ServicoRepository servicoRepository) {
        this.funcionarioRepository = funcionarioRepository;
        this.funcionarioMapper = funcionarioMapper;
        this.profissionalServicoRepository = profissionalServicoRepository;
        this.servicoRepository = servicoRepository;
    }

    /**
     * Cria um novo funcionário para a barbearia.
     */
    @SuppressWarnings("null")
    @Transactional
    public FuncionarioResponseDto criarFuncionario(FuncionarioRequestDto dto, Long barbeariaId) {
        // Verifica se já existe funcionário com mesmo email na barbearia
        if (funcionarioRepository.existsByEmailAndBarbeariaId(dto.getEmail(), barbeariaId)) {
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

    /**
     * Edita dados de um funcionário existente.
     * 
     * Validações:
     * - Funcionário deve existir e pertencer à barbearia
     * - Email não pode estar duplicado na mesma barbearia
     * 
     * @param barbeariaId   ID da barbearia
     * @param funcionarioId ID do funcionário
     * @param dto           Novos dados
     * @return DTO atualizado
     */
    @Transactional
    @SuppressWarnings("null")
    public FuncionarioResponseDto editarFuncionario(Long barbeariaId, Long funcionarioId,
            FuncionarioRequestDto dto) {
        // Verifica se o funcionário existe e pertence à barbearia
        JpaFuncionario funcionario = funcionarioRepository.findById(funcionarioId)
                .orElseThrow(() -> new IllegalArgumentException("Funcionário não encontrado"));

        if (!funcionario.getBarbeariaId().equals(barbeariaId)) {
            throw new IllegalArgumentException("Funcionário não pertence a esta barbearia");
        }

        // Verifica se email está duplicado (exceto para o próprio funcionário)
        if (!funcionario.getEmail().equals(dto.getEmail()) &&
                funcionarioRepository.existsByEmailAndBarbeariaId(dto.getEmail(), barbeariaId)) {
            throw new IllegalArgumentException("Já existe um funcionário com este email nesta barbearia");
        }

        // Atualiza dados
        funcionario.setNome(dto.getNome());
        funcionario.setEmail(dto.getEmail());
        funcionario.setTelefone(dto.getTelefone());
        // Nota: perfis e serviços não são atualizados por este endpoint
        // Eles devem ser gerenciados por endpoints específicos se necessário

        JpaFuncionario atualizado = funcionarioRepository.save(funcionario);
        return funcionarioMapper.toResponseDto(atualizado);
    }

    /**
     * Desativa um funcionário (soft delete).
     * 
     * @param barbeariaId   ID da barbearia
     * @param funcionarioId ID do funcionário
     * @return Mensagem de sucesso
     */
    @Transactional
    @SuppressWarnings("null")
    public String desativarFuncionario(Long barbeariaId, Long funcionarioId) {
        // Verifica se o funcionário existe e pertence à barbearia
        JpaFuncionario funcionario = funcionarioRepository.findById(funcionarioId)
                .orElseThrow(() -> new IllegalArgumentException("Funcionário não encontrado"));

        if (!funcionario.getBarbeariaId().equals(barbeariaId)) {
            throw new IllegalArgumentException("Funcionário não pertence a esta barbearia");
        }

        // Marca como inativo
        funcionario.setAtivo(false);
        funcionarioRepository.save(funcionario);

        return "Funcionário desativado com sucesso. O histórico de agendamentos foi preservado.";
    }

    /**
     * Associa serviços a um funcionário.
     * 
     * @param barbeariaId   ID da barbearia
     * @param funcionarioId ID do funcionário
     * @param servicoIds    Lista de IDs dos serviços
     * @return Lista de IDs dos serviços associados
     */
    @Transactional
    @SuppressWarnings("null")
    public List<Long> associarServicos(Long barbeariaId, Long funcionarioId, List<Long> servicoIds) {
        // Verifica funcionário
        JpaFuncionario funcionario = funcionarioRepository.findById(funcionarioId)
                .orElseThrow(() -> new IllegalArgumentException("Funcionário não encontrado"));

        if (!funcionario.getBarbeariaId().equals(barbeariaId)) {
            throw new IllegalArgumentException("Funcionário não pertence a esta barbearia");
        }

        // Verifica serviços
        List<JpaServico> servicos = servicoRepository.findAllById(servicoIds);
        if (servicos.size() != servicoIds.size()) {
            throw new IllegalArgumentException("Um ou mais serviços não foram encontrados");
        }

        for (JpaServico servico : servicos) {
            if (!servico.getBarbeariaId().equals(barbeariaId)) {
                throw new IllegalArgumentException("O serviço " + servico.getNome() + " não pertence a esta barbearia");
            }
        }

        // Remove associações existentes para atualizar (estratégia simples: remove tudo
        // e adiciona novos)
        // Nota: Em um cenário real, poderia ser melhor fazer um merge (adicionar novos,
        // remover excluídos)
        // Mas como não temos método delete exposto no repository customizado, vamos
        // iterar e verificar

        // Vamos apenas ADICIONAR os novos que ainda não existem para evitar duplicidade
        // Se quiser remover, precisaria de um endpoint específico ou lógica de
        // sincronização completa

        List<Long> associados = new ArrayList<>();

        for (Long servicoId : servicoIds) {
            // Verifica se já existe vínculo
            if (profissionalServicoRepository.findByFuncionarioIdAndServicoId(funcionarioId, servicoId).isEmpty()) {
                JpaProfissionalServico novoVinculo = new JpaProfissionalServico();
                novoVinculo.setFuncionarioId(funcionarioId);
                novoVinculo.setServicoId(servicoId);
                novoVinculo.setAtivo(true);
                novoVinculo.setDataCriacao(LocalDateTime.now());
                novoVinculo.setDataAtualizacao(LocalDateTime.now());

                profissionalServicoRepository.save(novoVinculo);
            } else {
                // Se já existe mas está inativo, reativa?
                profissionalServicoRepository.findByFuncionarioIdAndServicoId(funcionarioId, servicoId)
                        .ifPresent(vinculo -> {
                            if (!vinculo.isAtivo()) {
                                vinculo.setAtivo(true);
                                vinculo.setDataAtualizacao(LocalDateTime.now());
                                profissionalServicoRepository.save(vinculo);
                            }
                        });
            }
            associados.add(servicoId);
        }

        return associados;
    }

    /**
     * Lista os serviços que um profissional realiza.
     * 
     * @param funcionarioId ID do funcionário
     * @return Lista de serviços
     */
    @Transactional(readOnly = true)
    @SuppressWarnings("null")
    public List<ServicoDto> listarServicosDoProfissional(Long funcionarioId) {
        // Busca vínculos ativos
        List<JpaProfissionalServico> vinculos = profissionalServicoRepository
                .findServicosByFuncionarioIdAtivo(funcionarioId);

        if (vinculos.isEmpty()) {
            return List.of();
        }

        List<Long> servicoIds = vinculos.stream()
                .map(JpaProfissionalServico::getServicoId)
                .collect(Collectors.toList());

        // Busca detalhes dos serviços
        List<JpaServico> servicos = servicoRepository.findAllById(servicoIds);

        return servicos.stream()
                .filter(JpaServico::isAtivo) // Garante que o serviço em si também está ativo
                .map(ServicoMapper::toDto)
                .collect(Collectors.toList());
    }
}
