import {Component} from '@angular/core';
import {HttpClient} from "@angular/common/http";


import {SuperConceptDeltaService} from "./superConceptDelta.service";
import {AppService} from "../app.service";
import {SettingsService} from "../settings/settings.service";
import {PceStartComponent} from "../pceStart/pceStart.component";
import {PceStartService} from "../pceStart/pceStart.service";
import {SuperConceptDeltaSpringBootService} from "./superConceptDeltaSpringBoot.service";
import {HighlightingPceService} from "../highlightingPce/highlightingPce.service";

@Component({
  selector: 'superconcept-delta',
  templateUrl: './superConceptDelta.component.html',
  styleUrls: ['./superConceptDelta.component.css']
})

export class SuperConceptDeltaComponent {

  constructor(public superConceptDeltaService: SuperConceptDeltaService, private http: HttpClient, public appService: AppService, public settingsService: SettingsService, public pceStartService: PceStartService, public springBootService : SuperConceptDeltaSpringBootService, public highlightingPceService: HighlightingPceService) {
  }

  public finishPreprocessingPce = false;
  public superConceptCandidatesList = [];
  public superConceptCandidatesListPart1 = [];
  public superConceptCandidatesListPart2 = [];
  public superConceptDirectFcList = [];
  public finishSuperConceptCandidates = false;
  public finishSuperConcept = false;
  public finishDelta = false;
  public superConcept = false;
  public superConceptAttributeList = [];
  public superConceptValueList = [];
  public deltaAttributeList = [];
  public deltaValueList = [];
  public deltaIdRoleGroup = [];
  public finishFhir = false;

  public superConceptListFhirFull = [];
  public superConceptPathListFhirFull = [];
  public listSummaryValueFhirFull = [];
  public listSummaryPathFhirFull = [];
  public listSummaryValueWithFull = [];
  public listSummaryFhirPathWithFull = [];
  public referencesListFhirFull = [];
  public namesListFhirFull = [];
  public listSummaryNameFhirFull = [];
  public listSummaryFhirPrioFull = [];
  public listSummaryFhirSuperconceptPrioFull = [];

  public superConceptListFhirLight = [];
  public superConceptPathListFhirLight = [];
  public listSummaryValueFhirLight = [];
  public listSummaryPathFhirLight = [];
  public referencesListFhirLight = [];
  public namesListFhirLight = [];
  public listSummaryNameFhirLight = [];
  public listSummaryFhirPrioLight = [];
  public listSummaryFhirSuperconceptPrioLight = [];

  public idLightFull = 'light';
  public sourceProfile = Array();
  public nameProfile = '';

  public isCopyToClipboard = false;
  public isDownload = false;

