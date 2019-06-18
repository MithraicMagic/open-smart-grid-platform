/**
 * Copyright 2015 Smart Society Services B.V.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.periodicmeterreads;

import org.joda.time.DateTime;
import org.openmuc.jdlms.AttributeAddress;
import org.openmuc.jdlms.GetResult;
import org.openmuc.jdlms.ObisCode;
import org.openmuc.jdlms.datatypes.DataObject;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.AttributeAddressForProfile;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.DlmsCaptureObject;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.DlmsObjectConfigService;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.DlmsObjectType;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.model.Medium;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.model.ProfileCaptureTime;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.AmrProfileStatusCodeHelper;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.DlmsHelper;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.JdlmsObjectToStringUtil;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionManager;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.BufferedDateTimeValidationException;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Component()
public class GetPeriodicMeterReadsCommandExecutor
        extends AbstractPeriodicMeterReadsCommandExecutor<PeriodicMeterReadsRequestDto, PeriodicMeterReadsResponseDto> {

    static final String PERIODIC_E_METER_READS = "Periodic E-Meter Reads";
    private static final String FORMAT_DESCRIPTION = "GetPeriodicMeterReads %s from %s until %s, retrieve attribute: %s";

    private static final Logger LOGGER = LoggerFactory.getLogger(GetPeriodicMeterReadsCommandExecutor.class);

    private final DlmsHelper dlmsHelper;
    private final DlmsObjectConfigService dlmsObjectConfigService;
    private final AmrProfileStatusCodeHelper amrProfileStatusCodeHelper;

    @Autowired
    public GetPeriodicMeterReadsCommandExecutor(final DlmsHelper dlmsHelper,
                                                final AmrProfileStatusCodeHelper amrProfileStatusCodeHelper,
                                                final DlmsObjectConfigService dlmsObjectConfigService) {
        super(PeriodicMeterReadsRequestDataDto.class);
        this.dlmsHelper = dlmsHelper;
        this.amrProfileStatusCodeHelper = amrProfileStatusCodeHelper;
        this.dlmsObjectConfigService = dlmsObjectConfigService;
    }

    @Override
    public PeriodicMeterReadsRequestDto fromBundleRequestInput(final ActionRequestDto bundleInput)
            throws ProtocolAdapterException {

        this.checkActionRequestType(bundleInput);
        final PeriodicMeterReadsRequestDataDto periodicMeterReadsRequestDataDto =
                (PeriodicMeterReadsRequestDataDto) bundleInput;

        return new PeriodicMeterReadsRequestDto(periodicMeterReadsRequestDataDto.getPeriodType(),
                periodicMeterReadsRequestDataDto.getBeginDate(), periodicMeterReadsRequestDataDto.getEndDate());
    }

    @Override
    public PeriodicMeterReadsResponseDto execute(final DlmsConnectionManager conn, final DlmsDevice device,
                                                 final PeriodicMeterReadsRequestDto periodicMeterReadsQuery) throws ProtocolAdapterException {

        if (periodicMeterReadsQuery == null) {
            throw new IllegalArgumentException(
                    "PeriodicMeterReadsQuery should contain PeriodType, BeginDate and EndDate.");
        }

        final PeriodTypeDto queryPeriodType = periodicMeterReadsQuery.getPeriodType();

        final DateTime from = new DateTime(periodicMeterReadsQuery.getBeginDate());
        final DateTime to = new DateTime(periodicMeterReadsQuery.getEndDate());

        final AttributeAddressForProfile profileBufferAddress = this.getProfileBufferAddress(queryPeriodType, from, to,
                device);

        final List<AttributeAddress> scalerUnitAddresses = this.getScalerUnitAddresses(profileBufferAddress);

        ProfileCaptureTime intervalTime = getProfileCaptureTime(device, this.dlmsObjectConfigService, Medium.ELECTRICITY);

        LOGGER.debug("Retrieving current billing period and profiles for period type: {}, from: {}, to: {}", queryPeriodType,
                from, to);

        // Get results one by one because getWithList does not work for all devices
        final List<GetResult> getResultList = new ArrayList<>();

        final List<AttributeAddress> allAttributeAddresses = new ArrayList<>();
        allAttributeAddresses.add(profileBufferAddress.getAttributeAddress());
        allAttributeAddresses.addAll(scalerUnitAddresses);

        for (final AttributeAddress address : allAttributeAddresses) {

            conn.getDlmsMessageListener()
                    .setDescription(String.format(FORMAT_DESCRIPTION, queryPeriodType, from, to,
                            JdlmsObjectToStringUtil.describeAttributes(address)));

            getResultList.addAll(this.dlmsHelper.getAndCheck(conn, device,
                    "retrieve periodic meter reads for " + queryPeriodType, address));
        }

        LOGGER.info("Received getResult: {} ", getResultList);

        final DataObject resultData = this.dlmsHelper.readDataObject(getResultList.get(0), PERIODIC_E_METER_READS);
        final List<DataObject> bufferedObjectsList = resultData.getValue();

        final List<PeriodicMeterReadsResponseItemDto> periodicMeterReads = new ArrayList<>();
        for (final DataObject bufferedObject : bufferedObjectsList) {
            final List<DataObject> bufferedObjectValue = bufferedObject.getValue();

            try {

                periodicMeterReads.add(
                        this.convertToResponseItem(periodicMeterReadsQuery, bufferedObjectValue,
                                getResultList, profileBufferAddress, scalerUnitAddresses, periodicMeterReads, intervalTime));
            } catch (final BufferedDateTimeValidationException e) {
                LOGGER.warn(e.getMessage(), e);
            }
        }

        return new PeriodicMeterReadsResponseDto(queryPeriodType, periodicMeterReads);
    }

    private PeriodicMeterReadsResponseItemDto convertToResponseItem(
            final PeriodicMeterReadsRequestDto periodicMeterReadsQuery, final List<DataObject> bufferedObjects,
            final List<GetResult> getResultList, final AttributeAddressForProfile attributeAddressForProfile,
            final List<AttributeAddress> attributeAddresses,
            final List<PeriodicMeterReadsResponseItemDto> periodicMeterReads,
            final ProfileCaptureTime intervalTime)
            throws ProtocolAdapterException, BufferedDateTimeValidationException {

        LOGGER.info("Converting bufferObject with value: {} ", bufferedObjects);

        final Date previousLogTime = getPreviousLogTime(periodicMeterReads);
        final Date logTime = readClock(periodicMeterReadsQuery, bufferedObjects, attributeAddressForProfile, previousLogTime, intervalTime, this.dlmsHelper);

        final AmrProfileStatusCodeDto status = this.readStatus(bufferedObjects, attributeAddressForProfile);

        if (periodicMeterReadsQuery.getPeriodType() == PeriodTypeDto.INTERVAL) {
            DlmsMeterValueDto importValue = this.getScaledMeterValue(bufferedObjects, getResultList,
                    attributeAddresses, attributeAddressForProfile, DlmsObjectType.ACTIVE_ENERGY_IMPORT,
                    "positiveActiveEnergy");
            DlmsMeterValueDto exportValue = this.getScaledMeterValue(bufferedObjects, getResultList,
                    attributeAddresses, attributeAddressForProfile, DlmsObjectType.ACTIVE_ENERGY_EXPORT,
                    "negativeActiveEnergy");

            LOGGER.info("Resulting values: LogTime: {}, status: {}, importValue {}, exportValue {} ", logTime, status
                    , importValue, exportValue);

            return new PeriodicMeterReadsResponseItemDto(logTime, importValue, exportValue, status);
        } else {
            DlmsMeterValueDto importValueRate1 = this.getScaledMeterValue(bufferedObjects, getResultList,
                    attributeAddresses, attributeAddressForProfile, DlmsObjectType.ACTIVE_ENERGY_IMPORT_RATE_1,
                    "positiveActiveEnergyTariff1");
            DlmsMeterValueDto importValueRate2 = this.getScaledMeterValue(bufferedObjects, getResultList,
                    attributeAddresses, attributeAddressForProfile, DlmsObjectType.ACTIVE_ENERGY_IMPORT_RATE_2,
                    "positiveActiveEnergyTariff2");
            DlmsMeterValueDto exportValueRate1 = this.getScaledMeterValue(bufferedObjects, getResultList,
                    attributeAddresses, attributeAddressForProfile, DlmsObjectType.ACTIVE_ENERGY_EXPORT_RATE_1,
                    "negativeActiveEnergyTariff1");
            DlmsMeterValueDto exportValueRate2 = this.getScaledMeterValue(bufferedObjects, getResultList,
                    attributeAddresses, attributeAddressForProfile, DlmsObjectType.ACTIVE_ENERGY_EXPORT_RATE_2,
                    "negativeActiveEnergyTariff2");

            LOGGER.info("Resulting values: LogTime: {}, status: {}, importRate1Value {}, importRate2Value {}, "
                            + "exportRate1Value {}, exportRate2Value {} ", logTime, status,
                    importValueRate1, importValueRate2, exportValueRate1, exportValueRate2);

            return new PeriodicMeterReadsResponseItemDto(logTime, importValueRate1, importValueRate2,
                    exportValueRate1, exportValueRate2, status);
        }
    }


    protected Date getPreviousLogTime(final List<PeriodicMeterReadsResponseItemDto> periodicMeterReads) {

        if (periodicMeterReads.isEmpty()) {
            return null;
        }

        return periodicMeterReads.get(periodicMeterReads.size() - 1).getLogTime();
    }


    private AmrProfileStatusCodeDto readStatus(final List<DataObject> bufferedObjects,
                                               final AttributeAddressForProfile attributeAddressForProfile) throws ProtocolAdapterException {

        final Integer statusIndex = attributeAddressForProfile.getIndex(DlmsObjectType.AMR_STATUS, null);

        AmrProfileStatusCodeDto amrProfileStatusCode = null;

        if (statusIndex != null) {
            amrProfileStatusCode = this.readAmrProfileStatusCode(bufferedObjects.get(statusIndex));
        }

        return amrProfileStatusCode;
    }

    private DlmsMeterValueDto getScaledMeterValue(final List<DataObject> bufferedObjects,
                                                  final List<GetResult> getResultList, final List<AttributeAddress> attributeAddresses,
                                                  final AttributeAddressForProfile attributeAddressForProfile, DlmsObjectType objectType,
                                                  String description) throws ProtocolAdapterException {

        final DataObject importValue = this.readValue(bufferedObjects, attributeAddressForProfile, objectType);
        final DataObject importScalerUnit = this.readScalerUnit(getResultList, attributeAddresses,
                attributeAddressForProfile, objectType);

        return this.dlmsHelper.getScaledMeterValue(importValue, importScalerUnit, description);
    }

    private DataObject readValue(final List<DataObject> bufferedObjects,
                                 final AttributeAddressForProfile attributeAddressForProfile, DlmsObjectType objectType) {

        final Integer valueIndex = attributeAddressForProfile.getIndex(objectType, 2);

        DataObject value = null;

        if (valueIndex != null) {
            value = bufferedObjects.get(valueIndex);
        }

        return value;
    }

    private DataObject readScalerUnit(final List<GetResult> getResultList,
                                      final List<AttributeAddress> attributeAddresses,
                                      final AttributeAddressForProfile attributeAddressForProfile, DlmsObjectType objectType) {

        final DlmsCaptureObject captureObject =
                attributeAddressForProfile.getCaptureObject(objectType);

        int index = 0;
        Integer scalerUnitIndex = null;
        for (final AttributeAddress address : attributeAddresses) {
            final String obisCode = captureObject.getRelatedObject().getObisCode();
            if (address.getInstanceId().equals(new ObisCode(obisCode))) {
                scalerUnitIndex = index;
            }
            index++;
        }

        // Get scaler unit from result list. Note: "index + 1" because the first result is the array with values
        // and should be skipped. The first scaler unit is at index 1.
        if (scalerUnitIndex != null) {
            return getResultList.get(scalerUnitIndex + 1).getResultData();
        }

        return null;
    }

    private AttributeAddressForProfile getProfileBufferAddress(final PeriodTypeDto periodType,
                                                               final DateTime beginDateTime, final DateTime endDateTime,
                                                               final DlmsDevice device) throws ProtocolAdapterException {

        final DlmsObjectType type = DlmsObjectType.getTypeForPeriodType(periodType);

        // Add the attribute address for the profile
        final AttributeAddressForProfile attributeAddressProfile = this.dlmsObjectConfigService.findAttributeAddressForProfile(
                device, type, 0, beginDateTime, endDateTime, Medium.ELECTRICITY)
                .orElseThrow(() -> new ProtocolAdapterException("No address found for " + type));

        LOGGER.info("Dlms object config service returned profile buffer address {} ", attributeAddressProfile);

        return attributeAddressProfile;
    }

    private List<AttributeAddress> getScalerUnitAddresses(final AttributeAddressForProfile attributeAddressForProfile) {

        final List<AttributeAddress> attributeAddresses =
                this.dlmsObjectConfigService.getAttributeAddressesForScalerUnit(attributeAddressForProfile, 0);

        LOGGER.info("Dlms object config service returned scaler unit addresses {} ", attributeAddresses);

        return attributeAddresses;
    }

    /**
     * Reads AmrProfileStatusCode from DataObject holding a bitvalue in a
     * numeric datatype.
     *
     * @param amrProfileStatusData AMR profile register value.
     * @return AmrProfileStatusCode object holding status enum values.
     * @throws ProtocolAdapterException on invalid register data.
     */
    private AmrProfileStatusCodeDto readAmrProfileStatusCode(final DataObject amrProfileStatusData)
            throws ProtocolAdapterException {

        if (!amrProfileStatusData.isNumber()) {
            throw new ProtocolAdapterException("Could not read AMR profile register data. Invalid data type.");
        }

        final Set<AmrProfileStatusCodeFlagDto> flags = this.amrProfileStatusCodeHelper
                .toAmrProfileStatusCodeFlags(amrProfileStatusData.getValue());
        return new AmrProfileStatusCodeDto(flags);
    }
}
