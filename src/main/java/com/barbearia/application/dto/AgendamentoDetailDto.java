package com.barbearia.application.dto;

import com.barbearia.domain.enums.StatusAgendamento;
import java.time.LocalDateTime;

/**
 * DTO detalhado para visualização completa de um agendamento.
 * 
 * Contém todas as informações relevantes:
 * - Dados do agendamento (id, data/hora, status)
 * - Informações do cliente (id, nome, telefone, email, documento)
 * - Informações da barbearia (id, nome, endereço, telefone)
 * - Informações do barbeiro (id, nome) - pode ser null se não atribuído
 * - Informações do serviço (id, nome, valor, descrição)
 * - Observações e datas de criação/atualização
 * 
 * Este DTO é usado para retornar detalhes completos quando solicitado um 
 * agendamento específico. A quantidade de dados justifica a necessidade de
 * verificação de autorização no serviço.
 * 
 * @author Sua Barbearia Team
 */
public record AgendamentoDetailDto(
        // Dados do agendamento
        Long id,
        LocalDateTime dataHora,
        StatusAgendamento status,
        String observacoes,
        LocalDateTime dataCriacao,
        LocalDateTime dataAtualizacao,
        
        // Dados do cliente
        Long clienteId,
        String nomeCliente,
        String emailCliente,
        String telefoneCliente,
        String documentoCliente,
        
        // Dados da barbearia
        Long barbeariaId,
        String nomeBarbearia,
        String enderecoBarbearia,
        String telefoneBarbearia,
        
        // Dados do barbeiro (pode ser null)
        Long barbeiroId,
        String nomeBarbeiro,
        
        // Dados do serviço
        Long servicoId,
        String nomeServico,
        String descricaoServico,
        Double valorServico
) {
}
