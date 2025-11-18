package com.barbearia.domain.entities;

/**
 * Perfil para profissionais barbeiros.
 * 
 * Particularidades reais da vida profissional:
 * - Realiza cortes de cabelo e barba
 * - Comissão de 15% sobre serviços realizados
 * - Especialista em cabelo masculino
 * 
 * Conceitos POO:
 * - Polimorfismo: sobrescreve métodos da interface Perfil
 * - Encapsulamento: lógica específica interna
 * 
 * @author Sua Barbearia Team
 */
public class PerfilBarbeiro implements Perfil {
    
    private static final double PERCENTUAL_COMISSAO = 0.15; // 15%
    
    @Override
    public String getProfissao() {
        return "BARBEIRO";
    }
    
    @Override
    public boolean podeRealizarServico(Servico servico) {
        // Barbeiro pode realizar apenas serviços de barba e corte
        return servico instanceof ServicoBarba || servico instanceof ServicoCorte;
    }
    
    @Override
    public double calcularComissao(double valorServico) {
        // Comissão de 15% sobre o valor do serviço
        return valorServico * PERCENTUAL_COMISSAO;
    }
    
    @Override
    public String getEspecialidades() {
        return "Cortes de cabelo e barba";
    }
    
    @Override
    public String toString() {
        return "PerfilBarbeiro{profissao='" + getProfissao() + "', comissao=" + (PERCENTUAL_COMISSAO * 100) + "%}";
    }
}
