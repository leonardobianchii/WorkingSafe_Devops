#!/bin/bash
set -euo pipefail

##############################################
#  CONFIGURAÇÕES DA SOLUÇÃO WORKINGSAFE
##############################################

# Região e Resource Group
RG_NAME="rg-workingsafe"
LOCATION="eastus"

# Azure SQL
SQL_SERVER_NAME="srv-workingsafe"
SQL_ADMIN_USER="rm558576"
SQL_ADMIN_PASSWORD="Fiap@devops2025"   # uso acadêmico / demo
SQL_DB_NAME="db_workingsafe"

# Azure Container Registry
ACR_NAME="acrworkingsafe"              # mesmo nome do seu ACR

# Imagem da aplicação no ACR
APP_IMAGE_REPOSITORY="fiap/workingsafe-api"
APP_IMAGE_TAG="3"                      # tag que você conferiu no ACR
CONTAINER_PORT=8080

# Azure Container Instance
ACI_NAME="aci-workingsafe"

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
az provider register --namespace Microsoft.Sql --wait >/dev/null

##############################################
#  RESOURCE GROUP
##############################################

echo "📦 Criando (ou garantindo) Resource Group: $RG_NAME"
az group create \
  --name "$RG_NAME" \
  --location "$LOCATION" \
  --output none

##############################################
#  AZURE SQL SERVER + DATABASE
##############################################

echo "🗄️ Criando (ou garantindo) Azure SQL Server: $SQL_SERVER_NAME"
az sql server create \
  --name "$SQL_SERVER_NAME" \
  --resource-group "$RG_NAME" \
  --location "$LOCATION" \
  --admin-user "$SQL_ADMIN_USER" \
  --admin-password "$SQL_ADMIN_PASSWORD" \
  --output none

echo "🌐 Liberando acesso ao SQL apenas para serviços Azure..."
az sql server firewall-rule create \
  --resource-group "$RG_NAME" \
  --server "$SQL_SERVER_NAME" \
  --name "AllowAzureServices" \
  --start-ip-address "0.0.0.0" \
  --end-ip-address "0.0.0.0" \
  --output none

echo "🗃️ Criando (ou garantindo) banco de dados: $SQL_DB_NAME"
az sql db create \
  --resource-group "$RG_NAME" \
  --server "$SQL_SERVER_NAME" \
  --name "$SQL_DB_NAME" \
  --service-objective Basic \
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
  --output none

# Descobre o login server (ex.: acrworkingsafe.azurecr.io)
ACR_LOGIN_SERVER=$(az acr show -n "$ACR_NAME" --query loginServer -o tsv)

echo "🔑 Buscando credenciais do ACR..."
ACR_USER=$(az acr credential show -n "$ACR_NAME" --query username -o tsv)
ACR_PASS=$(az acr credential show -n "$ACR_NAME" --query "passwords[0].value" -o tsv)

##############################################
#  AZURE CONTAINER INSTANCE (ACI)
##############################################

echo "----------------------------------------"
echo "🚀 Iniciando deploy no Azure Container Instances"
echo "Imagem: $ACR_LOGIN_SERVER/$APP_IMAGE_REPOSITORY:$APP_IMAGE_TAG"
echo "----------------------------------------"

# DNS único pra não conflitar com deploys anteriores
DNS_LABEL="${ACI_NAME}-$(date +%s)"

echo "🧹 Removendo container anterior (se existir)..."
az container delete \
  --name "$ACI_NAME" \
  --resource-group "$RG_NAME" \
  --yes \
  --only-show-errors || true

echo "🐋 Criando novo container no ACI..."
az container create \
  --name "$ACI_NAME" \
  --resource-group "$RG_NAME" \
  --location "$LOCATION" \
  --image "$ACR_LOGIN_SERVER/$APP_IMAGE_REPOSITORY:$APP_IMAGE_TAG" \
  --cpu 1 \
  --memory 1.5 \
  --os-type Linux \
  --dns-name-label "$DNS_LABEL" \
  --ports $CONTAINER_PORT \
  --protocol TCP \
  --restart-policy Always \
  --registry-login-server "$ACR_LOGIN_SERVER" \
  --registry-username "$ACR_USER" \
  --registry-password "$ACR_PASS" \
  --output none

##############################################
#  RESULTADO FINAL
##############################################

echo "✅ Infraestrutura criada e deploy concluído com sucesso!"
echo "➡️  URL da aplicação:"
echo "    http://${DNS_LABEL}.${LOCATION}.azurecontainer.io:${CONTAINER_PORT}"
