package com.barbearia.domain.entities;

/**
 * Perfil para profissionais manicures.
 * 
 * Particularidades reais da vida profissional:
 * - Realiza serviços de manicure e pedicure
 * - Comissão de 12% sobre serviços realizados
 * - Especialista em cuidados com unhas
 * 
 * Conceitos POO:
 * - Polimorfismo: sobrescreve métodos da interface Perfil
 * - Encapsulamento: lógica específica interna
 * 
 * @author Sua Barbearia Team
 */
public class PerfilManicure implements Perfil {
    
    private static final double PERCENTUAL_COMISSAO = 0.12; // 12%
    
    @Override
    public String getProfissao() {
        return "MANICURE";
    }
    
    @Override
    public boolean podeRealizarServico(Servico servico) {
        // Manicure pode realizar apenas serviços de manicure
        return servico instanceof ServicoManicure;
    }
    
    @Override
    public double calcularComissao(double valorServico) {
        // Comissão de 12% sobre o valor do serviço
        return valorServico * PERCENTUAL_COMISSAO;
    }
    
    @Override
    public String getEspecialidades() {
        return "Manicure e pedicure";
    }
    
    @Override
    public String toString() {
        return "PerfilManicure{profissao='" + getProfissao() + "', comissao=" + (PERCENTUAL_COMISSAO * 100) + "%}";
    }
}
