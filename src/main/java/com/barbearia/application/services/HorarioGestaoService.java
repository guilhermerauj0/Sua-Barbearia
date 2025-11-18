package com.barbearia.application.services;

import com.barbearia.application.dto.*;
import com.barbearia.domain.entities.FeriadoExcecao;
import com.barbearia.domain.enums.TipoExcecao;
import com.barbearia.infrastructure.persistence.entities.FeriadoExcecaoEntity;
import com.barbearia.infrastructure.persistence.repositories.FeriadoExcecaoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Serviço para gestão de horários de funcionamento e exceções (feriados, fechamentos especiais).
 * Responsável por CRUD completo e validações de regras de negócio.
 */
@Service
public class HorarioGestaoService {
    
    private static final Logger log = LoggerFactory.getLogger(HorarioGestaoService.class);
    
    private final FeriadoExcecaoRepository feriadoExcecaoRepository;
    
    public HorarioGestaoService(FeriadoExcecaoRepository feriadoExcecaoRepository) {
        this.feriadoExcecaoRepository = feriadoExcecaoRepository;
    }
    
    // ===== MÉTODOS PARA EXCEÇÕES/FERIADOS =====
    
    /**
     * Lista todas as exceções de horário de uma barbearia.
     * 
     * @param barbeariaId ID da barbearia
     * @return lista de exceções
     */
    public List<FeriadoExcecaoResponseDto> listarExcecoes(Long barbeariaId) {
        log.info("Listando exceções de horário para barbearia {}", barbeariaId);
        List<FeriadoExcecaoEntity> excecoes = feriadoExcecaoRepository.findByBarbeariaId(barbeariaId);
        return excecoes.stream()
                .map(this::toResponseDto)
                .collect(Collectors.toList());
    }
    
    /**
     * Lista exceções ativas de uma barbearia em um período.
     * 
     * @param barbeariaId ID da barbearia
     * @param dataInicio data inicial
     * @param dataFim data final
     * @return lista de exceções no período
     */
    public List<FeriadoExcecaoResponseDto> listarExcecoesNoPeriodo(
            Long barbeariaId, LocalDate dataInicio, LocalDate dataFim) {
        log.info("Listando exceções para barbearia {} entre {} e {}", 
                barbeariaId, dataInicio, dataFim);
        List<FeriadoExcecaoEntity> excecoes = feriadoExcecaoRepository
                .findByBarbeariaIdAndDataBetweenAndAtivo(barbeariaId, dataInicio, dataFim, true);
        return excecoes.stream()
                .map(this::toResponseDto)
                .collect(Collectors.toList());
    }
    
    /**
     * Busca uma exceção específica por ID.
     * 
     * @param id ID da exceção
     * @param barbeariaId ID da barbearia (para validação de propriedade)
     * @return exceção encontrada
     * @throws IllegalArgumentException se não encontrar ou não pertencer à barbearia
     */
    public FeriadoExcecaoResponseDto buscarExcecaoPorId(Long id, Long barbeariaId) {
        if (id == null || barbeariaId == null) {
            throw new IllegalArgumentException("ID e barbeariaId não podem ser nulos");
        }
        
        log.info("Buscando exceção {} para barbearia {}", id, barbeariaId);
        FeriadoExcecaoEntity excecao = feriadoExcecaoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Exceção não encontrada"));
        
        if (!excecao.getBarbeariaId().equals(barbeariaId)) {
            throw new IllegalArgumentException("Exceção não pertence a esta barbearia");
        }
        
        return toResponseDto(excecao);
    }
    
    /**
     * Cria uma nova exceção de horário.
     * 
     * @param barbeariaId ID da barbearia
     * @param requestDto dados da exceção
     * @return exceção criada
     */
    @Transactional
    @SuppressWarnings({"null", "nullness"})
    public FeriadoExcecaoResponseDto criarExcecao(Long barbeariaId, FeriadoExcecaoRequestDto requestDto) {
        log.info("Criando exceção de horário para barbearia {} na data {}", 
                barbeariaId, requestDto.getData());
        
        // Validar se já existe exceção para esta data
        if (feriadoExcecaoRepository.existsByBarbeariaIdAndData(barbeariaId, requestDto.getData())) {
            throw new IllegalArgumentException(
                "Já existe uma exceção cadastrada para esta data"
            );
        }
        
        // Criar entidade de domínio para validação
        FeriadoExcecao domainExcecao = new FeriadoExcecao(
            null,
            barbeariaId,
            requestDto.getData(),
            requestDto.getTipo(),
            requestDto.getHorarioAbertura(),
            requestDto.getHorarioFechamento(),
            requestDto.getDescricao()
        );
        
        // Validações já feitas no construtor da entidade de domínio
        
        // Converter para entidade JPA e salvar
        FeriadoExcecaoEntity entity = toEntity(domainExcecao);
        FeriadoExcecaoEntity savedEntity = feriadoExcecaoRepository.save(entity);
        
        log.info("Exceção criada com sucesso: ID {}", savedEntity.getId());
        return toResponseDto(savedEntity);
    }
    
