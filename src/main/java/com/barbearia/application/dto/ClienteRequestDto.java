package com.barbearia.application.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * DTO (Data Transfer Object) para requisição de registro de cliente.
 * 
 * Este objeto é usado para receber dados do cliente via API.
 * Contém validações usando Bean Validation para garantir integridade dos dados.
 * 
 * Por que usar DTO?
 * - Separa a API (entrada/saída) do domínio interno
 * - Permite validações específicas da camada de apresentação
 * - Evita expor estrutura interna das entidades
 * - Facilita versionamento da API
 * 
 * @author Sua Barbearia Team
 */
public class ClienteRequestDto {
    
    /**
     * Nome completo do cliente
     * Validações:
     * - Não pode ser vazio ou nulo
     * - Mínimo 3 caracteres
     * - Máximo 100 caracteres
     */
    @NotBlank(message = "Nome é obrigatório")
    @Size(min = 3, max = 100, message = "Nome deve ter entre 3 e 100 caracteres")
    private String nome;
    
    /**
     * Email do cliente
     * Validações:
     * - Não pode ser vazio ou nulo
     * - Deve ter formato válido de email
     * - Unicidade será verificada no service
     */
    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email deve ser válido")
    private String email;
    
    /**
     * Senha do cliente (em texto puro - será feito hash no service)
     * Validações:
     * - Não pode ser vazia ou nula
     * - Mínimo 8 caracteres
     * - Deve conter pelo menos: 1 letra maiúscula, 1 minúscula, 1 número e 1 caractere especial
     */
    @NotBlank(message = "Senha é obrigatória")
    @Size(min = 8, message = "Senha deve ter no mínimo 8 caracteres")
    @Pattern(
        regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&#])[A-Za-z\\d@$!%*?&#]{8,}$",
        message = "Senha deve conter pelo menos uma letra maiúscula, uma minúscula, um número e um caractere especial"
    )
    private String senha;
    
    /**
     * Confirmação da senha (deve ser igual à senha)
     */
    @NotBlank(message = "Confirmação de senha é obrigatória")
    private String confirmarSenha;
    
    /**
     * Telefone do cliente
     * Validações:
     * - Não pode ser vazio ou nulo
     * - Deve conter apenas números
     * - Formato: (XX) XXXXX-XXXX ou (XX) XXXX-XXXX
     */
    @NotBlank(message = "Telefone é obrigatório")
    @Pattern(
        regexp = "^\\(?\\d{2}\\)?\\s?\\d{4,5}-?\\d{4}$",
        message = "Telefone deve estar no formato (XX) XXXXX-XXXX ou (XX) XXXX-XXXX"
    )
    private String telefone;
    
    // Construtores
    
    public ClienteRequestDto() {
    }
    
    public ClienteRequestDto(String nome, String email, String senha, String confirmarSenha, String telefone) {
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.confirmarSenha = confirmarSenha;
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
    
    /**
     * Valida se a senha e a confirmação são iguais
     * Este método deve ser chamado no service antes de processar o registro
     * 
     * @return true se as senhas coincidem, false caso contrário
     */
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
