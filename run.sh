#! /bin/bash

set -e

cd /work
caddy --conf ./Caddyfile --log stdout &
./gradlew run --console=plain