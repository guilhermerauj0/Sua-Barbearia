package com.barbearia.infrastructure.persistence.entities;

import com.barbearia.domain.enums.TipoPerfil;
import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entidade JPA para Funcionario usando composição com perfis.
 * Não usa mais herança JOINED, armazena o perfil como enum.
 */
@Entity
@Table(name = "funcionarios")
public class JpaFuncionario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "barbearia_id", nullable = false)
    private Long barbeariaId;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false, unique = true)
    private String email;

    @Column
    private String telefone;

    @Enumerated(EnumType.STRING)
    @Column(name = "perfil_type", nullable = false, length = 20)
    private TipoPerfil perfilType;

    @Column(nullable = false)
    private boolean ativo;

    @Column(name = "data_criacao", nullable = false)
    private LocalDateTime dataCriacao;

    @Column(name = "data_atualizacao", nullable = false)
    private LocalDateTime dataAtualizacao;

    // Campos para sistema de links de acesso (migration V6)
    @Column(name = "access_token", unique = true)
    private String accessToken;

    @Column(name = "token_ativo", nullable = false)
    private Boolean tokenAtivo = false;

    @Column(name = "token_gerado_em")
    private LocalDateTime tokenGeradoEm;

    @Column(name = "token_expira_em")
    private LocalDateTime tokenExpiraEm;

    @PrePersist
    protected void onCreate() {
        dataCriacao = LocalDateTime.now();
        dataAtualizacao = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        dataAtualizacao = LocalDateTime.now();
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

    public TipoPerfil getPerfilType() {
        return perfilType;
    }

    public void setPerfilType(TipoPerfil perfilType) {
        this.perfilType = perfilType;
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
