#!/bin/bash

echo 'Deploying latest Trainer Advisor version'
source ./secrets.sh
docker --context qyoga-prod compose -p qyoga -f ../qyoga/docker-compose.yml pull
docker --context qyoga-prod compose -p qyoga -f ../qyoga/docker-compose.yml up -d
