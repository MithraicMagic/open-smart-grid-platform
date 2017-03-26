/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.core.application.tasks;

import static org.springframework.data.jpa.domain.Specifications.where;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.stereotype.Component;

import com.alliander.osgp.core.application.config.SchedulingConfig;
import com.alliander.osgp.core.application.services.DeviceRequestMessageService;
import com.alliander.osgp.domain.core.entities.Device;
import com.alliander.osgp.domain.core.entities.DeviceModel;
import com.alliander.osgp.domain.core.entities.Event;
import com.alliander.osgp.domain.core.entities.Manufacturer;
import com.alliander.osgp.domain.core.exceptions.ArgumentNullOrEmptyException;
import com.alliander.osgp.domain.core.repositories.DeviceModelRepository;
import com.alliander.osgp.domain.core.repositories.DeviceRepository;
import com.alliander.osgp.domain.core.repositories.EventRepository;
import com.alliander.osgp.domain.core.repositories.ManufacturerRepository;
import com.alliander.osgp.domain.core.valueobjects.DeviceFunction;
import com.alliander.osgp.shared.exceptionhandling.FunctionalException;
import com.alliander.osgp.shared.infra.jms.DeviceMessageMetadata;
import com.alliander.osgp.shared.infra.jms.ProtocolRequestMessage;
import com.alliander.osgp.shared.infra.jms.RequestMessage;

/**
 * Periodic task to fetch events from devices of a manufacturer in case the
 * devices have events older than X hours. This ensures all devices are
 * contacted, and are allowed to send any new events in their buffers. See
 * {@link SchedulingConfig#eventRetrievalScheduledTaskCronTrigger()} and
 * {@link SchedulingConfig#eventRetrievalScheduler()}.
 */
