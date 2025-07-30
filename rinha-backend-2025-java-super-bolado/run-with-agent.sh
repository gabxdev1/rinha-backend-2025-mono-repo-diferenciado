#!/bin/bash



# Limpa e empacota o projeto para garantir que o JAR está atualizado
# e que as dependências estão no diretório target/dependency/
mvn clean package dependency:copy-dependencies

# Define o classpath incluindo o JAR do seu projeto e todas as suas dependências
# O comando `mvn dependency:build-classpath` pode ser útil para construir isso dinamicamente,
# mas para um script, podemos inferir o caminho comum de dependências.
# Assumindo que o dependency:copy-dependencies colocará tudo em target/dependency
CP=$(echo target/dependency/*.jar | tr ' ' ':')
YOUR_APP_JAR="target/rinha-backend-2025-java-super-bolado-1.0.0.jar"

java -agentlib:native-image-agent=config-output-dir=src/main/resources/META-INF/native-image \
     -cp "${YOUR_APP_JAR}:${CP}" \
     br.com.gabxdev.RinhaBackend2025JavaApplication