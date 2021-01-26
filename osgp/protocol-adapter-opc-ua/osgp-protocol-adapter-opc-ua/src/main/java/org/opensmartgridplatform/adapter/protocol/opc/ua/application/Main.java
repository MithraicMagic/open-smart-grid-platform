package org.opensmartgridplatform.adapter.protocol.opc.ua.application;

import org.opensmartgridplatform.adapter.protocol.opc.ua.application.services.ClientRunner;

class Main {
    public static void main(final String[] args) {
        final ClientRunner clientRunner = new ClientRunner();
        clientRunner.tryConnect("opc.tcp://milo.digitalpetri.com:62541/milo");
        clientRunner.runMethod();
        clientRunner.closeConnection();
    }
}