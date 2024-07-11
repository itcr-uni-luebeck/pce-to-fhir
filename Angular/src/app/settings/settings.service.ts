import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {lastValueFrom} from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class SettingsService {
  public url_server = 'http://localhost:8000/';


  constructor(public httpClient: HttpClient) {
  }

  public start = '';

  // public url_server_luebeck = 'https://ontoserver.imi.uni-luebeck.de/fhir';
  // public url_server_csiro = 'https://r4.ontoserver.csiro.au/fhir';
  public serverInit = 'https://ontoserver.imi.uni-luebeck.de/fhir';

  // public serverInit = '';
  public url_other_server = '';

  public listNameServer = new Array();
  public listUrlServer = new Array();

  public serverChange = '';
  public isOtherServer = false;
  public syntaxCheck = '';

  public listSnomedVersions = new Array();
  public listSnomedVersionsDisplay = new Array();
  public versionChange = 'International edition -- 20230430';
  public versionHome = 'International edition -- 20230430';
  public sctVersion = 'http://snomed.info/sct/900000000000207008/version/20230430';

  public sctEdition : Record<string, string> = {
    '900000000000207008': 'International edition',
    '11000221109': 'Argentinian Edition',
    '32506021000036107': 'Australian edition',
    '11000172109': 'Belgian edition',
    '20611000087101': 'Canadian French edition',
    '20621000087109' : 'Canadian English edition',
    '554471000005108': 'Danish edition',
    '11000229106': 'Finnish edition',
    '11000146104': 'Netherlands edition',
    '21000210109': 'New Zealand edition',
    '449081005': 'Spanish edition',
    '45991000052106': 'Swedish edition',
    '83821000000107': 'UK edition',
    '5631000179106': 'Uruguay edition',
    '731000124108': 'US edition',
    '5991000124107': 'US edition (with ICD-10-CM maps)',
    '11000181102' : 'Estonian edition',
    '11000220105' : 'Irish edition',
    '51000202101' : 'Norwegian edition'
  }


  clickSettings(id: string) {
    if(this.serverChange === '') {
      setTimeout(() => {
        let elementRange = document.getElementById('radio0');
        if (elementRange != null) {
          //@ts-ignore
          elementRange.checked = true;
        }
      }, 10);
    }
    this.start = id;

    this.listSnomedVersions = new Array();
    this.listSnomedVersionsDisplay = new Array();
    this.getSnomedVersions(this.serverInit).then((response: any) => {
      const value = response.value;
      for(let i = 0; i < value.length; i++) {
        this.listSnomedVersions.push(value[i].result);
        const s = value[i].result.split("http://snomed.info/sct/").join("");
        const code = s.split("/version/")[0];
        const version = s.split("/version/")[1];
        console.log(this.sctEdition[code.toString()])
        if(this.sctEdition[code.toString()] !== undefined) {
          this.listSnomedVersionsDisplay.push(this.sctEdition[code.toString()] + " -- " + version);
        } else {
          this.listSnomedVersionsDisplay.push(value[i].result);
        }
      }
      this.listSnomedVersionsDisplay.sort();
      // this.versionChange = 'International edition -- 20220930';
      this.isLoadingReady = false;
    })
  }

  public isLoadingReady = true;

  getServer(value: string, idx?: number) {
    this.isLoadingReady = true;
    this.versionChange = '';
    if(value !== 'other') {
      this.isOtherServer = false;
      this.syntaxCheck = '';
      // @ts-ignore
      this.serverChange = this.listUrlServer[idx];
      this.listSnomedVersions = new Array();
      this.listSnomedVersionsDisplay = new Array();
      if(this.serverChange === undefined) {
        this.serverChange = this.serverInit;
      }
      this.getSnomedVersions(this.serverChange).then((response: any) => {
        const value = response.value;
        for(let i = 0; i < value.length; i++) {
          this.listSnomedVersions.push(value[i].result);
          const s = value[i].result.split("http://snomed.info/sct/").join("");
          const code = s.split("/version/")[0];
          const version = s.split("/version/")[1];
          if(this.sctEdition[code.toString()] !== undefined) {
            this.listSnomedVersionsDisplay.push(this.sctEdition[code.toString()] + " -- " + version);
          } else {
            this.listSnomedVersionsDisplay.push(value[i].result);
          }
        }
        this.listSnomedVersionsDisplay.sort();
        this.isLoadingReady = false;
      })
    } else {
      this.isOtherServer = true;
    }
  }

  checkUrl() {
    this.syntaxCheck = 'check';
    this.isLoadingReady = true;
    this.versionChange = '';
    this.listSnomedVersionsDisplay = new Array();
    this.listSnomedVersions = new Array();
    this.checkSyntaxUrl(this.url_other_server).then((response: any) => {
      const result = response.result;
      if(result.toString() === 'true') {
        this.syntaxCheck = 'success';
        this.serverChange = this.url_other_server;
        this.listSnomedVersions = new Array();
        this.listSnomedVersionsDisplay = new Array();
        this.getSnomedVersions(this.url_other_server).then((response: any) => {
          const value = response.value;
          for(let i = 0; i < value.length; i++) {
            this.listSnomedVersions.push(value[i].result);
            const s = value[i].result.split("http://snomed.info/sct/").join("");
            const code = s.split("/version/")[0];
            const version = s.split("/version/")[1];
            if(this.sctEdition[code.toString()] !== undefined) {
              this.listSnomedVersionsDisplay.push(this.sctEdition[code.toString()] + " -- " + version);
            } else {
              this.listSnomedVersionsDisplay.push(value[i].result);
            }
            // this.listSnomedVersionsDisplay.push(this.sctEdition[code.toString()] + " -- " + version);
          }
          this.listSnomedVersionsDisplay.sort();
          this.isLoadingReady = false;
        });
      } else {
        this.syntaxCheck = 'error';
      }
    })
  }

  isSaveDisabled() {
    if(this.isOtherServer && this.syntaxCheck !== 'success' && !this.listSnomedVersionsDisplay.includes(this.versionChange)) {
      return true
    } else if((this.syntaxCheck === '' || this.syntaxCheck === 'success') && this.listSnomedVersionsDisplay.includes(this.versionChange)) {
      return false;
    } else {
      return true;
    }
  }

  saveModal() {
    // this.serverInit = this.serverChange;
    this.versionHome = this.versionChange;
    for (const v in this.sctEdition) {
      if(this.sctEdition[v].toString().split(" ").join("") === this.versionChange.split("--")[0].toString().split(" ").join("")) {
        this.sctVersion = "http://snomed.info/sct/" + v + "/version/" + this.versionChange.split("--")[1].toString().split(" ").join("");
      }
    }
  }

  cancelModal() {
    for(let i = 0; i < this.listUrlServer.length; i++) {
      if(this.serverInit === this.listUrlServer[i]) {
        setTimeout(() => {
          let elementRange = document.getElementById('radio' + i);
          if (elementRange != null) {
            //@ts-ignore
            elementRange.checked = true;
            this.getServer(this.listNameServer[i]);
          }
        }, 10);
      }
    }

    // if(this.serverInit === this.url_server_luebeck) {
    //   setTimeout(() => {
    //     let elementRange = document.getElementById('radioLuebeck');
    //     if (elementRange != null) {
    //       //@ts-ignore
    //       elementRange.checked = true;
    //       this.getServer('luebeck');
    //     }
    //   }, 10);
    // } else if(this.serverInit === this.url_server_csiro) {
    //   setTimeout(() => {
    //     let elementRange = document.getElementById('radioCsiro');
    //     if (elementRange != null) {
    //       //@ts-ignore
    //       elementRange.checked = true;
    //     }
    //   }, 10);
    // } else {
    //   if(this.serverInit === this.url_server_luebeck) {
    //     setTimeout(() => {
    //       let elementRange = document.getElementById('radioOther');
    //       if (elementRange != null) {
    //         //@ts-ignore
    //         elementRange.checked = true;
    //       }
    //     }, 10);
    //   }
    // }
  }

  // Spring boot


  checkSyntaxUrl(url: string) {
    let u = this.encodeURIComponent(url);
    const urlRequest = this.url_server + 'settingsurl/?url=' + u;
    const response = this.httpClient.get(urlRequest);
    return lastValueFrom(response);
  }

  getSnomedVersions(url: string) {
    let u = this.encodeURIComponent(url);
    const urlRequest = this.url_server + 'settingssnomedversions/?url=' + u;
    const response = this.httpClient.get(urlRequest);
    console.log(urlRequest)
    return lastValueFrom(response);
  }


  encodeURIComponent(value: any): string {
    value += '';
    value = value.trim();
    value = encodeURIComponent(value);
    return value;
  }


}
