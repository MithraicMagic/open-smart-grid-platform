import {Component, Input, OnInit} from "@angular/core";
import {DeviceService} from "../../../../../services/device.service";
import {Device} from "../../../../../entities/device";
import {HttpResponse} from "@angular/common/http";
import {FormGroup} from "@angular/forms";

@Component({
  template: ''
})
export abstract class BaseFormComponent implements OnInit {

  private _device: Device;

  @Input()
  set device(val: Device) {
    this._device = val;
    this.deviceIdentification = this._device?.deviceIdentification;
    if (val) this.patchForm(this._device);
  }

  get device(): Device {
    return this._device;
  }

  deviceIdentification: string;
  complexTypeChanges: object = {};

  abstract form: FormGroup;
  message: string;
  error: boolean;

  public constructor(protected deviceService: DeviceService) {
  }

  abstract ngOnInit(): void;

  patchForm(device: Device): void {
    this.form.patchValue(device);
  }

  selectComplexType(selectedId: any, options: any[], idName: string, propertyName: string) {
    const newValue = options.find(o => o[idName] === selectedId);
    const newComplexType = {};
    newComplexType[idName] = newValue;
    this.complexTypeChanges[propertyName] = newComplexType;
  }

  handleResponse(response: HttpResponse<string>) {
    this.error = !response.ok;
    if (this.error) {
      this.message = 'Something went wrong when trying to save the device';
    } else {
      this.message = response.body;
    }
    const responseBox = document.querySelector('#response');
    //@ts-ignore div has innerText
    responseBox.innerText = this.message;
    responseBox.className = this.error ? 'error' : 'success';
  }

  getDirtyValues(formGroup: FormGroup): object {
    const dirtyValues = {};
    Object.keys(formGroup.controls).forEach((c) => {
      const currentControl = formGroup.get(c);
      if (currentControl.dirty) {
        dirtyValues[c] = currentControl.value;
      }
    });
    return dirtyValues
  }

  onSave(): void {
    if (!this.form.dirty) return;
    this.deviceService.updateDevice(this.getDirtyValues(this.form), this.deviceIdentification).subscribe(this.handleResponse);
  }
}
