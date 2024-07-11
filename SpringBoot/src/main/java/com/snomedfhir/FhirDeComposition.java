package com.snomedfhir;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;

public class FhirDeComposition {

    public HashMap<ArrayList<String>, String> hashMapStructureMaps = new HashMap<>();
    public ArrayList<String> listNames = new ArrayList<>();
    PreprocessingPce preprocessingPce = new PreprocessingPce();



    public JSONObject init(String url, String version, String pce, String superConcept, String delta) throws JSONException {
        System.out.println("--De-composition--");

//        pce = "64572001 |Disease (disorder)| :" +
//                "{116676008 |Associated morphology (attribute)| = 442672001 |Swelling (morphologic abnormality)|," +
//                "370135005 |Pathological process (attribute)| = 472964009 |Allergic process (qualifier value)|," +
//                "363698007 |Finding site (attribute)| = 123851003 |Mouth region structure (body structure)|," +
//                "246075003 |Causative agent (attribute)| = 227512001 |Pistachio nut (substance)|}";
//
//        pce = "91934008 |Allergy to nut (finding)| :\n" +
//                "{719722006 |Has realization (attribute)| = 472964009 |Allergic process (qualifier value)|,\n" +
//                "246075003 |Causative agent (attribute)| = 256353000 |Hazelnut (substance)|,\n" +
//                "363698007 |Finding site (attribute)| = 123851003 |Mouth region structure (body structure)|,\n" +
//                "116676008 |Associated morphology (attribute)| = 442672001 |Swelling (morphologic abnormality)|}";
//
//        delta = "{\"attribute\": [\"246075003 |Causative agent (attribute)|\", \"116676008 |Associated morphology (attribute)|\", \"363698007 |Finding site (attribute)|\"], \"value\": [\"227512001 |Pistachio nut (substance)|\", \"442672001 |Swelling (morphologic abnormality)|\", \"123851003 |Mouth region structure (body structure)|\"], \"id\": [\"0\", \"0\", \"0\"]}";
//        delta = "{\"attribute\": [\"116676008 |Associated morphology (attribute)|\", \"363698007 |Finding site (attribute)|\"], \"value\": [\"442672001 |Swelling (morphologic abnormality)|\", \"123851003 |Mouth region structure (body structure)|\"], \"id\": [\"0\", \"0\"]}";
////
//        pce ="419452009 |Allergic reaction caused by food (disorder)| :\n" +
//                "{370135005 |Pathological process (attribute)| = 472964009 |Allergic process (qualifier value)|,\n" +
//                "246075003 |Causative agent (attribute)| = 227512001 |Pistachio nut (substance)|,\n" +
//                "116676008 |Associated morphology (attribute)| = 442672001 |Swelling (morphologic abnormality)|,\n" +
//                "363698007 |Finding site (attribute)| = 123851003 |Mouth region structure (body structure)|}";
//
//        delta = "{\"attribute\":[\"246075003 |Causative agent (attribute)|\",\"363698007 |Finding site (attribute)|\",\"116676008 |Associated morphology (attribute)|\"],\"id\":[\"0\",\"0\",\"0\"],\"value\":[\"227512001 |Pistachio nut (substance)|\",\"123851003 |Mouth region structure (body structure)|\",\"442672001 |Swelling (morphologic abnormality)|\"]}";
//        superConcept = "15920521000119105";
//
//        versionFhirPath = "light";
////
////        superConcept =  "48821000119104";
//        url = "https://ontoserver.imi.uni-luebeck.de/fhir";
//        version = "20230430";

//        pce = "781474001 |Allergic disorder|: { 370135005 |Pathological process| = 472964009 |Allergic process|, 246075003 |Causative agent| = 227512001 |Pistachio nut|, 116676008 | Associated morphology| = 442672001 |Swelling|, 363698007 |Finding site| = 123851003 |Mouth region structure|}";
//
//        delta = "{\"attribute\":[\"116676008 |Associated morphology (attribute)|\",\"363698007 |Finding site (attribute)|\",\"246075003 |Causative agent (attribute)|\"],\"id\":[\"0\",\"0\",\"0\"],\"value\":[\"442672001 |Swelling (morphologic abnormality)|\",\"123851003 |Mouth region structure (body structure)|\",\"227512001 |Pistachio nut (substance)|\"]}";
//        superConcept =  "420373003";
//        url = "https://ontoserver.imi.uni-luebeck.de/fhir";
//        version = "20220930";

//        pce = "71388002 |Procedure (procedure)| :\n" +
//                "{260686004 |Method (attribute)| = 129314006 |Biopsy - action (qualifier value)|,\n" +
//                "363704007 |Procedure site (attribute)| = 764554000 |Bone structure of distal left femur (body structure)|},\n" +
//                "{260686004 |Method (attribute)| = 312251004 |Computed tomography imaging - action (qualifier value)|,\n" +
//                "363704007 |Procedure site (attribute)| = 71341001 |Bone structure of femur (body structure)|,\n" +
//                "363703001 |Has intent (attribute)| = 429892002 |Guidance intent (qualifier value)|}";
//
//        delta = "{\"attribute\":[\"260686004 |Method (attribute)|\",\"363704007 |Procedure site (attribute)|\",\"260686004 |Method (attribute)|\",\"363704007 |Procedure site (attribute)|\",\"363703001 |Has intent (attribute)|\"],\"id\":[\"0\",\"0\",\"1\",\"1\",\"1\"],\"value\":[\"129314006 |Biopsy - action (qualifier value)|\",\"764554000 |Bone structure of distal left femur (body structure)|\",\"312251004 |Computed tomography imaging - action (qualifier value)|\",\"71341001 |Bone structure of femur (body structure)|\",\"429892002 |Guidance intent (qualifier value)|\"]}";
//
//        superConcept = "128927009";
//
//        version = "20230430";
//        url = "https://ontoserver.imi.uni-luebeck.de/fhir";

        hashMapStructureMaps = initializeHashMap();
        JSONObject jsonStructureMap = getStructureMap(url, version, superConcept);
        HashMap<String, ArrayList<String>> hashMapElements = getElementsStructureMap(jsonStructureMap, new ArrayList<>());
        Boolean b = checkProcedure(url, version, superConcept);
        if(b) {
            b = checkRoleGroupProcedure(pce);
        }
        if(b) { // special case for procedures with more rg and method-attribute
            JSONObject jsonObject = getElementFhirPathProcedure(url, version, superConcept, pce, delta, hashMapElements);
//            System.out.println(jsonObject);

            return jsonObject;
        }


        System.out.println(jsonStructureMap);
        JSONObject jsonObject = getElementFhirPath(url, superConcept, pce, delta, hashMapElements, new ArrayList<>());
        System.out.println(jsonObject);




        return jsonObject;
//        return new JSONObject();
    }

