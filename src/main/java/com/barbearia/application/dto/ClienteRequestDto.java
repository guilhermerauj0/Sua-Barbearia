package com.barbearia.application.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class ClienteRequestDto {
    
    @NotBlank(message = "Nome é obrigatório")
    @Size(min = 3, max = 100, message = "Nome deve ter entre 3 e 100 caracteres")
    private String nome;
    
    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email deve ser válido")
    private String email;
    
    @NotBlank(message = "Senha é obrigatória")
    @Size(min = 8, message = "Senha deve ter no mínimo 8 caracteres")
    @Pattern(
        regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&#])[A-Za-z\\d@$!%*?&#]{8,}$",
        message = "Senha deve conter pelo menos uma letra maiúscula, uma minúscula, um número e um caractere especial"
    )
    private String senha;
    
    @NotBlank(message = "Confirmação de senha é obrigatória")
    private String confirmarSenha;
    
    @NotBlank(message = "Telefone é obrigatório")
    @Pattern(
        regexp = "^\\(?\\d{2}\\)?\\s?\\d{4,5}-?\\d{4}$",
        message = "Telefone deve estar no formato (XX) XXXXX-XXXX ou (XX) XXXX-XXXX"
    )
    private String telefone;
    
    public ClienteRequestDto() {
    }
    
    public ClienteRequestDto(String nome, String email, String senha, String confirmarSenha, String telefone) {
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.confirmarSenha = confirmarSenha;
        this.telefone = telefone;
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
    
    public String getSenha() {
        return senha;
    }
    
    public void setSenha(String senha) {
        this.senha = senha;
    }
    
    public String getConfirmarSenha() {
        return confirmarSenha;
    }
    
    public void setConfirmarSenha(String confirmarSenha) {
        this.confirmarSenha = confirmarSenha;
    }
    
    public String getTelefone() {
        return telefone;
    }
    
    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }
    
    public boolean senhasConferem() {
        return senha != null && senha.equals(confirmarSenha);
    }
    
    @Override
    public String toString() {
        return "ClienteRequestDto{" +
                "nome='" + nome + '\'' +
                ", email='" + email + '\'' +
                ", telefone='" + telefone + '\'' +
                ", senha=[PROTEGIDO]" +
                '}';
    }
}
