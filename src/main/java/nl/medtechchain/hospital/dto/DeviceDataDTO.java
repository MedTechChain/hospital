package nl.medtechchain.hospital.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeviceDataDTO {

    private String udi;
    private String speciality;
    private String manufacturer;
    private String model;
    private String firmwareVersion;
    private String deviceType;
    private String deviceCategory;
    private OffsetDateTime productionDate;
    private OffsetDateTime lastServiceDate;
    private OffsetDateTime warrantyExpiryDate;
    private OffsetDateTime lastSyncTime;
    private int usageHours;
    private int batteryLevel;
    private int syncFrequencySeconds;
    private boolean activeStatus;
}