package org.opensmartgridplatform.webdeviceeditor.application.services;

import org.opensmartgridplatform.domain.core.entities.Device;
import org.opensmartgridplatform.domain.core.entities.Organisation;
import org.opensmartgridplatform.domain.core.entities.ProtocolInfo;
import org.opensmartgridplatform.domain.core.repositories.DeviceRepository;
import org.opensmartgridplatform.domain.core.repositories.ProtocolInfoRepository;
import org.opensmartgridplatform.webdeviceeditor.domain.entities.DeviceDTO;
import org.opensmartgridplatform.webdeviceeditor.domain.mapper.DeviceMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
public class DeviceService {
    private final DeviceRepository deviceRepository;
    private final ProtocolInfoRepository protocolInfoRepository;
    private final Organisation organisation;

    @Autowired
    public DeviceService(final DeviceRepository deviceRepository, final ProtocolInfoRepository protocolInfoRepository,
            final Organisation organisation) {
        this.deviceRepository = deviceRepository;
        this.protocolInfoRepository = protocolInfoRepository;
        this.organisation = organisation;
    }

    public Page<DeviceDTO> getAll(final int page) {
        final Page<Device> entities = this.deviceRepository.findAllAuthorized(this.organisation,
                PageRequest.of(page, 20));
        return entities.map(DeviceMapper.INSTANCE::deviceToDto);
    }

    public DeviceDTO get(final String identification) {
        final Device device = this.deviceRepository.findByDeviceIdentification(identification);
        return DeviceMapper.INSTANCE.deviceToDto(device);
    }

    public void updateDevice(final DeviceDTO partialDevice, final String identification) {
        final Device device = this.deviceRepository.findByDeviceIdentification(identification);
        if (partialDevice.getProtocolInfo() != null) {
            final ProtocolInfo protocolInfo = this.protocolInfoRepository.findById(partialDevice.getProtocolInfo()).get();
            device.updateProtocol(protocolInfo);
        }
        if (partialDevice.getDeviceType() != null) {
            device.updateRegistrationData(device.getNetworkAddress(), partialDevice.getDeviceType());
        }
        if (partialDevice.getNetworkAddress() != null) {
            device.updateRegistrationData(partialDevice.getNetworkAddress(), device.getDeviceType());
        }
        DeviceMapper.INSTANCE.merge(partialDevice, device);
        DeviceMapper.INSTANCE.deviceToDto(this.deviceRepository.save(device));
    }
}
