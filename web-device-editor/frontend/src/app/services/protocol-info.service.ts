import {Injectable} from '@angular/core';
import {Constants} from "../../global";
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";
import {ProtocolInfo} from "../entities/protocol-info";

@Injectable({
  providedIn: 'root'
})
export class ProtocolInfoService {
  apiUrl = Constants.BASE_API_URL + '/protocolInfos'

  constructor(private http: HttpClient) {
  }

  getProtocolInfos(): Observable<ProtocolInfo[]> {
    return this.http.get<ProtocolInfo[]>(this.apiUrl);
  }
}
