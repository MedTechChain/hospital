# MedTech Chain - Hospital Server

## Description

## Load Crypto

Before running the service (IDE or Docker), make sure to copy the crypto 
material required for the integration with Fabric infrastructure.

Use the `scripts/load-crypto.sh` script to automatically copy the crypto material.

## Build
```shell
gradle build
```

## Run in Docker

```shell
docker-compose --profile <default|light> -p medtechchain-hpt up -d --build
```