# Comanda Livre 🍽️

O **Comanda Livre** é um projeto para gerenciamento de comandas em restaurantes. Este repositório atua como meu **laboratório de engenharia**, onde aplico conceitos de arquitetura, escalabilidade e novas tecnologias conforme evoluo em minha jornada como desenvolvedor.

Atualmente, o projeto está em transição de um **Monolito Modular** (Spring Modulith) que rodava em Docker Compose para uma infraestrutura orquestrada por **Kubernetes (K8s)**.

---

## 🚀 Estado Atual: Migração para Kubernetes

Nesta etapa inicial, o foco foi a migração completa dos recursos auxiliares e da API para um cluster local utilizando **Kind**. A infraestrutura foi organizada utilizando **Kustomize**, permitindo a separação entre definições base e customizações de ambiente.

### Componentes da Infraestrutura:

* **PostgreSQL:** Banco de dados principal e instância dedicada para o Keycloak.
* **Keycloak:** Gestão de identidade e acesso (IAM).
* **MinIO:** Storage de objetos compatível com S3 para fotos e documentos.
* **MailHog:** Servidor SMTP local para testes de envio de e-mail.
* **Redis Stack:** Cache distribuído e interface visual de monitoramento.

> **Nota de Performance:** Todos os manifestos possuem limites de recursos (`limits/requests`) configurados para testar o desempenho da aplicação operando em cenários de recursos reduzidos.

---

## 📂 Estrutura de Diretórios

A organização segue o padrão de **Base/Overlays** do Kustomize:

```text
.
├── k8s
│   ├── base                   # Recursos compartilhados (Deployments/Services)
│   │   ├── apps               # Manifesto da API Backend
│   │   ├── auth               # Instância do Keycloak
│   │   ├── infra              # Recursos de apoio (DB, Cache, Mail, Storage)
│   │   └── namespace.yaml
│   ├── cluster                # Configuração do Cluster Kind
│   │   └── kind-config.yaml
│   ├── old                    # Histórico da migração (Antigo arquivo único)
│   │   └── kubernete.yml
│   └── overlays
│       └── dev                # Patches, ConfigMaps e Secrets para ambiente local
│           ├── config.yaml
│           ├── patch-nodeports.yaml
│           └── secrets.yaml
└── screenshots                

```

---

## 🛠️ Comandos Úteis

### Gestão do Cluster (Kind)

```bash
# Criar o cluster com o mapeamento de portas necessário
kind create cluster --config k8s/cluster/kind-config.yaml

# Deletar o cluster
kind delete cluster --name comanda-livre

```

### 📦 Preparando a Imagem (Local)

Antes de aplicar os manifestos, certifique-se de que a imagem do backend está disponível no nó do cluster:

```bash

kind load docker-image comandalivre/api:latest --name comanda-livre

```

### Gestão de Manifestos (Kustomize)

```bash
# Validar as configurações geradas sem aplicar
kubectl kustomize k8s/overlays/dev

# Aplicar todas as alterações no cluster
kubectl apply -k k8s/overlays/dev

```

#### 🌐 Onde acessar?

Após o deploy, os serviços estarão disponíveis nas seguintes portas (mapeadas via `extraPortMappings` no Kind):

| Serviço | URL Local | Porta K8s (NodePort) |
| --- | --- | --- |
| **API Backend** | `http://localhost:8080` | 30080 |
| **Keycloak UI** | `http://localhost:8082` | 30082 |
| **MailHog Web** | `http://localhost:8025` | 30025 |
| **Postgres** | `localhost:5432` | 30432 |

---

## 🖼️ Screenshots do Ambiente

| Swagger API | Keycloak Auth | MailHog (SMTP) | Podman | Ferramenta de Testes |
| --- | --- | --- | --- | --- |
| ![Screenshot Swagger API](screenshots/swagger.png) | ![Screenshot Keycloak](screenshots/keycloak.png) | ![Screenshot MailHog](screenshots/mailhog.png) | ![Screenshot Podman](screenshots/podman_deployments.png) | ![Screenshot Testes](screenshots/test_tool.png) |

---

## 🛤️ Próximos Passos

* [ ] Publicar o codigo fonte da API e do Frontend Web (Flutter).
* [ ] Implementar mensageria com **Kafka** (Migração da `spring-modulith-events-api`).
* [ ] Criar scripts de automação para tarefas repetitivas.
* [ ] Iniciar a extração de microservices.
* [ ] Implementar comunicação entre serviços via **gRPC**.
* [ ] Adicionar um **API Gateway** para centralizar as chamadas.
