FROM openjdk:8-alpine

ENV SERVER_PORT=8054
ENV BASE_DIR=/var/run/pacs-integration
ENV CONTEXT_PATH=/pacs-integration
ENV WAR_DIRECTORY=/var/run/pacs-integration/pacs-integration
ENV SERVER_OPTS="-Xms512m -Xmx1024m -XX:PermSize=256m -XX:MaxPermSize=512m"
ENV DEBUG_OPTS="-agentlib:jdwp=transport=dt_socket,address=8000,server=y,suspend=n"
ENV PACS_DB="bahmni_pacs"
ENV POSTGRES_USER="postgres"

RUN mkdir -p ${WAR_DIRECTORY}

COPY package/resources/core-1.0-SNAPSHOT.jar /opt/pacs-integration/lib/pacs-integration.jar
COPY pacs-integration-webapp/target/pacs-integration.war /etc/pacs-integration/pacs-integration.war

RUN cd ${WAR_DIRECTORY} && jar xvf /etc/pacs-integration/pacs-integration.war

COPY package/start.sh start.sh
RUN chmod +x start.sh
CMD [ "./start.sh" ]