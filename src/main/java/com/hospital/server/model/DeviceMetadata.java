package com.hospital.server.model;

// TODO: There should be two types of metadata, for now we just have one type

public class DeviceMetadata {
// TODO: change the fields that we will have here to whatever we are actually using
    private String id;
    private String name;
    private long timestamp;
    private String location;

    // Getters
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getLocation() {
        return location;
    }

    // Setters
    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
