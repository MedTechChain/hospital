package com.hospital.server;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.hospital.server.generator.DeviceMetadataGenerator;

import java.io.IOException;
import java.net.http.HttpClient;
import java.util.Random;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// TODO: rename package to nl.medtechchain
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

		DeviceMetadataGenerator generator = new DeviceMetadataGenerator(client, random);

		generator.generateAndSendPayloads("http://localhost:8080/api/devices/portable");
	}
}
