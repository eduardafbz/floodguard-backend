# FloodGuard Backend

Sistema de monitoramento e alerta de risco de alagamentos, desenvolvido em Spring Boot. O backend recebe dados de chuva e ocorrências relatadas, calcula automaticamente o nível de risco de cada região monitorada e gera alertas quando o risco se torna relevante.

## 📌 Sobre o projeto

O FloodGuard nasceu da necessidade de monitorar regiões urbanas suscetíveis a alagamentos, cruzando dois tipos de informação:

- **Volume de chuva** (mm/h) informado para uma região
- **Ocorrências reportadas** por moradores nas últimas horas

Com base nesses dois fatores, o sistema classifica automaticamente o risco em 4 níveis e dispara um alerta quando necessário — sem intervenção manual.

## 🏗️ Arquitetura

O projeto segue a arquitetura em camadas padrão do Spring Boot:

```
Controller  →  Service  →  Repository  →  Banco de Dados
```

- **Controller**: expõe os endpoints REST e trata requisições HTTP
- **Service**: contém as regras de negócio (cálculo de risco, criação de alertas)
- **Repository**: interfaces do Spring Data JPA para acesso ao banco
- **Model**: entidades JPA mapeadas para as tabelas do PostgreSQL
- **DTO**: objetos de transferência usados para validar dados de entrada e moldar respostas
- **Exception**: exceções customizadas com status HTTP apropriado

### Estrutura de pastas

```
src/main/java/com/floodguard/
├── controller/
│   ├── AlertaController.java
│   ├── OcorrenciaController.java
│   └── RegiaoController.java
├── dto/
│   ├── OcorrenciaRequest.java
│   └── RiscoResponse.java
├── exception/
│   └── RecursoNaoEncontradoException.java
├── model/
│   ├── Alerta.java
│   ├── NivelRisco.java
│   ├── Ocorrencia.java
│   └── Regiao.java
├── repository/
│   ├── AlertaRepository.java
│   ├── OcorrenciaRepository.java
│   └── RegiaoRepository.java
├── service/
│   ├── AlertaService.java
│   ├── OcorrenciaService.java
│   └── RiscoService.java
└── FloodGuardApplication.java
```

## 🧠 Regra de negócio: cálculo de risco

O `RiscoService` classifica o nível de risco cruzando `chuvaPorHora` (mm) com o número de ocorrências registradas nas **últimas 2 horas**:

| Nível | Condição |
|---|---|
| `EMERGENCIA` | chuva ≥ 50mm/h **e** ≥ 3 ocorrências |
| `RISCO` | chuva ≥ 30mm/h **ou** ≥ 3 ocorrências |
| `ATENCAO` | chuva ≥ 15mm/h **ou** ≥ 1 ocorrência |
| `NORMAL` | nenhuma das condições acima |

Sempre que uma região deixa de estar em `NORMAL`, um **alerta é criado automaticamente**, contendo título, mensagem, nível e coordenadas da região.

## 🔌 Endpoints da API

### Regiões

| Método | Rota | Descrição |
|---|---|---|
| `GET` | `/api/regioes` | Lista todas as regiões cadastradas |
| `POST` | `/api/regioes` | Cadastra uma nova região |
| `PUT` | `/api/regioes/{id}/risco?chuvaPorHora=X` | Recalcula o risco da região e gera alerta se necessário |

**Exemplo — cadastrar região:**
```bash
curl -X POST http://localhost:8080/api/regioes \
  -H "Content-Type: application/json" \
  -d '{"nome":"Vila Prudente","latitude":-23.58,"longitude":-46.58}'
```

**Exemplo — atualizar risco:**
```bash
curl -X PUT "http://localhost:8080/api/regioes/1/risco?chuvaPorHora=40"
```

### Ocorrências

| Método | Rota | Descrição |
|---|---|---|
| `GET` | `/api/ocorrencias` | Lista todas as ocorrências |
| `POST` | `/api/ocorrencias` | Registra uma nova ocorrência de alagamento |

**Exemplo:**
```bash
curl -X POST http://localhost:8080/api/ocorrencias \
  -H "Content-Type: application/json" \
  -d '{"descricao":"Alagamento na Rua X","latitude":-23.58,"longitude":-46.58}'
```

### Alertas

| Método | Rota | Descrição |
|---|---|---|
| `GET` | `/api/alertas` | Lista todos os alertas gerados automaticamente |

## 🗄️ Modelo de dados

**Regiao**
- `id`, `nome`, `latitude`, `longitude`, `nivelRisco`, `chuvaPorHora`

**Ocorrencia**
- `id`, `descricao`, `latitude`, `longitude`, `dataHora`

**Alerta**
- `id`, `titulo`, `mensagem`, `nivelRisco`, `dataHora`, `latitude`, `longitude`

## ⚙️ Tecnologias

- Java 21
- Spring Boot 3.5.5
- Spring Data JPA / Hibernate
- PostgreSQL
- Bean Validation (`jakarta.validation`)
- Maven

## 🚀 Como rodar o projeto

### Pré-requisitos
- Java 21+
- Maven
- PostgreSQL rodando na porta padrão (5432)

### 1. Suba o banco de dados

**Com Docker:**
```bash
docker run --name floodguard-db \
  -e POSTGRES_PASSWORD=postgres \
  -e POSTGRES_DB=floodguard \
  -p 5432:5432 -d postgres
```

**Ou localmente com PostgreSQL instalado:**
```bash
createdb floodguard
```

### 2. Configure as credenciais (opcional)

Por padrão, o `application.properties` usa `postgres/postgres`. Para customizar, defina variáveis de ambiente:

```bash
export DB_USER=seu_usuario
export DB_PASSWORD=sua_senha
```

### 3. Rode a aplicação

```bash
mvn clean install
mvn spring-boot:run
```

A API sobe em `http://localhost:8080`. As tabelas são criadas automaticamente pelo Hibernate (`ddl-auto=update`).

### 4. Teste os endpoints

Use `curl`, Postman ou Insomnia para testar as rotas listadas acima. Fluxo sugerido:

1. Cadastre uma região (`POST /api/regioes`)
2. Registre uma ou mais ocorrências próximas (`POST /api/ocorrencias`)
3. Atualize o risco da região informando a chuva (`PUT /api/regioes/{id}/risco?chuvaPorHora=X`)
4. Confira se um alerta foi gerado (`GET /api/alertas`)

## 📋 Próximos passos

- [ ] Integração com API de clima externa para automatizar `chuvaPorHora`
- [ ] Notificações push (Firebase) para o alerta chegar em tempo real no mobile
- [ ] Autenticação e autorização
- [ ] Endpoints de detalhe (`GET /{id}`) e exclusão
- [ ] Testes automatizados (unitários e de integração)
- [ ] Documentação interativa da API (Swagger/OpenAPI)

## 👥 Equipe

Maria Eduarda de Faria Braz
Maiara
Mariana
Vinicius