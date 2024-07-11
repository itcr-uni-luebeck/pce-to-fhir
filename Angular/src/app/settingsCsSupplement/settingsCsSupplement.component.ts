import {Component} from '@angular/core';
import {HttpClient} from "@angular/common/http";


import {AppService} from "../app.service";
import {SettingsCsSupplementService} from "./settingsCsSupplement.service";
import {SettingsService} from "../settings/settings.service";
import {SettingsCsSupplementComponentSpringBootService} from "./settingsCsSupplementSpringBoot.service";
import {PceStartService} from "../pceStart/pceStart.service";

@Component({
  selector: 'app-settingscs',
  templateUrl: './settingsCsSupplement.component.html',
  styleUrls: ['./settingsCsSupplement.component.css']
})

export class SettingsCsSupplementComponent {

  constructor(private http: HttpClient, public appService: AppService, public settingsCsService: SettingsCsSupplementService, public settingsService: SettingsService, public springBootService: SettingsCsSupplementComponentSpringBootService, public pceStartService: PceStartService) {
  }

  public resultPCE = '';
  public oldUrlCsSupplement = '';
  public idRowStoredPCE = '';
  public validatePceMessage = '';
  public listStoredPCEs = new Array();
  public listStoredPCEsName = new Array();


  loadPCE() {
    console.log("HIER LOAD")
    this.resultPCE = '';
    let idx = Number.parseInt(this.idRowStoredPCE);

    let pce = this.listStoredPCEs[idx].split("@@")[1];

    this.springBootService.validatePceC(pce).then((response: any) => {
      let value = response.result;
      console.log(value)
      if(value === 'True') {
        setTimeout(() => {
          this.appService.item = 'superconcept-delta';
          this.pceStartService.pce = pce;
        }, 1000);

      } else {
        if (!value.includes("@")) {
          this.validatePceMessage = value.split("@");
        } else {
          this.validatePceMessage = value;
        }

      }
    })
  }


  getStoredPCEs(url: string) {
    this.listStoredPCEs = new Array();
    this.listStoredPCEsName = new Array();

    if(this.settingsCsService.newUrl.length > 0) {
      this.springBootService.getCodeSystem(url).then((response: any) => {
        let result = response.result;
        let concept = result[0].concept;
        for (let i = 0; i < concept.length; i++) {
          let id = i;
          let code = concept[i].code;
          let display = concept[i].display;
          let definition = concept[i].definition;

          if (!this.listStoredPCEs.includes(id + "@@" + code + "@@" + display + "@@" + definition)) {
            this.listStoredPCEs.push(id + "@@" + code + "@@" + display + "@@" + definition);
            if(typeof display !== 'undefined') {
              this.listStoredPCEsName.push(display.split(":").join("  :  ").split(",").join("  ,  "));
            } else {
              this.listStoredPCEsName.push("");
            }
          }
        }
        this.oldUrlCsSupplement = url;
      })
    }
  }

  clickRowStore(event: any, row: number) {
    let tables = document.querySelectorAll("table");
    for (let i = 0; i < tables.length; i++) {
      this.idRowStoredPCE = row.toString();
      let trs = document.querySelectorAll("tr");
      for (let j = 0; j < trs.length; j++) {
        let element = trs[j];
        if (element !== null) {
          setTimeout(() => {
            element.style.background = 'transparent';
            if (element.id === this.idRowStoredPCE) {
              element.style.background = 'lightblue';
            }
          }, 10);
        }
      }
    }
  }




}

