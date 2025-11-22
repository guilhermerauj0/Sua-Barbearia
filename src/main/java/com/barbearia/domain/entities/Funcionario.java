package com.barbearia.domain.entities;

import com.barbearia.domain.enums.TipoPerfil;
import java.time.LocalDateTime;

/**
 * Classe que representa um funcionário da barbearia.
 * 
 * Cada funcionário possui um perfil profissional (barbeiro, manicure,
 * esteticista, colorista)
 * que define suas particularidades e permissões.
 * Usa composição em vez de herança para maior flexibilidade.
 * 
 * Conceitos POO:
 * - Composição: "tem um" perfil em vez de "é um" tipo específico
 * - Encapsulamento: atributos privados e delegação para perfil
 * - Polimorfismo: delega comportamentos para o perfil
 * 
 * @author Sua Barbearia Team
 */
public class Funcionario {

    private Long id;
    private Long barbeariaId;
    private String nome;
    private String email;
    private String telefone;
    private boolean ativo;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataAtualizacao;
    private TipoPerfil tipoPerfil;
    private transient Perfil perfil; // Não persistido, criado sob demanda

    // Campos para sistema de links de acesso (sem senha)
    private String accessToken;
    private Boolean tokenAtivo;
    private LocalDateTime tokenGeradoEm;
    private LocalDateTime tokenExpiraEm; // null = sem expiração

    public Funcionario() {
    }

    public Funcionario(Long barbeariaId, String nome, String email, String telefone, TipoPerfil tipoPerfil) {
        this.barbeariaId = barbeariaId;
        this.nome = nome;
        this.email = email;
        this.telefone = telefone;
        this.tipoPerfil = tipoPerfil;
        this.ativo = true;
        this.dataCriacao = LocalDateTime.now();
        this.dataAtualizacao = LocalDateTime.now();
    }

    public Funcionario(Long id, String nome, String email, String telefone, Long barbeariaId, boolean ativo,
            TipoPerfil tipoPerfil) {
        this.id = id;
        this.barbeariaId = barbeariaId;
        this.nome = nome;
        this.email = email;
        this.telefone = telefone;
        this.ativo = ativo;
        this.tipoPerfil = tipoPerfil;
        this.dataCriacao = LocalDateTime.now();
        this.dataAtualizacao = LocalDateTime.now();
    }

    /**
     * Obtém o perfil do funcionário.
     * Se não existir, cria baseado no tipoPerfil.
     * 
     * @return perfil do funcionário
     */
    private Perfil obterPerfil() {
        if (perfil == null && tipoPerfil != null) {
            perfil = criarPerfil(tipoPerfil);
        }
        return perfil;
    }

    /**
     * Factory method para criar perfil baseado no tipo.
     * 
     * @param tipo tipo do perfil
     * @return instância do perfil
     */
    private Perfil criarPerfil(TipoPerfil tipo) {
        switch (tipo) {
            case BARBEIRO:
                return new PerfilBarbeiro();
            case MANICURE:
                return new PerfilManicure();
            case ESTETICISTA:
                return new PerfilEsteticista();
            case COLORISTA:
                return new PerfilColorista();
            default:
                throw new IllegalArgumentException("Tipo de perfil desconhecido: " + tipo);
        }
    }

    /**
     * Retorna a profissão do funcionário.
     * Delega para o perfil.
     * 
     * @return tipo de profissão (BARBEIRO, MANICURE, ESTETICISTA, COLORISTA)
     */
    public String getProfissao() {
        Perfil p = obterPerfil();
        return p != null ? p.getProfissao() : "DESCONHECIDA";
    }

    /**
     * Verifica se o funcionário pode realizar um serviço específico.
     * Delega para o perfil.
     * 
     * @param servico o serviço a ser verificado
     * @return true se pode realizar, false caso contrário
     */
    public boolean podeRealizarServico(Servico servico) {
        verificarAtivo();
        Perfil p = obterPerfil();
        return p != null && p.podeRealizarServico(servico);
    }

    /**
     * Calcula a comissão do funcionário sobre o valor do serviço.
     * Delega para o perfil.
     * 
     * @param valorServico valor do serviço realizado
     * @return valor da comissão
     */
    public double calcularComissao(double valorServico) {
        Perfil p = obterPerfil();
        return p != null ? p.calcularComissao(valorServico) : 0.0;
    }

