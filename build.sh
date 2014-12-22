#!/usr/bin/env bash
echo "THIS SCRIPT IS NOT PATH AGNOSTIC!"

rm -rf build
mkdir build

for i in directory originator node quote
do
        echo "Building $i..."
        cd $i
        activator dist
        mv target/universal/*.zip ../build
        activator clean
        cd ..
        echo "...done"
done

echo "Copying node binary to provisioning folder"
cp build/node* provision/node-binary.zip
