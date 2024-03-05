package com.hospital.server.model;

public class WearableDeviceDTO {
    private String deviceSerialNumber;
    private String udiDeviceName;
    private String softwareVersion;
    private String timestamp; // ISO 8601 format

    private boolean hasHeartRateSensor;
    private boolean hasAccelerometer;
    private boolean hasGyroscope;
    private boolean hasBarometric;
    private boolean hasMicrophone;
    private boolean hasMagnetometer;
    private boolean hasTemperatureSensor;
    private boolean hasGps;

    // Getters and Setters
    public String getDeviceSerialNumber() {
        return deviceSerialNumber;
    }

    public void setDeviceSerialNumber(String deviceSerialNumber) {
        this.deviceSerialNumber = deviceSerialNumber;
    }

    public String getUdiDeviceName() {
        return udiDeviceName;
    }

    public void setUdiDeviceName(String udiDeviceName) {
        this.udiDeviceName = udiDeviceName;
    }

    public String getSoftwareVersion() {
        return softwareVersion;
    }

    public void setSoftwareVersion(String softwareVersion) {
        this.softwareVersion = softwareVersion;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isHasHeartRateSensor() {
        return hasHeartRateSensor;
    }

    public void setHasHeartRateSensor(boolean hasHeartRateSensor) {
        this.hasHeartRateSensor = hasHeartRateSensor;
    }

    public boolean isHasAccelerometer() {
        return hasAccelerometer;
    }

    public void setHasAccelerometer(boolean hasAccelerometer) {
        this.hasAccelerometer = hasAccelerometer;
    }

    public boolean isHasGyroscope() {
        return hasGyroscope;
    }

    public void setHasGyroscope(boolean hasGyroscope) {
        this.hasGyroscope = hasGyroscope;
    }

    public boolean isHasBarometric() {
        return hasBarometric;
    }

    public void setHasBarometric(boolean hasBarometric) {
        this.hasBarometric = hasBarometric;
    }

    public boolean isHasMicrophone() {
        return hasMicrophone;
    }

    public void setHasMicrophone(boolean hasMicrophone) {
        this.hasMicrophone = hasMicrophone;
    }

    public boolean isHasMagnetometer() {
        return hasMagnetometer;
    }

    public void setHasMagnetometer(boolean hasMagnetometer) {
        this.hasMagnetometer = hasMagnetometer;
    }

    public boolean isHasTemperatureSensor() {
        return hasTemperatureSensor;
    }

    public void setHasTemperatureSensor(boolean hasTemperatureSensor) {
        this.hasTemperatureSensor = hasTemperatureSensor;
    }

    public boolean isHasGps() {
        return hasGps;
    }

    public void setHasGps(boolean hasGps) {
        this.hasGps = hasGps;
    }
}
