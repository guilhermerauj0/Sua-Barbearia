package com.barbearia.infrastructure.config;

import com.barbearia.application.security.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * Filtro de autenticação JWT que intercepta todas as requisições.
 * 
 * Responsabilidades:
 * - Extrair o token JWT do header Authorization
 * - Validar o token
 * - Configurar o contexto de segurança do Spring Security
 * 
 * Este filtro é executado uma vez por requisição (OncePerRequestFilter).
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    private final JwtService jwtService;
    
    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }
    
    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        
        // 1. Extrai o header Authorization
        final String authHeader = request.getHeader("Authorization");
        
        // 2. Verifica se o header existe e começa com "Bearer "
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
        
        try {
            // 3. Extrai o token (remove "Bearer " do início)
            final String jwt = authHeader.substring(7);
            
            // 4. Extrai o subject (email) do token
            final String userEmail = jwtService.extractSubject(jwt);
            
            // 5. Se o email foi extraído e não há autenticação no contexto
            if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                
                // 6. Valida o token
                if (jwtService.isTokenValid(jwt, userEmail)) {
                    
                    // 7. Extrai a role do token
                    String role = (String) jwtService.extractClaim(jwt, "role");
                    
                    // 8. Cria as authorities (permissões)
                    List<SimpleGrantedAuthority> authorities = List.of(
                            new SimpleGrantedAuthority("ROLE_" + role)
                    );
                    
                    // 9. Cria o objeto de autenticação
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userEmail,
                            null,
                            authorities
                    );
                    
                    // 10. Adiciona detalhes da requisição
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    
                    // 11. Define a autenticação no contexto de segurança
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        } catch (Exception e) {
            // Em caso de erro na validação do token, apenas continua sem autenticar
            // Isso permite que o Spring Security rejeite a requisição se necessário
            logger.error("Erro ao processar token JWT: " + e.getMessage());
        }
        
        // 12. Continua a cadeia de filtros
        filterChain.doFilter(request, response);
    }
}
