/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.osgp.adapter.protocol.dlms.domain.commands;

import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

import org.openmuc.jdlms.AccessResultCode;
import org.openmuc.jdlms.ClientConnection;
import org.openmuc.jdlms.DataObject;
import org.openmuc.jdlms.GetRequestParameter;
import org.openmuc.jdlms.GetResult;
import org.openmuc.jdlms.ObisCode;
import org.openmuc.jdlms.RequestParameterFactory;
import org.osgp.adapter.protocol.dlms.application.mapping.DataObjectToEventListConverter;
import org.osgp.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alliander.osgp.dto.valueobjects.smartmetering.Event;
import com.alliander.osgp.dto.valueobjects.smartmetering.EventLogCategory;
import com.alliander.osgp.dto.valueobjects.smartmetering.FindEventsQuery;

@Component()
public class RetrieveEventsCommandExecutor implements CommandExecutor<FindEventsQuery, List<Event>> {

    private static final Logger LOGGER = LoggerFactory.getLogger(RetrieveEventsCommandExecutor.class);

    private static final int CLASS_ID = 7;
    private static final int ATTRIBUTE_ID = 2;

    @Autowired
    DataObjectToEventListConverter dataObjectToEventListConverter;

    // @formatter:off
    private static final EnumMap<EventLogCategory, ObisCode> EVENT_LOG_CATEGORY_OBISCODE_MAP = new EnumMap<>(EventLogCategory.class);
    static{
        EVENT_LOG_CATEGORY_OBISCODE_MAP.put(EventLogCategory.STANDARD_EVENT_LOG,        new ObisCode("0.0.99.98.0.255"));
        EVENT_LOG_CATEGORY_OBISCODE_MAP.put(EventLogCategory.FRAUD_DETECTION_LOG,       new ObisCode("0.0.99.98.1.255"));
        EVENT_LOG_CATEGORY_OBISCODE_MAP.put(EventLogCategory.COMMUNICATION_SESSION_LOG, new ObisCode("0.0.99.98.4.255"));
        EVENT_LOG_CATEGORY_OBISCODE_MAP.put(EventLogCategory.M_BUS_EVENT_LOG,           new ObisCode("0.0.99.98.3.255"));
    }
    // @formatter:on

    @Override
    public List<Event> execute(final ClientConnection conn, final FindEventsQuery findEventsQuery) throws IOException,
            ProtocolAdapterException {

        List<Event> eventList = new ArrayList<>();

        final RequestParameterFactory factory = new RequestParameterFactory(CLASS_ID,
                EVENT_LOG_CATEGORY_OBISCODE_MAP.get(findEventsQuery.getEventLogCategory()), ATTRIBUTE_ID);

        final GetRequestParameter getRequestParameter = factory.createGetRequestParameter();

        final List<GetResult> getResultList = conn.get(getRequestParameter);

        if (getResultList.isEmpty()) {
            throw new ProtocolAdapterException("No GetResult received while retrieving event register "
                    + findEventsQuery.getEventLogCategory());
        }

        if (getResultList.size() > 1) {
            throw new ProtocolAdapterException("Expected 1 GetResult while retrieving event log for "
                    + findEventsQuery.getEventLogCategory() + ". Got " + getResultList.size());
        }

        final GetResult result = getResultList.get(0); // A
        if (!AccessResultCode.SUCCESS.equals(result.resultCode())) {
            LOGGER.info("Result of getting events for {} is {}", findEventsQuery.getEventLogCategory(),
                    result.resultCode());
            return eventList;
        }

        final DataObject resultData = result.resultData();
        eventList = this.dataObjectToEventListConverter.convert(resultData);

        return this.filterEventList(eventList, findEventsQuery);

    }

    private List<Event> filterEventList(final List<Event> eventList, final FindEventsQuery findEventsQuery) {

        final List<Event> filtered = new ArrayList<>();
        for (final Event event : eventList) {
            if (event.getTimestamp().isAfter(findEventsQuery.getFrom())
                    && event.getTimestamp().isBefore(findEventsQuery.getUntil())) {
                filtered.add(event);
            }
        }
        return filtered;
    }

}
