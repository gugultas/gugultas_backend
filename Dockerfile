#
# Build stage
#
FROM maven:3.8.5-openjdk-17 AS build

COPY . .

RUN mvn clean package -DskipTests

#
# Package stage
#
FROM openjdk:17-slim

COPY --from=build /target/backend-0.0.1-SNAPSHOT.jar backend.jar

COPY --from=build ./prod-uploads prod-uploads

# ENV PORT=8080
EXPOSE 8080

ENTRYPOINT ["java","-jar","backend.jar"]
