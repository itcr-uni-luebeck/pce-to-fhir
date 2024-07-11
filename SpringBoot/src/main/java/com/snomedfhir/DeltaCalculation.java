package com.snomedfhir;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;


public class DeltaCalculation {

    public DeltaCalculation() {

    }

    OntoserverRequest ontoserverRequest = new OntoserverRequest();
    PreprocessingPce preprocessingPce = new PreprocessingPce();
    Worker worker = new Worker();
    String urlOntoserver = "https://ontoserver.imi.uni-luebeck.de/fhir";
    String sctVersion = "20220930";

    String path = "/Users/tessa/Documents/Studium-Semester/Master-Arbeit/pce-to-fhir/SpringBoot/src/main/java/com/snomedfhir/Python/";
    String file = "Delta.py";


    public JSONObject init(String pce, String superConcept, String measureName, String url) throws IOException, JSONException, OWLOntologyCreationException {
        pce = pce.replace("}{", "},{");
        JSONObject jsonPce = preprocessingPce.init(pce, new ArrayList<>());

        BufferedWriter writer = new BufferedWriter(new FileWriter("SpringBoot/src/main/java/com/snomedfhir/Python/similarity_calculation-pce-superconcept.txt"));
        writer.write(jsonPce + "@" + superConcept + "@" + measureName);
        writer.close();
        String s = Worker.runPythonScriptDelta(path, file, superConcept);
        System.out.println("--Delta--");
        System.out.println(s);
        String[] delta = s.split(", \\[\\[");
        String dWithout = delta[0];
        String dWith;
        if(s.contains(", [[")) {
            dWith = delta[1];
        } else {
            dWith = delta[0];
        }
        String[] deltaWithout = dWithout.split("],");
        String[] deltaWith = dWith.split("],");
        ArrayList<String> attributeDeltaList = new ArrayList<>();
        ArrayList<String> valueDeltaList = new ArrayList<>();
        ArrayList<String> rgIdList = new ArrayList<>();

        for(int i = 0; i < deltaWithout.length; i++) {
            if(deltaWithout[i].contains("=")) {
                String arNew = deltaWithout[i].replace("'", "").replace(" ", "").replace("[", "").replace("]", "");
                String attribute = arNew.split("=")[0];
                String value = arNew.split("=")[1];
                attributeDeltaList.add("\"" + attribute + " |" + OntoserverRequest.lookUpNameFsn(url, attribute) + "|" + "\"");
                if (!value.contains("#")) {
                    valueDeltaList.add("\"" + value + " |" + OntoserverRequest.lookUpNameFsn(url, value) + "|" + "\"");
                }
                rgIdList.add("\"" +  "\"");
                System.out.println("x" + " . " + attribute + " |" + OntoserverRequest.lookUpNameFsn(url, attribute) + "|" + " -- " + value + " |" + OntoserverRequest.lookUpNameFsn(url, value) + "|");
            }
        }
        System.out.println(dWith);
        for(int i = 0; i < deltaWith.length; i++) {
            String[]  attributrelation = deltaWith[i].split(",");

            for(String ar : attributrelation) {
                if (ar.contains("=")) {
                    String arNew = ar.replace("'", "").replace(" ", "").replace("[", "").replace("]", "");
                    System.out.println("--> " + arNew);
                    String attribute = arNew.split("=")[0];
                    String value = arNew.split("=")[1];
                    attributeDeltaList.add("\"" + attribute + " |" + OntoserverRequest.lookUpNameFsn(url, attribute) + "|" + "\"");
                    if (!value.contains("#")) {
                        valueDeltaList.add("\"" + value + " |" + OntoserverRequest.lookUpNameFsn(url, value) + "|" + "\"");
                    }
                    rgIdList.add("\"" + i + "\"");
                    System.out.println(i + " . " + attribute + " |" + OntoserverRequest.lookUpNameFsn(url, attribute) + "|" + " -- " + value + " |" + OntoserverRequest.lookUpNameFsn(url, value) + "|");
                }
            }
        }
        String result = "{\"attribute\": " + attributeDeltaList + ", \"value\": " + valueDeltaList + ", \"id\": " + rgIdList + "}";
        return new JSONObject(result);
    }

