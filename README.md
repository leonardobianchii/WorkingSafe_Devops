# WorkingSafe – DevOps (Azure DevOps + Azure Cloud)

Projeto da **Global Solution 2025/2 – 2º ADS (FIAP)**  
Disciplina: **DevOps Tools & Cloud Computing**

> Tema da GS: **O futuro do trabalho** – bem-estar, saúde mental e risco de burnout em ambientes remotos e híbridos.

---

## 1. Visão geral da solução

O **WorkingSafe** é uma plataforma para **monitorar bem-estar e risco de burnout** em equipes remotas/híbridas.

Fluxo principal:

1. **Colaboradores** fazem check-ins diários informando:
   - humor, foco, horas trabalhadas, pausas, comentários.
2. A API calcula **índices de bem-estar** e **risco de burnout** a partir do histórico.
3. **Gestores** visualizam apenas **dados agregados por time/empresa**, sem expor indivíduos.
4. A aplicação armazena e expõe **recomendações de IA** personalizadas para cada colaborador.
5. Um dashboard (front/mobile) consome esses dados via API Java.

Esta disciplina foca **exclusivamente no pipeline DevOps + infraestrutura em nuvem** usada para hospedar a API Java.

---

## 2. Componentes principais da API WorkingSafe

### 2.1 Entidades centrais

- **Empresa**
  - Dona do “tenant” da solução.
  - Relações:
    - 1 Empresa → N Times de Equipe
    - 1 Empresa → N Usuários
    - 1 Empresa → N Configurações de Gestor
    - 1 Empresa → N Agregados Semanais

- **TimeEquipe**
  - Times/departamentos (ex.: Tecnologia, RH, Comercial).
  - Relações:
    - N Times → 1 Empresa
    - N Usuários → 1 Time
    - N Agregados Semanais → 1 Time
    - N ConfigGestor → 1 Time (opcional)

- **Papel / UsuarioPapel**
  - Papéis: `COLABORADOR`, `GESTOR`, `ADMIN`.
  - `UsuarioPapel` faz N:N entre usuário e papel.
  - Base para as *roles* do Spring Security / JWT.

- **Usuario**
  - Colaborador/gestor/admin autenticado.
  - Relações:
    - N Usuários → 1 Empresa
    - N Usuários → 1 Time (opcional)
    - 1 Usuário → N Checkins
    - 1 Usuário → N Recomendações de IA
    - 1 Usuário → N UsuarioPapel

- **Checkin**
  - Registro diário do colaborador:
    - humor, foco, pausas, horas trabalhadas, observações etc.
  - Relação: N Checkins → 1 Usuário.

- **RecomendacaoIA**
  - Mensagens geradas por IA (categoria, texto, validade, prompt de origem).
  - Relação: N Recomendações → 1 Usuário.

- **ConfigGestor**
  - Regras de alerta (limiar de risco, janela de dias, anonimização).
  - Relações:
    - N Configs → 1 Empresa
    - N Configs → 1 Time (opcional).

- **AggIndiceSemanal**
  - Agregados por semana (empresa/time, nº de usuários, média de bem-estar, risco).
  - Relações:
    - N Agregados → 1 Empresa
    - N Agregados → 1 Time (opcional).

---

## 3. Arquitetura em nuvem (Macro)

> **Importante:** o desenho da arquitetura deve estar em um arquivo de imagem (ex.: `docs/arquitetura-working-safe.png`) e será referenciado no PDF da disciplina.   

Arquitetura atual:

- **Camada de Aplicação**
  - API Java Spring Boot (WorkingSafe API).
  - Empacotada em **Docker**.

- **Camada de Container / Compute**
  - **Azure Container Registry (ACR)**  
    - Nome: `acrworkingsafe`  
    - Repositório de imagem: `fiap/workingsafe-api`  
    - Tags numéricas geradas pela pipeline de build (`Build.BuildId`).
  - **Azure Container Instances (ACI)**  
    - Resource Group: `rg-workingsafe`  
    - Container Group: `aci-workingsafe` (DNS dinâmico com timestamp)  
    - Porta exposta: `8080` (HTTP).

