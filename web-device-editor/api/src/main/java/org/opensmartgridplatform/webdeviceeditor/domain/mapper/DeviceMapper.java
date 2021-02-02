package org.opensmartgridplatform.webdeviceeditor.domain.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;
import org.opensmartgridplatform.domain.core.entities.Device;
import org.opensmartgridplatform.webdeviceeditor.domain.entities.DeviceDTO;

@Mapper(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface DeviceMapper {
    DeviceMapper INSTANCE = Mappers.getMapper(DeviceMapper.class);

    @Mapping(source = "protocolInfo.id", target = "protocolInfo")
    DeviceDTO deviceToDto(Device device);

    void merge(DeviceDTO deviceDTO, @MappingTarget Device device);
}