    /**
     * Atualiza uma exceção existente.
     * 
     * @param id ID da exceção
     * @param barbeariaId ID da barbearia
     * @param requestDto novos dados
     * @return exceção atualizada
     */
    @Transactional
    public FeriadoExcecaoResponseDto atualizarExcecao(
            Long id, Long barbeariaId, FeriadoExcecaoRequestDto requestDto) {
        if (id == null || barbeariaId == null) {
            throw new IllegalArgumentException("ID e barbeariaId não podem ser nulos");
        }
        
        log.info("Atualizando exceção {} para barbearia {}", id, barbeariaId);
        
        FeriadoExcecaoEntity entity = feriadoExcecaoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Exceção não encontrada"));
        
        if (!entity.getBarbeariaId().equals(barbeariaId)) {
            throw new IllegalArgumentException("Exceção não pertence a esta barbearia");
        }
        
        // Criar entidade de domínio para validação
        new FeriadoExcecao(
            id,
            barbeariaId,
            requestDto.getData(),
            requestDto.getTipo(),
            requestDto.getHorarioAbertura(),
            requestDto.getHorarioFechamento(),
            requestDto.getDescricao()
        ).validate();
        
        // Atualizar entity
        entity.setData(requestDto.getData());
        entity.setTipo(requestDto.getTipo());
        entity.setHorarioAbertura(requestDto.getHorarioAbertura());
        entity.setHorarioFechamento(requestDto.getHorarioFechamento());
        entity.setDescricao(requestDto.getDescricao());
        entity.setAtivo(requestDto.getAtivo() != null ? requestDto.getAtivo() : true);
        
        FeriadoExcecaoEntity savedEntity = feriadoExcecaoRepository.save(entity);
        
        log.info("Exceção atualizada com sucesso: ID {}", savedEntity.getId());
        return toResponseDto(savedEntity);
    }
    
    /**
     * Remove (desativa) uma exceção.
     * 
     * @param id ID da exceção
     * @param barbeariaId ID da barbearia
     */
    @Transactional
    public void removerExcecao(Long id, Long barbeariaId) {
        if (id == null || barbeariaId == null) {
            throw new IllegalArgumentException("ID e barbeariaId não podem ser nulos");
        }
        
        log.info("Removendo exceção {} para barbearia {}", id, barbeariaId);
        
        FeriadoExcecaoEntity entity = feriadoExcecaoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Exceção não encontrada"));
        
        if (!entity.getBarbeariaId().equals(barbeariaId)) {
            throw new IllegalArgumentException("Exceção não pertence a esta barbearia");
        }
        
        // Soft delete
        entity.setAtivo(false);
        feriadoExcecaoRepository.save(entity);
        
        log.info("Exceção desativada com sucesso: ID {}", id);
    }
    
    /**
     * Verifica se a barbearia está aberta em uma data/hora específica,
     * considerando horários normais e exceções.
     * 
     * @param barbeariaId ID da barbearia
     * @param dataHora data e hora a verificar
     * @return true se estiver aberta
     */
    public boolean estaAberto(Long barbeariaId, LocalDate data, LocalTime horario) {
        // Primeiro verificar se há exceção para esta data
        var excecaoOpt = feriadoExcecaoRepository.findByBarbeariaIdAndDataAndAtivo(
            barbeariaId, data, true
        );
        
        if (excecaoOpt.isPresent()) {
            FeriadoExcecaoEntity excecao = excecaoOpt.get();
            
            // Se for fechado, retorna false
            if (excecao.getTipo() == TipoExcecao.FECHADO) {
                return false;
            }
            
            // Se for horário especial, verifica o horário
            if (excecao.getTipo() == TipoExcecao.HORARIO_ESPECIAL) {
                return !horario.isBefore(excecao.getHorarioAbertura()) 
                    && horario.isBefore(excecao.getHorarioFechamento());
            }
        }
        
        // Se não há exceção, verificar horário normal de funcionamento
        // (isso será implementado quando integrarmos com HorarioFuncionamento)
        return true;
    }
    
    // ===== MÉTODOS DE CONVERSÃO =====
    
    private FeriadoExcecaoResponseDto toResponseDto(FeriadoExcecaoEntity entity) {
        return new FeriadoExcecaoResponseDto(
            entity.getId(),
            entity.getBarbeariaId(),
            entity.getData(),
            entity.getTipo(),
            entity.getHorarioAbertura(),
            entity.getHorarioFechamento(),
            entity.getDescricao(),
            entity.isAtivo()
        );
    }
    
    private FeriadoExcecaoEntity toEntity(FeriadoExcecao domain) {
        FeriadoExcecaoEntity entity = new FeriadoExcecaoEntity();
        entity.setId(domain.getId());
        entity.setBarbeariaId(domain.getBarbeariaId());
        entity.setData(domain.getData());
        entity.setTipo(domain.getTipo());
        entity.setHorarioAbertura(domain.getHorarioAbertura());
        entity.setHorarioFechamento(domain.getHorarioFechamento());
        entity.setDescricao(domain.getDescricao());
        entity.setAtivo(domain.isAtivo());
        return entity;
    }
}
