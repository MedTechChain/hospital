package nl.medtechchain.hospital.util;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class PlatformConfig {

    @Getter
    private final String id;
    private final Map<nl.medtechchain.proto.config.PlatformConfig.Config, String> map;

    public PlatformConfig(nl.medtechchain.proto.config.PlatformConfig configs) {
        this.id = configs.getId();

        this.map = new HashMap<>();

        for (nl.medtechchain.proto.config.PlatformConfig.Entry e : configs.getMapList())
            map.put(e.getKey(), e.getValue());
    }

    public Optional<String> get(nl.medtechchain.proto.config.PlatformConfig.Config property) {
        return Optional.ofNullable(map.get(property));
    }

    public String getUnsafe(nl.medtechchain.proto.config.PlatformConfig.Config property) {
        var o = Optional.ofNullable(map.get(property));
        if (o.isPresent())
            return o.get();

        throw new ConfigurationException("Config property not set: " + property.name());
    }

    public void override(nl.medtechchain.proto.config.PlatformConfig.Config property, String value) {
        map.put(property, value);
    }
}
