package org.opensmartgridplatform.webdeviceeditor.application.controller;

import org.opensmartgridplatform.domain.core.valueobjects.IntegrationType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("integrationTypes")
public class IntegrationTypeController {
    @GetMapping
    public IntegrationType[] getAll() {
        return IntegrationType.values();
    }
}
