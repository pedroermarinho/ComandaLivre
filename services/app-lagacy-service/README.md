 # ComandaLivre API

Bem-vindo à documentação do ComandaLivre API. Este projeto serve como o backend para um ecossistema de aplicações, incluindo **ComandaLivre** (gerenciamento de restaurantes) e **PrumoDigital** (gerenciamento de projetos de construção), construído sobre uma arquitetura de monólito modular.

## 📖 Visão Geral da Arquitetura

Este projeto adota uma arquitetura de **Monólito Modular** com o auxílio do **Spring Modulith**. O objetivo é ter um único deploy (monólito) com um forte encapsulamento e limites bem definidos entre os domínios de negócio, facilitando a manutenção e a potencial evolução futura para microsserviços.

A estrutura é organizada nos seguintes módulos principais:

  * **`shared`**: Contém código transversal e compartilhado, como entidades de base (`users`, `assets`), utilitários, configurações de infraestrutura e a API pública de serviços comuns.
  * **`company`**: Módulo responsável por gerenciar a entidade "Empresa" (que pode ser um restaurante ou uma construtora), seus tipos, configurações, funcionários, cargos e convites.
  * **`comandalivre`**: Módulo com a lógica de negócio específica da aplicação ComandaLivre. Inclui gerenciamento de produtos, cardápios, mesas, comandas e pedidos.
  * **`prumodigital`**: Módulo com a lógica de negócio específica da aplicação PrumoDigital, focado em gerenciamento de projetos, relatórios diários de obras e atividades.

Dentro de cada módulo, seguimos princípios da **Clean Architecture**, separando o código em camadas: `domain` (coração do negócio), `data` (acesso a dados), `infra` (infraestrutura) e `presenter` (API).

## 🛠️ Tech Stack

