# hjk crm

## create a database manually

* docker run --name postgres --restart always -d -e POSTGRES_USER=postgres -e POSTGRES_PASSWORD=123456 -p 5432:5432 -v /var/hjk/db/pgdata:/var/lib/postgresql/data postgres

* docker exec -it postgres /bin/bash

* su - postgres

* psql

* postgres=# CREATE database hjkcrm owner postgres;

* postgres=# \q

* exit

## deploy

* docker run --name maven -v /opt/project:/project -v /var/repository:/root/.m2/repository -it maven /bin/bash

After the project builded in docker, exit from docker, and change directory /opt/project/hjk-crm and execute docker build: 

* docker build -t hjk-crm .

* docker run --name hjk-crm --restart always -d -p 8081:8080 --link postgres:postgres -v /opt/hjk/backend/logs:/var/hjk/logs -v /opt/hjk/backend/luceneindex:/var/hjk/luceneindex hjk-crm

copy static resouces to /opt/www/hjkcrm

* docker run -d --name nginx -v /opt/www/:/usr/share/nginx/html -p 80:80 nginx

