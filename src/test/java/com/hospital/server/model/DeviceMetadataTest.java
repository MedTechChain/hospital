package com.hospital.server.model;

import java.util.Collection;
import java.util.Set;
import java.util.UUID;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class DeviceMetadataTest {

    private DeviceMetadata device;
    String uuid = "dd8cd1e2-2539-423c-a7c0-504c4d38b04e";
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
        device.setUUID("22df97f0-5a0f-43e1-8b18-0ad290fd32c2");
        Assertions.assertThat(device.getUUID()).isEqualTo("22df97f0-5a0f-43e1-8b18-0ad290fd32c2");
    }

    @Test
    public void testSetVersion() {
        device.setVersion("v0.9.2");
        Assertions.assertThat(device.getVersion()).isEqualTo("v0.9.2");
    }


}
