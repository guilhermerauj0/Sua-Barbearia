package com.barbearia.domain.entities;

/**
 * Interface para perfis de funcionários da barbearia.
 * 
 * Define o contrato para diferentes tipos de profissionais,
 * permitindo que cada perfil tenha suas particularidades.
 * 
 * Conceitos POO:
 * - Abstração: interface com métodos polimórficos
 * - Polimorfismo: implementações específicas por perfil
 * - Encapsulamento: cada perfil encapsula suas regras
 * 
 * @author Sua Barbearia Team
 */
public interface Perfil {
    
    /**
     * Retorna o tipo de profissão do perfil.
     * 
     * @return tipo de profissão (BARBEIRO, MANICURE, ESTETICISTA, COLORISTA)
     */
    String getProfissao();
    
    /**
     * Verifica se o perfil pode realizar um serviço específico.
     * Cada perfil implementa suas próprias regras.
     * 
     * @param servico o serviço a ser verificado
     * @return true se pode realizar, false caso contrário
     */
    boolean podeRealizarServico(Servico servico);
    
    /**
     * Calcula a comissão do profissional sobre o valor do serviço.
     * Cada perfil pode ter percentuais diferentes.
     * 
     * @param valorServico valor do serviço realizado
     * @return valor da comissão calculada
     */
    default double calcularComissao(double valorServico) {
        return 0.0; // Implementação padrão sem comissão
    }
    
    /**
     * Retorna descrição das especialidades do perfil.
     * 
     * @return descrição das especialidades
     */
    default String getEspecialidades() {
        return "Serviços gerais";
    }
}
