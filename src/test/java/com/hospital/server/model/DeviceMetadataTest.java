package com.hospital.server.model;

import java.util.Collection;
import java.util.Set;
import java.util.UUID;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class DeviceMetadataTest {

    private DeviceMetadata device;
    String uuid = UUID.randomUUID().toString();
    String version = "v1.2.3";

    @BeforeEach
    public void setup() {
        device = new DeviceMetadata(uuid, version);
    }

    @Test
    public void testGetUUID() {
        Assertions.assertThat(device.getUUID()).isEqualTo(uuid);
    }

    @Test
    public void testGetVersion() {
        Assertions.assertThat(device.getVersion()).isEqualTo(version);
    }

    @Test
    public void testSetUUID() {
        String testUUID = UUID.randomUUID().toString();
        device.setUUID(testUUID);
        Assertions.assertThat(device.getUUID()).isEqualTo(testUUID);
    }

    @Test
    public void testSetVersion() {
        device.setVersion("v0.9.2");
        Assertions.assertThat(device.getVersion()).isEqualTo("v0.9.2");
    }


}
