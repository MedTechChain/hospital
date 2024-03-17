package com.hospital.server.generator;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hospital.server.model.DeviceMetadata;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.Random;
import java.util.UUID;

public class DeviceMetadataGenerator {

    private final String[] versions = {
            "v1.2.1",
            "v1.2.2",
            "v1.2.3",
            "v1.3.0",
            "v1.3.1",
            "v1.4.0",
            "v1.4.1",
            "v1.4.2",
            "v1.5.0",
            "v1.5.1"
    };

    private final HttpClient client;
    private final Random random;
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Constructor for DeviceMetadataGenerator.
     *
     * @param client    the HTTP client to use for sending requests
     * @param random    the random number generator for version selection
     */
    public DeviceMetadataGenerator(HttpClient client, Random random) {
        this.client = client;
        this.random = random;
    }

    /**
     * Generates and sends a specified number of device metadata payloads to the given endpoint URI.
     * Each payload is unique, with a randomly selected version and a new UUID.
     *
     * @param endpointUri       the URI of the endpoint to send payloads to
     * @param numberOfPayloads  the number of payloads to generate and send
     */
    public void generateAndSendPayloads(String endpointUri, int numberOfPayloads) {
        for (int i = 0; i < numberOfPayloads; i++) {
            try {
                // Serialization into JSON using Jackson
                // Generate a random UUID for each device/version combination
                String uuid = UUID.randomUUID().toString();

                // Select a random version from the array
                String version = versions[random.nextInt(versions.length)];

                DeviceMetadata payload = new DeviceMetadata(uuid, version);

                // Convert the DeviceMetadata object to a JSON string
                String json = objectMapper.writeValueAsString(payload);

                // Construct a POST HttpRequest with the JSON payload.
                HttpRequest postRequest = HttpRequest.newBuilder()
                        .uri(new URI(endpointUri))
                        .header("Content-Type", "application/json")
                        .POST(BodyPublishers.ofString(json))
                        .build();

                HttpResponse<String> response = client.send(postRequest, BodyHandlers.ofString());

                System.out.println("POST Response status code: " + response.statusCode());
                System.out.println("POST Response body: " + response.body());

                // Wait 1 second between requests
                Thread.sleep(1000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
