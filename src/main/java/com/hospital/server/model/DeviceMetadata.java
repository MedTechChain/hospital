package com.hospital.server.model;

// TODO: There should be two types of metadata, for now we just have one type

public class DeviceMetadata {
    // TODO: change the fields that we will have here to whatever we are actually using

    /**
     * Creates a DeviceMetadata object with the specified values.
     *
     * @param uuid      the username of the device (a unique identifier for the device)
     * @param version   the software version of the device (formatted vX.Y.Z)
     */
    public DeviceMetadata(String uuid, String version) {
        this.uuid = uuid;
        this.version = version;
    }

    private String uuid; // UUID field
    private String version; // Version field

    // Getters
    public String getUUID() {
        return uuid;
    }

    public String getVersion() {
        return version;
    }

    // Setters
    public void setUUID(String uuid) {
        this.uuid = uuid;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
