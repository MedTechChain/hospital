package nl.medtechchain.hospital.service.encryption;

import nl.medtechchain.hospital.service.encryption.paillier.dto.PaillierEncryptRequest;
import nl.medtechchain.hospital.service.encryption.paillier.dto.PaillierEncryptResponse;
import nl.medtechchain.hospital.util.PlatformConfig;
import org.springframework.web.client.RestClient;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;

import static nl.medtechchain.proto.config.PlatformConfig.Config.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;

public class PaillierEncryption extends PlatformEncryption {

    private final int bitLength;
    private final BigInteger publicKey;
    private final String ttpAddress;

    PaillierEncryption(PlatformConfig platformConfig) {
        super(platformConfig);
        this.bitLength = Integer.parseInt(platformConfig.getUnsafe(CONFIG_FEATURE_QUERY_ENCRYPTION_PAILLIER_BIT_LENGTH));
        this.publicKey = new BigInteger(platformConfig.getUnsafe(CONFIG_FEATURE_QUERY_ENCRYPTION_PAILLIER_PUBLIC_KEY));
        this.ttpAddress = platformConfig.getUnsafe(CONFIG_FEATURE_QUERY_ENCRYPTION_PAILLIER_TTP_ADRRESS);
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
