#!/bin/bash

echo "Compiling Syamailcoin Node..."

CLASSPATH="lib/bcprov-jdk18on-1.78.1.jar:lib/bcpkix-jdk18on-1.78.1.jar:lib/bcutil-jdk18on-1.78.1.jar:lib/gson-2.10.1.jar:lib/Java-WebSocket-1.5.7.jar:lib/slf4j-api-2.0.9.jar:lib/slf4j-simple-2.0.9.jar"

mkdir -p bin

find src -name "*.java" > sources.txt

javac -d bin -cp "$CLASSPATH" @sources.txt

if [ $? -eq 0 ]; then
    echo "Compilation successful!"
    rm sources.txt
else
    echo "Compilation failed!"
    rm sources.txt
    exit 1
fi