    public void init() throws JSONException {
        String superConcept = "413586006";
//        String superConcept = "125605004";

        ArrayList<ArrayList<String>> deltaList = new ArrayList<>();

        // look up to get JSON with attribute relations
        JSONArray jsonArray = OntoserverRequest.lookUpNameNormalForm(urlOntoserver, superConcept);
        // get all attribute relations of the super concept
        assert jsonArray != null;
        ArrayList<ArrayList<String>> attributesSuperConcepts = worker.getAttributeRelation(jsonArray);


        // determine and preprocessing concept model domain of super concept
        SnomedConceptModel snomedConceptModel = new SnomedConceptModel();
//        JSONObject conceptModelDomain = snomedConceptModel.getConceptModelDomain(urlOntoserver, sctVersion, superConcept);
//        System.out.println(conceptModelDomain);
        JSONObject conceptModelDomain = new JSONObject("{\"domainTemplateForPrecoordination\":[{\"init\":\"[[+id(<< 373873005 |Pharmaceutical / biologic product (product)|)]]: [[0..1]] 1142139005 |Count of base of active ingredient| = [[+int(>#0..)]], [[0..1]] 1142140007 |Count of active ingredient| = [[+int(>#0..)]], [[0..1]] 1142141006 |Count of base and modification pair| = [[+int(>#0..)]], [[0..1]] 1148793005 |Unit of presentation size quantity| = [[+dec(>#0..)]], [[0..1]] 1149367008 |Has target population| = [[+id(< 27821000087106 |Product target population (qualifier value)|)]], [[0..1]] 320091000221107 |Unit of presentation size unit| = [[+id(<< 767524001 |Unit of measure (qualifier value)|)]], [[0..1]] 411116001 |Has manufactured dose form| = [[+id(<< 736542009 |Pharmaceutical dose form (dose form)|)]], [[0..1]] 763032000 |Has unit of presentation| = [[+id(<< 732935002 |Unit of presentation (unit of presentation)|)]], [[0..*]] 766939001 |Plays role| = [[+id(<< 766940004 |Role (role)|)]], [[0..1]] 774158006 |Has product name| = [[+id(<< 774167006 |Product name (product name)|)]], [[0..1]] 774159003 |Has supplier| = [[+id(<< 774164004 |Supplier (supplier)|)]], [[0..*]] 860781008 |Has product characteristic| = [[+id(<< 362981000 |Qualifier value (qualifier value)|)]], [[0..*]] { [[0..1]] 1142135004 |Has presentation strength numerator value| = [[+dec(>#0..)]], [[0..1]] 1142136003 |Has presentation strength denominator value| = [[+dec(>#0..)]], [[0..1]] 1142137007 |Has concentration strength denominator value| = [[+dec(>#0..)]], [[0..1]] 1142138002 |Has concentration strength numerator value| = [[+dec(>#0..)]], [[0..1]] 1149366004 |Has ingredient qualitative strength| = [[+id(< 1149484003 |Ingredient qualitative strength (qualifier value)|)]], [[0..1]] 127489000 |Has active ingredient| = [[+id(<< 105590001 |Substance (substance)|)]], [[0..1]] 732943007 |Has basis of strength substance| = [[+id(< 105590001 |Substance (substance)|)]], [[0..1]] 732945000 |Has presentation strength numerator unit| = [[+id(< 767524001 |Unit of measure (qualifier value)|)]], [[0..1]] 732947008 |Has presentation strength denominator unit| = [[+id(< 767524001 |Unit of measure (qualifier value)|)]], [[0..1]] 733722007 |Has concentration strength denominator unit| = [[+id(< 767524001 |Unit of measure (qualifier value)|)]], [[0..1]] 733725009 |Has concentration strength numerator unit| = [[+id(< 767524001 |Unit of measure (qualifier value)|)]], [[0..1]] 762949000 |Has precise active ingredient| = [[+id(<< 105590001 |Substance (substance)|)]], [[0..*]] 860779006 |Has ingredient characteristic| = [[+id(<< 362981000 |Qualifier value (qualifier value)|)]] }\",\"focusconcept\":\"<<373873005\",\"withRoleGroups\":[{\"roleGroupCardinalityMax\":\"*\",\"roleGroupCardinalityMin\":\"0\",\"attribute\":[{\"constraint\":\"[[+dec(>#0..\",\"attributeCardinalityMax\":\"1\",\"attributeNameCode\":\"1142135004\",\"attributeCardinalityMin\":\"0\",\"attributeNameDisplay\":\"Has presentation strength numerator value\"},{\"constraint\":\"[[+dec(>#0..\",\"attributeCardinalityMax\":\"1\",\"attributeNameCode\":\"1142136003\",\"attributeCardinalityMin\":\"0\",\"attributeNameDisplay\":\"Has presentation strength denominator value\"},{\"constraint\":\"[[+dec(>#0..\",\"attributeCardinalityMax\":\"1\",\"attributeNameCode\":\"1142137007\",\"attributeCardinalityMin\":\"0\",\"attributeNameDisplay\":\"Has concentration strength denominator value\"},{\"constraint\":\"[[+dec(>#0..\",\"attributeCardinalityMax\":\"1\",\"attributeNameCode\":\"1142138002\",\"attributeCardinalityMin\":\"0\",\"attributeNameDisplay\":\"Has concentration strength numerator value\"},{\"constraint\":\"<1149484003\",\"attributeCardinalityMax\":\"1\",\"attributeNameCode\":\"1149366004\",\"attributeCardinalityMin\":\"0\",\"attributeNameDisplay\":\"Has ingredient qualitative strength\"},{\"constraint\":\"<<105590001\",\"attributeCardinalityMax\":\"1\",\"attributeNameCode\":\"127489000\",\"attributeCardinalityMin\":\"0\",\"attributeNameDisplay\":\"Has active ingredient\"},{\"constraint\":\"<105590001\",\"attributeCardinalityMax\":\"1\",\"attributeNameCode\":\"732943007\",\"attributeCardinalityMin\":\"0\",\"attributeNameDisplay\":\"Has basis of strength substance\"},{\"constraint\":\"<767524001\",\"attributeCardinalityMax\":\"1\",\"attributeNameCode\":\"732945000\",\"attributeCardinalityMin\":\"0\",\"attributeNameDisplay\":\"Has presentation strength numerator unit\"},{\"constraint\":\"<767524001\",\"attributeCardinalityMax\":\"1\",\"attributeNameCode\":\"732947008\",\"attributeCardinalityMin\":\"0\",\"attributeNameDisplay\":\"Has presentation strength denominator unit\"},{\"constraint\":\"<767524001\",\"attributeCardinalityMax\":\"1\",\"attributeNameCode\":\"733722007\",\"attributeCardinalityMin\":\"0\",\"attributeNameDisplay\":\"Has concentration strength denominator unit\"},{\"constraint\":\"<767524001\",\"attributeCardinalityMax\":\"1\",\"attributeNameCode\":\"733725009\",\"attributeCardinalityMin\":\"0\",\"attributeNameDisplay\":\"Has concentration strength numerator unit\"},{\"constraint\":\"<<105590001\",\"attributeCardinalityMax\":\"1\",\"attributeNameCode\":\"762949000\",\"attributeCardinalityMin\":\"0\",\"attributeNameDisplay\":\"Has precise active ingredient\"},{\"constraint\":\"<<362981000\",\"attributeCardinalityMax\":\"*\",\"attributeNameCode\":\"860779006\",\"attributeCardinalityMin\":\"0\",\"attributeNameDisplay\":\"Has ingredient characteristic\"}]}],\"withoutRoleGroups\":[{\"constraint\":\"int(>#0..)\",\"attributeCardinalityMax\":\"1\",\"attributeNameCode\":\"1142139005\",\"attributeCardinalityMin\":\"0\",\"attributeNameDisplay\":\"Count of base of active ingredient\"},{\"constraint\":\"int(>#0..)\",\"attributeCardinalityMax\":\"1\",\"attributeNameCode\":\"1142140007\",\"attributeCardinalityMin\":\"0\",\"attributeNameDisplay\":\"Count of active ingredient\"},{\"constraint\":\"int(>#0..)\",\"attributeCardinalityMax\":\"1\",\"attributeNameCode\":\"1142141006\",\"attributeCardinalityMin\":\"0\",\"attributeNameDisplay\":\"Count of base and modification pair\"},{\"constraint\":\"dec(>#0..)\",\"attributeCardinalityMax\":\"1\",\"attributeNameCode\":\"1148793005\",\"attributeCardinalityMin\":\"0\",\"attributeNameDisplay\":\"Unit of presentation size quantity\"},{\"constraint\":\"<27821000087106\",\"attributeCardinalityMax\":\"1\",\"attributeNameCode\":\"1149367008\",\"attributeCardinalityMin\":\"0\",\"attributeNameDisplay\":\"Has target population\"},{\"constraint\":\"<<767524001\",\"attributeCardinalityMax\":\"1\",\"attributeNameCode\":\"320091000221107\",\"attributeCardinalityMin\":\"0\",\"attributeNameDisplay\":\"Unit of presentation size unit\"},{\"constraint\":\"<<736542009\",\"attributeCardinalityMax\":\"1\",\"attributeNameCode\":\"411116001\",\"attributeCardinalityMin\":\"0\",\"attributeNameDisplay\":\"Has manufactured dose form\"},{\"constraint\":\"<<732935002\",\"attributeCardinalityMax\":\"1\",\"attributeNameCode\":\"763032000\",\"attributeCardinalityMin\":\"0\",\"attributeNameDisplay\":\"Has unit of presentation\"},{\"constraint\":\"<<766940004\",\"attributeCardinalityMax\":\"*\",\"attributeNameCode\":\"766939001\",\"attributeCardinalityMin\":\"0\",\"attributeNameDisplay\":\"Plays role\"},{\"constraint\":\"<<774167006\",\"attributeCardinalityMax\":\"1\",\"attributeNameCode\":\"774158006\",\"attributeCardinalityMin\":\"0\",\"attributeNameDisplay\":\"Has product name\"},{\"constraint\":\"<<774164004\",\"attributeCardinalityMax\":\"1\",\"attributeNameCode\":\"774159003\",\"attributeCardinalityMin\":\"0\",\"attributeNameDisplay\":\"Has supplier\"},{\"constraint\":\"<<362981000\",\"attributeCardinalityMax\":\"*\",\"attributeNameCode\":\"860781008\",\"attributeCardinalityMin\":\"0\",\"attributeNameDisplay\":\"Has product characteristic\"}]}],\"proximalPrimitiveConstraint\":\"<< 373873005 |Pharmaceutical / biologic product (product)|\",\"guideURL\":\"http://snomed.org/dom373873005\",\"domainConstraint\":\"<< 373873005 |Pharmaceutical / biologic product (product)|\",\"referencedComponentId\":\"373873005\",\"parentDomain\":\"\",\"proximalPrimitiveRefinement\":\"\"}\n");
//        JSONObject conceptModelDomain = new JSONObject("{\"domainTemplateForPrecoordination\":[{\"init\":\"[[+id(<< 64572001 |Disease (disorder)|)]]: [[0..*]] { [[0..1]] 255234002 |After| = [[+id(<< 404684003 |Clinical finding (finding)| OR << 71388002 |Procedure (procedure)|)]], [[0..*]] 47429007 |Associated with| = [[+id(<< 404684003 |Clinical finding (finding)| OR << 71388002 |Procedure (procedure)| OR << 272379006 |Event (event)| OR << 410607006 |Organism (organism)| OR << 105590001 |Substance (substance)| OR << 260787004 |Physical object (physical object)| OR << 78621006 |Physical force (physical force)|)]], [[0..1]] 288556008 |Before| = [[+id(<< 71388002 |Procedure (procedure)|)]], [[0..1]] 246075003 |Causative agent| = [[+id(<< 410607006 |Organism (organism)| OR << 105590001 |Substance (substance)| OR << 260787004 |Physical object (physical object)| OR << 78621006 |Physical force (physical force)|)]], [[0..1]] 263502005 |Clinical course| = [[+id(<< 288524001 |Courses (qualifier value)|)]], [[0..1]] 42752001 |Due to| = [[+id(<< 404684003 |Clinical finding (finding)| OR << 272379006 |Event (event)| OR << 71388002 |Procedure (procedure)|)]], [[0..1]] 371881003 |During| = [[+id(<< 71388002 |Procedure (procedure)|)]], [[0..1]] 246456000 |Episodicity| = [[+id(<< 288526004 |Episodicities (qualifier value)|)]], [[0..1]] 419066007 |Finding informer| = [[+id(<< 420158005 |Performer of method (person)| OR << 419358007 |Subject of record or other provider of history (person)| OR << 444018008 |Person with characteristic related to subject of record (person)|)]], [[0..1]] 418775008 |Finding method| = [[+id(<< 71388002 |Procedure (procedure)|)]],[[0..1]] 363713009 |Has interpretation| = [[+id(<< 260245000 |Finding values (qualifier value)| OR << 263714004 |Colors (qualifier value)|)]], [[0..1]] 363714003 |Interprets| = [[+id(<< 363787002 |Observable entity (observable entity)| OR << 108252007 |Laboratory procedure (procedure)| OR << 386053000 |Evaluation procedure (procedure)|)]], [[0..1]] 246454002 |Occurrence| = [[+id(<< 282032007 |Periods of life (qualifier value)|)]], [[0..1]] 370135005 |Pathological process| = [[+id(<< 769247005 |Abnormal immune process (qualifier value)| OR << 441862004 |Infectious process (qualifier value)| OR << 472963003 |Hypersensitivity process (qualifier value)| OR << 308490002 |Pathological developmental process (qualifier value)| )]], [[0..*]] 726633004 |Temporally related to| = [[+id(<< 404684003 |Clinical finding (finding)| OR << 71388002 |Procedure (procedure)|)]], [[0..1]] 246112005 |Severity| = [[+id(<< 272141005 |Severities (qualifier value)|)]], [[0..1]] 363698007 |Finding site| = [[+id(<< 442083009 |Anatomical or acquired body structure (body structure)|)]], [[0..1]] 116676008 |Associated morphology| = [[+id(<< 49755003 |Morphologically abnormal structure (morphologic abnormality)|)]] }\",\"focusconcept\":\"<<64572001\",\"withRoleGroups\":[{\"roleGroupCardinalityMax\":\"*\",\"roleGroupCardinalityMin\":\"0\",\"attribute\":[{\"constraint\":\"<<404684003OR<<71388002\",\"attributeCardinalityMax\":\"1\",\"attributeNameCode\":\"255234002\",\"attributeCardinalityMin\":\"0\",\"attributeNameDisplay\":\"After\"},{\"constraint\":\"<<404684003OR<<71388002OR<<272379006OR<<410607006OR<<105590001OR<<260787004OR<<78621006\",\"attributeCardinalityMax\":\"*\",\"attributeNameCode\":\"47429007\",\"attributeCardinalityMin\":\"0\",\"attributeNameDisplay\":\"Associated with\"},{\"constraint\":\"<<71388002\",\"attributeCardinalityMax\":\"1\",\"attributeNameCode\":\"288556008\",\"attributeCardinalityMin\":\"0\",\"attributeNameDisplay\":\"Before\"},{\"constraint\":\"<<410607006OR<<105590001OR<<260787004OR<<78621006\",\"attributeCardinalityMax\":\"1\",\"attributeNameCode\":\"246075003\",\"attributeCardinalityMin\":\"0\",\"attributeNameDisplay\":\"Causative agent\"},{\"constraint\":\"<<288524001\",\"attributeCardinalityMax\":\"1\",\"attributeNameCode\":\"263502005\",\"attributeCardinalityMin\":\"0\",\"attributeNameDisplay\":\"Clinical course\"},{\"constraint\":\"<<404684003OR<<272379006OR<<71388002\",\"attributeCardinalityMax\":\"1\",\"attributeNameCode\":\"42752001\",\"attributeCardinalityMin\":\"0\",\"attributeNameDisplay\":\"Due to\"},{\"constraint\":\"<<71388002\",\"attributeCardinalityMax\":\"1\",\"attributeNameCode\":\"371881003\",\"attributeCardinalityMin\":\"0\",\"attributeNameDisplay\":\"During\"},{\"constraint\":\"<<288526004\",\"attributeCardinalityMax\":\"1\",\"attributeNameCode\":\"246456000\",\"attributeCardinalityMin\":\"0\",\"attributeNameDisplay\":\"Episodicity\"},{\"constraint\":\"<<420158005OR<<419358007OR<<444018008\",\"attributeCardinalityMax\":\"1\",\"attributeNameCode\":\"419066007\",\"attributeCardinalityMin\":\"0\",\"attributeNameDisplay\":\"Finding informer\"},{\"constraint\":\"<<71388002\",\"attributeCardinalityMax\":\"1\",\"attributeNameCode\":\"418775008\",\"attributeCardinalityMin\":\"0\",\"attributeNameDisplay\":\"Finding method\"},{\"constraint\":\"<<260245000OR<<263714004\",\"attributeCardinalityMax\":\"1\",\"attributeNameCode\":\"363713009\",\"attributeCardinalityMin\":\"0\",\"attributeNameDisplay\":\"Has interpretation\"},{\"constraint\":\"<<363787002OR<<108252007OR<<386053000\",\"attributeCardinalityMax\":\"1\",\"attributeNameCode\":\"363714003\",\"attributeCardinalityMin\":\"0\",\"attributeNameDisplay\":\"Interprets\"},{\"constraint\":\"<<282032007\",\"attributeCardinalityMax\":\"1\",\"attributeNameCode\":\"246454002\",\"attributeCardinalityMin\":\"0\",\"attributeNameDisplay\":\"Occurrence\"},{\"constraint\":\"<<769247005OR<<441862004OR<<472963003OR<<308490002\",\"attributeCardinalityMax\":\"1\",\"attributeNameCode\":\"370135005\",\"attributeCardinalityMin\":\"0\",\"attributeNameDisplay\":\"Pathological process\"},{\"constraint\":\"<<404684003OR<<71388002\",\"attributeCardinalityMax\":\"*\",\"attributeNameCode\":\"726633004\",\"attributeCardinalityMin\":\"0\",\"attributeNameDisplay\":\"Temporally related to\"},{\"constraint\":\"<<272141005\",\"attributeCardinalityMax\":\"1\",\"attributeNameCode\":\"246112005\",\"attributeCardinalityMin\":\"0\",\"attributeNameDisplay\":\"Severity\"},{\"constraint\":\"<<442083009\",\"attributeCardinalityMax\":\"1\",\"attributeNameCode\":\"363698007\",\"attributeCardinalityMin\":\"0\",\"attributeNameDisplay\":\"Finding site\"},{\"constraint\":\"<<49755003\",\"attributeCardinalityMax\":\"1\",\"attributeNameCode\":\"116676008\",\"attributeCardinalityMin\":\"0\",\"attributeNameDisplay\":\"Associated morphology\"}]}]}],\"proximalPrimitiveConstraint\":\"<< 64572001 |Disease (disorder)|\",\"guideURL\":\"http:\\/\\/snomed.org\\/dom64572001\",\"domainConstraint\":\"<< 64572001 |Disease (disorder)|\",\"referencedComponentId\":\"64572001\",\"parentDomain\":\"404684003 |Clinical finding (finding)|\",\"proximalPrimitiveRefinement\":\"\"}\n");
        ArrayList<String> attributesWithoutList = snomedConceptModel.getConceptModelDomainAttributesWithout(conceptModelDomain);
        ArrayList<ArrayList<String>> attributesWithList = snomedConceptModel.getConceptModelDomainAttributesWith(conceptModelDomain);

        // check if an attribute is grouped or not -- example: 370153004 (loopup does not always return correct)
        ArrayList<String> attributesWithoutSuperConceptFinalList = getAttributesWithoutSuperConcept(attributesSuperConcepts, attributesWithoutList);
        ArrayList<ArrayList<String>> attributesWithSuperConceptFinalList = getAttributesWithSuperConcept(attributesSuperConcepts, attributesWithList);

        System.out.println("-------");
        System.out.println(attributesWithSuperConceptFinalList);

//        // preprocessing pce

        String pce = "";


        JSONObject jsonObject = preprocessingPce.init(pce, new ArrayList<>());
        ArrayList<String> attributeRelationWithoutPceList = worker.getAttributesWithoutRoleGroup(jsonObject);
        ArrayList<ArrayList<String>> attributeRelationWithPceList = worker.getAttributesWithRoleGroup(jsonObject);

        System.out.println(attributeRelationWithPceList);


        // todo - neu

        // check with rg fits best
        String pceSC = "";
        for (int i = 0; i < attributesWithSuperConceptFinalList.size(); i++) {

        }





        // first get numbers of RG
        int numberRgSc = attributesWithSuperConceptFinalList.size();
        int numberRgPce = attributeRelationWithPceList.size();

        if(numberRgSc == numberRgPce) {  // every rg of pce fits with rg of sc
            ArrayList<String> arrayListMatches = new ArrayList<>();
            for (int i = 0; i < attributesWithSuperConceptFinalList.size(); i++) {
//                String match = "";
//                System.out.println("-- neue PCE Rg --");
//                for (int k = 0; k < attributesWithSuperConceptFinalList.size(); k++) {
//                    ArrayList<String> listRgPce = attributesWithSuperConceptFinalList.get(k);
//                    int counter = 0;
//                    System.out.println("....");
//                    System.out.println(listRgPce);
//                    for (int j = 0; j < attributesWithSuperConceptFinalList.get(i).size(); j++) {
//                        for (String attributePce : listRgPce) {
//                            if (attributesWithSuperConceptFinalList.get(i).get(j).equals(attributePce.replace("#", ""))) {   // equivalent
//                                counter = counter + 1;
////                                System.out.println(attributesWithSuperConceptFinalList.get(i).get(j) + " -- " + attributePce);
//                            } else if (attributesWithSuperConceptFinalList.get(i).get(j).split("@")[0].equals(attributePce.replace("#", "").split("@")[0])) {   // test if attributes are same
//                                String codeA = attributesWithSuperConceptFinalList.get(i).get(j).split("@")[1];
//                                String codeB = attributePce.replace("#", "").split("@")[0];
//                                String result = OntoserverRequest.getSubsumesRelation(urlOntoserver, "20220930", codeA, codeB);
//                                if (result.equals("subsumes")) { // Attribute wurde spezifiziert
//                                    counter = counter + 1;
////                                System.out.println("----> " + attributesWithSuperConceptFinalList.get(i).get(j).split("@")[0] + " " + result);
//                                }
//                            }
//                        }
//                    }
//                    match = match + "-" + counter;
//                }
//                match = match.replaceFirst("-", "");
//                arrayListMatches.add(match);
//                System.out.println(match);
            }

            // determine number of Rg of Sc to PCE rg
//            ArrayList<Integer> idRgList = new ArrayList<>();
//            for (int i = 0; i < arrayListMatches.size(); i++) {
//                int max = 0;
//                int id = -1;
//                String[] match = arrayListMatches.get(i).split("-");
//                for(int j = 0; j < match.length; j++) {
//                    if(Integer.parseInt(match[j]) > max) {
//                        max = Integer.parseInt(match[j]);
//                        id = j;
//                    }
//                }
//                idRgList.add(id);
//            }
//            System.out.println(idRgList);

        }





        // todo - ende


//
//        // determineDelta
////        deltaList.add(determineDeltaWithoutRoleGroup(attributesWithoutSuperConceptFinalList, attributeRelationWithoutPceList));
//        deltaList.add(determineDeltaWithRoleGroup(attributesWithSuperConceptFinalList, attributeRelationWithPceList));

        // ungruppierte attribute nehmen -- in eine Liste, attribut für attribut durchgucken und testen, ob es vorhanden ist
        // wenn ja : subsumption testen : subsumes -> abspeichern
        // wenn nein: abspeichern




    }

