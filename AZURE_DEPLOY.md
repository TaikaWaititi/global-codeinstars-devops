# Deploy em VM Azure

Este guia complementa o README para a demonstracao da disciplina em uma VM Linux no Azure.

## Configuracao Recomendada da VM

- Sistema operacional: Ubuntu Server LTS.
- Tamanho: VM com pelo menos 2 vCPU e 2 GB de RAM para build Maven dentro do Docker.
- Disco: SSD padrao com espaco suficiente para imagens Docker.
- Portas liberadas no NSG:
  - `22/tcp` para SSH.
  - `8080/tcp` para API/Swagger.
  - `5432/tcp` para evidenciar acesso externo ao banco, se exigido pelo professor.

Por seguranca, a porta `5432` deve ficar restrita ao IP de quem vai avaliar/gravar sempre que possivel. Para a rubrica, o container do banco tambem fica com a porta publicada no `docker-compose.yml`.

## Preparar a VM

Conecte por SSH:

```bash
ssh azureuser@IP_PUBLICO_DA_VM
```

Instale Docker e Docker Compose:

```bash
chmod +x scripts/azure-vm-setup.sh
./scripts/azure-vm-setup.sh
```

Saia e entre novamente na sessao SSH:

```bash
exit
ssh azureuser@IP_PUBLICO_DA_VM
```

Valide a instalacao:

```bash
docker --version
docker compose version
```

## Subir o Sistema

Clone o repositorio e entre na pasta:

```bash
git clone https://github.com/Marixavq/gs-sistema-helios.git
cd gs-sistema-helios
```

Crie o arquivo `.env` a partir do exemplo:

```bash
cp .env.example .env
nano .env
```

Na VM Azure, mantenha `APP_HOST_PORT=8080` para acessar a API por `http://IP_PUBLICO_DA_VM:8080`. Use outra porta apenas se houver conflito local durante testes.

Suba app e banco em segundo plano com o script preparado:

```bash
chmod +x scripts/*.sh
./scripts/azure-deploy.sh
```

Ou execute manualmente:

```bash
docker compose up -d --build
docker compose ps
```

Defina o IP publico para facilitar os testes:

```bash
export PUBLIC_IP=IP_PUBLICO_DA_VM
```

Acesse:

```text
http://IP_PUBLICO_DA_VM:8080/swagger-ui.html
```

## Evidencias para Gravar

Mostre que os containers estao em execucao:

```bash
docker compose ps
```

Mostre logs dos dois containers:

```bash
docker logs sistema-helios-app-rm566515
docker logs postgres-helios-rm566515
```

Mostre diretorio e usuario do app:

```bash
docker container exec -it sistema-helios-app-rm566515 sh -lc "pwd && ls -la && whoami"
```

Mostre diretorio e usuario do banco:

```bash
docker container exec -it postgres-helios-rm566515 sh -lc "pwd && ls -la && whoami"
```

Execute os testes de CRUD usando o IP publico:

Opcao recomendada:

```bash
BASE_URL="http://${PUBLIC_IP}:8080" ./scripts/azure-smoke-test.sh
```

Ou execute manualmente:

```bash
curl -X POST "http://${PUBLIC_IP}:8080/api/habitats" \
  -H "Content-Type: application/json" \
  -d '{"nome":"Tundralandia Alpha","localizacao":"Marte","tipoHabitat":"Residencial","capacidadeTotal":50,"statusOperacional":"Ativo"}'

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

Comprove direto no banco:

Opcao recomendada:

```bash
./scripts/azure-evidence.sh
```

Ou execute manualmente:

```bash
docker container exec -it postgres-helios-rm566515 psql -U helios_user -d helios_db -c "select id, nome, localizacao, status_operacional from habitats;"
docker container exec -it postgres-helios-rm566515 psql -U helios_user -d helios_db -c "select id_ocupante, nome, funcao, status_ocupante from ocupantes;"
docker container exec -it postgres-helios-rm566515 psql -U helios_user -d helios_db -c "select id, habitat_id, nome_modulo, nivel_risco, status_modulo from modulos_habitacionais;"
docker container exec -it postgres-helios-rm566515 psql -U helios_user -d helios_db -c "select id, id_modulo, nome_sensor, tipo_sensor, status_sensor from sensor;"
docker container exec -it postgres-helios-rm566515 psql -U helios_user -d helios_db -c "select r.id, o.nome, m.nome_modulo, r.status_reserva from reservas r join ocupantes o on o.id_ocupante = r.id_ocupante join modulos_habitacionais m on m.id = r.id_modulo;"
```

## Portas no Azure

Se a API nao abrir pelo navegador, confira:

- A porta `8080` esta liberada no NSG da VM.
- A VM esta usando o IP publico correto.
- O container esta de pe com `docker compose ps`.
- O log da aplicacao nao mostra erro de conexao com o banco.

Para o banco, a aplicacao usa a rede Docker interna. A porta `5432` publicada serve para cumprir a exigencia de porta exposta e para demonstracoes controladas.

Se forem configurar portas pelo Azure CLI, use o guia [azure-nsg-commands.md](azure-nsg-commands.md).
