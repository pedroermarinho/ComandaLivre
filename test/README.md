# ComandaLivre-API: Ferramenta de Geração de Dados e Testes de Integração

Este projeto (`data-test`) é uma ferramenta robusta e modular desenvolvida em Python para geração de dados sintéticos e testes de integração automatizados para a `ComandaLivre-API`. Ele permite simular cenários de uso complexos, desde o onboarding de usuários até fluxos de negócio específicos, garantindo a qualidade e a integridade da API.

## Funcionalidades Principais

-   **Geração de Dados Sintéticos Realistas (pt_BR)**: Utiliza a biblioteca `Faker` configurada para o locale `pt_BR` para gerar nomes, cidades, telefones (formato `(XX) 9XXXX-XXXX`) e e-mails realistas e únicos, evitando conflitos no banco de dados.
-   **Testes de Integração Modulares ("Stories")**: Os testes são organizados em "Stories", que representam fluxos de negócio completos ou cenários de teste específicos. Cada story é um módulo Python independente, facilitando a criação e manutenção.
-   **CLI Interativa**: Uma interface de linha de comando (CLI) construída com `Typer` e `Rich` que oferece uma experiência amigável para:
    -   Descobrir e listar todas as stories disponíveis.
    -   Executar stories individualmente, um número determinado de vezes ou em loop infinito.
    -   Visualizar logs detalhados e um resumo claro dos resultados.

## Configuração do Ambiente

### Pré-requisitos

-   Python 3.10+
-   `uv` (gerenciador de pacotes e ambientes virtuais)

### Instalação

1.  **Navegue até o diretório do projeto `data-test`**:

    ```bash
    cd data-test
    ```

2.  **Crie e ative um ambiente virtual com `uv`** (se ainda não tiver feito):

    ```bash
    uv venv
    source .venv/bin/activate
    ```

3.  **Instale as dependências** (o `uv` lerá o `pyproject.toml`):

    ```bash
    uv pip install -e .
    ```

## Como Usar a CLI

A CLI é o ponto de entrada para todas as funcionalidades.

### 1. Listar Stories Disponíveis

Para ver quais stories estão disponíveis para execução:

```bash
uv run python main.py list-stories
```

### 2. Executar uma Story

Use o comando `run` para iniciar a execução de uma story.

#### Execução Interativa (selecionar no menu):

Se você não especificar uma story, a CLI apresentará um menu interativo:

```bash
uv run python main.py run
```

#### Execução Direta:

Especifique o nome do arquivo da story (sem a extensão `.py`):

```bash
uv run python main.py run public_routes_story
```

#### Opções de Execução:

-   `-i` ou `--iterations <N>`: Executa a story `N` vezes.
-   `-l` ou `--loop`: Executa a story em um loop infinito (pressione `Ctrl+C` para parar).
-   `-d` ou `--delay <segundos>`: Adiciona um atraso entre as iterações (usado com `--loop`).

**Exemplos:**

-   Executar `public_routes_story` 5 vezes:
    ```bash
    uv run python main.py run public_routes_story --iterations 5
    ```

-   Executar `public_routes_story` em loop, com 2 segundos de atraso entre as execuções:
    ```bash
    uv run python main.py run public_routes_story --loop --delay 2
    ```

## Adicionando Novas Stories

Para adicionar um novo cenário de teste:

1.  Crie um novo arquivo Python no diretório `data_test/stories/` (ex: `onboarding_story.py`).
2.  Dentro deste arquivo, defina uma função chamada `run_story()`. Esta função conterá a lógica específica do seu teste.
3.  Utilize o `api_client` (disponível em `data_test.cli.api_client`) e o `data_generator` (disponível em `data_test.cli.data_generator`) para interagir com a API e gerar dados.

**Exemplo Básico de uma Nova Story:**

```python
# data_test/stories/onboarding_story.py
import logging
from data_test.cli.api_client import api_client
from data_test.cli.data_generator import data_generator
from rich.console import Console

console = Console()

def run_story():
    console.print("[bold yellow]Iniciando Onboarding Story[/bold yellow]")
    user_data = data_generator.generate_user_data()
    response = api_client.register_user(user_data)

    if response and response.status_code == 201:
        console.print(f"[green]Usuário {user_data['email']} registrado com sucesso.[/green]")
        # Lógica adicional, como autenticar o usuário, criar empresa, etc.
    else:
        console.print(f"[red]Falha ao registrar usuário {user_data['email']}:[/red] {response.status_code} - {response.text}")

```

A CLI irá descobrir e listar automaticamente sua nova story, permitindo que você a execute.

## Configuração da API

O arquivo `data_test/cli/config.py` contém as configurações globais para a ferramenta, incluindo a URL base da API e os endpoints específicos.

-   **`API_BASE_URL`**: A URL base da sua `ComandaLivre-API`.
-   **`CREATE_USER_ENDPOINT`**: O endpoint para registro de novos usuários.
-   **`AUTHENTICATE_ENDPOINT`**: O endpoint para autenticação de usuários.

**É crucial verificar e ajustar esses endpoints em `data_test/cli/config.py` para que correspondam à sua implementação da API.**

---
Desenvolvido por um Engenheiro de QA Sênior especializado em Python.
