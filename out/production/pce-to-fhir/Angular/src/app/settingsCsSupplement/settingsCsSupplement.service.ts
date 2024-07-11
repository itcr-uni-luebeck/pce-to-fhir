import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {lastValueFrom} from 'rxjs';
import {SettingsService} from "../settings/settings.service";
import {SettingsCsSupplementComponent} from "./settingsCsSupplement.component";
import {SettingsCsSupplementComponentSpringBootService} from "./settingsCsSupplementSpringBoot.service";

@Injectable({
  providedIn: 'root'
})
export class SettingsCsSupplementService {
  public url_server = 'http://localhost:8080/';


  constructor(public httpClient: HttpClient, public settings: SettingsService, public springBootService: SettingsCsSupplementComponentSpringBootService) {
  }

  public start = '';
  public isOtherServer = false;
  public syntaxCheck = '';
  public serverChange = '';
  // public url_server_luebeck = 'https://ontoserver.imi.uni-luebeck.de/fhir';
  // public url_server_luebeck = '';
  public url_other_server = '';

  public listFullUrl = new Array();
  public listTitleUrl = new Array();
  public nameCsSupplement = '';

  clickSettings(id: string) {

    if(this.serverChange === '') {
      setTimeout(() => {
        let elementRange = document.getElementById('radioCs0');
        if (elementRange !== null) {
          //@ts-ignore
          elementRange.checked = true;
        }
      }, 10);
      this.serverChange = this.settings.listUrlServer[0];
    }

    this.start = id;
    // this.serverChange = this.url_server_luebeck;

    this.springBootService.getAllCsSupplements(this.serverChange).then((response: any) => {
      console.log(response)
      const value  = response.value;
      for (let i = 0; i < value.length; i++) {
        const fullUrl = value[i].url;
        const title = value[i].title;

        this.listTitleUrl.push(title);
        this.listFullUrl.push(fullUrl);
      }
    })
  }

  getServer(value: string, idx?: number) {
    if(value !== 'other') {
      this.isOtherServer = false;
      this.syntaxCheck = '';
      // @ts-ignore
      this.serverChange = this.settings.listUrlServer[idx];
      this.listTitleUrl = new Array();
      this.listFullUrl = new Array();
      this.springBootService.getAllCsSupplements(this.serverChange).then((response: any) => {
        const value = response.value;
        for (let i = 0; i < value.length; i++) {
          const fullUrl = value[i].url;
          const title = value[i].title;

          this.listTitleUrl.push(title);
          this.listFullUrl.push(fullUrl);
        }
      })
    } else if(value === 'other') {
      this.isOtherServer = true;
    }
  }

  checkUrl() {
    this.syntaxCheck = 'check';
    this.springBootService.checkSyntaxUrl(this.url_other_server).then((response: any) => {
      const result = response.result;
      if(result.toString() === 'true') {
        this.syntaxCheck = 'success';
        this.serverChange = this.url_other_server;
        this.listTitleUrl = new Array();
        this.listFullUrl = new Array();
        this.springBootService.getAllCsSupplements(this.serverChange).then((response: any) => {
          const value  = response.value;
          for (let i = 0; i < value.length; i++) {
            const fullUrl = value[i].url;
            const title = value[i].title;

            this.listTitleUrl.push(title);
            this.listFullUrl.push(fullUrl);
          }
        })
      } else {
        this.syntaxCheck = 'error';
      }
    })
  }

  getNames() {
    let list = new Array();
    for(let i = 0; i < this.listTitleUrl.length; i++) {
      if(!list.includes(this.listTitleUrl[i])) {
        list.push(this.listTitleUrl[i])
      }
    }
    return list;
  }

  isInputDisabled()  {
    if(this.isOtherServer && this.syntaxCheck !== 'success') {
      return true;
    }
    return false;
  }

  isSaveDisabled() {
    if(this.isOtherServer && this.syntaxCheck !== 'success' && !this.listTitleUrl.includes(this.nameCsSupplement)) {
      return true
    } else if((this.syntaxCheck === '' || this.syntaxCheck === 'success') && this.listTitleUrl.includes(this.nameCsSupplement)) {
      return false;
    } else {
      return true;
    }
  }


  public newUrl = '';

  loadCsSupplement() {
    let idx = 0;
    for (let i = 0; i < this.listTitleUrl.length; i++) {
      if(this.nameCsSupplement === this.listTitleUrl[i]) {
        idx = i;
        break;
      }
    }
    this.newUrl = this.listFullUrl[idx];
  }

  reset() {
    this.start = '';
    this.isOtherServer = false;
    this.syntaxCheck = '';
    this.serverChange = '';
    this.url_other_server = '';
    this.listFullUrl = new Array();
    this.listTitleUrl = new Array();
    this.nameCsSupplement = '';
  }

}
