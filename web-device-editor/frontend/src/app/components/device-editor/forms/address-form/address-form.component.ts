import {Component} from '@angular/core';
import {BaseFormComponent} from "../shared/components/base-form-component";
import {FormControl, FormGroup} from "@angular/forms";
import {Device} from "../../../../entities/device";

@Component({
  selector: 'app-address-form',
  templateUrl: './address-form.component.html',
  styleUrls: ['../shared/styles/form.scss']
})
export class AddressFormComponent extends BaseFormComponent {

  form = new FormGroup({
    municipality: new FormControl(),
    city: new FormControl(),
    street: new FormControl(),
    postalCode: new FormControl(),
    number: new FormControl(),
    numberAddition: new FormControl()
  });

  ngOnInit(): void {
  }

  patchForm(device: Device) {
    this.form.patchValue(device.containerAddress);
  }

  onSave(): void {
    this.deviceService.updateDevice({containerAddress: this.getDirtyValues(this.form)}, this.deviceIdentification).subscribe(this.handleResponse);
  }
}
