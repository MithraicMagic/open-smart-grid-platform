package org.opensmartgridplatform.webdeviceeditor;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.opensmartgridplatform.domain.core.entities.Device;
import org.opensmartgridplatform.domain.core.valueobjects.Address;
import org.opensmartgridplatform.webdeviceeditor.domain.entities.DeviceDTO;
import org.opensmartgridplatform.webdeviceeditor.domain.mapper.DeviceMapper;

class DeviceEditorApplicationTests {
    @Test
    public void mapTest() {
        final Device device = new Device();
        final Address address = new Address("Eindhoven", "0000AA", "JFK Laan", 1, "a", "Eindhoven");
        device.setContainerAddress(address);
        final DeviceDTO dto = DeviceMapper.INSTANCE.deviceToDto(device);
        Assertions.assertThat(dto.getContainerAddress().getCity()).isEqualTo(address.getCity());
    }

    @Test
    public void mergeTest() {
        final Device device = new Device();
        device.setContainerAddress(new Address());
        final DeviceDTO dto = new DeviceDTO();
        final Address address = new Address("Eindhoven", "0000AA", "JFK Laan", 1, "a", "Eindhoven");
        dto.setContainerAddress(address);
        DeviceMapper.INSTANCE.merge(dto, device);
        Assertions.assertThat(device.getContainerAddress().getCity()).isEqualTo(address.getCity());
    }
}