    private HashMap<ArrayList<String>, String> initializeHashMap() {
        HashMap<ArrayList<String>, String> hashMapStructureMaps = new HashMap<>();

        ArrayList<String> arrayListAllergy = new ArrayList<>();
        arrayListAllergy.add("KBV-MIO-AllergyIntolerance");
        arrayListAllergy.add("KDS-MII-AllergyIntolerance");
        hashMapStructureMaps.put(arrayListAllergy, "<<609328004");             // Allergic disposition

        ArrayList<String> arrayListAllergicDisease = new ArrayList<>();
        arrayListAllergicDisease.add("KBV-MIO-AllergicDisease");
        arrayListAllergicDisease.add("KDS-MII-AllergicDisease");
        hashMapStructureMaps.put(arrayListAllergicDisease, "<<781474001");             // Allergic disease

        ArrayList<String> arrayListAllergicReaction = new ArrayList<>();
        arrayListAllergicReaction.add("KBV-MIO-AllergicReaction");
        arrayListAllergicReaction.add("KDS-MII-AllergicReaction");
        hashMapStructureMaps.put(arrayListAllergicReaction, "<<419076005");             // Allergic reaction

        ArrayList<String> arrayListProcedure = new ArrayList<>();
        arrayListProcedure.add("KBV-MIO-Procedure");
        arrayListProcedure.add("KDS-MII-Procedure");
        hashMapStructureMaps.put(arrayListProcedure, "<<71388002");                     // Procedure

//        ArrayList<String> arrayListClinicalFinding = new ArrayList<>();
//        arrayListClinicalFinding.add("KBV-Mio-ClinicalFinding");
////        arrayListClinicalFinding.add("KDS-MII-Procedure");
//        hashMapStructureMaps.put(arrayListClinicalFinding, "<<404684003 MINUS <<473011001");                     // Procedure
        return hashMapStructureMaps;
    }

    private JSONObject getElementFhirPath(String url, String superConcept, String pce, String delta, HashMap<String, ArrayList<String>> hashMapElements, ArrayList<String> specialList) throws JSONException {
        JSONObject jsonObjectPce = preprocessingPce.init(pce, new ArrayList<>());
        JSONObject jsonObjectDelta = new JSONObject(delta);

        String result = "{\"result\":[" +
                getFhirPathFull(url, superConcept, jsonObjectPce, hashMapElements, specialList) + "," +                       // delta ignored, use all attributes
                getFhirPathLight(url, superConcept, jsonObjectPce, jsonObjectDelta, hashMapElements, specialList)  +         // uses delta and necessary fhir elements
                "]}";
        return new JSONObject(result);
    }

