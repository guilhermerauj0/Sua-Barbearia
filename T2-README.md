# T2 - Registro de Cliente

## Implementação Completa

### Arquitetura Implementada

```
src/main/java/com/barbearia/
├── domain/entities/
│   ├── Usuario.java (classe abstrata base)
│   └── Cliente.java (estende Usuario)
├── application/
│   ├── dto/
│   │   ├── ClienteRequestDto.java (entrada da API)
│   │   └── ClienteResponseDto.java (saída da API, sem senha)
│   ├── services/
│   │   └── ClienteService.java (lógica de negócio)
│   └── factories/
│       └── UsuarioFactory.java (criação de objetos)
├── infrastructure/
│   ├── config/
│   │   └── SecurityConfig.java (configuração Spring Security)
│   └── persistence/
│       ├── entities/
│       │   └── JpaCliente.java (entidade JPA)
│       └── repositories/
│           └── ClienteRepository.java (acesso ao banco)
└── adapters/
    ├── controllers/
    │   └── AuthController.java (endpoint REST)
    └── mappers/
        └── ClienteMapper.java (conversões)
```

### Funcionalidades Implementadas

1. **Classe abstrata Usuario**
   - Atributos comuns: id, nome, email, senha, telefone, role, dataCriacao, dataAtualizacao
   - Construtor protegido para subclasses
   - Método atualizarDataModificacao()

2. **Classe Cliente**
   - Herda de Usuario
   - Role automático: "CLIENTE"
   - Atributo ativo (soft delete)
   - Lista de agendamentos (futuro)
   - Métodos ativar/desativar
   - equals/hashCode por email

3. **DTOs**
   - **ClienteRequestDto**: validações com Bean Validation
     - Nome: 3-100 caracteres
     - Email: formato válido
     - Senha: mínimo 8 caracteres, maiúscula, minúscula, número e especial
     - Telefone: formato (XX) XXXXX-XXXX
   - **ClienteResponseDto**: sem senha (segurança)

4. **Service**
   - Validação de senhas (senha == confirmarSenha)
   - Verificação de email único
   - Hash de senha com BCrypt
   - Uso de Factory para criar clientes
   - Transações com @Transactional

5. **Factory**
   - Criação consistente de objetos Cliente
   - Validações centralizadas
   - Normalização de dados (email minúsculo, telefone sem formatação)

6. **Entidade JPA**
   - Anotações JPA completas
   - Email único (índice)
   - PrePersist/PreUpdate para datas
   - Soft delete com campo ativo

7. **Repository**
   - Métodos de busca: findByEmail, findByTelefone
   - Verificações: existsByEmail, existsByTelefone
   - Busca de ativos: findByEmailAndAtivoTrue

8. **Controller**
   - POST /api/auth/cliente/registrar
   - GET /api/auth/cliente/email-disponivel
   - Validação automática com @Valid
   - Tratamento de exceções
   - Resposta 201 para sucesso, 400 para erro

### Como Testar

#### 1. Iniciar a aplicação

```bash
# Com Docker
docker-compose up -d

# Ou localmente
mvn spring-boot:run
```

#### 2. Testar registro de cliente

**Requisição de sucesso:**

```bash
curl -X POST http://localhost:8080/api/auth/cliente/registrar \
  -H "Content-Type: application/json" \
  -d '{
    "nome": "João Silva",
    "email": "joao@email.com",
    "senha": "Senha@123",
    "confirmarSenha": "Senha@123",
    "telefone": "(11) 98765-4321"
  }'
```

**Resposta esperada (201 Created):**

```json
{
  "id": 1,
  "nome": "João Silva",
  "email": "joao@email.com",
  "telefone": "11987654321",
  "role": "CLIENTE",
  "ativo": true,
  "dataCriacao": "2025-10-27T18:30:00"
}
```

**Teste de email duplicado:**

```bash
# Tentar registrar novamente com mesmo email
curl -X POST http://localhost:8080/api/auth/cliente/registrar \
  -H "Content-Type: application/json" \
  -d '{
    "nome": "Maria Silva",
    "email": "joao@email.com",
    "senha": "Senha@123",
    "confirmarSenha": "Senha@123",
    "telefone": "(11) 91234-5678"
  }'
```

**Resposta esperada (400 Bad Request):**

```
Email já cadastrado no sistema
```

**Teste de validações:**

```bash
# Senha fraca
curl -X POST http://localhost:8080/api/auth/cliente/registrar \
  -H "Content-Type: application/json" \
  -d '{
    "nome": "Pedro Santos",
    "email": "pedro@email.com",
    "senha": "123456",
    "confirmarSenha": "123456",
    "telefone": "(11) 99999-8888"
  }'
```

**Resposta esperada (400 Bad Request):**

```json
{
  "senha": "Senha deve ter no mínimo 8 caracteres"
}
```

#### 3. Verificar email disponível

```bash
curl http://localhost:8080/api/auth/cliente/email-disponivel?email=novo@email.com
```

**Resposta:** `true` (disponível) ou `false` (já cadastrado)

### Validações Implementadas

- Email: formato válido (@Email) e unicidade (verificado no service)
- Senha: 
  - Mínimo 8 caracteres
  - Pelo menos 1 letra maiúscula
  - Pelo menos 1 letra minúscula
  - Pelo menos 1 número
  - Pelo menos 1 caractere especial (@$!%*?&#)
- Telefone: formato (XX) XXXXX-XXXX ou (XX) XXXX-XXXX
- Senha e confirmação devem ser iguais

### Checklist de Aceitação

- [x] Endpoint POST /api/auth/cliente/registrar implementado
- [x] Retorna 201 com cliente criado
- [x] Senha armazenada com hash BCrypt
- [x] Email duplicado retorna 400
- [x] Validações de DTO funcionando
- [x] Classe abstrata Usuario criada
- [x] Cliente estende Usuario
- [x] DTOs criados (Request e Response)
- [x] Service com validações implementado
- [x] Factory para criação de clientes
- [x] Entidade JPA com anotações
- [x] Repository com métodos de busca
- [x] Controller com endpoint
- [x] Mapper para conversões
- [x] Clean Architecture aplicada
- [x] Comentários em português
- [x] Código compilando sem erros

### Próximos Passos

1. Criar testes unitários para ClienteService
2. Criar testes de integração para AuthController
3. Implementar autenticação com JWT (T3)
4. Adicionar Swagger para documentação da API
