#
# Build stage
#
FROM maven:3.8.5-openjdk-17 AS build

ENV PROD_DB_HOST=${PROD_DB_HOST}
ENV PROD_DB_PORT=${PROD_DB_PORT}
ENV PROD_DB_NAME=${PROD_DB_NAME}
ENV PROD_DB_USERNAME=${PROD_DB_USERNAME}
ENV PROD_DB_PASSWORD=${PROD_DB_PASSWORD}
ENV GMAIL_USERNAME=${GMAIL_USERNAME}
ENV GMAIL_PASSWORD=${GMAIL_PASSWORD}
ENV REFRESH_COOKIE_NAME=${REFRESH_COOKIE_NAME}
ENV JWT_REFRESH_TOKEN_SECRET=${JWT_REFRESH_TOKEN_SECRET}
ENV REFRESH_TOKEN_EXPIRATION=${REFRESH_TOKEN_EXPIRATION}
ENV JWT_ACCESS_TOKEN_SECRET=${JWT_ACCESS_TOKEN_SECRET}
ENV ACCESS_TOKEN_EXPIRATION=${ACCESS_TOKEN_EXPIRATION}
ENV JWT_ACTIVATION_TOKEN_SECRET=${JWT_ACTIVATION_TOKEN_SECRET}
ENV ACTIVATION_TOKEN_EXPIRATION=${ACTIVATION_TOKEN_EXPIRATION}
ENV CLIENT_URI=${CLIENT_URI}
ENV PROFILE=${PROFILE}

COPY . .

RUN mvn clean package -DskipTests

#
# Package stage
#
FROM openjdk:17-slim

COPY --from=build /target/backend-0.0.1-SNAPSHOT.jar backend.jar

# ENV PORT=8080
EXPOSE 8080

ENTRYPOINT ["java","-jar","backend.jar"]
