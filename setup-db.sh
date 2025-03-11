#!/bin/bash

database_name=${1:-bookingDB}
echo "Using database: ${database_name}"

container_name="booking-db-container"
container_port=5432

# check container is running
function container_running() {
  docker ps --filter "name=${container_name}" --format '{{.ID}}' | grep -q .
}

# check container exists
function container_exists() {
  docker ps -a --filter "name=${container_name}" --format '{{.ID}}' | grep -q .
}

# check container
if container_exists; then
    echo "Container exists."
    if container_running; then
        echo "Container is already running."
    else
        echo "Starting container..."
        docker start ${container_name}
        sleep 5
    fi
else
    echo "Starting new PostGIS container..."
    docker run -e "POSTGRES_PASSWORD=password" \
               -p ${container_port}:5432 \
               --name ${container_name} -d postgis/postgis:16-3.5-alpine

    echo "Waiting for PostgreSQL to be ready..."
    for i in {1..10}; do
        sleep 2
        if docker exec ${container_name} pg_isready -U postgres &>/dev/null; then
            echo "PostgreSQL is ready!"
            break
        fi
        echo -n "."
    done
fi

if ! container_running; then
    echo "PostGIS container could not be started. Is there another container using the port?"
    exit 1
fi

# check database existence
db_exists=$(docker exec ${container_name} su postgres -c "psql -tAc \"SELECT 1 FROM pg_database WHERE datname = '${database_name}';\"")

if [[ "$db_exists" != "1" ]]; then
    echo "Creating database: ${database_name}"
    docker exec ${container_name} su postgres -c "createdb ${database_name} -U postgres"
else
    echo "Database ${database_name} already exists."
fi

# check role rds_superuser exists
role_exists=$(docker exec ${container_name} su postgres -c "psql -tAc \"SELECT 1 FROM pg_roles WHERE rolname='rds_superuser';\"")

if [[ "$role_exists" != "1" ]]; then
    echo "Creating role: rds_superuser"
    docker exec ${container_name} su postgres -c "psql -c 'CREATE ROLE rds_superuser WITH SUPERUSER;'"
else
    echo "Role rds_superuser already exists."
fi

#echo "run flywayMigrate on ${database_name}"
#./gradlew "-Dflyway.url=jdbc:postgresql://localhost:${container_port}/${database_name}" db:flyMigrate
