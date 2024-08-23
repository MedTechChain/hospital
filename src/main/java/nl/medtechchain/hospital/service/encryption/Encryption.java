package nl.medtechchain.hospital.service.encryption;

public interface Encryption {
    String encryptString(String plaintext);

    String encryptLong(long plaintext);

    String encryptBool(boolean plaintext);
}

