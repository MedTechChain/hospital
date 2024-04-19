# MedTech Chain - Hospital Server

## Description

## Load Crypto & Libs

Before running the service (IDE or Docker), make sure to copy the crypto 
material required for the integration with Fabric infrastructure.

Use the `scripts/copy-crypto.sh` script to copy the crypto material.
Use the `scripts/copy-protos.sh` script to copy the proto jar.

## Run in Docker

```shell
docker-compose --profile <default|light> -p medtechchain-hpt up -d --build
```