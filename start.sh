# rebuild containers
# docker compose -f docker-start-gw.yml build
# docker compose -f contextnet-stationary.yml build

# starts gw, kafka and zookeeper in the background
docker compose -f docker-start-gw.yml up -d

# starts pn and gd, showing logs in stdout
docker compose -f contextnet-stationary.yml up
