package com.barbearia.application.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

/**
 * DTO para requisição de criação de múltiplos bloqueios de horário em lote.
 * 
 * Facilita bloqueio recorrente (ex: almoço de seg-sex).
 */
public class HorarioBloqueadoLoteRequestDto {

    @NotEmpty(message = "Lista de bloqueios não pode ser vazia")
    @Valid
    private List<HorarioBloqueadoRequestDto> bloqueios;

    public HorarioBloqueadoLoteRequestDto() {
    }

    public HorarioBloqueadoLoteRequestDto(List<HorarioBloqueadoRequestDto> bloqueios) {
        this.bloqueios = bloqueios;
    }

    // Getters e Setters

    public List<HorarioBloqueadoRequestDto> getBloqueios() {
        return bloqueios;
    }

    public void setBloqueios(List<HorarioBloqueadoRequestDto> bloqueios) {
        this.bloqueios = bloqueios;
    }
}
