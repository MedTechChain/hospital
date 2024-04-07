package nl.medtechchain.hospital.generator;

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

/**
 * Generates and sends simulated device metadata payloads for healthcare devices.
 * <p>
 * This class supports generating metadata for both portable and wearable devices,
 * each with a unique identifier and random attributes. The metadata is sent to a
 * specified server endpoint in JSON format. The class leverages Google Protocol Buffers
 * for structured data representation and Java's HTTPClient for network communication.
 */
public class DeviceMetadataGenerator {

    private final String[] versions;
    private final String[] manufacturers;
    private final HttpClient client;
    private final ThreadLocalRandom random;

    /**
     * Initializes a new instance of the {@code DeviceMetadataGenerator} class.
     *
     * @param client The {@link HttpClient} used for sending HTTP requests.
     * @param random The {@link ThreadLocalRandom} used for generating random device attributes.
     */
    public DeviceMetadataGenerator(HttpClient client, ThreadLocalRandom random) {
        this.client = client;
        this.random = random;

        this.versions = new String[]{
                "v1.2.1", "v1.2.2", "v1.2.3", "v1.3.0", "v1.3.1",
                "v1.4.0", "v1.4.1", "v1.4.2", "v1.5.0", "v1.5.1"
        };
        this.manufacturers = new String[]{
                "MEDITECH", "HEALTHCORP", "LIFEINSTRUMENTS", "GLOBALMED"
        };
    }

    /**
     * Continuously generates and sends device metadata payloads to the specified endpoint.
     * <p>
     * The method alternates between generating payloads for portable and wearable devices,
     * selecting random attributes for each. It then serializes the payload to JSON and
     * sends it to the specified endpoint URI. The process repeats indefinitely, with a
     * delay between each request.
     * <p>
     * Note: This method runs in an infinite loop and must be manually stopped.
     *
     * @param endpointUri The URI of the endpoint to which the payloads are sent.
     */
    public void generateAndSendPayloads(String endpointUri) {
        while (true) {
            String endpoint = endpointUri;
            // Random data
            int deviceType = random.nextInt(2);
            int delay = random.nextInt(1, 6);
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

                // Wait 1-5 seconds between requests
                Thread.sleep(1000L * delay);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
