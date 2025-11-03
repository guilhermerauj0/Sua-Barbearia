package com.barbearia.application.factories;

import com.barbearia.application.dto.BarbeariaRequestDto;
import com.barbearia.domain.entities.Barbearia;
import com.barbearia.domain.entities.Cliente;
import com.barbearia.application.dto.ClienteRequestDto;

/**
 * Factory (Fábrica) para criação de objetos Usuario.
 * 
 * Padrão de Projeto Factory:
 * - Centraliza a lógica de criação de objetos complexos
 * - Facilita manutenção quando a criação se torna mais complexa
 * - Garante consistência na criação de objetos
 * 
 * Por que usar Factory?
 * - Se precisarmos adicionar validações ou inicializações complexas, fazemos aqui
 * - Mantém o código de criação em um único lugar
 * - Facilita testes unitários (podemos mockar a factory)
 * 
 * @author Sua Barbearia Team
 */
public class UsuarioFactory {
    
    /**
     * Cria um novo Cliente a partir de um DTO de requisição
     * 
     * IMPORTANTE: A senha recebida no DTO está em texto puro.
     * O hash da senha deve ser feito ANTES de chamar este método.
     * 
     * @param dto Dados do cliente vindo da requisição
     * @param senhaHash Senha já com hash aplicado
     * @return Nova instância de Cliente
     * @throws IllegalArgumentException se os dados forem inválidos
     */
    public static Cliente criarCliente(ClienteRequestDto dto, String senhaHash) {
        // Validações básicas
        validarDadosObrigatorios(dto, senhaHash);
        
        // Cria o cliente usando o construtor com parâmetros
        Cliente cliente = new Cliente(
            dto.getNome().trim(),
            dto.getEmail().toLowerCase().trim(), // Email sempre em minúsculo
            senhaHash,
            limparTelefone(dto.getTelefone())
        );
        
        return cliente;
    }
    
    /**
     * Cria um Cliente de forma simples (para casos de teste ou migrações)
     * 
     * @param nome Nome do cliente
     * @param email Email do cliente
     * @param senhaHash Senha com hash
     * @param telefone Telefone do cliente
     * @return Nova instância de Cliente
     */
    public static Cliente criarCliente(String nome, String email, String senhaHash, String telefone) {
        validarParametros(nome, email, senhaHash, telefone);
        
        return new Cliente(
            nome.trim(),
            email.toLowerCase().trim(),
            senhaHash,
            limparTelefone(telefone)
        );
    }
    
    /**
     * Valida se todos os dados obrigatórios estão presentes
     * 
     * @param dto DTO com dados do cliente
     * @param senhaHash Senha com hash
     * @throws IllegalArgumentException se algum dado estiver inválido
     */
    private static void validarDadosObrigatorios(ClienteRequestDto dto, String senhaHash) {
        if (dto == null) {
            throw new IllegalArgumentException("DTO não pode ser nulo");
        }
        
        if (senhaHash == null || senhaHash.trim().isEmpty()) {
            throw new IllegalArgumentException("Senha com hash é obrigatória");
        }
        
        validarParametros(dto.getNome(), dto.getEmail(), senhaHash, dto.getTelefone());
    }
    
