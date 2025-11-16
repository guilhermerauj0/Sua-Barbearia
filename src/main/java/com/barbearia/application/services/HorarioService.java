package com.barbearia.application.services;

import com.barbearia.application.dto.HorarioDisponivelDto;
import com.barbearia.adapters.mappers.HorarioDisponivelMapper;
import com.barbearia.infrastructure.persistence.entities.JpaFuncionario;
import com.barbearia.infrastructure.persistence.entities.JpaAgendamento;
import com.barbearia.infrastructure.persistence.entities.JpaServico;
import com.barbearia.infrastructure.persistence.repositories.FuncionarioRepository;
import com.barbearia.infrastructure.persistence.repositories.ProfissionalServicoRepository;
import com.barbearia.infrastructure.persistence.repositories.HorarioFuncionamentoRepository;
import com.barbearia.infrastructure.persistence.repositories.AgendamentoRepository;
import com.barbearia.infrastructure.persistence.repositories.ServicoRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Serviço para gerenciar horários disponíveis para agendamentos.
 * 
 * Lógica de cálculo de disponibilidade:
 * 1. Validar parâmetros de entrada
 * 2. Buscar todos os profissionais qualificados para o serviço
 * 3. Para cada profissional:
 *    - Obter horário de funcionamento para o dia da semana
 *    - Buscar agendamentos existentes para a data
 *    - Calcular slots de 30min de intervalo
 *    - Remover slots já ocupados
 *    - Retornar slots disponíveis com nome do profissional
 * 
 * @author Sua Barbearia Team
 */
@Service
public class HorarioService {
    
    private final FuncionarioRepository funcionarioRepository;
    private final ProfissionalServicoRepository profissionalServicoRepository;
    private final HorarioFuncionamentoRepository horarioFuncionamentoRepository;
    private final AgendamentoRepository agendamentoRepository;
    private final ServicoRepository servicoRepository;
    
    // Intervalo padrão entre horários: 30 minutos
    private static final int INTERVALO_MINUTOS = 30;
    
    public HorarioService(
            FuncionarioRepository funcionarioRepository,
            ProfissionalServicoRepository profissionalServicoRepository,
            HorarioFuncionamentoRepository horarioFuncionamentoRepository,
            AgendamentoRepository agendamentoRepository,
            ServicoRepository servicoRepository) {
        this.funcionarioRepository = funcionarioRepository;
        this.profissionalServicoRepository = profissionalServicoRepository;
        this.horarioFuncionamentoRepository = horarioFuncionamentoRepository;
        this.agendamentoRepository = agendamentoRepository;
        this.servicoRepository = servicoRepository;
    }
    
    /**
     * Obtém os horários disponíveis para um serviço em uma data específica.
     * 
     * @param barbeariaId ID da barbearia
     * @param servicoId ID do serviço desejado
     * @param data Data para consultar disponibilidade
     * @return Lista de horários disponíveis com informações do profissional
     */
    public List<HorarioDisponivelDto> obterHorariosDisponiveis(Long barbeariaId, Long servicoId, LocalDate data) {
        List<HorarioDisponivelDto> horariosDisponiveis = new ArrayList<>();
        
        // Validar parâmetros
        if (barbeariaId == null || servicoId == null || data == null) {
            return horariosDisponiveis;
        }
        
        // Validar se a data não é no passado
        if (data.isBefore(LocalDate.now())) {
            return horariosDisponiveis;
        }
        
        // Obter informações do serviço (para saber a duração)
        Optional<JpaServico> servicoOpt = servicoRepository.findById(servicoId);
        if (servicoOpt.isEmpty()) {
            return horariosDisponiveis;
        }
        JpaServico servico = servicoOpt.get();
        
        // Obter dia da semana (1=SEGUNDA, 7=DOMINGO - ISO 8601)
        int diaSemana = data.getDayOfWeek().getValue();
        
        // Buscar horário de funcionamento para esse dia
        Optional<com.barbearia.infrastructure.persistence.entities.JpaHorarioFuncionamento> horarioOpt =
                horarioFuncionamentoRepository.findByBarbeariaIdAndDiaSemanaAtivo(barbeariaId, diaSemana);
        
        if (horarioOpt.isEmpty()) {
            // Barbearia fechada nesse dia
            return horariosDisponiveis;
        }
        
        var horarioFuncionamento = horarioOpt.get();
        LocalTime horaAbertura = horarioFuncionamento.getHoraAbertura();
        LocalTime horaFechamento = horarioFuncionamento.getHoraFechamento();
        
        // Buscar todos os profissionais que podem fazer esse serviço
        List<com.barbearia.infrastructure.persistence.entities.JpaProfissionalServico> profissionaisCasos =
                profissionalServicoRepository.findFuncionariosByServicoIdAtivo(servicoId);
        
        // Para cada profissional qualificado
        for (var profissionalServico : profissionaisCasos) {
            Long funcionarioId = profissionalServico.getFuncionarioId();
            
            // Obter dados do funcionário
            Optional<JpaFuncionario> funcionarioOpt = funcionarioRepository.findByIdAtivo(funcionarioId);
            if (funcionarioOpt.isEmpty()) {
                continue;
            }
            JpaFuncionario funcionario = funcionarioOpt.get();
            
            // Buscar agendamentos do profissional para essa data
            LocalDateTime inicioData = data.atStartOfDay();
            LocalDateTime fimData = data.atTime(23, 59, 59);
            
            List<JpaAgendamento> agendamentos = agendamentoRepository.findByBarbeariaIdAndPeriodo(
                barbeariaId, inicioData, fimData);
            
            // Filtrar apenas agendamentos desse profissional
            List<JpaAgendamento> agendamentosProfissional = agendamentos.stream()
                .filter(a -> funcionarioId.equals(a.getBarbeiroId()))
                .toList();
            
            // Calcular slots disponíveis
            List<HorarioDisponivelDto> slotsDisponiveis = calcularSlotsDisponiveis(
                funcionario, data, horaAbertura, horaFechamento,
                agendamentosProfissional, servico.getDuracao());
            
            horariosDisponiveis.addAll(slotsDisponiveis);
        }
        
        return horariosDisponiveis;
    }
    
