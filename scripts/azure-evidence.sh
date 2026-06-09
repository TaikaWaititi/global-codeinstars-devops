#!/usr/bin/env bash
set -euo pipefail

POSTGRES_DB="${POSTGRES_DB:-helios_db}"
POSTGRES_USER="${POSTGRES_USER:-helios_user}"

echo "== Containers =="
docker compose ps

echo
echo "== Logs App =="
docker logs --tail 120 sistema-helios-app-rm566515

echo
echo "== Logs Banco =="
docker logs --tail 80 postgres-helios-rm566515

echo
echo "== Exec App: pwd, ls -la, whoami =="
docker container exec sistema-helios-app-rm566515 sh -lc "pwd && ls -la && whoami"

echo
echo "== Exec Banco: pwd, ls -la, whoami =="
docker container exec postgres-helios-rm566515 sh -lc "pwd && ls -la && whoami"

echo
echo "== SELECTs de persistencia =="
docker container exec postgres-helios-rm566515 psql -U "${POSTGRES_USER}" -d "${POSTGRES_DB}" -c "select id, nome, localizacao, status_operacional from habitats;"
docker container exec postgres-helios-rm566515 psql -U "${POSTGRES_USER}" -d "${POSTGRES_DB}" -c "select id_ocupante, nome, funcao, status_ocupante from ocupantes;"
docker container exec postgres-helios-rm566515 psql -U "${POSTGRES_USER}" -d "${POSTGRES_DB}" -c "select id, habitat_id, nome_modulo, nivel_risco, status_modulo from modulos_habitacionais;"
docker container exec postgres-helios-rm566515 psql -U "${POSTGRES_USER}" -d "${POSTGRES_DB}" -c "select id, id_modulo, nome_sensor, tipo_sensor, status_sensor from sensor;"
docker container exec postgres-helios-rm566515 psql -U "${POSTGRES_USER}" -d "${POSTGRES_DB}" -c "select r.id, o.nome, m.nome_modulo, r.status_reserva from reservas r join ocupantes o on o.id_ocupante = r.id_ocupante join modulos_habitacionais m on m.id = r.id_modulo;"
