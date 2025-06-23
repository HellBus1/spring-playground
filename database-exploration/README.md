# Transactional Exploration

```sh
docker run --name spring-playground-database \
  -e POSTGRES_USER=monitoring1 \
  -e POSTGRES_PASSWORD=passw0rd \
  -p 5432:5432 \
  -d postgres:15.13-bullseye
```

```sh
export DB_NAME=database-exploration
export DB_USERNAME=monitoring1
export DB_PASSWORD=passw0rd
```