    /**
     * Valida se o funcionário está ativo.
     * 
     * @throws IllegalStateException se o funcionário não estiver ativo
     */
    public void verificarAtivo() {
        if (!ativo) {
            throw new IllegalStateException("Funcionário inativo não pode realizar ações.");
        }
    }

    /**
     * Ativa o funcionário e atualiza a data de modificação.
     */
    public void ativar() {
        this.ativo = true;
        atualizarDataModificacao();
    }

    /**
     * Desativa o funcionário e atualiza a data de modificação.
     */
    public void desativar() {
        this.ativo = false;
        atualizarDataModificacao();
    }

    /**
     * Atualiza a data de modificação para agora.
     */
    private void atualizarDataModificacao() {
        this.dataAtualizacao = LocalDateTime.now();
    }

    /**
     * Gera um novo token UUID único para acesso via link.
     * Marca como ativo e registra data de geração.
     * 
     * @return o token gerado
     */
    public String gerarNovoToken() {
        this.accessToken = java.util.UUID.randomUUID().toString();
        this.tokenAtivo = true;
        this.tokenGeradoEm = LocalDateTime.now();
        atualizarDataModificacao();
        return this.accessToken;
    }

    /**
     * Define a data de expiração do token.
     * Se null, o token não expira.
     * 
     * @param dataExpiracao data/hora de expiração ou null
     */
    public void definirExpiracao(LocalDateTime dataExpiracao) {
        this.tokenExpiraEm = dataExpiracao;
        atualizarDataModificacao();
    }

    /**
     * Ativa o token de acesso.
     */
    public void ativarToken() {
        if (this.accessToken == null) {
            throw new IllegalStateException("Token não foi gerado ainda");
        }
        this.tokenAtivo = true;
        atualizarDataModificacao();
    }

    /**
     * Desativa o token de acesso.
     */
    public void desativarToken() {
        this.tokenAtivo = false;
        atualizarDataModificacao();
    }

    /**
     * Verifica se o token é válido (ativo E não expirado).
     * 
     * @return true se válido, false caso contrário
     */
    public boolean verificarTokenValido() {
        if (this.accessToken == null || this.tokenAtivo == null || !this.tokenAtivo) {
            return false;
        }

        if (this.tokenExpiraEm != null && LocalDateTime.now().isAfter(this.tokenExpiraEm)) {
            return false; // Token expirado
        }

        return true;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        Funcionario that = (Funcionario) obj;
        return email != null ? email.equals(that.email) : that.email == null;
    }

    @Override
    public int hashCode() {
        return email != null ? email.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "Funcionario{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", email='" + email + '\'' +
                ", tipoPerfil=" + tipoPerfil +
                ", profissao='" + getProfissao() + '\'' +
                ", ativo=" + ativo +
                '}';
    }

    // Getters e Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getBarbeariaId() {
        return barbeariaId;
    }

    public void setBarbeariaId(Long barbeariaId) {
        this.barbeariaId = barbeariaId;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(LocalDateTime dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public LocalDateTime getDataAtualizacao() {
        return dataAtualizacao;
    }

    public void setDataAtualizacao(LocalDateTime dataAtualizacao) {
        this.dataAtualizacao = dataAtualizacao;
    }

    public TipoPerfil getTipoPerfil() {
        return tipoPerfil;
    }

    public void setTipoPerfil(TipoPerfil tipoPerfil) {
        this.tipoPerfil = tipoPerfil;
        this.perfil = null; // Limpa perfil para recriar com novo tipo
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public Boolean getTokenAtivo() {
        return tokenAtivo;
    }

    public void setTokenAtivo(Boolean tokenAtivo) {
        this.tokenAtivo = tokenAtivo;
    }

    public LocalDateTime getTokenGeradoEm() {
        return tokenGeradoEm;
    }

    public void setTokenGeradoEm(LocalDateTime tokenGeradoEm) {
        this.tokenGeradoEm = tokenGeradoEm;
    }

    public LocalDateTime getTokenExpiraEm() {
        return tokenExpiraEm;
    }

    public void setTokenExpiraEm(LocalDateTime tokenExpiraEm) {
        this.tokenExpiraEm = tokenExpiraEm;
    }
}
