package com.hospital.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.Random;

@SpringBootApplication
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	// this.constract.submitTransaction("CreateWatch", uuid.random.String, version);
		// Array of predefined versions
		String[] versions = {
				"{ \"version\": \"v1.2.1\" }",
				"{ \"version\": \"v1.2.2\" }",
				"{ \"version\": \"v1.2.3\" }",
				"{ \"version\": \"v1.3.0\" }",
				"{ \"version\": \"v1.3.1\" }",
				"{ \"version\": \"v1.4.0\" }",
				"{ \"version\": \"v1.4.1\" }",
				"{ \"version\": \"v1.4.2\" }",
				"{ \"version\": \"v1.5.0\" }",
				"{ \"version\": \"v1.5.1\" }"
		};

		Random random = new Random();

		while (true) {
			// Select a random version from the array
			String json = versions[random.nextInt(versions.length)];

			// Example POST request
			try {
				HttpRequest postRequest = HttpRequest.newBuilder()
						.uri(new URI("http://localhost:8080/api/devices"))
						.header("Content-Type", "application/json")
						.POST(BodyPublishers.ofString(json))
						.build();

				HttpClient client = HttpClient.newHttpClient();
				HttpResponse<String> postResponse = client.send(postRequest, BodyHandlers.ofString());

				System.out.println("POST Response status code: " + postResponse.statusCode());
				System.out.println("POST Response body: " + postResponse.body());
			} catch (Exception e) {
				e.printStackTrace();
			}

			// wait 1 second between requests
			try {
				Thread.sleep(1000);
			} catch (InterruptedException ie) {
				Thread.currentThread().interrupt();
			}
		}
	}
}
