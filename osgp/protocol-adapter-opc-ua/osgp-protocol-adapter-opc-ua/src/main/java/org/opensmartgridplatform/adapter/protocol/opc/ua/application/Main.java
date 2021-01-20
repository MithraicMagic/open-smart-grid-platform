package org.opensmartgridplatform.adapter.protocol.opc.ua.application;

import org.opensmartgridplatform.adapter.protocol.opc.ua.application.services.ClientRunner;

class Main {
    public static void main(final String[] args) {
        new ClientRunner().onConnect(false);
    }
}