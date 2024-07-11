package com.snomedfhir.api;

import com.snomedfhir.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
public class Controller {

    // ------- PCE START -------

    @CrossOrigin
    @GetMapping(value = "/validatepce")
    public String validatePce(@RequestParam String url, @RequestParam String pce) {
        OntoserverRequest or = new OntoserverRequest();
        return or.validatePce(pce, url);
    }


    // ------- LOAD PCE FROM CODE SYSTEM SUPPLEMENT -------

    @CrossOrigin
    @GetMapping(value = "/getallcodesystemsupplement")
    public String getAllCodeSystemSupplement(@RequestParam String url) throws JSONException, IOException {
        JSONObject jsonObject = CodeSystemSupplement.getCsSupplements(url);
        return jsonObject.toString();
    }

    @CrossOrigin
    @GetMapping(value = "/getcodesystemsupplement")
    public String getCodeSystemSupplement(@RequestParam String url) throws JSONException, IOException {
        JSONArray jsonArray = OntoserverRequest.getCodeSystemSupplement(url);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("result", jsonArray);
        return jsonObject.toString();
    }

    // ------- SUPER CONCEPT DELTA -------

    @CrossOrigin
    @GetMapping(value = "/preprocessingpce")
    public String preprocessingPce(@RequestParam String url, @RequestParam String pce) throws JSONException {
        PreprocessingPce preprocessingPce = new PreprocessingPce();
        JSONObject jsonObject = preprocessingPce.preprocessingHighlightingPCE(url, pce);
        return jsonObject.toString();
    }

    @CrossOrigin
    @GetMapping(value = "/superconceptcandidates")
    public String superConceptCandiates(@RequestParam String url, @RequestParam String pce) throws JSONException, OWLOntologyCreationException, IOException {
        SnomedOwl snomedOwl = new SnomedOwl();
        JSONObject jsonObject = snomedOwl.init(pce, url);
        return jsonObject.toString();
    }

    @CrossOrigin
    @GetMapping(value = "/superconcept")
    public String superConcept(@RequestParam String url, @RequestParam String pce, @RequestParam String[] list1, @RequestParam String[] list2) throws JSONException, OWLOntologyCreationException, IOException {
        SnomedOwl snomedOwl = new SnomedOwl();
//        JSONObject jsonObject = snomedOwl.getSuperConcept(list1, list2, pce, "LeacockChodorow", url);
////        JSONObject jsonObject = snomedOwl.getSuperConcept(list, pce, "JiangConrathDissimilarity", url);
        JSONObject jsonObject = snomedOwl.getSuperConcept(list1, list2, pce, "BatetSanchezValls", url);
////        JSONObject jsonObject = snomedOwl.getSuperConcept(list, pce, "Lin", url);
        return jsonObject.toString();

    }

    @CrossOrigin
    @GetMapping(value = "/delta")
    public String delta(@RequestParam String url, @RequestParam String pce, @RequestParam String superconcept) throws JSONException, OWLOntologyCreationException, IOException {
        DeltaCalculation deltaCalculation = new DeltaCalculation();
        JSONObject jsonObject = deltaCalculation.init(pce, superconcept, "BatetSanchezValls", url);
        System.out.println(jsonObject.toString());
        return jsonObject.toString();
    }


    // ------- MAPPING TO ELEMENTS OF FHIR RESOURCES -------

    @CrossOrigin
    @GetMapping(value = "/tofhir")
    public String toFhir(@RequestParam String url, @RequestParam String version, @RequestParam String pce, @RequestParam String superconcept, @RequestParam String delta, @RequestParam String fhirpathversion) throws JSONException, OWLOntologyCreationException, IOException {
        FhirDeComposition fhirDeComposition = new FhirDeComposition();
        JSONObject jsonObject = fhirDeComposition.init(url, version, pce, superconcept, delta);
        return jsonObject.toString();
    }






    // ------- SETTINGS -------
    @CrossOrigin
    @GetMapping(value = "/settingsurl")
    public String testUrl(@RequestParam String url) throws JSONException, IOException {
        Settings settings = new Settings();
        JSONObject jsonObject = settings.init(url);
        return jsonObject.toString();
    }

    @CrossOrigin
    @GetMapping(value = "/settingssnomedversions")
    public String getSnomedVersions(@RequestParam String url) throws JSONException, IOException {
        Settings settings = new Settings();
        JSONObject jsonObject = settings.getSnomedVersions(url);
        return jsonObject.toString();
    }

    @CrossOrigin
    @GetMapping(value = "/settingsserver")
    public String getServer() throws JSONException, IOException {
        Settings settings = new Settings();
        JSONArray jsonArray = settings.getServer();
        return jsonArray.toString();
    }

}
