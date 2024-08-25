package nl.medtechchain.hospital.service.encryption;

import nl.medtechchain.hospital.service.encryption.paillier.dto.PaillierEncryptRequest;
import nl.medtechchain.hospital.service.encryption.paillier.dto.PaillierEncryptResponse;
import org.springframework.web.client.RestClient;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;

import static org.springframework.http.MediaType.APPLICATION_JSON;

public class PlatformPaillierEncryption implements PlatformEncryptionInterface {

    private final int bitLength;
    private final BigInteger publicKey;
    private final String ttpAddress;

    PlatformPaillierEncryption(int bitLength, BigInteger publicKey, String ttpAddress) {
        this.bitLength = bitLength;
        this.publicKey = publicKey;
        this.ttpAddress = ttpAddress;
    }

    @Override
    public String encryptString(String plaintext) {
        return encrypt(stringToBigInteger(plaintext)).toString();
    }

    @Override
    public String encryptLong(long plaintext) {
        return encrypt(BigInteger.valueOf(plaintext)).toString();
    }

    @Override
    public String encryptBool(boolean plaintext) {
        if (plaintext)
            return encrypt(BigInteger.ONE).toString();
        return encrypt(BigInteger.ZERO).toString();
    }

    // This could be replaced by performing the encryption on the hospital side
    // This is performed to ease development
    private BigInteger encrypt(BigInteger value) {
        var client = RestClient.create();

        var bound = BigInteger.valueOf(2).pow(bitLength).subtract(BigInteger.valueOf(1));
        if (value.compareTo(bound) > 0)
            throw new IllegalArgumentException("Number exceeds plaintext bound: " + bound);

        PaillierEncryptResponse response = client.post()
                .uri("http://" + ttpAddress + "/api/paillier/encrypt")
                .contentType(APPLICATION_JSON)
                .body(new PaillierEncryptRequest(publicKey.toString(), value.toString()))
                .retrieve()
                .body(PaillierEncryptResponse.class);


        assert response != null;
        return new BigInteger(response.getCiphertext());
    }

    private BigInteger stringToBigInteger(String s) {
        byte[] bytes = s.getBytes(StandardCharsets.UTF_8);
        return new BigInteger(1, bytes);  // 1 indicates positive number
    }
}
