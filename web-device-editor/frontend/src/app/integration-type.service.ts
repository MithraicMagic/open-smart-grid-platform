import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";
import {Constants} from "../global";

@Injectable({
  providedIn: 'root'
})
export class IntegrationTypeService {

  apiUrl = Constants.BASE_API_URL + '/integrationType'

  constructor(private http: HttpClient) { }

  getIntegrationTypes(): Observable<string[]> {
    return this.http.get<string[]>(this.apiUrl);
  }
}
