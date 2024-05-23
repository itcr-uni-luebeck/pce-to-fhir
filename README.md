# PCEtoFHIR - FHIR StructureMaps

[![View Site - on GitHub Pages](https://img.shields.io/badge/View_Site-on_GitHub_Pages-blueviolet?logo=github)](https://itcr-uni-luebeck.github.io/pce-to-fhir/)

## Table of Contents

1. [General](#1-general)
2. [Allergies](#2-allergies)
3. [Disease due to allergies](#3-disease-due-to-allergies)
4. [Allergic reaction](#4-allergic-reaction)
5. [Clinical finding (general)](#5-clinical-finding-general)
6. [Procedure](#6-procedure)

## 1. General
The aim of PCEtoFHIR is to develop and implement an approach that enables the storage of a SNOMED CT post-coordinated expression (PCE) within FHIR resources by using only pre-coordinated codes. For this alternative representation, the PCE shall be decomposed into pre-coordinated concepts, which can then be stored in suitable elements of matching FHIR resources.  An overview of the envisioned approach is shown in Figure 2.
A PCE, which is first checked for syntactic and semantic correctness, serves as input. This can be classified within SNOMED CT by using OWL and a reasoner, whereby the direct supertype ancestors can be determined. Of these concepts, the most similar concept to the PCE is determined (superconcept). The delta can then be calculated between the superconcept and the PCE, which includes all the information of the PCE that the superconcept does not represent. In the final step, appropriate elements of matching FHIR resources must be identified to store the information of the superconcept and the delta. In our work, two sets of FHIR profiles were considered as target representations for the mapping:
* profiles of the German National Association of Statutory Health Insurance Physicians (NASHIP, version 1.4.0, based on FHIR R4) [1] 
* profiles of the core data set of the German Medical Informatics Initiative (MII, version 1.0, based on FHIR R4) [2].  

Therefore, FHIR StructureMaps defining these associations on a general level need to be created beforehand.

The [FHIR StructureMaps]() and the mapping of the individual elements in tabular form are provided in this repository.

<img src="Images\overview-methods.png" style="width:80%; display: block; margin-left: auto; margin-right: auto; margin-bottom: 30px"/>

#### Publication
Ohlsen T, Drenkhahn C, Ingenerf J. Decomposition of post-coordinated SNOMED CT expressions for storage as HL7 FHIR resources (PCEtoFHIR) (Preprint). JMIR Medical Informatics. Published online February 28, 2024. doi:10.2196/preprints.57853


## 2. Allergies
Value rage: << 609328004 |Allergic disposition (finding)|

<table style="margin-left: auto; margin-right: auto;  margin-bottom: 30px">
    <tr>
        <th> <p style="margin-bottom:-5px">SNOMED CT element</p> </th>
        <th> <p style="margin-bottom:-5px">FHIRPath NASHIP</p> </th>
        <th> <p style="margin-bottom:-5px">FHIRPath MII</p> </th>
    </tr>
    <tr>
        <td> <p style="margin-bottom:-5px"><font color="#FF7F0E">Super concept</font></p></td>
        <td>---</td>
        <td>Condition.code</td>
    </tr>
    <tr>
        <td> <p style="margin-bottom:-5px"><font color="#FF7F0E">Causative agent</font></p></td>
        <td>AllergyIntolerance.code</td>
        <td>---</td>
    </tr>
    <tr>
        <td rowspan="2"> <p style="margin-bottom:-5px"><font color="#FF7F0E">Finding site</font></p> </td>
        <td rowspan="2"> <p style="margin-bottom:-5px">Extension von HL7 International: <br>AllergyIntolerance.openEHR-location</p> </td>
        <td> <p style="margin-bottom:-5px">Observation.bodySite</p> </td>
    </tr>
    <tr>
        <td> <p style="margin-bottom:-5px">Condition.bodySite</p> </td>
    </tr>
    <tr>
        <td rowspan="2"> <p style="margin-bottom:-5px"><font color="#FF7F0E">Associated morphology</font></p> </td>
        <td rowspan="2"> <p style="margin-bottom:-5px">AllergyIntolerance.reaction.manifestation.coding:snomed</p> </td>
        <td> <p style="margin-bottom:-5px">Observation.code</p> </td>
    </tr>
    <tr>
        <td> <p style="margin-bottom:-5px">Condition.evidence.code</p> </td>
    </tr>
    <tr>
        <td> <p style="margin-bottom:-5px"><font color="#FF7F0E">Pathological process</font></p> </td>
        <td> <p style="margin-bottom:-5px">AllergyIntolerance.reaction.manifestation.coding:snomed</p> </td>
        <td> <p style="margin-bottom:-5px">Condition.evidence.code</p> </td>
    </tr>
    <tr>
        <td> <p style="margin-bottom:-5px"><font color="#FF7F0E">Has realization</font></p> </td>
        <td> <p style="margin-bottom:-5px">AllergyIntolerance.reaction.manifestation.coding:snomed</p> </td>
        <td> <p style="margin-bottom:-5px">Condition.evidence.code</p> </td>
    </tr>
    <tr>
        <td> <p style="margin-bottom:-5px"><font color="#FF7F0E">Occurrence</font></p></td>
        <td>AllergyIntolerance.onsetAge.extension:lebensphase-von</td>
        <td>Condition.onset[x]:onsetPeriod.start.extension:lebensphase-von</td>
    </tr>
    <tr>
        <td> <p style="margin-bottom:-5px"><font color="#FF7F0E">Clinical course</font></p></td>
        <td>---</td>
        <td>Extension von HL7 International: <br>Condition.condition-diseaseCourse</td>
    </tr>
    <tr>
        <td> <p style="margin-bottom:-5px"><font color="#FF7F0E">Due to</font></p></td>
        <td>---</td>
        <td>Extension von HL7 International: <br>Condition.condition-dueTo</td>
    </tr>
</table>


#### References
MII:
* Condition.evidence.detail referenced Observation

#### Names of profiles
NASHIP:
* KBV_PR_Base_AllergyIntolerance

MII:
* Profile - Observation - Laboruntersuchung
* Profile - Condition - Diagnose


## 3. Disease due to allergies
Value rage: <<781474001 |Allergic disorder (disorder)|

<table style="margin-left: auto; margin-right: auto;  margin-bottom: 30px">
    <tr>
        <th> <p style="margin-bottom:-5px">SNOMED CT element</p> </th>
        <th> <p style="margin-bottom:-5px">FHIRPath NASHIP</p> </th>
        <th> <p style="margin-bottom:-5px">FHIRPath MII</p> </th>
    </tr>
    <tr>
        <td> <p style="margin-bottom:-5px"><font color="#FF7F0E">Super concept</font></p></td>
        <td>Condition.code</td>
        <td>Condition.code</td>
    </tr>
    <tr>
        <td> <p style="margin-bottom:-5px"><font color="#FF7F0E">Causative agent</font></p></td>
        <td>AllergyIntolerance.code</td>
        <td>---</td>
    </tr>
    <tr>
        <td rowspan="2"> <p style="margin-bottom:-5px"><font color="#FF7F0E">Finding site</font></p> </td>
        <td rowspan="2"> <p style="margin-bottom:-5px">Condition.bodySite</p> </td>
        <td> <p style="margin-bottom:-5px">Observation.bodySite</p> </td>
    </tr>
    <tr>
        <td> <p style="margin-bottom:-5px">Condition.bodySite</p> </td>
    </tr>
    <tr>
        <td rowspan="2"> <p style="margin-bottom:-5px"><font color="#FF7F0E">Associated morphology</font></p> </td>
        <td> <p style="margin-bottom:-5px">AllergyIntolerance.reaction.manifestation.coding:snomed</p> </td>
        <td> <p style="margin-bottom:-5px">Observation.code</p> </td>
    </tr>
    <tr>
        <td> <p style="margin-bottom:-5px"></p>Condition.evidence.code</td>
        <td> <p style="margin-bottom:-5px">Condition.evidence.code</p> </td>
    </tr>
    <tr>
        <td rowspan="2"> <p style="margin-bottom:-5px"><font color="#FF7F0E">Pathological process</font></p> </td>
        <td> <p style="margin-bottom:-5px">AllergyIntolerance.reaction.manifestation.coding:snomed</p> </td>
        <td rowspan="2"> <p style="margin-bottom:-5px">Condition.evidence.code</p> </td>
    </tr>
    <tr>
        <td> <p style="margin-bottom:-5px">Condition.evidence.code</p> </td>
    </tr>
    <tr>
        <td rowspan="2"> <p style="margin-bottom:-5px"><font color="#FF7F0E">Has realization</font></p> </td>
        <td> <p style="margin-bottom:-5px">AllergyIntolerance.reaction.manifestation.coding:snomed</p> </td>
        <td rowspan="2"> <p style="margin-bottom:-5px">Condition.evidence.code</p> </td>
    </tr>
    <tr>
        <td> <p style="margin-bottom:-5px">Condition.evidence.code</p> </td>
    </tr>
    <tr>
        <td rowspan="2"> <p style="margin-bottom:-5px"><font color="#FF7F0E">Occurrence</font></p></td>
        <td>Condition.onset[x]:onsetAge.extension:lebensphase-von</td>
        <td rowspan="2">Condition.onset[x]:onsetPeriod.start.extension:lebensphase-von</td>
    </tr>
    <tr>
        <td> <p style="margin-bottom:-5px">AllergyIntolerance.onsetAge.extension:lebensphase-von</p> </td>
    </tr>
    <tr>
        <td> <p style="margin-bottom:-5px"><font color="#FF7F0E">Clinical course</font></p></td>
        <td>Extension von HL7 International: <br>Condition.condition-diseaseCourse</td>
        <td>Extension von HL7 International: <br>Condition.condition-diseaseCourse</td>
    </tr>
    <tr>
        <td> <p style="margin-bottom:-5px"><font color="#FF7F0E">Due to</font></p></td>
        <td>Extension von HL7 International: <br>Condition.condition-dueTo</td>
        <td>Extension von HL7 International: <br>Condition.condition-dueTo</td>
    </tr>
</table>


#### References
NASHIP:
*  Condition.evidence.detail referenced Observation 

MII:
* Condition.evidence.detail referenced Observation

#### Names of profiles
NASHIP:
* KBV_PR_Base_AllergyIntolerance
* KBV_PR_Base_Condition_Diagnosis

MII:
* Profile - Observation - Laboruntersuchung
* Profile - Condition - Diagnose

## 4. Allergic reaction

Value rage: <<419076005 |Allergic reaction (disorder)|

<table style="margin-left: auto; margin-right: auto;  margin-bottom: 30px">
    <tr>
        <th> <p style="margin-bottom:-5px">SNOMED CT element</p> </th>
        <th> <p style="margin-bottom:-5px">FHIRPath NASHIP</p> </th>
        <th> <p style="margin-bottom:-5px">FHIRPath MII</p> </th>
    </tr>
    <tr>
        <td> <p style="margin-bottom:-5px"><font color="#FF7F0E">Super concept</font></p></td>
        <td>Condition.code</td>
        <td>Condition.code</td>
    </tr>
    <tr>
        <td> <p style="margin-bottom:-5px"><font color="#FF7F0E">Causative agent</font></p></td>
        <td>AllergyIntolerance.code</td>
        <td>---</td>
    </tr>
    <tr>
        <td rowspan="2"> <p style="margin-bottom:-5px"><font color="#FF7F0E">Finding site</font></p> </td>
        <td rowspan="2"> <p style="margin-bottom:-5px">Condition.bodySite</p> </td>
        <td> <p style="margin-bottom:-5px">Observation.bodySite</p> </td>
    </tr>
    <tr>
        <td> <p style="margin-bottom:-5px">Condition.bodySite</p> </td>
    </tr>
    <tr>
        <td rowspan="2"> <p style="margin-bottom:-5px"><font color="#FF7F0E">Associated morphology</font></p> </td>
        <td> <p style="margin-bottom:-5px">AllergyIntolerance.reaction.manifestation.coding:snomed</p> </td>
        <td> <p style="margin-bottom:-5px">Observation.code</p> </td>
    </tr>
    <tr>
        <td> <p style="margin-bottom:-5px"></p>Condition.evidence.code</td>
        <td> <p style="margin-bottom:-5px">Condition.evidence.code</p> </td>
    </tr>
    <tr>
        <td rowspan="2"> <p style="margin-bottom:-5px"><font color="#FF7F0E">Pathological process</font></p> </td>
        <td> <p style="margin-bottom:-5px">AllergyIntolerance.reaction.manifestation.coding:snomed</p> </td>
        <td rowspan="2"> <p style="margin-bottom:-5px">Condition.evidence.code</p> </td>
    </tr>
    <tr>
        <td> <p style="margin-bottom:-5px">Condition.evidence.code</p> </td>
    </tr>
    <tr>
        <td rowspan="2"> <p style="margin-bottom:-5px"><font color="#FF7F0E">Has realization</font></p> </td>
        <td> <p style="margin-bottom:-5px">AllergyIntolerance.reaction.manifestation.coding:snomed</p> </td>
        <td rowspan="2"> <p style="margin-bottom:-5px">Condition.evidence.code</p> </td>
    </tr>
    <tr>
        <td> <p style="margin-bottom:-5px">Condition.evidence.code</p> </td>
    </tr>
    <tr>
        <td rowspan="2"> <p style="margin-bottom:-5px"><font color="#FF7F0E">Occurrence</font></p></td>
        <td>Condition.onset[x]:onsetAge.extension:lebensphase-von</td>
        <td rowspan="2">Condition.onset[x]:onsetPeriod.start.extension:lebensphase-von</td>
    </tr>
    <tr>
        <td> <p style="margin-bottom:-5px">AllergyIntolerance.onsetAge.extension:lebensphase-von</p> </td>
    </tr>
    <tr>
        <td> <p style="margin-bottom:-5px"><font color="#FF7F0E">Clinical course</font></p></td>
        <td>Extension von HL7 International: <br>Condition.condition-diseaseCourse</td>
        <td>Extension von HL7 International: <br>Condition.condition-diseaseCourse</td>
    </tr>
    <tr>
        <td> <p style="margin-bottom:-5px"><font color="#FF7F0E">Due to</font></p></td>
        <td>Extension von HL7 International: <br>Condition.condition-dueTo</td>
        <td>Extension von HL7 International: <br>Condition.condition-dueTo</td>
    </tr>
</table>

#### References
NASHIP:
*  Condition.evidence.detail referenced Observation 

MII:
* Condition.evidence.detail referenced Observation

#### Names of profiles
NASHIP:
* KBV_PR_Base_AllergyIntolerance
* KBV_PR_Base_Condition_Diagnosis

MII:
* Profile - Observation - Laboruntersuchung
* Profile - Condition - Diagnose



## 5. Clinical finding (general)
Value range: <<404684003 | Clinical finding (finding)|  MINUS (<<781474001 |Allergic disorder (disorder)| OR << 609328004 |Allergic disposition (finding)| OR <<419076005 |Allergic reaction (disorder)| )

<table style="margin-left: auto; margin-right: auto;  margin-bottom: 30px">
    <tr>
        <th> <p style="margin-bottom:-5px">SNOMED CT element</p> </th>
        <th> <p style="margin-bottom:-5px">FHIRPath NASHIP</p> </th>
        <th> <p style="margin-bottom:-5px">FHIRPath MII</p> </th>
    </tr>
    <tr>
        <td> <p style="margin-bottom:-5px"><font color="#FF7F0E">Super concept</font></p></td>
        <td>Condition.code</td>
        <td>Condition.code</td>
    </tr>
    <tr>
        <td rowspan="2"> <p style="margin-bottom:-5px"><font color="#FF7F0E">Finding site</font></p> </td>
        <td rowspan="2"> <p style="margin-bottom:-5px">Condition.bodySite</p> </td>
        <td> <p style="margin-bottom:-5px">Observation.bodySite</p> </td>
    </tr>
    <tr>
        <td> <p style="margin-bottom:-5px">Condition.bodySite</p> </td>
    </tr>
    <tr>
        <td> <p style="margin-bottom:-5px"><font color="#FF7F0E">Associated morphology</font></p> </td>
        <td> <p style="margin-bottom:-5px">Condition.evidence.code</p> </td>
        <td> <p style="margin-bottom:-5px"></p>Condition.evidence.code</td>
    </tr>
    <tr>
        <td> <p style="margin-bottom:-5px"><font color="#FF7F0E">Pathological process</font></p> </td>
        <td> <p style="margin-bottom:-5px">Condition.evidence.code</p> </td>
        <td> <p style="margin-bottom:-5px">Condition.evidence.code</p> </td>
    </tr>
    <tr>
        <td> <p style="margin-bottom:-5px"><font color="#FF7F0E">Clinical course</font></p></td>
        <td>Extension von HL7 International: <br>Condition.condition-diseaseCourse</td>
        <td>Extension von HL7 International: <br>Condition.condition-diseaseCourse</td>
    </tr>
    <tr>
        <td> <p style="margin-bottom:-5px"><font color="#FF7F0E">Due to</font></p></td>
        <td>Extension von HL7 International: <br>Condition.condition-dueTo</td>
        <td>Extension von HL7 International: <br>Condition.condition-dueTo</td>
    </tr>
</table>


#### References
NASHIP:
*  Condition.evidence.detail referenced Observation 

MII:
* Condition.evidence.detail referenced Observation

#### Names of profiles
NASHIP:
* KBV_PR_Base_Condition_Diagnosis

MII:
* Profile - Observation - Laboruntersuchung
* Profile - Condtion - Diagnosis



## 6. Procedure
Value range: <<71388002 |Procedure (procedure)|


<table style="margin-left: auto; margin-right: auto;  margin-bottom: 30px">
    <tr>
        <th> <p style="margin-bottom:-5px">SNOMED CT element</p> </th>
        <th> <p style="margin-bottom:-5px">FHIRPath NASHIP</p> </th>
        <th> <p style="margin-bottom:-5px">FHIRPath MII</p> </th>
    </tr>
    <tr>
        <td> <p style="margin-bottom:-5px"><font color="#FF7F0E">Super concept</font></p></td>
        <td>Procedure.code</td>
        <td>Procedure.code</td>
    </tr>
    <tr>
        <td> <p style="margin-bottom:-5px"><font color="#FF7F0E">Method</font></p></td>
        <td>Extension von HL7 International: <br> Procedure.procedure-method</td>
        <td>Extension von HL7 International: <br> Procedure.procedure-method</td>
    </tr>
    <tr>
        <td> <p style="margin-bottom:-5px"><font color="#FF7F0E">Procedure site - Direct</font></p></td>
        <td>Procedure.bodySite</td>
        <td>Procedure.bodySite</td>
    </tr>
    <tr>
        <td> <p style="margin-bottom:-5px"><font color="#FF7F0E">Procedure site - Indirect</font></p></td>
        <td>Procedure.bodySite</td>
        <td>Procedure.bodySite</td>
    </tr>
    <tr>
        <td> <p style="margin-bottom:-5px"><font color="#FF7F0E">Dirct substance</font></p></td>
        <td>Procedure.usedCode</td>
        <td>Procedure.usedCode</td>
    </tr>
    <tr>
        <td> <p style="margin-bottom:-5px"><font color="#FF7F0E">Dirct morphology</font></p></td>
        <td>Procedure.bodySite</td>
        <td>Procedure.bodySite</td>
    </tr>
    <tr>
        <td> <p style="margin-bottom:-5px"><font color="#FF7F0E">Using substance</font></p></td>
        <td>Procedure.usedCode</td>
        <td>Procedure.usedCode</td>
    </tr>
    <tr>
        <td> <p style="margin-bottom:-5px"><font color="#FF7F0E">Using device</font></p></td>
        <td>Procedure.usedCode</td>
        <td>Procedure.usedCode</td>
    </tr>
    <tr>
        <td> <p style="margin-bottom:-5px"><font color="#FF7F0E">Using access device</font></p></td>
        <td>Procedure.usedCode</td>
        <td>Procedure.usedCode</td>
    </tr>
    <tr>
        <td> <p style="margin-bottom:-5px"><font color="#FF7F0E">Has intent</font></p></td>
        <td>Procedure.category</td>
        <td>Procedure.category</td>
    </tr>
    <tr>
        <td> <p style="margin-bottom:-5px"><font color="#FF7F0E">Access</font></p></td>
        <td>Procedure.usedCode</td>
        <td>Procedure.usedCode</td>
    </tr>
    <tr>
        <td> <p style="margin-bottom:-5px"><font color="#FF7F0E">Surgical approach</font></p></td>
        <td>Procedure.usedCode</td>
        <td>Procedure.usedCode</td>
    </tr>
    <tr>
        <td> <p style="margin-bottom:-5px"><font color="#FF7F0E">Has Focus</font></p></td>
        <td>Procedure.reasonCode</td>
        <td>Procedure.reasonCode</td>
    </tr>
</table>


#### References
NASHIP:
*  Procedure.partOf referenced Pocedure (if more than one RoleGroup is used)

MII:
*  Procedure.partOf referenced Pocedure (if more than one RoleGroup is used)

#### Names of profiles
NASHIP:
* KBV_PR_Base_Procedure

MII:
* SD MII Prozedur Procedure



## References

[1] Kassenärztliche Bundesvereinigung. KBV-Basis-Profile. Accessed November 29, 2023. https://simplifier.net/organization/kassenrztlichebundesvereinigungkbv

[2]	Medizininformatik Initiative. Der Kerndatensatz der Medizininformatik-Initiative. Accessed November 29, 2023. https://www.medizininformatik-initiative.de/de/der-kerndatensatz-der-medizininformatik-initiative