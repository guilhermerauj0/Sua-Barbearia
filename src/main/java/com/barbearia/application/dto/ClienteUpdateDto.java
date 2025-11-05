package com.barbearia.application.dto;

/**
 * DTO para atualização de dados do cliente.
 * 
 * Contém apenas os campos que podem ser atualizados pelo cliente:
 * - nome
 * - email
 * - telefone
 * 
 * Campos como role, id e datas são imutáveis e não devem ser inclusos aqui.
 * Todos os campos são opcionais para permitir atualização parcial.
 * 
 * @author Sua Barbearia Team
 */
public class ClienteUpdateDto {
    
    private String nome;
    private String email;
    private String telefone;
    
    public ClienteUpdateDto() {
    }
    
    public ClienteUpdateDto(String nome, String email, String telefone) {
        this.nome = nome;
        this.email = email;
        this.telefone = telefone;
    }
    
    // Getters e Setters
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
    
    /**
     * Verifica se algum campo foi preenchido para atualização.
     * 
     * @return true se pelo menos um campo foi fornecido
     */
    public boolean temCamposParaAtualizar() {
        return nome != null || email != null || telefone != null;
    }
    
    @Override
    public String toString() {
        return "ClienteUpdateDto{" +
                "nome='" + nome + '\'' +
                ", email='" + email + '\'' +
                ", telefone='" + telefone + '\'' +
                '}';
    }
}
