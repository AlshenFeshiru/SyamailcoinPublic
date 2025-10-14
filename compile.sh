#!/bin/bash

echo "Compiling Syamailcoin..."

mkdir -p lib data

echo "Building with Maven (includes all Java sources)..."
mvn clean compile

if [ $? -eq 0 ]; then
    echo "Maven compilation successful!"
else
    echo "Maven compilation failed!"
    exit 1
fi

echo "Compiling C++ SAI-288..."
cd src/cpp
g++ -std=c++11 -fPIC -shared -o ../../lib/libsai288.so SAI288.cpp
cd ../..

echo "Compiling Rust SAI-288..."
cd src/rust
rustc --crate-type=cdylib -o ../../lib/libsai288rust.so SAI288.rs
cd ../..

echo "All compilations complete!"
