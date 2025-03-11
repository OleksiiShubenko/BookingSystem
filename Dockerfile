FROM openjdk:17-oracle

COPY build/libs/booking-system-1.0.jar /opt/app.jar

ENTRYPOINT exec java $JAVA_OPTS -jar /opt/app.jar
