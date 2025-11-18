package com.barbearia.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

/**
 * Configuração de CORS (Cross-Origin Resource Sharing) para a API.
 * 
 * Permite que aplicações frontend em diferentes domínios acessem a API.
 * 
 * Configurações:
 * - Origens permitidas: localhost (desenvolvimento) e domínio de produção
 * - Métodos HTTP: GET, POST, PUT, DELETE, PATCH, OPTIONS
 * - Headers: Authorization, Content-Type e outros padrões
 * - Credenciais: Permitidas (necessário para autenticação)
 * - Max Age: 3600 segundos (cache de configuração CORS no navegador)
 * 
 * @author Sua Barbearia Team
 */
@Configuration
public class CorsConfig {

    /**
     * Configura CORS para todos os endpoints da API.
     * 
     * @return CorsConfigurationSource configurado
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // Origens permitidas (adicione o domínio de produção quando disponível)
        configuration.setAllowedOriginPatterns(Arrays.asList(
            "http://localhost:*",
            "http://127.0.0.1:*",
            "https://localhost:*",
            "https://127.0.0.1:*",
            "http://www.agendasuabarbearias.tech",
            "https://www.agendasuabarbearias.tech",
            "https://sua-barbearia-frontend.vercel.app",
            "http://sua-barbearia-frontend.vercel.app"
            // Adicione aqui o domínio de produção:
            // "https://sua-barbearia.com",
            // "https://www.sua-barbearia.com"
        ));
        
        // Métodos HTTP permitidos
        configuration.setAllowedMethods(Arrays.asList(
            "GET",
            "POST",
            "PUT",
            "DELETE",
            "PATCH",
            "OPTIONS",
            "HEAD"
        ));
        
        // Headers permitidos
        configuration.setAllowedHeaders(Arrays.asList(
            "Authorization",
            "Content-Type",
            "Accept",
            "Origin",
            "Access-Control-Request-Method",
            "Access-Control-Request-Headers",
            "X-Requested-With",
            "Cache-Control"
        ));
        
        // Headers expostos (que o frontend pode acessar)
        configuration.setExposedHeaders(Arrays.asList(
            "Authorization",
            "Content-Type",
            "X-Total-Count"
        ));
        
        // Permitir credenciais (cookies, headers de autenticação)
        configuration.setAllowCredentials(true);
        
        // Tempo de cache da configuração CORS no navegador (1 hora)
        configuration.setMaxAge(3600L);
        
        // Aplica a configuração para todos os endpoints
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        
        return source;
    }
}
