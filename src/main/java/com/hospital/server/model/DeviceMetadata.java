package com.hospital.server.model;

// TODO: There should be two types of metadata, for now we just have one type

public class DeviceMetadata {
    // TODO: change the fields that we will have here to whatever we are actually using
    private String uuid; // UUID field
    private String version; // Version field

    // Getters
    public String getUuid() {
        return uuid;
    }

    public String getVersion() {
        return version;
    }

    // Setters
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
