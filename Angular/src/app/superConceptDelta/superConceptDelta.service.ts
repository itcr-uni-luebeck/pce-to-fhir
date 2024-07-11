import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {lastValueFrom} from 'rxjs';
import {SettingsService} from "../settings/settings.service";
import {PceStartService} from "../pceStart/pceStart.service";

@Injectable({
  providedIn: 'root'
})
export class SuperConceptDeltaService {
  public url_server = 'http://localhost:8000/';


  constructor(public httpClient: HttpClient, public settingsService: SettingsService, public pceStartService: PceStartService) {
  }

  public pceJson = "";
  public superConcept = "";
  public fhirPathVersion = "full";
  public finishPreprocessingPce = false;



}
