#!/bin/bash

git submodule update --init --remote
cp -r BuildCraft/common/buildcraft src/main/java

cp -r BuildCraft/api/buildcraft/api src/main/java/buildcraft

cp -r BuildCraft/buildcraft_resources/assets src/main/resources/assets


