package com.barbearia.application.services;

import com.barbearia.adapters.mappers.BarbeariaMapper;
import com.barbearia.adapters.mappers.ServicoMapper;
import com.barbearia.adapters.mappers.HorarioFuncionamentoMapper;
import com.barbearia.application.dto.BarbeariaRequestDto;
import com.barbearia.application.dto.BarbeariaResponseDto;
import com.barbearia.application.dto.BarbeariaListItemDto;
import com.barbearia.application.dto.ServicoDto;
import com.barbearia.application.dto.HorarioFuncionamentoRequestDto;
import com.barbearia.application.dto.HorarioFuncionamentoResponseDto;
import com.barbearia.application.factories.UsuarioFactory;
import com.barbearia.application.factories.JpaServicoFactory;
import com.barbearia.application.utils.DocumentoValidator;
import com.barbearia.domain.entities.Barbearia;
import com.barbearia.infrastructure.persistence.entities.JpaBarbearia;
import com.barbearia.infrastructure.persistence.entities.JpaServico;
import com.barbearia.infrastructure.persistence.entities.JpaHorarioFuncionamento;
import com.barbearia.infrastructure.persistence.repositories.BarbeariaRepository;
import com.barbearia.infrastructure.persistence.repositories.ServicoRepository;
import com.barbearia.infrastructure.persistence.repositories.HorarioFuncionamentoRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service responsável pela lógica de negócio de Barbearia.
 * 
 * Responsabilidades:
 * - Validar regras de negócio
 * - Verificar duplicidade de dados (email e documento)
 * - Validar CPF/CNPJ usando algoritmos oficiais
 * - Fazer hash de senhas
 * - Coordenar operações entre repository e domain
 * 
 * Validações implementadas:
 * - Senhas devem conferir
 * - Email deve ser único no sistema
 * - Documento (CPF ou CNPJ) deve ser válido
 * - Documento deve ser único por tipo (não pode ter dois CPFs iguais)
 * 
 * Anotações:
 * - @Service: marca como componente de serviço do Spring
 * - @Transactional: garante atomicidade das operações de banco
 * 
 * @author Sua Barbearia Team
 */
@Service
public class BarbeariaService {
    
    /**
     * Repository para acesso ao banco de dados
     */
    private final BarbeariaRepository barbeariaRepository;
    
    /**
     * Repository para acesso a dados de serviços
     */
    private final ServicoRepository servicoRepository;
    
    /**
     * Repository para acesso a dados de horários de funcionamento
     */
    private final HorarioFuncionamentoRepository horarioFuncionamentoRepository;
    
    /**
     * Encoder para fazer hash de senhas usando BCrypt
     * BCrypt é um algoritmo robusto e recomendado para senhas
     */
    private final BCryptPasswordEncoder passwordEncoder;
    
    /**
     * Construtor com injeção de dependências
     * Spring injeta automaticamente quando há apenas um construtor
     * 
     * @param barbeariaRepository Repository de barbearias
     * @param servicoRepository Repository de serviços
     * @param horarioFuncionamentoRepository Repository de horários de funcionamento
     */
    public BarbeariaService(BarbeariaRepository barbeariaRepository, 
                           ServicoRepository servicoRepository,
                           HorarioFuncionamentoRepository horarioFuncionamentoRepository) {
        this.barbeariaRepository = barbeariaRepository;
        this.servicoRepository = servicoRepository;
        this.horarioFuncionamentoRepository = horarioFuncionamentoRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }
    
