import {Manufacturer} from "./manufacturer";

export class DeviceModel {
  constructor(
    public manufacturer: Manufacturer,
    public modelCode: string,
    public description: string,
    public fileStorage: boolean
  ) {
  }
}
