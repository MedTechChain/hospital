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

    private final Random random = new Random();
    private final HttpClient client = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public void generateAndSendPayloads(String endpointUri) {
        while (true) {
            try {
                // Serialization into JSON using Jackson
                DeviceMetadata payload = new DeviceMetadata();

                // Generate a random UUID for each device/version combination
                payload.setUuid(UUID.randomUUID().toString());

                // Select a random version from the array
                payload.setVersion(versions[random.nextInt(versions.length)]);

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
