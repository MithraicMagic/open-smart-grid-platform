import {Injectable} from '@angular/core';
import {Constants} from "../global";
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class DeviceLifecycleStatusService {

  apiUrl = Constants.BASE_API_URL + '/deviceLifecycleStatus'

  constructor(private http: HttpClient) { }

  getDeviceLifecycleStatuses(): Observable<string[]> {
    return this.http.get<string[]>(this.apiUrl);
  }
}
