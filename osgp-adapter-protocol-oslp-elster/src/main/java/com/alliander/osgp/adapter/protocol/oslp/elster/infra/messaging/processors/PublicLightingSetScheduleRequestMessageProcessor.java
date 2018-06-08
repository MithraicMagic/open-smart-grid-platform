/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.protocol.oslp.elster.infra.messaging.processors;

import java.io.IOException;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.alliander.osgp.adapter.protocol.oslp.elster.device.DeviceResponse;
import com.alliander.osgp.adapter.protocol.oslp.elster.device.DeviceResponseHandler;
import com.alliander.osgp.adapter.protocol.oslp.elster.device.requests.SetScheduleDeviceRequest;
import com.alliander.osgp.adapter.protocol.oslp.elster.device.responses.GetConfigurationDeviceResponse;
import com.alliander.osgp.adapter.protocol.oslp.elster.infra.messaging.DeviceRequestMessageProcessor;
import com.alliander.osgp.adapter.protocol.oslp.elster.infra.messaging.DeviceRequestMessageType;
import com.alliander.osgp.adapter.protocol.oslp.elster.infra.messaging.OslpEnvelopeProcessor;
import com.alliander.osgp.dto.valueobjects.RelayTypeDto;
import com.alliander.osgp.dto.valueobjects.ScheduleDto;
import com.alliander.osgp.dto.valueobjects.ScheduleMessageDataContainerDto;
import com.alliander.osgp.dto.valueobjects.ScheduleMessageTypeDto;
import com.alliander.osgp.oslp.OslpEnvelope;
import com.alliander.osgp.oslp.SignedOslpEnvelopeDto;
import com.alliander.osgp.oslp.UnsignedOslpEnvelopeDto;
import com.alliander.osgp.shared.infra.jms.Constants;

/**
 * Class for processing public lighting set schedule request messages
 */
