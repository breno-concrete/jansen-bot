FROM eclipse-temurin:21-jre-alpine
# a imagem da JRE qeu será "rodada" qunado esse container subir



WORKDIR /app
#odiretorio respectivo odne os próximo comandos devem ser executados

COPY target/jansen-bot-1.0.0.jar app.jar
#copiar a pasta do caminho para dentro de app (onde o container vai rodar a aplicação)
EXPOSE 8080
#para fins de documentação, a porta que será usada
ENTRYPOINT ["java", "-jar", "app.jar"]
# comando a serem executados deposi do build do container
