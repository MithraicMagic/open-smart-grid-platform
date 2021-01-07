import {DeviceAuthorization} from "./device-authorization";

export class Organisation {
  constructor(
    public organisationIdentification: string,
    public name: string,
    public prefix: string,
    public authorizations: DeviceAuthorization[],
    public functionGroup: string,
    public enabled: boolean,
    public domains: string
  ) {
  }
}
