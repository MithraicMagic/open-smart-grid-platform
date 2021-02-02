import {Component} from '@angular/core';
import {IntegrationTypeService} from "../../../../services/integration-type.service";
import {DeviceService} from "../../../../services/device.service";
import {BaseFormComponent} from "../shared/components/base-form-component";
import {ProtocolInfoService} from "../../../../services/protocol-info.service";
import {ProtocolInfo} from "../../../../entities/protocol-info";
import {FormControl, FormGroup} from "@angular/forms";

@Component({
  selector: 'app-generic-form',
  templateUrl: './generic-form.component.html',
  styleUrls: ['../shared/styles/form.scss']
})
export class GenericFormComponent extends BaseFormComponent {

  form = new FormGroup({
    deviceIdentification: new FormControl({value: null, disabled: true}),
    alias: new FormControl(),
    deviceType: new FormControl(),
    networkAddress: new FormControl(/*{value: null, disabled: true}*/),
    protocolInfo: new FormControl(),
    gatewayDevice: new FormControl(),
    deviceModel: new FormControl(),
    integrationType: new FormControl()
  });

  integrationTypes: string[];
  protocolInfos: ProtocolInfo[];

  constructor(
    private integrationTypeService: IntegrationTypeService,
    private protocolInfoService: ProtocolInfoService,
    deviceService: DeviceService
  ) {
    super(deviceService);
  }

  ngOnInit(): void {
    this.getIntegrationTypes();
    this.getProtocolInfos();
  }

  getIntegrationTypes(): void {
    this.integrationTypeService.getIntegrationTypes().subscribe(type => this.integrationTypes = type);
  }

  getProtocolInfos(): void {
    this.protocolInfoService.getProtocolInfos().subscribe(infos => this.protocolInfos = infos);
  }
}
