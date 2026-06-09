#!/usr/bin/env bash
set -euo pipefail

APP_HOST_PORT="${APP_HOST_PORT:-8080}"
BASE_URL="${BASE_URL:-http://localhost:${APP_HOST_PORT}}"
STAMP="$(date +%s)"

json_field() {
  python3 -c 'import json,sys; print(json.load(sys.stdin)[sys.argv[1]])' "$1"
}

post_json() {
  local path="$1"
  local payload="$2"
  curl -fsS -X POST "${BASE_URL}${path}" \
    -H "Content-Type: application/json" \
    -d "${payload}"
}

echo "Testando API em ${BASE_URL}"
curl -fsS "${BASE_URL}/api-docs" >/dev/null

habitat_json="$(post_json /api/habitats "{\"nome\":\"Tundralandia Azure ${STAMP}\",\"localizacao\":\"Marte\",\"tipoHabitat\":\"Residencial\",\"capacidadeTotal\":50,\"statusOperacional\":\"Ativo\"}")"
habitat_id="$(printf "%s" "${habitat_json}" | json_field id)"
echo "Habitat criado: ${habitat_id}"

ocupante_json="$(post_json /api/ocupantes "{\"nome\":\"Joao Silva Azure ${STAMP}\",\"funcao\":\"Operador\",\"statusOcupante\":\"ATIVO\"}")"
ocupante_id="$(printf "%s" "${ocupante_json}" | json_field id)"
echo "Ocupante criado: ${ocupante_id}"

modulo_json="$(post_json /api/modulos "{\"idHabitat\":${habitat_id},\"nomeModulo\":\"Modulo Aurora Azure ${STAMP}\",\"tipoModulo\":\"Residencial\",\"capacidadeOcupantes\":8,\"capacidadeAtual\":5,\"statusModulo\":\"ATIVO\",\"nivelRisco\":\"BAIXO\",\"indiceRisco\":\"12%\"}")"
modulo_id="$(printf "%s" "${modulo_json}" | json_field id)"
echo "Modulo criado: ${modulo_id}"

sensor_json="$(post_json /api/sensores "{\"idModulo\":${modulo_id},\"nomeSensor\":\"Sensor Temperatura Azure ${STAMP}\",\"tipoSensor\":\"TEMPERATURA\",\"statusSensor\":\"ATIVO\",\"unidadeMedida\":\"C\",\"limiteMinimo\":18.0,\"limiteMaximo\":30.0,\"intervaloLeituraSegundos\":60}")"
sensor_id="$(printf "%s" "${sensor_json}" | json_field id)"
echo "Sensor criado: ${sensor_id}"

reserva_json="$(post_json /api/reservas "{\"idOcupante\":${ocupante_id},\"idModulo\":${modulo_id},\"dataInicio\":\"2026-05-31\",\"dataFim\":\"2026-06-07\",\"statusReserva\":\"Ativa\"}")"
reserva_id="$(printf "%s" "${reserva_json}" | json_field id)"
echo "Reserva criada: ${reserva_id}"

echo "Smoke test concluido com sucesso."
