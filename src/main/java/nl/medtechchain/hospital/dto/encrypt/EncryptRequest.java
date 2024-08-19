package nl.medtechchain.hospital.dto.encrypt;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EncryptRequest {
    private String publicKey;
    private String value;
}
