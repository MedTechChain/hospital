package com.hospital.server.model;

import java.time.Instant;

public class BedsideMonitorDTO {
    private String deviceManufacturer;
    private String deviceType;
    private int softwareVersion;
    private String timestamp; // Representing google.protobuf.Timestamp as ISO 8601 string

    private boolean hasEcgModule;
    private boolean hasRespModule;
    private boolean hasSpo2Module;
    private boolean hasNibpModule;
    private boolean hasTempModule;
    private boolean has2ChannelInvbpModule;
    private boolean hasSidestreamCo2Module;
    private boolean hasEntropyModule;
    private boolean hasSidestreamN2oModule;
    private boolean hasNeuromuscularTransmissionModule;
    private boolean hasCardiacOutputModule;

    // Getters and Setters
    public String getDeviceManufacturer() {
        return deviceManufacturer;
    }

    public void setDeviceManufacturer(String deviceManufacturer) {
        this.deviceManufacturer = deviceManufacturer;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public int getSoftwareVersion() {
        return softwareVersion;
    }

    public void setSoftwareVersion(int softwareVersion) {
        this.softwareVersion = softwareVersion;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isHasEcgModule() {
        return hasEcgModule;
    }

    public void setHasEcgModule(boolean hasEcgModule) {
        this.hasEcgModule = hasEcgModule;
    }

    public boolean isHasRespModule() {
        return hasRespModule;
    }

    public void setHasRespModule(boolean hasRespModule) {
        this.hasRespModule = hasRespModule;
    }

    public boolean isHasSpo2Module() {
        return hasSpo2Module;
    }

    public void setHasSpo2Module(boolean hasSpo2Module) {
        this.hasSpo2Module = hasSpo2Module;
    }

    public boolean isHasNibpModule() {
        return hasNibpModule;
    }

    public void setHasNibpModule(boolean hasNibpModule) {
        this.hasNibpModule = hasNibpModule;
    }

    public boolean isHasTempModule() {
        return hasTempModule;
    }

    public void setHasTempModule(boolean hasTempModule) {
        this.hasTempModule = hasTempModule;
    }

    public boolean isHas2ChannelInvbpModule() {
        return has2ChannelInvbpModule;
    }

    public void setHas2ChannelInvbpModule(boolean has2ChannelInvbpModule) {
        this.has2ChannelInvbpModule = has2ChannelInvbpModule;
    }

    public boolean isHasSidestreamCo2Module() {
        return hasSidestreamCo2Module;
    }

    public void setHasSidestreamCo2Module(boolean hasSidestreamCo2Module) {
        this.hasSidestreamCo2Module = hasSidestreamCo2Module;
    }

    public boolean isHasEntropyModule() {
        return hasEntropyModule;
    }

    public void setHasEntropyModule(boolean hasEntropyModule) {
        this.hasEntropyModule = hasEntropyModule;
    }

    public boolean isHasSidestreamN2oModule() {
        return hasSidestreamN2oModule;
    }

    public void setHasSidestreamN2oModule(boolean hasSidestreamN2oModule) {
        this.hasSidestreamN2oModule = hasSidestreamN2oModule;
    }

    public boolean isHasNeuromuscularTransmissionModule() {
        return hasNeuromuscularTransmissionModule;
    }

    public void setHasNeuromuscularTransmissionModule(boolean hasNeuromuscularTransmissionModule) {
        this.hasNeuromuscularTransmissionModule = hasNeuromuscularTransmissionModule;
    }

    public boolean isHasCardiacOutputModule() {
        return hasCardiacOutputModule;
    }

    public void setHasCardiacOutputModule(boolean hasCardiacOutputModule) {
        this.hasCardiacOutputModule = hasCardiacOutputModule;
    }
}
