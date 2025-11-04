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
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.Operation;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(apiInfo())
                .servers(apiServers())
                .paths(new io.swagger.v3.oas.models.Paths()
                        .addPathItem("/api/auth/cliente/registrar", registrarClientePath())
                        .addPathItem("/api/auth/cliente/login", loginClientePath())
                        .addPathItem("/api/auth/barbearia/registrar", registrarBarbeariaPath())
                        .addPathItem("/api/auth/barbearia/login", loginBarbeariaPath()));
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
                .url("https://api.suabarbearia.com")
                .description("Servidor de Produção");
        
        return List.of(devServer, prodServer);
    }

    private PathItem registrarClientePath() {
        return new PathItem()
                .post(new Operation()
                        .tags(List.of("Clientes"))
                        .summary("Registrar novo cliente")
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
                        .summary("Login de cliente")
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
                        .summary("Registrar nova barbearia")
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
                  "nome": "Maria Santos",
                  "email": "maria.santos@email.com",
                  "senha": "SenhaForte@123",
                  "confirmarSenha": "SenhaForte@123",
                  "telefone": "(11) 98765-4321",
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
                  "senha": "SenhaForte@123",
                  "confirmarSenha": "SenhaForte@123",
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
                        .example(1))
                .addProperty("nome", new StringSchema()
                        .description("Nome do proprietário")
                        .example("Maria Santos"))
                .addProperty("email", new StringSchema()
                        .description("Email da barbearia")
                        .example("maria.santos@email.com"))
                .addProperty("telefone", new StringSchema()
                        .description("Telefone de contato (apenas números)")
                        .example("11987654321"))
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
                        .example("2025-11-03T12:59:22.680365557"));
    }

    private Object barbeariaResponseExample() {
        return """
                {
                  "id": 1,
                  "nome": "Maria Santos",
                  "email": "maria.santos@email.com",
                  "telefone": "11987654321",
                  "nomeFantasia": "Barbearia Elegance",
                  "tipoDocumento": "CPF",
                  "documento": "12345678909",
                  "endereco": "Rua das Flores, 123 - São Paulo/SP",
                  "role": "BARBEARIA",
                  "ativo": true,
                  "dataCriacao": "2025-11-03T12:59:22.680365557"
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
                  "email": "joao.silva@email.com",
                  "senha": "SenhaForte@123"
                }
                """;
    }

    private Schema<?> loginResponseSchema() {
        return new Schema<>()
                .type("object")
                .description("Resposta do login contendo token JWT e dados do usuário")
                .addProperty("token", new StringSchema()
                        .description("Token JWT para autenticação")
                        .example("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJqb2FvLnNpbHZhQGVtYWlsLmNvbSIsInVzZXJJZCI6MSwiYmFyYmVpYSI6ZmFsc2UsIm5vbWUiOiJKb8OjbyBTaWx2YSIsInJvbGUiOiJDTElFTlRFIiwiaWF0IjoxNzMwNDk5NjAwLCJleHAiOjE3MzA1MDMyMDB9.abc123xyz"))
                .addProperty("tipo", new StringSchema()
                        .description("Tipo do token")
                        .example("Bearer"))
                .addProperty("userId", new IntegerSchema()
                        .description("ID do usuário autenticado")
                        .format("int64")
                        .example(1))
                .addProperty("nome", new StringSchema()
                        .description("Nome do usuário")
                        .example("João Silva"))
                .addProperty("email", new StringSchema()
                        .description("Email do usuário")
                        .example("joao.silva@email.com"))
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
                  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJqb2FvLnNpbHZhQGVtYWlsLmNvbSIsInVzZXJJZCI6MSwiYmFyYmVpYSI6ZmFsc2UsIm5vbWUiOiJKb8OjbyBTaWx2YSIsInJvbGUiOiJDTElFTlRFIiwiaWF0IjoxNzMwNDk5NjAwLCJleHAiOjE3MzA1MDMyMDB9.abc123xyz",
                  "tipo": "Bearer",
                  "userId": 1,
                  "nome": "João Silva",
                  "email": "joao.silva@email.com",
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
                  "nome": "João Silva",
                  "email": "joao.silva@email.com",
                  "senha": "SenhaForte@123",
                  "confirmarSenha": "SenhaForte@123",
                  "telefone": "(11) 98765-4321"
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
                        .example(1))
                .addProperty("nome", new StringSchema()
                        .description("Nome completo do cliente")
                        .example("João Silva"))
                .addProperty("email", new StringSchema()
                        .description("Email do cliente")
                        .example("joao.silva@email.com"))
                .addProperty("telefone", new StringSchema()
                        .description("Telefone do cliente")
                        .example("11987654321"))
                .addProperty("role", new StringSchema()
                        .description("Papel do usuário no sistema")
                        .example("CLIENTE"))
                .addProperty("ativo", new BooleanSchema()
                        .description("Status de ativação do cliente")
                        .example(true))
                .addProperty("dataCriacao", new StringSchema()
                        .description("Data e hora do registro")
                        .format("date-time")
                        .example("2025-10-29T14:30:00"));
    }

    private Object clienteResponseExample() {
        return """
                {
                  "id": 1,
                  "nome": "João Silva",
                  "email": "joao.silva@email.com",
                  "telefone": "11987654321",
                  "role": "CLIENTE",
                  "ativo": true,
                  "dataCriacao": "2025-10-29T14:30:00"
                }
                """;
    }

    private PathItem loginBarbeariaPath() {
        return new PathItem()
                .post(new Operation()
                        .tags(List.of("Barbearias"))
                        .summary("Login de barbearia")
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
}
