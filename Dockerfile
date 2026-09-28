FROM eclipse-temurin:21-jre-alpine
# Imagem base: Linux Alpine com Java 21 JRE


WORKDIR /app
#cria (se não existir) e entra na pasta /app dentro da imagem. Os comandos seguintes partem daqui.

COPY target/jansen-bot-1.0.0.jar app.jar
# copia o jar da minha máquina (target/) para dentro da imagem, com o nome app.jar, na pasta /app.


EXPOSE 8080
#documenta que a app escuta na 8080. Não abre a porta: quem publica é o -p no docker run.


ENTRYPOINT ["java", "-jar", "app.jar"]
# comando executado quando o container inicia (docker run): liga a aplicação com java -jar.
