package nl.medtechchain.hospital.service;

import jakarta.annotation.PreDestroy;
import nl.medtechchain.hospital.util.PlatformConfigWrapper;
import nl.medtechchain.proto.common.ChaincodeResponse;
import nl.medtechchain.proto.devicedata.DeviceDataAsset;
import org.hyperledger.fabric.client.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.logging.Logger;

@Service
public class ChaincodeService {

    @Value("${hospital.override-ttp-address:#{null}}")
    private String overrideTtpAddress;

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

    public PlatformConfigWrapper getPlatformConfig() {
        try {
            var response = configContract.evaluateTransaction("GetPlatformConfig");
            var chaincodeResponse = ChaincodeResponse.parseFrom(Base64.getDecoder().decode(response));
            if (chaincodeResponse.getChaincodeResponseCase() == ChaincodeResponse.ChaincodeResponseCase.SUCCESS) {
                return new PlatformConfigWrapper(nl.medtechchain.proto.config.PlatformConfig.parseFrom(Base64.getDecoder().decode(chaincodeResponse.getSuccess().getMessage())));
            }

            if (chaincodeResponse.getChaincodeResponseCase() == ChaincodeResponse.ChaincodeResponseCase.ERROR)
                throw new IllegalStateException(chaincodeResponse.getError().toString());

            throw new IllegalStateException("Unrecognized chaincode response");

        } catch (Throwable e) {
            logger.severe("Cannot retrieve platform config: " + e);
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
