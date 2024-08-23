package nl.medtechchain.hospital.service.encryption.paillier.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaillierEncryptResponse {
    private String ciphertext;
}
