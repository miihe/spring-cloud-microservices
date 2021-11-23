FROM openjdk:8-jdk-alpine
EXPOSE 8888
COPY build/libs/payment-service-1.0-SNAPSHOT.jar .
RUN apk add --no-cache bash
COPY wait-for.sh /wait-for.sh
RUN chmod +x /wait-for.sh
ENTRYPOINT ["/wait-for.sh", "deposit-service:9991", "--timeout=130", "--", "java", "-jar", "payment-service-1.0-SNAPSHOT.jar"]