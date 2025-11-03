package com.barbearia.application.dto;

import com.barbearia.domain.enums.TipoDocumento;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * DTO (Data Transfer Object) para requisição de registro de barbearia.
 * 
 * Este objeto transporta dados da requisição HTTP até a camada de serviço.
 * Contém validações usando Bean Validation (Jakarta Validation).
 * 
 * Validações implementadas:
 * - Nome: obrigatório, 3-100 caracteres
 * - Email: obrigatório, formato válido
 * - Senha: obrigatória, mínimo 8 caracteres, com complexidade
 * - Telefone: obrigatório, formato brasileiro
 * - Nome Fantasia: obrigatório, 3-100 caracteres
 * - Tipo de Documento: obrigatório (CPF ou CNPJ)
 * - Documento: obrigatório, formato validado
 * - Endereço: obrigatório, 5-200 caracteres
 * 
 * @author Sua Barbearia Team
 */
public class BarbeariaRequestDto {
    
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
    
    @NotBlank(message = "Nome fantasia é obrigatório")
    @Size(min = 3, max = 100, message = "Nome fantasia deve ter entre 3 e 100 caracteres")
    private String nomeFantasia;
    
    @NotNull(message = "Tipo de documento é obrigatório")
    private TipoDocumento tipoDocumento;
    
    @NotBlank(message = "Documento é obrigatório")
    private String documento;
    
    @NotBlank(message = "Endereço é obrigatório")
    @Size(min = 5, max = 200, message = "Endereço deve ter entre 5 e 200 caracteres")
    private String endereco;
    
    /**
     * Construtor padrão
     */
    public BarbeariaRequestDto() {
    }
    
    /**
     * Construtor com todos os parâmetros
     */
    public BarbeariaRequestDto(String nome, String email, String senha, String confirmarSenha,
                               String telefone, String nomeFantasia, TipoDocumento tipoDocumento,
                               String documento, String endereco) {
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.confirmarSenha = confirmarSenha;
        this.telefone = telefone;
        this.nomeFantasia = nomeFantasia;
        this.tipoDocumento = tipoDocumento;
        this.documento = documento;
        this.endereco = endereco;
    }
    
    /**
     * Verifica se a senha e confirmação são iguais
     * 
     * @return true se as senhas conferem, false caso contrário
     */
    public boolean senhasConferem() {
        return senha != null && senha.equals(confirmarSenha);
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
    
    @Override
    public String toString() {
        return "BarbeariaRequestDto{" +
                "nome='" + nome + '\'' +
                ", email='" + email + '\'' +
                ", telefone='" + telefone + '\'' +
                ", nomeFantasia='" + nomeFantasia + '\'' +
                ", tipoDocumento=" + tipoDocumento +
                ", documento='" + documento + '\'' +
                ", endereco='" + endereco + '\'' +
                ", senha=[PROTEGIDO]" +
                '}';
    }
}
