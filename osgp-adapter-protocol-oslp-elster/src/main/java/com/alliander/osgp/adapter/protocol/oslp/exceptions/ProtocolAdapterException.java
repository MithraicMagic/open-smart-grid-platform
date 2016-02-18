/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.protocol.oslp.exceptions;

public class ProtocolAdapterException extends Exception {

    /**
     * Serial Version UID.
     */
    private static final long serialVersionUID = 916943696172403469L;

    public ProtocolAdapterException(final String message) {
        super(message);
    }

    public ProtocolAdapterException(final String message, final Throwable throwable) {
        super(message, throwable);
    }
}
