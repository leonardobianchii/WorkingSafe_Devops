#!/bin/bash
set -euo pipefail

##############################################
#  CONFIGURAÇÕES DA SOLUÇÃO WORKINGSAFE
##############################################

# Região e Resource Group
RG_NAME="rg-workingsafe"
LOCATION="eastus"

# Azure Container Registry
ACR_NAME="acrworkingsafe"              # mesmo nome do seu ACR

# Imagem da aplicação no ACR
APP_IMAGE_REPOSITORY="fiap/workingsafe-api"
APP_IMAGE_TAG="3"                      # tag que você conferiu no ACR
APP_CONTAINER_PORT=8080

# Azure Container Instances
ACI_API_NAME="aci-workingsafe-api"
ACI_SQL_NAME="aci-sql-workingsafe"

# Container SQL Server
SQL_CONTAINER_IMAGE="mcr.microsoft.com/mssql/server:2019-latest"
SQL_CONTAINER_PORT=1433

# Senha do usuário sa (NÃO deixar hardcoded aqui)
# Antes de rodar o script:
#   export SQL_SA_PASSWORD="SuaSenhaForteAqui"
SQL_SA_PASSWORD="${SQL_SA_PASSWORD:-}"

if [[ -z "$SQL_SA_PASSWORD" ]]; then
  echo "❌ ERRO: variável de ambiente SQL_SA_PASSWORD não definida."
  echo "Defina antes de rodar, exemplo:"
  echo "  export SQL_SA_PASSWORD=\"SuaSenhaForteAqui\""
  exit 1
fi

##############################################
#  LOGIN E PREPARO DA SUBSCRIPTION
##############################################

echo "🔐 Verificando login na Azure..."
if ! az account show >/dev/null 2>&1; then
  az login
fi
echo "✅ Login ok."

echo "🧩 Registrando providers necessários (se ainda não estiverem)..."
az provider register --namespace Microsoft.ContainerInstance --wait >/dev/null
az provider register --namespace Microsoft.ContainerRegistry --wait >/dev/null

##############################################
#  RESOURCE GROUP
##############################################

echo "📦 Criando (ou garantindo) Resource Group: $RG_NAME"
az group create \
  --name "$RG_NAME" \
  --location "$LOCATION" \
  --output none

##############################################
#  AZURE CONTAINER REGISTRY (ACR)
##############################################

echo "🐳 Criando (ou garantindo) Azure Container Registry: $ACR_NAME"
az acr create \
  --resource-group "$RG_NAME" \
  --name "$ACR_NAME" \
  --sku Basic \
  --admin-enabled true \
  --output none || true

# Descobre o login server (ex.: acrworkingsafe.azurecr.io)
ACR_LOGIN_SERVER=$(az acr show -n "$ACR_NAME" --query loginServer -o tsv)

echo "🔑 Buscando credenciais do ACR..."
ACR_USER=$(az acr credential show -n "$ACR_NAME" --query username -o tsv)
ACR_PASS=$(az acr credential show -n "$ACR_NAME" --query "passwords[0].value" -o tsv)

##############################################
#  AZURE CONTAINER INSTANCE - SQL SERVER
##############################################

echo "----------------------------------------"
echo "🗄️ Criando SQL Server em container (ACI)"
echo "Imagem: $SQL_CONTAINER_IMAGE"
echo "DNS: ${ACI_SQL_NAME}.${LOCATION}.azurecontainer.io"
echo "----------------------------------------"

echo "🧹 Removendo container SQL anterior (se existir)..."
az container delete \
  --name "$ACI_SQL_NAME" \
  --resource-group "$RG_NAME" \
  --yes \
  --only-show-errors || true

az container create \
  --name "$ACI_SQL_NAME" \
  --resource-group "$RG_NAME" \
  --location "$LOCATION" \
  --image "$SQL_CONTAINER_IMAGE" \
  --cpu 2 \
  --memory 4 \
  --os-type Linux \
  --dns-name-label "$ACI_SQL_NAME" \
  --ports $SQL_CONTAINER_PORT \
  --protocol TCP \
  --restart-policy Always \
  --environment-variables \
      "ACCEPT_EULA=Y" \
      "MSSQL_SA_PASSWORD=$SQL_SA_PASSWORD" \
      "MSSQL_PID=Express" \
  --output none

SQL_FQDN="${ACI_SQL_NAME}.${LOCATION}.azurecontainer.io"

##############################################
#  AZURE CONTAINER INSTANCE - API
##############################################

echo "----------------------------------------"
echo "🚀 Iniciando deploy da API no Azure Container Instances"
echo "Imagem: $ACR_LOGIN_SERVER/$APP_IMAGE_REPOSITORY:$APP_IMAGE_TAG"
echo "----------------------------------------"

# DNS único pra não conflitar com deploys anteriores
API_DNS_LABEL="${ACI_API_NAME}-$(date +%s)"

echo "🧹 Removendo container API anterior (se existir)..."
az container delete \
  --name "$ACI_API_NAME" \
  --resource-group "$RG_NAME" \
  --yes \
  --only-show-errors || true

az container create \
  --name "$ACI_API_NAME" \
  --resource-group "$RG_NAME" \
  --location "$LOCATION" \
  --image "$ACR_LOGIN_SERVER/$APP_IMAGE_REPOSITORY:$APP_IMAGE_TAG" \
  --cpu 1 \
  --memory 1.5 \
  --os-type Linux \
  --dns-name-label "$API_DNS_LABEL" \
  --ports $APP_CONTAINER_PORT \
  --protocol TCP \
  --restart-policy Always \
  --registry-login-server "$ACR_LOGIN_SERVER" \
  --registry-username "$ACR_USER" \
  --registry-password "$ACR_PASS" \
  --output none

##############################################
#  RESULTADO FINAL
##############################################

echo "✅ Infraestrutura criada e deploy concluído com sucesso (APENAS CONTAINERS)!"
echo "----------------------------------------"
echo "🌐 URL da API:"
echo "    http://${API_DNS_LABEL}.${LOCATION}.azurecontainer.io:${APP_CONTAINER_PORT}"
echo ""
echo "🗄️ SQL Server em container:"
echo "    Host: ${SQL_FQDN}"
echo "    Porta: $SQL_CONTAINER_PORT"
echo "    Usuário: sa"
echo "    Senha: (valor da variável SQL_SA_PASSWORD)"
echo "    Database esperado pela aplicação: db_workingsafe"
echo "----------------------------------------"
echo "⚠️ Lembre de deixar no application.yml:"
echo "    jdbc:sqlserver://${SQL_FQDN}:1433;database=db_workingsafe;encrypt=true;trustServerCertificate=true;"
