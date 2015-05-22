/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.protocol.oslp.infra.messaging.processors;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.alliander.osgp.adapter.protocol.oslp.device.DeviceResponse;
import com.alliander.osgp.adapter.protocol.oslp.device.DeviceResponseHandler;
import com.alliander.osgp.adapter.protocol.oslp.device.requests.SetScheduleDeviceRequest;
import com.alliander.osgp.adapter.protocol.oslp.infra.messaging.DeviceRequestMessageProcessor;
import com.alliander.osgp.adapter.protocol.oslp.infra.messaging.DeviceRequestMessageType;
import com.alliander.osgp.dto.valueobjects.RelayType;
import com.alliander.osgp.dto.valueobjects.ScheduleMessageDataContainer;
import com.alliander.osgp.shared.infra.jms.Constants;

/**
 * Class for processing tariff switching set schedule request messages
 * 
 * @author CGI
 * 
 */
@Component("oslpTariffSwitchingSetScheduleRequestMessageProcessor")
public class TariffSwitchingSetScheduleRequestMessageProcessor extends DeviceRequestMessageProcessor {
    /**
     * Logger for this class
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(TariffSwitchingSetScheduleRequestMessageProcessor.class);

    public TariffSwitchingSetScheduleRequestMessageProcessor() {
        super(DeviceRequestMessageType.SET_TARIFF_SCHEDULE);
    }

    @Override
    public void processMessage(final ObjectMessage message) {
        LOGGER.debug("Processing tariff switching set schedule request message");

        String correlationUid = null;
        String domain = null;
        String domainVersion = null;
        String messageType = null;
        String organisationIdentification = null;
        String deviceIdentification = null;
        String ipAddress = null;
        Boolean isScheduled = null;
        int retryCount = 0;

        try {
            correlationUid = message.getJMSCorrelationID();
            domain = message.getStringProperty(Constants.DOMAIN);
            domainVersion = message.getStringProperty(Constants.DOMAIN_VERSION);
            messageType = message.getJMSType();
            organisationIdentification = message.getStringProperty(Constants.ORGANISATION_IDENTIFICATION);
            deviceIdentification = message.getStringProperty(Constants.DEVICE_IDENTIFICATION);
            ipAddress = message.getStringProperty(Constants.IP_ADDRESS);
            isScheduled = message.getBooleanProperty(Constants.IS_SCHEDULED);
            retryCount = message.getIntProperty(Constants.RETRY_COUNT);

        } catch (final JMSException e) {
            LOGGER.error("UNRECOVERABLE ERROR, unable to read ObjectMessage instance, giving up.", e);
            LOGGER.debug("correlationUid: {}", correlationUid);
            LOGGER.debug("domain: {}", domain);
            LOGGER.debug("domainVersion: {}", domainVersion);
            LOGGER.debug("messageType: {}", messageType);
            LOGGER.debug("organisationIdentification: {}", organisationIdentification);
            LOGGER.debug("deviceIdentification: {}", deviceIdentification);
            LOGGER.debug("ipAddress: {}", ipAddress);
            LOGGER.debug("scheduled: {}", isScheduled);
            return;
        }

        try {
            final ScheduleMessageDataContainer scheduleMessageDataContainer = (ScheduleMessageDataContainer) message.getObject();

            LOGGER.info("Calling DeviceService function: {} for domain: {} {}", messageType, domain, domainVersion);

            final DeviceResponseHandler deviceResponseHandler = new DeviceResponseHandler() {

                @Override
                public void handleResponse(final DeviceResponse deviceResponse) {
                    try {
                        TariffSwitchingSetScheduleRequestMessageProcessor.this.handleScheduledEmptyDeviceResponse(deviceResponse,
                                TariffSwitchingSetScheduleRequestMessageProcessor.this.responseMessageSender, message.getStringProperty(Constants.DOMAIN),
                                message.getStringProperty(Constants.DOMAIN_VERSION), message.getJMSType(),
                                message.propertyExists(Constants.IS_SCHEDULED) ? message.getBooleanProperty(Constants.IS_SCHEDULED) : false, message
                                        .getIntProperty(Constants.RETRY_COUNT));
                    } catch (final JMSException e) {
                        LOGGER.error("JMSException", e);
                    }

                }

                @Override
                public void handleException(final Throwable t, final DeviceResponse deviceResponse) {
                    try {
                        TariffSwitchingSetScheduleRequestMessageProcessor.this.handleUnableToConnectDeviceResponse(deviceResponse, t,
                                scheduleMessageDataContainer, TariffSwitchingSetScheduleRequestMessageProcessor.this.responseMessageSender, deviceResponse,
                                message.getStringProperty(Constants.DOMAIN), message.getStringProperty(Constants.DOMAIN_VERSION), message.getJMSType(),
                                message.propertyExists(Constants.IS_SCHEDULED) ? message.getBooleanProperty(Constants.IS_SCHEDULED) : false,
                                message.getIntProperty(Constants.RETRY_COUNT));
                    } catch (final JMSException e) {
                        LOGGER.error("JMSException", e);
                    }

                }
            };

            final SetScheduleDeviceRequest deviceRequest = new SetScheduleDeviceRequest(organisationIdentification, deviceIdentification, correlationUid,
                    scheduleMessageDataContainer.getScheduleList(), RelayType.TARIFF);

            this.deviceService.setSchedule(deviceRequest, deviceResponseHandler, ipAddress);

        } catch (final Exception e) {
            this.handleError(e, correlationUid, organisationIdentification, deviceIdentification, domain, domainVersion, messageType, retryCount);
        }
    }
}
