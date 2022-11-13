#Start with base image
FROM openjdk:11-slim as build

#who maintains the image
MAINTAINER almahari.com

COPY resources/vmware-gemfire-9.15.3.tgz vmware-gemfire-9.15.3.tgz

RUN tar -xvzf vmware-gemfire-9.15.3.tgz vmware-gemfire-9.15.3

ENV GEODE_HOME=vmware-gemfire-9.15.3

#Copy the jar file to the image
COPY target/geode-1.0-SNAPSHOT.jar geode-1.0-SNAPSHOT.jar

RUN ./vmware-gemfire-9.15.3/bin/gfsh version

#Execute
ENTRYPOINT ["java", "-jar", "/geode-1.0-SNAPSHOT.jar"]

EXPOSE 11235 1199 12480
