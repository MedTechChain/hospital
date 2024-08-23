package nl.medtechchain.hospital.service.encryption;

import nl.medtechchain.hospital.util.PlatformConfig;

import java.util.Optional;
import java.util.logging.Logger;

import static nl.medtechchain.proto.config.PlatformConfig.Config.CONFIG_FEATURE_QUERY_ENCRYPTION_SCHEME;

public abstract class PlatformEncryption implements Encryption {

    private static final Logger logger = Logger.getLogger(PlatformEncryption.class.getName());

    protected PlatformEncryption(PlatformConfig config) {
    }

    private enum SchemeType {
        NONE,
        PAILLIER
    }

    public static Optional<Encryption> get(PlatformConfig platformConfig) {
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

        Optional<Encryption> scheme;
        try {
            scheme = switch (schemeType) {
                case NONE -> Optional.empty();
                case PAILLIER -> Optional.of(new PaillierEncryption(platformConfig));
            };
        } catch (Throwable t) {
            logger.warning("Error while getting scheme: " + t.getMessage());
            scheme = Optional.empty();
        }

        return scheme;
    }
}

