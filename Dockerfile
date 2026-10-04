FROM maven:3.9.6-eclipse-temurin-21-alpine AS maven
#imagem base: Maven 3.9.6 com Java 21 JDK, Alpine Linux
#Com apelido para melhor identificação e escrita
WORKDIR /app
#Cria pasta default /app e define como pasta de trabalho do estágio
COPY pom.xml .
COPY src ./src
#copia o necessário do repo


RUN ["mvn", "package", "-DskipTests"]

#Comandos para comilação e geração do .jar

FROM eclipse-temurin:21-jre-alpine
#inicia outro estágio

WORKDIR /app
#Cria pasta default /app e define como pasta de trabalho do estágio

COPY --from=maven /app/target/jansen-bot-1.0.0.jar /app/app.jar
# copia do container anterior o jar gerado

EXPOSE 8080
#documenta que a app escuta na 8080. Não abre a porta: quem publica é o -p no docker run.


ENTRYPOINT ["java", "-jar", "app.jar"]
# comando executado quando o container inicia (docker run): liga a aplicação com java -jar.
