# PostgreSQL 18 com Docker — Guia de Inicialização

Este guia mostra como criar, iniciar, parar e diagnosticar um container PostgreSQL 18 usando Docker.

Também documenta o erro encontrado ao usar o PostgreSQL 18 com o volume montado no diretório antigo `/var/lib/postgresql/data`.

---

## 1. Pré-requisitos

Verifique se o Docker está instalado:

```bash
docker --version
```

Verifique se o Docker está funcionando:

```bash
docker ps
```

---

## 2. Problema de autenticação ao baixar a imagem

Ao executar:

```bash
docker run ...
```

pode aparecer:

```text
Unable to find image 'postgres:18' locally
docker: Error response from daemon:
authentication required - personal access token is expired
```

Isso indica que a autenticação do Docker Hub está com um Personal Access Token (PAT) expirado.

### 2.1 Fazer logout

```bash
docker logout
```

Depois tente baixar a imagem:

```bash
docker pull postgres:18
```

### 2.2 Se for necessário autenticar novamente

```bash
docker login
```

Informe seu usuário do Docker Hub e um Personal Access Token válido.

Depois:

```bash
docker pull postgres:18
```

---

## 3. Criar o PostgreSQL 18

### Importante: PostgreSQL 18

A partir do PostgreSQL 18, a imagem oficial mudou a forma recomendada de montar o volume de dados.

Use:

```text
/var/lib/postgresql
```

e não:

```text
/var/lib/postgresql/data
```

### Comando correto

```bash
docker run --name postgres-url-shortener   -e POSTGRES_USER=postgres   -e POSTGRES_PASSWORD=postgres   -e POSTGRES_DB=url_shortener   -p 5432:5432   -v postgres-url-shortener-data:/var/lib/postgresql   -d postgres:18
```

### O que cada parâmetro faz

| Parâmetro | Função |
|---|---|
| `--name postgres-url-shortener` | Nome do container |
| `-e POSTGRES_USER=postgres` | Usuário inicial |
| `-e POSTGRES_PASSWORD=postgres` | Senha inicial |
| `-e POSTGRES_DB=url_shortener` | Banco criado automaticamente |
| `-p 5432:5432` | Expõe a porta do PostgreSQL |
| `-v postgres-url-shortener-data:/var/lib/postgresql` | Persiste os dados |
| `-d` | Executa em segundo plano |
| `postgres:18` | Imagem PostgreSQL 18 |

---

## 4. Verificar se o container está rodando

```bash
docker ps
```

O resultado deverá mostrar algo parecido com:

```text
CONTAINER ID   IMAGE         STATUS         PORTS                    NAMES
xxxxxxxxxxxx   postgres:18   Up 10 seconds  0.0.0.0:5432->5432/tcp   postgres-url-shortener
```

---

## 5. Ver containers parados

Se `docker ps` não mostrar o PostgreSQL, use:

```bash
docker ps -a
```

Exemplo:

```text
CONTAINER ID   IMAGE         STATUS         PORTS     NAMES
xxxxxxxxxxxx   postgres:18   Exited (1)              postgres-url-shortener
```

Isso significa que o container existe, mas foi encerrado.

---

## 6. Consultar os logs

Quando um container encerra inesperadamente, o primeiro passo é consultar os logs:

```bash
docker logs postgres-url-shortener
```

Para acompanhar os logs em tempo real:

```bash
docker logs -f postgres-url-shortener
```

Pressione `Ctrl+C` para sair.

---

## 7. Erro específico do PostgreSQL 18

Um dos erros encontrados foi:

```text
Error: in 18+, these Docker images are configured to store database data in a
format which is compatible with "pg_ctlcluster" ...

Counter to that, there appears to be PostgreSQL data in:

/var/lib/postgresql/data (unused mount/volume)
```

### Causa

O volume foi montado assim:

```bash
-v postgres-url-shortener-data:/var/lib/postgresql/data
```

Essa configuração não é a recomendada para a imagem oficial do PostgreSQL 18.

No PostgreSQL 18, monte o volume em:

```bash
-v postgres-url-shortener-data:/var/lib/postgresql
```

---

## 8. Corrigir um container criado com o volume errado

Se o container ainda não possui dados importantes, faça uma recriação limpa.

### 8.1 Remover o container

```bash
docker rm postgres-url-shortener
```

Se ele ainda estiver executando:

```bash
docker rm -f postgres-url-shortener
```

### 8.2 Remover o volume antigo

```bash
docker volume rm postgres-url-shortener-data
```

> Atenção: remover um volume apaga os dados armazenados nele. Só faça isso quando tiver certeza de que não há dados importantes.

