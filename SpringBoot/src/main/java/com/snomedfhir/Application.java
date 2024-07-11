package com.snomedfhir;

import org.json.JSONException;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

import java.io.*;
import java.util.concurrent.TimeUnit;

@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
public class Application {
    public static void main(String[] args) throws JSONException, IOException, OWLOntologyCreationException, InterruptedException {
        SpringApplication.run(Application.class, args);
//
//        FhirDeComposition fhirDeComposition = new FhirDeComposition();
//        fhirDeComposition.init("", "", "", "", "");

//        FhirStructureMapClinicalFinding fhirStructureMapClinicalFinding = new FhirStructureMapClinicalFinding();
//        fhirStructureMapClinicalFinding.init();


//        FhirStructureMapAllergy fhirStructureMapAllergy = new FhirStructureMapAllergy();
//        fhirStructureMapAllergy.init();
//
//        FhirStructureMapAllergicDisease fhirStructureMapAllergicDisease = new FhirStructureMapAllergicDisease();
//        fhirStructureMapAllergicDisease.init();
//
//
//        FhirStructureMapAllergicReaction fhirStructureMapAllergicReaction = new FhirStructureMapAllergicReaction();
//        fhirStructureMapAllergicReaction.init();


//        FhirStructureMapProcedure fhirStructureMapProcedure = new FhirStructureMapProcedure();
//        fhirStructureMapProcedure.init();

//        FhirTest fhirTest = new FhirTest();
//        fhirTest.init();

//        String pce = "71388002 |Procedure (procedurea)| :\n" +
//                "{260686004 |Method (attribute)| = 129304002 |Excision - action (qualifier value)|,\n" +
//                "405813007 |Procedure site - Direct (attribute)| = 20837000 |Structure of right ovary (body structure)|,\n" +
//                "424226004 |Using device (attribute)| = 122456005 |Laser device (physical object)|},\n" +
//                "{260686004 |Method (attribute)| = 261519002 |Diathermy excision - action (qualifier value)|,\n" +
//                "405813007 |Procedure site - Direct (attribute)| = 113293009 |Structure of left fallopian tube (body structure)|}";
//
//        pce = "71388002:{260686004=129314006,363704007=764554000},{260686004=312251004,363704007=71341001,363703001=429892002}";
//
//        SnomedOwl snomedOwl = new SnomedOwl();
//        JSONObject jsonObject = snomedOwl.init(pce, snomedOwl.urlOntoserver);


//        String path = "/Users/tessa/Documents/Studium-Semester/Master-Arbeit/pce-to-fhir/SpringBoot/src/main/java/com/snomedfhir/Python/";
//        String file = "Similarity.py";
//
//        String value = Worker.runPythonScriptSimilarity(path, file);
//        System.out.println(value);

        // ------ TEST OWL  --------
//
//        String pce = "413586006 |Product containing precisely aspirin 250 milligram and caffeine 65 milligram and paracetamol 250 milligram/1 each conventional release oral tablet (clinical drug)|:763032000 |Has unit of presentation  (attribute)| = 732936001 |Tablet (unit of presentation)|,1142139005 |Count of base of active ingredient  (attribute)| = #3.0,411116001 |Has manufactured dose form  (attribute)| = 411116001 |Has manufactured dose form (attribute)|,{732943007 |Has basis of strength substance  (attribute)| = 255641001 |Caffeine (substance)|,732945000 |Has presentation strength numerator unit  (attribute)| = 258684004 |milligram (qualifier value)|,732947008 |Has presentation strength denominator unit  (attribute)| = 732936001 |Tablet (unit of presentation)|,1142135004 |Has presentation strength numerator value  (attribute)| = #65.0,1142136003 |Has presentation strength denominator value  (attribute)| = #1.0,762949000 |Has precise active ingredient  (attribute)| = 255641001 |Caffeine (substance)|},{732947008 |Has presentation strength denominator unit  (attribute)| = 732936001 |Tablet (unit of presentation)|,1142136003 |Has presentation strength denominator value  (attribute)| = #1.0,1142135004 |Has presentation strength numerator value  (attribute)| = #250.0,762949000 |Has precise active ingredient  (attribute)| = 387517004 |Paracetamol (substance)|,732943007 |Has basis of strength substance  (attribute)| = 387517004 |Paracetamol (substance)|,732945000 |Has presentation strength numerator unit  (attribute)| = 258684004 |milligram (qualifier value)|},{1142135004 |Has presentation strength numerator value  (attribute)| = #250.0,1142136003 |Has presentation strength denominator value  (attribute)| = #1.0,762949000 |Has precise active ingredient  (attribute)| = 387458008 |Aspirin (substance)|,732943007 |Has basis of strength substance  (attribute)| = 387458008 |Aspirin (substance)|,732945000 |Has presentation strength numerator unit  (attribute)| = 258684004 |milligram (qualifier value)|,732947008 |Has presentation strength denominator unit  (attribute)| = 732936001 |Tablet (unit of presentation)|}\",\n" +
//                "            \"display\": \"Product containing precisely aspirin  milligram and caffeine  milligram and paracetamol  milligram/ each conventional release oral tablet (clinical drug):Has unit of presentation  (attribute) = Tablet (unit of presentation),Count of base of active ingredient  (attribute) =  #3.0,Has manufactured dose form  (attribute) = Has manufactured dose form (attribute),{Has basis of strength substance  (attribute) = Caffeine (substance),Has presentation strength numerator unit  (attribute) = milligram (qualifier value),Has presentation strength denominator unit  (attribute) = Tablet (unit of presentation),Has presentation strength numerator value  (attribute) =  #65.0,Has presentation strength denominator value  (attribute) =  #1.0,Has precise active ingredient  (attribute) = Caffeine (substance)},{Has presentation strength denominator unit  (attribute) = Tablet (unit of presentation),Has presentation strength denominator value  (attribute) =  #1.0,Has presentation strength numerator value  (attribute) =  #250.0,Has precise active ingredient  (attribute) = Paracetamol (substance),Has basis of strength substance  (attribute) = Paracetamol (substance),Has presentation strength numerator unit  (attribute) = milligram (qualifier value)},{Has presentation strength numerator value  (attribute) =  #250.0,Has presentation strength denominator value  (attribute) =  #1.0,Has precise active ingredient  (attribute) = Aspirin (substance),Has basis of strength substance  (attribute) = Aspirin (substance),Has presentation strength numerator unit  (attribute) = milligram (qualifier value),Has presentation strength denominator unit  (attribute) = Tablet (unit of presentation)}\",\n" +
//                "            \"definition\": \"ConceptModel\"\n" +
//                "        },\n" +
//                "        {\n" +
//                "            \"code\": \"413350009 |Finding with explicit context (situation)|:{408731000 |Temporal context  (attribute)| = 410510008 |Temporal context value (qualifier value)|,408729009 |Finding context (attribute)| = 410517006 |Expectation context (qualifier value)|},{408729009 |Finding context  (attribute)| = 410514004 |Finding context value (qualifier value)|,408731000 |Temporal context (attribute)| = 410513005 |In the past (qualifier value)|}";
//
//        SnomedOwl snomedOwl = new SnomedOwl();
//        snomedOwl.init(pce, snomedOwl.urlOntoserver);


//        String pce = "125605004 |Fracture of bone (disorder)|:\n" +
//                "{116676008 |Associated morphology  (attribute)| = 52329006 |Fracture, open (morphologic abnormality)|,\n" +
//                "363698007 |Finding site  (attribute)| = 85050009 |Bone structure of humerus (body structure)|},\n" +
//                "{116676008 |Associated morphology (attribute)| = 20946005 |Fracture, closed (morphologic abnormality)|,\n" +
//                "363698007 |Finding site (attribute)| = 272673000 |Bone structure (body structure)|}";
//        String superConcept = "125605004 |Fracture of bone (disorder)|";
//        String url = "https://ontoserver.imi.uni-luebeck.de/fhir";
////
//        DeltaCalculation deltaCalculation = new DeltaCalculation();
//        deltaCalculation.init(pce, superConcept, "BatetSanchezValls", url);

//        python3 /Users/tessa/Documents/Studium-Semester/Master-Arbeit/pce-to-fhir/SpringBoot/src/main/java/com/snomedfhir/Python/Delta.py 15920521000119105

//        String path = "/Users/tessa/Documents/Studium-Semester/Master-Arbeit/pce-to-fhir/SpringBoot/src/main/java/com/snomedfhir/Python/";
//        String file = "Delta.py";
//        String s = Worker.runPythonScriptDelta(path, file, "15920521000119105");
//        System.out.println(s);

    }

}