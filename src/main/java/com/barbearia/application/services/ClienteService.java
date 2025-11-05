package com.barbearia.application.services;

import com.barbearia.application.dto.ClienteRequestDto;
import com.barbearia.application.dto.ClienteResponseDto;
import com.barbearia.application.dto.ClienteProfileDto;
import com.barbearia.application.dto.ClienteUpdateDto;
import com.barbearia.application.factories.UsuarioFactory;
import com.barbearia.domain.entities.Cliente;
import com.barbearia.domain.exceptions.AcessoNegadoException;
import com.barbearia.infrastructure.persistence.entities.JpaCliente;
import com.barbearia.infrastructure.persistence.repositories.ClienteRepository;
import com.barbearia.adapters.mappers.ClienteMapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service responsável pela lógica de negócio de Cliente.
 * 
 * Responsabilidades:
 * - Validar regras de negócio
 * - Verificar duplicidade de dados
 * - Fazer hash de senhas
 * - Coordenar operações entre repository e domain
 * 
 * Anotações:
 * - @Service: marca como componente de serviço do Spring
 * - @Transactional: garante atomicidade das operações de banco
 * 
 * @author Sua Barbearia Team
 */
@Service
public class ClienteService {
    
    /**
     * Repository para acesso ao banco de dados
     */
    private final ClienteRepository clienteRepository;
    
    /**
     * Encoder para fazer hash de senhas usando BCrypt
     * BCrypt é um algoritmo robusto e recomendado para senhas
     */
    private final BCryptPasswordEncoder passwordEncoder;
    
    /**
     * Construtor com injeção de dependências
     * Spring injeta automaticamente quando há apenas um construtor
     * 
     * @param clienteRepository Repository de clientes
     */
    public ClienteService(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }
    
    /**
     * Registra um novo cliente no sistema
     * 
     * Fluxo:
     * 1. Valida se as senhas conferem
     * 2. Verifica se email já existe
     * 3. Faz hash da senha
     * 4. Cria cliente usando factory
     * 5. Converte para entidade JPA
     * 6. Salva no banco
     * 7. Retorna DTO de resposta
     * 
     * @param requestDto Dados do cliente a ser registrado
     * @return DTO com dados do cliente criado (sem senha)
     * @throws IllegalArgumentException se dados forem inválidos ou email duplicado
     */
    @Transactional
    public ClienteResponseDto registrarCliente(ClienteRequestDto requestDto) {
        // 1. Valida se as senhas conferem
        validarSenhas(requestDto);
        
        // 2. Verifica duplicidade de email
        validarEmailUnico(requestDto.getEmail());
        
        // 3. Faz hash da senha (NUNCA armazene senha em texto puro!)
        String senhaHash = passwordEncoder.encode(requestDto.getSenha());
        
        // 4. Cria cliente de domínio usando a factory
        Cliente cliente = UsuarioFactory.criarCliente(requestDto, senhaHash);
        
        // 5. Converte cliente de domínio para entidade JPA
        JpaCliente jpaCliente = ClienteMapper.toJpaEntity(cliente);
        
        // 6. Salva no banco de dados
        @SuppressWarnings("null")
        JpaCliente clienteSalvo = clienteRepository.save(jpaCliente);
        
        // 7. Converte para DTO de resposta (sem senha) e retorna
        return ClienteMapper.toResponseDto(clienteSalvo);
    }
    
    /**
     * Valida se a senha e confirmação são iguais
     * 
     * @param requestDto DTO com dados do cliente
     * @throws IllegalArgumentException se as senhas não coincidirem
     */
    private void validarSenhas(ClienteRequestDto requestDto) {
        if (!requestDto.senhasConferem()) {
            throw new IllegalArgumentException("Senha e confirmação de senha não coincidem");
        }
    }
    
    /**
     * Verifica se o email já está cadastrado no sistema
     * 
     * Email deve ser único para garantir que cada cliente tenha uma conta única
     * 
     * @param email Email a ser verificado
     * @throws IllegalArgumentException se o email já existir
     */
    private void validarEmailUnico(String email) {
        String emailNormalizado = email.toLowerCase().trim();
        
        if (clienteRepository.existsByEmail(emailNormalizado)) {
            throw new IllegalArgumentException("Email já cadastrado no sistema");
        }
    }
    
    /**
     * Busca um cliente pelo ID
     * 
     * @param id ID do cliente
     * @return DTO com dados do cliente
     * @throws IllegalArgumentException se cliente não for encontrado
     */
    @SuppressWarnings("null")
    public ClienteResponseDto buscarPorId(Long id) {
        JpaCliente cliente = clienteRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado com ID: " + id));
        
        return ClienteMapper.toResponseDto(cliente);
    }
    
