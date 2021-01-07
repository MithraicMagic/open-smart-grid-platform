import {Component, OnInit} from '@angular/core';
import {DeviceService} from "../device.service";
import {Device} from "../device";

@Component({
  selector: 'app-device-list',
  templateUrl: './device-list.component.html',
  styleUrls: ['./device-list.component.scss']
})
export class DeviceListComponent implements OnInit {

  devices: Device[];
  totalPages: number;
  pageNumber: number;

  constructor(private deviceService: DeviceService) {
  }

  ngOnInit(): void {
    this.getDevices(0);
  }

  getDevices(pageNb: number) {
    this.deviceService.getDevices(pageNb).subscribe(devicesRes => {
      this.devices = devicesRes.content.devices;
      this.pageNumber = devicesRes.number;
      this.totalPages = devicesRes.totalPages;
    });
  }

  hasNextPage() {
    return this.pageNumber + 1 < this.totalPages;
  }

  hasPrevPage() {
    return this.pageNumber > 0;
  }

  nextPage() {
    if (this.hasNextPage()) {
      this.getDevices(this.pageNumber + 1);
    }
  }

  prevPage() {
    if (this.hasPrevPage()) {
      this.getDevices(this.pageNumber - 1);
    }
  }
}
