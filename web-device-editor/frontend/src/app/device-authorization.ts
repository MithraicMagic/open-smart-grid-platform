import {Device} from "./device";
import {Organisation} from "./organisation";

export class DeviceAuthorization {
  constructor(
    public device: Device,
    public organisation: Organisation,
    public functionGroup: number
  ) {
  }
}