    /**
     * Busca um cliente pelo email
     * 
     * @param email Email do cliente
     * @return DTO com dados do cliente
     * @throws IllegalArgumentException se cliente não for encontrado
     */
    public ClienteResponseDto buscarPorEmail(String email) {
        String emailNormalizado = email.toLowerCase().trim();
        
        JpaCliente cliente = clienteRepository.findByEmail(emailNormalizado)
            .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado com email: " + email));
        
        return ClienteMapper.toResponseDto(cliente);
    }
    
    /**
     * Verifica se um email já está cadastrado
     * 
     * @param email Email a ser verificado
     * @return true se email já existe, false caso contrário
     */
    public boolean emailJaCadastrado(String email) {
        String emailNormalizado = email.toLowerCase().trim();
        return clienteRepository.existsByEmail(emailNormalizado);
    }
    
    /**
     * Busca o perfil completo do cliente autenticado.
     * 
     * @param clienteId ID do cliente
     * @return DTO com perfil do cliente
     * @throws IllegalArgumentException se cliente não for encontrado
     */
    public ClienteProfileDto buscarMeuPerfil(Long clienteId) {
        @SuppressWarnings("null")
        JpaCliente cliente = clienteRepository.findById(clienteId)
            .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado"));
        
        return ClienteMapper.toProfileDto(cliente);
    }
    
    /**
     * Atualiza dados do cliente autenticado.
     * 
     * Regras de negócio:
     * 1. Apenas campos enviados são atualizados (atualização parcial)
     * 2. Email não pode ser alterado para um já existente
     * 3. Nome e telefone são validados
     * 4. Nunca permitir alteração de role
     * 
     * @param clienteId ID do cliente autenticado
     * @param updateDto DTO com dados a atualizar
     * @return DTO atualizado do cliente
     * @throws IllegalArgumentException se dados inválidos
     * @throws AcessoNegadoException se tentar alterar role
     */
    @Transactional
    public ClienteProfileDto atualizarMeuPerfil(Long clienteId, ClienteUpdateDto updateDto) {
        // Valida se há campos para atualizar
        if (!updateDto.temCamposParaAtualizar()) {
            throw new IllegalArgumentException("Nenhum campo para atualizar");
        }
        
        // Busca o cliente
        @SuppressWarnings("null")
        JpaCliente cliente = clienteRepository.findById(clienteId)
            .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado"));
        
        // Atualiza nome se fornecido
        if (updateDto.getNome() != null && !updateDto.getNome().isBlank()) {
            cliente.setNome(updateDto.getNome().trim());
        }
        
        // Atualiza email se fornecido
        if (updateDto.getEmail() != null && !updateDto.getEmail().isBlank()) {
            String emailNormalizado = updateDto.getEmail().toLowerCase().trim();
            
            // Verifica se o novo email já está em uso por outro cliente
            if (!cliente.getEmail().equalsIgnoreCase(emailNormalizado)) {
                if (clienteRepository.existsByEmail(emailNormalizado)) {
                    throw new IllegalArgumentException("Email já cadastrado por outro usuário");
                }
            }
            
            // Valida formato de email básico
            validarFormatoEmail(emailNormalizado);
            
            cliente.setEmail(emailNormalizado);
        }
        
        // Atualiza telefone se fornecido
        if (updateDto.getTelefone() != null && !updateDto.getTelefone().isBlank()) {
            String telefoneLimpo = updateDto.getTelefone().trim();
            validarFormatoTelefone(telefoneLimpo);
            cliente.setTelefone(telefoneLimpo);
        }
        
        // Salva as alterações
        @SuppressWarnings("null")
        JpaCliente clienteAtualizado = clienteRepository.save(cliente);
        
        return ClienteMapper.toProfileDto(clienteAtualizado);
    }
    
    /**
     * Valida formato básico de email.
     * 
     * @param email Email a ser validado
     * @throws IllegalArgumentException se email inválido
     */
    private void validarFormatoEmail(String email) {
        // Verificação básica: deve conter @ e um domínio
        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new IllegalArgumentException("Formato de email inválido");
        }
    }
    
    /**
     * Valida formato de telefone.
     * 
     * Aceita diversos formatos:
     * - (XX) XXXXX-XXXX
     * - (XX) XXXX-XXXX
     * - XX XXXXX XXXX
     * - XXXXXXXXXX
     * 
     * @param telefone Telefone a ser validado
     * @throws IllegalArgumentException se telefone inválido
     */
    private void validarFormatoTelefone(String telefone) {
        // Remove caracteres especiais para contagem
        String apenasNumeros = telefone.replaceAll("[^0-9]", "");
        
        // Telefone deve ter 10 ou 11 dígitos
        if (apenasNumeros.length() < 10 || apenasNumeros.length() > 11) {
            throw new IllegalArgumentException("Telefone deve ter 10 ou 11 dígitos");
        }
    }
}