    private ArrayList<String> determineDeltaWithoutRoleGroup(ArrayList<String> superConceptList, ArrayList<String> pceList) {
        ArrayList<String> deltaList = new ArrayList<>();
        ArrayList<String> superConceptHelper = new ArrayList<>();
        for(int i = 0; i < superConceptList.size(); i++) {
            superConceptHelper.add(superConceptList.get(i).split("@")[0]);
        }

        for(int i = 0; i < pceList.size(); i++) {
            String pceCode = pceList.get(i).split("@")[0];
            String pceValue = pceList.get(i).split("@")[1];
            if(superConceptHelper.contains(pceCode)) {
                for (int j = 0; j < superConceptList.size(); j++) {
                    if(superConceptHelper.get(j).equals(pceCode)) {
                        String superConceptValue = superConceptList.get(j).split("@")[1];
                        if(pceValue.contains("#")) {    // concrete values
                            if(!superConceptValue.replace("#", "").equals(pceValue.replace("#", ""))) {
                                deltaList.add(pceList.get(i)  + "@subsumes@number");
                            }
                        } else {
                            if(!superConceptValue.equals(pceValue)) {
                                String result = OntoserverRequest.getSubsumesRelation(urlOntoserver, sctVersion, superConceptValue, pceValue);
                                if(result.equals("subsumes")) {
                                    deltaList.add(pceList.get(i) + "@subsumes@without");
                                }
                            }
                        }
                    }
                }
            } else {    // super concept does not contains these attributes
                deltaList.add(pceList.get(i) + "@new@without");
            }
        }
        return deltaList;
    }

