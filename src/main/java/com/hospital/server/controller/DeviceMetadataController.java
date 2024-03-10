package com.hospital.server.controller;

import com.hospital.server.model.DeviceMetadata;

import java.util.ArrayList;
import java.util.List;

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
    private final List<DeviceMetadata> devices = new ArrayList<>();

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

    // TODO: what happens when data is not successfully committed to the blockchain, or when many devices are trying to access the API
    /**
     * Adds a new device metadata to the blockchain. Handles POST requests from devices in the hospital,
     * extracts UUID and version, and submits it to the blockchain using the gateway and contract.
     *
     * @param deviceMetadata        the device metadata to be pushed to the blockchain.
     * @return ResponseEntity       containing the added device metadata and HTTP status.
     * @throws GatewayException     indicates a problem with the fabric gateway.
     * @throws CommitException      indicates a problem with committing the transaction to the blockchain.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<DeviceMetadata> addDevice(@RequestBody DeviceMetadata deviceMetadata) throws GatewayException, CommitException {

        // Extract UUID and version from the deviceMetadata object
        String uuid = deviceMetadata.getUuid();
        String version = deviceMetadata.getVersion();

        // Submit the transaction to the blockchain
        // TODO: transactionResult is unused
        byte[] transactionResult = contract.submitTransaction("CreateWatch", uuid, version);

        // Save device metadata in local list
        devices.add(deviceMetadata);

        // Respond with the added device metadata and a status of CREATED
        return new ResponseEntity<>(deviceMetadata, HttpStatus.CREATED);
    }

    /**
     * Retrieves all device metadata entries.
     *
     * @return ResponseEntity       containing the list of all device metadata and HTTP status.
     */
    @GetMapping
    public ResponseEntity<List<DeviceMetadata>> getAllDevices() {
        return ResponseEntity.ok(devices);
    }
}