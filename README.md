# Sistema Hélios

Backend Java/Spring Boot para gerenciamento e monitoramento de habitats espaciais autônomos. O sistema registra habitats, módulos, ocupantes, sensores, leituras, alertas, ações automáticas, logs e reservas operacionais.

## Equipe

| Integrante | RM |
| --- | --- |
| Bruno Martins Bettio | RM564939 |
| José Diogo Da Silva Neves | RM562341 |
| Arthur dos Santos Cabral | RM566515 |
| Mariana Xavier Quispe | RM566357 |
| Julia Tiziotto Buttler | RM564975 |

## Tecnologias

- Java 21
- Spring Boot
- Spring Data JPA
- PostgreSQL no Docker/Azure
- H2 para execução local sem container
- Maven
- Docker e Docker Compose
- Swagger/OpenAPI

## Arquitetura em Nuvem

```mermaid
flowchart LR
    U["Usuário / Avaliador"] -->|HTTP 8080 pelo IP público| AZ["Azure Resource Group"]
    AZ --> NSG["Network Security Group<br/>Portas 22, 8080 e 5432"]
    NSG --> VM["Azure VM Ubuntu<br/>Docker Engine + Compose"]
    VM --> APP["Container App<br/>sistema-helios-app-rm566515<br/>Spring Boot API"]
    APP -->|JDBC na rede Docker| DB["Container Banco<br/>postgres-helios-rm566515<br/>PostgreSQL"]
    DB --> VOL["Volume nomeado<br/>postgres_data_rm566515"]
    APP --> API["Endpoints REST<br/>habitats, módulos, ocupantes, sensores, leituras, alertas, ações, logs e reservas"]
```

## Requisitos DevOps Atendidos

- Aplicação Java conteinerizada com imagem personalizada via `Dockerfile`.
- Imagem da aplicação otimizada com runtime Java reduzida via `jlink`, ficando abaixo de 400 MB.
- Container da aplicação executando com o usuário não privilegiado `helios`.
- Diretório de trabalho definido em `/opt/sistema-helios`.
- Variáveis de ambiente configuradas para a aplicação e para o banco.
- Portas expostas: aplicação `8080` e banco `5432`.
- Containers com nome contendo RM: `sistema-helios-app-rm566515` e `postgres-helios-rm566515`.
- Banco PostgreSQL com volume nomeado `postgres_data_rm566515`.
- Aplicação e banco na mesma rede Docker: `helios-net-rm566515`.
- CRUDs disponíveis para as entidades principais.
- Persistência em tabelas relacionadas, como `modulos_habitacionais -> habitats`, `sensor -> modulos_habitacionais`, `alerta -> sensor/modulo`, `acao_automatica -> alerta` e `reservas -> ocupantes/modulos_habitacionais`.

## Execução na VM Azure

1. Conecte-se à VM:

```bash
ssh azureuser@IP_PUBLICO_DA_VM
```

2. Instale o Docker na VM, caso ainda não esteja instalado:

```bash
chmod +x scripts/azure-vm-setup.sh
./scripts/azure-vm-setup.sh
```

Depois da instalação, saia da sessão SSH e entre novamente para atualizar o grupo do usuário.

3. Clone o repositório:

```bash
git clone https://github.com/TaikaWaititi/global-codeinstars-devops.git
cd global-codeinstars-devops
```

4. Configure as variáveis de ambiente:

```bash
cp .env.example .env
nano .env
```

Por padrão, a API pública fica na porta `8080` e o banco na porta `5432`. Se outra aplicação estiver usando essas portas durante testes locais, altere `APP_HOST_PORT` ou `DB_HOST_PORT` no arquivo `.env`.

5. Suba os containers em segundo plano:

```bash
docker compose up -d --build
```

6. Confira os containers:

```bash
docker compose ps
```

7. Acesse a aplicação usando o IP público da VM:

- API: `http://IP_PUBLICO_DA_VM:8080`
- Swagger: `http://IP_PUBLICO_DA_VM:8080/swagger-ui.html`

As portas `8080` e `5432` precisam estar liberadas no Network Security Group da VM Azure. Sempre que possível, a porta `5432` deve ficar restrita ao IP usado na avaliação ou gravação.

Para um passo a passo específico de Azure, consulte [AZURE_DEPLOY.md](AZURE_DEPLOY.md).

Scripts disponíveis para a VM:

- `scripts/azure-vm-setup.sh`: instala Docker e Docker Compose.
- `scripts/azure-deploy.sh`: sobe aplicação e banco com Docker Compose.
- `scripts/azure-smoke-test.sh`: cria dados via API para demonstração.
- `scripts/azure-evidence.sh`: exibe logs, comandos `exec` e consultas `SELECT` no banco.
- `azure-nsg-commands.md`: mostra exemplos de regras de NSG pelo Azure CLI.

## Comandos Úteis

Parar os containers:

```bash
docker compose down
```

Parar os containers e remover o volume do banco:

```bash
docker compose down -v
```

Rodar os testes Java:

```bash
./mvnw test
```

No Windows:

```powershell
.\mvnw.cmd test
```
