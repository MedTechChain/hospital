package com.hospital.server;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class KeyService {

    private final HttpClient client;
    private static final String KEY_URL = "http://localhost:8000/api/key";

    public KeyService(HttpClient client) {
        this.client = client;
    }

    public String fetchKey() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(KEY_URL))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            return response.body();
        } else {
            System.err.println("Failed to retrieve Key. HTTP Status: " + response.statusCode());
            return null;
        }
    }
}
