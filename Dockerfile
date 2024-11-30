# Usamos una imagen base de OpenJDK
FROM openjdk:17-jdk-slim AS build

# Instalamos Maven
RUN apt-get update && apt-get install -y maven

# Establecemos el directorio de trabajo
WORKDIR /app

# Copiamos el proyecto a la imagen
COPY . /app

# Ejecutamos Maven para empaquetar el proyecto y generar el .jar
RUN mvn clean package -DskipTests

# Utilizamos una imagen más liviana para el contenedor final
FROM openjdk:17-jdk-slim

# Establecemos el directorio de trabajo
WORKDIR /app

# Copiamos el archivo JAR generado desde el contenedor de construcción
COPY --from=build /app/target/Backend_Wakanda_Salud-0.0.1-SNAPSHOT.jar /app/backend_wakanda_salud.jar

# Exponemos el puerto 8761 para Eureka
EXPOSE 8080

# Definimos el comando para iniciar Eureka
ENTRYPOINT ["java", "-jar", "/app/backend_wakanda_salud.jar"]