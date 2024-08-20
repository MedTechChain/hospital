package nl.medtechchain.hospital.controller;

import com.google.protobuf.Timestamp;
import nl.medtechchain.hospital.dto.DeviceDataDTO;
import nl.medtechchain.hospital.dto.encrypt.EncryptRequest;
import nl.medtechchain.hospital.dto.encrypt.EncryptResponse;
import nl.medtechchain.hospital.service.ChaincodeService;
import nl.medtechchain.proto.config.PlatformConfig;
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
import org.springframework.web.client.RestClient;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Logger;

import static org.springframework.http.MediaType.APPLICATION_JSON;

@RestController
@RequestMapping("/api/device")
public class DeviceMetadataController {

    @Value("${hospital.override-ttp-address:#{null}}")
    private String overrideTtpAddress;

    private static final Logger logger = Logger.getLogger(DeviceMetadataController.class.getName());

    private final ChaincodeService chaincodeService;

    public DeviceMetadataController(ChaincodeService chaincodeService) {
        this.chaincodeService = chaincodeService;
    }

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<?> addPortableDevice(@RequestBody DeviceDataDTO deviceDataDTO) throws EndorseException, CommitException, SubmitException, CommitStatusException {
        var readPlatformConfigResponse = chaincodeService.getPlatformConfig();

        var platformConfig = readPlatformConfigResponse.getPlatformConfig();
        Optional<String> encryptionVersion;
        if (readPlatformConfigResponse.hasEncryptionVersion())
            encryptionVersion = Optional.of(readPlatformConfigResponse.getEncryptionVersion());
        else
            encryptionVersion = Optional.empty();

        var category = DeviceDataAsset.DeviceCategory.valueOf(deviceDataDTO.getDeviceCategory());
        if (category == DeviceDataAsset.DeviceCategory.UNRECOGNIZED || category == DeviceDataAsset.DeviceCategory.DEVICE_CATEGORY_UNSPECIFIED)
            throw new IllegalArgumentException("Invalid device data category: " + category);

        var speciality = MedicalSpeciality.valueOf(deviceDataDTO.getSpeciality());
        if (speciality == MedicalSpeciality.UNRECOGNIZED || speciality == MedicalSpeciality.MEDICAL_SPECIALITY_UNSPECIFIED)
            throw new IllegalArgumentException("Invalid medical speciality: " + speciality);

        var hospitals = platformConfig.getParticipantConfig().getHospitalsConfigList();
        var hospital = hospitals.stream().filter(h -> h.getName().equals(deviceDataDTO.getHospital())).findFirst();
        if (hospital.isEmpty())
            throw new IllegalArgumentException("Invalid hospital name: " + deviceDataDTO.getHospital());

        var hospitalIndex = hospitals.indexOf(hospital.get());

        var assetBuilder = DeviceDataAsset.newBuilder();

        assetBuilder.setTimestamp(Timestamp.newBuilder().setSeconds(Instant.now().getEpochSecond()).build());
        assetBuilder.setPlainDeviceData(
                DeviceDataAsset.PlainDeviceData
                        .newBuilder()
                        .setManufacturer(deviceDataDTO.getManufacturer())
                        .setModel(deviceDataDTO.getModel())
                        .setFirmwareVersion(deviceDataDTO.getFirmwareVersion())
                        .setDeviceType(deviceDataDTO.getDeviceType())
                        .setDeviceCategory(category)
                        .setProductionDate(Timestamp.newBuilder().setSeconds(deviceDataDTO.getProductionDate().toInstant().getEpochSecond()).build())
                        .setLastServiceDate(Timestamp.newBuilder().setSeconds(deviceDataDTO.getLastServiceDate().toInstant().getEpochSecond()).build())
                        .setWarrantyExpiryDate(Timestamp.newBuilder().setSeconds(deviceDataDTO.getWarrantyExpiryDate().toInstant().getEpochSecond()).build())
                        .setUsageHours(deviceDataDTO.getUsageHours())
                        .setBatteryLevel(deviceDataDTO.getBatteryLevel())
                        .setActiveStatus(deviceDataDTO.isActiveStatus())
                        .setLastSyncTime(Timestamp.newBuilder().setSeconds(deviceDataDTO.getLastSyncTime().toInstant().getEpochSecond()).build())
                        .setSyncFrequencySeconds(deviceDataDTO.getSyncFrequencySeconds())
                        .build()
        );

        DeviceDataAsset.SensitiveDeviceData.Builder sensitiveDeviceDataBuilder = DeviceDataAsset.SensitiveDeviceData.newBuilder();

        if (encryptionVersion.isPresent()) {
            assetBuilder.setEncryptionVersion(encryptionVersion.get());

            sensitiveDeviceDataBuilder
                    .setUdi(encrypt(stringToBigInteger(deviceDataDTO.getUdi()), platformConfig))
                    .setHospital(encrypt(BigInteger.valueOf(hospitalIndex), platformConfig))
                    .setSpeciality(encrypt(BigInteger.valueOf(speciality.getNumber()), platformConfig));
        } else
            sensitiveDeviceDataBuilder
                    .setUdi(deviceDataDTO.getUdi())
                    .setHospital(hospital.get().getName())
                    .setSpeciality(deviceDataDTO.getSpeciality());

        assetBuilder.setSensitiveDeviceData(sensitiveDeviceDataBuilder.build());

        var id = UUID.nameUUIDFromBytes(deviceDataDTO.getUdi().getBytes(StandardCharsets.UTF_8)).toString();
        var asset = assetBuilder.build();

        chaincodeService.storeDeviceData(id, asset);

        return new ResponseEntity<>(HttpStatus.CREATED);
    }


    private String encrypt(BigInteger value, PlatformConfig platformConfig) {
        var client = RestClient.create();

        switch (platformConfig.getFeatureConfig().getQueryConfig().getEncryptionConfig().getSchemeCase()) {
            case PAILLIER:
                var paillier = platformConfig.getFeatureConfig().getQueryConfig().getEncryptionConfig().getPaillier();
                var address = paillier.getTrustedThirdPartyAddress();
                if (overrideTtpAddress != null)
                    address = overrideTtpAddress;

                long startTime = System.nanoTime();

                EncryptResponse encryptResponse = client.post()
                        .uri("http://" + address + "/api/paillier/encrypt")
                        .contentType(APPLICATION_JSON)
                        .body(new EncryptRequest(paillier.getPublicKey(), value.toString()))
                        .retrieve()
                        .body(EncryptResponse.class);

                long endTime = System.nanoTime();

                long durationInMillis = (endTime - startTime) / 1_000_000;
                logger.info("Encryption request time: " + durationInMillis + " ms");

                assert encryptResponse != null;
                return encryptResponse.getCiphertext();
            default:
                logger.severe("Encryption scheme not set in config when encryption version is present");
                throw new IllegalStateException("Encryption scheme not set in config when encryption version is present");
        }
    }

    private BigInteger stringToBigInteger(String s) {
        byte[] bytes = s.getBytes(StandardCharsets.UTF_8);
        return new BigInteger(1, bytes);  // 1 indicates positive number
    }
}