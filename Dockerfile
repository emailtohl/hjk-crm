FROM java
MAINTAINER emailtohl@163.com
WORKDIR /app/hjk-crm
COPY target/hjk-crm-1.0.0-SNAPSHOT.jar /app/hjk-crm/
VOLUME ["/var/hjk/logs","/var/hjk/luceneindex"]
EXPOSE 8080
CMD ["java","-jar","/app/hjk-crm/hjk-crm-1.0.0-SNAPSHOT.jar","--spring.profiles.active=prod"]