# Imagem base do Java
FROM openjdk:21-jdk-slim

# Define o diretório de trabalho
WORKDIR /app

# Copia o arquivo JAR da aplicação para o container
COPY target/log-service.jar app.jar

# Expõe a porta que sua API usa
EXPOSE 8080

# Comando para rodar a aplicação
ENTRYPOINT ["java", "-jar", "app.jar"]
