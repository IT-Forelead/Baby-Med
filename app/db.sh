#!/bin/bash
source ../env.sh
docker rm -f babymed_db
docker-compose up -d postgres
