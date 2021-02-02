package org.opensmartgridplatform.webdeviceeditor.application.controller;

import org.opensmartgridplatform.webdeviceeditor.application.services.DeviceService;
import org.opensmartgridplatform.webdeviceeditor.domain.entities.DeviceDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("devices")
public class DeviceController {

    private final DeviceService deviceService;

    @Autowired
    public DeviceController(final DeviceService deviceService) {
        this.deviceService = deviceService;
    }

    @GetMapping
    public Page<DeviceDTO> getAll(@RequestParam final int page) {
        return this.deviceService.getAll(page);
    }

    @GetMapping("/{identification}")
    public DeviceDTO get(@PathVariable("identification") final String identification) {
        return this.deviceService.get(identification);
    }

    @PatchMapping("/{identification}")
    public String updateDevice(@PathVariable("identification") final String identification,
            @RequestBody final DeviceDTO partialDevice) {
        this.deviceService.updateDevice(partialDevice, identification);
        return "Successfully Updated Device";
    }
}