    private ArrayList<String> determineDeltaWithRoleGroup(ArrayList<ArrayList<String>> superConceptList, ArrayList<ArrayList<String>> pceList) {
        System.out.println(superConceptList);
        System.out.println(pceList);
        ArrayList<String> deltaList = new ArrayList<>();
        ArrayList<ArrayList<String>> superConceptHelper = new ArrayList<>();

        HashMap<String, ArrayList<String>> hashMap = new HashMap<>();

        for(int i = 0; i < superConceptList.size(); i++) {
            superConceptHelper.add(new ArrayList<>());
            for(int j = 0; j < superConceptList.get(i).size(); j++) {
                String k = superConceptList.get(i).get(j).split("@")[0];
                String v = superConceptList.get(i).get(j).split("@")[1];
                if(!hashMap.containsKey(k)) {
                    ArrayList<String> arrayList = new ArrayList<>();
                    arrayList.add(v);
                    hashMap.put(k, arrayList);
                } else {
                    ArrayList<String> arrayList = hashMap.get(k);
                    arrayList.add(v);
                    hashMap.put(k, arrayList);
                }
            }
        }
        System.out.println(hashMap);

        // länge helper der super concepte gibt die Azahl der RG an
        int number = superConceptHelper.size();


        for(int i = 0; i < pceList.size(); i++) {
            for(int j = 0; j < pceList.get(i).size(); j++) {
                String pceCode = pceList.get(i).get(j).split("@")[0];
                String pceValue = pceList.get(i).get(j).split("@")[1];

                if(hashMap.containsKey(pceCode)) {
                    ArrayList<String> valueList = hashMap.get(pceCode);

                } else {
                    System.out.println("NEU " + pceCode + " -- " + pceValue);
                }

//                System.out.println(superConceptHelper);



//                if(superConceptHelper.contains(pceCode)) {
//                    for (int k = 0; k < superConceptList.get(0).size(); k++) {
//                        if(superConceptHelper.get(k).equals(pceCode.replace("#", ""))) {
//                            String superConceptValue = superConceptList.get(0).get(k).split("@")[1];
//                            if(pceValue.contains("#")) {    // concrete values
//                                if(!superConceptValue.replace("#", "").equals(pceValue.replace("#", ""))) {
//                                    System.out.println("HIER " + superConceptValue.replace("#", "") + " -- " + pceValue.replace("#", ""));
//                                    deltaList.add(pceList.get(i).get(j) + "@number@with" + i);
//                                }
//                            } else {
//                                if(!superConceptValue.equals(pceValue)) {
//                                    String result = OntoserverRequest.getSubsumesRelation(urlOntoserver, sctVersion, superConceptValue, pceValue);
//                                    if(result.equals("subsumes")) {
//                                        deltaList.add(pceList.get(i).get(j) + "@subsumes@with" + i);
//                                    }
//                                }
//                            }
//                        }
//                    }

//                } else {
//                    deltaList.add(pceList.get(i).get(j) + "@new@with" + i);
//                }
            }
        }

//        System.out.println(deltaList);

        return new ArrayList<>();
    }

