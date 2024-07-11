import {Component} from '@angular/core';
import {HttpClient} from "@angular/common/http";


import {PceStartService} from "./pceStart.service";
import {AppService} from "../app.service";
import {SettingsService} from "../settings/settings.service";
import {SettingsCsSupplementComponent} from "../settingsCsSupplement/settingsCsSupplement.component";
import {SettingsCsSupplementService} from "../settingsCsSupplement/settingsCsSupplement.service";


@Component({
  selector: 'pce-start',
  templateUrl: './pceStart.component.html',
  styleUrls: ['./pceStart.component.css']
})

export class PceStartComponent {

// , public appPreprocessing: AppProcessingService

  constructor(private pceStartService: PceStartService, private http: HttpClient, public appService: AppService, public settingsService: SettingsService, public settingsCsService: SettingsCsSupplementService) {
  }

  public loadingOption = 'enter';
  public pce = '';
  public validatePceMessage = '';

  public checkPceInput() {
    this.validatePceMessage = "Validate PCE"

    this.pceStartService.validatePceC(this.pce.split("\n").join("")).then((response: any) => {
      let value = response.result;
      if(value === 'True') {
        this.validatePceMessage = "Syntax correct";
        setTimeout(() => {
          this.appService.item = 'superconcept-delta';
          this.pceStartService.pce = this.pce;
        }, 1000);

      } else {
        this.validatePceMessage = value.split("@");
      }

      // this.appPreprocessing.start();


    })
  }

}

