#!/bin/bash

echo "Starting Syamailcoin Node..."

cd bin
java -cp .:../lib/* org.syamailcoin.SyamailcoinNode
cd ..
