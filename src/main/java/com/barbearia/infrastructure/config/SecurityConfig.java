package com.barbearia.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Configuração de segurança da aplicação.
 * 
 * Por enquanto, desabilitamos a autenticação automática do Spring Security
 * para permitir acesso público aos endpoints de registro.
 * 
 * Futuramente, implementaremos JWT e controle de acesso por roles.
 * 
 * @author Sua Barbearia Team
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    /**
     * Configura a cadeia de filtros de segurança
     * 
     * @param http Objeto para configurar a segurança HTTP
     * @return SecurityFilterChain configurado
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // Desabilita CSRF (necessário para APIs REST)
            .csrf(csrf -> csrf.disable())
            // Permite acesso público a todos os endpoints por enquanto
            .authorizeHttpRequests(auth -> auth
                .anyRequest().permitAll()
            );
        
        return http.build();
    }
}
