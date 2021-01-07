package org.opensmartgridplatform.webdeviceeditor.application.controller;

import org.opensmartgridplatform.domain.core.valueobjects.DeviceLifecycleStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("deviceLifecycleStatuses")
public class DeviceLifecycleStatusController {
    @GetMapping
    public DeviceLifecycleStatus[] getAll() {
        return DeviceLifecycleStatus.values();
    }
}
