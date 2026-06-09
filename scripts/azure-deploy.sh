#!/usr/bin/env bash
set -euo pipefail

APP_HOST_PORT="${APP_HOST_PORT:-8080}"
DB_HOST_PORT="${DB_HOST_PORT:-5432}"

if ! command -v docker >/dev/null 2>&1; then
  echo "Docker nao encontrado. Execute scripts/azure-vm-setup.sh primeiro."
  exit 1
fi

if ! docker compose version >/dev/null 2>&1; then
  echo "Docker Compose plugin nao encontrado. Execute scripts/azure-vm-setup.sh primeiro."
  exit 1
fi

if [ ! -f .env ]; then
  cp .env.example .env
  echo "Arquivo .env criado a partir de .env.example."
  echo "Revise POSTGRES_PASSWORD antes de gravar a evidencia final."
fi

set -a
. ./.env
set +a

echo "Subindo Sistema Helios em background..."
docker compose up -d --build

echo "Aguardando API responder na porta ${APP_HOST_PORT}..."
for i in $(seq 1 60); do
  if curl -fsS "http://localhost:${APP_HOST_PORT}/api-docs" >/dev/null 2>&1; then
    echo "API pronta: http://localhost:${APP_HOST_PORT}/swagger-ui.html"
    docker compose ps
    exit 0
  fi
  sleep 2
done

echo "API nao respondeu dentro do tempo esperado."
docker compose ps
docker logs --tail 120 sistema-helios-app-rm566515 || true
exit 1
