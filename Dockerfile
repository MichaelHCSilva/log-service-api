# Usando a imagem do Java OpenJDK 21
FROM openjdk:21-jdk-slim

# Diretório de trabalho
WORKDIR /app

# Copiar o arquivo JAR para dentro da imagem
COPY target/log-service.jar app.jar

# Definir a variável de ambiente para garantir que o .env será carregado
COPY .env .env

# Definir o ponto de entrada para a aplicação Spring Boot
ENTRYPOINT ["java", "-jar", "app.jar"]
