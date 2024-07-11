package com.snomedfhir;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.reasoner.Node;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.OWLReasoner;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

public class SpecialFormRoleGroups {

    public SpecialFormRoleGroups() {

    }


    public ArrayList<String> superConceptCandidates = new ArrayList<>();
    public String superConcept;
    String path = "/Users/tessa/Documents/Studium-Semester/Master-Arbeit/pce-to-fhir/SpringBoot/src/main/java/com/snomedfhir/Python/";
    String fileLca = "SuperConceptCandidatesLca.py";

    public ArrayList<String> getSuperConcept(JSONObject jsonObject, String pce, OWLReasoner reasoner, OWLDataFactory dataFactory, String url) throws JSONException, IOException {
        SnomedOwl snomedOwl = new SnomedOwl();
        ArrayList<String> listPce = getPceRoleGroup(jsonObject);
        ArrayList<OWLClassExpression> listClassExpression = new ArrayList<>();
        ArrayList<String> listLca = new ArrayList<>();
        for (int i = 0; i < listPce.size(); i++) {
            jsonObject = snomedOwl.preprocessingPce.init(listPce.get(i), new ArrayList<>());
            OWLClassExpression classExpression = snomedOwl.createClassExpression(jsonObject, dataFactory);
            listClassExpression.add(classExpression);
        }
        System.out.println(listPce);
        System.out.println(listClassExpression);
        for (int i = 0; i < listClassExpression.size(); i++) {
            System.out.println("--Equivalent classes--");
            Node<OWLClass> equivalentClasses = reasoner.getEquivalentClasses(listClassExpression.get(i));
            System.out.println(equivalentClasses);
            if(equivalentClasses.getEntities().size() > 0) {
                superConcept = equivalentClasses.getEntities().toString().replaceAll("[a-zA-Z]", "").replace("[", "").replace("]", "").replace("<", "").replace(">", "").replace("/", "").replace(":", "").replace(" ", "").replace(".","");
                listLca.add(superConcept);
            } else {
                boolean directSuperConcepts = true;
                System.out.println("--Super classes--");
                NodeSet<OWLClass> superClasses = reasoner.getSuperClasses(listClassExpression.get(i), directSuperConcepts);
                ArrayList<String> arrayList = snomedOwl.getSuperConceptCandidates(superClasses, url, false);
                for (String c: arrayList) {
                    if(!superConceptCandidates.contains(c)) {
                        superConceptCandidates.add(c);
                    }
                }
//                String[] stringArr = new String[superConceptCandidates.size()];
//                stringArr = superConceptCandidates.toArray(stringArr);
//                JSONObject jsonObjectResult = snomedOwl.getSuperConcept(stringArr, new String[0], listPce.get(i), "BatetSanchezValls", url);
//                String sc = jsonObjectResult.getString("result");
//                listLca.add(sc);
            }
        }
        System.out.println(superConceptCandidates);
        String lca = calculateLca(superConceptCandidates, url);
        superConceptCandidates = new ArrayList<>();
        superConceptCandidates.add(lca);
        System.out.println(superConceptCandidates);
        return superConceptCandidates;
    }

    public String calculateLca(ArrayList<String> arrayList, String url) throws IOException {
        System.out.println("HIER");
        System.out.println(arrayList);

        BufferedWriter writer = new BufferedWriter(new FileWriter("SpringBoot/src/main/java/com/snomedfhir/Python/similarity_calculation-pce-superconcept.txt"));
        for (int i = 0; i < arrayList.size(); i++) {
            String code = arrayList.get(i).replaceAll("[a-zA-Z]", "").replace("[", "").replace("]", "").replace("<", "").replace(">", "").replace("/", "").replace(":", "").replace(" ", "").replace(".","").replace("\"","").replace("|()|", "");
            writer.write(code);
            writer.newLine();
        }
        writer.close();
        String result = "";
        String py = Worker.runPythonScriptLca(path, fileLca);
        System.out.println(py);
        if(py.contains(",")) {
            String[] output = py.split(",");
            for (String s : output) {
                String code = s.replace("]", "").replace("[", "").replace("'", "");
                result = "\"" + code + " |" + OntoserverRequest.lookUpNameFsn(url, code) + "|" + "\"";
            }
        } else {
            String code = py.replace("]", "").replace("[", "").replace("'", "");
            result = "\"" + code + " |" + OntoserverRequest.lookUpNameFsn(url, code) + "|" + "\"";
        }
        return result;
    }


    public Boolean checkRoleGroupsSpecialForm(JSONObject jsonObject) throws JSONException {
        ArrayList<ArrayList<String>> arrayList = new ArrayList<>();
        JSONArray roleGroups = jsonObject.getJSONArray("withRoleGroup");
        if(roleGroups.length() == 1) {
            return false;
        } else {
            for (int i = 0; i < roleGroups.length(); i++) {
                arrayList.add(new ArrayList<>());
                JSONObject jo = roleGroups.getJSONObject(i);
                JSONArray roleGroup = jo.getJSONArray("roleGroup");
                for (int j = 0; j < roleGroup.length(); j++) {
                    jo = roleGroup.getJSONObject(j);
                    String attribute = jo.getString("attributecode");
                    arrayList.get(i).add(attribute);
                }
            }
            for (int i = 0; i < arrayList.size(); i++) {
                Collection listOne = arrayList.get(i);
                for (int j = 0; j < arrayList.size(); j++) {
                    if(i != j) {
                        Collection listTwo = arrayList.get(j);
                        listTwo.retainAll(listOne);
                        if(listTwo.size() > 0) {
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }


    public static ArrayList<String> getPceRoleGroup(JSONObject jsonObject) throws JSONException {
        ArrayList<String> arrayList = new ArrayList<>();
        String fcFinal = "";
        JSONArray focusConcept = jsonObject.getJSONArray("focusconcept");
        for (int i = 0; i < focusConcept.length(); i++) {
            JSONObject jo = focusConcept.getJSONObject(i);
            String fc = jo.getString("code");
            fcFinal = fc + "+";
        }
        fcFinal = fcFinal.substring(0, fcFinal.lastIndexOf("+")) + ":";
        JSONArray roleGroups = jsonObject.getJSONArray("withRoleGroup");
        for (int i = 0; i < roleGroups.length(); i++) {
            String refinement = "{";
            JSONObject jo = roleGroups.getJSONObject(i);
            JSONArray roleGroup = jo.getJSONArray("roleGroup");
            for (int j = 0; j < roleGroup.length(); j++) {
                jo = roleGroup.getJSONObject(j);
                String attribute = jo.getString("attributecode");
                String value = jo.getString("valuecode");
                refinement = refinement + attribute + "=" + value + ",";
            }
            refinement = refinement.substring(0, refinement.lastIndexOf(",")) + "}";
            String pce = fcFinal + refinement;
            System.out.println(pce);
            arrayList.add(pce);
        }

        return arrayList;
    }
}
