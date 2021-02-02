import {Component} from '@angular/core';
import {DeviceLifecycleStatusService} from "../../../../services/device-lifecycle-status.service";
import {DeviceService} from "../../../../services/device.service";
import {BaseFormComponent} from "../shared/components/base-form-component";
import {FormControl, FormGroup} from "@angular/forms";

@Component({
  selector: 'app-status-form',
  templateUrl: './status-form.component.html',
  styleUrls: ['../shared/styles/form.scss']
})
export class StatusFormComponent extends BaseFormComponent {

  deviceLifecycleStatuses: string[];

  form = new FormGroup({
    deviceLifecycleStatus: new FormControl(),
    lastSuccessfulConnectionTimestamp: new FormControl(),
    lastFailedConnectionTimestamp: new FormControl(),
    failedConnectionCount: new FormControl(),
    inMaintenance: new FormControl(),
    activated: new FormControl()
  });

  constructor(
    private deviceLifecycleStatusService: DeviceLifecycleStatusService,
    deviceService: DeviceService) {
    super(deviceService);
  }

  ngOnInit(): void {
    this.getDeviceLifecycleStatuses();
  }

  getDeviceLifecycleStatuses() {
    this.deviceLifecycleStatusService.getDeviceLifecycleStatuses().subscribe(statuses => this.deviceLifecycleStatuses = statuses);
  }
}
