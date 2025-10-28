# 🚀 Como Rodar o Projeto - Sua Barbearia

## 📋 Pré-requisitos

- Docker e Docker Compose instalados
- Java 17+ (apenas para desenvolvimento local sem Docker)
- Maven 3.8+ (apenas para desenvolvimento local sem Docker)

## ⚙️ Configuração Inicial

### 1. Configurar Variáveis de Ambiente

Copie o arquivo de exemplo e configure suas variáveis:

```bash
cp .env.example .env
```

Edite o arquivo `.env` com suas credenciais:

```env
DB_NAME=barbearia_db
DB_USER=seu_usuario
DB_PASSWORD=sua_senha_segura
JWT_SECRET=sua-chave-secreta-256-bits
JWT_EXPIRATION=3600000
```

**⚠️ IMPORTANTE:** Nunca commite o arquivo `.env` para o Git! Ele contém informações sensíveis.

### 2. Rodar com Docker Compose (Recomendado)

Este comando inicia tanto o banco PostgreSQL quanto a aplicação:

```bash
docker-compose up -d
```

Verificar os logs:

```bash
# Logs de todos os serviços
docker-compose logs -f

# Logs apenas da aplicação
docker-compose logs -f app

# Logs apenas do banco
docker-compose logs -f postgres
```

### 3. Verificar se está funcionando

A aplicação estará disponível em: `http://localhost:8080`

Teste com:

```bash
curl http://localhost:8080/
```

### 4. Parar os containers

```bash
docker-compose down
```

Para parar e remover os volumes (⚠️ isso apaga os dados do banco):

```bash
docker-compose down -v
```

## 🔧 Desenvolvimento Local (sem Docker)

### 1. Iniciar apenas o PostgreSQL com Docker

```bash
docker-compose up -d postgres
```

### 2. Compilar e rodar a aplicação localmente

```bash
# Compilar
mvn clean install

# Rodar
mvn spring-boot:run
```

Ou usando o JAR:

```bash
java -jar target/Sua-Barbearia-0.0.1-SNAPSHOT.jar
```

## 📊 Acessar o Banco de Dados

Você pode conectar ao PostgreSQL usando qualquer cliente (DBeaver, pgAdmin, etc):

```
Host: localhost
Port: 5432
Database: barbearia_db (ou o nome configurado no .env)
Username: barbearia_user (ou o nome configurado no .env)
Password: (senha configurada no .env)
```

Ou via linha de comando:

```bash
docker exec -it sua-barbearia-db psql -U barbearia_user -d barbearia_db
```

## 🔍 Troubleshooting

### Porta já está em uso

Se a porta 8080 ou 5432 já estiver em uso:

```bash
# Verificar o que está usando a porta
sudo lsof -i :8080
sudo lsof -i :5432

# Parar o processo ou alterar a porta no .env
```

### Erro de conexão com banco

Aguarde alguns segundos para o PostgreSQL inicializar completamente. O healthcheck garante que a aplicação só inicia quando o banco estiver pronto.

### Reconstruir a imagem Docker

Se você fez alterações no código:

```bash
docker-compose up -d --build
```

## 📁 Estrutura de Arquivos Criados

```
.
├── docker-compose.yml      # Orquestração Docker
├── Dockerfile              # Imagem da aplicação
├── .env                    # Variáveis sensíveis (NÃO COMMITAR)
├── .env.example            # Template das variáveis
├── .gitignore              # Arquivos ignorados pelo Git
├── pom.xml                 # Dependências Maven
└── src/main/resources/
    └── application.properties  # Configurações Spring Boot
```

## ✅ Checklist de Aceitação - T1

- [x] Docker inicializa corretamente
- [x] Aplicação conecta ao banco
- [x] Variáveis sensíveis não estão hardcoded
- [x] Arquivo .env está no .gitignore
- [x] Placeholders ${} usados em application.properties
- [x] PostgreSQL configurado com healthcheck
- [x] docker-compose.yml funcional

## 🎯 Próximos Passos

Após validar que tudo está funcionando:

1. Fazer commit das alterações
2. Criar Pull Request para a branch principal
3. Prosseguir para a próxima task (T2 - Autenticação e Autorização)
