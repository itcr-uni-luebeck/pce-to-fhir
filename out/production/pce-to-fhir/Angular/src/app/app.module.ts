import {NgModule} from '@angular/core';
import {BrowserModule} from '@angular/platform-browser';

import {AppComponent} from './app.component';
import {FormsModule} from "@angular/forms";
import {HttpClientModule} from "@angular/common/http";
import {NgbModule} from "@ng-bootstrap/ng-bootstrap";
import {SettingsComponent} from "./settings/settings.component";
import {PceStartComponent} from "./pceStart/pceStart.component";
import {SuperConceptDeltaComponent} from "./superConceptDelta/superConceptDelta.component";
import {HighlightingPceComponent} from "./highlightingPce/highlightingPce.component";
import {SettingsCsSupplementComponent} from "./settingsCsSupplement/settingsCsSupplement.component";


@NgModule({
  declarations: [
    AppComponent,
    SettingsComponent,
    PceStartComponent,
    SuperConceptDeltaComponent,
    HighlightingPceComponent,
    SettingsCsSupplementComponent
  ],
  imports: [
    BrowserModule,
    FormsModule,
    HttpClientModule,
    NgbModule,
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
