package org.opensmartgridplatform.webdeviceeditor.application.services;

import ma.glasnost.orika.MapperFacade;
import org.opensmartgridplatform.domain.core.entities.Device;
import org.opensmartgridplatform.domain.core.entities.Organisation;
import org.opensmartgridplatform.domain.core.repositories.DeviceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
public class DeviceService {
    private final MapperFacade deviceEditorMapperFacade;
    private final DeviceRepository deviceRepository;
    private final Organisation organisation;

    @Autowired
    public DeviceService(final MapperFacade deviceEditorMapperFacade, final DeviceRepository deviceRepository,
            final Organisation organisation) {
        this.deviceEditorMapperFacade = deviceEditorMapperFacade;
        this.deviceRepository = deviceRepository;
        this.organisation = organisation;
    }

    public Page<org.opensmartgridplatform.webdeviceeditor.domain.entities.Device> getAll() {
        final Page<Device> entities = this.deviceRepository.findAllAuthorized(this.organisation, PageRequest.of(0, 20));
        return entities.map(entity -> this.deviceEditorMapperFacade.map(entity,
                org.opensmartgridplatform.webdeviceeditor.domain.entities.Device.class));
    }
}
