FROM gradle:jdk20 AS build

WORKDIR /app

COPY . .

RUN gradle clean build -x test

FROM tomcat:latest

ENV SPRING_PROFILES_ACTIVE=prod

RUN mkdir /var/logs
RUN chmod 755 /var/logs

COPY --from=build /app/build/libs/backend-0.0.1-SNAPSHOT.war /usr/local/tomcat/webapps/ROOT.war

CMD ["catalina.sh", "run"]
