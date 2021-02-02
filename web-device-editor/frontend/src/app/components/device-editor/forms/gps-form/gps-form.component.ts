import {Component} from '@angular/core';
import {BaseFormComponent} from "../shared/components/base-form-component";
import {FormControl, FormGroup} from "@angular/forms";
import {Device} from "../../../../entities/device";

@Component({
  selector: 'app-gps-form',
  templateUrl: './gps-form.component.html',
  styleUrls: ['../shared/styles/form.scss']
})
export class GpsFormComponent extends BaseFormComponent {
  form = new FormGroup({
    latitude: new FormControl(),
    longitude: new FormControl()
  });

  ngOnInit(): void {
  }

  patchForm(device: Device) {
    this.form.patchValue(device.gpsCoordinates);
  }

  updateProperty(event: Event) {
    //@ts-ignore All inputs emitting this event have ids and values
    this.device.gpsCoordinates[event.target.id] = event.target.value;
  }

  onSave(): void {
    this.deviceService.updateDevice({gpsCoordinates: this.getDirtyValues(this.form)}, this.deviceIdentification).subscribe(this.handleResponse);
  }
}