  public ngOnInit() {

    this.sourceProfile.push("KBV-Basisprofile");
    this.sourceProfile.push("KDS der MII");

    this.springBootService.preprocessingPce(this.pceStartService.pce).then((response: any) => {

      console.log(this.pceStartService.pce)
      this.superConceptDeltaService.pceJson = response;
      // this.highlightingPceService.start(response);

      let result = response.result;

      this.highlightingPceService.focusConceptCode = result[0].focusConceptCode;
      this.highlightingPceService.focusConceptName = result[1].focusConceptName;
      this.highlightingPceService.listSummaryAttributeNamesWithout = result[2].listSummaryAttributeNamesWithout;
      this.highlightingPceService.listSummaryAttributeCodesWithout = result[3].listSummaryAttributeCodesWithout;
      this.highlightingPceService.listSummaryValueWithout = result[4].listSummaryValueWithout;
      this.highlightingPceService.listSummaryAttributeNamesWith = result[5].listSummaryAttributeNamesWith;
      this.highlightingPceService.listSummaryAttributeCodesWith = result[6].listSummaryAttributeCodesWith;
      this.highlightingPceService.listSummaryValueWith = result[7].listSummaryValueWith;
      if(this.highlightingPceService.listSummaryValueWith.length > 0) {
        this.highlightingPceService.isRoleGroup = true;
      }

      this.finishPreprocessingPce = true;
      this.superConceptDeltaService.finishPreprocessingPce = true;

      this.springBootService.getSuperConceptCandidates(this.pceStartService.pce).then((response1: any) => {
        this.superConceptCandidatesList = response1.result;
        this.superConceptDirectFcList = response1.directFc;

        this.finishSuperConceptCandidates = true;


        for (let i = 0; i < this.superConceptCandidatesList.length; i++) {
          if (i < this.superConceptCandidatesList.length / 2) {
            this.superConceptCandidatesListPart1.push(this.superConceptCandidatesList[i])
          } else {
            this.superConceptCandidatesListPart2.push(this.superConceptCandidatesList[i])
          }
        }

        // @ts-ignore
        this.springBootService.getSuperConcept(this.pceStartService.pce, this.superConceptCandidatesList, this.superConceptDirectFcList).then((response2: any) => {
          this.superConcept = response2.result;
          this.superConceptDeltaService.superConcept = response2.result;
          this.superConceptAttributeList = response2.attribute;
          this.superConceptValueList = response2.value;
          this.finishSuperConcept = true;


          this.springBootService.getDelta(this.pceStartService.pce).then((response3: any) => {

            this.deltaAttributeList = response3.attribute;
            this.deltaValueList = response3.value;
            this.deltaIdRoleGroup = response3.id;
            this.finishDelta = true;

            this.springBootService.getFhirElements(this.pceStartService.pce, JSON.stringify(response3)).then((response4: any) => {

              let result = response4.result;
              console.log(result)

              this.superConceptListFhirFull = result[0].superConceptListFull;
              this.superConceptPathListFhirFull = result[1].superConceptPathListFull;
              this.listSummaryValueFhirFull = result[2].listSummaryValueFull;
              this.listSummaryPathFhirFull = result[3].listSummaryFhirPathFull;
              this.listSummaryNameFhirFull = result[4].listSummaryNameFull;
              this.referencesListFhirFull = result[5].referencesListFull;
              this.namesListFhirFull = result[6].namesListFull;
              this.listSummaryFhirPrioFull = result[7].listSummaryFhirPrio;
              this.listSummaryFhirSuperconceptPrioFull = result[8].listSummaryFhirSuperconceptPrio;

              this.superConceptListFhirLight = result[9].superConceptListLight;
              this.superConceptPathListFhirLight = result[10].superConceptPathListLight;
              this.listSummaryValueFhirLight = result[11].listSummaryValueLight;
              this.listSummaryPathFhirLight = result[12].listSummaryFhirPathLight;
              this.listSummaryNameFhirLight = result[13].listSummaryNameLight;
              this.referencesListFhirLight = result[14].referencesListLight;
              this.namesListFhirLight = result[15].namesListLight;
              this.listSummaryFhirPrioLight = result[16].listSummaryFhirPrio;
              this.listSummaryFhirSuperconceptPrioLight = result[17].listSummaryFhirSuperconceptPrio;


              this.finishFhir = true;

            })
          })
        })
      })
    })
  }







  getFhirPath(i: number, j: number, idx: string) {
    var list: any[]
    if(this.idLightFull === 'light' && idx === 'superconcept') {
      list = this.superConceptPathListFhirLight;
    } else if(this.idLightFull === 'light' && idx === '') {
      list = this.listSummaryPathFhirLight;
    } else if(this.idLightFull === 'full' && idx === 'superconcept') {
      list = this.superConceptPathListFhirFull;
    } else if(this.idLightFull === 'full' && idx === '') {
      list = this.listSummaryPathFhirFull;
    }
    if(j > -1) {
      // @ts-ignore
      if(list[i][j] !== undefined) {
        // @ts-ignore
        if(list[i][j].length > 0) {
          // @ts-ignore
          return list[i][j].toString().split(" ").join("").split(",").join("\n").split("HL7InternationalExtension:").join("HL7 International Extension: ").split("(Part").join(" (Part");
        }
      }
    } else {
      // @ts-ignore
      if(list[i] !== undefined) {
        // @ts-ignore
        if(list[i].length > 0) {
          // @ts-ignore
          return list[i].toString().split(" ").join("").split(",").join("\n").split("HL7InternationalExtension:").join("HL7 International Extension: ").split("(Part").join(" (Part");
        }
      }
    }

    // @ts-ignore
    return list[i].split("(Part").join(" (Part")

  }

