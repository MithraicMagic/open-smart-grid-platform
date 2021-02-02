import {Injectable} from '@angular/core';
import {Observable} from "rxjs";
import {HttpClient, HttpResponse} from "@angular/common/http";
import {Constants} from '../../global';
import {Device} from "../entities/device";

@Injectable({
  providedIn: 'root'
})
export class DeviceService {

  apiUrl = Constants.BASE_API_URL + '/devices'

  constructor(private http: HttpClient) {
  }

  getDevices(pageNb: number): Observable<any> {
    return this.http.get<any>(this.apiUrl + `?page=${pageNb}`);
  }

  getDevice(identification: string): Observable<Device> {
    return this.http.get<Device>(this.apiUrl + '/' + identification);
  }

  updateDevice(partialData: any, identification: string, pathAddition: string = ''): Observable<HttpResponse<string>> {
    return this.http.patch(`${this.apiUrl}/${identification}${pathAddition}`, partialData, { responseType: 'text', observe: 'response' });
  }
}
