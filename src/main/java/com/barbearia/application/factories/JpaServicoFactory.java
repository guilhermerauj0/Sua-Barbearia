package com.barbearia.application.factories;

import com.barbearia.infrastructure.persistence.entities.*;

/**
 * Factory para criar instâncias corretas de JpaServico baseado no tipo.
 * 
 * Implementa o padrão Factory Method para encapsular a lógica de criação
 * de objetos polimórficos.
 * 
 * Tipos suportados:
 * - CORTE: JpaServicoCorte
 * - BARBA: JpaServicoBarba
 * - MANICURE: JpaServicoManicure
 * - SOBRANCELHA: JpaServicoSobrancelha
 * - COLORACAO: JpaServicoColoracao
 * - TRATAMENTO_CAPILAR: JpaServicoTratamentoCapilar
 * 
 * @author Sua Barbearia Team
 */
public class JpaServicoFactory {
    
    /**
     * Cria uma instância de JpaServico do tipo especificado.
     * 
     * @param tipoServico tipo do serviço (CORTE, BARBA, MANICURE, etc)
     * @return nova instância de JpaServico apropriada
     * @throws IllegalArgumentException se tipo inválido
     */
    public static JpaServico criar(String tipoServico) {
        if (tipoServico == null || tipoServico.isBlank()) {
            throw new IllegalArgumentException("Tipo de serviço não pode ser nulo ou vazio");
        }
        
        return switch (tipoServico.toUpperCase()) {
            case "CORTE" -> new JpaServicoCorte();
            case "BARBA" -> new JpaServicoBarba();
            case "MANICURE" -> new JpaServicoManicure();
            case "SOBRANCELHA" -> new JpaServicoSobrancelha();
            case "COLORACAO" -> new JpaServicoColoracao();
            case "TRATAMENTO_CAPILAR" -> new JpaServicoTratamentoCapilar();
            default -> throw new IllegalArgumentException("Tipo de serviço inválido: " + tipoServico + 
                    ". Valores permitidos: CORTE, BARBA, MANICURE, SOBRANCELHA, COLORACAO, TRATAMENTO_CAPILAR");
        };
    }
}
