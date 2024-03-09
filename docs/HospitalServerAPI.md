# MedTech Chain Hospital Server API

Backend API for the Hospital Server.

### Add Device Metadata

`POST http://localhost:8080/api/devices`

The device metadata will be added to the blockchain.

#### Request Headers

| Header        | Value                                                                                                                                   |
|---------------|-----------------------------------------------------------------------------------------------------------------------------------------|
| Content-Type  | application/json                                                                                                                        |


#### Body

```json
{
  "version": "1.2.1"
}
```

#### Example

##### Request

```shell
curl --location 'http://localhost:8080/api/devices' \
--header 'Content-Type: application/json' \
--data-raw '{
    "version": "1.2.1"
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
curl --location 'GET http://localhost:8080/api/devices'
```

##### Response

`200 OK`

```json
[ {
    "version": "v1.3.1"
}, {
    "version": "v1.4.1"
}, {
    "version": "v1.3.0"
} ]
```
