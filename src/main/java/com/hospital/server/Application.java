package com.hospital.server;

import com.fasterxml.jackson.core.JsonProcessingException;
import nl.medtechchain.hospital.generator.DeviceMetadataGenerator;

import java.net.http.HttpClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import java.util.concurrent.ThreadLocalRandom;

// TODO: rename package to nl.medtechchain
// TODO: Github workflow
// TODO: lombok java
// TODO: use application.properties as much as possible
// TODO: comments for complicated parts

// TODO: send 1 between 0 and 60 seconds randomly

@SpringBootApplication
public class Application {

	public static void main(String[] args) throws JsonProcessingException {
		SpringApplication.run(Application.class, args);

		// Generate metadata to simulate devices in the hospital
		ThreadLocalRandom random = ThreadLocalRandom.current();
		HttpClient client = HttpClient.newHttpClient();

		DeviceMetadataGenerator generator = new DeviceMetadataGenerator(client, random);

		generator.generateAndSendPayloads("http://localhost:8080/api/devices");
	}
}
