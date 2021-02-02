/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.domain.core.valueobjects.smartmetering;

public enum EventType {
    EVENTLOG_CLEARED(255),
    POWER_FAILURE(1),
    POWER_FAILURE_G(1),
    POWER_FAILURE_W(1),
    POWER_RETURNED(2),
    CLOCK_UPDATE(3),
    CLOCK_ADJUSTED_OLD_TIME(4),
    CLOCK_ADJUSTED_NEW_TIME(5),
    CLOCK_INVALID(6),
    REPLACE_BATTERY(7),
    BATTERY_VOLTAGE_LOW(8),
    TARIFF_ACTIVATED(9),
    ERROR_REGISTER_CLEARED(10),
    ALARM_REGISTER_CLEARED(11),
    HARDWARE_ERROR_PROGRAM_MEMORY(12),
    HARDWARE_ERROR_RAM(13),
    HARDWARE_ERROR_NV_MEMORY(14),
    WATCHDOG_ERROR(15),
    HARDWARE_ERROR_MEASUREMENT_SYSTEM(16),
    FIRMWARE_READY_FOR_ACTIVATION(17),
    FIRMWARE_ACTIVATED(18),
    PASSIVE_TARIFF_UPDATED(19),
    SUCCESSFUL_SELFCHECK_AFTER_FIRMWARE_UPDATE(20),
    COMMUNICATION_MODULE_REMOVED(21),
    COMMUNICATION_MODULE_INSERTED(22),
    TERMINAL_COVER_REMOVED(40),
    TERMINAL_COVER_CLOSED(41),
    STRONG_DC_FIELD_DETECTED(42),
    NO_STRONG_DC_FIELD_ANYMORE(43),
    METER_COVER_REMOVED(44),
    METER_COVER_CLOSED(45),
    FAILED_LOGIN_ATTEMPT(46),
    CONFIGURATION_CHANGE(47),
    MODULE_COVER_OPENED(48),
    MODULE_COVER_CLOSED(49),
    METROLOGICAL_MAINTENANCE(71),
    TECHNICAL_MAINTENANCE(72),
    RETRIEVE_METER_READINGS_E(73),
    RETRIEVE_METER_READINGS_G(74),
    RETRIEVE_INTERVAL_DATA_E(75),
    RETRIEVE_INTERVAL_DATA_G(76),
    UNDER_VOLTAGE_L1(77),
    UNDER_VOLTAGE_L2(78),
    UNDER_VOLTAGE_L3(79),
    PV_VOLTAGE_SAG_L1(80),
    PV_VOLTAGE_SAG_L2(81),
    PV_VOLTAGE_SAG_L3(82),
    PV_VOLTAGE_SWELL_L1(83),
    PV_VOLTAGE_SWELL_L2(84),
    PV_VOLTAGE_SWELL_L3(85),
    OVER_VOLTAGE_L1(80),
    OVER_VOLTAGE_L2(81),
    OVER_VOLTAGE_L3(82),
    VOLTAGE_L1_NORMAL(83),
    VOLTAGE_L2_NORMAL(84),
    VOLTAGE_L3_NORMAL(85),
    PHASE_OUTAGE_L1(86),
    PHASE_OUTAGE_L2(87),
    PHASE_OUTAGE_L3(88),
    PHASE_OUTAGE_TEST(89),
    PHASE_RETURNED_L1(90),
    PHASE_RETURNED_L2(91),
    PHASE_RETURNED_L3(92),
    COMMUNICATION_ERROR_M_BUS_CHANNEL_1(100),
    COMMUNICATION_OK_M_BUS_CHANNEL_1(101),
    REPLACE_BATTERY_M_BUS_CHANNEL_1(102),
    FRAUD_ATTEMPT_M_BUS_CHANNEL_1(103),
    CLOCK_ADJUSTED_M_BUS_CHANNEL_1(104),
    NEW_M_BUS_DEVICE_DISCOVERED_CHANNEL_1(105),
    PERMANENT_ERROR_FROM_M_BUS_DEVICE_CHANNEL_1(106),
    COMMUNICATION_ERROR_M_BUS_CHANNEL_2(110),
    COMMUNICATION_OK_M_BUS_CHANNEL_2(111),
    REPLACE_BATTERY_M_BUS_CHANNEL_2(112),
    FRAUD_ATTEMPT_M_BUS_CHANNEL_2(113),
    CLOCK_ADJUSTED_M_BUS_CHANNEL_2(114),
    NEW_M_BUS_DEVICE_DISCOVERED_CHANNEL_2(115),
    PERMANENT_ERROR_FROM_M_BUS_DEVICE_CHANNEL_2(116),
    COMMUNICATION_ERROR_M_BUS_CHANNEL_3(120),
    COMMUNICATION_OK_M_BUS_CHANNEL_3(121),
    REPLACE_BATTERY_M_BUS_CHANNEL_3(122),
    FRAUD_ATTEMPT_M_BUS_CHANNEL_3(123),
    CLOCK_ADJUSTED_M_BUS_CHANNEL_3(124),
    NEW_M_BUS_DEVICE_DISCOVERED_CHANNEL_3(125),
    PERMANENT_ERROR_FROM_M_BUS_DEVICE_CHANNEL_3(126),
    COMMUNICATION_ERROR_M_BUS_CHANNEL_4(130),
    COMMUNICATION_OK_M_BUS_CHANNEL_4(131),
    REPLACE_BATTERY_M_BUS_CHANNEL_4(132),
    FRAUD_ATTEMPT_M_BUS_CHANNEL_4(133),
    CLOCK_ADJUSTED_M_BUS_CHANNEL_4(134),
    NEW_M_BUS_DEVICE_DISCOVERED_CHANNEL_4(135),
    PERMANENT_ERROR_FROM_M_BUS_DEVICE_CHANNEL_4(136),
    MANUFACTURER_SPECIFIC_231(231),
    MANUFACTURER_SPECIFIC_232(232),
    MANUFACTURER_SPECIFIC_233(233),
    MANUFACTURER_SPECIFIC_234(234),
    MANUFACTURER_SPECIFIC_235(235),
    MANUFACTURER_SPECIFIC_236(236),
    MANUFACTURER_SPECIFIC_237(237),
    MANUFACTURER_SPECIFIC_238(238),
    MANUFACTURER_SPECIFIC_239(239),
    MANUFACTURER_SPECIFIC_240(240),
    MANUFACTURER_SPECIFIC_241(241),
    MANUFACTURER_SPECIFIC_242(242),
    MANUFACTURER_SPECIFIC_243(243),
    MANUFACTURER_SPECIFIC_244(244),
    MANUFACTURER_SPECIFIC_245(245),
    MANUFACTURER_SPECIFIC_246(246),
    MANUFACTURER_SPECIFIC_247(247),
    MANUFACTURER_SPECIFIC_248(248),
    MANUFACTURER_SPECIFIC_249(249),
    FATAL_ERROR_ISKR(230),
    BILLING_RESET_ISKR(231),
    POWER_DOWN_PHASE_L1_ISKR(232),
    POWER_DOWN_PHASE_L2_ISKR(233),
    POWER_DOWN_PHASE_L3_ISKR(234),
    POWER_RESTORED_PHASE_L1_ISKR(235),
    POWER_RESTORED_PHASE_L2_ISKR(236),
    POWER_RESTORED_PHASE_L3_ISKR(237),
    MODULE_COVER_OPENED_ISKR(244),
    MODULE_COVER_CLOSED_ISKR(245);

    final int eventCode;

    EventType(final int eventCode) {
        this.eventCode = eventCode;
    }

    public static EventType fromValue(final String v) {
        return valueOf(v);
    }

    public int getEventCode() {
        return eventCode;
    }
}
