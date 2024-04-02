package com.hospital.server.generator;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.Random;
import java.util.UUID;

import nl.medtechchain.protos.devicemetadata.*;
import com.google.protobuf.util.JsonFormat;

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
     */
    public void generateAndSendPayloads(String endpointUri) {
        try {
            EncryptedPortableDeviceMetadata payload = EncryptedPortableDeviceMetadata.newBuilder()
                    .setUdi(UUID.randomUUID().toString())
                    .setOperatingSystem("Android")
                    .setOperatingSystemVersion(versions[random.nextInt(versions.length)])
                    .setAquiredPrice("1000")
                    .setUsageFrequency(EncryptedFrequency.newBuilder()
                            .setValue(1.0)
                            .setUnit(FrequencyUnit.TIMES_PER_DAY)
                            .build())
                    .build();

            // Convert the DeviceMetadata object to a JSON string;
            String jsonPayload = JsonFormat.printer().print(payload);
            System .out .println (jsonPayload);

            // Construct a POST HttpRequest with the JSON payload.
            HttpRequest postRequest = HttpRequest.newBuilder()
                    .uri(new URI(endpointUri))
                    .header("Content-Type", "application/json")
                    .POST(BodyPublishers.ofString(jsonPayload))
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
