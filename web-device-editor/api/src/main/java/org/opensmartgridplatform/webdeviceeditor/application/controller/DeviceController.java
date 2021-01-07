package org.opensmartgridplatform.webdeviceeditor.application.controller;

import org.opensmartgridplatform.webdeviceeditor.application.services.DeviceService;
import org.opensmartgridplatform.webdeviceeditor.domain.entities.Device;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
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
    public Page<Device> index() {
        return this.deviceService.getAll();
    }
}
