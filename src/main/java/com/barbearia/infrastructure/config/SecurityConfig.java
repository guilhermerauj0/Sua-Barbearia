package com.barbearia.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

/**
 * Configuração de segurança do Spring Security com JWT.
 * 
 * Responsabilidades:
 * - Definir quais endpoints são públicos e quais exigem autenticação
 * - Configurar o filtro JWT
 * - Desabilitar CSRF (não necessário para APIs REST stateless)
 * - Configurar política de sessão (stateless para JWT)
 * 
 * Endpoints públicos:
 * - POST /api/auth/cliente/registrar (registro de novos clientes)
 * - POST /api/auth/cliente/login (login de clientes)
 * - POST /api/auth/barbearia/registrar (registro de novas barbearias)
 * - POST /api/auth/barbearia/login (login de barbearias)
 * - GET /api/hello (endpoint de teste)
 * - Swagger UI e documentação da API
 * 
 * Endpoints protegidos:
 * - Todos os demais exigem autenticação JWT
 * 
 * @author Sua Barbearia Team
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CorsConfigurationSource corsConfigurationSource;
    
    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter,
                         CorsConfigurationSource corsConfigurationSource) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.corsConfigurationSource = corsConfigurationSource;
    }
    
    /**
     * Configura a cadeia de filtros de segurança com JWT.
     * 
     * @param http Objeto para configurar a segurança HTTP.
     * @return SecurityFilterChain configurado.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // Habilita CORS com a configuração personalizada
            .cors(cors -> cors.configurationSource(corsConfigurationSource))
            
            // Desabilita CSRF (não necessário para APIs REST stateless com JWT)
            .csrf(AbstractHttpConfigurer::disable)
            
            // Configura autorização de requisições
            .authorizeHttpRequests(auth -> auth
                // Endpoints públicos (não exigem autenticação)
                .requestMatchers(
                    "/",
                    "/error",
                    "/docs",
                    "/api/auth/**",
                    "/api/hello",
                    // Swagger UI e documentação
                    "/swagger-ui/**",
                    "/swagger-ui.html",
                    "/v3/api-docs/**",
                    "/swagger-resources/**",
                    "/webjars/**",
                    "/api-docs/**"
                ).permitAll()
                
                // Todos os demais endpoints exigem autenticação
                .anyRequest().authenticated()
            )
            
            // Configura política de sessão (stateless - não mantém sessão no servidor)
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            
            // Adiciona o filtro JWT antes do filtro de autenticação padrão
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }
    
    /**
     * Bean do BCryptPasswordEncoder para criptografia de senhas.
     * 
     * @return BCryptPasswordEncoder configurado.
     */
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
