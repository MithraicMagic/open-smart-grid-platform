package org.opensmartgridplatform.adapter.protocol.opc.ua.application.services;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.util.regex.Pattern;

import org.eclipse.milo.opcua.sdk.server.util.HostnameUtil;
import org.eclipse.milo.opcua.stack.core.util.SelfSignedCertificateBuilder;
import org.eclipse.milo.opcua.stack.core.util.SelfSignedCertificateGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class KeyStoreLoader {

    private static final Pattern IP_ADDR_PATTERN = Pattern.compile(
            "^(([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.){3}([01]?\\d\\d?|2[0-4]\\d|25[0-5])$");

    private static final String CLIENT_ALIAS = "client-ai";
    private static final char[] PASSWORD = "password".toCharArray();

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private X509Certificate clientCertificate;
    private KeyPair clientKeyPair;

    //todo make this save into the database instead
    KeyStoreLoader load(final Path baseDir) throws Exception {
        final KeyStore keyStore = KeyStore.getInstance("PKCS12");

        final Path serverKeyStore = baseDir.resolve("example-client.pfx");

        this.logger.info("Loading KeyStore at {}", serverKeyStore);

        if (!Files.exists(serverKeyStore)) {
            keyStore.load(null, PASSWORD);

            final KeyPair keyPair = SelfSignedCertificateGenerator.generateRsaKeyPair(2048);

            //todo changes more of these values
            final SelfSignedCertificateBuilder builder = new SelfSignedCertificateBuilder(keyPair)
                    .setCommonName("Eclipse Milo Client")
                    .setOrganization("GXF")
                    .setOrganizationalUnit("dev")
                    .setLocalityName("Folsom")
                    .setStateName("CA")
                    .setCountryCode("US")
                    .setApplicationUri("urn:eclipse:milo:examples:client")
                    .addDnsName("localhost")
                    .addIpAddress("127.0.0.1");

            // Get as many hostnames and IP addresses as we can listed in the certificate.
            for (final String hostname : HostnameUtil.getHostnames("0.0.0.0")) {
                if (IP_ADDR_PATTERN.matcher(hostname).matches()) {
                    builder.addIpAddress(hostname);
                } else {
                    builder.addDnsName(hostname);
                }
            }

            final X509Certificate certificate = builder.build();

            keyStore.setKeyEntry(CLIENT_ALIAS, keyPair.getPrivate(), PASSWORD, new X509Certificate[]{certificate});
            try (final OutputStream out = Files.newOutputStream(serverKeyStore)) {
                keyStore.store(out, PASSWORD);
            }
        } else {
            try (final InputStream in = Files.newInputStream(serverKeyStore)) {
                keyStore.load(in, PASSWORD);
            }
        }

        final Key serverPrivateKey = keyStore.getKey(CLIENT_ALIAS, PASSWORD);
        if (serverPrivateKey instanceof PrivateKey) {
            this.clientCertificate = (X509Certificate) keyStore.getCertificate(CLIENT_ALIAS);
            final PublicKey serverPublicKey = this.clientCertificate.getPublicKey();
            this.clientKeyPair = new KeyPair(serverPublicKey, (PrivateKey) serverPrivateKey);
        }

        return this;
    }

    X509Certificate getClientCertificate() {
        return this.clientCertificate;
    }

    KeyPair getClientKeyPair() {
        return this.clientKeyPair;
    }

}
