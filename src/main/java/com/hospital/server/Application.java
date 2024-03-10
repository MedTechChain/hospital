package com.hospital.server;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.hospital.server.generator.DeviceMetadataGenerator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {

	public static void main(String[] args) throws JsonProcessingException {
		SpringApplication.run(Application.class, args);

		// Generate metadata to simulate devices in the hospital
		DeviceMetadataGenerator generator = new DeviceMetadataGenerator();
		generator.generateAndSendPayloads("http://localhost:8080/api/devices");
	}
}
