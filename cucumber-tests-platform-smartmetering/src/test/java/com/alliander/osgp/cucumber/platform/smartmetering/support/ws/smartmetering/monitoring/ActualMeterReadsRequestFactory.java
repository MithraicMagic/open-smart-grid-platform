/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.cucumber.platform.smartmetering.support.ws.smartmetering.monitoring;

import com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.ActualMeterReadsAsyncRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.ActualMeterReadsRequest;
import com.alliander.osgp.cucumber.platform.smartmetering.support.ws.smartmetering.RequestFactoryHelper;

public class ActualMeterReadsRequestFactory {

    private ActualMeterReadsRequestFactory() {
        // Private constructor for utility class
    }

    public static ActualMeterReadsRequest forDevice() {
        final ActualMeterReadsRequest request = new ActualMeterReadsRequest();
        request.setDeviceIdentification(RequestFactoryHelper.getDeviceIdentificationFromScenarioContext());
        return request;
    }

    public static ActualMeterReadsAsyncRequest fromScenarioContext() {
        final ActualMeterReadsAsyncRequest asyncRequest = new ActualMeterReadsAsyncRequest();
        asyncRequest.setCorrelationUid(RequestFactoryHelper.getCorrelationUidFromScenarioContext());
        asyncRequest.setDeviceIdentification(RequestFactoryHelper.getDeviceIdentificationFromScenarioContext());
        return asyncRequest;
    }

}
