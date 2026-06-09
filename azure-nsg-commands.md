# Comandos Azure CLI para NSG

Use estes comandos se a equipe for configurar a VM pelo Azure CLI. Substitua os valores entre `<...>`.

```bash
az login

az network nsg rule create \
  --resource-group <RESOURCE_GROUP> \
  --nsg-name <NSG_NAME> \
  --name Allow-Helios-API-8080 \
  --priority 1001 \
  --access Allow \
  --direction Inbound \
  --protocol Tcp \
  --source-address-prefixes Internet \
  --source-port-ranges '*' \
  --destination-address-prefixes '*' \
  --destination-port-ranges 8080

az network nsg rule create \
  --resource-group <RESOURCE_GROUP> \
  --nsg-name <NSG_NAME> \
  --name Allow-Helios-Postgres-5432 \
  --priority 1002 \
  --access Allow \
  --direction Inbound \
  --protocol Tcp \
  --source-address-prefixes <SEU_IP_PUBLICO>/32 \
  --source-port-ranges '*' \
  --destination-address-prefixes '*' \
  --destination-port-ranges 5432
```

Para a porta `5432`, prefira restringir `--source-address-prefixes` ao IP de quem vai gravar ou avaliar. A API deve ser demonstrada pela porta `8080`.
