package org.opensmartgridplatform.webdeviceeditor.application.controller;

import java.util.List;

import org.opensmartgridplatform.domain.core.entities.ProtocolInfo;
import org.opensmartgridplatform.domain.core.repositories.ProtocolInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("protocolInfos")
public class ProtocolInfoController {
    private final ProtocolInfoRepository protocolInfoRepository;

    @Autowired
    public ProtocolInfoController(final ProtocolInfoRepository protocolInfoRepository) {
        this.protocolInfoRepository = protocolInfoRepository;
    }

    @GetMapping
    public List<ProtocolInfo> getAll() {
        return this.protocolInfoRepository.findAll();
    }
}
