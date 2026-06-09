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

## Evidências para o Vídeo

Mostre os logs dos dois containers:

```bash
docker logs sistema-helios-app-rm566515
docker logs postgres-helios-rm566515
```

Mostre o diretório e o usuário do container da aplicação:

```bash
docker container exec -it sistema-helios-app-rm566515 sh -lc "pwd && ls -la && whoami"
```

Resultado esperado:

- `pwd`: `/opt/sistema-helios`
- `whoami`: `helios`

Mostre também o diretório e o usuário do container do banco:

```bash
docker container exec -it postgres-helios-rm566515 sh -lc "pwd && ls -la && whoami"
```

## Teste de CRUD e Persistência

Na VM Azure, defina:

```bash
export PUBLIC_IP=IP_PUBLICO_DA_VM
```

Crie os dados principais em sequência:

```bash
chmod +x scripts/*.sh
./scripts/azure-smoke-test.sh
```

Também é possível executar os testes manualmente:

```bash
curl -X POST "http://${PUBLIC_IP}:8080/api/habitats" \
  -H "Content-Type: application/json" \
  -d '{"nome":"Habitat Ares Alpha","localizacao":"Marte","tipoHabitat":"Residencial","capacidadeTotal":50,"statusOperacional":"Ativo"}'

curl -X POST "http://${PUBLIC_IP}:8080/api/ocupantes" \
  -H "Content-Type: application/json" \
  -d '{"nome":"Joao Silva","funcao":"Operador","statusOcupante":"ATIVO"}'

curl -X POST "http://${PUBLIC_IP}:8080/api/modulos" \
  -H "Content-Type: application/json" \
  -d '{"idHabitat":1,"nomeModulo":"Modulo Aurora","tipoModulo":"Residencial","capacidadeOcupantes":8,"capacidadeAtual":5,"statusModulo":"ATIVO","nivelRisco":"BAIXO","indiceRisco":"12%"}'

curl -X POST "http://${PUBLIC_IP}:8080/api/sensores" \
  -H "Content-Type: application/json" \
  -d '{"idModulo":1,"nomeSensor":"Sensor de Temperatura Sala 01","tipoSensor":"TEMPERATURA","statusSensor":"ATIVO","unidadeMedida":"C","limiteMinimo":18.0,"limiteMaximo":30.0,"intervaloLeituraSegundos":60}'

curl -X POST "http://${PUBLIC_IP}:8080/api/reservas" \
  -H "Content-Type: application/json" \
  -d '{"idOcupante":1,"idModulo":1,"dataInicio":"2026-05-31","dataFim":"2026-06-07","statusReserva":"Ativa"}'
```

Liste os registros pela API:

```bash
curl "http://${PUBLIC_IP}:8080/api/habitats"
curl "http://${PUBLIC_IP}:8080/api/ocupantes"
curl "http://${PUBLIC_IP}:8080/api/modulos"
curl "http://${PUBLIC_IP}:8080/api/sensores"
curl "http://${PUBLIC_IP}:8080/api/reservas"
```

Comprove a persistência diretamente no banco:

```bash
./scripts/azure-evidence.sh
```

Também é possível executar as consultas manualmente:

```bash
docker container exec -it postgres-helios-rm566515 psql -U helios_user -d helios_db -c "select id, nome, localizacao, status_operacional from habitats;"
docker container exec -it postgres-helios-rm566515 psql -U helios_user -d helios_db -c "select id_ocupante, nome, funcao, status_ocupante from ocupantes;"
docker container exec -it postgres-helios-rm566515 psql -U helios_user -d helios_db -c "select id, habitat_id, nome_modulo, nivel_risco, status_modulo from modulos_habitacionais;"
docker container exec -it postgres-helios-rm566515 psql -U helios_user -d helios_db -c "select id, id_modulo, nome_sensor, tipo_sensor, status_sensor from sensor;"
docker container exec -it postgres-helios-rm566515 psql -U helios_user -d helios_db -c "select r.id, o.nome, m.nome_modulo, r.status_reserva from reservas r join ocupantes o on o.id_ocupante = r.id_ocupante join modulos_habitacionais m on m.id = r.id_modulo;"
```

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
