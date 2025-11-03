package com.barbearia.domain.enums;

/**
 * Enum que representa os tipos de documento válidos para registro de barbearia.
 * 
 * Uma barbearia pode ser registrada como:
 * - Pessoa Física (CPF): Profissional autônomo, barbeiro individual
 * - Pessoa Jurídica (CNPJ): Estabelecimento comercial, empresa
 * 
 * Por que usar enum?
 * - Tipo seguro: evita strings mágicas ("cpf", "CNPJ", etc)
 * - Validação em tempo de compilação
 * - Facilita manutenção e extensão futura
 * 
 * @author Sua Barbearia Team
 */
public enum TipoDocumento {
    
    /**
     * Cadastro de Pessoa Física
     */
    CPF("CPF", 11, "###.###.###-##"),
    
    /**
     * Cadastro Nacional de Pessoa Jurídica
     */
    CNPJ("CNPJ", 14, "##.###.###/####-##");
    
    private final String descricao;
    private final int tamanho;
    private final String mascara;
    
    /**
     * Construtor privado do enum
     * 
     * @param descricao Descrição legível do tipo de documento
     * @param tamanho Quantidade de dígitos (sem formatação)
     * @param mascara Máscara de formatação do documento
     */
    TipoDocumento(String descricao, int tamanho, String mascara) {
        this.descricao = descricao;
        this.tamanho = tamanho;
        this.mascara = mascara;
    }
    
    /**
     * Obtém a descrição do tipo de documento
     * 
     * @return Descrição (CPF ou CNPJ)
     */
    public String getDescricao() {
        return descricao;
    }
    
    /**
     * Obtém o tamanho do documento (quantidade de dígitos sem formatação)
     * 
     * @return 11 para CPF, 14 para CNPJ
     */
    public int getTamanho() {
        return tamanho;
    }
    
    /**
     * Obtém a máscara de formatação do documento
     * 
     * @return Máscara (###.###.###-## para CPF, ##.###.###/####-## para CNPJ)
     */
    public String getMascara() {
        return mascara;
    }
    
    /**
     * Verifica se o documento tem o tamanho correto para este tipo
     * 
     * @param documento Documento sem formatação (apenas números)
     * @return true se o tamanho está correto, false caso contrário
     */
    public boolean validarTamanho(String documento) {
        if (documento == null) {
            return false;
        }
        String apenasNumeros = documento.replaceAll("[^0-9]", "");
        return apenasNumeros.length() == this.tamanho;
    }
}
