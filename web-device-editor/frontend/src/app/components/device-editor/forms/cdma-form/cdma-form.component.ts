import {Component} from '@angular/core';
import {BaseFormComponent} from "../shared/components/base-form-component";
import {FormControl, FormGroup} from "@angular/forms";
import {Device} from "../../../../entities/device";

@Component({
  selector: 'app-cdma-form',
  templateUrl: './cdma-form.component.html',
  styleUrls: ['../shared/styles/form.scss']
})
export class CdmaFormComponent extends BaseFormComponent {
  form = new FormGroup({
    mastSegmentInput: new FormControl(),
    batchNumberInput: new FormControl()
  });

  ngOnInit(): void {
  }

  patchForm(device: Device) {
    this.form.patchValue(device.cdmaSettings);
  }

  onSave(): void {
    this.deviceService.updateDevice({cdmaSettings: this.getDirtyValues(this.form)}, this.deviceIdentification).subscribe(this.handleResponse);
  }
}
