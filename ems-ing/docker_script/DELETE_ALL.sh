. ./SET_ENV

echo 'stop docker container'
docker stop ${DOCKER_APP}

echo 'delete docker container'
docker rm ${DOCKER_APP}

echo 'delete docker image'
docker rmi ${APP_IMAGE}

echo 'delete docker network'
docker network rm ${APP_NETWORK}