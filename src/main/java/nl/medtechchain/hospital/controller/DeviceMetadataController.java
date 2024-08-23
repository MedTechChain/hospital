package nl.medtechchain.hospital.controller;

import com.google.protobuf.Timestamp;
import nl.medtechchain.hospital.dto.DeviceDataDTO;
import nl.medtechchain.hospital.service.ChaincodeService;
import nl.medtechchain.hospital.service.encryption.PlatformEncryption;
import nl.medtechchain.proto.devicedata.DeviceCategory;
import nl.medtechchain.proto.devicedata.DeviceDataAsset;
import nl.medtechchain.proto.devicedata.MedicalSpeciality;
import org.hyperledger.fabric.client.CommitException;
import org.hyperledger.fabric.client.CommitStatusException;
import org.hyperledger.fabric.client.EndorseException;
import org.hyperledger.fabric.client.SubmitException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.UUID;

import static nl.medtechchain.proto.config.PlatformConfig.Config.CONFIG_FEATURE_QUERY_ENCRYPTION_PAILLIER_TTP_ADRRESS;

@RestController
@RequestMapping("/api/device")
public class DeviceMetadataController {

    @Value("${hospital.override-paillier-ttp-address:#{null}}")
    private String overridePaillierTtpAddress;

    @Value("${hospital.name}")
    private String hospitalName;

    private final ChaincodeService chaincodeService;

    public DeviceMetadataController(ChaincodeService chaincodeService) {
        this.chaincodeService = chaincodeService;
    }

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<?> addPortableDevice(@RequestBody DeviceDataDTO deviceDataDTO) throws EndorseException, CommitException, SubmitException, CommitStatusException {
        var platformConfig = chaincodeService.getPlatformConfig();

        if (overridePaillierTtpAddress != null)
            platformConfig.override(CONFIG_FEATURE_QUERY_ENCRYPTION_PAILLIER_TTP_ADRRESS, overridePaillierTtpAddress);

        var encryptionScheme = PlatformEncryption.get(platformConfig);

        var category = DeviceCategory.valueOf(deviceDataDTO.getDeviceCategory());
        if (category == DeviceCategory.UNRECOGNIZED || category == DeviceCategory.DEVICE_CATEGORY_UNSPECIFIED)
            throw new IllegalArgumentException("Invalid device data category: " + category);

        var speciality = MedicalSpeciality.valueOf(deviceDataDTO.getSpeciality());
        if (speciality == MedicalSpeciality.UNRECOGNIZED || speciality == MedicalSpeciality.MEDICAL_SPECIALITY_UNSPECIFIED)
            throw new IllegalArgumentException("Invalid medical speciality: " + speciality);

        var assetBuilder = DeviceDataAsset.newBuilder();

        assetBuilder.setTimestamp(Timestamp.newBuilder().setSeconds(Instant.now().getEpochSecond()).build());
        assetBuilder.setConfigId(platformConfig.getId());

        var deviceDataBuilder = DeviceDataAsset.DeviceData.newBuilder();
        deviceDataBuilder.setManufacturer(DeviceDataAsset.StringField.newBuilder().setPlain(deviceDataDTO.getManufacturer()).build());
        deviceDataBuilder.setModel(DeviceDataAsset.StringField.newBuilder().setPlain(deviceDataDTO.getModel()).build());
        deviceDataBuilder.setFirmwareVersion(DeviceDataAsset.StringField.newBuilder().setPlain(deviceDataDTO.getFirmwareVersion()).build());
        deviceDataBuilder.setDeviceType(DeviceDataAsset.StringField.newBuilder().setPlain(deviceDataDTO.getDeviceType()).build());
        deviceDataBuilder.setCategory(DeviceDataAsset.DeviceCateogryField.newBuilder().setPlain(category).getDefaultInstanceForType());
        deviceDataBuilder.setProductionDate(DeviceDataAsset.TimestampField.newBuilder().setPlain(Timestamp.newBuilder().setSeconds(deviceDataDTO.getProductionDate().toInstant().getEpochSecond()).build()).build());
        deviceDataBuilder.setLastServiceDate(DeviceDataAsset.TimestampField.newBuilder().setPlain(Timestamp.newBuilder().setSeconds(deviceDataDTO.getLastServiceDate().toInstant().getEpochSecond()).build()).build());
        deviceDataBuilder.setWarrantyExpiryDate(DeviceDataAsset.TimestampField.newBuilder().setPlain(Timestamp.newBuilder().setSeconds(deviceDataDTO.getWarrantyExpiryDate().toInstant().getEpochSecond()).build()).build());
        deviceDataBuilder.setLastSyncTime(DeviceDataAsset.TimestampField.newBuilder().setPlain(Timestamp.newBuilder().setSeconds(deviceDataDTO.getLastSyncTime().toInstant().getEpochSecond()).build()).build());
        deviceDataBuilder.setUsageHours(DeviceDataAsset.IntegerField.newBuilder().setPlain(deviceDataDTO.getUsageHours()).build());
        deviceDataBuilder.setBatteryLevel(DeviceDataAsset.IntegerField.newBuilder().setPlain(deviceDataDTO.getBatteryLevel()).build());
        deviceDataBuilder.setSyncFrequencySeconds(DeviceDataAsset.IntegerField.newBuilder().setPlain(deviceDataDTO.getSyncFrequencySeconds()).build());
        deviceDataBuilder.setActiveStatus(DeviceDataAsset.BoolField.newBuilder().setPlain(deviceDataDTO.isActiveStatus()).build());

        if (encryptionScheme.isPresent()) {
            deviceDataBuilder.setHospital(DeviceDataAsset.StringField.newBuilder().setEncrypted(encryptionScheme.get().encryptString(hospitalName)).build());
            deviceDataBuilder.setSpeciality(DeviceDataAsset.MedicalSpecialityField.newBuilder().setEncrypted(encryptionScheme.get().encryptLong(speciality.getNumber())).build());
        } else {
            deviceDataBuilder.setHospital(DeviceDataAsset.StringField.newBuilder().setPlain(hospitalName).build());
            deviceDataBuilder.setSpeciality(DeviceDataAsset.MedicalSpecialityField.newBuilder().setPlain(speciality).build());
        }

        assetBuilder.setDeviceData(deviceDataBuilder.build());

        var id = UUID.nameUUIDFromBytes(deviceDataDTO.getUdi().getBytes(StandardCharsets.UTF_8)).toString();
        var asset = assetBuilder.build();

        chaincodeService.storeDeviceData(id, asset);

        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}