package com.barbearia.application.services;

import com.barbearia.application.dto.LoginRequestDto;
import com.barbearia.application.dto.LoginResponseDto;
import com.barbearia.application.security.JwtService;
import com.barbearia.infrastructure.persistence.entities.JpaCliente;
import com.barbearia.infrastructure.persistence.repositories.ClienteRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Service responsável pela autenticação de usuários.
 * 
 * Responsabilidades:
 * - Validar credenciais (email/senha)
 * - Gerar token JWT após autenticação bem-sucedida
 * - Incluir claims necessários no token (userId, role)
 */
@Service
public class AuthService {
    
    private final ClienteRepository clienteRepository;
    private final JwtService jwtService;
    private final BCryptPasswordEncoder passwordEncoder;
    
    public AuthService(
            ClienteRepository clienteRepository, 
            JwtService jwtService,
            BCryptPasswordEncoder passwordEncoder) {
        this.clienteRepository = clienteRepository;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
    }
    
    /**
     * Realiza o login do cliente.
     * 
     * @param loginRequest DTO com credenciais (email e senha)
     * @return LoginResponseDto com token JWT e informações do usuário
     * @throws IllegalArgumentException se as credenciais forem inválidas
     */
    @SuppressWarnings("null")
    public LoginResponseDto login(LoginRequestDto loginRequest) {
        // 1. Normaliza o email (lowercase e trim)
        String emailNormalizado = loginRequest.email().toLowerCase().trim();
        
        // 2. Busca o cliente pelo email
        JpaCliente cliente = clienteRepository.findByEmail(emailNormalizado)
                .orElseThrow(() -> new IllegalArgumentException("Credenciais inválidas"));
        
        // 3. Verifica a senha usando BCrypt
        if (!passwordEncoder.matches(loginRequest.senha(), cliente.getSenha())) {
            throw new IllegalArgumentException("Credenciais inválidas");
        }
        
        // 4. Cria os claims do token (informações extras)
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", cliente.getId());
        claims.put("role", "CLIENTE"); // Por enquanto, todos são clientes
        claims.put("nome", cliente.getNome());
        
        // 5. Gera o token JWT
        String token = jwtService.generateToken(claims, emailNormalizado);
        
        // 6. Retorna a resposta com o token e informações do usuário
        return new LoginResponseDto(
                token,
                cliente.getId(),
                cliente.getNome(),
                cliente.getEmail(),
                "CLIENTE",
                jwtService.getExpirationTime()
        );
    }
}
