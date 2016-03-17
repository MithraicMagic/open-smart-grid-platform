/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.ws.smartmetering.application.mapping;

import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.metadata.Type;

import com.alliander.osgp.adapter.ws.schema.smartmetering.common.OsgpUnitType;
import com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.MeterValue;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.OsgpMeterValue;

public class MeterValueConverter extends CustomConverter<OsgpMeterValue, MeterValue> {

    @Override
    public MeterValue convert(final OsgpMeterValue source, final Type<? extends MeterValue> destinationType) {
        if (source == null) {
            return null;
        }
        final MeterValue m = new MeterValue();
        m.setValue(source.getValue());
        m.setUnit(OsgpUnitType.valueOf(source.getOsgpUnit().name()));
        return m;
    }

}
