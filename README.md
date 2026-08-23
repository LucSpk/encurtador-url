# Encurtador de URLs

API REST para transformar URLs longas em links curtos e redirecioná-los posteriormente. O projeto foi desenvolvido com Spring Boot, utiliza PostgreSQL como armazenamento persistente e Redis para cache das consultas de redirecionamento.

## Funcionalidades

- Criação de URLs curtas a partir de uma URL original.
- Reutilização do mesmo código curto quando a URL original já estiver cadastrada.
- Redirecionamento HTTP para a URL original por meio do código curto.
- Geração determinística de códigos em Base62 a partir do identificador persistido.
- Cache em Redis para reduzir consultas ao PostgreSQL durante os redirecionamentos.
- Respostas de erro estruturadas para códigos curtos inexistentes e falhas de persistência.

## Tecnologias

- Java 17
- Spring Boot 4.1.0
- Spring Web
- Spring Cache
- Spring Data Redis
- PostgreSQL 18
- Maven Wrapper
- JUnit 5 e Spring Boot Test

## Pré-requisitos

- JDK 17 ou superior
- Docker, para executar PostgreSQL e Redis localmente
- Porta `5432` livre para PostgreSQL
- Porta `6379` livre para Redis
- Porta `8080` livre para a aplicação

## Configuração da infraestrutura

### PostgreSQL

O projeto espera uma conexão PostgreSQL com os seguintes dados locais:

| Propriedade | Valor esperado |
|---|---|
| Host | `localhost` |
| Porta | `5432` |
| Banco | `url_shortener` |
| Usuário | `postgres` |
| Senha | `postgres` |

Para criar o banco rapidamente usando a imagem oficial do PostgreSQL 18:

```bash
docker run --name postgres-url-shortener
   -e POSTGRES_USER=postgres
   -e POSTGRES_PASSWORD=postgres
   -e POSTGRES_DB=url_shortener   
   -p 5432:5432   
   -v postgres-url-shortener-data:/var/lib/postgresql   
   -d postgres:18
```

A aplicação não possui migration automática. Crie a tabela esperada pelo repositório antes de iniciar a API:

```SQL
CREATE TABLE IF NOT EXISTS urls (
    id BIGSERIAL PRIMARY KEY,
    short_code VARCHAR(32) UNIQUE,
    original_url TEXT NOT NULL UNIQUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
```

O guia completo de operação, diagnóstico e persistência do PostgreSQL está em [doc/postgresql_docker_guia.md](doc/postgresql_docker_guia.md).

### Redis

Inicie o Redis localmente com:

```bash
docker run -d \
  --name redis \
  -p 6379:6379 \
  redis:latest
```

A configuração padrão usa `localhost:6379`. O cache `urls` possui TTL de 5 minutos e não armazena valores nulos.

## Executando o projeto

Clone o repositório e entre no diretório do projeto:

```bash
git clone <URL_DO_REPOSITORIO>
cd encurtador-url
```

Com PostgreSQL e Redis em execução, inicie a aplicação:

```bash
./mvnw spring-boot:run
```

No Windows, utilize:

```bat
mvnw.cmd spring-boot:run
```

A API ficará disponível em `http://localhost:8080`.

### Configuração da URL base

A URL retornada no campo `shortUrl` é construída a partir de `app.shortener.base-url`. Por padrão, ela é `http://localhost:8080/`. Para alterar esse valor:

```bash
APP_SHORTENER_BASE_URL=https://exemplo.com/ ./mvnw spring-boot:run
```

Garanta que o valor termine com `/` para que o código curto seja concatenado corretamente.

## API

### Criar uma URL curta

`POST /url`

Requisição:

```bash
curl -i -X POST http://localhost:8080/url \
  -H 'Content-Type: application/json' \
  -d '{"url":"https://www.exemplo.com/artigo/com-uma-url-longa"}'
```

Resposta `201 Created`:

```json
{
  "shortCode": "1",
  "shortUrl": "http://localhost:8080/1"
}
```

O header `Location` também aponta para a URL curta criada. Se a URL original já existir, a API retorna o código curto previamente associado a ela.

### Redirecionar para a URL original

`GET /{shortCode}`

```bash
curl -i http://localhost:8080/1
```

Resposta esperada: `302 Found`, com o header `Location` contendo a URL original.

Para acompanhar o redirecionamento diretamente pelo terminal:

```bash
curl -i -L http://localhost:8080/1
```

### URL não encontrada

Quando o código curto não existe, a API retorna `404 Not Found`:

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

## Testes e build

Executar os testes:

```bash
./mvnw test
```

Gerar o artefato executável:

```bash
./mvnw clean package
```

O JAR será criado em `target/encurtador-url-0.0.1-SNAPSHOT.jar`.

Executar o JAR:

```bash
java -jar target/encurtador-url-0.0.1-SNAPSHOT.jar
```

O teste atual valida o carregamento do contexto Spring. Os testes de integração que iniciam a aplicação precisam de PostgreSQL e Redis disponíveis.

## Estrutura do projeto

```text
src/
├── main/
│   ├── java/.../application/     # DTOs e serviços de aplicação
│   ├── java/.../config/          # Configuração de conexão com PostgreSQL
│   ├── java/.../controller/      # Endpoints de criação e redirecionamento
│   ├── java/.../domain/          # Entidades de domínio
│   ├── java/.../handlers/        # Tratamento padronizado de exceções
│   ├── java/.../repository/      # Persistência via JDBC
│   └── resources/application.yaml
└── test/                          # Testes automatizados
```

## Fluxo da aplicação

1. O cliente envia uma URL original para `POST /url`.
2. A URL é persistida no PostgreSQL e recebe um identificador sequencial.
3. O identificador é convertido para Base62 e salvo como `short_code`.
4. A API devolve o código curto e a URL pública correspondente.
5. Em `GET /{shortCode}`, o Redis é consultado antes do PostgreSQL.
6. A aplicação responde com `302 Found` e o destino original no header `Location`.

## Configurações atuais e próximos aprimoramentos

- As credenciais e a URL JDBC do PostgreSQL estão definidas diretamente em `PostgreSqlConfig`; em ambientes compartilhados ou de produção, recomenda-se externalizá-las para variáveis de ambiente ou configuração segura.
- A criação da tabela ainda é manual; uma ferramenta de migration, como Flyway ou Liquibase, pode tornar a implantação reproduzível.
- A validação do formato da URL e a documentação OpenAPI podem ser adicionadas para fortalecer o contrato público da API.

## Licença

Este projeto ainda não declara uma licença de distribuição.
