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
    private String hospital;
    private String speciality;
    private String manufacturer;
    private String model;
    private String firmwareVersion;
    private String deviceType;
    private String deviceCategory;
    private OffsetDateTime productionDate;
    private OffsetDateTime lastServiceDate;
    private OffsetDateTime warrantyExpiryDate;
    private int usageHours;
    private int batteryLevel;
    private boolean activeStatus;
    private OffsetDateTime lastSyncTime;
    private String syncFrequencySeconds;
}