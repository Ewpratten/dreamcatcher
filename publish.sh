#! /bin/bash

docker build -t dreamcatcher_app .
docker tag dreamcatcher_app ewpratten/dreamcatcher_app:latest
docker push ewpratten/dreamcatcher_app:latest