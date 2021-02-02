import {Address} from "./address";
import {GpsCoordinates} from "./gps-coordinates";
import {CdmaSettings} from "./cdma-settings";
import {DeviceAuthorization} from "./device-authorization";
import {DeviceModel} from "./device-model";

export class Device {
  public deviceIdentification: string;
  public alias: string;
  public containerAddress: Address;
  public gpsCoordinates: GpsCoordinates;
  public cdmaSettings: CdmaSettings;
  public deviceType: string;
  public networkAddress: string;
  public activated: boolean;
  public authorizations: DeviceAuthorization[];
  public protocolInfo: number;
  public inMaintenance: boolean;
  public gatewayDevice: Device;
  public deviceModel: DeviceModel;
  public deviceLifecycleStatus: string;
  public lastSuccessfulConnectionTimestamp: number;
  public lastFailedConnectionTimestamp: number;
  public failedConnectionCount: number;
  public integrationType: string;
}
