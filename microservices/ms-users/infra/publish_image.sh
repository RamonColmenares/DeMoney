#!/bin/bash

# Parámetros
CI_REGISTRY=registry-grupo5.local
NETWORK_NAME=networkg5
USERREG=grupo5
PASSREG=grupo5
REPOSITORY=ms-users
TAG=latest
DOCKER_COMPOSE_FILE=docker-compose.yaml
DEPLOY_NAME=grupo5_deploy

# Obtener la versión
VERSION="0.0.1"

# Función para ejecutar comandos y verificar su resultado
execute_fn() {
  local TASK_NAME=$1
  local COMMAND=$2

  echo "$COMMAND"
  if $COMMAND; then
    echo -e "\nOK. La tarea se ejecutó exitosamente: $TASK_NAME.\n"
  else
    echo -e "\nERROR. La tarea no se pudo ejecutar: $TASK_NAME.\n"
    exit 1
  fi
}

# Autenticarse en el registro
echo 'Iniciando sesión en el registro...'
docker login -u $USERREG -p $PASSREG $CI_REGISTRY || true

# Descargar imagen
echo "Descargando imagen $REPOSITORY:$TAG..."
docker pull $CI_REGISTRY/$REPOSITORY:$TAG

# Crear red si no existe
#echo "Creando red $NETWORK_NAME si no existe..."
#docker network create -d overlay --attachable $NETWORK_NAME || true

# Publicar imagen
echo -e "\nPublicando versión $VERSION..."
execute_fn "Publicar imagen" "docker stack deploy --compose-file $DOCKER_COMPOSE_FILE --with-registry-auth $DEPLOY_NAME"


