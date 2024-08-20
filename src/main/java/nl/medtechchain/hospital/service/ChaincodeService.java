package nl.medtechchain.hospital.service;

import com.google.protobuf.InvalidProtocolBufferException;
import jakarta.annotation.PreDestroy;
import nl.medtechchain.proto.config.ReadPlatformConfigResponse;
import nl.medtechchain.proto.devicedata.DeviceDataAsset;
import org.hyperledger.fabric.client.*;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.logging.Logger;

@Service
public class ChaincodeService {

    private static final Logger logger = Logger.getLogger(ChaincodeService.class.getName());

    private final Gateway gateway;
    private final Contract deviceDataContract;
    private final Contract configContract;

    public ChaincodeService(Environment env, Gateway gateway) {
        this.gateway = gateway;
        Network network = gateway.getNetwork(env.getProperty("gateway.channel-name"));
        this.deviceDataContract = network.getContract(env.getProperty("gateway.chaincode-name"),
                env.getProperty("gateway.data-contract-name"));

        this.configContract = network.getContract(env.getProperty("gateway.chaincode-name"),
                env.getProperty("gateway.config-contract-name"));
    }

    public ReadPlatformConfigResponse getPlatformConfig() {
        try {
            var responseBytes = configContract.evaluateTransaction("GetPlatformConfig");
            return ReadPlatformConfigResponse.parseFrom(Base64.getDecoder().decode(new String(responseBytes)));
        } catch (GatewayException | InvalidProtocolBufferException e) {
            logger.severe("Cannot retrieve platform config: " + e.getMessage());
            throw new IllegalStateException("Cannot retrieve platform config:", e);
        }
    }

    public void storeDeviceData(String id, DeviceDataAsset asset) throws EndorseException, CommitException, SubmitException, CommitStatusException {
        deviceDataContract.submitTransaction("StoreDeviceData", id, Base64.getEncoder().encodeToString(asset.toByteArray()));
    }

    @PreDestroy
    public void destroy() {
        logger.info("Running a pre-destroy hook to close the gateway...");
        this.gateway.close();
        logger.info("Successfully closed the gateway");
    }
}