    /**
     * Valida parâmetros individuais
     * 
     * @param nome Nome do cliente
     * @param email Email do cliente
     * @param senha Senha (ou hash) do cliente
     * @param telefone Telefone do cliente
     * @throws IllegalArgumentException se algum parâmetro for inválido
     */
    private static void validarParametros(String nome, String email, String senha, String telefone) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome é obrigatório");
        }
        
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email é obrigatório");
        }
        
        if (senha == null || senha.trim().isEmpty()) {
            throw new IllegalArgumentException("Senha é obrigatória");
        }
        
        if (telefone == null || telefone.trim().isEmpty()) {
            throw new IllegalArgumentException("Telefone é obrigatório");
        }
    }
    
    /**
     * Remove caracteres especiais do telefone, mantendo apenas números
     * Exemplo: (11) 98765-4321 -> 11987654321
     * 
     * @param telefone Telefone com formatação
     * @return Telefone apenas com números
     */
    private static String limparTelefone(String telefone) {
        if (telefone == null) {
            return null;
        }
        // Remove tudo que não for número
        return telefone.replaceAll("[^0-9]", "");
    }
    
    /**
     * Cria uma nova Barbearia a partir de um DTO de requisição
     * 
     * IMPORTANTE: A senha recebida no DTO está em texto puro.
     * O hash da senha deve ser feito ANTES de chamar este método.
     * O documento já deve estar validado antes de chamar este método.
     * 
     * @param dto Dados da barbearia vindo da requisição
     * @param senhaHash Senha já com hash aplicado
     * @return Nova instância de Barbearia
     * @throws IllegalArgumentException se os dados forem inválidos
     */
    public static Barbearia criarBarbearia(BarbeariaRequestDto dto, String senhaHash) {
        // Validações básicas
        validarDadosBarbeariaObrigatorios(dto, senhaHash);
        
        // Limpa o documento (remove formatação)
        String documentoLimpo = limparDocumento(dto.getDocumento());
        
        // Cria a barbearia usando o construtor com parâmetros
        Barbearia barbearia = new Barbearia(
            dto.getNome().trim(),
            dto.getEmail().toLowerCase().trim(), // Email sempre em minúsculo
            senhaHash,
            limparTelefone(dto.getTelefone()),
            dto.getNomeFantasia().trim(),
            dto.getTipoDocumento(),
            documentoLimpo,
            dto.getEndereco().trim()
        );
        
        return barbearia;
    }
    
    /**
     * Valida se todos os dados obrigatórios da barbearia estão presentes
     * 
     * @param dto DTO com dados da barbearia
     * @param senhaHash Senha com hash
     * @throws IllegalArgumentException se algum dado estiver inválido
     */
    private static void validarDadosBarbeariaObrigatorios(BarbeariaRequestDto dto, String senhaHash) {
        if (dto == null) {
            throw new IllegalArgumentException("DTO não pode ser nulo");
        }
        
        if (senhaHash == null || senhaHash.trim().isEmpty()) {
            throw new IllegalArgumentException("Senha com hash é obrigatória");
        }
        
        validarParametrosBarbearia(
            dto.getNome(),
            dto.getEmail(),
            senhaHash,
            dto.getTelefone(),
            dto.getNomeFantasia(),
            dto.getDocumento(),
            dto.getEndereco()
        );
    }
    
    /**
     * Valida parâmetros individuais da barbearia
     */
    private static void validarParametrosBarbearia(String nome, String email, String senha,
                                                   String telefone, String nomeFantasia,
                                                   String documento, String endereco) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome é obrigatório");
        }
        
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email é obrigatório");
        }
        
        if (senha == null || senha.trim().isEmpty()) {
            throw new IllegalArgumentException("Senha é obrigatória");
        }
        
        if (telefone == null || telefone.trim().isEmpty()) {
            throw new IllegalArgumentException("Telefone é obrigatório");
        }
        
        if (nomeFantasia == null || nomeFantasia.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome fantasia é obrigatório");
        }
        
        if (documento == null || documento.trim().isEmpty()) {
            throw new IllegalArgumentException("Documento é obrigatório");
        }
        
        if (endereco == null || endereco.trim().isEmpty()) {
            throw new IllegalArgumentException("Endereço é obrigatório");
        }
    }
    
    /**
     * Remove caracteres especiais do documento, mantendo apenas números
     * Exemplo: 123.456.789-00 -> 12345678900
     * 
     * @param documento Documento com formatação
     * @return Documento apenas com números
     */
    private static String limparDocumento(String documento) {
        if (documento == null) {
            return null;
        }
        return documento.replaceAll("[^0-9]", "");
    }
}
