package com.hospital.server.controller;

import com.google.protobuf.util.JsonFormat;
import nl.medtechchain.protos.devicemetadata.EncryptedPortableDeviceMetadata;
import nl.medtechchain.protos.devicemetadata.EncryptedWearableDeviceMetadata;
import org.hyperledger.fabric.client.*;

import jakarta.annotation.PreDestroy;

import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.ResponseStatus;

// TODO: add tests for API
// TODO: create two endpoints for two types of metadata
@RestController
@RequestMapping("/api/devices")
public class DeviceMetadataController {

    private final Gateway gateway;
    private final Contract contract;

    /**
     * Creates a new QueryController object, which is used for sending queries to the blockchain.
     *
     * @param env               the Spring environment (to access the defined properties)
     * @param gateway           the Fabric Gateway
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
     *  Closes the gateway before the controller is destroyed.
     */
    @PreDestroy
    public void destroy() {
        System.out.println("Running a pre-destroy hook to close the gateway...");
        this.gateway.close();
        System.out.println("Successfully closed the gateway");
    }

    @PostMapping("/portable")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<?> addPortableDevice(@RequestBody String jsonDeviceMetadata) throws Exception {
        EncryptedPortableDeviceMetadata deviceMetadata;
        try {
            EncryptedPortableDeviceMetadata.Builder builder = EncryptedPortableDeviceMetadata.newBuilder();
            JsonFormat.parser().merge(jsonDeviceMetadata, builder);
            deviceMetadata = builder.build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Invalid device metadata format");
        }

        // Serialize the protobuf object back to a JSON string for the chaincode
        String jsonString = JsonFormat.printer().print(deviceMetadata);

        String deviceType = "0"; // 0=PORTABLE_DEVICE, 1=WEARABLE_DEVICE

        String udi = deviceMetadata.getUdi();
        byte[] transactionResult = contract.submitTransaction("CreateDeviceMetadataAsset", udi, deviceType, jsonString);

        System.out.println(new String(transactionResult));

        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}