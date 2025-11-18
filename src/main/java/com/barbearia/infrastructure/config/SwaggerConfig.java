package com.barbearia.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.examples.Example;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.media.*;
import io.swagger.v3.oas.models.parameters.RequestBody;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.Operation;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(apiInfo())
                .servers(apiServers())
                .addSecurityItem(new SecurityRequirement().addList("Bearer"))
                .components(new io.swagger.v3.oas.models.Components()
                        .addSecuritySchemes("Bearer", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("JWT Bearer token para autenticação")))
                .paths(new io.swagger.v3.oas.models.Paths()
                        // Clientes
                        .addPathItem("/api/auth/cliente/registrar", registrarClientePath())
                        .addPathItem("/api/auth/cliente/login", loginClientePath())
                        .addPathItem("/api/clientes/meus-agendamentos/historico", listarHistoricoPath())
                        .addPathItem("/api/clientes/meu-perfil", atualizarPerfilPath())
                        // Barbearias
                        .addPathItem("/api/auth/barbearia/registrar", registrarBarbeariaPath())
                        .addPathItem("/api/auth/barbearia/login", loginBarbeariaPath())
                        .addPathItem("/api/barbearias", listarBarbeariaPath())
                        .addPathItem("/api/barbearias/{id}/servicos", listarServicosPath())
                        .addPathItem("/api/barbearias/{id}/horarios-disponiveis", obterHorariosDisponiveisPath())
                        .addPathItem("/api/barbearias/servicos", criarServicoPath())
                        // Funcionários
                        .addPathItem("/api/barbearias/meus-funcionarios", meusFuncionariosPath())
                        // Agendamentos - Barbearia
                        .addPathItem("/api/barbearias/meus-agendamentos", meusAgendamentosPath())
                        .addPathItem("/api/barbearias/agendamentos/{id}", atualizarStatusAgendamentoPath())
                        // Gestão Financeira
                        .addPathItem("/api/barbearias/gestao-financeira", gestaoFinanceiraPath())
                        // Gestão de Clientes (LGPD)
                        .addPathItem("/api/barbearias/meus-clientes", meusClientesPath())
                        .addPathItem("/api/barbearias/clientes/{id}", detalhesClientePath())
                        // Agendamentos - Cliente
                        .addPathItem("/api/agendamentos", criarAgendamentoPath())
                        .addPathItem("/api/agendamentos/{id}", buscarPorIdPath()));
    }

    private Info apiInfo() {
        return new Info()
                .title("API Sua Barbearia")
                .version("1.0.0")
                .description("API REST para sistema de barbearia")
                .contact(new Contact()
                        .name("Time Sua Barbearia")
                        .email("contato@suabarbearia.com")
                        .url("https://github.com/guilhermerauj0/Sua-Barbearia"))
                .license(new License()
                        .name("MIT License")
                        .url("https://opensource.org/licenses/MIT"));
    }

    private List<Server> apiServers() {
        Server devServer = new Server()
                .url("http://localhost:8080")
                .description("Servidor de Desenvolvimento");
        
        Server prodServer = new Server()
                .url("https://sua-barbearia.onrender.com")
                .description("Servidor de Produção (Render)");
        
        return List.of(devServer, prodServer);
    }

    private PathItem registrarClientePath() {
        return new PathItem()
                .post(new Operation()
                        .tags(List.of("Clientes"))
                        .summary("Registrar cliente")
                        .description("Cadastra um novo cliente no sistema com os dados fornecidos")
                        .requestBody(new RequestBody()
                                .description("Dados do cliente para registro")
                                .required(true)
                                .content(new Content()
                                        .addMediaType("application/json", new MediaType()
                                                .schema(clienteRequestSchema())
                                                .example(clienteRequestExample()))))
                        .responses(new ApiResponses()
                                .addApiResponse("201", new ApiResponse()
                                        .description("Cliente registrado com sucesso")
                                        .content(new Content()
                                                .addMediaType("application/json", new MediaType()
                                                        .schema(clienteResponseSchema())
                                                        .example(clienteResponseExample()))))
                                .addApiResponse("400", new ApiResponse()
                                        .description("Dados inválidos ou email já cadastrado")
                                        .content(new Content()
                                                .addMediaType("application/json", new MediaType()
                                                        .example("Email já cadastrado no sistema"))))
                                .addApiResponse("500", new ApiResponse()
                                        .description("Erro interno do servidor")
                                        .content(new Content()
                                                .addMediaType("application/json", new MediaType()
                                                        .example("Erro ao registrar cliente"))))));
    }

    private PathItem loginClientePath() {
        return new PathItem()
                .post(new Operation()
                        .tags(List.of("Clientes"))
                        .summary("Login cliente")
                        .description("Autentica um cliente no sistema e retorna um token JWT")
                        .requestBody(new RequestBody()
                                .description("Credenciais de login")
                                .required(true)
                                .content(new Content()
                                        .addMediaType("application/json", new MediaType()
                                                .schema(loginRequestSchema())
                                                .example(loginRequestExample()))))
                        .responses(new ApiResponses()
                                .addApiResponse("200", new ApiResponse()
                                        .description("Login realizado com sucesso")
                                        .content(new Content()
                                                .addMediaType("application/json", new MediaType()
                                                        .schema(loginResponseSchema())
                                                        .example(loginResponseExample()))))
                                .addApiResponse("400", new ApiResponse()
                                        .description("Dados inválidos (email ou senha vazios)")
                                        .content(new Content()
                                                .addMediaType("application/json", new MediaType()
                                                        .example("Email e senha são obrigatórios"))))
                                .addApiResponse("401", new ApiResponse()
                                        .description("Credenciais inválidas")
                                        .content(new Content()
                                                .addMediaType("application/json", new MediaType()
                                                        .example("Email ou senha incorretos"))))
                                .addApiResponse("500", new ApiResponse()
                                        .description("Erro interno do servidor")
                                        .content(new Content()
                                                .addMediaType("application/json", new MediaType()
                                                        .example("Erro ao realizar login"))))));
    }

    private PathItem registrarBarbeariaPath() {
        return new PathItem()
                .post(new Operation()
                        .tags(List.of("Barbearias"))
                        .summary("Registrar barbearia")
                        .description("Cadastra uma nova barbearia no sistema com validação de CPF/CNPJ. Aceita tanto CPF (pessoa física) quanto CNPJ (pessoa jurídica).")
                        .requestBody(new RequestBody()
                                .description("Dados da barbearia para registro")
                                .required(true)
                                .content(new Content()
                                        .addMediaType("application/json", new MediaType()
                                                .schema(barbeariaRequestSchema())
                                                .addExamples("CPF", new Example()
                                                        .summary("Registro com CPF")
                                                        .description("Exemplo de registro de barbearia usando CPF (pessoa física)")
                                                        .value(barbeariaRequestCPFExample()))
                                                .addExamples("CNPJ", new Example()
                                                        .summary("Registro com CNPJ")
                                                        .description("Exemplo de registro de barbearia usando CNPJ (pessoa jurídica)")
                                                        .value(barbeariaRequestCNPJExample())))))
                        .responses(new ApiResponses()
                                .addApiResponse("201", new ApiResponse()
                                        .description("Barbearia registrada com sucesso")
                                        .content(new Content()
                                                .addMediaType("application/json", new MediaType()
                                                        .schema(barbeariaResponseSchema())
                                                        .example(barbeariaResponseExample()))))
                                .addApiResponse("400", new ApiResponse()
                                        .description("Dados inválidos (CPF/CNPJ inválido, email duplicado, etc.)")
                                        .content(new Content()
                                                .addMediaType("application/json", new MediaType()
                                                        .example("CPF inválido. Verifique o número informado."))))
                                .addApiResponse("500", new ApiResponse()
                                        .description("Erro interno do servidor")
                                        .content(new Content()
                                                .addMediaType("application/json", new MediaType()
                                                        .example("Erro ao registrar barbearia"))))));
    }

    private Schema<?> barbeariaRequestSchema() {
        Schema<?> schema = new Schema<>();
        schema.setType("object");
        schema.setDescription("Dados para registro de nova barbearia");
        schema.addProperty("nome", new StringSchema()
                .description("Nome do proprietário")
                .minLength(3)
                .maxLength(100)
                .example("Maria Santos"));
        schema.addProperty("email", new StringSchema()
                .description("Email da barbearia (deve ser único)")
                .format("email")
                .example("maria.santos@email.com"));
        schema.addProperty("senha", new StringSchema()
                .description("Senha forte (min 8 caracteres, com maiúscula, minúscula, número e caractere especial)")
                .minLength(8)
                .pattern("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&#])[A-Za-z\\d@$!%*?&#]{8,}$")
                .example("SenhaForte@123"));
        schema.addProperty("confirmarSenha", new StringSchema()
                .description("Confirmação da senha (deve ser igual ao campo senha)")
                .example("SenhaForte@123"));
        schema.addProperty("telefone", new StringSchema()
                .description("Telefone de contato")
                .pattern("^\\(?\\d{2}\\)?\\s?\\d{4,5}-?\\d{4}$")
                .example("(11) 98765-4321"));
        schema.addProperty("nomeFantasia", new StringSchema()
                .description("Nome fantasia da barbearia")
                .minLength(3)
                .maxLength(100)
                .example("Barbearia Elegance"));
        schema.addProperty("tipoDocumento", new StringSchema()
                .description("Tipo de documento (CPF para pessoa física ou CNPJ para pessoa jurídica)")
                .example("CPF"));
        schema.addProperty("documento", new StringSchema()
                .description("Número do CPF (11 dígitos) ou CNPJ (14 dígitos)")
                .example("123.456.789-09"));
        schema.addProperty("endereco", new StringSchema()
                .description("Endereço completo da barbearia")
                .minLength(10)
                .maxLength(200)
                .example("Rua das Flores, 123 - São Paulo/SP"));
        schema.setRequired(List.of("nome", "email", "senha", "confirmarSenha", "telefone", "nomeFantasia", "tipoDocumento", "documento", "endereco"));
        return schema;
    }

    private Object barbeariaRequestCPFExample() {
        return """
                {
                  "nome": "Ana Costa",
                  "email": "ana.costa@email.com",
                  "senha": "MinhaSenha@789",
                  "confirmarSenha": "MinhaSenha@789",
                  "telefone": "(11) 97654-3210",
                  "nomeFantasia": "Barbearia Elegance",
                  "tipoDocumento": "CPF",
                  "documento": "123.456.789-09",
                  "endereco": "Rua das Flores, 123 - São Paulo/SP"
                }
                """;
    }

    private Object barbeariaRequestCNPJExample() {
        return """
                {
                  "nome": "Carlos Oliveira",
                  "email": "carlos.oliveira@email.com",
                  "senha": "OutraSenha@456",
                  "confirmarSenha": "OutraSenha@456",
                  "telefone": "(21) 99876-5432",
                  "nomeFantasia": "Barbearia Premium",
                  "tipoDocumento": "CNPJ",
                  "documento": "11.222.333/0001-81",
                  "endereco": "Av. Principal, 500 - Rio de Janeiro/RJ"
                }
                """;
    }

    private Schema<?> barbeariaResponseSchema() {
        return new Schema<>()
                .type("object")
                .description("Dados da barbearia retornados após o registro (sem senha)")
                .addProperty("id", new IntegerSchema()
                        .description("ID único da barbearia")
                        .format("int64")
                        .example(2))
                .addProperty("nome", new StringSchema()
                        .description("Nome do proprietário")
                        .example("Ana Costa"))
                .addProperty("email", new StringSchema()
                        .description("Email da barbearia")
                        .example("ana.costa@email.com"))
                .addProperty("telefone", new StringSchema()
                        .description("Telefone de contato (apenas números)")
                        .example("11976543210"))
                .addProperty("nomeFantasia", new StringSchema()
                        .description("Nome fantasia da barbearia")
                        .example("Barbearia Elegance"))
                .addProperty("tipoDocumento", new StringSchema()
                        .description("Tipo de documento")
                        .example("CPF"))
                .addProperty("documento", new StringSchema()
                        .description("Número do documento (apenas números)")
                        .example("12345678909"))
                .addProperty("endereco", new StringSchema()
                        .description("Endereço completo")
                        .example("Rua das Flores, 123 - São Paulo/SP"))
                .addProperty("role", new StringSchema()
                        .description("Papel do usuário no sistema")
                        .example("BARBEARIA"))
                .addProperty("ativo", new BooleanSchema()
                        .description("Status de ativação da barbearia")
                        .example(true))
                .addProperty("dataCriacao", new StringSchema()
                        .description("Data e hora do registro")
                        .format("date-time")
                        .example("2025-11-17T11:30:00"));
    }

    private Object barbeariaResponseExample() {
        return """
                {
                  "id": 2,
                  "nome": "Ana Costa",
                  "email": "ana.costa@email.com",
                  "telefone": "11976543210",
                  "nomeFantasia": "Barbearia Elegance",
                  "tipoDocumento": "CPF",
                  "documento": "12345678909",
                  "endereco": "Rua das Flores, 123 - São Paulo/SP",
                  "role": "BARBEARIA",
                  "ativo": true,
                  "dataCriacao": "2025-11-17T11:30:00"
                }
                """;
    }

    private Schema<?> loginRequestSchema() {
        Schema<?> schema = new Schema<>();
        schema.setType("object");
        schema.setDescription("Credenciais para login de cliente");
        schema.addProperty("email", new StringSchema()
                .description("Email do cliente")
                .format("email")
                .example("joao.silva@email.com"));
        schema.addProperty("senha", new StringSchema()
                .description("Senha do cliente")
                .format("password")
                .example("SenhaForte@123"));
        schema.setRequired(List.of("email", "senha"));
        return schema;
    }

    private Object loginRequestExample() {
        return """
                {
                  "email": "pedro.santos@email.com",
                  "senha": "MinhaSenha@456"
                }
                """;
    }

    private Schema<?> loginResponseSchema() {
        return new Schema<>()
                .type("object")
                .description("Resposta do login contendo token JWT e dados do usuário")
                .addProperty("token", new StringSchema()
                        .description("Token JWT para autenticação")
                        .example("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJwZWRyby5zYW50b3NAZW1haWwuY29tIiwidXNlcklkIjoyLCJub21lIjoiUGVkcm8gU2FudG9zIiwicm9sZSI6IkNMSUVOVEUiLCJpYXQiOjE3MzE4MTcyMDAsImV4cCI6MTczMTgyMDgwMH0.abc123xyz"))
                .addProperty("tipo", new StringSchema()
                        .description("Tipo do token")
                        .example("Bearer"))
                .addProperty("userId", new IntegerSchema()
                        .description("ID do usuário autenticado")
                        .format("int64")
                        .example(2))
                .addProperty("nome", new StringSchema()
                        .description("Nome do usuário")
                        .example("Pedro Santos"))
                .addProperty("email", new StringSchema()
                        .description("Email do usuário")
                        .example("pedro.santos@email.com"))
                .addProperty("role", new StringSchema()
                        .description("Papel do usuário no sistema")
                        .example("CLIENTE"))
                .addProperty("expiresIn", new IntegerSchema()
                        .description("Tempo de expiração do token em milissegundos")
                        .format("int64")
                        .example(3600000));
    }

    private Object loginResponseExample() {
        return """
                {
                  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJwZWRyby5zYW50b3NAZW1haWwuY29tIiwidXNlcklkIjoyLCJub21lIjoiUGVkcm8gU2FudG9zIiwicm9sZSI6IkNMSUVOVEUiLCJpYXQiOjE3MzE4MTcyMDAsImV4cCI6MTczMTgyMDgwMH0.abc123xyz",
                  "tipo": "Bearer",
                  "userId": 2,
                  "nome": "Pedro Santos",
                  "email": "pedro.santos@email.com",
                  "role": "CLIENTE",
                  "expiresIn": 3600000
                }
                """;
    }

    private Schema<?> clienteRequestSchema() {
        Schema<?> schema = new Schema<>();
        schema.setType("object");
        schema.setDescription("Dados para registro de novo cliente");
        schema.addProperty("nome", new StringSchema()
                .description("Nome completo do cliente")
                .minLength(3)
                .maxLength(100)
                .example("João Silva"));
        schema.addProperty("email", new StringSchema()
                .description("Email do cliente (deve ser único)")
                .format("email")
                .example("joao.silva@email.com"));
        schema.addProperty("senha", new StringSchema()
                .description("Senha forte (min 8 caracteres, com maiúscula, minúscula, número e caractere especial)")
                .minLength(8)
                .pattern("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&#])[A-Za-z\\d@$!%*?&#]{8,}$")
                .example("SenhaForte@123"));
        schema.addProperty("confirmarSenha", new StringSchema()
                .description("Confirmação da senha (deve ser igual ao campo senha)")
                .example("SenhaForte@123"));
        schema.addProperty("telefone", new StringSchema()
                .description("Telefone do cliente")
                .pattern("^\\(?\\d{2}\\)?\\s?\\d{4,5}-?\\d{4}$")
                .example("(11) 98765-4321"));
        schema.setRequired(List.of("nome", "email", "senha", "confirmarSenha", "telefone"));
        return schema;
    }

    private Object clienteRequestExample() {
        return """
                {
                  "nome": "Pedro Santos",
                  "email": "pedro.santos@email.com",
                  "senha": "MinhaSenha@456",
                  "confirmarSenha": "MinhaSenha@456",
                  "telefone": "(11) 91234-5678"
                }
                """;
    }

    private Schema<?> clienteResponseSchema() {
        return new Schema<>()
                .type("object")
                .description("Dados do cliente retornados após o registro (sem senha)")
                .addProperty("id", new IntegerSchema()
                        .description("ID único do cliente")
                        .format("int64")
                        .example(2))
                .addProperty("nome", new StringSchema()
                        .description("Nome completo do cliente")
                        .example("Pedro Santos"))
                .addProperty("email", new StringSchema()
                        .description("Email do cliente")
                        .example("pedro.santos@email.com"))
                .addProperty("telefone", new StringSchema()
                        .description("Telefone do cliente")
                        .example("11912345678"))
                .addProperty("role", new StringSchema()
                        .description("Papel do usuário no sistema")
                        .example("CLIENTE"))
                .addProperty("ativo", new BooleanSchema()
                        .description("Status de ativação do cliente")
                        .example(true))
                .addProperty("dataCriacao", new StringSchema()
                        .description("Data e hora do registro")
                        .format("date-time")
                        .example("2025-11-17T10:30:00"));
    }

    private Object clienteResponseExample() {
        return """
                {
                  "id": 2,
                  "nome": "Pedro Santos",
                  "email": "pedro.santos@email.com",
                  "telefone": "11912345678",
                  "role": "CLIENTE",
                  "ativo": true,
                  "dataCriacao": "2025-11-17T10:30:00"
                }
                """;
    }

    private PathItem loginBarbeariaPath() {
        return new PathItem()
                .post(new Operation()
                        .tags(List.of("Barbearias"))
                        .summary("Login barbearia")
                        .description("Autentica uma barbearia no sistema e retorna um token JWT com barbeariaId")
                        .requestBody(new RequestBody()
                                .description("Credenciais de login")
                                .required(true)
                                .content(new Content()
                                        .addMediaType("application/json", new MediaType()
                                                .schema(loginRequestSchema())
                                                .example(loginBarbeariaRequestExample()))))
                        .responses(new ApiResponses()
                                .addApiResponse("200", new ApiResponse()
                                        .description("Login realizado com sucesso")
                                        .content(new Content()
                                                .addMediaType("application/json", new MediaType()
                                                        .schema(loginResponseSchema())
                                                        .example(loginBarbeariaResponseExample()))))
                                .addApiResponse("400", new ApiResponse()
                                        .description("Dados inválidos (email ou senha vazios)")
                                        .content(new Content()
                                                .addMediaType("application/json", new MediaType()
                                                        .example("Email e senha são obrigatórios"))))
                                .addApiResponse("401", new ApiResponse()
                                        .description("Credenciais inválidas")
                                        .content(new Content()
                                                .addMediaType("application/json", new MediaType()
                                                        .example("Email ou senha incorretos"))))
                                .addApiResponse("500", new ApiResponse()
                                        .description("Erro interno do servidor")
                                        .content(new Content()
                                                .addMediaType("application/json", new MediaType()
                                                        .example("Erro ao realizar login"))))));
    }

    private Object loginBarbeariaRequestExample() {
        return """
                {
                  "email": "maria.santos@email.com",
                  "senha": "SenhaForte@123"
                }
                """;
    }

    private Object loginBarbeariaResponseExample() {
        return """
                {
                  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
                  "tipo": "Bearer",
                  "userId": 5,
                  "nome": "Barbearia do Zé",
                  "email": "maria.santos@email.com",
                  "role": "BARBEARIA",
                  "expiresIn": 3600000
                }
                """;
    }

    private PathItem listarHistoricoPath() {
        return new PathItem()
                .get(new Operation()
                        .tags(List.of("Clientes"))
                        .summary("Listar histórico de agendamentos")
                        .description("Lista todos os agendamentos passados do cliente autenticado (dataHora < agora)")
                        .security(List.of(new SecurityRequirement().addList("Bearer")))
                        .responses(new ApiResponses()
                                .addApiResponse("200", new ApiResponse()
                                        .description("Lista de agendamentos passados obtida com sucesso")
                                        .content(new Content()
                                                .addMediaType("application/json", new MediaType()
                                                        .schema(new ArraySchema()
                                                                .items(agendamentoBriefSchema()))
                                                        .example(listarHistoricoExample()))))
                                .addApiResponse("400", new ApiResponse()
                                        .description("ID do cliente inválido")
                                        .content(new Content()
                                                .addMediaType("application/json", new MediaType()
                                                        .example("ID do cliente não pode ser nulo"))))
                                .addApiResponse("401", new ApiResponse()
                                        .description("Token JWT inválido ou ausente")
                                        .content(new Content()
                                                .addMediaType("application/json", new MediaType()
                                                        .example("Token JWT inválido ou clienteId não encontrado"))))
                                .addApiResponse("500", new ApiResponse()
                                        .description("Erro interno do servidor")
                                        .content(new Content()
                                                .addMediaType("application/json", new MediaType()
                                                        .example("Erro ao buscar histórico"))))));
    }

    private Schema<?> agendamentoBriefSchema() {
        return new Schema<>()
                .type("object")
                .description("Dados resumidos de um agendamento")
                .addProperty("id", new IntegerSchema()
                        .description("ID único do agendamento")
                        .format("int64")
                        .example(1))
                .addProperty("dataHora", new StringSchema()
                        .description("Data e hora do agendamento")
                        .format("date-time")
                        .example("2025-11-10T14:30:00"))
                .addProperty("status", new StringSchema()
                        .description("Status do agendamento")
                        .example("CONCLUIDO"))
                .addProperty("nomeBarbearia", new StringSchema()
                        .description("Nome da barbearia")
                        .example("Barbearia #1"))
                .addProperty("nomeBarbeiro", new StringSchema()
                        .description("Nome do barbeiro (pode ser null)")
                        .nullable(true)
                        .example(null))
                .addProperty("nomeServico", new StringSchema()
                        .description("Nome do serviço")
                        .example("Serviço #1"))
                .addProperty("observacoes", new StringSchema()
                        .description("Observações do agendamento")
                        .example("Corte + barba"));
    }

    private Object listarHistoricoExample() {
        return """
                [
                  {
                    "id": 1,
                    "dataHora": "2025-10-03T14:30:00",
                    "status": "CONCLUIDO",
                    "nomeBarbearia": "Barbearia #1",
                    "nomeBarbeiro": null,
                    "nomeServico": "Serviço #1",
                    "observacoes": "Corte + barba"
                  },
                  {
                    "id": 2,
                    "dataHora": "2025-10-19T14:30:00",
                    "status": "CONCLUIDO",
                    "nomeBarbearia": "Barbearia #1",
                    "nomeBarbeiro": null,
                    "nomeServico": "Serviço #1",
                    "observacoes": "Corte simples, cliente satisfeito"
                  }
                ]
                """;
    }

    private PathItem buscarPorIdPath() {
        return new PathItem()
                .get(new Operation()
                        .tags(List.of("Agendamentos"))
                        .summary("Buscar detalhes de um agendamento")
                        .description("Retorna os detalhes completos de um agendamento específico. Cliente só pode ver seus próprios agendamentos.")
                        .security(List.of(new SecurityRequirement().addList("Bearer")))
                        .addParametersItem(new io.swagger.v3.oas.models.parameters.Parameter()
                                .name("id")
                                .in("path")
                                .required(true)
                                .description("ID do agendamento")
                                .schema(new IntegerSchema()
                                        .format("int64")
                                        .example(1)))
                        .responses(new ApiResponses()
                                .addApiResponse("200", new ApiResponse()
                                        .description("Agendamento encontrado com sucesso")
                                        .content(new Content()
                                                .addMediaType("application/json", new MediaType()
                                                        .schema(agendamentoDetailSchema())
                                                        .example(agendamentoDetailExample()))))
                                .addApiResponse("401", new ApiResponse()
                                        .description("Token JWT inválido ou ausente")
                                        .content(new Content()
                                                .addMediaType("application/json", new MediaType()
                                                        .example("Token JWT inválido ou userId não encontrado"))))
                                .addApiResponse("403", new ApiResponse()
                                        .description("Acesso negado - cliente não pode acessar este agendamento")
                                        .content(new Content()
                                                .addMediaType("application/json", new MediaType()
                                                        .example("Você não tem permissão para acessar este agendamento"))))
                                .addApiResponse("404", new ApiResponse()
                                        .description("Agendamento não encontrado")
                                        .content(new Content()
                                                .addMediaType("application/json", new MediaType()
                                                        .example("Agendamento com ID 999 não existe"))))
                                .addApiResponse("500", new ApiResponse()
                                        .description("Erro interno do servidor")
                                        .content(new Content()
                                                .addMediaType("application/json", new MediaType()
                                                        .example("Erro ao buscar agendamento"))))));
    }

    private Schema<?> agendamentoDetailSchema() {
        return new Schema<>()
                .type("object")
                .description("Dados completos de um agendamento")
                .addProperty("id", new IntegerSchema()
                        .description("ID único do agendamento")
                        .format("int64")
                        .example(1))
                .addProperty("dataHora", new StringSchema()
                        .description("Data e hora do agendamento")
                        .format("date-time")
                        .example("2025-11-10T14:30:00"))
                .addProperty("status", new StringSchema()
                        .description("Status do agendamento (PENDENTE, CONFIRMADO, CONCLUIDO, CANCELADO)")
                        .example("CONCLUIDO"))
                .addProperty("observacoes", new StringSchema()
                        .description("Observações do agendamento")
                        .example("Corte + barba"))
                .addProperty("dataCriacao", new StringSchema()
                        .description("Data e hora da criação")
                        .format("date-time")
                        .example("2025-11-03T12:00:00"))
                .addProperty("dataAtualizacao", new StringSchema()
                        .description("Data e hora da última atualização")
                        .format("date-time")
                        .example("2025-11-03T14:30:00"))
                .addProperty("clienteId", new IntegerSchema()
                        .description("ID do cliente")
                        .format("int64")
                        .example(1))
                .addProperty("nomeCliente", new StringSchema()
                        .description("Nome do cliente")
                        .example("João Silva"))
                .addProperty("emailCliente", new StringSchema()
                        .description("Email do cliente")
                        .example("joao.silva@example.com"))
                .addProperty("telefoneCliente", new StringSchema()
                        .description("Telefone do cliente")
                        .example("(11) 98765-4321"))
                .addProperty("documentoCliente", new StringSchema()
                        .description("Documento do cliente")
                        .example("12345678901"))
                .addProperty("barbeariaId", new IntegerSchema()
                        .description("ID da barbearia")
                        .format("int64")
                        .example(1))
                .addProperty("nomeBarbearia", new StringSchema()
                        .description("Nome da barbearia")
                        .example("Barbearia #1"))
                .addProperty("enderecoBarbearia", new StringSchema()
                        .description("Endereço da barbearia")
                        .example("Rua Exemplo, 123"))
                .addProperty("telefoneBarbearia", new StringSchema()
                        .description("Telefone da barbearia")
                        .example("(00) 0000-0000"))
                .addProperty("barbeiroId", new IntegerSchema()
                        .description("ID do barbeiro (pode ser null)")
                        .format("int64")
                        .nullable(true)
                        .example(null))
                .addProperty("nomeBarbeiro", new StringSchema()
                        .description("Nome do barbeiro (pode ser null)")
                        .nullable(true)
                        .example(null))
                .addProperty("servicoId", new IntegerSchema()
                        .description("ID do serviço")
                        .format("int64")
                        .example(1))
                .addProperty("nomeServico", new StringSchema()
                        .description("Nome do serviço")
                        .example("Serviço #1"))
                .addProperty("descricaoServico", new StringSchema()
                        .description("Descrição do serviço")
                        .example("Descrição do serviço"))
                .addProperty("valorServico", new NumberSchema()
                        .description("Valor do serviço")
                        .format("double")
                        .example(0.0));
    }

    private Object agendamentoDetailExample() {
        return """
                {
                  "id": 1,
                  "dataHora": "2025-11-10T14:30:00",
                  "status": "CONCLUIDO",
                  "observacoes": "Corte + barba",
                  "dataCriacao": "2025-11-03T12:00:00",
                  "dataAtualizacao": "2025-11-03T14:30:00",
                  "clienteId": 1,
                  "nomeCliente": "João Silva",
                  "emailCliente": "joao.silva@example.com",
                  "telefoneCliente": "(11) 98765-4321",
                  "documentoCliente": "12345678901",
                  "barbeariaId": 1,
                  "nomeBarbearia": "Barbearia #1",
                  "enderecoBarbearia": "Rua Exemplo, 123",
                  "telefoneBarbearia": "(00) 0000-0000",
                  "barbeiroId": null,
                  "nomeBarbeiro": null,
                  "servicoId": 1,
                  "nomeServico": "Serviço #1",
                  "descricaoServico": "Descrição do serviço",
                  "valorServico": 0.0
                }
                """;
    }

    private PathItem atualizarPerfilPath() {
        return new PathItem()
                .put(new Operation()
                        .tags(List.of("Clientes"))
                        .summary("Atualizar meu perfil")
                        .description("Atualiza dados do cliente autenticado. Permite atualização parcial - apenas campos fornecidos são atualizados")
                        .security(List.of(new SecurityRequirement().addList("Bearer")))
                        .requestBody(new RequestBody()
                                .description("Dados a serem atualizados (todos opcionais)")
                                .required(true)
                                .content(new Content()
                                        .addMediaType("application/json", new MediaType()
                                                .schema(clienteUpdateSchema())
                                                .example(clienteUpdateExample()))))
                        .responses(new ApiResponses()
                                .addApiResponse("200", new ApiResponse()
                                        .description("Perfil atualizado com sucesso")
                                        .content(new Content()
                                                .addMediaType("application/json", new MediaType()
                                                        .schema(clienteProfileSchema())
                                                        .example(clienteProfileExample()))))
                                .addApiResponse("400", new ApiResponse()
                                        .description("Dados inválidos ou nenhum campo para atualizar")
                                        .content(new Content()
                                                .addMediaType("application/json", new MediaType()
                                                        .example("Email já cadastrado por outro usuário"))))
                                .addApiResponse("401", new ApiResponse()
                                        .description("Token JWT inválido ou ausente"))
                                .addApiResponse("404", new ApiResponse()
                                        .description("Cliente não encontrado"))
                                .addApiResponse("500", new ApiResponse()
                                        .description("Erro interno do servidor"))));
    }

    private Schema<?> clienteProfileSchema() {
        return new ObjectSchema()
                .addProperty("id", new NumberSchema().format("int64"))
                .addProperty("nome", new StringSchema())
                .addProperty("email", new StringSchema())
                .addProperty("telefone", new StringSchema())
                .addProperty("role", new StringSchema())
                .addProperty("ativo", new BooleanSchema())
                .addProperty("dataCriacao", new StringSchema().format("date-time"))
                .addProperty("dataAtualizacao", new StringSchema().format("date-time"));
    }

    private String clienteProfileExample() {
        return """
                {
                  "id": 1,
                  "nome": "João Silva",
                  "email": "joao.silva@example.com",
                  "telefone": "(11) 98765-4321",
                  "role": "CLIENTE",
                  "ativo": true,
                  "dataCriacao": "2025-11-01T10:00:00",
                  "dataAtualizacao": "2025-11-05T14:30:00"
                }
                """;
    }

    private Schema<?> clienteUpdateSchema() {
        return new ObjectSchema()
                .addProperty("nome", new StringSchema())
                .addProperty("email", new StringSchema())
                .addProperty("telefone", new StringSchema());
    }

    private String clienteUpdateExample() {
        return """
                {
                  "nome": "João Silva Atualizado",
                  "email": "joao.novo@example.com",
                  "telefone": "(11) 99999-8888"
                }
                """;
    }

    private PathItem listarBarbeariaPath() {
        return new PathItem()
                .get(new Operation()
                        .tags(List.of("Barbearias"))
                        .summary("Listar barbearias")
                        .description("Retorna lista de todas as barbearias ativas do sistema")
                        .responses(new ApiResponses()
                                .addApiResponse("200", new ApiResponse()
                                        .description("Lista de barbearias retornada com sucesso")
                                        .content(new Content()
                                                .addMediaType("application/json", new MediaType()
                                                        .schema(new ArraySchema()
                                                                .items(barbeariaListItemSchema()))
                                                        .example(barbeariaListExample()))))
                                .addApiResponse("500", new ApiResponse()
                                        .description("Erro interno do servidor"))));
    }

    private PathItem listarServicosPath() {
        return new PathItem()
                .get(new Operation()
                        .tags(List.of("Barbearias"))
                        .summary("Listar serviços de uma barbearia")
                        .description("Retorna lista de serviços ativos de uma barbearia específica. " +
                                "Suporta múltiplos tipos de serviços: CORTE, BARBA, MANICURE, SOBRANCELHA, COLORACAO e TRATAMENTO_CAPILAR")
                        .addParametersItem(new io.swagger.v3.oas.models.parameters.Parameter()
                                .name("id")
                                .in("path")
                                .description("ID da barbearia")
                                .required(true)
                                .schema(new NumberSchema().format("int64")))
                        .responses(new ApiResponses()
                                .addApiResponse("200", new ApiResponse()
                                        .description("Lista de serviços retornada com sucesso")
                                        .content(new Content()
                                                .addMediaType("application/json", new MediaType()
                                                        .schema(new ArraySchema()
                                                                .items(servicoSchema()))
                                                        .example(servicoListExample()))))
                                .addApiResponse("404", new ApiResponse()
                                        .description("Barbearia não encontrada")
                                        .content(new Content()
                                                .addMediaType("application/json", new MediaType()
                                                        .example("Barbearia não encontrada"))))
                                .addApiResponse("400", new ApiResponse()
                                        .description("Barbearia inativa")
                                        .content(new Content()
                                                .addMediaType("application/json", new MediaType()
                                                        .example("Barbearia está inativa"))))
                                .addApiResponse("500", new ApiResponse()
                                        .description("Erro interno do servidor"))));
    }

    private Schema<?> barbeariaListItemSchema() {
        return new ObjectSchema()
                .addProperty("id", new NumberSchema().format("int64"))
                .addProperty("nome", new StringSchema())
                .addProperty("nomeFantasia", new StringSchema())
                .addProperty("endereco", new StringSchema())
                .addProperty("telefone", new StringSchema())
                .addProperty("email", new StringSchema())
                .addProperty("avaliacaoMedia", new NumberSchema().format("double"));
    }

    private String barbeariaListExample() {
        return """
                [
                  {
                    "id": 1,
                    "nome": "Barbearia Premium",
                    "nomeFantasia": "Premium Barber",
                    "endereco": "Rua Exemplo, 123",
                    "telefone": "(11) 3000-0000",
                    "email": "premium@barbearia.com",
                    "avaliacaoMedia": 4.5
                  },
                  {
                    "id": 4,
                    "nome": "Maria Santos",
                    "nomeFantasia": "Maria Barber",
                    "endereco": "Av. Paulista, 1000",
                    "telefone": "(11) 4000-0000",
                    "email": "maria.santos@email.com",
                    "avaliacaoMedia": 0.0
                  }
                ]
                """;
    }

    private Schema<?> servicoSchema() {
        return new ObjectSchema()
                .addProperty("id", new NumberSchema().format("int64").description("Identificador único do serviço"))
                .addProperty("nome", new StringSchema().description("Nome do serviço"))
                .addProperty("descricao", new StringSchema().description("Descrição detalhada do serviço"))
                .addProperty("preco", new NumberSchema().format("double").description("Preço em reais"))
                .addProperty("duracao", new NumberSchema().format("int32").description("Duração em minutos"))
                .addProperty("barbeariaId", new NumberSchema().format("int64").description("ID da barbearia que oferece o serviço"))
                .addProperty("ativo", new BooleanSchema().description("Status de ativação do serviço"))
                .addProperty("tipoServico", buildTipoServicoSchema());
    }

    private Schema<?> buildTipoServicoSchema() {
        StringSchema schema = new StringSchema();
        schema.setDescription("Tipo de serviço ofertado (obrigatório). " +
                "Valores permitidos: CORTE, BARBA, MANICURE, SOBRANCELHA, COLORACAO, TRATAMENTO_CAPILAR");
        schema.setEnum(List.of("CORTE", "BARBA", "MANICURE", "SOBRANCELHA", "COLORACAO", "TRATAMENTO_CAPILAR"));
        schema.setExample("CORTE");
        return schema;
    }

    private String servicoListExample() {
        return """
                [
                  {
                    "id": 1,
                    "nome": "Corte de Cabelo",
                    "descricao": "Corte clássico",
                    "preco": 50.0,
                    "duracao": 30,
                    "barbeariaId": 1,
                    "ativo": true,
                    "tipoServico": "CORTE"
                  },
                  {
                    "id": 2,
                    "nome": "Barba",
                    "descricao": "Aparelho de barba",
                    "preco": 40.0,
                    "duracao": 20,
                    "barbeariaId": 1,
                    "ativo": true,
                    "tipoServico": "BARBA"
                  },
                  {
                    "id": 3,
                    "nome": "Corte + Barba",
                    "descricao": "Corte e aparelho de barba",
                    "preco": 80.0,
                    "descricao": "Combo completo",
                    "preco": 80.0,
                    "duracao": 50,
                    "barbeariaId": 1,
                    "ativo": true,
                    "tipoServico": "CORTE"
                  },
                  {
                    "id": 4,
                    "nome": "Manicure",
                    "descricao": "Manicure com esmaltação",
                    "preco": 40.0,
                    "duracao": 45,
                    "barbeariaId": 1,
                    "ativo": true,
                    "tipoServico": "MANICURE"
                  },
                  {
                    "id": 5,
                    "nome": "Design de Sobrancelha",
                    "descricao": "Sobrancelha com design",
                    "preco": 25.0,
                    "duracao": 15,
                    "barbeariaId": 1,
                    "ativo": true,
                    "tipoServico": "SOBRANCELHA"
                  },
                  {
                    "id": 6,
                    "nome": "Coloração Premium",
                    "descricao": "Coloração com produtos importados",
                    "preco": 120.0,
                    "duracao": 90,
                    "barbeariaId": 1,
                    "ativo": true,
                    "tipoServico": "COLORACAO"
                  },
                  {
                    "id": 7,
                    "nome": "Hidratação Profunda",
                    "descricao": "Hidratação com mask premium",
                    "preco": 60.0,
                    "duracao": 60,
                    "barbeariaId": 1,
                    "ativo": true,
                    "tipoServico": "TRATAMENTO_CAPILAR"
                  }
                ]
                """;
    }

    private PathItem criarServicoPath() {
        return new PathItem()
                .post(new Operation()
                        .tags(List.of("Barbearias"))
                        .summary("Criar serviço")
                        .description("Cria um novo serviço para a barbearia autenticada.\n\n" +
                                "**IMPORTANTE:** O campo 'tipoServico' é OBRIGATÓRIO e deve ser um dos seguintes valores:\n" +
                                "- **CORTE**: Cortes de cabelo em geral\n" +
                                "- **BARBA**: Aparagem e design de barba\n" +
                                "- **MANICURE**: Serviços de manicure/unhas\n" +
                                "- **SOBRANCELHA**: Design e coloração de sobrancelhas\n" +
                                "- **COLORACAO**: Serviços de coloração capilar\n" +
                                "- **TRATAMENTO_CAPILAR**: Hidratação, alinhamento, etc\n\n" +
                                "É possível criar múltiplos serviços do mesmo tipo com nomes e preços diferentes " +
                                "(ex: 'Corte Clássico' e 'Corte Degradê' ambos do tipo CORTE).\n\n" +
                                "Todos os campos são obrigatórios: nome, preço, duração e tipoServico.")
                        .security(List.of(new SecurityRequirement().addList("Bearer")))
                        .requestBody(new RequestBody()
                                .description("Dados do serviço a ser criado - TODOS OS CAMPOS SÃO OBRIGATÓRIOS")
                                .required(true)
                                .content(new Content()
                                        .addMediaType("application/json", new MediaType()
                                                .schema(servicoRequestSchema())
                                                .example(servicoRequestExample()))))
                        .responses(new ApiResponses()
                                .addApiResponse("201", new ApiResponse()
                                        .description("Serviço criado com sucesso")
                                        .content(new Content()
                                                .addMediaType("application/json", new MediaType()
                                                        .schema(servicoSchema())
                                                        .example(servicoResponseExample()))))
                                .addApiResponse("400", new ApiResponse()
                                        .description("Dados inválidos. Verifique se:\n" +
                                                "- nome não está vazio\n" +
                                                "- preço é maior que zero\n" +
                                                "- duração é maior que zero\n" +
                                                "- tipoServico está preenchido e é um dos valores permitidos"))
                                .addApiResponse("401", new ApiResponse()
                                        .description("Não autenticado - Token JWT inválido ou expirado"))
                                .addApiResponse("403", new ApiResponse()
                                        .description("Não autorizado - Role BARBEARIA necessário"))
                                .addApiResponse("500", new ApiResponse()
                                        .description("Erro interno do servidor"))));
    }

    private Schema<?> servicoRequestSchema() {
        return new ObjectSchema()
                .addProperty("nome", new StringSchema()
                        .description("Nome do serviço (obrigatório, não pode estar vazio)")
                        .example("Corte Degradê"))
                .addProperty("descricao", new StringSchema()
                        .description("Descrição detalhada do serviço (opcional)")
                        .example("Corte com degradê total, máquina 0 nas laterais"))
                .addProperty("preco", new NumberSchema().format("double")
                        .description("Preço do serviço em reais (obrigatório, deve ser > 0)")
                        .example(60.00))
                .addProperty("duracao", new NumberSchema().format("int32")
                        .description("Duração do serviço em minutos (obrigatório, deve ser > 0)")
                        .example(40))
                .addProperty("tipoServico", buildTipoServicoSchema());
    }

    private String servicoRequestExample() {
        return """
                {
                  "nome": "Corte Degradê",
                  "descricao": "Corte com degradê total, máquina 0 nas laterais",
                  "preco": 60.00,
                  "duracao": 40,
                  "tipoServico": "CORTE"
                }
                """;
    }

    private String servicoResponseExample() {
        return """
                {
                  "id": 3,
                  "nome": "Corte Degradê",
                  "descricao": "Corte com degradê total, máquina 0 nas laterais",
                  "preco": 60.0,
                  "duracao": 40,
                  "barbeariaId": 1,
                  "ativo": true,
                  "tipoServico": "CORTE"
                }
                """;
    }

    private PathItem obterHorariosDisponiveisPath() {
        return new PathItem()
                .get(new Operation()
                        .tags(List.of("Barbearias"))
                        .summary("Obter horários disponíveis para agendamento")
                        .description("Retorna os horários disponíveis para um serviço em uma data específica. " +
                                "Considera o horário de funcionamento da barbearia, profissionais qualificados, " +
                                "duração do serviço e agendamentos existentes.")
                        .addParametersItem(new io.swagger.v3.oas.models.parameters.Parameter()
                                .name("id")
                                .in("path")
                                .description("ID da barbearia")
                                .required(true)
                                .schema(new Schema<>().type("integer").format("int64")))
                        .addParametersItem(new io.swagger.v3.oas.models.parameters.Parameter()
                                .name("servicoId")
                                .in("query")
                                .description("ID do serviço desejado")
                                .required(true)
                                .schema(new Schema<>().type("integer").format("int64")))
                        .addParametersItem(new io.swagger.v3.oas.models.parameters.Parameter()
                                .name("data")
                                .in("query")
                                .description("Data para consultar (formato: yyyy-MM-dd)")
                                .required(true)
                                .schema(new Schema<>().type("string").format("date")))
                        .responses(new ApiResponses()
                                .addApiResponse("200", new ApiResponse()
                                        .description("Lista de horários disponíveis")
                                        .content(new Content()
                                                .addMediaType("application/json", new MediaType()
                                                        .schema(new ArraySchema()
                                                                .items(new ObjectSchema()
                                                                        .addProperty("funcionarioId", new Schema<>().type("integer").format("int64"))
                                                                        .addProperty("funcionarioNome", new Schema<>().type("string"))
                                                                        .addProperty("profissao", new StringSchema()
                                                                                .addEnumItem("BARBEIRO")
                                                                                .addEnumItem("MANICURE")
                                                                                .addEnumItem("ESTETICISTA")
                                                                                .addEnumItem("COLORISTA"))
                                                                        .addProperty("data", new Schema<>().type("string").format("date"))
                                                                        .addProperty("horarioInicio", new Schema<>().type("string").format("time"))
                                                                        .addProperty("horarioFim", new Schema<>().type("string").format("time"))))
                                                        .example(obterHorariosDisponiveisExample()))))
                                .addApiResponse("400", new ApiResponse()
                                        .description("Parâmetros inválidos")
                                        .content(new Content()
                                                .addMediaType("application/json", new MediaType()
                                                        .example("Parâmetro servicoId é obrigatório"))))
                                .addApiResponse("404", new ApiResponse()
                                        .description("Barbearia ou serviço não encontrado")
                                        .content(new Content()
                                                .addMediaType("application/json", new MediaType()
                                                        .example("Barbearia não encontrada"))))
                                .addApiResponse("500", new ApiResponse()
                                        .description("Erro interno do servidor")
                                        .content(new Content()
                                                .addMediaType("application/json", new MediaType()
                                                        .example("Erro ao obter horários disponíveis"))))));
    }

    private String obterHorariosDisponiveisExample() {
        return """
                [
                  {
                    "funcionarioId": 1,
                    "funcionarioNome": "João Silva",
                    "profissao": "BARBEIRO",
                    "data": "2025-11-20",
                    "horarioInicio": "09:00:00",
                    "horarioFim": "09:30:00"
                  },
                  {
                    "funcionarioId": 1,
                    "funcionarioNome": "João Silva",
                    "profissao": "BARBEIRO",
                    "data": "2025-11-20",
                    "horarioInicio": "09:30:00",
                    "horarioFim": "10:00:00"
                  },
                  {
                    "funcionarioId": 2,
                    "funcionarioNome": "Maria Santos",
                    "profissao": "MANICURE",
                    "data": "2025-11-20",
                    "horarioInicio": "09:00:00",
                    "horarioFim": "09:30:00"
                  }
                ]
                """;
    }

    private PathItem criarAgendamentoPath() {
        return new PathItem()
                .post(new Operation()
                        .tags(List.of("Agendamentos"))
                        .summary("Criar agendamento")
                        .description("Cria um novo agendamento para o cliente autenticado. Valida a disponibilidade do profissional e se ele executa o serviço solicitado.")
                        .security(List.of(new SecurityRequirement().addList("Bearer")))
                        .requestBody(new RequestBody()
                                .description("Dados do agendamento a criar")
                                .required(true)
                                .content(new Content()
                                        .addMediaType("application/json", new MediaType()
                                                .schema(agendamentoRequestSchema())
                                                .example(agendamentoRequestExample()))))
                        .responses(new ApiResponses()
                                .addApiResponse("201", new ApiResponse()
                                        .description("Agendamento criado com sucesso")
                                        .content(new Content()
                                                .addMediaType("application/json", new MediaType()
                                                        .schema(agendamentoResponseSchema())
                                                        .example(agendamentoResponseExample()))))
                                .addApiResponse("400", new ApiResponse()
                                        .description("Dados inválidos ou recurso não encontrado")
                                        .content(new Content()
                                                .addMediaType("application/json", new MediaType()
                                                        .example("Serviço com ID 999 não existe"))))
                                .addApiResponse("401", new ApiResponse()
                                        .description("Token JWT inválido ou não fornecido")
                                        .content(new Content()
                                                .addMediaType("application/json", new MediaType()
                                                        .example("Token JWT inválido ou userId não encontrado"))))
                                .addApiResponse("403", new ApiResponse()
                                        .description("Acesso negado (apenas clientes podem criar agendamentos)")
                                        .content(new Content()
                                                .addMediaType("application/json", new MediaType()
                                                        .example("Apenas clientes podem criar agendamentos"))))
                                .addApiResponse("422", new ApiResponse()
                                        .description("Validação de negócio falhou (conflito de horário, etc)")
                                        .content(new Content()
                                                .addMediaType("application/json", new MediaType()
                                                        .example("Horário não disponível para este funcionário"))))
                                .addApiResponse("500", new ApiResponse()
                                        .description("Erro interno do servidor")
                                        .content(new Content()
                                                .addMediaType("application/json", new MediaType()
                                                        .example("Erro ao criar agendamento"))))));
    }

    private Schema<?> agendamentoRequestSchema() {
        Schema<?> schema = new Schema<>();
        schema.setType("object");
        schema.setDescription("Dados para criação de novo agendamento");
        schema.addProperty("servicoId", new NumberSchema()
                .description("ID do serviço desejado")
                .example(1L));
        schema.addProperty("funcionarioId", new NumberSchema()
                .description("ID do profissional que executará o serviço")
                .example(1L));
        schema.addProperty("dataHora", new StringSchema()
                .format("date-time")
                .description("Data e hora do agendamento (formato ISO 8601)")
                .example("2025-11-20T14:30:00"));
        schema.addProperty("observacoes", new StringSchema()
                .description("Observações adicionais (opcional)")
                .example("Preferência de corte com máquina 2"));
        schema.setRequired(List.of("servicoId", "funcionarioId", "dataHora"));
        return schema;
    }

    private Object agendamentoRequestExample() {
        return """
                {
                  "servicoId": 1,
                  "funcionarioId": 1,
                  "dataHora": "2025-11-20T14:30:00",
                  "observacoes": "Corte normal com máquina 2"
                }
                """;
    }

    private Schema<?> agendamentoResponseSchema() {
        Schema<?> schema = new Schema<>();
        schema.setType("object");
        schema.setDescription("Dados do agendamento criado");
        schema.addProperty("id", new NumberSchema()
                .description("ID do agendamento criado")
                .example(123L));
        schema.addProperty("clienteId", new NumberSchema()
                .description("ID do cliente proprietário do agendamento")
                .example(5L));
        schema.addProperty("barbeariaId", new NumberSchema()
                .description("ID da barbearia")
                .example(1L));
        schema.addProperty("servicoId", new NumberSchema()
                .description("ID do serviço")
                .example(1L));
        schema.addProperty("funcionarioId", new NumberSchema()
                .description("ID do profissional")
                .example(1L));
        schema.addProperty("dataHora", new StringSchema()
                .format("date-time")
                .description("Data e hora do agendamento")
                .example("2025-11-20T14:30:00"));
        schema.addProperty("status", new StringSchema()
                .description("Status do agendamento")
                .example("PENDENTE"));
        schema.addProperty("observacoes", new StringSchema()
                .description("Observações do agendamento")
                .example("Corte normal com máquina 2"));
        schema.addProperty("dataCriacao", new StringSchema()
                .format("date-time")
                .description("Data e hora de criação")
                .example("2025-11-17T10:15:30"));
        schema.addProperty("dataAtualizacao", new StringSchema()
                .format("date-time")
                .description("Data e hora da última atualização")
                .example("2025-11-17T10:15:30"));
        return schema;
    }

    private Object agendamentoResponseExample() {
        return """
                {
                  "id": 123,
                  "clienteId": 5,
                  "barbeariaId": 1,
                  "servicoId": 1,
                  "funcionarioId": 1,
                  "dataHora": "2025-11-20T14:30:00",
                  "status": "PENDENTE",
                  "observacoes": "Corte normal com máquina 2",
                  "dataCriacao": "2025-11-17T10:15:30",
                  "dataAtualizacao": "2025-11-17T10:15:30"
                }
                """;
    }

    private PathItem meusFuncionariosPath() {
        return new PathItem()
                .get(new Operation()
                        .tags(List.of("Funcionários"))
                        .summary("Listar meus funcionários")
                        .description("Lista todos os funcionários ativos da barbearia autenticada.\n\n" +
                                "**AUTENTICAÇÃO:** Endpoint protegido - requer token JWT.\n\n" +
                                "**AUTORIZAÇÃO:** Apenas usuários com role BARBEARIA podem acessar.\n\n" +
                                "**IDENTIFICAÇÃO:** O ID da barbearia é extraído automaticamente do token JWT (não precisa ser informado).\n\n" +
                                "Retorna apenas funcionários ativos (campo 'ativo' = true).")
                        .security(List.of(new SecurityRequirement().addList("Bearer")))
                        .responses(new ApiResponses()
                                .addApiResponse("200", new ApiResponse()
                                        .description("Lista de funcionários retornada com sucesso")
                                        .content(new Content()
                                                .addMediaType("application/json", new MediaType()
                                                        .schema(new ArraySchema()
                                                                .items(funcionarioResponseSchema()))
                                                        .example(funcionarioListResponseExample()))))
                                .addApiResponse("401", new ApiResponse()
                                        .description("Não autenticado - Token JWT inválido ou expirado"))
                                .addApiResponse("403", new ApiResponse()
                                        .description("Não autorizado - Role BARBEARIA necessário"))
                                .addApiResponse("500", new ApiResponse()
                                        .description("Erro interno do servidor"))))
                .post(new Operation()
                        .tags(List.of("Funcionários"))
                        .summary("Criar funcionário")
                        .description("Cria um novo funcionário para a barbearia autenticada.\n\n" +
                                "**AUTENTICAÇÃO:** Endpoint protegido - requer token JWT.\n\n" +
                                "**AUTORIZAÇÃO:** Apenas usuários com role BARBEARIA podem acessar.\n\n" +
                                "**IDENTIFICAÇÃO:** O ID da barbearia é extraído automaticamente do token JWT.\n\n" +
                                "**IMPORTANTE:** O campo 'profissao' é OBRIGATÓRIO e deve ser um dos seguintes valores:\n" +
                                "- **BARBEIRO**: Profissional especializado em cortes de cabelo e barba\n" +
                                "- **MANICURE**: Profissional especializado em serviços de unhas\n" +
                                "- **ESTETICISTA**: Profissional especializado em estética (sobrancelhas, etc)\n" +
                                "- **COLORISTA**: Profissional especializado em coloração capilar\n\n" +
                                "**VALIDAÇÕES:**\n" +
                                "- nome: obrigatório, 3-100 caracteres\n" +
                                "- email: obrigatório, formato válido, único por barbearia\n" +
                                "- telefone: obrigatório, 10-20 caracteres\n" +
                                "- profissao: obrigatório, um dos valores acima\n\n" +
                                "**OBSERVAÇÃO:** Funcionários não possuem login no sistema (não são usuários autenticáveis).")
                        .security(List.of(new SecurityRequirement().addList("Bearer")))
                        .requestBody(new RequestBody()
                                .description("Dados do funcionário a ser criado - TODOS OS CAMPOS SÃO OBRIGATÓRIOS")
                                .required(true)
                                .content(new Content()
                                        .addMediaType("application/json", new MediaType()
                                                .schema(funcionarioRequestSchema())
                                                .examples(Map.of(
                                                        "barbeiro", new Example()
                                                                .summary("Exemplo: Barbeiro")
                                                                .value(funcionarioRequestExampleBarbeiro()),
                                                        "manicure", new Example()
                                                                .summary("Exemplo: Manicure")
                                                                .value(funcionarioRequestExampleManicure()),
                                                        "esteticista", new Example()
                                                                .summary("Exemplo: Esteticista")
                                                                .value(funcionarioRequestExampleEsteticista()),
                                                        "colorista", new Example()
                                                                .summary("Exemplo: Colorista")
                                                                .value(funcionarioRequestExampleColorista())
                                                )))))
                        .responses(new ApiResponses()
                                .addApiResponse("201", new ApiResponse()
                                        .description("Funcionário criado com sucesso")
                                        .content(new Content()
                                                .addMediaType("application/json", new MediaType()
                                                        .schema(funcionarioResponseSchema())
                                                        .example(funcionarioResponseExample()))))
                                .addApiResponse("400", new ApiResponse()
                                        .description("Dados inválidos. Possíveis erros:\n" +
                                                "- Nome vazio ou com menos de 3 caracteres\n" +
                                                "- Email inválido ou já cadastrado para esta barbearia\n" +
                                                "- Telefone vazio ou com formato inválido\n" +
                                                "- Profissão vazia ou com valor não permitido"))
                                .addApiResponse("401", new ApiResponse()
                                        .description("Não autenticado - Token JWT inválido ou expirado"))
                                .addApiResponse("403", new ApiResponse()
                                        .description("Não autorizado - Role BARBEARIA necessário"))
                                .addApiResponse("500", new ApiResponse()
                                        .description("Erro interno do servidor"))));
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private Schema<?> funcionarioRequestSchema() {
        Schema profissaoSchema = new StringSchema()
                .description("Profissão do funcionário (obrigatório: BARBEIRO, MANICURE, ESTETICISTA, COLORISTA)")
                .example("BARBEIRO");
        profissaoSchema._enum(List.of("BARBEIRO", "MANICURE", "ESTETICISTA", "COLORISTA"));
        
        return new ObjectSchema()
                .addProperty("nome", new StringSchema()
                        .description("Nome completo do funcionário (obrigatório, 3-100 caracteres)")
                        .example("Carlos Silva"))
                .addProperty("email", new StringSchema()
                        .description("Email do funcionário (obrigatório, formato válido, único por barbearia)")
                        .example("carlos.silva@email.com"))
                .addProperty("telefone", new StringSchema()
                        .description("Telefone do funcionário (obrigatório, 10-20 caracteres)")
                        .example("(11) 98765-4321"))
                .addProperty("profissao", profissaoSchema);
    }

    private Schema<?> funcionarioResponseSchema() {
        return new ObjectSchema()
                .addProperty("id", new NumberSchema()
                        .description("ID do funcionário")
                        .example(1L))
                .addProperty("barbeariaId", new NumberSchema()
                        .description("ID da barbearia proprietária")
                        .example(1L))
                .addProperty("nome", new StringSchema()
                        .description("Nome completo do funcionário")
                        .example("Carlos Silva"))
                .addProperty("email", new StringSchema()
                        .description("Email do funcionário")
                        .example("carlos.silva@email.com"))
                .addProperty("telefone", new StringSchema()
                        .description("Telefone do funcionário")
                        .example("(11) 98765-4321"))
                .addProperty("profissao", new StringSchema()
                        .description("Profissão do funcionário")
                        .example("BARBEIRO"))
                .addProperty("ativo", new BooleanSchema()
                        .description("Indica se o funcionário está ativo")
                        .example(true))
                .addProperty("dataCriacao", new StringSchema()
                        .format("date-time")
                        .description("Data e hora de criação do registro")
                        .example("2025-01-20T10:30:00"))
                .addProperty("dataAtualizacao", new StringSchema()
                        .format("date-time")
                        .description("Data e hora da última atualização")
                        .example("2025-01-20T10:30:00"));
    }

    private Object funcionarioRequestExampleBarbeiro() {
        return """
                {
                  "nome": "Carlos Silva",
                  "email": "carlos.silva@email.com",
                  "telefone": "(11) 98765-4321",
                  "profissao": "BARBEIRO"
                }
                """;
    }

    private Object funcionarioRequestExampleManicure() {
        return """
                {
                  "nome": "Juliana Costa",
                  "email": "juliana.costa@email.com",
                  "telefone": "(11) 98765-1234",
                  "profissao": "MANICURE"
                }
                """;
    }

    private Object funcionarioRequestExampleEsteticista() {
        return """
                {
                  "nome": "Fernanda Oliveira",
                  "email": "fernanda.oliveira@email.com",
                  "telefone": "(11) 98765-5678",
                  "profissao": "ESTETICISTA"
                }
                """;
    }

    private Object funcionarioRequestExampleColorista() {
        return """
                {
                  "nome": "Roberto Alves",
                  "email": "roberto.alves@email.com",
                  "telefone": "(11) 98765-9999",
                  "profissao": "COLORISTA"
                }
                """;
    }

    private Object funcionarioResponseExample() {
        return """
                {
                  "id": 1,
                  "barbeariaId": 1,
                  "nome": "Carlos Silva",
                  "email": "carlos.silva@email.com",
                  "telefone": "(11) 98765-4321",
                  "profissao": "BARBEIRO",
                  "ativo": true,
                  "dataCriacao": "2025-01-20T10:30:00",
                  "dataAtualizacao": "2025-01-20T10:30:00"
                }
                """;
    }

    private Object funcionarioListResponseExample() {
        return """
                [
                  {
                    "id": 1,
                    "barbeariaId": 1,
                    "nome": "Carlos Silva",
                    "email": "carlos.silva@email.com",
                    "telefone": "(11) 98765-4321",
                    "profissao": "BARBEIRO",
                    "ativo": true,
                    "dataCriacao": "2025-01-20T10:30:00",
                    "dataAtualizacao": "2025-01-20T10:30:00"
                  },
                  {
                    "id": 2,
                    "barbeariaId": 1,
                    "nome": "Juliana Costa",
                    "email": "juliana.costa@email.com",
                    "telefone": "(11) 98765-1234",
                    "profissao": "MANICURE",
                    "ativo": true,
                    "dataCriacao": "2025-01-20T11:15:00",
                    "dataAtualizacao": "2025-01-20T11:15:00"
                  },
                  {
                    "id": 3,
                    "barbeariaId": 1,
                    "nome": "Fernanda Oliveira",
                    "email": "fernanda.oliveira@email.com",
                    "telefone": "(11) 98765-5678",
                    "profissao": "ESTETICISTA",
                    "ativo": true,
                    "dataCriacao": "2025-01-20T14:45:00",
                    "dataAtualizacao": "2025-01-20T14:45:00"
                  },
                  {
                    "id": 4,
                    "barbeariaId": 1,
                    "nome": "Roberto Alves",
                    "email": "roberto.alves@email.com",
                    "telefone": "(11) 98765-9999",
                    "profissao": "COLORISTA",
                    "ativo": true,
                    "dataCriacao": "2025-01-20T16:20:00",
                    "dataAtualizacao": "2025-01-20T16:20:00"
                  }
                ]
                """;
    }
    
    private PathItem meusAgendamentosPath() {
        return new PathItem()
                .get(new Operation()
                        .tags(List.of("Gestão de Agendamentos - Barbearia"))
                        .summary("Listar meus agendamentos")
                        .description("Lista agendamentos da barbearia autenticada com filtro opcional por data.\n\n" +
                                "**AUTENTICAÇÃO:** Endpoint protegido - requer token JWT.\n\n" +
                                "**AUTORIZAÇÃO:** Apenas usuários com role BARBEARIA podem acessar.\n\n" +
                                "**FILTRO POR DATA:** Parâmetro opcional 'data' (formato yyyy-MM-dd).\n" +
                                "- Se fornecido: retorna apenas agendamentos daquela data específica\n" +
                                "- Se omitido: retorna todos os agendamentos da barbearia\n\n" +
                                "**DADOS RETORNADOS:** Informações completas incluindo:\n" +
                                "- Dados do agendamento (id, dataHora, status, observações)\n" +
                                "- Dados do cliente (id, nome, telefone)\n" +
                                "- Dados do serviço (id, nome, tipo, preço, duração)\n" +
                                "- Dados do funcionário (id, nome, profissão)")
                        .security(List.of(new SecurityRequirement().addList("Bearer")))
                        .addParametersItem(new io.swagger.v3.oas.models.parameters.Parameter()
                                .name("data")
                                .in("query")
                                .required(false)
                                .description("Data para filtrar agendamentos (formato: yyyy-MM-dd). Exemplo: 2025-12-01")
                                .schema(new StringSchema()
                                        .format("date")
                                        .example("2025-12-01")))
                        .responses(new ApiResponses()
                                .addApiResponse("200", new ApiResponse()
                                        .description("Lista de agendamentos retornada com sucesso")
                                        .content(new Content()
                                                .addMediaType("application/json", new MediaType()
                                                        .schema(new ArraySchema()
                                                                .items(agendamentoBarbeariaSchema()))
                                                        .example(agendamentoBarbeariaListExample()))))
                                .addApiResponse("400", new ApiResponse()
                                        .description("Formato de data inválido")
                                        .content(new Content()
                                                .addMediaType("application/json", new MediaType()
                                                        .example("Formato de data inválido. Use yyyy-MM-dd"))))
                                .addApiResponse("401", new ApiResponse()
                                        .description("Não autenticado - Token JWT inválido ou expirado"))
                                .addApiResponse("403", new ApiResponse()
                                        .description("Não autorizado - Role BARBEARIA necessário"))
                                .addApiResponse("500", new ApiResponse()
                                        .description("Erro interno do servidor"))));
    }
    
    private PathItem atualizarStatusAgendamentoPath() {
        return new PathItem()
                .patch(new Operation()
                        .tags(List.of("Gestão de Agendamentos - Barbearia"))
                        .summary("Atualizar status do agendamento")
                        .description("Atualiza o status de um agendamento da barbearia autenticada.\n\n" +
                                "**AUTENTICAÇÃO:** Endpoint protegido - requer token JWT.\n\n" +
                                "**AUTORIZAÇÃO:** Apenas usuários com role BARBEARIA podem acessar.\n\n" +
                                "**PROPRIEDADE:** O agendamento deve pertencer à barbearia autenticada.\n\n" +
                                "**IDEMPOTÊNCIA:** Chamar com o mesmo status múltiplas vezes não gera erro.\n\n" +
                                "**STATUS PERMITIDOS:**\n" +
                                "- PENDENTE: Aguardando confirmação\n" +
                                "- CONFIRMADO: Agendamento confirmado pela barbearia\n" +
                                "- CONCLUIDO: Serviço realizado\n" +
                                "- CANCELADO: Agendamento cancelado\n\n" +
                                "**REGRAS DE TRANSIÇÃO:**\n" +
                                "- ❌ Não pode confirmar agendamento cancelado\n" +
                                "- ❌ Não pode cancelar agendamento concluído\n" +
                                "- ✅ Pode sempre marcar como concluído\n" +
                                "- ✅ Pode cancelar agendamento pendente ou confirmado\n\n" +
                                "**NOTIFICAÇÕES:** Cliente é notificado automaticamente sobre mudanças de status.")
                        .security(List.of(new SecurityRequirement().addList("Bearer")))
                        .addParametersItem(new io.swagger.v3.oas.models.parameters.Parameter()
                                .name("id")
                                .in("path")
                                .required(true)
                                .description("ID do agendamento a ser atualizado")
                                .schema(new NumberSchema()
                                        .format("int64")
                                        .example(100)))
                        .requestBody(new RequestBody()
                                .description("Novo status do agendamento")
                                .required(true)
                                .content(new Content()
                                        .addMediaType("application/json", new MediaType()
                                                .schema(agendamentoUpdateSchema())
                                                .examples(Map.of(
                                                        "confirmar", new Example()
                                                                .summary("Confirmar agendamento")
                                                                .description("Muda status de PENDENTE para CONFIRMADO")
                                                                .value(agendamentoUpdateExampleConfirmar()),
                                                        "concluir", new Example()
                                                                .summary("Concluir agendamento")
                                                                .description("Marca agendamento como CONCLUIDO após realização do serviço")
                                                                .value(agendamentoUpdateExampleConcluir()),
                                                        "cancelar", new Example()
                                                                .summary("Cancelar agendamento")
                                                                .description("Cancela agendamento (não pode ser cancelado se já concluído)")
                                                                .value(agendamentoUpdateExampleCancelar())
                                                )))))
                        .responses(new ApiResponses()
                                .addApiResponse("200", new ApiResponse()
                                        .description("Status atualizado com sucesso")
                                        .content(new Content()
                                                .addMediaType("application/json", new MediaType()
                                                        .schema(agendamentoResponseSchema())
                                                        .example(agendamentoUpdateResponseExample()))))
                                .addApiResponse("400", new ApiResponse()
                                        .description("Transição de status inválida")
                                        .content(new Content()
                                                .addMediaType("application/json", new MediaType()
                                                        .example("Não é possível confirmar um agendamento cancelado"))))
                                .addApiResponse("401", new ApiResponse()
                                        .description("Não autenticado - Token JWT inválido ou expirado"))
                                .addApiResponse("403", new ApiResponse()
                                        .description("Não autorizado - Agendamento não pertence à barbearia")
                                        .content(new Content()
                                                .addMediaType("application/json", new MediaType()
                                                        .example("Este agendamento não pertence à sua barbearia"))))
                                .addApiResponse("404", new ApiResponse()
                                        .description("Agendamento não encontrado")
                                        .content(new Content()
                                                .addMediaType("application/json", new MediaType()
                                                        .example("Agendamento com ID 999 não existe"))))
                                .addApiResponse("500", new ApiResponse()
                                        .description("Erro interno do servidor"))));
    }
    
    private Schema<?> agendamentoBarbeariaSchema() {
        Schema<?> schema = new Schema<>();
        schema.setType("object");
        schema.addProperty("id", new NumberSchema()
                .format("int64")
                .description("ID do agendamento")
                .example(100));
        schema.addProperty("dataHora", new StringSchema()
                .format("date-time")
                .description("Data e hora do agendamento")
                .example("2025-12-01T14:00:00"));
        schema.addProperty("status", new StringSchema()
                .description("Status do agendamento")
                .example("PENDENTE"));
        schema.addProperty("observacoes", new StringSchema()
                .description("Observações do agendamento")
                .example("Corte degradê"));
        schema.addProperty("clienteId", new NumberSchema()
                .format("int64")
                .description("ID do cliente")
                .example(10));
        schema.addProperty("clienteNome", new StringSchema()
                .description("Nome do cliente")
                .example("João Silva"));
        schema.addProperty("clienteTelefone", new StringSchema()
                .description("Telefone do cliente")
                .example("(11) 98765-4321"));
        schema.addProperty("servicoId", new NumberSchema()
                .format("int64")
                .description("ID do serviço")
                .example(5));
        schema.addProperty("servicoNome", new StringSchema()
                .description("Nome do serviço")
                .example("Corte Degradê"));
        schema.addProperty("servicoTipo", new StringSchema()
                .description("Tipo do serviço")
                .example("CORTE"));
        schema.addProperty("servicoPreco", new NumberSchema()
                .format("double")
                .description("Preço do serviço")
                .example(50.00));
        schema.addProperty("servicoDuracao", new NumberSchema()
                .format("int32")
                .description("Duração do serviço em minutos")
                .example(30));
        schema.addProperty("funcionarioId", new NumberSchema()
                .format("int64")
                .description("ID do funcionário")
                .example(3));
        schema.addProperty("funcionarioNome", new StringSchema()
                .description("Nome do funcionário")
                .example("Carlos Barbeiro"));
        schema.addProperty("funcionarioProfissao", new StringSchema()
                .description("Profissão do funcionário")
                .example("BARBEIRO"));
        schema.addProperty("dataCriacao", new StringSchema()
                .format("date-time")
                .description("Data de criação do agendamento")
                .example("2025-11-18T10:00:00"));
        schema.addProperty("dataAtualizacao", new StringSchema()
                .format("date-time")
                .description("Data da última atualização")
                .example("2025-11-18T10:00:00"));
        return schema;
    }
    
    private Object agendamentoBarbeariaListExample() {
        return """
                [
                  {
                    "id": 100,
                    "dataHora": "2025-12-01T14:00:00",
                    "status": "PENDENTE",
                    "observacoes": "Corte degradê",
                    "clienteId": 10,
                    "clienteNome": "João Silva",
                    "clienteTelefone": "(11) 98765-4321",
                    "servicoId": 5,
                    "servicoNome": "Corte Degradê",
                    "servicoTipo": "CORTE",
                    "servicoPreco": 50.00,
                    "servicoDuracao": 30,
                    "funcionarioId": 3,
                    "funcionarioNome": "Carlos Barbeiro",
                    "funcionarioProfissao": "BARBEIRO",
                    "dataCriacao": "2025-11-18T10:00:00",
                    "dataAtualizacao": "2025-11-18T10:00:00"
                  },
                  {
                    "id": 101,
                    "dataHora": "2025-12-01T15:00:00",
                    "status": "CONFIRMADO",
                    "observacoes": "Manicure completa",
                    "clienteId": 11,
                    "clienteNome": "Maria Santos",
                    "clienteTelefone": "(11) 91234-5678",
                    "servicoId": 8,
                    "servicoNome": "Manicure Premium",
                    "servicoTipo": "MANICURE",
                    "servicoPreco": 40.00,
                    "servicoDuracao": 45,
                    "funcionarioId": 4,
                    "funcionarioNome": "Ana Manicure",
                    "funcionarioProfissao": "MANICURE",
                    "dataCriacao": "2025-11-18T11:00:00",
                    "dataAtualizacao": "2025-11-18T12:00:00"
                  }
                ]
                """;
    }
    
    @SuppressWarnings({"rawtypes", "unchecked"})
    private Schema<?> agendamentoUpdateSchema() {
        Schema statusSchema = new StringSchema()
                .description("Novo status do agendamento")
                .example("CONFIRMADO");
        statusSchema._enum(List.of("PENDENTE", "CONFIRMADO", "CONCLUIDO", "CANCELADO"));
        
        return new ObjectSchema()
                .addProperty("status", statusSchema)
                .required(List.of("status"));
    }
    
    private Object agendamentoUpdateExampleConfirmar() {
        return """
                {
                  "status": "CONFIRMADO"
                }
                """;
    }
    
    private Object agendamentoUpdateExampleConcluir() {
        return """
                {
                  "status": "CONCLUIDO"
                }
                """;
    }
    
    private Object agendamentoUpdateExampleCancelar() {
        return """
                {
                  "status": "CANCELADO"
                }
                """;
    }
    
    private Object agendamentoUpdateResponseExample() {
        return """
                {
                  "id": 100,
                  "clienteId": 10,
                  "barbeariaId": 1,
                  "servicoId": 5,
                  "funcionarioId": 3,
                  "dataHora": "2025-12-01T14:00:00",
                  "status": "CONFIRMADO",
                  "observacoes": "Corte degradê",
                  "dataCriacao": "2025-11-18T10:00:00",
                  "dataAtualizacao": "2025-11-18T13:30:00"
                }
                """;
    }
    
    private PathItem gestaoFinanceiraPath() {
        return new PathItem()
                .get(new Operation()
                        .tags(List.of("Gestão Financeira - Barbearia"))
                        .summary("Obter relatório financeiro")
                        .description("Retorna relatório financeiro completo da barbearia autenticada por período.\n\n" +
                                "**AUTENTICAÇÃO:** Endpoint protegido - requer token JWT.\n\n" +
                                "**AUTORIZAÇÃO:** Apenas usuários com role BARBEARIA podem acessar.\n\n" +
                                "**SEGURANÇA:** Apenas a própria barbearia pode acessar seus dados financeiros.\n\n" +
                                "**PERÍODOS SUPORTADOS:**\n" +
                                "- **DIA:** Últimas 24 horas\n" +
                                "- **SEMANA:** Últimos 7 dias (padrão)\n" +
                                "- **MES:** Últimos 30 dias\n\n" +
                                "**DADOS DO RELATÓRIO:**\n" +
                                "- Faturamento total do período (apenas agendamentos concluídos)\n" +
                                "- Total de agendamentos concluídos\n" +
                                "- Ticket médio (valor médio por agendamento)\n" +
                                "- Faturamento médio por dia\n" +
                                "- Top 5 serviços mais rentáveis com percentuais\n\n" +
                                "**CACHE:** Relatórios são cacheados para melhor performance.\n\n" +
                                "**CÁLCULOS:**\n" +
                                "- Ticket médio = Faturamento total / Total de agendamentos\n" +
                                "- Faturamento por dia = Faturamento total / Dias do período\n" +
                                "- Percentual de serviço = (Faturamento do serviço / Faturamento total) × 100")
                        .security(List.of(new SecurityRequirement().addList("Bearer")))
                        .addParametersItem(new io.swagger.v3.oas.models.parameters.Parameter()
                                .name("periodo")
                                .in("query")
                                .required(false)
                                .description("Período do relatório: DIA, SEMANA ou MES (padrão: MES)")
                                .schema(new StringSchema()
                                        ._enum(List.of("DIA", "SEMANA", "MES"))
                                        ._default("MES")
                                        .example("MES")))
                        .responses(new ApiResponses()
                                .addApiResponse("200", new ApiResponse()
                                        .description("Relatório financeiro gerado com sucesso")
                                        .content(new Content()
                                                .addMediaType("application/json", new MediaType()
                                                        .schema(relatorioFinanceiroSchema())
                                                        .examples(Map.of(
                                                                "mensal", new Example()
                                                                        .summary("Relatório mensal")
                                                                        .description("Relatório financeiro dos últimos 30 dias")
                                                                        .value(relatorioFinanceiroMensalExample()),
                                                                "semanal", new Example()
                                                                        .summary("Relatório semanal")
                                                                        .description("Relatório financeiro dos últimos 7 dias")
                                                                        .value(relatorioFinanceiroSemanalExample()),
                                                                "diario", new Example()
                                                                        .summary("Relatório diário")
                                                                        .description("Relatório financeiro das últimas 24 horas")
                                                                        .value(relatorioFinanceiroDiarioExample())
                                                        )))))
                                .addApiResponse("400", new ApiResponse()
                                        .description("Período inválido")
                                        .content(new Content()
                                                .addMediaType("application/json", new MediaType()
                                                        .example("Período do relatório não pode ser nulo"))))
                                .addApiResponse("401", new ApiResponse()
                                        .description("Não autenticado - Token JWT inválido ou expirado"))
                                .addApiResponse("403", new ApiResponse()
                                        .description("Não autorizado - Role BARBEARIA necessário"))
                                .addApiResponse("500", new ApiResponse()
                                        .description("Erro interno do servidor"))));
    }
    
    private Schema<?> relatorioFinanceiroSchema() {
        Schema<?> schema = new Schema<>();
        schema.setType("object");
        schema.setDescription("Relatório financeiro completo da barbearia");
        schema.addProperty("periodo", new StringSchema()
                .description("Período do relatório")
                .example("MES"));
        schema.addProperty("dataInicio", new StringSchema()
                .format("date-time")
                .description("Data/hora de início do período")
                .example("2025-10-19T00:51:00"));
        schema.addProperty("dataFim", new StringSchema()
                .format("date-time")
                .description("Data/hora de fim do período")
                .example("2025-11-18T00:51:00"));
        schema.addProperty("faturamentoTotal", new NumberSchema()
                .format("double")
                .description("Faturamento total do período (R$)")
                .example(15000.00));
        schema.addProperty("totalAgendamentos", new NumberSchema()
                .format("int64")
                .description("Total de agendamentos concluídos")
                .example(300));
        schema.addProperty("ticketMedio", new NumberSchema()
                .format("double")
                .description("Valor médio por agendamento (R$)")
                .example(50.00));
        schema.addProperty("faturamentoPorDia", new NumberSchema()
                .format("double")
                .description("Faturamento médio por dia (R$)")
                .example(500.00));
        
        Schema<?> servicoRentabilidadeSchema = new Schema<>();
        servicoRentabilidadeSchema.setType("object");
        servicoRentabilidadeSchema.addProperty("servicoId", new NumberSchema()
                .format("int64")
                .example(5));
        servicoRentabilidadeSchema.addProperty("servicoNome", new StringSchema()
                .example("Corte Degradê"));
        servicoRentabilidadeSchema.addProperty("totalRealizacoes", new NumberSchema()
                .format("int64")
                .example(150));
        servicoRentabilidadeSchema.addProperty("faturamentoTotal", new NumberSchema()
                .format("double")
                .example(7500.00));
        servicoRentabilidadeSchema.addProperty("percentualFaturamento", new NumberSchema()
                .format("double")
                .example(50.00));
        
        schema.addProperty("servicosMaisRentaveis", new ArraySchema()
                .items(servicoRentabilidadeSchema)
                .description("Top 5 serviços mais rentáveis do período"));
        
        return schema;
    }
    
    private Object relatorioFinanceiroMensalExample() {
        return """
                {
                  "periodo": "MES",
                  "dataInicio": "2025-10-19T00:51:00",
                  "dataFim": "2025-11-18T00:51:00",
                  "faturamentoTotal": 15000.00,
                  "totalAgendamentos": 300,
                  "ticketMedio": 50.00,
                  "faturamentoPorDia": 500.00,
                  "servicosMaisRentaveis": [
                    {
                      "servicoId": 5,
                      "servicoNome": "Corte Degradê",
                      "totalRealizacoes": 150,
                      "faturamentoTotal": 7500.00,
                      "percentualFaturamento": 50.00
                    },
                    {
                      "servicoId": 3,
                      "servicoNome": "Barba Completa",
                      "totalRealizacoes": 80,
                      "faturamentoTotal": 3200.00,
                      "percentualFaturamento": 21.33
                    },
                    {
                      "servicoId": 8,
                      "servicoNome": "Manicure Premium",
                      "totalRealizacoes": 50,
                      "faturamentoTotal": 2000.00,
                      "percentualFaturamento": 13.33
                    },
                    {
                      "servicoId": 12,
                      "servicoNome": "Sombrancelha",
                      "totalRealizacoes": 60,
                      "faturamentoTotal": 1800.00,
                      "percentualFaturamento": 12.00
                    },
                    {
                      "servicoId": 1,
                      "servicoNome": "Corte Simples",
                      "totalRealizacoes": 30,
                      "faturamentoTotal": 900.00,
                      "percentualFaturamento": 6.00
                    }
                  ]
                }
                """;
    }
    
    private Object relatorioFinanceiroSemanalExample() {
        return """
                {
                  "periodo": "SEMANA",
                  "dataInicio": "2025-11-11T00:51:00",
                  "dataFim": "2025-11-18T00:51:00",
                  "faturamentoTotal": 3500.00,
                  "totalAgendamentos": 70,
                  "ticketMedio": 50.00,
                  "faturamentoPorDia": 500.00,
                  "servicosMaisRentaveis": [
                    {
                      "servicoId": 5,
                      "servicoNome": "Corte Degradê",
                      "totalRealizacoes": 35,
                      "faturamentoTotal": 1750.00,
                      "percentualFaturamento": 50.00
                    },
                    {
                      "servicoId": 3,
                      "servicoNome": "Barba Completa",
                      "totalRealizacoes": 20,
                      "faturamentoTotal": 800.00,
                      "percentualFaturamento": 22.86
                    },
                    {
                      "servicoId": 8,
                      "servicoNome": "Manicure Premium",
                      "totalRealizacoes": 15,
                      "faturamentoTotal": 600.00,
                      "percentualFaturamento": 17.14
                    }
                  ]
                }
                """;
    }
    
    private Object relatorioFinanceiroDiarioExample() {
        return """
                {
                  "periodo": "DIA",
                  "dataInicio": "2025-11-17T00:51:00",
                  "dataFim": "2025-11-18T00:51:00",
                  "faturamentoTotal": 500.00,
                  "totalAgendamentos": 10,
                  "ticketMedio": 50.00,
                  "faturamentoPorDia": 500.00,
                  "servicosMaisRentaveis": [
                    {
                      "servicoId": 5,
                      "servicoNome": "Corte Degradê",
                      "totalRealizacoes": 5,
                      "faturamentoTotal": 250.00,
                      "percentualFaturamento": 50.00
                    },
                    {
                      "servicoId": 3,
                      "servicoNome": "Barba Completa",
                      "totalRealizacoes": 3,
                      "faturamentoTotal": 120.00,
                      "percentualFaturamento": 24.00
                    },
                    {
                      "servicoId": 8,
                      "servicoNome": "Manicure Premium",
                      "totalRealizacoes": 2,
                      "faturamentoTotal": 80.00,
                      "percentualFaturamento": 16.00
                    }
                  ]
                }
                """;
    }
    
    // ===== Gestão de Clientes (LGPD) - T15 =====
    
    private PathItem meusClientesPath() {
        return new PathItem()
                .get(new Operation()
                        .tags(List.of("Gestão de Clientes - Barbearia (LGPD)"))
                        .summary("Listar meus clientes atendidos")
                        .description("Lista todos os clientes que foram atendidos pela barbearia. Clientes anonimizados não aparecem.")
                        .security(List.of(new SecurityRequirement().addList("Bearer")))
                        .responses(new ApiResponses()
                                .addApiResponse("200", new ApiResponse()
                                        .description("Lista de clientes")
                                        .content(new Content()
                                                .addMediaType("application/json", new MediaType()
                                                        .example("""
                                                                [{
                                                                  "id": 10,
                                                                  "nome": "João Silva",
                                                                  "telefone": "11987654321",
                                                                  "email": "joao@email.com",
                                                                  "totalAgendamentos": 5,
                                                                  "ultimoAgendamento": "2025-11-15T14:00:00",
                                                                  "ativo": true,
                                                                  "anonimizado": false
                                                                }]
                                                                """))))
                                .addApiResponse("401", new ApiResponse().description("Não autenticado"))
                                .addApiResponse("403", new ApiResponse().description("Não autorizado"))));
    }
    
    private PathItem detalhesClientePath() {
        return new PathItem()
                .get(new Operation()
                        .tags(List.of("Gestão de Clientes - Barbearia (LGPD)"))
                        .summary("Buscar detalhes de um cliente")
                        .description("Retorna detalhes completos do cliente incluindo histórico de agendamentos.")
                        .security(List.of(new SecurityRequirement().addList("Bearer")))
                        .addParametersItem(new io.swagger.v3.oas.models.parameters.Parameter()
                                .name("id")
                                .in("path")
                                .required(true)
                                .schema(new NumberSchema().format("int64")))
                        .responses(new ApiResponses()
                                .addApiResponse("200", new ApiResponse()
                                        .description("Detalhes do cliente")
                                        .content(new Content()
                                                .addMediaType("application/json", new MediaType()
                                                        .example("""
                                                                {
                                                                  "id": 10,
                                                                  "nome": "João Silva",
                                                                  "telefone": "11987654321",
                                                                  "email": "joao@email.com",
                                                                  "documento": null,
                                                                  "endereco": null,
                                                                  "totalAgendamentos": 5,
                                                                  "agendamentosConcluidos": 4,
                                                                  "agendamentosCancelados": 1,
                                                                  "primeiroAgendamento": "2025-10-01T10:00:00",
                                                                  "ultimoAgendamento": "2025-11-15T14:00:00",
                                                                  "ativo": true,
                                                                  "anonimizado": false,
                                                                  "dataCriacao": "2025-09-01T08:00:00",
                                                                  "dataAtualizacao": "2025-11-15T14:05:00",
                                                                  "agendamentos": []
                                                                }
                                                                """))))
                                .addApiResponse("404", new ApiResponse().description("Cliente não encontrado"))))
                .delete(new Operation()
                        .tags(List.of("Gestão de Clientes - Barbearia (LGPD)"))
                        .summary("Anonimizar cliente (LGPD)")
                        .description("Anonimiza dados pessoais do cliente preservando histórico de agendamentos. Operação irreversível!")
                        .security(List.of(new SecurityRequirement().addList("Bearer")))
                        .addParametersItem(new io.swagger.v3.oas.models.parameters.Parameter()
                                .name("id")
                                .in("path")
                                .required(true)
                                .schema(new NumberSchema().format("int64")))
                        .responses(new ApiResponses()
                                .addApiResponse("204", new ApiResponse().description("Cliente anonimizado com sucesso"))
                                .addApiResponse("403", new ApiResponse().description("Cliente já anonimizado"))
                                .addApiResponse("404", new ApiResponse().description("Cliente não encontrado"))));
    }
}
