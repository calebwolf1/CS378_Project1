#!/bin/bash

mkdir -p "bin"
javac -d "bin" src/*.java

if [ $? -ne 0 ]; then
    echo "Compilation failed. Exiting."
    exit 1
fi

# Iterate over each file in the "traces" directory
for file in traces/*; do
    java -cp "bin" Main "$(basename "$file")"
done