package org.opensmartgridplatform.adapter.protocol.opc.ua.application.services;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;

import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.eclipse.milo.opcua.sdk.client.api.identity.AnonymousProvider;
import org.eclipse.milo.opcua.sdk.client.api.identity.IdentityProvider;
import org.eclipse.milo.opcua.sdk.client.nodes.UaNode;
import org.eclipse.milo.opcua.stack.core.Identifiers;
import org.eclipse.milo.opcua.stack.core.UaException;
import org.eclipse.milo.opcua.stack.core.security.SecurityPolicy;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.structured.EndpointDescription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Client {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final String endPointUrl;

    public Client(final String url) {
        this.endPointUrl = url;
    }

    public String getEndpointUrl() {
        return this.endPointUrl;
    }

    public Predicate<EndpointDescription> endpointFilter() {
        return e -> true;
    }

    public SecurityPolicy getSecurityPolicy() {
        return SecurityPolicy.None;
    }

    public IdentityProvider getIdentityProvider() {
        return new AnonymousProvider();
    }

    public void run(final OpcUaClient client, final CompletableFuture<OpcUaClient> future) throws Exception {
        // synchronous connect
        client.connect().get();

        // start browsing at root folder
        //this.browseNode("", client, Identifiers.BrowseDescription);
        this.browseNode("", client, Identifiers.RootFolder);
        future.complete(client);
    }

    private void browseNode(final String indent, final OpcUaClient client, final NodeId browseRoot) {
        try {
            final List<? extends UaNode> nodes = client.getAddressSpace().browseNodes(browseRoot);

            for (final UaNode node : nodes) {
                this.logger.info("{} Node={}", indent, node.getBrowseName().getName());

                // recursively browse to children
                this.browseNode(indent + "  ", client, node.getNodeId());
            }
        } catch (final UaException e) {
            this.logger.error("Browsing nodeId={} failed: {}", browseRoot, e.getMessage(), e);
        }
    }
}
