# MedTech Chain Hospital Server API

Backend API for the Hospital Server managing device metadata and interactions with the blockchain.

## Endpoints

### Add Device Metadata

Adds device metadata to the blockchain.

- **POST** `http://localhost:8080/api/devices`

#### Request Headers

| Header        | Value            |
|---------------|------------------|
| Content-Type  | application/json |

#### Request Body

Include the `uuid` and `version` of the device in the request body.

```json
{
  "uuid": "123e4567-e89b-12d3-a456-426614174000",
  "version": "v1.2.1"
}
```

#### Example

##### Request

```shell
curl --location --request POST 'http://localhost:8080/api/devices' \
--header 'Content-Type: application/json' \
--data-raw '{
    "uuid": "123e4567-e89b-12d3-a456-426614174000",
    "version": "v1.2.1"
}'
```

##### Response

`201 CREATED`

This request does not return any response body.

-----

### Get All Device Metadata

`GET http://localhost:8080/api/devices`

#### Request Headers

`None`

#### Example

##### Request

```shell
curl --location --request GET 'http://localhost:8080/api/devices'
```

##### Response

`200 OK`

```json
[
  {
    "uuid": "ff36a9f8-625f-452d-93d9-f316f0cf9b60",
    "version": "v1.3.1"
  },
  {
    "uuid": "2b003c7c-7c82-43e7-a949-4f236fd6db6e",
    "version": "v1.4.1"
  },
  {
    "uuid": "e63a30a6-b891-46e2-b0b6-c08831785082",
    "version": "v1.3.0"
  }
]
```
