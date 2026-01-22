# Sistema de Controle de Gastos 

Sistema backend profissional desenvolvido em **Java 21** com **Spring Boot 3** para controle de gastos pessoais com autenticação JWT, paginação, filtros avançados, dashboard e relatórios em PDF.

## 🚀 Tecnologias

- Java 21
- Spring Boot 3.2.1
- Spring Security + JWT
- Spring Data JPA (com Specifications para filtros dinâmicos)
- MySQL 8+
- MapStruct (mapeamento DTO ↔ Entity)
- iText PDF (geração de relatórios)
- SpringDoc OpenAPI/Swagger
- JUnit 5 + Mockito (testes)
- H2 Database (testes)

## ✨ Funcionalidades Implementadas

### Autenticação e Autorização
- ✅ Registro de usuários com senha criptografada (BCrypt)
- ✅ Login com geração de token JWT
- ✅ Proteção de endpoints por roles (USER, ADMIN)
- ✅ Filtro JWT para validação de tokens

### CRUD Completo
- ✅ Categorias (apenas ADMIN pode criar/editar/deletar)
- ✅ Gastos (cada usuário vê apenas seus próprios gastos)

### Paginação e Ordenação
- ✅ Todos os endpoints de listagem suportam paginação
- ✅ Ordenação configurável (por data, valor, descrição, etc)

### Filtros Avançados
- ✅ Filtro por descrição (busca parcial)
- ✅ Filtro por categoria
- ✅ Filtro por período (data início e fim)
- ✅ Filtro por faixa de valor (mínimo e máximo)
- ✅ Combinação de múltiplos filtros

### Dashboard com Estatísticas
- ✅ Total de gastos do mês
- ✅ Comparação com mês anterior
- ✅ Quantidade de gastos
- ✅ Ticket médio
- ✅ Gastos agrupados por categoria com percentuais
- ✅ Maiores gastos do período
- ✅ Gastos por dia (para gráficos)

### Relatórios em PDF
- ✅ Geração de relatórios PDF personalizados
- ✅ Filtro por período
- ✅ Agrupamento por categoria
- ✅ Totalizadores e gráficos

### Documentação da API
- ✅ Swagger/OpenAPI integrado
- ✅ Documentação interativa em `/swagger-ui.html`

### Testes Automatizados
- ✅ Testes unitários de Services
- ✅ Testes de integração de Controllers
- ✅ Testes de segurança

## 📋 Pré-requisitos

- JDK 21
- Maven 3.8+
- MySQL 8.0+

## 🔧 Configuração

### 1. Banco de Dados

```sql
CREATE DATABASE controle_gastos;
```

### 2. Configurar Credenciais

Edite `src/main/resources/application.properties`:

```properties
spring.datasource.username=SEU_USUARIO
spring.datasource.password=SUA_SENHA
jwt.secret=SUA_CHAVE_SECRETA_AQUI
```

### 3. Compilar

```bash
mvn clean install
```

### 4. Executar

```bash
mvn spring-boot:run
```

Aplicação disponível em: `http://localhost:8080`

Swagger UI: `http://localhost:8080/swagger-ui.html`

## 📚 API Endpoints

### Autenticação

```http
POST /api/auth/register
POST /api/auth/login
```

### Categorias (ADMIN apenas para POST/PUT/DELETE)

```http
GET    /api/categorias
GET    /api/categorias/{id}
POST   /api/categorias
PUT    /api/categorias/{id}
DELETE /api/categorias/{id}
```

### Gastos

```http
GET    /api/gastos
GET    /api/gastos/{id}
GET    /api/gastos/filtrar
POST   /api/gastos
PUT    /api/gastos/{id}
DELETE /api/gastos/{id}
```

### Dashboard

```http
GET /api/dashboard?mes=2024-01
```

### Relatórios

```http
GET /api/relatorios/pdf?dataInicio=2024-01-01&dataFim=2024-01-31
```

## 🔐 Autenticação

### 1. Registrar Usuário

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "nome": "João Silva",
    "email": "joao@email.com",
    "senha": "senha123"
  }'
```

### 2. Login

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "joao@email.com",
    "senha": "senha123"
  }'
```

Resposta:
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "tipo": "Bearer",
  "email": "joao@email.com",
  "nome": "João Silva"
}
```

### 3. Usar o Token

```bash
curl -X GET http://localhost:8080/api/gastos \
  -H "Authorization: Bearer SEU_TOKEN_AQUI"
```

## 📊 Exemplos de Uso

### Criar Gasto

```bash
curl -X POST http://localhost:8080/api/gastos \
  -H "Authorization: Bearer SEU_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "descricao": "Almoço executivo",
    "valor": 100.00,
    "categoriaId": 1,
    "dataGasto": "2024-01-20",
    "observacao": "Reunião com cliente"
  }'
```

### Listar com Paginação

```bash
curl "http://localhost:8080/api/gastos?page=0&size=10&sort=dataGasto,desc" \
  -H "Authorization: Bearer SEU_TOKEN"
```

### Filtrar Gastos

```bash
curl "http://localhost:8080/api/gastos/filtrar?descricao=almoço&valorMinimo=50&valorMaximo=200&dataInicio=2024-01-01&dataFim=2024-01-31&page=0&size=20" \
  -H "Authorization: Bearer SEU_TOKEN"
```

### Obter Dashboard

```bash
curl "http://localhost:8080/api/dashboard?mes=2024-01" \
  -H "Authorization: Bearer SEU_TOKEN"
```

### Download PDF

```bash
curl "http://localhost:8080/api/relatorios/pdf?dataInicio=2024-01-01&dataFim=2024-01-31" \
  -H "Authorization: Bearer SEU_TOKEN" \
  --output relatorio.pdf
```

