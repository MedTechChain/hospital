package com.hospital.server;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.hospital.server.generator.DeviceMetadataGenerator;

import java.io.IOException;
import java.net.http.HttpClient;
import java.util.Random;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// TODO: rename package to nl.tudelft.medtechchain
// TODO: Github workflow
// TODO: lombok java
// TODO: use application.properties as much as possible
// TODO: comments for complicated parts

@SpringBootApplication
public class Application {

	public static void main(String[] args) throws JsonProcessingException {
		SpringApplication.run(Application.class, args);

		// Generate metadata to simulate devices in the hospital
		// TODO: ThreadLocal current
		Random random = new Random();
		HttpClient client = HttpClient.newHttpClient();

		// Fetch an encryption key from the trusted third party
		KeyService ks = new KeyService(client);
		try {
			String key = ks.fetchKey();
			if (key == null || key.isBlank()) {
				throw new RuntimeException("No Key received from the server.");
			}
			System.out.println("Received Key: " + key);
		} catch (IOException | InterruptedException e) {
			throw new RuntimeException("Failed to retrieve Key", e);
		}

		DeviceMetadataGenerator generator = new DeviceMetadataGenerator(client, random);

		// For testing, it is better to put this lower unless you want to wait 10 hours.
		int numberOfPayloads = 1;
		generator.generateAndSendPayloads("http://localhost:8080/api/devices", numberOfPayloads);
	}
}
