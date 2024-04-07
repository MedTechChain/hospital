package nl.medtechchain.hospital;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * The {@code KeyService} class is responsible for fetching encryption keys
 * from a specified key service URL.
 * <p>
 * It uses the {@link HttpClient} to make HTTP requests to the key service endpoint.
 * <p>
 * Note: This class is currently not used within the application. It has been
 * developed for potential future use or as a reference for implementing similar
 * service calls.
 */
public class KeyService {

    private final HttpClient client;
    private static final String KEY_URL = "http://localhost:8000/api/key";

    /**
     * Constructs a {@code KeyService} with a specified {@link HttpClient}.
     *
     * @param client The {@link HttpClient} instance used for making HTTP requests.
     */
    public KeyService(HttpClient client) {
        this.client = client;
    }

    /**
     * Fetches an encryption key from the key service.
     * <p>
     * This method sends a GET request to the key service URL and expects to receive
     * the encryption key as a response. It returns the key if the request is successful
     * (HTTP status code 200) or {@code null} if the request fails.
     *
     * @return The fetched encryption key as a {@code String} if the request is successful;
     *         {@code null} otherwise.
     * @throws IOException if an I/O error occurs when sending or receiving the HTTP request.
     * @throws InterruptedException if the operation is interrupted.
     */
    public String fetchKey() throws IOException, InterruptedException {
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
