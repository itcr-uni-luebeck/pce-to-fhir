import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {SettingsService} from "../settings/settings.service";
import {AppService} from "../app.service";

@Injectable({
  providedIn: 'root'
})
export class HighlightingPceService {
  public url_server = 'http://localhost:8000/';

  constructor(public httpClient: HttpClient, private settingsService: SettingsService, private  appService: AppService) {
  }

  focusConceptCode = '';
  focusConceptName = '';
  listSummaryAttributeNamesWithout = new Array();
  listSummaryAttributeCodesWithout = new Array();
  listSummaryValueWithout = new Array();
  listSummaryAttributeNamesWith = new Array();
  listSummaryAttributeCodesWith = new Array();
  listSummaryValueWith = new Array();
  isRefinement = false;
  isRoleGroup = false;
  input = '';


  getFocusconcept() {
    let s = "";
    if(this.focusConceptCode.includes("+")) {
      let l1 = this.focusConceptCode.split("+");
      let l2 = this.focusConceptName.split("+");
      for(let i = 0; i < l2.length; i++) {
        s = s + l1[i] + " |" + l2[i] + "|" + " + ";
      }
      if(l1.length > 1) {
        s = s.substring(0, s.lastIndexOf(" + "));
      }
    } else {
      s = this.focusConceptCode + " |" + this.focusConceptName + "|";
    }

    return s;
  }


  checkRG0(rg: number, subrg: number) {
    for(let i = 0; i < this.listSummaryAttributeCodesWith[rg][subrg].length; i++) {
      if(this.listSummaryAttributeCodesWith[rg][subrg] !== undefined) {
        if(this.listSummaryAttributeCodesWith[rg][subrg].length > 0) {
          return true;
        }
      }
    }
    return false;
  }

  checkRG1(rg: number, subrg: number, col: number) {
     if(this.listSummaryAttributeCodesWith[rg][subrg][col] !==  undefined && this.listSummaryAttributeNamesWith[rg][subrg][col] !== undefined) {
       if(this.listSummaryValueWith[rg][subrg][col].length > 0) {
         return true;
       }
     }
     return false;
  }

  checkRG2(rg: number, subrg: number, col: number) {
    return (this.listSummaryAttributeNamesWith[rg][subrg].length - 1  > col);
  }


  checkRG1Template(rg: number, subrg: number) {
    if(this.listSummaryAttributeCodesWith[rg] !==  undefined && this.listSummaryAttributeNamesWith[rg] !== undefined) {
      if(this.listSummaryValueWith[rg][subrg].length > 0) {
        return true;
      }
    }
    return false;
  }

  checkRG2Template(rg: number, subrg: number) {
    return (this.listSummaryAttributeNamesWith[rg].length - 1  > subrg);
  }

  checkRG0Template(rg: number, subrg: number) {
    // for(let i = 0; i < this.listSummaryAttributeCodesWith[rg][subrg].length; i++) {
    //   console.log(this.listSummaryAttributeCodesWith[rg][subrg])
    //   if(this.listSummaryAttributeCodesWith[rg][subrg] !== undefined) {
    //     if(this.listSummaryAttributeCodesWith[rg][subrg].length > 0) {
    //       return true;
    //     }
    //   }
    // }
    // return false;

    return true;
  }




}