@Component("oslpPublicLightingSetScheduleRequestMessageProcessor")
public class PublicLightingSetScheduleRequestMessageProcessor extends DeviceRequestMessageProcessor
        implements OslpEnvelopeProcessor {
    /**
     * Logger for this class
     */
    private static final Logger LOGGER = LoggerFactory
            .getLogger(PublicLightingSetScheduleRequestMessageProcessor.class);

    public PublicLightingSetScheduleRequestMessageProcessor() {
        super(DeviceRequestMessageType.SET_LIGHT_SCHEDULE);
    }

    @Override
    public void processMessage(final ObjectMessage message) {
        LOGGER.debug("Processing public lighting set schedule request message");

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
            final ScheduleDto schedule = (ScheduleDto) message.getObject();
            ScheduleMessageDataContainerDto.Builder builder = new ScheduleMessageDataContainerDto.Builder(schedule);
            if (schedule.getAstronomicalSunriseOffset() != null || schedule.getAstronomicalSunsetOffset() != null) {
                builder = builder.withScheduleMessageType(ScheduleMessageTypeDto.RETRIEVE_CONFIGURATION);
            }
            final ScheduleMessageDataContainerDto scheduleMessageDataContainer = builder.build();

            LOGGER.info("Calling DeviceService function: {} for domain: {} {}", messageType, domain, domainVersion);

            final SetScheduleDeviceRequest deviceRequest = new SetScheduleDeviceRequest(organisationIdentification,
                    deviceIdentification, correlationUid, scheduleMessageDataContainer, RelayTypeDto.LIGHT, domain,
                    domainVersion, messageType, ipAddress, retryCount, isScheduled);

            this.deviceService.setSchedule(deviceRequest);
        } catch (final Exception e) {
            this.handleError(e, correlationUid, organisationIdentification, deviceIdentification, domain, domainVersion,
                    messageType, retryCount);
        }
    }

    @Override
    public void processSignedOslpEnvelope(final String deviceIdentification,
            final SignedOslpEnvelopeDto signedOslpEnvelopeDto) {

        final UnsignedOslpEnvelopeDto unsignedOslpEnvelopeDto = signedOslpEnvelopeDto.getUnsignedOslpEnvelopeDto();
        final OslpEnvelope oslpEnvelope = signedOslpEnvelopeDto.getOslpEnvelope();
        final String correlationUid = unsignedOslpEnvelopeDto.getCorrelationUid();
        final String organisationIdentification = unsignedOslpEnvelopeDto.getOrganisationIdentification();
        final String domain = unsignedOslpEnvelopeDto.getDomain();
        final String domainVersion = unsignedOslpEnvelopeDto.getDomainVersion();
        final String messageType = unsignedOslpEnvelopeDto.getMessageType();
        final String ipAddress = unsignedOslpEnvelopeDto.getIpAddress();
        final int retryCount = unsignedOslpEnvelopeDto.getRetryCount();
        final boolean isScheduled = unsignedOslpEnvelopeDto.isScheduled();
        final ScheduleMessageDataContainerDto scheduleDataContainer = (ScheduleMessageDataContainerDto) unsignedOslpEnvelopeDto
                .getExtraData();

        final SetScheduleDeviceRequest deviceRequest = new SetScheduleDeviceRequest(organisationIdentification,
                deviceIdentification, correlationUid, scheduleDataContainer, RelayTypeDto.LIGHT, domain, domainVersion,
                messageType, ipAddress, retryCount, isScheduled);

        final DeviceResponseHandler deviceResponseHandler = new DeviceResponseHandler() {

            private final ScheduleMessageTypeDto scheduleMessageTypeDto = deviceRequest.getScheduleDataContainer()
                    .getScheduleMessageType();

            @Override
            public void handleResponse(final DeviceResponse deviceResponse) {

                switch (this.scheduleMessageTypeDto) {
                case RETRIEVE_CONFIGURATION:
                    final GetConfigurationDeviceResponse response = (GetConfigurationDeviceResponse) deviceResponse;
                    PublicLightingSetScheduleRequestMessageProcessor.this
                            .handleGetConfigurationBeforeSetScheduleResponse(deviceRequest, response);
                    break;
                case SET_ASTRONOMICAL_OFFSETS:
                    PublicLightingSetScheduleRequestMessageProcessor.this
                            .handleSetScheduleAstronomicalOffsetsResponse(deviceRequest);
                    break;
                default:
                    PublicLightingSetScheduleRequestMessageProcessor.this.handleEmptyDeviceResponse(deviceResponse,
                            PublicLightingSetScheduleRequestMessageProcessor.this.responseMessageSender, domain,
                            domainVersion, messageType, retryCount);
                }
            }

            @Override
            public void handleException(final Throwable t, final DeviceResponse deviceResponse) {
                PublicLightingSetScheduleRequestMessageProcessor.this.handleUnableToConnectDeviceResponse(
                        deviceResponse, t, unsignedOslpEnvelopeDto.getExtraData(),
                        PublicLightingSetScheduleRequestMessageProcessor.this.responseMessageSender, deviceResponse,
                        domain, domainVersion, messageType, isScheduled, retryCount);
            }
        };

        try {
            this.deviceService.doSetSchedule(oslpEnvelope, deviceRequest, deviceResponseHandler, ipAddress, domain,
                    domainVersion, messageType, retryCount, isScheduled, scheduleDataContainer.getPageInfo());
        } catch (final IOException e) {
            this.handleError(e, correlationUid, organisationIdentification, deviceIdentification, domain, domainVersion,
                    messageType, retryCount);
        }
    }

    private void handleGetConfigurationBeforeSetScheduleResponse(final SetScheduleDeviceRequest deviceRequest,
            final GetConfigurationDeviceResponse deviceResponse) {
        // Configuration is retrieved, so now continue with setting the
        // astronomical offsets
        LOGGER.info("Calling DeviceService function: {} for domain: {} {}", deviceRequest.getMessageType(),
                deviceRequest.getDomain(), deviceRequest.getDomainVersion());

        final ScheduleMessageDataContainerDto scheduleDataContainer = new ScheduleMessageDataContainerDto.Builder(
                deviceRequest.getScheduleDataContainer().getSchedule())
                        .withConfiguration(deviceResponse.getConfiguration())
                        .withScheduleMessageType(ScheduleMessageTypeDto.SET_ASTRONOMICAL_OFFSETS).build();

        final SetScheduleDeviceRequest newDeviceRequest = new SetScheduleDeviceRequest(
                deviceRequest.getOrganisationIdentification(), deviceRequest.getDeviceIdentification(),
                deviceRequest.getCorrelationUid(), scheduleDataContainer, RelayTypeDto.LIGHT, deviceRequest.getDomain(),
                deviceRequest.getDomainVersion(), deviceRequest.getMessageType(), deviceRequest.getIpAddress(),
                deviceRequest.getRetryCount(), deviceRequest.isScheduled());

        this.deviceService.setSchedule(newDeviceRequest);
    }

    private void handleSetScheduleAstronomicalOffsetsResponse(final SetScheduleDeviceRequest deviceRequest) {

        // Astronomical offsets are set, so now continue with the actual
        // schedule
        LOGGER.info("Calling DeviceService function: {} for domain: {} {}", deviceRequest.getMessageType(),
                deviceRequest.getDomain(), deviceRequest.getDomainVersion());

        final ScheduleMessageDataContainerDto scheduleDataContainer = new ScheduleMessageDataContainerDto.Builder(
                deviceRequest.getScheduleDataContainer().getSchedule())
                        .withScheduleMessageType(ScheduleMessageTypeDto.SET_SCHEDULE).build();

        final SetScheduleDeviceRequest newDeviceRequest = new SetScheduleDeviceRequest(
                deviceRequest.getOrganisationIdentification(), deviceRequest.getDeviceIdentification(),
                deviceRequest.getCorrelationUid(), scheduleDataContainer, RelayTypeDto.LIGHT, deviceRequest.getDomain(),
                deviceRequest.getDomainVersion(), deviceRequest.getMessageType(), deviceRequest.getIpAddress(),
                deviceRequest.getRetryCount(), deviceRequest.isScheduled());

        this.deviceService.setSchedule(newDeviceRequest);
    }

}
