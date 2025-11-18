package com.barbearia.domain.entities;

/**
 * Perfil para profissionais coloristas.
 * 
 * Particularidades reais da vida profissional:
 * - Realiza coloração capilar e tratamentos capilares
 * - Comissão de 18% sobre serviços realizados (maior devido à especialização)
 * - Especialista em colorimetria e química capilar
 * 
 * Conceitos POO:
 * - Polimorfismo: sobrescreve métodos da interface Perfil
 * - Encapsulamento: lógica específica interna
 * 
 * @author Sua Barbearia Team
 */
public class PerfilColorista implements Perfil {
    
    private static final double PERCENTUAL_COMISSAO = 0.18; // 18%
    
    @Override
    public String getProfissao() {
        return "COLORISTA";
    }
    
    @Override
    public boolean podeRealizarServico(Servico servico) {
        // Colorista pode realizar coloração e tratamento capilar
        return servico instanceof ServicoColoracao || servico instanceof ServicoTratamentoCapilar;
    }
    
    @Override
    public double calcularComissao(double valorServico) {
        // Comissão de 18% sobre o valor do serviço
        return valorServico * PERCENTUAL_COMISSAO;
    }
    
    @Override
    public String getEspecialidades() {
        return "Coloração capilar e tratamentos químicos";
    }
    
    @Override
    public String toString() {
        return "PerfilColorista{profissao='" + getProfissao() + "', comissao=" + (PERCENTUAL_COMISSAO * 100) + "%}";
    }
}