@Component
public class EventRetrievalScheduledTask implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(EventRetrievalScheduledTask.class);

    /**
     * JPA specifications to fetch a page of events for a device.
     */
    class JpaEventSpecifications {
        public Specification<Event> isFromDevice(final Device device) throws ArgumentNullOrEmptyException {
            if (device == null) {
                throw new ArgumentNullOrEmptyException("Device is null");
            }
            return new Specification<Event>() {
                @Override
                public Predicate toPredicate(final Root<Event> eventRoot, final CriteriaQuery<?> query,
                        final CriteriaBuilder cb) {
                    return cb.equal(eventRoot.<Integer> get("device"), device.getId());
                }
            };
        }
    }

    @Autowired
    private DeviceRequestMessageService deviceRequestMessageService;

    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private ManufacturerRepository manufacturerRepository;

    @Autowired
    private DeviceModelRepository deviceModelRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private String manufacturerName;

    @Autowired
    private int maximumAllowedAge;

    private JpaEventSpecifications eventSpecifications = new JpaEventSpecifications();

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run() {
        final Manufacturer manufacturer = this.findManufacturer(this.manufacturerName);
        if (manufacturer == null) {
            return;
        }

        final List<DeviceModel> deviceModels = this.findDeviceModels(manufacturer);
        if (deviceModels == null || deviceModels.isEmpty()) {
            return;
        }

        final List<Device> devices = this.findDevices(deviceModels);
        if (devices.isEmpty()) {
            return;
        }

        final Map<Device, Event> map = this.findEventsForDevices(devices);
        if (map.isEmpty()) {
            return;
        }

        // Determine which devices to contact by evaluating the age of the
        // latest event for a device.
        final List<Device> devicesToContact = new ArrayList<>();
        final DateTime maxAge = DateTime.now(DateTimeZone.UTC).minusHours(this.maximumAllowedAge);
        for (final Entry<Device, Event> entry : map.entrySet()) {
            if (this.isEventOlderThanMaxInterval(maxAge, entry.getValue())) {
                LOGGER.info("Found device which has events older than: {}", maxAge);
                devicesToContact.add(entry.getKey());
            }
        }

        // Send a request message to protocol adapter component for each device
        // to contact.
        for (final Device device : devicesToContact) {
            final ProtocolRequestMessage protocolRequestMessage = this.createProtocolRequestMessage(device);
            if (protocolRequestMessage != null) {
                try {
                    LOGGER.info("Attempting to contact device: {}", device.getDeviceIdentification());
                    this.deviceRequestMessageService.processMessage(protocolRequestMessage);
                } catch (final FunctionalException e) {
                    LOGGER.error("Exception during sending of protocol request message", e);
                }
            }
        }
    }

    /**
     * Try to find a manufacturer by name (case sensitive).
     */
    private Manufacturer findManufacturer(final String name) {
        LOGGER.info("Trying to find manufacturer for name: {}", name);
        final Manufacturer manufacturer = this.manufacturerRepository.findByName(name);
        if (manufacturer == null) {
            LOGGER.warn("No manufacturer found for name: {}", name);
        } else {
            LOGGER.info("Manufacturer found for name: {}", name);
        }
        return manufacturer;
    }

    /**
     * Try to find all device models for a manufacturer.
     */
    private List<DeviceModel> findDeviceModels(final Manufacturer manufacturer) {
        LOGGER.info("Trying to find device models for manufacturer...");
        final List<DeviceModel> deviceModels = this.deviceModelRepository.findByManufacturerId(manufacturer);
        if (deviceModels == null) {
            LOGGER.warn("No device models found for manufacturer with name: {}, deviceModels == null",
                    manufacturer.getName());
        } else if (deviceModels.isEmpty()) {
            LOGGER.warn("No device models found for manufacturer with name: {}, deviceModels.isEmpty()",
                    manufacturer.getName());
        } else {
            LOGGER.info("{} device models found for manufacturer with name: {}", deviceModels.size(),
                    manufacturer.getName());
        }
        return deviceModels;
    }

    /**
     * Try to find all devices which are not 'in maintenance' for a list of
     * device models.
     */
    private List<Device> findDevices(final List<DeviceModel> deviceModels) {
        LOGGER.info("Trying to find devices for device models for manufacturer...");
        final List<Device> devices = new ArrayList<>();
        for (final DeviceModel deviceModel : deviceModels) {
            final List<Device> devs = this.deviceRepository.findByDeviceModelAndInMaintenanceFalse(deviceModel);
            devices.addAll(devs);
        }
        if (devices.isEmpty()) {
            LOGGER.warn("No devices found for device models for manufacturer");
        } else {
            LOGGER.info("{} devices found for device models for manufacturer", devices.size());
        }
        return devices;
    }

    /**
     * Find the latest event for each device in the list of devices.
     */
    private Map<Device, Event> findEventsForDevices(final List<Device> devices) {
        final Map<Device, Event> map = new HashMap<>();
        for (final Device device : devices) {
            try {
                final List<Event> events = this.findEventsForDevice(device);
                final Event event = this.findLatestEventForDevice(events);
                map.put(device, event);
            } catch (final ArgumentNullOrEmptyException e) {
                LOGGER.error("Exception during findEventsForDevices", e);
            }
        }
        return map;
    }

    /**
     * Try to find events for a device.
     */
    private List<Event> findEventsForDevice(final Device device) throws ArgumentNullOrEmptyException {
        LOGGER.info("Trying to find events for device: {}", device.getDeviceIdentification());
        final PageRequest request = new PageRequest(0, 1, Sort.Direction.DESC, "dateTime");
        final Specifications<Event> specifications = where(this.eventSpecifications.isFromDevice(device));
        final Page<Event> page = this.eventRepository.findAll(specifications, request);
        if (page == null) {
            LOGGER.error("No page of event found for device: {}", device.getDeviceIdentification());
            return null;
        }
        final List<Event> events = page.getContent();
        if (events == null) {
            LOGGER.warn("No events found for device: {}, events == null", device.getDeviceIdentification());
        } else if (events.isEmpty()) {
            LOGGER.warn("No events found for device: {}, events.isEmpty()", device.getDeviceIdentification());
        } else {
            LOGGER.info("{} events found for device: {}", events.size(), device.getDeviceIdentification());
        }
        return events;
    }

    /**
     * Try to find the latest event in the list of events.
     */
    private Event findLatestEventForDevice(final List<Event> events) {
        if (events == null || events.isEmpty()) {
            return null;
        }
        // Expected is a list containing 1 event.
        if (events.size() == 1) {
            return events.get(0);
        }
        // This programmatic search should not be necessary, as the PageRequest
        // is used with descending sort direction.
        Event latestEvent = events.get(0);
        for (final Event event : events) {
            if (latestEvent.getDateTime().before(event.getDateTime())) {
                LOGGER.info("Latest event: {} is before current event: {}", latestEvent.getDateTime(),
                        event.getDateTime());
                latestEvent = event;
            }
        }
        LOGGER.info("Returning latestEvent: {}", latestEvent.getDateTime());
        return latestEvent;
    }

    /**
     * Determine if an event is older than X hours as indicated by maxAge.
     */
    private boolean isEventOlderThanMaxInterval(final DateTime maxAge, final Event event) {
        if (event == null) {
            // In case the event instance is null, try to contact the device.
            LOGGER.info("Event instance is null");
            return true;
        }

        final Date date = maxAge.toDate();
        final boolean result = event.getDateTime().before(date);
        LOGGER.info("event date time: {}, current date time minus {} hours: {}, is event before? : {}",
                event.getDateTime(), this.maximumAllowedAge, maxAge, result);
        return result;
    }

    /**
     * Create a message to send to the protocol adapter component.
     */
    private ProtocolRequestMessage createProtocolRequestMessage(final Device device) {
        final String deviceIdentification = device.getDeviceIdentification();
        final String organisation = "LianderNetManagement";
        // Creating message with empty CorrelationUID, in order to prevent a
        // response from protocol adapter component.
        final String correlationUid = "";
        final DeviceFunction deviceFunction = DeviceFunction.GET_STATUS;
        final String domain = "CORE";
        final String domainVersion = "1.0";

        final DeviceMessageMetadata deviceMessageMetadata = new DeviceMessageMetadata(deviceIdentification,
                organisation, correlationUid, deviceFunction.toString(), 0);

        String ipAddress = null;
        if (device.getNetworkAddress() == null) {
            // In case the device does not have an known IP address, don't sent
            // a request message.
            LOGGER.warn("Unable to create protocol request message because the IP address is empty for device: {}",
                    deviceIdentification);
            return null;
        } else {
            ipAddress = device.getNetworkAddress().getHostAddress();
        }

        final RequestMessage requestMessage = new RequestMessage(correlationUid, organisation, deviceIdentification,
                null);

        return new ProtocolRequestMessage.Builder().deviceMessageMetadata(deviceMessageMetadata).domain(domain)
                .domainVersion(domainVersion).ipAddress(ipAddress).request(requestMessage).build();
    }
}
