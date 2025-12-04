package com.barbearia.application.dto;

/**
 * DTO para estatísticas de avaliações de uma barbearia.
 * 
 * Fornece visão completa das avaliações com médias por aspecto.
 */
public class EstatisticasAvaliacoesDto {

    private Long barbeariaId;
    private Double mediaGeral;
    private Double mediaServico;
    private Double mediaAmbiente;
    private Double mediaLimpeza;
    private Double mediaAtendimento;
    private Long totalAvaliacoes;

    // Distribuição de notas (quantas avaliações por nota)
    private Long avaliacoes1Estrela;
    private Long avaliacoes2Estrelas;
    private Long avaliacoes3Estrelas;
    private Long avaliacoes4Estrelas;
    private Long avaliacoes5Estrelas;

    public EstatisticasAvaliacoesDto() {
    }

    public EstatisticasAvaliacoesDto(Long barbeariaId, Double mediaGeral, Double mediaServico,
            Double mediaAmbiente, Double mediaLimpeza, Double mediaAtendimento,
            Long totalAvaliacoes) {
        this.barbeariaId = barbeariaId;
        this.mediaGeral = mediaGeral;
        this.mediaServico = mediaServico;
        this.mediaAmbiente = mediaAmbiente;
        this.mediaLimpeza = mediaLimpeza;
        this.mediaAtendimento = mediaAtendimento;
        this.totalAvaliacoes = totalAvaliacoes;
    }

    // Getters e Setters

    public Long getBarbeariaId() {
        return barbeariaId;
    }

    public void setBarbeariaId(Long barbeariaId) {
        this.barbeariaId = barbeariaId;
    }

    public Double getMediaGeral() {
        return mediaGeral;
    }

    public void setMediaGeral(Double mediaGeral) {
        this.mediaGeral = mediaGeral;
    }

    public Double getMediaServico() {
        return mediaServico;
    }

    public void setMediaServico(Double mediaServico) {
        this.mediaServico = mediaServico;
    }

    public Double getMediaAmbiente() {
        return mediaAmbiente;
    }

    public void setMediaAmbiente(Double mediaAmbiente) {
        this.mediaAmbiente = mediaAmbiente;
    }

    public Double getMediaLimpeza() {
        return mediaLimpeza;
    }

    public void setMediaLimpeza(Double mediaLimpeza) {
        this.mediaLimpeza = mediaLimpeza;
    }

    public Double getMediaAtendimento() {
        return mediaAtendimento;
    }

    public void setMediaAtendimento(Double mediaAtendimento) {
        this.mediaAtendimento = mediaAtendimento;
    }

    public Long getTotalAvaliacoes() {
        return totalAvaliacoes;
    }

    public void setTotalAvaliacoes(Long totalAvaliacoes) {
        this.totalAvaliacoes = totalAvaliacoes;
    }

    public Long getAvaliacoes1Estrela() {
        return avaliacoes1Estrela;
    }

    public void setAvaliacoes1Estrela(Long avaliacoes1Estrela) {
        this.avaliacoes1Estrela = avaliacoes1Estrela;
    }

    public Long getAvaliacoes2Estrelas() {
        return avaliacoes2Estrelas;
    }

    public void setAvaliacoes2Estrelas(Long avaliacoes2Estrelas) {
        this.avaliacoes2Estrelas = avaliacoes2Estrelas;
    }

    public Long getAvaliacoes3Estrelas() {
        return avaliacoes3Estrelas;
    }

    public void setAvaliacoes3Estrelas(Long avaliacoes3Estrelas) {
        this.avaliacoes3Estrelas = avaliacoes3Estrelas;
    }

    public Long getAvaliacoes4Estrelas() {
        return avaliacoes4Estrelas;
    }

    public void setAvaliacoes4Estrelas(Long avaliacoes4Estrelas) {
        this.avaliacoes4Estrelas = avaliacoes4Estrelas;
    }

    public Long getAvaliacoes5Estrelas() {
        return avaliacoes5Estrelas;
    }

    public void setAvaliacoes5Estrelas(Long avaliacoes5Estrelas) {
        this.avaliacoes5Estrelas = avaliacoes5Estrelas;
    }
}
