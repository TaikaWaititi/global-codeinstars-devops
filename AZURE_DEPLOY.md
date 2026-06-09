# Deploy em VM Azure

Este guia complementa o README para a demonstração da disciplina em uma VM Linux no Azure.

## Configuração Recomendada da VM

- Sistema operacional: Ubuntu Server LTS.
- Tamanho: VM com pelo menos 2 vCPU e 2 GB de RAM para build Maven dentro do Docker.
- Disco: SSD padrão com espaço suficiente para imagens Docker.
- Portas liberadas no NSG:
  - `22/tcp` para SSH.
  - `8080/tcp` para API/Swagger.
  - `5432/tcp` para evidenciar acesso externo ao banco, se exigido pelo professor.

Por segurança, a porta `5432` deve ficar restrita ao IP de quem vai avaliar ou gravar sempre que possível. Para a rubrica, o container do banco também fica com a porta publicada no `docker-compose.yml`.

## Preparar a VM

Conecte-se por SSH:

```bash
ssh azureuser@IP_PUBLICO_DA_VM
```

Instale Docker e Docker Compose:

```bash
chmod +x scripts/azure-vm-setup.sh
./scripts/azure-vm-setup.sh
```

Saia da sessão SSH e entre novamente:

```bash
exit
ssh azureuser@IP_PUBLICO_DA_VM
```

Valide a instalação:

```bash
docker --version
docker compose version
```

## Subir o Sistema

Clone o repositório e entre na pasta:

```bash
git clone https://github.com/TaikaWaititi/global-codeinstars-devops.git
cd global-codeinstars-devops
```

Crie o arquivo `.env` a partir do exemplo:

```bash
cp .env.example .env
nano .env
```

Na VM Azure, mantenha `APP_HOST_PORT=8080` para acessar a API por `http://IP_PUBLICO_DA_VM:8080`. Use outra porta apenas se houver conflito local durante testes.

Suba aplicação e banco em segundo plano com o script preparado:

```bash
chmod +x scripts/*.sh
./scripts/azure-deploy.sh
```

Ou execute manualmente:

```bash
docker compose up -d --build
docker compose ps
```

Defina o IP público para facilitar os testes:

```bash
export PUBLIC_IP=IP_PUBLICO_DA_VM
```

Acesse:

```text
http://IP_PUBLICO_DA_VM:8080/swagger-ui.html
```

## Evidências para Gravar

Mostre que os containers estão em execução:

```bash
docker compose ps
```

Mostre os logs dos dois containers:

```bash
docker logs sistema-helios-app-rm566515
docker logs postgres-helios-rm566515
```

Mostre o diretório e o usuário do container da aplicação:

```bash
docker container exec -it sistema-helios-app-rm566515 sh -lc "pwd && ls -la && whoami"
```

Mostre o diretório e o usuário do container do banco:

```bash
docker container exec -it postgres-helios-rm566515 sh -lc "pwd && ls -la && whoami"
```

Execute os testes de CRUD usando o IP público:

```bash
BASE_URL="http://${PUBLIC_IP}:8080" ./scripts/azure-smoke-test.sh
```

Também é possível executar manualmente:

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

## Portas no Azure

Se a API não abrir pelo navegador, confira:

- A porta `8080` está liberada no NSG da VM.
- A VM está usando o IP público correto.
- O container está em execução com `docker compose ps`.
- O log da aplicação não mostra erro de conexão com o banco.

Para o banco, a aplicação usa a rede Docker interna. A porta `5432` publicada serve para cumprir a exigência de porta exposta e para demonstrações controladas.

Se a equipe configurar portas pelo Azure CLI, use o guia [azure-nsg-commands.md](azure-nsg-commands.md).
