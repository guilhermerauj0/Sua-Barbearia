package com.barbearia.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * DTO para criação de agendamento direto pela barbearia.
 * Permite agendar clientes que ainda não possuem conta no sistema.
 */
public class AgendamentoDiretoRequestDto {

    @NotBlank(message = "Nome do cliente é obrigatório")
    private String clienteNome;

    @NotBlank(message = "Telefone do cliente é obrigatório")
    private String clienteTelefone;

    private String clienteEmail; // opcional

    @NotNull(message = "ID do serviço é obrigatório")
    private Long servicoId;

    @NotNull(message = "ID do funcionário é obrigatório")
    private Long funcionarioId;

    @NotNull(message = "Data/hora é obrigatória")
    private LocalDateTime dataHora;

    private String observacoes;

    // Constructors

    public AgendamentoDiretoRequestDto() {
    }

    public AgendamentoDiretoRequestDto(String clienteNome, String clienteTelefone, String clienteEmail,
            Long servicoId, Long funcionarioId, LocalDateTime dataHora, String observacoes) {
        this.clienteNome = clienteNome;
        this.clienteTelefone = clienteTelefone;
        this.clienteEmail = clienteEmail;
        this.servicoId = servicoId;
        this.funcionarioId = funcionarioId;
        this.dataHora = dataHora;
        this.observacoes = observacoes;
    }

    // Getters and Setters

    public String getClienteNome() {
        return clienteNome;
    }

    public void setClienteNome(String clienteNome) {
        this.clienteNome = clienteNome;
    }

    public String getClienteTelefone() {
        return clienteTelefone;
    }

    public void setClienteTelefone(String clienteTelefone) {
        this.clienteTelefone = clienteTelefone;
    }

    public String getClienteEmail() {
        return clienteEmail;
    }

    public void setClienteEmail(String clienteEmail) {
        this.clienteEmail = clienteEmail;
    }

    public Long getServicoId() {
        return servicoId;
    }

    public void setServicoId(Long servicoId) {
        this.servicoId = servicoId;
    }

    public Long getFuncionarioId() {
        return funcionarioId;
    }

    public void setFuncionarioId(Long funcionarioId) {
        this.funcionarioId = funcionarioId;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public void setDataHora(LocalDateTime dataHora) {
        this.dataHora = dataHora;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }
}
