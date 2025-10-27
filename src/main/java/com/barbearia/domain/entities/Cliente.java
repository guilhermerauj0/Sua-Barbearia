package com.barbearia.domain.entities;

import java.util.ArrayList;
import java.util.List;

/**
 * Entidade de domínio que representa um Cliente do sistema.
 * 
 * Cliente é um tipo específico de Usuário que pode agendar serviços na barbearia.
 * Esta classe estende Usuario e adiciona funcionalidades específicas de clientes.
 * 
 * Princípios aplicados:
 * - Herança: Cliente herda comportamentos comuns de Usuario
 * - Encapsulamento: Lógica de negócio específica de clientes
 * - Single Responsibility: Cliente só gerencia informações de clientes
 * 
 * @author Sua Barbearia Team
 */
public class Cliente extends Usuario {
    
    /**
     * Constante que define o papel deste tipo de usuário
     */
    private static final String ROLE_CLIENTE = "CLIENTE";
    
    /**
     * Lista de agendamentos realizados pelo cliente
     * (será implementada futuramente quando criar a entidade Agendamento)
     */
    private List<Long> agendamentosIds;
    
    /**
     * Indica se o cliente está ativo no sistema
     */
    private boolean ativo;
    
    /**
     * Construtor padrão
     * Define automaticamente o role como CLIENTE
     */
    public Cliente() {
        super();
        super.setRole(ROLE_CLIENTE);
        this.ativo = true;
        this.agendamentosIds = new ArrayList<>();
    }
    
    /**
     * Construtor com parâmetros para criação de um novo cliente
     * 
     * @param nome Nome completo do cliente
     * @param email Email único do cliente (será validado)
     * @param senha Senha que será armazenada com hash (mínimo 8 caracteres)
     * @param telefone Telefone de contato (formato válido)
     */
    public Cliente(String nome, String email, String senha, String telefone) {
        super(nome, email, senha, telefone, ROLE_CLIENTE);
        this.ativo = true;
        this.agendamentosIds = new ArrayList<>();
    }
    
    /**
     * Verifica se o cliente possui agendamentos
     * 
     * @return true se possui agendamentos, false caso contrário
     */
    public boolean possuiAgendamentos() {
        return agendamentosIds != null && !agendamentosIds.isEmpty();
    }
    
    /**
     * Adiciona um agendamento ao histórico do cliente
     * 
     * @param agendamentoId ID do agendamento a ser adicionado
     */
    public void adicionarAgendamento(Long agendamentoId) {
        if (agendamentoId == null) {
            throw new IllegalArgumentException("ID do agendamento não pode ser nulo");
        }
        if (this.agendamentosIds == null) {
            this.agendamentosIds = new ArrayList<>();
        }
        this.agendamentosIds.add(agendamentoId);
    }
    
    /**
     * Ativa o cliente no sistema
     */
    public void ativar() {
        this.ativo = true;
        atualizarDataModificacao();
    }
    
    /**
     * Desativa o cliente no sistema (soft delete)
     */
    public void desativar() {
        this.ativo = false;
        atualizarDataModificacao();
    }
    
    // Getters e Setters específicos
    
    public List<Long> getAgendamentosIds() {
        return agendamentosIds;
    }
    
    public void setAgendamentosIds(List<Long> agendamentosIds) {
        this.agendamentosIds = agendamentosIds;
    }
    
    public boolean isAtivo() {
        return ativo;
    }
    
    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }
    
    /**
     * Sobrescreve o método equals para comparar clientes pelo email
     * Dois clientes são iguais se tiverem o mesmo email
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Cliente cliente = (Cliente) obj;
        return getEmail() != null && getEmail().equals(cliente.getEmail());
    }
    
    /**
     * Sobrescreve o método hashCode para ser consistente com equals
     */
    @Override
    public int hashCode() {
        return getEmail() != null ? getEmail().hashCode() : 0;
    }
    
    /**
     * Representação em String do cliente (útil para logs)
     */
    @Override
    public String toString() {
        return "Cliente{" +
                "id=" + getId() +
                ", nome='" + getNome() + '\'' +
                ", email='" + getEmail() + '\'' +
                ", telefone='" + getTelefone() + '\'' +
                ", ativo=" + ativo +
                ", totalAgendamentos=" + (agendamentosIds != null ? agendamentosIds.size() : 0) +
                '}';
    }
}
