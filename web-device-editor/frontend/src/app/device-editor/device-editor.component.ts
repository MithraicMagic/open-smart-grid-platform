import {Component, OnInit} from '@angular/core';
import {DeviceService} from "../device.service";
import {IntegrationTypeService} from "../integration-type.service";
import {DeviceLifecycleStatusService} from "../device-lifecycle-status.service";
import {ActivatedRoute} from "@angular/router";
import {FormControl, FormGroup, Validators} from "@angular/forms";

@Component({
  selector: 'app-device-editor',
  templateUrl: './device-editor.component.html',
  styleUrls: ['./device-editor.component.scss']
})
export class DeviceEditorComponent implements OnInit {

  deviceForm = new FormGroup({
    deviceIdentification: new FormControl({value: '', disabled: true}, Validators.required),
    alias: new FormControl(''),
    containerAddress: new FormGroup({
      city: new FormControl(''),
      postalCode: new FormControl(''),
      street: new FormControl(''),
      number: new FormControl(''),
      numberAddition: new FormControl(''),
      municipality: new FormControl('')
    }),
    gpsCoordinates: new FormGroup({
      longitude: new FormControl(''),
      latitude: new FormControl('')
    }),
    cdmaSettings: new FormGroup({
      mastSegment: new FormControl(''),
      batchNumber: new FormControl('')
    }),
    deviceType: new FormControl(''),
    networkAddress: new FormControl(''),
    isActivated: new FormControl(''),
    authorizations: new FormControl(''), // select from organisations
    protocolInfo: new FormControl(''), // select from protocolinfos
    inMaintenance: new FormControl(''),
    gatewayDevice: new FormControl(''), // select
    organisations: new FormControl(''), // select
    deviceModel: new FormControl(''), // select from models
    deviceLifecycleStatus: new FormControl(''), // select
    lastSuccessfulConnectionTimestamp: new FormControl({value: '', disabled: true}),// date
    lastFailedConnectionTimestamp: new FormControl({value: '', disabled: true}),//date
    failedConnectionCount: new FormControl({value: '', disabled: true}),
    integrationType: new FormControl('') // select
  })

  integrationTypes: string[];
  deviceLifecycleStatuses: string[];

  constructor(
    private deviceService: DeviceService,
    private integrationTypeService: IntegrationTypeService,
    private deviceLifecycleStatusService: DeviceLifecycleStatusService,
    private route: ActivatedRoute
  ) {
  }

  ngOnInit(): void {
    this.getDevice();
    this.getIntegrationTypes();
    this.getDeviceLifecycleStatuses();
  }

  getDevice(): void {
    const id = this.route.snapshot.paramMap.get("id");
    this.deviceService.getDevice(id).subscribe(device => this.deviceForm.patchValue(device));
  }

  getIntegrationTypes(): void {
    this.integrationTypeService.getIntegrationTypes().subscribe(integrationTypes => this.integrationTypes = integrationTypes);
  }

  getDeviceLifecycleStatuses(): void {
    this.deviceLifecycleStatusService.getDeviceLifecycleStatuses().subscribe(dls => this.deviceLifecycleStatuses = dls);
  }

  onSubmit() {
    console.info(this.deviceForm.value);
  }
}
