#!/bin/bash

echo "Starting Syamailcoin Node..."

CLASSPATH="bin:lib/bcprov-jdk18on-1.78.1.jar:lib/bcpkix-jdk18on-1.78.1.jar:lib/bcutil-jdk18on-1.78.1.jar:lib/gson-2.10.1.jar:lib/Java-WebSocket-1.5.7.jar:lib/slf4j-api-2.0.9.jar:lib/slf4j-simple-2.0.9.jar"

java -cp "$CLASSPATH" Main
