FROM maven:3.9.6-eclipse-temurin-21-alpine AS maven
# Imagem base: Maven 3.9.6 com Java 21 JDK, Alpine Linux
# Com apelido para melhor identificação e escrita

WORKDIR /app
# Cria pasta default /app e define como pasta de trabalho do estágio

COPY pom.xml .
COPY src ./src
# Copia o necessário do repositório (não copiar target pois será gerado depois, nem o .env).


RUN ["mvn", "package", "-DskipTests"]
# Comandos para compilação e geração do .jar
# Pula os testes pois um depende de um impementação futura dentro do docker

FROM eclipse-temurin:21-jre-alpine
# Inicia outro estágio

WORKDIR /app
# Cria pasta default /app e define como pasta de trabalho do estágio, cria-se outro porque é um disco separado do outro
# estágio

COPY --from=maven /app/target/jansen-bot-1.0.0.jar /app/app.jar
# Copia do estágio anterior o jar gerado

EXPOSE 8080
# Documenta que a app escuta na 8080. Não abre a porta: quem publica é o -p no docker run.


ENTRYPOINT ["java", "-jar", "app.jar"]
# Comando executado quando o container inicia (docker run): liga a aplicação com java -jar.
