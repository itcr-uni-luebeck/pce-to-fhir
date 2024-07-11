import {Component} from '@angular/core';
import {HttpClient} from "@angular/common/http";

import {HighlightingPceService} from "./highlightingPce.service";
import {AppService} from "../app.service";
import {SettingsService} from "../settings/settings.service";
import {SuperConceptDeltaService} from "../superConceptDelta/superConceptDelta.service";

@Component({
  selector: 'app-highlightingPce',
  templateUrl: './highlightingPce.component.html',
  styleUrls: ['./highlightingPce.component.css']
})

export class HighlightingPceComponent {

  constructor(public highlightingPceService: HighlightingPceService, private http: HttpClient, public appService: AppService, public settingsService: SettingsService, public superConceptDeltaService : SuperConceptDeltaService) {
  }


  // ngOnInit() {
  //   console.log(this.superConceptDeltaService.pceJson)
  //
  //   let json = JSON.parse(this.superConceptDeltaService.pceJson.toString());
  //
  //   let focusConceptJson = json.focusconcept;
  //   console.log(focusConceptJson)
  // }



}

