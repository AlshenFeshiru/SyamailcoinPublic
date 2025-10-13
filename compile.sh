#!/bin/bash

echo "Compiling Syamailcoin Node..."

mkdir -p bin
mkdir -p lib
mkdir -p data

cd src/java
javac -d ../../bin -cp ../../lib/*:. *.java org/syamailcoin/*.java org/syamailcoin/core/*.java 2>&1 | grep -v "^Note:"
cd ../..

if [ $? -eq 0 ]; then
    echo "Compilation successful!"
else
    echo "Compilation failed!"
    exit 1
fi

cd src/cpp
g++ -std=c++11 -fPIC -shared -o ../../lib/libsai288.so SAI288.cpp
cd ../..

cd src/go
go build -buildmode=c-shared -o ../../lib/libsai288go.so SAI288.go
cd ../..

cd src/rust
rustc --crate-type=cdylib -o ../../lib/libsai288rust.so SAI288.rs
cd ../..

echo "All SAI-288 implementations compiled!"
