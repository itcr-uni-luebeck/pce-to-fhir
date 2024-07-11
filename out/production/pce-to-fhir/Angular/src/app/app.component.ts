import {Component} from '@angular/core';
import {AppService} from "./app.service";
import {HttpClient} from "@angular/common/http";
import {Title} from "@angular/platform-browser";
import {SettingsService} from "./settings/settings.service";
import {SuperConceptDeltaService} from "./superConceptDelta/superConceptDelta.service";
import * as events from "events";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})


export class AppComponent {

  constructor(public appService: AppService, private http: HttpClient, private titleService:Title, public settingsService: SettingsService,  public superConceptDeltaService: SuperConceptDeltaService) {
    this.titleService.setTitle("PCEtoFHIR");
  }

  test = '';
  testList = [];

  ngOnInit(): void {
    // @ts-ignore
    this.testList.push('Resource.codeA');
    // @ts-ignore
    this.testList.push('Resource.codeB');

    this.appService.getStoredServer().then((response: any) => {
      for(let i = 0; i < response.length; i++) {
        this.settingsService.listNameServer.push(response[i].name);
        this.settingsService.listUrlServer.push(response[i].url.split(" ").join(""));
        if(i === 0) {
          this.settingsService.serverInit = response[i].url.split(" ").join("");
        }
      }
    })
  }

  onKeyDown(event: any) {
    if (event.key !== 'Backspace') {
      event.stopPropagation();
      event.preventDefault();
      return;
    }
    this.test = '';
    // // event.keyCode = null;
    // if(event.keyCode === 81) {
    //   event.preventDefault;
    // }
    // console.log("HIER EVENT " + event.keyCode)
  }

  onKeyDownDelete() {
    this.test = "";
  }

  click(id: number) {
    // let element = document.getElementById(id);
    // if (element != null) {
    //   if(element.style.color === 'black') {
    //     // @ts-ignore
    //     element.style.color = 'orange';
    //   } else {
    //     // @ts-ignore
    //     element.style.color = 'black';
    //   }
    //
    // }

    let tables = document.querySelectorAll("table");
    for (let i = 0; i < tables.length; i++) {
      let trs = document.querySelectorAll("td");
      for (let j = 0; j < trs.length; j++) {
        let element = trs[j];
        if (element != null) {
            if(element.id === 'element' + id) {
              if(element.style.color === '' || element.style.color === 'black') {
                // @ts-ignore
                element.style.color = '#bb2d3b';
              } else {
                  // @ts-ignore
                  element.style.color = 'black';
              }
            }
        }
      }
    }
  }

  getId(id: number) {
    id = id + 1;
    return 'element' + id;
  }


}