  clickCol(i: number,  j: number, k: number, id?:any, light?: string) {

    if(id === undefined) {
      id = 'element' + i + j + k + light;
    } else if(id === '') {
      id = 'element' + i + j + k + light;
    }

    let elementInput = document.getElementById(id);
    if (elementInput !== null) {
      // @ts-ignore
      let isChecked = elementInput.checked;
      // @ts-ignore
      elementInput.checked = isChecked;

      if(this.idLightFull === 'full') {
        if(id !== 'superconcept' + i ) {
          // @ts-ignore
          this.listSummaryFhirPrioFull[i][j][k] = isChecked;
        } else {
          // @ts-ignore
          this.listSummaryFhirSuperconceptPrioFull[i] = isChecked;
        }
      } else {
        if(id !== 'superconcept-light' + i ) {
          // @ts-ignore
          this.listSummaryFhirPrioLight[i][j][k] = isChecked;
        } else {
          // @ts-ignore
          this.listSummaryFhirSuperconceptPrioLight[i] = isChecked;
        }
      }
    }

  }

  getId(i: number, j: number, k: number, dummy?: string) {
    return 'element' + i + j + k + dummy
  }

  checkSwitch(i: number, j: number, k: number, id: string) {
    if(id === 'full-path') {
      // @ts-ignore
      return this.listSummaryPathFhirFull[i][j][k].length > 1 ;
    } else if(id === 'full-superconcept') {
      // @ts-ignore
      return this.superConceptPathListFhirFull[i].length > 1;
    } else if(id === 'light-path') {
      // @ts-ignore
      return this.listSummaryPathFhirLight[i][j][k].length > 1 ;
    } else if(id === 'light-superconcept') {
      // @ts-ignore
      return this.superConceptPathListFhirLight[i].length > 1;
    }
    return false;
  }

  getFhirPathReferences(value: string, i:number) {
    return value.split("->")[i].split("@")[0];
  }

  getFhirName(value: string, i:number) {
    return value.split("->")[i].split("@")[0];
  }

  checkReferences() {
    if(this.idLightFull === 'light') {
      for (let i = 0; i < this.listSummaryNameFhirLight.length; i++) {
        if(this.nameProfile === this.listSummaryNameFhirLight[i]) {
          // @ts-ignore
          return this.referencesListFhirLight[i].length > 0;
        }
      }
    } else if(this.idLightFull === 'full') {
      for (let i = 0; i < this.listSummaryNameFhirFull.length; i++) {
        if(this.nameProfile === this.listSummaryNameFhirFull[i]) {
          // @ts-ignore
          return this.referencesListFhirFull[i].length > 0;
        }
      }
    }
    return false;
  }

  getRowSpan(value: any) {
    if (value !== undefined) {
      return value.length;
    }
    return 0;
  }


  changeVersion(id: string) {
    this.idLightFull = id;
  }



  onKeyDown(event: any) {
    if (event.key !== 'Backspace') {
      event.stopPropagation();
      event.preventDefault();
      return;
    }
    this.nameProfile = '';
  }



  copyToClipboard() {
    let result = this.getResult();
    const selBox = document.createElement('textarea');
    selBox.style.position = 'fixed';
    selBox.style.left = '0';
    selBox.style.top = '0';
    selBox.style.opacity = '0';
    selBox.value = result;
    document.body.appendChild(selBox);
    selBox.focus();
    selBox.select();
    document.execCommand('copy');
    document.body.removeChild(selBox);
    this.isCopyToClipboard = true;
    setTimeout(() => {
      this.isCopyToClipboard = false;
    }, 2000);
  }

