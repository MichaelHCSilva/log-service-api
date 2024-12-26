
# **Sistema de Registro e Monitoramento de Logs**

Este projeto é uma API REST para gerenciamento de usuários e registro de logs de diferentes níveis, permitindo consultas, inserções e exclusões. O sistema também está integrado ao Grafana para visualização e monitoramento dos dados de log.

---

## **Sumário**

- [Tecnologias Utilizadas](#tecnologias-utilizadas)
- [Pré-requisitos](#pré-requisitos)
- [Configuração do Projeto](#configuração-do-projeto)
- [Endpoints da API](#endpoints-da-api)
  - [Autenticação](#autenticação)
  - [Gerenciamento de Logs](#gerenciamento-de-logs)
- [Configuração do Grafana](#configuração-do-grafana)

---

## **Tecnologias Utilizadas**

### **Backend**

- **Java 21**
- **Spring Boot 3.3.5**:
  - Starter Data JPA
  - Starter Web
  - Starter Security
  - Starter Validation
  - Starter Actuator
  - DevTools

### **Banco de Dados**

- **PostgreSQL**

### **Monitoramento**

- **Grafana**

### **Limitação de Taxa**

- **Bucket4j**

### **Autenticação**

- **JWT (JSON Web Tokens)**

### **Infraestrutura**

- **Docker** e **Docker Compose**

---

## **Pré-requisitos**

Certifique-se de ter instalado:

- Docker e Docker Compose
- Postman (Testar a API)
- Navegador Web (Acessar o Grafana)

---

## **Configuração do Projeto**

1. **Clone o repositório**:
   ```
   git clone https://github.com/MichaelHCSilva/log-service-api.git
   cd log-service-api
   ```

2. **Suba os containers Docker**:
   ```
   docker-compose up -d
   ```

3. **Acesse o PostgreSQL**:
   - **Host:** `postgres`
   - **Porta:** `5432`
   - **Banco de Dados:** `log_service`
   - **Usuário:** `postgres`
   - **Senha:** `admin`

4. **Inicie o servidor Spring Boot**:
   ```
   mvn spring-boot:run
   ```

5. **Acesse a API**:
   - **URL base:** `http://localhost:8080`

---

## **Endpoints da API**

### **Autenticação**

#### Registro de Usuário
- **URL:** `POST /auth/register`
- **Body (JSON)**:
  ```json
  {
    "username": "username",
    "password": "password"
  }
  ```

#### Login para Geração de Token
- **URL:** `POST /auth/login`
- **Body (JSON)**:
  ```json
  {
    "username": "username",
    "password": "password"
  }
  ```
- **Resposta de Sucesso**:
  Apenas o token será retornado. Exemplo:
  ```json
  {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
  }
  ```

#### Configuração do Token no Postman
1. Vá até a aba **Authorization** no Postman.
2. Selecione **Bearer Token**.
3. Cole o token gerado no campo de token.

---

### **Gerenciamento de Logs**

#### Inserir Logs

- **URL:** `POST /logs`

Exemplo de um log de erro:
```
{
  "nivel": "ERROR",
  "message": "Falha ao conectar ao banco de dados",
  "additionalData": {
    "errorDetails": {
      "exception": "SQLException",
      "stackTrace": "java.sql.SQLException: Conexão recusada (Connection refused)
  at com.example.Database.connect(Database.java:45)
  at com.example.Service.process(Service.java:30)"
    },
    "environment": {
      "os": "Windows 10",
      "javaVersion": "21",
      "applicationVersion": "1.0.0"
    },
    "errorCode": 500
  }
}
```

#### Consultar Logs por Nível
- **URL:** `GET /logs?nivel=ERROR`

#### Consultar Logs por Período
- **URL:** `GET /logs?nivel=ERROR&startDate=2024-11-01T00:00:00&endDate=2024-11-25T23:59:59`

#### Excluir Log por ID
- **URL:** `DELETE /logs/{id}`
- Exemplo de ID: `550e8400-e29b-41d4-a716-446655440000`

---

## **Configuração do Grafana**

1. **Acesse o Grafana**:
   - **URL:** [http://localhost:3000](http://localhost:3000)
   - **Usuário padrão:** `admin`
   - **Senha padrão:** `admin`

2. **Adicione uma nova fonte de dados**:
   - Vá para **Configuration** > **Data Sources** > **Add data source**.
   - Selecione **PostgreSQL**.

3. **Configure a conexão**:
   - **Host:** `postgres:5432`
   - **Database:** `log_service`
   - **User:** `postgres`
   - **Password:** `admin`
   - **SSL Mode:** `disable`

4. Clique em **Save & Test** para verificar a conexão.

---
