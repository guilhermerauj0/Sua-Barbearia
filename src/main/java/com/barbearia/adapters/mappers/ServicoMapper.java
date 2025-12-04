package com.barbearia.adapters.mappers;

import com.barbearia.application.dto.ServicoDto;
import com.barbearia.domain.entities.*;
import com.barbearia.infrastructure.persistence.entities.JpaServico;

import org.springframework.stereotype.Component;

/**
 * Mapper para conversão entre diferentes representações de Serviço.
 * 
 * Conversões suportadas:
 * - JPA (JpaServico) -> DTO: para retornar na API
 * - JPA (JpaServico) -> Domínio (subclasses de Servico): para processar domínio
 * 
 * Conceitos de POO:
 * - Polimorfismo: Retorna a subclasse correta baseado no tipo de serviço
 * - Encapsulamento: Responsável por conversões entre camadas
 * 
 * @author Sua Barbearia Team
 */
@Component
public class ServicoMapper {

    /**
     * Converte JpaServico para ServicoDto
     * 
     * @param jpaServico JpaServico vindo do banco
     * @return ServicoDto para retornar na API
     */
    public static ServicoDto toDto(JpaServico jpaServico) {
        if (jpaServico == null) {
            return null;
        }

        ServicoDto dto = new ServicoDto();
        dto.setId(jpaServico.getId());
        dto.setNome(jpaServico.getNome());
        dto.setDescricao(jpaServico.getDescricao());
        dto.setPreco(jpaServico.getPreco());
        dto.setDuracao(jpaServico.getDuracao());
        dto.setBarbeariaId(jpaServico.getBarbeariaId());
        dto.setAtivo(jpaServico.isAtivo());
        dto.setTipoServico(jpaServico.getTipoServico());

        return dto;
    }

    /**
     * Converte JpaServico para a subclasse correta de Servico de domínio
     * usando polimorfismo baseado no tipo de serviço.
     * 
     * @param jpaServico JpaServico vindo do banco
     * @return Subclasse apropriada de Servico
     */
    public static Servico toDomain(JpaServico jpaServico) {
        if (jpaServico == null) {
            return null;
        }

        Servico servico = null;
        String tipoServico = jpaServico.getTipoServico();

        // Usa polimorfismo para retornar a subclasse correta baseada no tipo
        switch (tipoServico) {
            case "CORTE":
                servico = new ServicoCorte();
                break;
            case "BARBA":
                servico = new ServicoBarba();
                break;
            case "MANICURE":
                servico = new ServicoManicure();
                break;
            case "SOBRANCELHA":
                servico = new ServicoSobrancelha();
                break;
            case "COLORACAO":
                servico = new ServicoColoracao();
                break;
            case "TRATAMENTO_CAPILAR":
                servico = new ServicoTratamentoCapilar();
                break;
            default:
                // Se não reconhecer o tipo, não converte
                return null;
        }

        // Preenche os atributos da classe base
        servico.setId(jpaServico.getId());
        servico.setNome(jpaServico.getNome());
        servico.setDescricao(jpaServico.getDescricao());
        servico.setPreco(jpaServico.getPreco());
        servico.setDuracao(jpaServico.getDuracao());
        servico.setBarbeariaId(jpaServico.getBarbeariaId());
        servico.setAtivo(jpaServico.isAtivo());
        servico.setDataCriacao(jpaServico.getDataCriacao());
        servico.setDataAtualizacao(jpaServico.getDataAtualizacao());

        return servico;
    }
}
