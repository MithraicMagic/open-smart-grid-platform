import {Address} from "./address";
import {GpsCoordinates} from "./gps-coordinates";
import {CdmaSettings} from "./cdma-settings";
import {DeviceAuthorization} from "./device-authorization";
import {ProtocolInfo} from "./protocol-info";
import {DeviceModel} from "./device-model";

export class Device {

  constructor(
    public deviceIdentification: string,
    public alias: string,
    public containerAddress: Address,
    public gpsCoordinates: GpsCoordinates,
    public cdmaSettings: CdmaSettings,
    public deviceType: string,
    public networkAddress: string,
    public isActivated: boolean,
    public authorizations: DeviceAuthorization[],
    public protocolInfo: ProtocolInfo,
    public inMaintenance: boolean,
    public gatewayDevice: Device,
    public organisations: string[],
    public deviceModel: DeviceModel,
    public deviceLifecycleStatus: string,
    public lastSuccessfulConnectionTimestamp: Date,
    public lastFailedConnectionTimestamp: Date,
    public failedConnectionCount: number,
    public integrationType: string
  ) {
  }


}