    private String getFhirPathLight(String url, String superConcept, JSONObject jsonObjectPce, JSONObject jsonObjectDelta, HashMap<String, ArrayList<String>> hashMapElements, ArrayList<String> specialList) throws JSONException {
        ArrayList<ArrayList<String>> listSummaryValue = new ArrayList<>();
        ArrayList<String> listCode = new ArrayList<>();
        ArrayList<ArrayList<ArrayList<String>>> listSummaryFhirPath = new ArrayList<>();
        ArrayList<ArrayList<ArrayList<Boolean>>> listSummaryFhirPrio = new ArrayList<>();
        ArrayList<String> listSummaryName = new ArrayList<>();
        ArrayList<Boolean> listSummaryFhirSuperconceptPrio = new ArrayList<>();
        ArrayList<String> superConceptList = new ArrayList<>();
        ArrayList<String> superConceptPathList = new ArrayList<>();
        ArrayList<ArrayList<String>> referencesList = new ArrayList<>();
        ArrayList<ArrayList<String>> namesList = new ArrayList<>();

        for (String name: listNames) {
            if(name.contains("KBV")) {
                listSummaryName.add("\"KBV-Basisprofile\"");
            } else if(name.contains("KDS")) {
                listSummaryName.add("\"KDS der MII\"");
            }

            // all attributes of delta
            JSONArray attributeArray = jsonObjectDelta.getJSONArray("attribute");
            JSONArray attributeValue = jsonObjectDelta.getJSONArray("value");
            JSONArray idRoleGroup = jsonObjectDelta.getJSONArray("id");

            int max = -1;
            for (int i = 0; i < idRoleGroup.length(); i++) {
                int v = idRoleGroup.getInt(i);
                if(v > max) {
                    max = v;
                }
            }

            listSummaryValue.add(new ArrayList<>());
            listSummaryFhirPath.add(new ArrayList<>());
            listSummaryFhirPrio.add(new ArrayList<>());
            for (int i = 0; i < attributeArray.length(); i++) {
                ArrayList<String> a = new ArrayList<>();
                ArrayList<Boolean> pathPrio = new ArrayList<>();
                String attributeCode = attributeArray.getString(i).split(" \\|")[0];
                String value = attributeValue.getString(i);

                ArrayList<String> fhirPathList = hashMapElements.get(name + "@" + attributeCode);
                for (int j = 0; j < fhirPathList.size(); j++) {
                    String s = "";
                    if(max > 0) {
                        s = " (Part" + (idRoleGroup.getInt(i)+1) + ")";
                    }
                    a.add("\"" + fhirPathList.get(j).split("@")[0] + s + "\"");
                    if(fhirPathList.get(j).contains("true")) {
                        pathPrio.add(Boolean.valueOf(fhirPathList.get(j).split("@")[2]));
                    } else {
                        pathPrio.add(false);
                    }
                }

                listSummaryValue.get(listSummaryValue.size()-1).add("\"" + value + "\"");
                listSummaryFhirPath.get(listSummaryFhirPath.size()-1).add(a);
                listSummaryFhirPrio.get(listSummaryFhirPrio.size()-1).add(pathPrio);
                listCode.add(attributeCode);
            }


            // other attributes with "always"
            for (String key : hashMapElements.keySet()) {
                ArrayList<String> fhirPathListHelper = hashMapElements.get(key);
                for (int i = 0; i < fhirPathListHelper.size(); i++) {
                    if(fhirPathListHelper.get(i).substring(fhirPathListHelper.get(i).indexOf("@")).length() > 1) {
                        String path = fhirPathListHelper.get(i).split("@")[0];
                        String idx = "";
                        if(fhirPathListHelper.get(i).split("@").length > 1) {
                            idx = fhirPathListHelper.get(i).split("@")[1];
                        }

                        if(idx.equals("always")) {
                            if(!listCode.contains(key.split("@")[1])) {

                                if(jsonObjectPce.has("withoutRoleGroup")) {
                                    JSONArray without = jsonObjectPce.getJSONArray("withoutRoleGroup");
                                    for(int k = 0; k < without.length(); k++) {
                                        JSONObject j = without.getJSONObject(k);
                                        String valueCode = j.getString("valuecode");
                                        String attributeCode = j.getString("attributecode");
                                        if(attributeCode.equals(key.split("@")[1])) {
                                            String nameCode = "";

                                            ArrayList<String> pathList = new ArrayList<>();
                                            ArrayList<Boolean> pathPrio = new ArrayList<>();

                                            String fsn = "";
                                            if (!valueCode.contains("#")) {
                                                nameCode = OntoserverRequest.lookUpNameFsn(url, valueCode);
                                                fsn = valueCode + " |" + nameCode + "|";
                                            } else {
                                                fsn = "";
                                            }
                                            listSummaryValue.get(listSummaryValue.size()-1).add("\"" + fsn + "\"");
                                            if(hashMapElements.get(name + "@" + attributeCode) != null) {
                                                for (int l = 0; l < hashMapElements.get(name + "@" + attributeCode).size(); l++) {
                                                    pathList.add("\"" + hashMapElements.get(name + "@" + attributeCode).get(l).split("@")[0] + "\"");
                                                    if(hashMapElements.get(name + "@" + attributeCode).get(l).contains("true")) {
                                                        pathPrio.add(Boolean.valueOf(hashMapElements.get(name + "@" + attributeCode).get(l).split("@")[2]));
                                                    } else {
                                                        pathPrio.add(false);
                                                    }
                                                }
                                            } else {
                                                pathList.add("\"" + "\"");
                                                pathPrio.add(false);
                                            }
                                            listSummaryFhirPath.get(listSummaryFhirPath.size()-1).add(pathList);
                                            listSummaryFhirPrio.get(listSummaryFhirPrio.size()-1).add(pathPrio);
                                        }

                                    }
                                }

                                if(jsonObjectPce.has("withRoleGroup")) {
                                    JSONArray with = jsonObjectPce.getJSONArray("withRoleGroup");
                                    for(int l = 0; l < with.length(); l++) {
                                        JSONObject jo = with.getJSONObject(l);
                                        JSONArray rg = jo.getJSONArray("roleGroup");
                                        for(int j = 0; j < rg.length(); j++) {
                                            jo = rg.getJSONObject(j);
                                            String valueCode = jo.getString("valuecode");
                                            String attributeCode = jo.getString("attributecode");
                                            if(attributeCode.equals(key.split("@")[1])) {
                                                String nameCode = "";
                                                ArrayList<String> pathList = new ArrayList<>();
                                                ArrayList<Boolean> pathPrio = new ArrayList<>();

                                                String fsn = "";
                                                if (!valueCode.contains("#")) {
                                                    nameCode = OntoserverRequest.lookUpNameFsn(url, valueCode);
                                                    fsn = valueCode + " |" + nameCode + "|";
                                                } else {
                                                    fsn = "";
                                                }
                                                listSummaryValue.get(listSummaryValue.size()-1).add("\"" + fsn + "\"");
                                                if(hashMapElements.get(name + "@" + attributeCode) != null) {
                                                    for (int x = 0; x < hashMapElements.get(name + "@" + attributeCode).size(); x++) {
                                                        String s = "";
                                                        if(with.length() > 1) {
                                                            s = " (Part" + (i+1) + ")";
                                                        }
//                                                        pathList.get(pathList.size()-1).add("\"" + hashMapElements.get(name + "@" + attributeCode).get(k).split("@")[0] + s + "\"");    // " (Part " + i + )
//

                                                        pathList.add("\"" + hashMapElements.get(name + "@" + attributeCode).get(x).split("@")[0] + s + "\"");
                                                        if(hashMapElements.get(name + "@" + attributeCode).get(x).contains("true")) {
                                                            pathPrio.add(Boolean.valueOf(hashMapElements.get(name + "@" + attributeCode).get(x).split("@")[2] ));
                                                        } else {
                                                            pathPrio.add(false);
                                                        }
                                                    }
                                                } else {
                                                    pathList.add("\"" + "\"");
                                                    pathPrio.add(false);
                                                }
                                                listSummaryFhirPath.get(listSummaryFhirPath.size()-1).add(pathList);
                                                listSummaryFhirPrio.get(listSummaryFhirPrio.size()-1).add(pathPrio);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // super concept
            String superConceptSummary = superConcept + " |" + OntoserverRequest.lookUpNameFsn(url, superConcept) + "|";
            superConceptList.add("\"" + superConceptSummary + "\"");
            for (int i = 0; i < hashMapElements.get(name + "@superconcept").size(); i++) {
                superConceptPathList.add("\"" + hashMapElements.get(name + "@superconcept").get(i).split("@")[0] + "\"");
            }
            for (int i = 0; i < specialList.size(); i++) {
                superConceptList.add("\"" + specialList.get(i) + "\"");
                superConceptPathList.add("\"" + superConceptPathList.get(0).replace("\"", "") + " (Part" + (i+1) + ")\"");     // superConceptPathList.get(superConceptPathList.size()-1) +
            }

            if(superConceptPathList.get(0).length() > 0) {
                listSummaryFhirSuperconceptPrio.add(true);
            } else {
                listSummaryFhirSuperconceptPrio.add(false);
            }

            // references
            referencesList.add(new ArrayList<>());
            for (int i = 0; i < hashMapElements.get(name + "@references").size(); i++) {
                if (!hashMapElements.get(name + "@references").get(i).equals(" @ ")) {
                    String refRes = hashMapElements.get(name + "@references").get(i).split("->")[1].split("@")[0];
                    String ref = hashMapElements.get(name + "@references").get(i);

                    for (int j = 0; j < listSummaryFhirPath.size(); j++) {
                        for (int k = 0; k < listSummaryFhirPath.get(j).size(); k++) {
                            for (int l = 0; l < listSummaryFhirPath.get(j).get(k).size(); l++) {
                                if (listSummaryFhirPath.get(j).get(k).get(l).contains(refRes) && !referencesList.get(referencesList.size() - 1).contains("\"" + ref + "\"")) {
                                    if(listSummaryFhirPath.get(j).get(k).get(l).contains("Procedure") && listSummaryFhirPath.get(j).get(k).get(l).contains("Part")) {
                                        if(listSummaryFhirPath.get(j).get(k).get(l).contains("Part")) {
                                            referencesList.get(referencesList.size() - 1).add("\"" + ref + "\"");
                                        }
                                    } else {
                                        referencesList.get(referencesList.size() - 1).add("\"" + ref + "\"");
                                    }

                                }
                            }
                        }
                    }
                }
            }

            // names of profiles
            namesList.add(new ArrayList<>());
            for (int i = 0; i < hashMapElements.get(name + "@namesprofiles").size(); i++) {
                String res = hashMapElements.get(name + "@namesprofiles").get(i).split("->")[0];
                for (int j = 0; j < listSummaryFhirPath.size(); j++) {
                    for (int k = 0; k < listSummaryFhirPath.get(j).size(); k++) {
                        for (int l = 0; l < listSummaryFhirPath.get(j).get(k).size(); l++) {
                            if (listSummaryFhirPath.get(j).get(k).get(l).contains(res) && !namesList.get(namesList.size()-1).contains("\"" + hashMapElements.get(name + "@namesprofiles").get(i) + "\"")) {
                                namesList.get(namesList.size()-1).add("\"" + hashMapElements.get(name + "@namesprofiles").get(i) + "\"");
                            }
                        }
                    }
                }
            }
        }
//        System.out.println("-------------");
//        System.out.println(listSummaryFhirPrio);
//        System.out.println(listSummaryValue);
//        System.out.println(listSummaryFhirPath);
//        System.out.println(listSummaryName);
//        System.out.println(superConceptList);
//        System.out.println(superConceptPathList);
//        System.out.println(referencesList);
//        System.out.println(namesList);

        String result =
                "{\"superConceptListLight\":" + superConceptList + "}," +
                "{\"superConceptPathListLight\":" + superConceptPathList + "}," +
                "{\"listSummaryValueLight\":" + listSummaryValue + "}," +
                "{\"listSummaryFhirPathLight\":" + listSummaryFhirPath + "}," +
                "{\"listSummaryNameLight\":" + listSummaryName + "}," +
                "{\"referencesListLight\":" + referencesList + "}," +
                "{\"namesListLight\":" + namesList + "}," +
                "{\"listSummaryFhirPrio\":" + listSummaryFhirPrio + "}," +
                "{\"listSummaryFhirSuperconceptPrio\":" + listSummaryFhirSuperconceptPrio + "}";

        return result;
    }

    private String getFhirPathFull(String url, String superConcept, JSONObject jsonObjectPce, HashMap<String, ArrayList<String>> hashMapElements, ArrayList<String> specialList) throws JSONException {
        ArrayList<ArrayList<String>> listSummaryValue = new ArrayList<>();
        ArrayList<ArrayList<ArrayList<String>>> listSummaryFhirPath = new ArrayList<>();
        ArrayList<ArrayList<ArrayList<Boolean>>> listSummaryFhirPrio = new ArrayList<>();
        ArrayList<String> listSummaryName = new ArrayList<>();
        ArrayList<Boolean> listSummaryFhirSuperconceptPrio = new ArrayList<>();
        ArrayList<String> superConceptList = new ArrayList<>();
        ArrayList<String> superConceptPathList = new ArrayList<>();
        ArrayList<ArrayList<String>> referencesList = new ArrayList<>();
        ArrayList<ArrayList<String>> namesList = new ArrayList<>();

        for (String name: listNames) {
            if (jsonObjectPce.has("withoutRoleGroup")) {
                JSONArray without = jsonObjectPce.getJSONArray("withoutRoleGroup");
                ArrayList<ArrayList<String>> pathList = new ArrayList<>();
                ArrayList<ArrayList<Boolean>> pathPrio = new ArrayList<>();
                ArrayList<String> valueList = new ArrayList<>();
                for (int i = 0; i < without.length(); i++) {
                    pathList.add(new ArrayList<>());
                    pathPrio.add(new ArrayList<>());
                    JSONObject j = without.getJSONObject(i);
                    String valueCode = j.getString("valuecode");
                    String attributeCode = j.getString("attributecode");
                    String nameCode = "";
                    if (!valueCode.contains("#")) {
                        nameCode = OntoserverRequest.lookUpNameFsn(url, valueCode);
                        valueList.add("\"" + valueCode + " |" + nameCode + "|\"");
                    } else {
                        valueList.add("\"" + valueCode + " |" + nameCode + "|\"");
                    }
                    if (hashMapElements.get(name + "@" + attributeCode) != null) {
                        for (int k = 0; k < hashMapElements.get(name + "@" + attributeCode).size(); k++) {
                            pathList.get(pathList.size() - 1).add("\"" + hashMapElements.get(name + "@" + attributeCode).get(k).split("@")[0] + "\"");
                            if(hashMapElements.get(name + "@" + attributeCode).get(k).contains("true")) {
                                pathPrio.get(pathPrio.size() - 1).add(Boolean.valueOf(hashMapElements.get(name + "@" + attributeCode).get(k).split("@")[2]));
                            } else {
                                pathPrio.get(pathPrio.size() - 1).add(false);
                            }
                        }
                    } else {
                        pathList.get(pathList.size() - 1).add("\"" + "\"");
                        pathPrio.get(pathPrio.size() - 1).add(false);

                    }
                }
                if(pathList.size() > 0) {
                    listSummaryValue.add(valueList);
                    listSummaryFhirPath.add(pathList);
                    listSummaryFhirPrio.add(pathPrio);
                }
            }
            if(name.contains("KBV")) {
                listSummaryName.add("\"KBV-Basisprofile\"");
            } else if(name.contains("KDS")) {
                listSummaryName.add("\"KDS der MII\"");
            }

            if (jsonObjectPce.has("withRoleGroup")) {
                JSONArray with = jsonObjectPce.getJSONArray("withRoleGroup");
                ArrayList<String> valueList = new ArrayList<>();
                ArrayList<ArrayList<String>> pathList = new ArrayList<>();
                ArrayList<ArrayList<Boolean>> pathPrio = new ArrayList<>();
                for (int i = 0; i < with.length(); i++) {
                    JSONObject jo = with.getJSONObject(i);
                    JSONArray rg = jo.getJSONArray("roleGroup");

                    for (int j = 0; j < rg.length(); j++) {
                        pathList.add(new ArrayList<>());
                        pathPrio.add(new ArrayList<>());
                        jo = rg.getJSONObject(j);
                        String valueCode = jo.getString("valuecode");
                        String attributeCode = jo.getString("attributecode");
                        String nameCode = "";
                        if (!valueCode.contains("#")) {
                            nameCode = OntoserverRequest.lookUpNameFsn(url, valueCode);
                            valueList.add("\"" + valueCode + " |" + nameCode + "|\"");
                        } else {
                            valueList.add("\"" + valueCode + " |" + nameCode + "|\"");
                        }
                        if (hashMapElements.get(name + "@" + attributeCode) != null) {
                            for (int k = 0; k < hashMapElements.get(name + "@" + attributeCode).size(); k++) {
                                String s = "";
                                if(with.length() > 1) {
                                    s = " (Part" + (i+1) + ")";
                                }
                                pathList.get(pathList.size()-1).add("\"" + hashMapElements.get(name + "@" + attributeCode).get(k).split("@")[0] + s + "\"");
                                if(hashMapElements.get(name + "@" + attributeCode).get(k).contains("true")) {
                                    pathPrio.get(pathPrio.size() - 1).add(Boolean.valueOf(hashMapElements.get(name + "@" + attributeCode).get(k).split("@")[2]));
                                } else {
                                    pathPrio.get(pathPrio.size() - 1).add(false);
                                }
                            }
                        } else {
                            pathList.get(pathList.size()-1).add("\"" + "\"");
                            pathPrio.get(pathPrio.size()-1).add(false);
                        }
                    }
                }
                if(pathList.size() > 0) {
                    listSummaryFhirPath.add(pathList);
                    listSummaryFhirPrio.add(pathPrio);
                }
                if(valueList.size() > 0) {
                    listSummaryValue.add(valueList);
                }
            }

            // super concept
            String superConceptSummary = superConcept + " |" + OntoserverRequest.lookUpNameFsn(url, superConcept) + "|";
            superConceptList.add("\"" + superConceptSummary + "\"");
            for (int i = 0; i < hashMapElements.get(name + "@superconcept").size(); i++) {
                superConceptPathList.add("\"" + hashMapElements.get(name + "@superconcept").get(i).split("@")[0] + "\"");
            }
            for (int i = 0; i < specialList.size(); i++) {
                superConceptList.add("\"" + specialList.get(i) + "\"");
                superConceptPathList.add("\"" + superConceptPathList.get(0).replace("\"", "") + " (Part " + (i+1) + ")\"");     // superConceptPathList.get(superConceptPathList.size()-1) +
            }


            if(superConceptPathList.get(0).length() > 0) {
                listSummaryFhirSuperconceptPrio.add(true);
            } else {
                listSummaryFhirSuperconceptPrio.add(false);
            }

            // references
            referencesList.add(new ArrayList<>());
            for (int i = 0; i < hashMapElements.get(name + "@references").size(); i++) {
                if (!hashMapElements.get(name + "@references").get(i).equals(" @ ")) {
                    String refRes = hashMapElements.get(name + "@references").get(i).split("->")[1].split("@")[0];
                    String ref = hashMapElements.get(name + "@references").get(i);

                    for (int j = 0; j < listSummaryFhirPath.size(); j++) {
                        for (int k = 0; k < listSummaryFhirPath.get(j).size(); k++) {
                            for (int l = 0; l < listSummaryFhirPath.get(j).get(k).size(); l++) {
                                if (listSummaryFhirPath.get(j).get(k).get(l).contains(refRes) && !referencesList.get(referencesList.size()-1).contains("\"" + ref + "\"")) {
                                    if(listSummaryFhirPath.get(j).get(k).get(l).contains("Procedure") && listSummaryFhirPath.get(j).get(k).get(l).contains("Part")) {
                                        if(listSummaryFhirPath.get(j).get(k).get(l).contains("Part")) {
                                            referencesList.get(referencesList.size() - 1).add("\"" + ref + "\"");
                                        }
                                    } else {
                                        referencesList.get(referencesList.size() - 1).add("\"" + ref + "\"");
                                    }
                                }
                            }
                        }
                    }
                }

            }

            // names of profiles
            namesList.add(new ArrayList<>());
            for (int i = 0; i < hashMapElements.get(name + "@namesprofiles").size(); i++) {
                String res = hashMapElements.get(name + "@namesprofiles").get(i).split("->")[0];
                for (int j = 0; j < listSummaryFhirPath.size(); j++) {
                    for (int k = 0; k < listSummaryFhirPath.get(j).size(); k++) {
                        for (int l = 0; l < listSummaryFhirPath.get(j).get(k).size(); l++) {
                            if (listSummaryFhirPath.get(j).get(k).get(l).contains(res) && !namesList.get(namesList.size()-1).contains("\"" + hashMapElements.get(name + "@namesprofiles").get(i) + "\"")) {
                                namesList.get(namesList.size()-1).add("\"" + hashMapElements.get(name + "@namesprofiles").get(i) + "\"");
                            }
                        }
                    }
                }
            }
        }

        String result =
            "{\"superConceptListFull\":" + superConceptList + "}," +
            "{\"superConceptPathListFull\":" + superConceptPathList + "}," +
            "{\"listSummaryValueFull\":" + listSummaryValue + "}," +
            "{\"listSummaryFhirPathFull\":" + listSummaryFhirPath + "}," +
            "{\"listSummaryNameFull\":" + listSummaryName + "}," +
            "{\"referencesListFull\":" + referencesList + "}," +
            "{\"namesListFull\":" + namesList + "}," +
            "{\"listSummaryFhirPrio\":" + listSummaryFhirPrio + "}," +
            "{\"listSummaryFhirSuperconceptPrio\":" + listSummaryFhirSuperconceptPrio + "}";


//        System.out.println("-------------");
//        System.out.println(listSummaryFhirPrio);
//        System.out.println(superConceptList);
        System.out.println(superConceptPathList);
//        System.out.println(referencesList);
//        System.out.println(namesList);
//        System.out.println(listSummaryName);
//        System.out.println(listSummaryValue);
//        System.out.println(listSummaryFhirPath);
//        System.out.println();
//        System.out.println(result);

        return result;
    }

    public HashMap<String, ArrayList<String>> getElementsStructureMap(JSONObject jsonStructureMap, ArrayList<String> arrayListX) throws JSONException {
        System.out.println(jsonStructureMap);
        HashMap<String, ArrayList<String>> hashMap = new HashMap<>();

        if (listNames.size() == 0) {
            listNames = arrayListX;
        }

        for (String name: listNames) {
            System.out.println("--- " + name + " ---");
            JSONObject jsonObjectName = jsonStructureMap.getJSONObject(name);
            JSONArray arrayGroup = jsonObjectName.getJSONArray("group");
            for (int i = 0; i < arrayGroup.length(); i++) {
                JSONObject json = arrayGroup.getJSONObject(i);
                JSONArray rule = json.getJSONArray("rule");
                for (int j = 0; j < rule.length(); j++) {
                    ArrayList<String> arrayList = new ArrayList<>();
                    json = rule.getJSONObject(j);
                    JSONArray source = json.getJSONArray("source");
                    JSONObject sourceJson = source.getJSONObject(0);
                    String elementSource = sourceJson.getString("element");

                    JSONArray target = json.getJSONArray("target");
                    for (int k = 0; k < target.length(); k++) {
                        JSONObject targetArray = target.getJSONObject(k);
                        if (targetArray.has("element")) {
                            String elementTarget = targetArray.getString("element");
                            String parameterValue1 = "";
                            String parameterValue2 = "";
                            if(targetArray.has("parameter")) {
                                JSONArray parameterArray = targetArray.getJSONArray("parameter");
                                JSONObject json1 = parameterArray.getJSONObject(0);
                                if(json1.has("valueString")) {
                                    parameterValue1 = json1.getString("valueString");
                                }
                                if(parameterArray.length() > 1) {
                                    JSONObject json2 = parameterArray.getJSONObject(1);
                                    parameterValue2 = String.valueOf(json2.getBoolean("valueBoolean"));
                                }
                            }
                            arrayList.add(elementTarget + "@" + parameterValue1 + "@" + parameterValue2);
                        } else {
                            arrayList.add(" " + "@" + " ");
                        }
                        hashMap.put(name + "@" + elementSource, arrayList);
                    }

                }
            }
        }
        return hashMap;
    }

    private JSONObject getStructureMap(String url, String version, String superConcept) throws JSONException {
        listNames = getStructureMapHelper(url, version, hashMapStructureMaps, superConcept);
        String json = "{";
        for (String name: listNames) {
            String urlHapi = "https://fhir.imi.uni-luebeck.de/fhir/StructureMap/" + name;
            try {
                HttpClient httpClient = HttpClient.newBuilder()
                        .version(HttpClient.Version.HTTP_2)
                        .connectTimeout(Duration.ofSeconds(10))
                        .build();
                HttpRequest request = HttpRequest.newBuilder()
                        .GET()
                        .uri(URI.create(urlHapi))
                        .build();
                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                json = json + "\"" + name + "\":" + response.body() + ",";
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if(json.contains(",")) {
            json = json.substring(0, json.lastIndexOf(","));
        }
        json = json + "}";
        return new JSONObject(json.replace("\n", ""));
    }


    private ArrayList<String> getStructureMapHelper(String url, String version, HashMap<ArrayList<String>, String> hashMapStructureMaps, String code) {
        for (ArrayList<String> key : hashMapStructureMaps.keySet()) {
            String ecl = hashMapStructureMaps.get(key);
            ArrayList<String> listEcl = OntoserverRequest.eclRequest(url, version, ecl, false);
            if(listEcl.contains(code)) {
                return key;
            }
        }
        return new ArrayList<>();
    }

    private String getStructureMapHelper(String url, String version, ArrayList<String> arrayList) {
        ArrayList<String> arrayListHelper = new ArrayList<>();
        if(arrayList.size() > 1) {
            System.out.println(arrayList);
            for(int i = 0; i < arrayList.size(); i++) {
                for(int j = 0; j < arrayList.size(); j++) {
                    String codeA = hashMapStructureMaps.get(arrayList.get(i)).split("\\|")[0].replace(" ", "").replace("<<", "");
                    String codeB = hashMapStructureMaps.get(arrayList.get(j)).split("\\|")[0].replace(" ", "").replace("<<", "");
                    String result = OntoserverRequest.getSubsumesRelation(url, version, codeA, codeB);
                    if(result.equals("subsumes") && !arrayListHelper.contains(arrayList.get(j))) {
                        arrayListHelper.add(arrayList.get(j));
                    }
                }
            }
            if(arrayListHelper.size() > 1) {
                getStructureMapHelper(url, version, arrayListHelper);
            } else {
                return arrayListHelper.get(0);
            }
        } else {
            return arrayList.get(0);
        }
        return "";
    }

    private Boolean checkProcedure(String url, String version, String superConcept) {
        String s = OntoserverRequest.getSubsumesRelation(url, version, "71388002", superConcept);
        return s.equals("subsumes") || s.equals("equivalent");
    }

    private Boolean checkRoleGroupProcedure(String pce) throws JSONException {
        PreprocessingPce preprocessingPce = new PreprocessingPce();
        String s = preprocessingPce.createJson(pce, new ArrayList<>());
        JSONObject jsonObject = new JSONObject(s);
        JSONArray jsonArray = jsonObject.getJSONArray("withRoleGroup");
        boolean b = false;
        if(jsonArray.length() > 1) {
            for (int i = 0; i < jsonArray.length(); i++) {
                // check, if all RG contains method
                System.out.println(jsonArray.get(i));
                b = jsonArray.get(i).toString().contains("\"attributecode\":\"260686004");
            }
        }
        return b;
    }

    private JSONObject getElementFhirPathProcedure(String url, String version, String superConcept, String pce, String delta, HashMap<String, ArrayList<String>> hashMapElements) throws JSONException {
        System.out.println("--- SPECIAL CASE ---");

        JSONObject jsonObject = new JSONObject(delta);
        JSONArray jsonArrayAttribute = jsonObject.getJSONArray("attribute");
        JSONArray jsonArrayRoleGroup = jsonObject.getJSONArray("id");
        JSONArray jsonArrayValue = jsonObject.getJSONArray("value");

        int id = -1;
        ArrayList<ArrayList<String>> arrayList = new ArrayList<>();
        for (int i = 0; i < jsonArrayRoleGroup.length(); i++) {
            if(id != Integer.parseInt((String) jsonArrayRoleGroup.get(i))) {
                id = Integer.parseInt((String) jsonArrayRoleGroup.get(i));
                arrayList.add(new ArrayList<>());
            }
            arrayList.get(id).add(jsonArrayAttribute.get(i) + "=" + jsonArrayValue.get(i));
        }
        ArrayList<String> result = new ArrayList<>();
        for (int i = 0; i < arrayList.size(); i++) {
            ArrayList<String> a = arrayList.get(i);
            for (int j = 0; j < a.size(); j++) {
                String[] s = a.get(j).split(",");
                for (String attribute: s) {
                    if(attribute.contains("260686004")) {    // Method
                        String ecl =    "(< 71388002 |Procedure (procedure)| :\n" +
                                        attribute + ") MINUS < (< 71388002 |Procedure (procedure)| :\n" +
                                        attribute + ")";
                        String code = OntoserverRequest.eclRequest(url, version, ecl, false).get(0);
                        String display = OntoserverRequest.lookUpNameFsn(url, code);
                        result.add(code + " |" + display + "|");
                    }
                }
            }
        }
        JSONObject jsonObjectFhir = getElementFhirPath(url, superConcept, pce, delta, hashMapElements, result);

        // super concepts --> normal + of the role groups

//        System.out.println(jsonObjectFhir);

        return jsonObjectFhir;
    }


}