    /**
     * Calcula os slots disponíveis para um funcionário em um dia específico.
     * 
     * @param funcionario Funcionário
     * @param data Data para calcular slots
     * @param horaAbertura Hora de abertura da barbearia
     * @param horaFechamento Hora de fechamento da barbearia
     * @param agendamentos Agendamentos já marcados para esse profissional
     * @param duracaoServicoMinutos Duração do serviço em minutos
     * @return Lista de horários disponíveis
     */
    private List<HorarioDisponivelDto> calcularSlotsDisponiveis(
            JpaFuncionario funcionario,
            LocalDate data,
            LocalTime horaAbertura,
            LocalTime horaFechamento,
            List<JpaAgendamento> agendamentos,
            Integer duracaoServicoMinutos) {
        
        List<HorarioDisponivelDto> slots = new ArrayList<>();
        
        if (duracaoServicoMinutos == null || duracaoServicoMinutos <= 0) {
            duracaoServicoMinutos = 60; // Default de 1 hora
        }
        
        // Gerar todos os possíveis slots no dia
        LocalTime horaAtual = horaAbertura;
        
        while (horaAtual.isBefore(horaFechamento)) {
            LocalTime horaFim = horaAtual.plusMinutes(duracaoServicoMinutos);
            
            // Verificar se o slot cabe dentro do horário de funcionamento
            if (horaFim.isAfter(horaFechamento)) {
                break;
            }
            
            // Verificar se o slot não conflita com agendamentos existentes
            if (estaDisponivel(horaAtual, horaFim, agendamentos, data)) {
                HorarioDisponivelDto dto = HorarioDisponivelMapper.toDto(
                    funcionario, data, horaAtual, horaFim);
                slots.add(dto);
            }
            
            // Avançar pelo intervalo padrão
            horaAtual = horaAtual.plusMinutes(INTERVALO_MINUTOS);
        }
        
        return slots;
    }
    
    /**
     * Verifica se um slot de tempo está disponível (sem conflitos com agendamentos).
     * 
     * @param horaInicio Hora de início do slot
     * @param horaFim Hora de fim do slot
     * @param agendamentos Agendamentos já marcados
     * @param data Data do slot
     * @return true se o slot está disponível, false caso contrário
     */
    private boolean estaDisponivel(LocalTime horaInicio, LocalTime horaFim, 
                                   List<JpaAgendamento> agendamentos, LocalDate data) {
        LocalDateTime inicioSlot = LocalDateTime.of(data, horaInicio);
        LocalDateTime fimSlot = LocalDateTime.of(data, horaFim);
        
        for (JpaAgendamento agendamento : agendamentos) {
            LocalDateTime inicioAgendamento = agendamento.getDataHora();
            
            // Assumir que a duração do agendamento é conhecida (pode estar em JpaServico)
            // Por enquanto, assumir 1 hora como padrão
            LocalDateTime fimAgendamento = inicioAgendamento.plusHours(1);
            
            // Verificar sobreposição
            if (!(fimSlot.isBefore(inicioAgendamento) || horaInicio.isBefore(
                inicioAgendamento.toLocalTime()) && inicioSlot.isAfter(fimAgendamento))) {
                
                // Se não há separação clara, há conflito
                if (!(fimSlot.isBefore(inicioAgendamento) || inicioSlot.isAfter(fimAgendamento))) {
                    return false;
                }
            }
        }
        
        return true;
    }
}
