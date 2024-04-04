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
import java.util.concurrent.ThreadLocalRandom;

import com.google.protobuf.MessageOrBuilder;
import nl.medtechchain.protos.devicemetadata.*;
import com.google.protobuf.util.JsonFormat;
import nl.medtechchain.protos.query.Speciality;

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

    private final String[] manufacturers = {
            "MEDITECH",
            "HEALTHCORP",
            "LIFEINSTRUMENTS",
            "GLOBALMED"
    };

    private final HttpClient client;
    private final ThreadLocalRandom random;
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Constructor for DeviceMetadataGenerator.
     *
     * @param client    the HTTP client to use for sending requests
     * @param random    the random number generator for version selection
     */
    public DeviceMetadataGenerator(HttpClient client, ThreadLocalRandom random) {
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
        while (true) {
            String endpoint = endpointUri;
            // Random data
            int deviceType = random.nextInt(2);
            int delay = random.nextInt(1, 5);
            int speciality = random.nextInt(4);
            int manufacturer = random.nextInt(4);
            int operatingSystem = random.nextInt(1, 11);
            int version = random.nextInt(versions.length);
            int price = random.nextInt(100,1001);
            int usageFrequency = random.nextInt(1,11);
            MessageOrBuilder payload = null;
            try {
                if (deviceType == 0) { // Portable device
                    payload = EncryptedPortableDeviceMetadata.newBuilder()
                            .setUdi(UUID.randomUUID().toString())
                            .setMedicalSpeciality(Speciality.forNumber(speciality).name())
                            .setManufacturerName(manufacturers[manufacturer])
                            .setOperatingSystem(String.valueOf(operatingSystem))
                            .setOperatingSystemVersion(versions[version])
                            .setAquiredPrice(String.valueOf(price))
                            .setUsageFrequency(EncryptedFrequency.newBuilder()
                                    .setValue(usageFrequency)
                                    .setUnit(FrequencyUnit.TIMES_PER_DAY)
                                    .build())
                            .build();
                    endpoint = endpoint.concat("/portable");
                } else { // Wearable device
                    payload = EncryptedWearableDeviceMetadata.newBuilder()
                            .setUdi(UUID.randomUUID().toString())
                            .setMedicalSpeciality(Speciality.forNumber(speciality).name())
                            .setManufacturerName(manufacturers[manufacturer])
                            .setOperatingSystem(String.valueOf(operatingSystem))
                            .setOperatingSystemVersion(versions[version])
                            .setAquiredPrice(String.valueOf(price))
                            .setDataSyncFrequency(EncryptedFrequency.newBuilder()
                                    .setValue(usageFrequency)
                                    .setUnit(FrequencyUnit.TIMES_PER_DAY)
                                    .build())
                            .build();
                    endpoint = endpoint.concat("/wearable");
                }

                // Convert the DeviceMetadata object to a JSON string;
                String jsonPayload = JsonFormat.printer().print(payload);
                System .out .println (jsonPayload);

                // Construct a POST HttpRequest with the JSON payload.
                HttpRequest postRequest = HttpRequest.newBuilder()
                        .uri(new URI(endpoint))
                        .header("Content-Type", "application/json")
                        .POST(BodyPublishers.ofString(jsonPayload))
                        .build();

                HttpResponse<String> response = client.send(postRequest, BodyHandlers.ofString());

                System.out.println("POST Response status code: " + response.statusCode());
                System.out.println("POST Response body: " + response.body());

                // Wait 1 second between requests
                Thread.sleep(1000L * delay);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
