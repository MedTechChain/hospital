package nl.medtechchain.hospital.controller;

import com.google.protobuf.ByteString;
import com.google.protobuf.util.JsonFormat;
import nl.medtechchain.protos.devicemetadata.DeviceType;
import nl.medtechchain.protos.devicemetadata.EncryptedDeviceMetadata;
import nl.medtechchain.protos.devicemetadata.EncryptedPortableDeviceMetadata;
import nl.medtechchain.protos.devicemetadata.EncryptedWearableDeviceMetadata;
import nl.medtechchain.protos.encryption.EncryptionKeyMetadata;
import nl.medtechchain.protos.encryption.EncryptionSchemeType;
import org.hyperledger.fabric.client.*;
import jakarta.annotation.PreDestroy;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.UUID;

/**
 * Controller class for handling device metadata operations.
 * <p>
 * This class provides endpoints for adding portable and wearable device metadata to a blockchain ledger.
 * It encapsulates the logic for processing incoming JSON-formatted device metadata, validating it against
 * Google Protocol Buffers definitions, and then submitting transactions to the Hyperledger Fabric blockchain
 * using the Fabric Gateway API.
 */
@RestController
@RequestMapping("/api/devices")
public class DeviceMetadataController {

    private final Gateway gateway;
    private final Contract contract;

    /**
     * Constructs a new DeviceMetadataController.
     * <p>
     * Initializes the controller with the necessary blockchain network and smart contract information
     * for submitting device metadata transactions.
     *
     * @param env      The Spring environment to access application properties.
     * @param gateway  The Fabric Gateway for blockchain network interactions.
     */
    public DeviceMetadataController(Environment env, Gateway gateway) {
        this.gateway = gateway;

        // Get a network instance representing the channel where the smart contract is deployed
        Network network = gateway.getNetwork(env.getProperty("gateway.channel-name"));
        // Get the smart contract from the network
        this.contract = network.getContract(env.getProperty("gateway.chaincode-name"),
                env.getProperty("gateway.smart-contract-name"));
    }

    /**
     * Performs cleanup tasks before the controller is destroyed.
     * <p>
     * Specifically, it closes the Fabric Gateway connection to release resources.
     */
    @PreDestroy
    public void destroy() {
        System.out.println("Running a pre-destroy hook to close the gateway...");
        this.gateway.close();
        System.out.println("Successfully closed the gateway");
    }

    /**
     * Adds a new portable device metadata entry to the blockchain.
     * <p>
     * Accepts JSON-formatted device metadata, validates and transforms it into a blockchain transaction,
     * and submits the transaction to add the device metadata to the ledger.
     *
     * @param jsonDeviceMetadata The JSON string representing the device metadata.
     * @return A ResponseEntity indicating the outcome of the operation.
     * @throws Exception if there is an issue processing the request or submitting the transaction.
     */
    @PostMapping("/portable")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<?> addPortableDevice(@RequestBody String jsonDeviceMetadata) throws Exception {
        EncryptedPortableDeviceMetadata deviceMetadata;

        // Check if the json body is valid protobuf format for this device type
        try {
            EncryptedPortableDeviceMetadata.Builder builder = EncryptedPortableDeviceMetadata.newBuilder();
            JsonFormat.parser().merge(jsonDeviceMetadata, builder);
            deviceMetadata = builder.build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Invalid device metadata format");
        }
        // Convert protobuf metadata object to byte array
        byte[] deviceBytes = deviceMetadata.toByteArray();

        // Wrap specific device metadata in general device metadata object
        // Stores device type, raw bytes, and metadata about encryption key
        EncryptedDeviceMetadata wrappedDeviceMetadata = EncryptedDeviceMetadata.newBuilder()
                .setType(DeviceType.PORTABLE_DEVICE)
                .setRawBytes(ByteString.copyFrom(deviceBytes))
                .setEncryptionKeyMetadata(EncryptionKeyMetadata.newBuilder()
                        .setScheme(EncryptionSchemeType.PAILLIER_2048)
                        .setId(UUID.randomUUID().toString())
                        .build())
                .build();

        // Serialize the protobuf object back to a JSON string for the chaincode
        String jsonString = JsonFormat.printer().includingDefaultValueFields().print(wrappedDeviceMetadata);

        String udi = deviceMetadata.getUdi();
        contract.submitTransaction("CreateDeviceMetadataAsset", udi, DeviceType.PORTABLE_DEVICE.toString(), jsonString);

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    /**
     * Adds a new wearable device metadata entry to the blockchain.
     * <p>
     * Similar to the portable device endpoint, this method accepts, validates, and processes
     * JSON-formatted wearable device metadata for submission as a blockchain transaction.
     *
     * @param jsonDeviceMetadata The JSON string representing the wearable device metadata.
     * @return A ResponseEntity indicating the outcome of the operation.
     * @throws Exception if there is an issue processing the request or submitting the transaction.
     */
    @PostMapping("/wearable")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<?> addWearableDevice(@RequestBody String jsonDeviceMetadata) throws Exception {
        EncryptedWearableDeviceMetadata deviceMetadata;

        // Check if the json body is valid protobuf format for this device type
        try {
            EncryptedWearableDeviceMetadata.Builder builder = EncryptedWearableDeviceMetadata.newBuilder();
            JsonFormat.parser().merge(jsonDeviceMetadata, builder);
            deviceMetadata = builder.build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Invalid device metadata format");
        }
        // Convert protobuf metadata object to byte array
        byte[] deviceBytes = deviceMetadata.toByteArray();

        // Wrap specific device metadata in general device metadata object
        // Stores device type, raw bytes, and metadata about encryption key
        EncryptedDeviceMetadata wrappedDeviceMetadata = EncryptedDeviceMetadata.newBuilder()
                .setType(DeviceType.WEARABLE_DEVICE)
                .setRawBytes(ByteString.copyFrom(deviceBytes))
                .setEncryptionKeyMetadata(EncryptionKeyMetadata.newBuilder()
                        .setScheme(EncryptionSchemeType.PAILLIER_2048)
                        .setId(UUID.randomUUID().toString())
                        .build())
                .build();

        // Serialize the protobuf object back to a JSON string for the chaincode
        String jsonString = JsonFormat.printer().includingDefaultValueFields().print(wrappedDeviceMetadata);

        String udi = deviceMetadata.getUdi();
        contract.submitTransaction("CreateDeviceMetadataAsset", udi, DeviceType.WEARABLE_DEVICE.toString(), jsonString);

        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}