- **Camada de Dados**
  - **Azure SQL Database**
    - Server: `srv-workingsafe`
    - Database: `db_workingsafe`
    - Login: `rm558576` (senha configurada via variável de ambiente).

- **Camada de DevOps**
  - **Azure DevOps Project**: `WorkingSafe`
  - **Azure Repos**: repositório Git privado com código da API.
  - **Azure Boards**: histórias/tarefas vinculadas a commits, branches e PRs.
  - **Azure Pipelines**
    - Build (YAML) – CI.
    - Release (Classic) – CD, com task Azure CLI chamando script Bash para criar/atualizar o ACI.

---

## 4. Scripts de Infraestrutura

Atendendo ao requisito de **provisionamento via Azure CLI** e naming convention da disciplina:   

Pasta sugerida: `/scripts`

- `script-infra-rg-acr-aci-sql.sh`  
  Cria:
  - Resource Group `rg-workingsafe`
  - Azure Container Registry `acrworkingsafe`
  - Azure SQL Server `srv-workingsafe` + DB `db_workingsafe`
  - (Opcional) primeiro container no ACI para teste.

- `script-infra-aci-deploy.sh`  
  Não precisa ser chamado manualmente – o conteúdo está copiado na task **Azure CLI** da Release.  
  Versão atual usada na Release (Bash):

  ```bash
  #!/bin/bash
  set -euo pipefail

  ACR_NAME="acrworkingsafe"
  ACR_LOGIN_SERVER="acrworkingsafe.azurecr.io"
  IMAGE_NAME="fiap/workingsafe-api"
  TAG="3"                         # tag fixa usada neste deploy
  ACI_NAME="aci-workingsafe"
  RESOURCE_GROUP="rg-workingsafe"
  LOCATION="eastus"
  PORT=8080

  echo "------------------------------------"
  echo "Iniciando deploy no Azure Container Instances"
  echo "Imagem: $ACR_LOGIN_SERVER/$IMAGE_NAME:$TAG"
  echo "------------------------------------"

  echo "Verificando se o repositório e a tag existem no ACR..."
  az acr repository show -n "$ACR_NAME" --repository "$IMAGE_NAME" >/dev/null
  if ! az acr repository show-tags -n "$ACR_NAME" --repository "$IMAGE_NAME" -o tsv | grep -x "$TAG" >/dev/null; then
    echo "❌ A tag '$TAG' não existe no repositório '$IMAGE_NAME'."
    exit 1
  fi
  echo "✅ Imagem encontrada no ACR!"

  echo "Realizando login no ACR..."
  az acr login --name "$ACR_NAME"

  ACR_USER=$(az acr credential show -n "$ACR_NAME" --query username -o tsv)
  ACR_PASS=$(az acr credential show -n "$ACR_NAME" --query "passwords[0].value" -o tsv)

  DNS_LABEL="${ACI_NAME}-$(date +%s)"

  echo "Removendo container anterior (se existir)..."
  az container delete --name "$ACI_NAME" --resource-group "$RESOURCE_GROUP" --yes --only-show-errors || true

  echo "Criando container Linux no ACI..."
  az container create \
    --name "$ACI_NAME" \
    --resource-group "$RESOURCE_GROUP" \
    --location "$LOCATION" \
    --image "$ACR_LOGIN_SERVER/$IMAGE_NAME:$TAG" \
    --cpu 1 \
    --memory 1.5 \
    --os-type Linux \
    --dns-name-label "$DNS_LABEL" \
    --ports "$PORT" \
    --protocol TCP \
    --restart-policy Always \
    --registry-login-server "$ACR_LOGIN_SERVER" \
    --registry-username "$ACR_USER" \
    --registry-password "$ACR_PASS"

  echo "✅ Deploy concluído com sucesso!"
  echo "Acesse sua aplicação em:"
  echo "➡️  http://${DNS_LABEL}.${LOCATION}.azurecontainer.io:${PORT}"
