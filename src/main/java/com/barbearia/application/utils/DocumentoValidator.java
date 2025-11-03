package com.barbearia.application.utils;

import com.barbearia.domain.enums.TipoDocumento;

/**
 * Classe utilitária para validação de documentos (CPF e CNPJ).
 * 
 * Implementa os algoritmos oficiais de validação de:
 * - CPF: Cadastro de Pessoas Físicas (11 dígitos)
 * - CNPJ: Cadastro Nacional de Pessoa Jurídica (14 dígitos)
 * 
 * Validações realizadas:
 * 1. Formato e tamanho corretos
 * 2. Dígitos verificadores (algoritmo módulo 11)
 * 3. Sequências inválidas (ex: 111.111.111-11, 000.000.000-00)
 * 
 * Por que validar?
 * - Garantir integridade dos dados
 * - Evitar cadastros com documentos inválidos
 * - Conformidade com regras de negócio brasileiras
 * 
 * @author Sua Barbearia Team
 */
public class DocumentoValidator {
    
    /**
     * Valida um documento baseado no seu tipo
     * 
     * @param documento Número do documento (pode conter formatação)
     * @param tipo Tipo do documento (CPF ou CNPJ)
     * @return true se o documento é válido, false caso contrário
     */
    public static boolean validar(String documento, TipoDocumento tipo) {
        if (documento == null || tipo == null) {
            return false;
        }
        
        // Remove formatação (pontos, traços, barras)
        String documentoLimpo = documento.replaceAll("[^0-9]", "");
        
        return switch (tipo) {
            case CPF -> validarCPF(documentoLimpo);
            case CNPJ -> validarCNPJ(documentoLimpo);
        };
    }
    
    /**
     * Valida um CPF (Cadastro de Pessoa Física)
     * 
     * Algoritmo:
     * 1. Remove formatação
     * 2. Verifica se tem 11 dígitos
     * 3. Verifica se não é sequência inválida (111.111.111-11, etc)
     * 4. Calcula e valida o primeiro dígito verificador
     * 5. Calcula e valida o segundo dígito verificador
     * 
     * @param cpf CPF sem formatação (apenas números)
     * @return true se o CPF é válido, false caso contrário
     */
    public static boolean validarCPF(String cpf) {
        // Verifica se tem 11 dígitos
        if (cpf == null || cpf.length() != 11) {
            return false;
        }
        
        // Verifica se não é sequência inválida (todos os dígitos iguais)
        if (cpf.matches("(\\d)\\1{10}")) {
            return false;
        }
        
        try {
            // Calcula o primeiro dígito verificador
            int soma = 0;
            for (int i = 0; i < 9; i++) {
                soma += Character.getNumericValue(cpf.charAt(i)) * (10 - i);
            }
            int primeiroDigito = 11 - (soma % 11);
            if (primeiroDigito >= 10) {
                primeiroDigito = 0;
            }
            
            // Verifica o primeiro dígito
            if (primeiroDigito != Character.getNumericValue(cpf.charAt(9))) {
                return false;
            }
            
            // Calcula o segundo dígito verificador
            soma = 0;
            for (int i = 0; i < 10; i++) {
                soma += Character.getNumericValue(cpf.charAt(i)) * (11 - i);
            }
            int segundoDigito = 11 - (soma % 11);
            if (segundoDigito >= 10) {
                segundoDigito = 0;
            }
            
            // Verifica o segundo dígito
            return segundoDigito == Character.getNumericValue(cpf.charAt(10));
            
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Valida um CNPJ (Cadastro Nacional de Pessoa Jurídica)
     * 
     * Algoritmo:
     * 1. Remove formatação
     * 2. Verifica se tem 14 dígitos
     * 3. Verifica se não é sequência inválida (00.000.000/0000-00, etc)
     * 4. Calcula e valida o primeiro dígito verificador
     * 5. Calcula e valida o segundo dígito verificador
     * 
     * @param cnpj CNPJ sem formatação (apenas números)
     * @return true se o CNPJ é válido, false caso contrário
     */
    public static boolean validarCNPJ(String cnpj) {
        // Verifica se tem 14 dígitos
        if (cnpj == null || cnpj.length() != 14) {
            return false;
        }
        
        // Verifica se não é sequência inválida (todos os dígitos iguais)
        if (cnpj.matches("(\\d)\\1{13}")) {
            return false;
        }
        
        try {
            // Pesos para o primeiro dígito verificador
            int[] pesosPrimeiro = {5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
            
            // Calcula o primeiro dígito verificador
            int soma = 0;
            for (int i = 0; i < 12; i++) {
                soma += Character.getNumericValue(cnpj.charAt(i)) * pesosPrimeiro[i];
            }
            int primeiroDigito = soma % 11;
            primeiroDigito = (primeiroDigito < 2) ? 0 : 11 - primeiroDigito;
            
            // Verifica o primeiro dígito
            if (primeiroDigito != Character.getNumericValue(cnpj.charAt(12))) {
                return false;
            }
            
            // Pesos para o segundo dígito verificador
            int[] pesosSegundo = {6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
            
            // Calcula o segundo dígito verificador
            soma = 0;
            for (int i = 0; i < 13; i++) {
                soma += Character.getNumericValue(cnpj.charAt(i)) * pesosSegundo[i];
            }
            int segundoDigito = soma % 11;
            segundoDigito = (segundoDigito < 2) ? 0 : 11 - segundoDigito;
            
            // Verifica o segundo dígito
            return segundoDigito == Character.getNumericValue(cnpj.charAt(13));
            
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Remove a formatação de um documento, deixando apenas os números
     * 
     * @param documento Documento com ou sem formatação
     * @return Documento apenas com números
     */
    public static String limparDocumento(String documento) {
        if (documento == null) {
            return null;
        }
        return documento.replaceAll("[^0-9]", "");
    }
}
