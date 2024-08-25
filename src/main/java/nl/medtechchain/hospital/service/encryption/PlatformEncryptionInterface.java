package nl.medtechchain.hospital.service.encryption;

import nl.medtechchain.hospital.util.PlatformConfigWrapper;

import java.math.BigInteger;
import java.util.Optional;
import java.util.logging.Logger;

import static nl.medtechchain.proto.config.PlatformConfig.Config.*;

public interface PlatformEncryptionInterface {
    String encryptString(String plaintext);

    String encryptLong(long plaintext);

    String encryptBool(boolean plaintext);

    class Factory {
        private enum SchemeType {
            NONE,
            PAILLIER
        }

        public static Optional<PlatformEncryptionInterface> getInstance(PlatformConfigWrapper platformConfig) {
            var logger = Logger.getLogger(PlatformEncryptionInterface.class.getName());

            var schemeOpt = platformConfig.get(CONFIG_FEATURE_QUERY_ENCRYPTION_SCHEME);

            if (schemeOpt.isEmpty()) {
                logger.warning("No encryption scheme set, defaulting to none");
            }

            var schemeType = schemeOpt
                    .map(s -> {
                        try {
                            return SchemeType.valueOf(s.toUpperCase());
                        } catch (IllegalArgumentException e) {
                            logger.warning("Invalid encryption scheme: " + s + ", defaulting to none");
                            return SchemeType.NONE;
                        }
                    }).orElse(SchemeType.NONE);

            Optional<PlatformEncryptionInterface> scheme = Optional.empty();
            try {
                switch (schemeType) {
                    case PAILLIER:
                        var bitLength = Integer.parseInt(platformConfig.getUnsafe(CONFIG_FEATURE_QUERY_ENCRYPTION_PAILLIER_BIT_LENGTH));
                        var publicKey = new BigInteger(platformConfig.getUnsafe(CONFIG_FEATURE_QUERY_ENCRYPTION_PAILLIER_PUBLIC_KEY));
                        var ttpAddress = platformConfig.getUnsafe(CONFIG_FEATURE_QUERY_ENCRYPTION_PAILLIER_TTP_ADRRESS);
                        scheme = Optional.of(new PlatformPaillierEncryption(bitLength, publicKey, ttpAddress));
                        break;
                }
            } catch (Throwable t) {
                logger.warning("Error while getting scheme: " + t.getMessage());
                scheme = Optional.empty();
            }

            return scheme;
        }
    }
}

