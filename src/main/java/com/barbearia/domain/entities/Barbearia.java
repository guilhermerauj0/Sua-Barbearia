package com.barbearia.domain.entities;

import com.barbearia.domain.enums.TipoDocumento;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidade de domínio que representa uma Barbearia do sistema.
 * 
 * Barbearia é um tipo específico de Usuário que pode oferecer serviços e gerenciar agendamentos.
 * Esta classe estende Usuario e adiciona funcionalidades específicas de barbearias.
 * 
 * Uma barbearia pode ser:
 * - Pessoa Física (CPF): Barbeiro autônomo
 * - Pessoa Jurídica (CNPJ): Estabelecimento comercial
 * 
 * Princípios aplicados:
 * - Herança: Barbearia herda comportamentos comuns de Usuario
 * - Encapsulamento: Lógica de negócio específica de barbearias
 * - Single Responsibility: Barbearia só gerencia informações de estabelecimentos
 * 
 * @author Sua Barbearia Team
 */
public class Barbearia extends Usuario {
    
    /**
     * Constante que define o papel deste tipo de usuário
     */
    private static final String ROLE_BARBEARIA = "BARBEARIA";
    
    /**
     * Nome fantasia da barbearia
     */
    private String nomeFantasia;
    
    /**
     * Tipo de documento (CPF ou CNPJ)
     */
    private TipoDocumento tipoDocumento;
    
    /**
     * Número do documento (CPF ou CNPJ) sem formatação
     */
    private String documento;
    
    /**
     * Endereço completo da barbearia
     */
    private String endereco;
    
    /**
     * Lista de serviços oferecidos pela barbearia
     * (será implementada futuramente quando criar a entidade Servico)
     */
    private List<Long> servicosIds;
    
    /**
     * Lista de agendamentos da barbearia
     * (será implementada futuramente quando criar a entidade Agendamento)
     */
    private List<Long> agendamentosIds;
    
    /**
     * Indica se a barbearia está ativa no sistema
     */
    private boolean ativo;
    
    /**
     * Construtor padrão
     * Define automaticamente o role como BARBEARIA
     */
    public Barbearia() {
        super();
        super.setRole(ROLE_BARBEARIA);
        this.ativo = true;
        this.servicosIds = new ArrayList<>();
        this.agendamentosIds = new ArrayList<>();
    }
    
    /**
     * Construtor com parâmetros para criação de uma nova barbearia
     * 
     * @param nome Nome completo ou razão social
     * @param email Email da barbearia (único)
     * @param senha Senha que será armazenada com hash
     * @param telefone Telefone de contato
     * @param nomeFantasia Nome fantasia do estabelecimento
     * @param tipoDocumento Tipo do documento (CPF ou CNPJ)
     * @param documento Número do documento sem formatação
     * @param endereco Endereço completo
     */
    public Barbearia(String nome, String email, String senha, String telefone,
                     String nomeFantasia, TipoDocumento tipoDocumento, 
                     String documento, String endereco) {
        super(nome, email, senha, telefone, ROLE_BARBEARIA);
        this.nomeFantasia = nomeFantasia;
        this.tipoDocumento = tipoDocumento;
        this.documento = documento;
        this.endereco = endereco;
        this.ativo = true;
        this.servicosIds = new ArrayList<>();
        this.agendamentosIds = new ArrayList<>();
    }
    
    /**
     * Verifica se a barbearia possui serviços cadastrados
     * 
     * @return true se possui serviços, false caso contrário
     */
    public boolean possuiServicos() {
        return servicosIds != null && !servicosIds.isEmpty();
    }
    
    /**
     * Adiciona um serviço ao catálogo da barbearia
     * 
     * @param servicoId ID do serviço a ser adicionado
     */
    public void adicionarServico(Long servicoId) {
        if (servicoId == null) {
            throw new IllegalArgumentException("ID do serviço não pode ser nulo");
        }
        if (this.servicosIds == null) {
            this.servicosIds = new ArrayList<>();
        }
        this.servicosIds.add(servicoId);
    }
    
    /**
     * Verifica se a barbearia possui agendamentos
     * 
     * @return true se possui agendamentos, false caso contrário
     */
    public boolean possuiAgendamentos() {
        return agendamentosIds != null && !agendamentosIds.isEmpty();
    }
    
    /**
     * Adiciona um agendamento ao histórico da barbearia
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
     * Ativa a barbearia no sistema
     */
    public void ativar() {
        this.ativo = true;
        atualizarDataModificacao();
    }
    
    /**
     * Desativa a barbearia no sistema (soft delete)
     */
    public void desativar() {
        this.ativo = false;
        atualizarDataModificacao();
    }
    
    /**
     * Verifica se o documento é CPF
     * 
     * @return true se for CPF, false caso contrário
     */
    public boolean isCPF() {
        return TipoDocumento.CPF.equals(this.tipoDocumento);
    }
    
    /**
     * Verifica se o documento é CNPJ
     * 
     * @return true se for CNPJ, false caso contrário
     */
    public boolean isCNPJ() {
        return TipoDocumento.CNPJ.equals(this.tipoDocumento);
    }
    
    // Getters e Setters específicos
    
    public String getNomeFantasia() {
        return nomeFantasia;
    }
    
    public void setNomeFantasia(String nomeFantasia) {
        this.nomeFantasia = nomeFantasia;
    }
    
    public TipoDocumento getTipoDocumento() {
        return tipoDocumento;
    }
    
    public void setTipoDocumento(TipoDocumento tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }
    
    public String getDocumento() {
        return documento;
    }
    
    public void setDocumento(String documento) {
        this.documento = documento;
    }
    
    public String getEndereco() {
        return endereco;
    }
    
    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }
    
    public List<Long> getServicosIds() {
        return servicosIds;
    }
    
    public void setServicosIds(List<Long> servicosIds) {
        this.servicosIds = servicosIds;
    }
    
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
     * Sobrescreve o método equals para comparar barbearias pelo documento
     * Duas barbearias são iguais se tiverem o mesmo tipo de documento e número
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Barbearia barbearia = (Barbearia) obj;
        return tipoDocumento == barbearia.tipoDocumento && 
               documento != null && documento.equals(barbearia.documento);
    }
    
    /**
     * Sobrescreve o método hashCode para ser consistente com equals
     */
    @Override
    public int hashCode() {
        int result = tipoDocumento != null ? tipoDocumento.hashCode() : 0;
        result = 31 * result + (documento != null ? documento.hashCode() : 0);
        return result;
    }
    
    /**
     * Representação em String da barbearia (útil para logs)
     */
    @Override
    public String toString() {
        return "Barbearia{" +
                "id=" + getId() +
                ", nomeFantasia='" + nomeFantasia + '\'' +
                ", nome='" + getNome() + '\'' +
                ", email='" + getEmail() + '\'' +
                ", tipoDocumento=" + tipoDocumento +
                ", documento='" + documento + '\'' +
                ", telefone='" + getTelefone() + '\'' +
                ", ativo=" + ativo +
                ", totalServicos=" + (servicosIds != null ? servicosIds.size() : 0) +
                ", totalAgendamentos=" + (agendamentosIds != null ? agendamentosIds.size() : 0) +
                '}';
    }
}
