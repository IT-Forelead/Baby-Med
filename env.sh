#!/bin/bash

export APP_MODE=DEV

export POSTGRES_HOST="localhost"
export POSTGRES_PORT=5432
export POSTGRES_USER="cieloassist"
export POSTGRES_PASSWORD="123"
export POSTGRES_DATABASE="cieloassist"
export POSTGRES_POOL_SIZE=1024

export HTTP_HOST="localhost"
export HTTP_PORT=9001

export HTTP_HEADER_LOG=false
export HTTP_BODY_LOG=false

export USERS_HTTP_PORT=8000

export USERS_RPC_PORT=10000