    /**
     * Registra uma nova barbearia no sistema
     * 
     * Fluxo:
     * 1. Valida se as senhas conferem
     * 2. Verifica se email já existe
     * 3. Valida o documento (CPF ou CNPJ)
     * 4. Verifica se documento já existe
     * 5. Faz hash da senha
     * 6. Cria barbearia usando factory
     * 7. Converte para entidade JPA
     * 8. Salva no banco
     * 9. Retorna DTO de resposta
     * 
     * @param requestDto Dados da barbearia a ser registrada
     * @return DTO com dados da barbearia criada (sem senha)
     * @throws IllegalArgumentException se dados forem inválidos ou duplicados
     */
    @Transactional
    public BarbeariaResponseDto registrarBarbearia(BarbeariaRequestDto requestDto) {
        // 1. Valida se as senhas conferem
        validarSenhas(requestDto);
        
        // 2. Verifica duplicidade de email
        validarEmailUnico(requestDto.getEmail());
        
        // 3. Valida o documento (CPF ou CNPJ)
        validarDocumento(requestDto);
        
        // 4. Verifica duplicidade de documento
        validarDocumentoUnico(requestDto);
        
        // 5. Faz hash da senha (NUNCA armazene senha em texto puro!)
        String senhaHash = passwordEncoder.encode(requestDto.getSenha());
        
        // 6. Cria barbearia de domínio usando a factory
        Barbearia barbearia = UsuarioFactory.criarBarbearia(requestDto, senhaHash);
        
        // 7. Converte barbearia de domínio para entidade JPA
        JpaBarbearia jpaBarbearia = BarbeariaMapper.toJpaEntity(barbearia);
        
        // 8. Salva no banco de dados
        @SuppressWarnings("null")
        JpaBarbearia barbeariaSalva = barbeariaRepository.save(jpaBarbearia);
        
        // 9. Converte para DTO de resposta (sem senha) e retorna
        return BarbeariaMapper.toResponseDto(barbeariaSalva);
    }
    
    /**
     * Valida se a senha e confirmação são iguais
     * 
     * @param requestDto DTO com dados da barbearia
     * @throws IllegalArgumentException se as senhas não coincidirem
     */
    private void validarSenhas(BarbeariaRequestDto requestDto) {
        if (!requestDto.senhasConferem()) {
            throw new IllegalArgumentException("Senha e confirmação de senha não coincidem");
        }
    }
    
    /**
     * Verifica se o email já está cadastrado no sistema
     * 
     * Email deve ser único para garantir que cada barbearia tenha uma conta única
     * 
     * @param email Email a ser verificado
     * @throws IllegalArgumentException se o email já existir
     */
    private void validarEmailUnico(String email) {
        String emailNormalizado = email.toLowerCase().trim();
        
        if (barbeariaRepository.existsByEmail(emailNormalizado)) {
            throw new IllegalArgumentException("Email já cadastrado no sistema");
        }
    }
    
    /**
     * Valida o documento (CPF ou CNPJ) usando algoritmos oficiais
     * 
     * Validações:
     * - CPF: 11 dígitos, algoritmo módulo 11
     * - CNPJ: 14 dígitos, algoritmo módulo 11
     * 
     * @param requestDto DTO com dados da barbearia
     * @throws IllegalArgumentException se o documento for inválido
     */
    private void validarDocumento(BarbeariaRequestDto requestDto) {
        String documento = requestDto.getDocumento();
        
        if (!DocumentoValidator.validar(documento, requestDto.getTipoDocumento())) {
            String tipoDocumento = requestDto.getTipoDocumento().getDescricao();
            throw new IllegalArgumentException(
                String.format("%s inválido. Verifique o número informado.", tipoDocumento)
            );
        }
    }
    
    /**
     * Verifica se o documento já está cadastrado
     * 
     * Unicidade composta: não pode haver dois CPFs iguais OU dois CNPJs iguais
     * Mas pode haver um CPF e um CNPJ com os mesmos números (são tipos diferentes)
     * 
     * Exemplo:
     * - CPF 123.456.789-00 pode existir
     * - CNPJ 12.345.678/9000-12 pode existir
     * - Mas não pode ter dois CPFs 123.456.789-00
     * 
     * @param requestDto DTO com dados da barbearia
     * @throws IllegalArgumentException se o documento já existir
     */
    private void validarDocumentoUnico(BarbeariaRequestDto requestDto) {
        String documentoLimpo = DocumentoValidator.limparDocumento(requestDto.getDocumento());
        
        if (barbeariaRepository.existsByTipoDocumentoAndDocumento(
                requestDto.getTipoDocumento(), documentoLimpo)) {
            String tipoDocumento = requestDto.getTipoDocumento().getDescricao();
            throw new IllegalArgumentException(
                String.format("%s já cadastrado no sistema", tipoDocumento)
            );
        }
    }
    
    /**
     * Busca uma barbearia pelo ID
     * 
     * @param id ID da barbearia
     * @return DTO com dados da barbearia
     * @throws IllegalArgumentException se barbearia não for encontrada
     */
    @SuppressWarnings("null")
    public BarbeariaResponseDto buscarPorId(Long id) {
        JpaBarbearia barbearia = barbeariaRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Barbearia não encontrada com ID: " + id));
        
        return BarbeariaMapper.toResponseDto(barbearia);
    }
    
