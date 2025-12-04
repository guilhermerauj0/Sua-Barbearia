package com.barbearia.application.services;

import com.barbearia.application.dto.HorarioDisponivelDto;
import com.barbearia.adapters.mappers.HorarioDisponivelMapper;
import com.barbearia.infrastructure.persistence.entities.JpaFuncionario;
import com.barbearia.infrastructure.persistence.entities.JpaAgendamento;
import com.barbearia.infrastructure.persistence.entities.JpaServico;
import com.barbearia.infrastructure.persistence.entities.JpaHorarioBloqueado;
import com.barbearia.infrastructure.persistence.repositories.FuncionarioRepository;
import com.barbearia.infrastructure.persistence.repositories.ProfissionalServicoRepository;
import com.barbearia.infrastructure.persistence.repositories.HorarioFuncionamentoRepository;
import com.barbearia.infrastructure.persistence.repositories.AgendamentoRepository;
import com.barbearia.infrastructure.persistence.repositories.ServicoRepository;
import com.barbearia.infrastructure.persistence.repositories.HorarioExcecaoRepository;
import com.barbearia.infrastructure.persistence.entities.JpaHorarioExcecao;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class HorarioService {

    private final FuncionarioRepository funcionarioRepository;
    private final ProfissionalServicoRepository profissionalServicoRepository;
    private final HorarioFuncionamentoRepository horarioFuncionamentoRepository;
    private final AgendamentoRepository agendamentoRepository;
    private final ServicoRepository servicoRepository;
    private final HorarioBloqueioService horarioBloqueioService;
    private final HorarioExcecaoRepository horarioExcecaoRepository;

    // Intervalo padrão entre horários: 30 minutos
    private static final int INTERVALO_MINUTOS = 30;

    public HorarioService(
            FuncionarioRepository funcionarioRepository,
            ProfissionalServicoRepository profissionalServicoRepository,
            HorarioFuncionamentoRepository horarioFuncionamentoRepository,
            AgendamentoRepository agendamentoRepository,
            ServicoRepository servicoRepository,
            HorarioBloqueioService horarioBloqueioService,
            HorarioExcecaoRepository horarioExcecaoRepository) {
        this.funcionarioRepository = funcionarioRepository;
        this.profissionalServicoRepository = profissionalServicoRepository;
        this.horarioFuncionamentoRepository = horarioFuncionamentoRepository;
        this.agendamentoRepository = agendamentoRepository;
        this.servicoRepository = servicoRepository;
        this.horarioBloqueioService = horarioBloqueioService;
        this.horarioExcecaoRepository = horarioExcecaoRepository;
    }

    /**
     * Obtém os horários disponíveis para um serviço em uma data específica.
     * 
     * @param barbeariaId ID da barbearia
     * @param servicoId   ID do serviço desejado
     * @param data        Data para consultar disponibilidade
     * @return Lista de horários disponíveis com informações do profissional
     */
    public List<HorarioDisponivelDto> obterHorariosDisponiveis(Long barbeariaId, Long servicoId, LocalDate data) {
        return obterHorariosDisponiveis(barbeariaId, servicoId, data, null);
    }

    /**
     * Obtém os horários disponíveis para um serviço em uma data específica,
     * opcionalmente filtrando por profissional.
     * 
     * @param barbeariaId    ID da barbearia
     * @param servicoId      ID do serviço desejado
     * @param data           Data para consultar disponibilidade
     * @param profissionalId ID do profissional (opcional)
     * @return Lista de horários disponíveis com informações do profissional
     */
    public List<HorarioDisponivelDto> obterHorariosDisponiveis(Long barbeariaId, Long servicoId, LocalDate data,
            Long profissionalId) {
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

        // Buscar todos os profissionais que podem fazer esse serviço
        List<com.barbearia.infrastructure.persistence.entities.JpaProfissionalServico> profissionaisCasos = profissionalServicoRepository
                .findFuncionariosByServicoIdAtivo(servicoId);

        // Para cada profissional qualificado
        for (var profissionalServico : profissionaisCasos) {
            Long funcionarioId = profissionalServico.getFuncionarioId();

            // Se um profissional específico foi solicitado, filtrar os outros
            if (profissionalId != null && !profissionalId.equals(funcionarioId)) {
                continue;
            }

            // Obter dados do funcionário
            Optional<JpaFuncionario> funcionarioOpt = funcionarioRepository.findByIdAtivo(funcionarioId);
            if (funcionarioOpt.isEmpty()) {
                continue;
            }
            JpaFuncionario funcionario = funcionarioOpt.get();

            LocalTime horaAbertura;
            LocalTime horaFechamento;

            // 1. Verificar se há EXCEÇÃO para esta data (Prioridade Alta)
            Optional<JpaHorarioExcecao> excecaoOpt = horarioExcecaoRepository.findByFuncionarioIdAndData(funcionarioId,
                    data);

            if (excecaoOpt.isPresent()) {
                var excecao = excecaoOpt.get();
                horaAbertura = excecao.getHoraAbertura();
                horaFechamento = excecao.getHoraFechamento();
            } else {
                // 2. Se não houver exceção, usar horário padrão recorrente

                // Tenta buscar horário específico do profissional
                Optional<com.barbearia.infrastructure.persistence.entities.JpaHorarioFuncionamento> horarioOpt = horarioFuncionamentoRepository
                        .findByFuncionarioIdAndDiaSemanaAtivo(funcionarioId, diaSemana);

                // Se não tiver horário específico, tenta o da barbearia
                if (horarioOpt.isEmpty()) {
                    horarioOpt = horarioFuncionamentoRepository.findByBarbeariaIdAndDiaSemanaAtivo(barbeariaId,
                            diaSemana);
                }

                if (horarioOpt.isEmpty()) {
                    // Profissional e barbearia fechados nesse dia
                    continue;
                }

                var horarioFuncionamento = horarioOpt.get();
                horaAbertura = horarioFuncionamento.getHoraAbertura();
                horaFechamento = horarioFuncionamento.getHoraFechamento();
            }

            // Buscar agendamentos do profissional para essa data
            LocalDateTime inicioData = data.atStartOfDay();
            LocalDateTime fimData = data.atTime(23, 59, 59);

            List<JpaAgendamento> agendamentos = agendamentoRepository.findByBarbeariaIdAndPeriodo(
                    barbeariaId, inicioData, fimData);

            // Filtrar apenas agendamentos desse profissional
            List<JpaAgendamento> agendamentosProfissional = agendamentos.stream()
                    .filter(a -> funcionarioId.equals(a.getBarbeiroId()))
                    .toList();

            // Buscar bloqueios do profissional para essa data
            List<JpaHorarioBloqueado> bloqueios = horarioBloqueioService.listarBloqueiosPorData(funcionarioId, data);

            // Calcular slots disponíveis
            List<HorarioDisponivelDto> slotsDisponiveis = calcularSlotsDisponiveis(
                    funcionario, data, horaAbertura, horaFechamento,
                    agendamentosProfissional, servico.getDuracao(), bloqueios);

            horariosDisponiveis.addAll(slotsDisponiveis);
        }

        return horariosDisponiveis;
    }

    /**
     * Retorna os dias com disponibilidade em um determinado mês.
     */
    public List<LocalDate> obterDatasDisponiveis(
            Long barbeariaId,
            Long servicoId,
            int ano,
            int mes,
            Long funcionarioId) {

        List<LocalDate> datasDisponiveis = new ArrayList<>();
        YearMonth yearMonth = YearMonth.of(ano, mes);
        LocalDate hoje = LocalDate.now();

        // Iterar por todos os dias do mês
        for (int dia = 1; dia <= yearMonth.lengthOfMonth(); dia++) {
            LocalDate data = yearMonth.atDay(dia);

            // Ignorar datas passadas
            if (data.isBefore(hoje)) {
                continue;
            }

            // Verificar se há disponibilidade neste dia
            // Nota: Isso pode ser otimizado no futuro para não calcular todos os slots
            List<HorarioDisponivelDto> slots = obterHorariosDisponiveis(
                    barbeariaId, servicoId, data, funcionarioId);

            if (!slots.isEmpty()) {
                datasDisponiveis.add(data);
            }
        }

        return datasDisponiveis;
    }

    /**
     * Calcula os slots disponíveis para um funcionário em um dia específico.
     * 
     * @param funcionario           Funcionário
     * @param data                  Data para calcular slots
     * @param horaAbertura          Hora de abertura da barbearia
     * @param horaFechamento        Hora de fechamento da barbearia
     * @param agendamentos          Agendamentos já marcados para esse profissional
     * @param duracaoServicoMinutos Duração do serviço em minutos
     * @param bloqueios             Bloqueios de horário do profissional
     * @return Lista de horários disponíveis
     */
    private List<HorarioDisponivelDto> calcularSlotsDisponiveis(
            JpaFuncionario funcionario,
            LocalDate data,
            LocalTime horaAbertura,
            LocalTime horaFechamento,
            List<JpaAgendamento> agendamentos,
            Integer duracaoServicoMinutos,
            List<JpaHorarioBloqueado> bloqueios) {

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

            // Verificar se o slot não está bloqueado E não conflita com agendamentos
            if (!estaBloqueado(horaAtual, horaFim, bloqueios, data) &&
                    estaDisponivel(horaAtual, horaFim, agendamentos, data, duracaoServicoMinutos)) {

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
     * Verifica se um slot de tempo está bloqueado pelo profissional.
     * 
     * @param horaInicio Hora de início do slot
     * @param horaFim    Hora de fim do slot
     * @param bloqueios  Bloqueios do profissional
     * @param data       Data do slot
     * @return true se o slot está bloqueado, false caso contrário
     */
    private boolean estaBloqueado(LocalTime horaInicio, LocalTime horaFim,
            List<com.barbearia.infrastructure.persistence.entities.JpaHorarioBloqueado> bloqueios,
            LocalDate data) {
        LocalDateTime inicioSlot = LocalDateTime.of(data, horaInicio);
        LocalDateTime fimSlot = LocalDateTime.of(data, horaFim);

        for (var bloqueio : bloqueios) {
            LocalDateTime inicioBloqueio = LocalDateTime.of(bloqueio.getData(), bloqueio.getHorarioInicio());
            LocalDateTime fimBloqueio = LocalDateTime.of(bloqueio.getData(), bloqueio.getHorarioFim());

            // Há sobreposição se: (inicioSlot < fimBloqueio) E (fimSlot > inicioBloqueio)
            if (inicioSlot.isBefore(fimBloqueio) && fimSlot.isAfter(inicioBloqueio)) {
                return true; // Slot está bloqueado
            }
        }

        return false;
    }

    /**
     * Verifica se um slot de tempo está disponível (sem conflitos com
     * agendamentos).
     * 
     * @param horaInicio            Hora de início do slot
     * @param horaFim               Hora de fim do slot
     * @param agendamentos          Agendamentos já marcados
     * @param data                  Data do slot
     * @param duracaoServicoMinutos Duração do serviço sendo consultado
     * @return true se o slot está disponível, false caso contrário
     */
    private boolean estaDisponivel(LocalTime horaInicio, LocalTime horaFim,
            List<JpaAgendamento> agendamentos, LocalDate data,
            Integer duracaoServicoMinutos) {
        LocalDateTime inicioSlot = LocalDateTime.of(data, horaInicio);
        LocalDateTime fimSlot = LocalDateTime.of(data, horaFim);

        for (JpaAgendamento agendamento : agendamentos) {
            // Pular agendamentos cancelados
            if (agendamento.getStatus() == com.barbearia.domain.enums.StatusAgendamento.CANCELADO) {
                continue;
            }

            LocalDateTime inicioAgendamento = agendamento.getDataHora();

            // Usar duração real do serviço
            Integer duracao = duracaoServicoMinutos;
            if (duracao == null || duracao <= 0) {
                duracao = 60; // fallback para 1 hora
            }

            LocalDateTime fimAgendamento = inicioAgendamento.plusMinutes(duracao);

            // Há conflito se: (inicioSlot < fimAgendamento) E (fimSlot > inicioAgendamento)
            if (inicioSlot.isBefore(fimAgendamento) && fimSlot.isAfter(inicioAgendamento)) {
                return false; // Slot está ocupado
            }
        }

        return true; // Slot está disponível
    }
}