  download() {
    this.getResult();
    this.isDownload = true;
    setTimeout(() => {
      this.isDownload = false;
    }, 2000);
  }

  public result = "";
  getResult() {
    let result = "";

    let listSummaryNameFhir: string | any[] = [];
    let listSummaryFhirSuperconceptPrio: any[] = []
    let superConceptListFhir: string[] = []
    let superConceptPathListFhir: string | never[] = []
    let listSummaryPathFhir: string[][][] = []
    let listSummaryFhirPrio: any[][][] = []
    let listSummaryValueFhir: string[][] = []
    let referencesListFhir: string[][] = []
    let namesListFhir: string[][] = []

    if(this.idLightFull === 'full') {
      listSummaryNameFhir = this.listSummaryNameFhirFull;
      listSummaryFhirSuperconceptPrio = this.listSummaryFhirSuperconceptPrioFull;
      superConceptListFhir = this.superConceptListFhirFull;
      superConceptPathListFhir = this.superConceptPathListFhirFull;
      listSummaryPathFhir = this.listSummaryPathFhirFull;
      listSummaryFhirPrio = this.listSummaryFhirPrioFull;
      listSummaryValueFhir = this.listSummaryValueFhirFull;
      referencesListFhir = this.referencesListFhirFull;
      namesListFhir = this.namesListFhirFull;
    } else {
      listSummaryNameFhir = this.listSummaryNameFhirLight;
      listSummaryFhirSuperconceptPrio = this.listSummaryFhirSuperconceptPrioLight;
      superConceptListFhir = this.superConceptListFhirLight;
      superConceptPathListFhir = this.superConceptPathListFhirLight;
      listSummaryPathFhir = this.listSummaryPathFhirLight;
      listSummaryFhirPrio = this.listSummaryFhirPrioLight;
      listSummaryValueFhir = this.listSummaryValueFhirLight;
      referencesListFhir = this.referencesListFhirLight;
      namesListFhir = this.namesListFhirLight;
    }

    for (let  i = 0; i < listSummaryNameFhir.length; i++) {
      if(listSummaryNameFhir[i] === this.nameProfile) {
        // super concept
        if(listSummaryFhirSuperconceptPrio[i]) {
          result = result +
            "---- super concept ---- \n" +
            superConceptListFhir[i] + " --> " + superConceptPathListFhir[i] + "\n" ;
        }
        // attribute
        // @ts-ignore
        for (let j = 0; j < listSummaryPathFhir[i].length; j++) {
          let s = "";
          // @ts-ignore
          for (let k = 0; k < listSummaryPathFhir[i][j].length; k++) {
            if(listSummaryFhirPrio[i][j][k]) {
              s = s + listSummaryPathFhir[i][j][k] + "; ";
            }
          }
          s = s.substring(0, s.lastIndexOf(";"));
          if(s.length > 0) {
            if(!result.includes("---- attribute ----")) {
              result = result +
                "---- attribute ---- \n";
            }
            result = result +
              listSummaryValueFhir[i][j] + " --> " + s + "\n";
          }
        }
        // references
        // @ts-ignore
        for (let j = 0; j < referencesListFhir[i].length; j++) {
          // @ts-ignore
          let ref = referencesListFhir[i][j].split("@")[0];
          if(result.includes(ref.split("->")[1])) {
            if(!result.includes("---- reference ----")) {
              result = result +
                "---- reference ---- \n";
            }
            result = result + ref.split("->").join(" --> ") + "\n";
          }
        }
        // names of profile
        // @ts-ignore
        for (let j = 0; j < namesListFhir[i].length; j++) {
          // @ts-ignore
          let name = namesListFhir[i][j].split("@")[0];
          if(result.includes(name.split("->")[0])) {
            if (!result.includes("---- profile name ----")) {
              result = result +
                "---- profile name ---- \n";
            }
            // @ts-ignore
            result = result + name.split("->").join(" --> ") + "\n";
          }
        }
      }
    }
    this.result= result;
    return result;
  }


}
