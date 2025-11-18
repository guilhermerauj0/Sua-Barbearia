package com.barbearia.domain.entities;

/**
 * Perfil para profissionais esteticistas.
 * 
 * Particularidades reais da vida profissional:
 * - Realiza serviços de sobrancelha e estética facial
 * - Comissão de 13% sobre serviços realizados
 * - Especialista em design de sobrancelhas
 * 
 * Conceitos POO:
 * - Polimorfismo: sobrescreve métodos da interface Perfil
 * - Encapsulamento: lógica específica interna
 * 
 * @author Sua Barbearia Team
 */
public class PerfilEsteticista implements Perfil {
    
    private static final double PERCENTUAL_COMISSAO = 0.13; // 13%
    
    @Override
    public String getProfissao() {
        return "ESTETICISTA";
    }
    
    @Override
    public boolean podeRealizarServico(Servico servico) {
        // Esteticista pode realizar serviços de sobrancelha
        return servico instanceof ServicoSobrancelha;
    }
    
    @Override
    public double calcularComissao(double valorServico) {
        // Comissão de 13% sobre o valor do serviço
        return valorServico * PERCENTUAL_COMISSAO;
    }
    
    @Override
    public String getEspecialidades() {
        return "Design de sobrancelhas e estética facial";
    }
    
    @Override
    public String toString() {
        return "PerfilEsteticista{profissao='" + getProfissao() + "', comissao=" + (PERCENTUAL_COMISSAO * 100) + "%}";
    }
}