    private ArrayList<String> getAttributesWithoutSuperConcept(ArrayList<ArrayList<String>> attributesSuperConcepts, ArrayList<String> attributesWithoutList) {
        ArrayList<String> arrayList = new ArrayList<>();
        for(int i = 0; i < attributesSuperConcepts.size(); i++) {
            for(int j = 0; j < attributesSuperConcepts.get(i).size(); j++) {
                String code = attributesSuperConcepts.get(i).get(j).split("@")[0];
                if(attributesWithoutList.contains(code)) {
                    arrayList.add(attributesSuperConcepts.get(i).get(j));
                }
            }
        }
        return arrayList;
    }

    private ArrayList<ArrayList<String>> getAttributesWithSuperConcept(ArrayList<ArrayList<String>> attributesSuperConcepts, ArrayList<ArrayList<String>> attributesWithList) {
        ArrayList<ArrayList<String>> arrayList = new ArrayList<>();
        for(int i = 0; i < attributesSuperConcepts.size(); i++) {
            arrayList.add(new ArrayList<>());
            for(int j = 0; j < attributesSuperConcepts.get(i).size(); j++) {
                String code = attributesSuperConcepts.get(i).get(j).split("@")[0];
                if(attributesWithList.get(0).contains(code)) {
                    arrayList.get(i).add(attributesSuperConcepts.get(i).get(j));
                }
            }
        }
        arrayList.removeIf(l -> l.size() == 0);
        return arrayList;
    }

