# Encurtador de URLs

Aplicação REST em Java com Spring Boot para encurtar URLs longas, persistir os dados em PostgreSQL e redirecionar para a URL original com cache em Redis.

## Visão geral

A API oferece dois fluxos principais:

- `POST /url`: recebe uma URL válida e retorna um código curto associado;
- `GET /{shortCode}`: busca a URL original e retorna um redirecionamento HTTP 302.

Também há suporte para:

- reutilização de URL já cadastrada;
- tratamento padronizado de exceções;
- logging com `traceId` por requisição;
- cache de consultas em Redis;
- documentação OpenAPI via Swagger UI.

## Stack tecnológica

- Java 17
- Spring Boot 4.1.0
- Spring Web
- Spring Validation
- Spring Cache
- Spring Data Redis
- Spring JDBC
- PostgreSQL
- Redis
- Springdoc OpenAPI
- Maven Wrapper
- JUnit 5 + Mockito

## Requisitos

- JDK 17+
- Docker para subir PostgreSQL e Redis localmente
- Portas livres:
  - `5432` para PostgreSQL
  - `6379` para Redis
  - `8080` para a aplicação

## Configuração local

### PostgreSQL

A aplicação usa a seguinte configuração padrão:

- host: `localhost`
- porta: `5432`
- banco: `url_shortener`
- usuário: `postgres`
- senha: `postgres`

Iniciando via Docker:

```bash
docker run -d \
  --name postgres-url-shortener \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_PASSWORD=postgres \
  -e POSTGRES_DB=url_shortener \
  -p 5432:5432 \
  -v postgres-url-shortener-data:/var/lib/postgresql/data \
  postgres:16-alpine
```

Crie a tabela esperada pela aplicação antes de iniciar o projeto:

```sql
CREATE TABLE IF NOT EXISTS urls (
    id BIGSERIAL PRIMARY KEY,
    short_code VARCHAR(32) UNIQUE,
    original_url TEXT NOT NULL UNIQUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
```

O guia adicional de operação do PostgreSQL está em [doc/postgresql_docker_guia.md](doc/postgresql_docker_guia.md).

### Redis

```bash
docker run -d \
  --name redis-url-shortener \
  -p 6379:6379 \
  redis:7-alpine
```

A aplicação está configurada para conectar em `localhost:6379` e usa cache com TTL de 5 minutos para as consultas de redirecionamento.

## Executando a aplicação

Clone e entre na pasta do projeto:

```bash
git clone <URL_DO_REPOSITORIO>
cd encurtador-url
```

Inicie a aplicação com o Maven Wrapper:

```bash
./mvnw spring-boot:run
```

No Windows:

```bat
mvnw.cmd spring-boot:run
```

A API ficará disponível em:

- `http://localhost:8080`
- Swagger UI: `http://localhost:8080/swagger-ui/index.html`
- Health check: `http://localhost:8080/actuator/health`

### Base URL da aplicação

A URL curta gerada é montada a partir da propriedade `app.shortener.base-url`, cujo valor padrão é:

```yaml
app:
  shortener:
    base-url: http://localhost:8080/
```

Você pode sobrescrever esse valor no ambiente:

```bash
APP_SHORTENER_BASE_URL=https://exemplo.com/ ./mvnw spring-boot:run
```

O valor deve terminar com `/` para concatenar corretamente o código curto.

## Endpoints da API

### 1) Criar uma URL curta

Endpoint:

```http
POST /url
```

Exemplo:

```bash
curl -i -X POST http://localhost:8080/url \
  -H 'Content-Type: application/json' \
  -H 'traceId: req-12345' \
  -d '{"url":"https://www.exemplo.com/artigo/com-uma-url-muito-longa"}'
```

Resposta esperada (`201 Created`):

```json
{
  "shortCode": "req-12345",
  "shortUrl": "http://localhost:8080/req-12345"
}
```

> Se a URL informada já estiver cadastrada, a API retorna o código curto já existente.

O header `Location` também aponta para o endpoint curto gerado.

### 2) Redirecionar para a URL original

Endpoint:

```http
GET /{shortCode}
```

Exemplo:

```bash
curl -i http://localhost:8080/req-12345
```

Resposta esperada:

- status: `302 Found`
- header `Location`: URL original

Para seguir o redirecionamento automaticamente:

```bash
curl -i -L http://localhost:8080/req-12345
```

### 3) Requisição inválida / URL não encontrada

Quando o código curto não existe, a API devolve `404` com um payload padronizado de erro. Exemplo:

```json
{
  "friendlyMessage": "URL não encontrada",
  "technicalMessage": "URL não encontrada para o código encurtado fornecido.",
  "errorCode": 404,
  "details": null,
  "traceId": null,
  "timestamp": "2026-08-23T12:00:00-03:00[America/Sao_Paulo]"
}
```

## Estrutura do projeto

```text
src/
├── main/
│   ├── java/
│   │   └── br/com/lucas/alves/encurtador_url/
│   │       ├── api/                 # Controllers, requests e responses
│   │       ├── application/         # Casos de uso e ports
│   │       ├── config/              # Configurações gerais da aplicação
│   │       ├── domain/              # Entidades e exceções de domínio
│   │       ├── handlers/            # Tratamento padronizado de erros
│   │       ├── infrastructure/      # Implementações de persistência
│   │       └── EncurtadorUrlApplication.java
│   └── resources/
│       └── application.yaml
├── test/
│   └── java/                       # Testes unitários do projeto
├── doc/
│   └── postgresql_docker_guia.md
├── pom.xml
├── mvnw / mvnw.cmd
├── README.md
└── target/
```

## Fluxo da aplicação

1. O cliente envia uma URL para `POST /url`.
2. A requisição recebe um `traceId` (gerado automaticamente pelo interceptor ou fornecido no header).
3. A aplicação tenta salvar a URL com esse valor como `short_code` no PostgreSQL.
4. Se a URL já existir, o código curto existente é reutilizado.
5. Em `GET /{shortCode}`, a aplicação consulta o Redis antes do banco.
6. Quando encontra o registro, responde com redirecionamento `302` para a URL original.

## Testes e build

Executar todos os testes:

```bash
./mvnw test
```

Gerar artefato executável:

```bash
./mvnw clean package
```

Executar o JAR gerado:

```bash
java -jar target/encurtador-url-0.0.1-SNAPSHOT.jar
```

## Observações importantes

- O projeto depende de PostgreSQL e Redis em execução antes do startup da API.
- O banco não realiza migrations automáticas; é necessário criar a tabela manualmente.
- O swagger oferece uma interface visual para testar os endpoints sem uso de ferramentas externas.
- A aplicação usa `traceId` como identificador curto da URL, e o filtro HTTP garante que ele exista em toda requisição que não seja health check.
