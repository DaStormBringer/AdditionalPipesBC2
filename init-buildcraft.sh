#!/bin/bash

git submodule update --init
cp -r BuildCraft/common/buildcraft src/main/java

cp -r BuildCraft/api/buildcraft/api src/main/java/buildcraft

