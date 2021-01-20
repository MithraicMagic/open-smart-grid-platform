package org.opensmartgridplatform.adapter.protocol.opc.ua.application.services;

import static org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.Unsigned.uint;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.eclipse.milo.examples.server.ExampleServer;
import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.eclipse.milo.opcua.stack.core.Stack;
import org.eclipse.milo.opcua.stack.core.types.builtin.LocalizedText;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientRunner {
    //todo create client method that creates a client with given settings
    private final Client client = new Client();

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final CompletableFuture<OpcUaClient> future = new CompletableFuture<>();
    private ExampleServer exampleServer;

    public void onConnect(final boolean serverRequired) {
        try {
            final OpcUaClient runningClient = this.createClient();

            this.future.whenCompleteAsync((c, ex) -> {
                if (ex != null) {
                    this.logger.error("Error running example: {}", ex.getMessage(), ex);
                }

                try {
                    runningClient.disconnect().get();
                    if (serverRequired && this.exampleServer != null) {
                        this.exampleServer.shutdown().get();
                    }
                    Stack.releaseSharedResources();
                } catch (final InterruptedException | ExecutionException e) {
                    this.logger.error("Error disconnecting: {}", e.getMessage(), e);
                }

                try {
                    Thread.sleep(1000);
                    System.exit(0);
                } catch (final InterruptedException e) {
                    e.printStackTrace();
                }
            });

            try {
                this.client.run(runningClient, this.future);
                this.future.get(15, TimeUnit.SECONDS);
            } catch (final Throwable t) {
                this.logger.error("Error running client example: {}", t.getMessage(), t);
                this.future.completeExceptionally(t);
            }
        } catch (final Throwable t) {
            this.logger.error("Error getting client: {}", t.getMessage(), t);

            this.future.completeExceptionally(t);

            try {
                Thread.sleep(1000);
                System.exit(0);
            } catch (final InterruptedException e) {
                e.printStackTrace();
            }
        }

        try {
            Thread.sleep(999_999_999);
        } catch (final InterruptedException e) {
            e.printStackTrace();
        }
    }

    private OpcUaClient createClient() throws Exception {
        final Path securityTempDir = Paths.get(System.getProperty("java.io.tmpdir"), "security");
        Files.createDirectories(securityTempDir);
        if (!Files.exists(securityTempDir)) {
            throw new Exception("unable to create security dir: " + securityTempDir);
        }

        LoggerFactory.getLogger(this.getClass())
                .info("security temp dir: {}", securityTempDir.toAbsolutePath());

        //todo see if we can load this in once
        final KeyStoreLoader loader = new KeyStoreLoader().load(securityTempDir);

        return OpcUaClient.create(this.client.getEndpointUrl(),
                endpoints ->
                        endpoints.stream()
                                .filter(this.client.endpointFilter())
                                .findFirst(),
                configBuilder ->
                        configBuilder
                                .setApplicationName(LocalizedText.english("eclipse milo opc-ua client"))
                                .setApplicationUri("urn:eclipse:milo:examples:client")
                                .setCertificate(loader.getClientCertificate())
                                .setKeyPair(loader.getClientKeyPair())
                                .setIdentityProvider(this.client.getIdentityProvider())
                                .setRequestTimeout(uint(5000))
                                .build()
        );
    }
}
