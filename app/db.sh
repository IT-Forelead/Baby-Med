#!/bin/bash
source ../.env
docker rm -f babymed_db
docker-compose up -d postgres
