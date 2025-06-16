# Usa una imagen base de Java
FROM eclipse-temurin:17-jdk-alpine

# Directorio de trabajo dentro del contenedor
WORKDIR /app

# Copia el .jar al contenedor
COPY target/softskills-0.0.1-SNAPSHOT.jar softskills.jar

# Exponer el puerto (ajusta si tu app usa otro)
EXPOSE 8080

# Comando para arrancar la app
ENTRYPOINT ["java", "-jar", "softskills.jar"]
