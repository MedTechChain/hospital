package nl.medtechchain.hospital.config;

import io.grpc.ChannelCredentials;
import io.grpc.Grpc;
import io.grpc.ManagedChannel;
import io.grpc.TlsChannelCredentials;
import org.hyperledger.fabric.client.Gateway;
import org.hyperledger.fabric.client.identity.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.PrivateKey;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;


@Configuration
public class GatewayConfig {

    @Bean
    public Gateway gateway(Environment env)
            throws IOException, CertificateException, InvalidKeyException {
        // Read environment properties
        Path cryptoPath = Paths.get(Objects
                .requireNonNull(env.getProperty("gateway.crypto-path")));
        Path certDirPath = cryptoPath.resolve(Paths.get(Objects
                .requireNonNull(env.getProperty("gateway.cert-dir-path"))));
        Path keyDirPath = cryptoPath.resolve(Paths.get(Objects
                .requireNonNull(env.getProperty("gateway.key-dir-path"))));
        Path tlsCertPath = cryptoPath.resolve(Paths.get(Objects
                .requireNonNull(env.getProperty("gateway.tls-cert-path"))));
        String mspId = env.getProperty("gateway.msp-id");
        String peerEndpoint = env.getProperty("gateway.peer-endpoint");
        String overrideAuth = env.getProperty("gateway.override-auth");

        // The gRPC client connection should be shared by all Gateway connections to this endpoint
        ManagedChannel channel = newGrpcConnection(tlsCertPath, peerEndpoint, overrideAuth);

        return Gateway
                .newInstance()
                .identity(newIdentity(certDirPath, mspId))
                .signer(newSigner(keyDirPath))
                .connection(channel)
                .evaluateOptions(options -> options.withDeadlineAfter(5, TimeUnit.SECONDS))
                .connect();
    }

    private ManagedChannel newGrpcConnection(Path tlsCertPath, String peerEndpoint,
                                             String overrideAuth) throws IOException {
        ChannelCredentials tlsCredentials = TlsChannelCredentials.newBuilder()
                .trustManager(tlsCertPath.toFile())
                .build();
        return Grpc.newChannelBuilder(peerEndpoint, tlsCredentials)
                .overrideAuthority(overrideAuth)
                .build();
    }

    private Identity newIdentity(Path certDirPath, String mspId)
            throws IOException, CertificateException {
        try (BufferedReader reader = Files.newBufferedReader(this.getFirstFile(certDirPath))) {
            X509Certificate certificate = Identities.readX509Certificate(reader);
            return new X509Identity(mspId, certificate);
        }
    }

    private Signer newSigner(Path keyDirPath) throws IOException, InvalidKeyException {
        try (BufferedReader reader = Files.newBufferedReader(this.getFirstFile(keyDirPath))) {
            PrivateKey privateKey = Identities.readPrivateKey(reader);
            return Signers.newPrivateKeySigner(privateKey);
        }
    }

    private Path getFirstFile(Path dirPath) throws IOException {
        try (Stream<Path> keyFiles = Files.list(dirPath)) {
            return keyFiles.findFirst().orElseThrow();
        }
    }
}
