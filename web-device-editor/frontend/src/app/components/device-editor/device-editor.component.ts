import {Component, OnInit} from '@angular/core';
import {DeviceService} from "../../services/device.service";
import {ActivatedRoute} from "@angular/router";
import {Device} from "../../entities/device";
import {Address} from "../../entities/address";
import {GpsCoordinates} from "../../entities/gps-coordinates";
import {CdmaSettings} from "../../entities/cdma-settings";

@Component({
  selector: 'app-device-editor',
  templateUrl: './device-editor.component.html',
  styleUrls: ['./device-editor.component.scss']
})
export class DeviceEditorComponent implements OnInit {

  selectedTabIndex = 0;

  deviceIdentification: string;
  device: Device;

  constructor(
    private deviceService: DeviceService,
    private route: ActivatedRoute
  ) {
  }

  ngOnInit(): void {
    this.getDevice();
  }

  getDevice(): void {
    const id = this.route.snapshot.paramMap.get("id");
    this.deviceService.getDevice(id).subscribe(device => {
      // Create device with default values
      this.device = Object.assign(new Device(), device, {
        containerAddress: device.containerAddress || new Address(),
        gpsCoordinates: device.gpsCoordinates || new GpsCoordinates(),
        cdmaSettings: device.cdmaSettings || new CdmaSettings()
      });
      this.deviceIdentification = this.device.deviceIdentification;
    });
  }

  selectTab(index: number): void {
    this.selectedTabIndex = index;
  }

  // onSave() {
  //   this.deviceService.updateDevice({...this.device, protocolInfo: undefined}, this.deviceIdentification).subscribe((response) => {
  //     this.message = response.body;
  //     this.error = response.ok;
  //   });
  //   this.deviceService.updateDevice(this.device.protocolInfo, this.deviceIdentification, '/protocolInfo').subscribe((response) => {
  //     this.message = response.body;
  //     this.error = response.ok;
  //   });
  // }
}
