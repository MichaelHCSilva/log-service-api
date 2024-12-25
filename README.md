# **Sistema de Registro e Monitoramento de Logs**

Este projeto é uma API REST para gerenciamento de usuários e registro de logs de diferentes níveis, permitindo consultas, inserções e exclusões. O sistema também está integrado ao Grafana para visualização e monitoramento dos dados de log.

---

## **Sumário**

- Tecnologias Utilizadas
- Pré-requisitos
- Configuração do Projeto
- Endpoints da API

  - Autenticação
  - Gerenciamento de Logs

- Configuração do Grafana
- Exemplos de Logs
- Contribuições
- Licença

---

## **Tecnologias Utilizadas**

### **Backend**

- **Java 21**: Linguagem principal do projeto.
- **Spring Boot 3.3.5**:

  - **Spring Boot Starter Data JPA**: Para abstração do acesso a dados.
  - **Spring Boot Starter Web**: Para criação de endpoints REST.
  - **Spring Boot Starter Security**: Para autenticação e segurança.
  - **Spring Boot Starter Validation**: Para validações de dados na camada de modelo.
  - **Spring Boot Starter Actuator**: Para monitoramento e gestão do aplicativo.
  - **Spring Boot DevTools**: Para desenvolvimento e atualização dinâmica durante o desenvolvimento.

### **Banco de Dados**

- **PostgreSQL**:

  - Utilizado como banco de dados relacional.
  - Integração com Spring Data JPA para manipulação de dados.

### **Monitoramento**

- **Grafana**: Painel para visualização de métricas e monitoramento do sistema.

### **Limitação de Taxa**

- **Bucket4j**: Controle de limites de requisições para endpoints, garantindo o gerenciamento de carga.

### **Autenticação e Autorização**

- **JWT (JSON Web Tokens)**: Gerenciamento de sessões de usuários com as bibliotecas:

  - jjwt-api
  - jjwt-impl
  - jjwt-jackson

### **Infraestrutura**

- **Docker**: Construção e execução do ambiente de desenvolvimento.

  - **Docker Compose** para gerenciar múltiplos containers:

    - **PostgreSQL**: Banco de dados.
    - **Grafana**: Painel de monitoramento.
    - **Log Service API**: Backend da aplicação.

---

## **Pré-requisitos**

Antes de começar, certifique-se de ter as seguintes ferramentas instaladas:

- Docker e Docker Compose
- Postman (testar a API)
- Navegador Web (acessar o Grafana)

---

## **Configuração do Projeto**

1.  **Clone o repositório:**

    ```
    git clone https://github.com/MichaelHCSilva/log-service-api.git
    cd log-service-api
    ```

2.  **Configure e inicie os containers Docker:**

    - Certifique-se de que o Docker está em execução.
    - Suba os containers:

      ```
      docker-compose up -d
      ```

3.  **Acesse o PostgreSQL:**

    - **Host:** `postgres`
    - **Porta:** `5432`
    - **Banco de Dados:** `log_service`
    - **Usuário:** `postgres`
    - **Senha:** `admin`

4.  **Inicie o servidor Spring Boot:**

    ```
      mvn spring-boot:run

    ```

5.  **Acesse a API no navegador ou Postman:**

    - **URL base:** `http://localhost:8080`

---

## **Endpoints da API**

### **Autenticação**

#### **Registro de Usuário**

- **URL:** `POST /auth/register`
- **Body (JSON):**

  ```
  {
    "username": "michael",
    "password": "1234@Michael"
  }
  ```

#### **Login para Geração de Token**

- **URL:** `POST /auth/login`
- **Body (JSON):**

  ```
  {
    "username": "michael",
    "password": "1234@Michael"
  }
  ```

---

### **Gerenciamento de Logs**

#### **Inserir Logs**

1.  **Log de Erro**

    ```
    {
      "nivel": "ERROR",
      "message": "Falha ao conectar ao banco de dados",
      "additionalData": {
        "errorDetails": {
          "exception": "SQLException",
          "stackTrace": "java.sql.SQLException: Conexão recusada (Connection refused)\n\tat com.example.Database.connect(Database.java:45)\n\tat com.example.Service.process(Service.java:30)"
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

#### **Consultar Logs por Nível**

- **URL:** `GET /logs?nivel=ERROR`

#### **Consultar Logs por Período**

- **URL:** `GET /logs?nivel=ERROR&startDate=2024-11-01T00:00:00&endDate=2024-11-25T23:59:59`

#### **Excluir Log por ID**

- **URL:** `DELETE /logs/{id}`
- **Exemplo de ID:** `550e8400-e29b-41d4-a716-446655440000`

---

## **Configuração do Grafana**

1.  **Acesse o Grafana:**

    - **URL:** [http://localhost:3000](http://localhost:3000)
    - **Usuário padrão:** `admin`
    - **Senha padrão:** `admin`

2.  **Adicione uma nova fonte de dados:**

    - Clique em **Configuration (ícone de engrenagem)** > **Data Sources** > **Add data source**.
    - Selecione **PostgreSQL**.

3.  **Configure a conexão:**

    - **Host:** `postgres:5432`
    - **Database:** `log_service`
    - **User:** `postgres`
    - **Password:** `admin`
    - **SSL Mode:** `disable`

4.  **Clique em Save & Test** para verificar a conexão.

---

