package nl.medtechchain.hospital;

import com.fasterxml.jackson.core.JsonProcessingException;
import nl.medtechchain.hospital.generator.DeviceMetadataGenerator;

import java.net.http.HttpClient;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import java.util.concurrent.ThreadLocalRandom;


// TODO: Implement Github workflow for CI/CD.
// TODO: Integrate Lombok for boilerplate code reduction.
// TODO: Utilize application.properties for configuration.

/**
 * Entry point for the hospital mock server.
 */
@SpringBootApplication
public class Application {

	/**
	 * Main method to run the Spring Boot application.
	 * <p>
	 * Initializes the application, setting up the environment for simulating
	 * hospital devices. It generates random device metadata and sends this data
	 * to a specified API endpoint.
	 *
	 * @param args Command line arguments passed to the application.
	 * @throws JsonProcessingException If there is an issue processing JSON during
	 *                                 device metadata generation or sending.
	 */
	public static void main(String[] args) throws JsonProcessingException {
		SpringApplication.run(Application.class, args);

		// Generate metadata to simulate devices in the hospital
		ThreadLocalRandom random = ThreadLocalRandom.current();
		HttpClient client = HttpClient.newHttpClient();

		DeviceMetadataGenerator generator = new DeviceMetadataGenerator(client, random);

		generator.generateAndSendPayloads("http://localhost:8080/api/devices");
	}
}