    /**
     * Busca uma barbearia pelo email
     * 
     * @param email Email da barbearia
     * @return DTO com dados da barbearia
     * @throws IllegalArgumentException se barbearia não for encontrada
     */
    public BarbeariaResponseDto buscarPorEmail(String email) {
        String emailNormalizado = email.toLowerCase().trim();
        
        JpaBarbearia barbearia = barbeariaRepository.findByEmail(emailNormalizado)
            .orElseThrow(() -> new IllegalArgumentException("Barbearia não encontrada com email: " + email));
        
        return BarbeariaMapper.toResponseDto(barbearia);
    }
    
    /**
     * Verifica se um email já está cadastrado
     * 
     * @param email Email a ser verificado
     * @return true se email já existe, false caso contrário
     */
    public boolean emailJaCadastrado(String email) {
        String emailNormalizado = email.toLowerCase().trim();
        return barbeariaRepository.existsByEmail(emailNormalizado);
    }
    
    /**
     * Verifica se um documento já está cadastrado
     * 
     * @param tipoDocumento Tipo do documento (CPF ou CNPJ)
     * @param documento Número do documento
     * @return true se documento já existe, false caso contrário
     */
    public boolean documentoJaCadastrado(String tipoDocumento, String documento) {
        String documentoLimpo = DocumentoValidator.limparDocumento(documento);
        return barbeariaRepository.existsByTipoDocumentoAndDocumento(
            com.barbearia.domain.enums.TipoDocumento.valueOf(tipoDocumento), documentoLimpo
        );
    }
    
    /**
     * Lista todas as barbearias ativas do sistema.
     * 
     * @return Lista de DTOs com informações resumidas das barbearias
     */
    public List<BarbeariaListItemDto> listarBarbearias() {
        List<JpaBarbearia> barbearias = barbeariaRepository.findByAtivoTrue();
        return barbearias.stream()
                .map(BarbeariaMapper::toListItemDto)
                .toList();
    }
    
    /**
     * Lista todos os serviços ativos de uma barbearia específica.
     * 
     * @param barbeariaId ID da barbearia
     * @return Lista de DTOs com serviços da barbearia
     * @throws IllegalArgumentException se barbearia não existe ou está inativa
     */
    public List<ServicoDto> listarServicosPorBarbearia(Long barbeariaId) {
        // Verifica se barbearia existe e está ativa
        @SuppressWarnings("null")
        JpaBarbearia barbearia = barbeariaRepository.findById(barbeariaId)
                .orElseThrow(() -> new IllegalArgumentException("Barbearia não encontrada"));
        
        if (!barbearia.isAtivo()) {
            throw new IllegalArgumentException("Barbearia está inativa");
        }
        
        // Busca serviços ativos da barbearia
        List<JpaServico> servicos = servicoRepository.findByBarbeariaIdAndAtivoTrue(barbeariaId);
        
        return servicos.stream()
                .map(ServicoMapper::toDto)
                .toList();
    }
    
