#!/bin/bash
source ../env.sh
docker-compose stop postgres
docker-compose up -d postgres