    private String createEcl(String focusconcet, JSONObject jsonObjectPce, ArrayList<ArrayList<String>> attributeList) throws JSONException {
        JSONArray withRoleGroup = jsonObjectPce.getJSONArray("withRoleGroup");
        for(int i = 0; i < withRoleGroup.length(); i++) {
            jsonObjectPce = withRoleGroup.getJSONObject(i);
            JSONArray roleGroup = jsonObjectPce.getJSONArray("roleGroup");
            ArrayList<String> attributePceList = new ArrayList<>();
            ArrayList<String> valuePceList = new ArrayList<>();
            String ecl =  focusconcet + ":(";
            for(int j = 0; j < roleGroup.length(); j++) {
                JSONObject relation = roleGroup.getJSONObject(j);
                attributePceList.add(relation.getString("attributecode"));
                valuePceList.add(relation.getString("valuecode"));
                ecl = ecl + "[1..1]{[1..1]" + relation.getString("attributecode") + "=" + relation.getString("valuecode") + "}OR";
            }
            ecl = ecl.substring(0, ecl.lastIndexOf("OR"));
            ecl = ecl + ")AND";

            for(int k = 0; k < attributeList.get(0).size(); k++) {
                if(!attributePceList.contains(attributeList.get(0).get(k))) {
                    ecl = ecl + "[0..0]" + attributeList.get(0).get(k) + "=*,";
                }
            }
            ecl = ecl.substring(0, ecl.lastIndexOf(","));
            return ecl;
        }
        return "";
    }

