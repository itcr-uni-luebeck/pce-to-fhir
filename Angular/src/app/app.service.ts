import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {lastValueFrom} from 'rxjs';
import {SettingsService} from "./settings/settings.service";
import {SuperConceptDeltaService} from "./superConceptDelta/superConceptDelta.service";

@Injectable({
  providedIn: 'root'
})
export class AppService {
  public url_server = 'http://localhost:8000/';
  public selectedApp = '';
  public selectedItem = '';
  private modalDisplayStyle: Array<any> = [];
  public item = 'start-pce';

  public idLastTab = '';
  public idActualTab = '';

  constructor(public httpClient: HttpClient, public settingsService: SettingsService, public superConceptDeltaService: SuperConceptDeltaService) {
  }



  // ---- Model ----

  modalStyleDisplay(id : string): string {
    let display = 'none';
    let json = this.modalDisplayStyle.filter(x => x.id === id);
    if (json[0] != null) {
      display = json[0].display;
    }
    return display;
  }

  openPopup(id: string): void {
    let json = {id: id, display: 'block'};
    this.modalDisplayStyle = this.modalDisplayStyle.filter(x => x.id !== id);
    this.modalDisplayStyle.push(json);
  }

  closePopup(id : string): void {
    let json = this.modalDisplayStyle.filter(x => x.id === id);
    if (json[0] != null) {
      json[0].display = 'none';
    }
  }



  // ---- Spring Boot ----

  getStoredServer() {
    const urlRequest = this.url_server + 'settingsserver';
    const response = this.httpClient.get(urlRequest);
    return lastValueFrom(response);
  }

}
