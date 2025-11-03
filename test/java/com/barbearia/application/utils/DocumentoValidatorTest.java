package com.barbearia.application.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes unitários para a classe DocumentoValidator.
 * 
 * Testa a validação de CPF e CNPJ seguindo o algoritmo módulo 11.
 */
@DisplayName("DocumentoValidator - Testes de Validação")
class DocumentoValidatorTest {

    @Test
    @DisplayName("Deve validar CPF válido com formatação")
    void deveValidarCPFValidoComFormatacao() {
        // CPF: 191.000.000-01 = 19100000001
        String cpf = "191.000.000-01".replaceAll("[^0-9]", "");
        assertTrue(DocumentoValidator.validarCPF(cpf));
    }

    @Test
    @DisplayName("Deve validar CPF válido sem formatação")
    void deveValidarCPFValidoSemFormatacao() {
        assertTrue(DocumentoValidator.validarCPF("19100000001"));
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "111.111.111-11",
        "000.000.000-00",
        "222.222.222-22",
        "333.333.333-33",
        "444.444.444-44",
        "555.555.555-55",
        "666.666.666-66",
        "777.777.777-77",
        "888.888.888-88",
        "999.999.999-99"
    })
    @DisplayName("Deve rejeitar CPF com dígitos repetidos")
    void deveRejeitarCPFComDigitosRepetidos(String cpf) {
        assertFalse(DocumentoValidator.validarCPF(cpf));
    }

    @Test
    @DisplayName("Deve rejeitar CPF com tamanho inválido")
    void deveRejeitarCPFComTamanhoInvalido() {
        assertFalse(DocumentoValidator.validarCPF("123.456.789"));
        assertFalse(DocumentoValidator.validarCPF("123.456.789-099"));
        assertFalse(DocumentoValidator.validarCPF("123"));
    }

    @Test
    @DisplayName("Deve rejeitar CPF nulo ou vazio")
    void deveRejeitarCPFNuloOuVazio() {
        assertFalse(DocumentoValidator.validarCPF(null));
        assertFalse(DocumentoValidator.validarCPF(""));
        assertFalse(DocumentoValidator.validarCPF("   "));
    }

    @Test
    @DisplayName("Deve rejeitar CPF com dígitos verificadores inválidos")
    void deveRejeitarCPFComDigitosVerificadoresInvalidos() {
        assertFalse(DocumentoValidator.validarCPF("123.456.789-00"));
        assertFalse(DocumentoValidator.validarCPF("123.456.789-10"));
        assertFalse(DocumentoValidator.validarCPF("987.654.321-00"));
    }

    @Test
    @DisplayName("Deve validar CNPJ válido com formatação")
    void deveValidarCNPJValidoComFormatacao() {
        // CNPJ: 11.444.777/0001-61 = 11444777000161
        String cnpj = "11.444.777/0001-61".replaceAll("[^0-9]", "");
        assertTrue(DocumentoValidator.validarCNPJ(cnpj));
    }

    @Test
    @DisplayName("Deve validar CNPJ válido sem formatação")
    void deveValidarCNPJValidoSemFormatacao() {
        assertTrue(DocumentoValidator.validarCNPJ("11444777000161"));
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "00.000.000/0000-00",
        "11.111.111/1111-11",
        "22.222.222/2222-22",
        "33.333.333/3333-33",
        "44.444.444/4444-44",
        "55.555.555/5555-55",
        "66.666.666/6666-66",
        "77.777.777/7777-77",
        "88.888.888/8888-88",
        "99.999.999/9999-99"
    })
    @DisplayName("Deve rejeitar CNPJ com dígitos repetidos")
    void deveRejeitarCNPJComDigitosRepetidos(String cnpj) {
        assertFalse(DocumentoValidator.validarCNPJ(cnpj));
    }

    @Test
    @DisplayName("Deve rejeitar CNPJ com tamanho inválido")
    void deveRejeitarCNPJComTamanhoInvalido() {
        assertFalse(DocumentoValidator.validarCNPJ("11.222.333/0001"));
        assertFalse(DocumentoValidator.validarCNPJ("11.222.333/0001-811"));
        assertFalse(DocumentoValidator.validarCNPJ("11222"));
    }

    @Test
    @DisplayName("Deve rejeitar CNPJ nulo ou vazio")
    void deveRejeitarCNPJNuloOuVazio() {
        assertFalse(DocumentoValidator.validarCNPJ(null));
        assertFalse(DocumentoValidator.validarCNPJ(""));
        assertFalse(DocumentoValidator.validarCNPJ("   "));
    }

    @Test
    @DisplayName("Deve rejeitar CNPJ com dígitos verificadores inválidos")
    void deveRejeitarCNPJComDigitosVerificadoresInvalidos() {
        assertFalse(DocumentoValidator.validarCNPJ("11.222.333/0001-00"));
        assertFalse(DocumentoValidator.validarCNPJ("11.222.333/0001-82"));
        assertFalse(DocumentoValidator.validarCNPJ("98.765.432/0001-00"));
    }

    @Test
    @DisplayName("Deve validar múltiplos CPFs válidos conhecidos")
    void deveValidarMultiplosCPFsValidosConhecidos() {
        // CPFs válidos (sem formatação)
        assertTrue(DocumentoValidator.validarCPF("19100000001"));
        assertTrue(DocumentoValidator.validarCPF("00000000191"));
        assertTrue(DocumentoValidator.validarCPF("11144477735"));
    }

    @Test
    @DisplayName("Deve validar múltiplos CNPJs válidos conhecidos")
    void deveValidarMultiplosCNPJsValidosConhecidos() {
        // CNPJs válidos (sem formatação)
        assertTrue(DocumentoValidator.validarCNPJ("11444777000161"));
        assertTrue(DocumentoValidator.validarCNPJ("00000000000191"));
    }
}
