# hjk crm

## deploy

* docker build -t hjk-crm .

* docker build -t hjk-crm-front .

* docker run --name postgres --restart always -d -e POSTGRES_USER=postgres -e POSTGRES_PASSWORD=123456 -p 5432:5432 -v /var/hjk/db/pgdata:/var/lib/postgresql/data postgres

* docker run --name hjk-crm --restart always -d -p 8080:8080 --link postgres:postgres -v /var/hjk/logs:/var/hjk/logs -v /var/hjk/luceneindex:/var/hjk/luceneindex hjk-crm

* docker run --name hjk-crm-front --restart always -d -p 80:80 hjk-crm-front


## create a database manually

* docker exec -it postgres /bin/bash

* su - postgres

* psql

* postgres=# CREATE database hjkcrm owner postgres;

* postgres=# \q

* exit