#!/usr/bin/env bash

export DEST=src/main/resources/META-INF/resources
./node_modules/.bin/ng build --prod --base-href "."
mkdir -p $DEST
rm -Rf ${DEST}
cp -R dist/* ${DEST}
