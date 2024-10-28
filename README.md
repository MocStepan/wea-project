# Webová aplikace katalogu knih

## Struktura projektu
Webová aplikace je rozdělena do adresářů
- Frontend
- Backend


## Použité technologie a dúležité knihovny

### Angular (Frontend)
- Typescript
- Eslint
- Angular Material

### Spring boot s Kotlin (Backend)
- Ktlint
- Jsonwebtoken
- Springdoc-openapi
- Blaze-persistence
- Hibernate jpamodelgen
- jackson-module-kogera

### Ostatní
- PostgreSQL
- PgAdmin
- Docker


## Konfigurace pro Docker a Docker compose
- Konfigurace Docker
  - ./backend/Dockerfile
    - Alpine docker
  - ./frontend/Dockerfile
    - Gradle docker, openjdk
- Konfigurace Docker compsoe
  - ./docker-compose.yml 
  - zde se taky nachází konfigurace k PostgreSQL a PgAdmin
- Enviroment file .env
  - Obsahuje hodnoty pro sestaveni db, backend, pgadmin


## Zprovoznění aplikace lokálně
- Nastavte si v project structure pro backend JDK 20 (používám azul 20.0.1).
- Nainstalujte si postgresql s pgAdmin a vytvořte si databázi s jakýmkoliv názvem.
- Na backendu si vytvořte kopii souboru application-template.yml, který je v backend/config a pojmenujte ho application.yml. Přepište si název DB, jméno a heslo. Tento nový application.yml má profil localDev, který se zapíná na lokálním prostředí a je v gitignore.
- Přepište v resources/logback.xml název cesty pro LOGS_DIR z /var/logs na ./var/logs.
- Nyní by vám měl fungovat backend.
- Pro frontend si stáhněte node.js, pak stačí jen nainstalovat node_modules příkazem npm install.


## Návod na sestavení a spuštění aplikace
  - docker compose --env-file .env up --build


## Vystavené endpointy
- Swagger: http://wea.nti.tul.cz:8000/api/v1/docs/swagger-ui/index.html
- Endpoint pro příjem dat o knihách: http://wea.nti.tul.cz:8000/api/v1/book/import

## Architektura
- https://gitlab.tul.cz/stepan.moc/wea-project/-/tree/architecture?ref_type=heads

## Authors
Štěpán Moc, Lukáč Matěj


<h3 align="left">Languages and Tools:</h3>
<p> <a href="https://angular.io" target="_blank" rel="noreferrer"> <img src="https://angular.io/assets/images/logos/angular/angular.svg" alt="angular" width="40" height="40"/> </a> <a href="https://www.docker.com/" target="_blank" rel="noreferrer"> <img src="https://raw.githubusercontent.com/devicons/devicon/master/icons/docker/docker-original-wordmark.svg" alt="docker" width="40" height="40"/> </a> <a href="https://www.postgresql.org" target="_blank" rel="noreferrer"> <img src="https://raw.githubusercontent.com/devicons/devicon/master/icons/postgresql/postgresql-original-wordmark.svg" alt="postgresql" width="40" height="40"/> </a> <a href="https://www.typescriptlang.org/" target="_blank" rel="noreferrer"> <img src="https://raw.githubusercontent.com/devicons/devicon/master/icons/typescript/typescript-original.svg" alt="typescript" width="40" height="40"/> </a> </p>
