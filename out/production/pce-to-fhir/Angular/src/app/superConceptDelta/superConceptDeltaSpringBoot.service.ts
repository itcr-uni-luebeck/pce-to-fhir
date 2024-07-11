import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {lastValueFrom} from 'rxjs';
import {SettingsService} from "../settings/settings.service";
import {PceStartService} from "../pceStart/pceStart.service";
import {SuperConceptDeltaService} from "./superConceptDelta.service";

@Injectable({
  providedIn: 'root'
})
export class SuperConceptDeltaSpringBootService {
  public url_server = 'http://localhost:8000/';


  constructor(public httpClient: HttpClient, public settingsService: SettingsService, public pceStartService: PceStartService, public superConceptDeltaService: SuperConceptDeltaService) {
  }



  preprocessingPce(pce: string)  {
    let p = this.encodeURIComponent(pce);
    const url = this.url_server + 'preprocessingpce/?url=' + this.settingsService.serverInit + '&pce=' + p;
    const response = this.httpClient.get(url);
    return lastValueFrom(response);
  }

  getSuperConceptCandidates(pce: string)  {
    let p = this.encodeURIComponent(pce);
    const url = this.url_server + 'superconceptcandidates/?url=' + this.settingsService.serverInit + '&pce=' + p;
    const response = this.httpClient.get(url);
    return lastValueFrom(response);
  }

  getSuperConcept(pce: string, list1: [], list2: [])  {
    let p = this.encodeURIComponent(pce);
    let l1 = this.encodeURIComponent(list1);
    let l2 = this.encodeURIComponent(list2);
    const url = this.url_server + 'superconcept/?url=' + this.settingsService.serverInit + '&pce=' + p + '&list1=' + l1 + '&list2=' + l2;
    console.log(url)
    const response = this.httpClient.get(url);
    return lastValueFrom(response);
  }

  getDelta(pce: string)  {
    let p = this.encodeURIComponent(pce);
    const url = this.url_server + 'delta/?url=' + this.settingsService.serverInit + '&pce=' + p + '&superconcept=' + this.superConceptDeltaService.superConcept.split("|")[0].split(" ").join("");
    const response = this.httpClient.get(url);
    return lastValueFrom(response);
  }

  getFhirElements(pce: string, delta: any)  {
    let p = this.encodeURIComponent(pce);
    let d = this.encodeURIComponent(delta);
    const url = this.url_server + 'tofhir/?url=' + this.settingsService.serverInit + '&version=' + this.settingsService.sctVersion + '&pce=' + p + '&superconcept=' + this.superConceptDeltaService.superConcept.split("|")[0].split(" ").join("") + '&delta=' + d + '&fhirpathversion=' + this.superConceptDeltaService.fhirPathVersion;
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
