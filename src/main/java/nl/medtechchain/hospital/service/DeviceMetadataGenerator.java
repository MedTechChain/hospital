package nl.medtechchain.hospital.service;

import nl.medtechchain.hospital.dto.DeviceDataDTO;
import nl.medtechchain.proto.devicedata.DeviceDataAsset;
import nl.medtechchain.proto.devicedata.MedicalSpeciality;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Stream;

import static org.springframework.http.MediaType.APPLICATION_JSON;

@Service
public class DeviceMetadataGenerator {

    private final String hospitalName;

    public DeviceMetadataGenerator(Environment env) {
        this.hospitalName = env.getProperty("hospital.name");
    }

    @Scheduled(fixedRate = 2000)
    public void generateAndSendPayloads() {
        var client = RestClient.create();

        System.out.println(generateRandomDeviceData());

        String createDeviceDataEndpoint = "http://localhost:8080/api/create";
        client.post()
                .uri(createDeviceDataEndpoint)
                .contentType(APPLICATION_JSON)
                .body(generateRandomDeviceData())
                .retrieve()
                .toBodilessEntity();
    }


    private DeviceDataDTO generateRandomDeviceData() {
        var random = ThreadLocalRandom.current();
        return new DeviceDataDTO(
                UUID.randomUUID().toString(),
                hospitalName,
                getRandomSpeciality().name(),
                getRandomManufacturer(),
                getRandomModel(),
                "v" + random.nextInt(10) + "." + random.nextInt(10),
                getRandomDeviceType(),
                getRandomDeviceCategory().name(),
                OffsetDateTime.now().minusDays(random.nextInt(365)),
                OffsetDateTime.now().minusDays(random.nextInt(180)),
                OffsetDateTime.now().plusDays(random.nextInt(365)),
                random.nextInt(10000),
                random.nextInt(101),
                random.nextBoolean(),
                OffsetDateTime.now().minusMinutes(random.nextInt(10)),
                String.valueOf(random.nextInt(3600))
        );
    }

    private String getRandomManufacturer() {
        List<String> prefixes = List.of("Med", "Bio", "Health", "Tech", "Care", "Life", "Pro", "Safe");
        List<String> suffixes = List.of("Devices", "Systems", "Solutions", "Instruments", "Technologies", "Innovations", "Medicals", "Labs");

        return getRandomElement(prefixes) + " " + getRandomElement(suffixes);
    }

    private String getRandomModel() {
        List<String> prefixes = List.of("X", "Bio", "Medi", "Pulse", "Vita", "Neo", "Opti", "Cardio");
        List<String> suffixes = List.of("Pro", "Ultra", "Max", "Plus", "One", "Prime", "Elite", "X2");
        List<String> numbers = List.of("1000", "200", "500", "800", "3000", "400", "750", "150");

        return getRandomElement(prefixes) + " " + getRandomElement(suffixes) + " " + getRandomElement(numbers);
    }

    private String getRandomDeviceType() {
        return getRandomElement(List.of(
                "ECG Monitor",
                "Infusion Pump",
                "MRI Scanner",
                "Ultrasound Machine",
                "Defibrillator",
                "Ventilator",
                "Pacemaker",
                "Blood Glucose Monitor",
                "Pulse Oximeter",
                "Endoscope"
        ));
    }


    private DeviceDataAsset.DeviceCategory getRandomDeviceCategory() {
        return getRandomElement(Stream.of(DeviceDataAsset.DeviceCategory.values()).filter(s -> s != DeviceDataAsset.DeviceCategory.UNRECOGNIZED && s != DeviceDataAsset.DeviceCategory.DEVICE_CATEGORY_UNSPECIFIED).toList());
    }

    private MedicalSpeciality getRandomSpeciality() {
        return getRandomElement(Stream.of(MedicalSpeciality.values()).filter(s -> s != MedicalSpeciality.UNRECOGNIZED && s != MedicalSpeciality.MEDICAL_SPECIALITY_UNSPECIFIED).toList());
    }

    public static <T> T getRandomElement(List<T> list) {
        int randomIndex = ThreadLocalRandom.current().nextInt(list.size());
        return list.get(randomIndex);
    }
}