    /**
     * Cria um novo serviço para uma barbearia.
     * 
     * Apenas a própria barbearia pode criar seus serviços.
     * 
     * Validações:
     * - Barbearia deve existir e estar ativa
     * - Nome do serviço é obrigatório
     * - Preço deve ser maior que zero
     * - Duração deve ser maior que zero
     * 
     * @param barbeariaId ID da barbearia proprietária do serviço
     * @param requestDto Dados do serviço a ser criado
     * @return DTO com dados do serviço criado
     * @throws IllegalArgumentException se dados inválidos ou barbearia não encontrada
     */
    @Transactional
    public ServicoDto criarServico(Long barbeariaId, com.barbearia.application.dto.ServicoRequestDto requestDto) {
        // Valida campos obrigatórios
        if (!requestDto.isValid()) {
            throw new IllegalArgumentException(
                    "Dados do serviço inválidos. Verifique se todos os campos obrigatórios foram preenchidos: " +
                    "nome, preço, duração e tipoServico. " +
                    "Tipos permitidos: CORTE, BARBA, MANICURE, SOBRANCELHA, COLORACAO, TRATAMENTO_CAPILAR");
        }
        
        // Verifica se a barbearia existe e está ativa
        @SuppressWarnings("null")
        JpaBarbearia barbearia = barbeariaRepository.findById(barbeariaId)
                .orElseThrow(() -> new IllegalArgumentException("Barbearia não encontrada"));
        
        if (!barbearia.isAtivo()) {
            throw new IllegalArgumentException("Barbearia está inativa e não pode criar serviços");
        }
        
        // Cria entidade JPA do serviço usando Factory Pattern
        // A factory cria a subclasse correta (JpaServicoCorte, JpaServicoBarba, etc.)
        // O tipoServico é obrigatório e deve ser um dos valores permitidos
        JpaServico servico = JpaServicoFactory.criar(requestDto.getTipoServico());
        servico.setBarbeariaId(barbeariaId);
        servico.setNome(requestDto.getNome().trim());
        servico.setDescricao(requestDto.getDescricao() != null ? requestDto.getDescricao().trim() : null);
        servico.setPreco(requestDto.getPreco());
        servico.setDuracao(requestDto.getDuracao());
        servico.setAtivo(true);
        
        // Salva no banco
        JpaServico servicoSalvo = servicoRepository.save(servico);
        
        // Retorna DTO
        return ServicoMapper.toDto(servicoSalvo);
    }
    
    /**
     * Cria ou atualiza um horário de funcionamento para uma barbearia.
     * 
     * Apenas a própria barbearia pode criar seus horários de funcionamento.
     * Se já existir um horário para o mesmo dia da semana, ele será atualizado.
     * 
     * Validações:
     * - Barbearia deve existir e estar ativa
     * - diaSemana deve estar entre 0 (domingo) e 6 (sábado)
     * - horaAbertura e horaFechamento são obrigatórios
     * - horaAbertura deve ser antes de horaFechamento
     * 
     * @param barbeariaId ID da barbearia proprietária do horário
     * @param requestDto Dados do horário de funcionamento a ser criado
     * @return DTO com dados do horário criado/atualizado
     * @throws IllegalArgumentException se dados inválidos ou barbearia não encontrada
     */
    @Transactional
    public HorarioFuncionamentoResponseDto criarHorarioFuncionamento(
            Long barbeariaId, 
            HorarioFuncionamentoRequestDto requestDto) {
        
        // Valida os dados do DTO
        if (!requestDto.isValid()) {
            throw new IllegalArgumentException(
                "Dados do horário inválidos. Verifique se:\n" +
                "- diaSemana está entre 0 (domingo) e 6 (sábado)\n" +
                "- horaAbertura e horaFechamento estão preenchidos\n" +
                "- horaAbertura é antes de horaFechamento"
            );
        }
        
        // Verifica se a barbearia existe e está ativa
        @SuppressWarnings("null")
        JpaBarbearia barbearia = barbeariaRepository.findById(barbeariaId)
                .orElseThrow(() -> new IllegalArgumentException("Barbearia não encontrada"));
        
        if (!barbearia.isAtivo()) {
            throw new IllegalArgumentException("Barbearia está inativa e não pode criar horários de funcionamento");
        }
        
        // Verifica se já existe um horário para este dia da semana
        Optional<JpaHorarioFuncionamento> horarioExistente = 
            horarioFuncionamentoRepository.findByBarbeariaIdAndDiaSemana(
                barbeariaId, 
                requestDto.getDiaSemana()
            );
        
        JpaHorarioFuncionamento horario;
        
        if (horarioExistente.isPresent()) {
            // Atualiza o horário existente
            horario = horarioExistente.get();
            horario.setHoraAbertura(requestDto.getHoraAbertura());
            horario.setHoraFechamento(requestDto.getHoraFechamento());
            horario.setAtivo(true);
        } else {
            // Cria novo horário
            horario = new JpaHorarioFuncionamento(
                barbeariaId,
                requestDto.getDiaSemana(),
                requestDto.getHoraAbertura(),
                requestDto.getHoraFechamento()
            );
            horario.setAtivo(true);
        }
        
        // Salva no banco
        JpaHorarioFuncionamento horarioSalvo = horarioFuncionamentoRepository.save(horario);
        
        // Retorna DTO
        return HorarioFuncionamentoMapper.toResponseDto(horarioSalvo);
    }
}