| Categoria                | Tecnologia / Ferramenta                                                                                             |
| :----------------------- | :------------------------------------------------------------------------------------------------------------------ |
| **Linguagem & Framework** | [Kotlin](https://kotlinlang.org/) 2.1+, [Spring Boot](https://spring.io/projects/spring-boot) 3.4+                        |
| **Banco de Dados** | [PostgreSQL](https://www.postgresql.org/)                                                                           |
| **Acesso a Dados** | [jOOQ](https://www.jooq.org/) para construção de queries type-safe                                                   |
| **Versionamento de DB** | [Liquibase](https://www.liquibase.org/) para gerenciamento de migrações de schema                                     |
| **Arquitetura Modular** | [Spring Modulith](https://spring.io/projects/spring-modulith)                                                       |
| **Autenticação** | [Spring Security 6](https://spring.io/projects/spring-security) com OAuth2 Resource Server                          |
| **Provedor de Identidade** | [Firebase Authentication](https://firebase.google.com/docs/auth)                                                    |
| **Armazenamento de Arquivos** | [AWS S3](https://aws.amazon.com/s3/) (Produção) / [Minio](https://min.io/) (Local)                                    |
| **Envio de Emails** | [Spring Boot Mail](https://docs.spring.io/spring-boot/docs/current/reference/html/io.html%23io.email) / [MailHog](https://github.com/mailhog/MailHog) (Local) |
| **Cache** | [Caffeine](https://github.com/ben-manes/caffeine)                                                                   |
| **Documentação da API** | [Springdoc OpenAPI](https://springdoc.org/) (Swagger UI)                                                            |
| **Qualidade de Código** | [Spotless](https://github.com/diffplug/spotless) com [Ktlint](https://ktlint.github.io/)                              |
| **Testes** | [JUnit 5](https://junit.org/junit5/), [Testcontainers](https://www.testcontainers.org/), [AssertJ](https://assertj.github.io/doc/) |
| **Containerização** | [Docker](https://www.docker.com/) & [Docker Compose](https://docs.docker.com/compose/)                              |

## 🚀 Configuração do Ambiente de Desenvolvimento

Siga os passos abaixo para configurar e executar o projeto localmente.

### Pré-requisitos

  * **JDK 21** ou superior.
  * **Docker** e **Docker Compose** instalados e em execução.
  * **Node.js & npm** (para instalar o Firebase CLI).
  * **Firebase CLI**: Se não tiver, instale com `npm install -g firebase-tools` e faça login com `firebase login`.

### Passo 1: Variáveis de Ambiente

O projeto utiliza um arquivo `.env` na raiz para gerenciar variáveis de ambiente, carregado pela dependência `spring-dotenv`.

1.  Copie o arquivo `.env.example` (se existir) para um novo arquivo chamado `.env` na raiz do projeto.
2.  Preencha as variáveis necessárias, como credenciais de banco de dados (se diferentes do padrão no `docker-compose.yml`), Firebase e outras configurações sensíveis.

O arquivo `application.yaml` já contém valores padrão para muitas variáveis, facilitando a execução em ambiente de desenvolvimento.

### Passo 2: Serviços de Infraestrutura com Docker

O `docker-compose.yml` provisiona os serviços necessários para o desenvolvimento local.

1.  Para iniciar os contêineres do PostgreSQL, Minio (emulador S3) e MailHog, execute:
    ```bash
    docker-compose up -d
    ```
2.  **Acessos:**
      * **Minio Console:** `http://localhost:9001` (Credenciais padrão: `minioadmin`/`minioadmin`)
      * **MailHog UI:** `http://localhost:8025`

### Passo 3: Emuladores do Firebase (Opcional)

Para testar o fluxo de autenticação localmente sem depender da infraestrutura real do Firebase, você pode usar os emuladores.

1.  **Inicialize os emuladores** (se for a primeira vez):
    ```bash
    firebase init emulators
    ```
    Selecione "Authentication" e configure as portas padrão.
2.  **Inicie os emuladores**:
    ```bash
    firebase emulators:start
    ```
    A UI dos emuladores estará disponível em `http://localhost:4000`.

### Passo 4: Banco de Dados e Geração de Código

O projeto usa Liquibase para migrações e jOOQ para geração de código type-safe.

1.  **Aplique as Migrações do Banco de Dados:**
    A primeira vez que você rodar, ou sempre que houver novas migrações, execute a task do Liquibase.

    ```bash
    ./gradlew update
    ```

    Isso criará os schemas (`public`, `company`, etc.) e todas as tabelas.

2.  **Gere as Classes jOOQ:**
    Após qualquer alteração no esquema do banco, você precisa regenerar as classes jOOQ. A task customizada `generateAllJooq` já está configurada para fazer isso para todos os schemas.

    ```bash
    ./gradlew generateAllJooq
    ```

    Esta task também executa `update` do Liquibase e `spotlessApply` para formatação.

### Passo 5: Executando a Aplicação

Com a infraestrutura Docker rodando e o banco de dados migrado, você pode iniciar a aplicação Spring Boot:

```bash
./gradlew bootRun
```

Ou execute a classe `ComandaLivreApiApplication.kt` diretamente pela sua IDE.

## ✅ Testes

O projeto separa testes de unidade e de integração.

  * **Testes de Unidade**: Rápidos e sem contexto Spring. São executados por padrão.
    ```bash
    ./gradlew test
    ```
  * **Testes de Integração**: Usam `@SpringBootTest`, Testcontainers e são marcados com `@Tag("integration")`.
    ```bash
    ./gradlew integrationTest
    ```
  * **Executar Todos os Testes:**
    ```bash
    ./gradlew check
    ```
    (A task `check` depende de `test` e `integrationTest` se você configurar)

## 🎨 Qualidade de Código

Utilizamos **Spotless** com **Ktlint** para garantir um padrão de código consistente.

  * **Para verificar se há problemas de formatação:**
    ```bash
    ./gradlew spotlessCheck
    ```
  * **Para aplicar a formatação automaticamente:**
    ```bash
    ./gradlew spotlessApply
    ```

## 📚 Documentação da API

A API é documentada usando **Springdoc OpenAPI v3**. Após iniciar a aplicação, a UI do Swagger estará disponível em:

  * **[http://localhost:8080/docs](http://localhost:8080/docs)**
