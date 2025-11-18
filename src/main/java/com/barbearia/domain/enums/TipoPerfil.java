package com.barbearia.domain.enums;

/**
 * Enum que representa os tipos de perfis profissionais disponíveis.
 * 
 * Usado para discriminar qual perfil um funcionário possui,
 * facilitando persistência no banco de dados e validações.
 * 
 * @author Sua Barbearia Team
 */
public enum TipoPerfil {
    /**
     * Barbeiro - Especialista em cortes de cabelo e barba
     */
    BARBEIRO("Barbeiro", "Cortes de cabelo e barba"),
    
    /**
     * Manicure - Especialista em cuidados com unhas
     */
    MANICURE("Manicure", "Manicure e pedicure"),
    
    /**
     * Esteticista - Especialista em design de sobrancelhas
     */
    ESTETICISTA("Esteticista", "Design de sobrancelhas e estética facial"),
    
    /**
     * Colorista - Especialista em coloração e tratamentos capilares
     */
    COLORISTA("Colorista", "Coloração capilar e tratamentos químicos");
    
    private final String descricao;
    private final String especialidades;
    
    TipoPerfil(String descricao, String especialidades) {
        this.descricao = descricao;
        this.especialidades = especialidades;
    }
    
    public String getDescricao() {
        return descricao;
    }
    
    public String getEspecialidades() {
        return especialidades;
    }
    
    @Override
    public String toString() {
        return descricao;
    }
}