### 8.3 Criar novamente

```bash
docker run --name postgres-url-shortener   -e POSTGRES_USER=postgres   -e POSTGRES_PASSWORD=postgres   -e POSTGRES_DB=url_shortener   -p 5432:5432   -v postgres-url-shortener-data:/var/lib/postgresql   -d postgres:18
```

### 8.4 Confirmar

```bash
docker ps
```

---

## 9. Entrar no PostgreSQL

Use:

```bash
docker exec -it postgres-url-shortener   psql -U postgres -d url_shortener
```

Você deverá entrar em um terminal semelhante a:

```text
url_shortener=#
```

---

## 10. Testar o banco

Dentro do `psql`:

```sql
SELECT version();
```

Para listar os bancos:

```sql
\l
```

Para listar as tabelas:

```sql
\dt
```

Para sair:

```sql
\q
```

---

## 11. Parar o PostgreSQL

Para parar o container:

```bash
docker stop postgres-url-shortener
```

O volume não é removido.

---

## 12. Iniciar novamente

Depois de parado:

```bash
docker start postgres-url-shortener
```

Verifique:

```bash
docker ps
```

---

## 13. Reiniciar o PostgreSQL

```bash
docker restart postgres-url-shortener
```

---

## 14. Verificar o volume

Liste os volumes:

```bash
docker volume ls
```

Você deverá encontrar:

```text
postgres-url-shortener-data
```

Para obter informações sobre o volume:

```bash
docker volume inspect postgres-url-shortener-data
```

---

## 15. Verificar o estado do container

Para consultar rapidamente o estado:

```bash
docker inspect postgres-url-shortener   --format='{{.State.Status}} - {{.State.ExitCode}}'
```

Exemplo quando está funcionando:

```text
running - 0
```

Exemplo quando encerrou com erro:

```text
exited - 1
```

---

## 16. Conexão com aplicações

Como a porta `5432` do container foi publicada para a máquina:

```text
localhost:5432
```

Uma aplicação Spring Boot pode utilizar:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/url_shortener
spring.datasource.username=postgres
spring.datasource.password=postgres
```

---

## 17. Fluxo recomendado para o dia a dia

Normalmente você não precisa recriar o container.

### Primeira vez

```bash
docker run --name postgres-url-shortener   -e POSTGRES_USER=postgres   -e POSTGRES_PASSWORD=postgres   -e POSTGRES_DB=url_shortener   -p 5432:5432   -v postgres-url-shortener-data:/var/lib/postgresql   -d postgres:18
```

### Nos próximos dias

Verifique:

```bash
docker ps
```

Se estiver parado:

```bash
docker start postgres-url-shortener
```

Se estiver rodando, não precisa fazer nada.

### Para finalizar o ambiente

```bash
docker stop postgres-url-shortener
```

Os dados continuam preservados no volume.

---

## 18. Comandos de diagnóstico

Quando algo não funcionar, siga esta ordem:

### 1. O container existe?

```bash
docker ps -a
```

### 2. O container está rodando?

```bash
docker ps
```

### 3. Por que ele encerrou?

```bash
docker logs postgres-url-shortener
```

### 4. Qual é o estado?

```bash
docker inspect postgres-url-shortener   --format='{{.State.Status}} - {{.State.ExitCode}}'
```

### 5. O volume existe?

```bash
docker volume ls
```

---

## 19. Configuração final deste projeto

A configuração utilizada no projeto de estudo é:

```text
Container:
postgres-url-shortener

Imagem:
postgres:18

Database:
url_shortener

User:
postgres

Password:
postgres

Host:
localhost

Porta:
5432

Volume:
postgres-url-shortener-data

Mount:
 /var/lib/postgresql
```

Arquitetura simplificada:

```text
Aplicação
    |
    | localhost:5432
    v
Docker
    |
    v
postgres-url-shortener
    |
    v
PostgreSQL 18
    |
    v
url_shortener

Volume:
postgres-url-shortener-data
```

---

## 20. Resumo dos comandos principais

```bash
# Ver containers rodando
docker ps

# Ver todos os containers
docker ps -a

# Iniciar
docker start postgres-url-shortener

# Parar
docker stop postgres-url-shortener

# Reiniciar
docker restart postgres-url-shortener

# Logs
docker logs postgres-url-shortener

# Entrar no PostgreSQL
docker exec -it postgres-url-shortener psql -U postgres -d url_shortener

# Ver volumes
docker volume ls
```

**Regra principal para este ambiente PostgreSQL 18:** monte o volume em `/var/lib/postgresql`, e não em `/var/lib/postgresql/data`.
