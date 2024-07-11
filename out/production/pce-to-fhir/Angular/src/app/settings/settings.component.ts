import {Component} from '@angular/core';
import {HttpClient} from "@angular/common/http";


import {SettingsService} from "./settings.service";
import {AppService} from "../app.service";

@Component({
  selector: 'app-settings',
  templateUrl: './settings.component.html',
  styleUrls: ['./settings.component.css']
})

export class SettingsComponent {

  constructor(private settingService: SettingsService, private http: HttpClient, public appService: AppService, public settingsService: SettingsService) {
  }





}