    public void getPrecoordinatedConcept(String focusconcet, JSONObject jsonObjectPce, ArrayList<ArrayList<String>> attributeList, String url) throws JSONException {
        JSONArray withRoleGroup = jsonObjectPce.getJSONArray("withRoleGroup");
        for(int i = 0; i < withRoleGroup.length(); i++) {
            jsonObjectPce = withRoleGroup.getJSONObject(i);
            JSONArray roleGroup = jsonObjectPce.getJSONArray("roleGroup");
            ArrayList<String> attributePceList = new ArrayList<>();
            ArrayList<String> valuePceList = new ArrayList<>();
            String ecl =  focusconcet + ":(";
            for(int j = 0; j < roleGroup.length(); j++) {
                JSONObject relation = roleGroup.getJSONObject(j);
                attributePceList.add(relation.getString("attributecode"));
                valuePceList.add(relation.getString("valuecode"));
                ecl = ecl + "[1..1]{[1..1]" + relation.getString("attributecode") + "=" + relation.getString("valuecode") + "}OR";
            }
            ecl = ecl.substring(0, ecl.lastIndexOf("OR"));
            ecl = ecl + ")AND";

            for(int k = 0; k < attributeList.get(0).size(); k++) {
                if(!attributePceList.contains(attributeList.get(0).get(k))) {
                    ecl = ecl + "[0..0]" + attributeList.get(0).get(k) + "=*,";
                }
            }
            ecl = ecl.substring(0, ecl.lastIndexOf(","));
            JSONObject jsonObject = OntoserverRequest.eclWithoutFilter(url, ecl, true);
            ArrayList<String> codeList = worker.getEclConcepts(jsonObject, true).get(0);
            ArrayList<String> normalFormList = worker.getEclConcepts(jsonObject, true).get(2);
//
            System.out.println(codeList);
        }


    }

}
