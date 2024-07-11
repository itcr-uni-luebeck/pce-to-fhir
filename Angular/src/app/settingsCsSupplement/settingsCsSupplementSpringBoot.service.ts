import {lastValueFrom} from "rxjs";
import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {SettingsService} from "../settings/settings.service";
import {PceStartService} from "../pceStart/pceStart.service";
import {SettingsCsSupplementComponent} from "./settingsCsSupplement.component";

@Injectable({
  providedIn: 'root'
})
export class SettingsCsSupplementComponentSpringBootService {
  public url_server = 'http://localhost:8000/';


  constructor(public httpClient: HttpClient, public settingsService: SettingsService, public pceStartService: PceStartService) {
  }


  checkSyntaxUrl(url: string) {
    let u = this.encodeURIComponent(url);
    const urlRequest = this.url_server + 'settingsurl/?url=' + u;
    const response = this.httpClient.get(urlRequest);
    return lastValueFrom(response);
  }

  getAllCsSupplements(urlServer: string) {
    let u = this.encodeURIComponent(urlServer);
    const url = this.url_server + 'getallcodesystemsupplement/?url=' + u;
    console.log(url)
    const response = this.httpClient.get(url);

    return lastValueFrom(response);
  }

  getCodeSystem(urlCs: string) {
    let u = this.encodeURIComponent(urlCs);
    const url = this.url_server + 'getcodesystemsupplement/?url=' + u;
    const response = this.httpClient.get(url);

    return lastValueFrom(response);
  }

  validatePceC(pce: string)  {
    let p = this.encodeURIComponent(pce);
    const url = this.url_server + 'validatepce/?url=' + this.settingsService.serverInit + '&pce=' + p;
    const response = this.httpClient.get(url);
    return lastValueFrom(response);
  }

  encodeURIComponent(value: any): string {
    value += '';
    value = value.trim();
    value = encodeURIComponent(value);
    return value;
  }
}
