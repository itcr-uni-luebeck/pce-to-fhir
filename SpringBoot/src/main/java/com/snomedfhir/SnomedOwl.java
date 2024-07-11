package com.snomedfhir;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.semanticweb.elk.owlapi.ElkReasonerFactory;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.Node;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class SnomedOwl {

    public static String fileSnomedOwl = "SpringBoot/src/main/java/com/snomedfhir/resources/snomed-version20230430.owl";
    public static IRI iri = IRI.create("http://snomed.info/id/");
    public static String identifierRoleGroup = "609096000";
    public ArrayList<String> superConceptCandidates = new ArrayList<>();
    public ArrayList<String> superConceptCandidatesDirectFc = new ArrayList<>();
    String urlOntoserver = "https://ontoserver.imi.uni-luebeck.de/fhir";
    String path = "/Users/tessa/Documents/Studium-Semester/Master-Arbeit/pce-to-fhir/SpringBoot/src/main/java/com/snomedfhir/Python/";
    String fileSim = "Similarity.py";
    Worker worker = new Worker();
    PreprocessingPce preprocessingPce = new PreprocessingPce();
    SpecialFormRoleGroups specialFormRoleGroups = new SpecialFormRoleGroups();

    public JSONObject init(String pce, String url) throws OWLOntologyCreationException, JSONException, IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter("SpringBoot/src/main/java/com/snomedfhir/Python/similarity_calculation-pce-superconcept.txt"));
        writer.write("");
        writer.close();
        System.out.println("--START--");
        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        OWLOntology snomedOntology = manager.loadOntologyFromOntologyDocument(new File(fileSnomedOwl));
        System.out.println("Loaded ontology: " + snomedOntology.getOntologyID());
        OWLReasonerFactory reasonerFactory = new ElkReasonerFactory();
        OWLReasoner reasoner = reasonerFactory.createReasoner(snomedOntology);
        OWLDataFactory dataFactory = manager.getOWLDataFactory();

        pce = pce.replace("}{", "},{");
        JSONObject jsonObject = preprocessingPce.init(pce, new ArrayList<>());
        System.out.println(jsonObject);

        // check if more RG with same attributes
        Boolean specialForm = specialFormRoleGroups.checkRoleGroupsSpecialForm(jsonObject);

        OWLClassExpression classExpression = createClassExpression(jsonObject, dataFactory);
        System.out.println(classExpression);
        System.out.println("--Equivalent classes--");

        Node<OWLClass> equivalentClasses = reasoner.getEquivalentClasses(classExpression);
        System.out.println(equivalentClasses);

        String superConcept;


        if(equivalentClasses.getEntities().size() > 0) {
            superConcept = equivalentClasses.getEntities().toString().replaceAll("[a-zA-Z]", "").replace("[", "").replace("]", "").replace("<", "").replace(">", "").replace("/", "").replace(":", "").replace(" ", "").replace(".","");
            superConceptCandidates.add("\"" + superConcept + " |" + OntoserverRequest.lookUpNameFsn(url, superConcept) + "|\"");
        } else if(!specialForm){
            boolean directSuperConcepts = true;
            System.out.println("--Super classes--");
            NodeSet<OWLClass> superClasses = reasoner.getSuperClasses(classExpression, directSuperConcepts);
            System.out.println(superClasses);
            superConceptCandidates = getSuperConceptCandidates(superClasses, url, directSuperConcepts);

//             NodeSet<OWLClass> superClassesDirectFc = reasoner.getSuperClasses(classExpression, true);
//             System.out.println(superClassesDirectFc);
//             superConceptCandidatesDirectFc = getSuperConceptCandidatesDirectFc(superClassesDirectFc, url);
//             System.out.println(superConceptCandidates);
        } else {
            System.out.println("-- Sonderform--");
            superConceptCandidates = specialFormRoleGroups.getSuperConcept(jsonObject, pce, reasoner, dataFactory, url);
            System.out.println("----<<<<<<<------");
            System.out.println(superConceptCandidates);


        }

        System.out.println(superConceptCandidates);

        String result = "{\"result\":" + superConceptCandidates + ",\"directFc\":" + superConceptCandidatesDirectFc + "}";
        return new JSONObject(result);
    }

    public ArrayList<String> getSuperConceptCandidatesDirectFc(NodeSet<OWLClass> superClasses, String url) {
        ArrayList<String> arrayList = new ArrayList<>();
        for (Node<OWLClass> node : superClasses) {
            if(!node.toString().contains("owl:Thing")) {
                String code = node.getEntities().toString().replaceAll("[a-zA-Z]", "").replace("[", "").replace("]", "").replace("<", "").replace(">", "").replace("/", "").replace(":", "").replace(" ", "").replace(".","");
                arrayList.add("\"" + code + " |" + OntoserverRequest.lookUpNameFsn(url, code) +  "|" + "\"");
            }
        }
        return arrayList;
    }


    public ArrayList<String> getSuperConceptCandidates(NodeSet<OWLClass> superClasses, String url, boolean directSuperConcepts) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter("SpringBoot/src/main/java/com/snomedfhir/Python/similarity_calculation-pce-superconcept.txt"));
        ArrayList<String> arrayList = new ArrayList<>();
        for (Node<OWLClass> node : superClasses) {
            if(!node.toString().contains("owl:Thing")) {
                String code = node.getEntities().toString().replaceAll("[a-zA-Z]", "").replace("[", "").replace("]", "").replace("<", "").replace(">", "").replace("/", "").replace(":", "").replace(" ", "").replace(".","");
                arrayList.add("\"" + code + " |" + OntoserverRequest.lookUpNameFsn(url, code) +  "|" + "\"");
                writer.write(code);
                writer.newLine();
            }
        }
        writer.close();
//        if(arrayList.size() > 1 && directSuperConcepts) {
//            String py = Worker.runPythonScriptLca(path, fileLca);
//            if(py.contains(",")) {
//                String[] output = py.split(",");
//                for (String s : output) {
//                    String code = s.replace("]", "").replace("[", "").replace("'", "");
//                    arrayList.add("\"" + code + " |" + OntoserverRequest.lookUpNameFsn(url, code) + "|" + "\"");
//                }
//            } else {
//                String code = py.replace("]", "").replace("[", "").replace("'", "");
//                arrayList.add("\"" + code + " |" + OntoserverRequest.lookUpNameFsn(url, code) + "|" + "\"");
//            }
//        }
        return arrayList;
    }

    public JSONObject getSuperConcept(String[] list1, String[] list2, String pce, String measureName, String url) throws IOException, JSONException {
        superConceptCandidates = new ArrayList<>();
        Collections.addAll(superConceptCandidates, list1);
        Collections.addAll(superConceptCandidatesDirectFc, list2);

        pce = pce.replace("}{", "},{");
        JSONObject jsonObject = preprocessingPce.init(pce, superConceptCandidates);

        BufferedWriter writer = new BufferedWriter(new FileWriter("SpringBoot/src/main/java/com/snomedfhir/Python/similarity_calculation-pce-superconcept.txt", false));

        if(superConceptCandidates.size() > 1) {
            for (String superConceptCandidate : superConceptCandidates) {
                String code = superConceptCandidate.split("\\|")[0].replace(" ", "").replace("\"", "");
                writer.write(jsonObject + "@" + code + "@" + measureName);
                writer.newLine();
            }
            writer.close();
            String x = Worker.runPythonScriptSimilarity(path, fileSim);
            System.out.println(x);
            if(x.contains("Wrong measure name!")) {
                System.out.println("Wrong measure name!");
            } else {
                String[] output = x.split(",");
                ArrayList<Double> simList = new ArrayList<>();
                ArrayList<String> cList = new ArrayList<>();

                System.out.println(List.of(output));

                for(int i = 0; i < output.length; i++) {
                    simList.add(Double.parseDouble(output[i].replace("[", "").replace("]", "").replace(" ", "").replace("'", "").split("@")[1]));
                    cList.add(output[i].replace("[", "").replace("]", "").replace(" ", "").replace("'", "").split("@")[0]);
                }
                if(!measureName.equals("JiangConrathDissimilarity")) {
                    Double max = Collections.max(simList);
                    for(int i = 0; i < simList.size(); i++) {
                        if(simList.get(i).equals(max)) {
                            String code = cList.get(i);
                            String s = code + " |" + OntoserverRequest.lookUpNameFsn(url, code) + "|";
                            ArrayList<ArrayList<String>> arrayList = getAttributeRelation(code, url);
                            String result = "{\"result\":\"" + s + "\", \"attribute\":" + arrayList.get(0) +  ",\"value\":" + arrayList.get(1) +"}";
                            return new JSONObject(result);
                        }
                    }
                } else {
                    Double min = Collections.min(simList);
                    for(int i = 0; i < simList.size(); i++) {
                        if(simList.get(i).equals(min)) {
                            String code = cList.get(i);
                            String s = code + " |" + OntoserverRequest.lookUpNameFsn(url, code) + "|";
                            ArrayList<ArrayList<String>> arrayList = getAttributeRelation(code, url);
                            String result = "{\"result\":\"" + s + "\", \"attribute\":" + arrayList.get(0) +  ",\"value\":" + arrayList.get(1) +"}";
                            return new JSONObject(result);
                        }
                    }
                }
            }
        }
        String code = superConceptCandidates.get(0).split("\\|")[0].replace(" ", "");
        ArrayList<ArrayList<String>> arrayList = getAttributeRelation(code, url);
        String result = "{\"result\":\"" + superConceptCandidates.get(0).replace("\"", "") + "\", \"attribute\":" + arrayList.get(0) +  ",\"value\":" + arrayList.get(1) +"}";
        return new JSONObject(result);
    }

    public ArrayList<ArrayList<String>> getAttributeRelation(String code, String url) throws JSONException {
        JSONArray jsonArray = OntoserverRequest.lookUpNameNormalForm(url, code);
        assert jsonArray != null;
        ArrayList<ArrayList<String>> attributesSuperConcepts = worker.getAttributeRelation(jsonArray);

        System.out.println(attributesSuperConcepts);

        ArrayList<String> arrayListAttribute = new ArrayList<>();
        ArrayList<String> arrayListValue = new ArrayList<>();
        for(int i = 0; i < attributesSuperConcepts.size(); i++) {
            for(int j = 0; j < attributesSuperConcepts.get(i).size(); j++) {
                String codeA = attributesSuperConcepts.get(i).get(j).split("@")[0];
                String codeV = attributesSuperConcepts.get(i).get(j).split("@")[1];
                arrayListAttribute.add("\"" + codeA + " |" + OntoserverRequest.lookUpNameFsn(url, codeA) + "|" + "\"");
                if(!codeV.contains("#")) {
                    arrayListValue.add("\"" + codeV + " |" + OntoserverRequest.lookUpNameFsn(url, codeV) + "|" + "\"");
                } else {
                    arrayListValue.add("\"" + codeV + "\"");
                }
            }
        }
        ArrayList<ArrayList<String>> arrayList = new ArrayList<>();
        arrayList.add(arrayListAttribute);
        arrayList.add(arrayListValue);
        return arrayList;
    }


    public OWLClassExpression createClassExpression(JSONObject jsonObject, OWLDataFactory dataFactory) throws JSONException {
        Set<OWLClassExpression> setFinal = new HashSet<>();
        // Focus concept
        ArrayList<OWLClassExpression> focusConcept = setFocusConcept(jsonObject, dataFactory);
        setFinal.addAll(focusConcept);

        // Attributes without RoleGroups
        ArrayList<OWLClassExpression> attributeRelationWithoutRoleGroup = setAttributeWithoutRoleGroup(jsonObject, dataFactory);
        setFinal.addAll(attributeRelationWithoutRoleGroup);

        // Attributes with RoleGroups
        OWLObjectPropertyExpression roleGroup = dataFactory.getOWLObjectProperty(IRI.create(iri + identifierRoleGroup));
        ArrayList<ArrayList<OWLClassExpression>> attributeRelationRoleGroup = setAttributeWithRoleGroup(jsonObject, dataFactory);
        for(int i = 0; i < attributeRelationRoleGroup.size(); i++) {
            Set<OWLClassExpression> set = new HashSet<>(attributeRelationRoleGroup.get(i));
            OWLClassExpression attributeRelationFinal = dataFactory.getOWLObjectIntersectionOf(set);
            setFinal.add(dataFactory.getOWLObjectSomeValuesFrom(roleGroup, attributeRelationFinal));
        }
        return dataFactory.getOWLObjectIntersectionOf(setFinal);
    }


    public ArrayList<OWLClassExpression> setFocusConcept(JSONObject jsonObject, OWLDataFactory dataFactory) throws JSONException {
        ArrayList<String> listCode = worker.getFocusConcept(jsonObject);
        ArrayList<OWLClassExpression> arrayListClassExpression = new ArrayList<>();
        for (int i = 0; i < listCode.size(); i++) {
            arrayListClassExpression.add(dataFactory.getOWLClass(IRI.create(iri + listCode.get(i))));
        }
        return arrayListClassExpression;
    }


    public ArrayList<ArrayList<OWLClassExpression>> setAttributeWithRoleGroup(JSONObject jsonObject, OWLDataFactory dataFactory) throws JSONException {
        ArrayList<ArrayList<String>> arrayList = worker.getAttributesWithRoleGroup(jsonObject);
        ArrayList<ArrayList<OWLClassExpression>> arrayListClassExpression = new ArrayList<>();
        for(int i = 0; i < arrayList.size(); i++) {
            arrayListClassExpression.add(new ArrayList<>());
            for(int j = 0; j < arrayList.get(i).size(); j++) {
                OWLObjectPropertyExpression attribute = dataFactory.getOWLObjectProperty(IRI.create(iri + arrayList.get(i).get(j).split("@")[0]));
                String code = arrayList.get(i).get(j).split("@")[1];
                OWLClassExpression attributeValue;
                OWLClassExpression attributeRelation = null;
                if(!code.contains("#")) {
                    attributeValue = dataFactory.getOWLClass(IRI.create(iri + code));
                    attributeRelation = dataFactory.getOWLObjectSomeValuesFrom(attribute, attributeValue);
                } else if(code.contains(".")) {
                    OWLLiteral l = dataFactory.getOWLLiteral(Double.parseDouble(code.replace("#", "")));
                    OWLDataPropertyExpression a = dataFactory.getOWLDataProperty(IRI.create(iri + arrayList.get(i).get(j).split("@")[0]));
                    attributeRelation = dataFactory.getOWLDataHasValue(a, l);
                } else if (!code.contains(".")) {
                    OWLLiteral l = dataFactory.getOWLLiteral(Integer.parseInt(code.replace("#", "")));
                    OWLDataPropertyExpression a = dataFactory.getOWLDataProperty(IRI.create(iri + arrayList.get(i).get(j).split("@")[0]));
                    attributeRelation = dataFactory.getOWLDataHasValue(a, l);
                }
                arrayListClassExpression.get(i).add(attributeRelation);
            }
        }
        return arrayListClassExpression;
    }


    public ArrayList<OWLClassExpression> setAttributeWithoutRoleGroup(JSONObject jsonObject, OWLDataFactory dataFactory) throws JSONException {
        ArrayList<String> arrayList = worker.getAttributesWithoutRoleGroup(jsonObject);
        ArrayList<OWLClassExpression> arrayListClassExpression = new ArrayList<>();
        for(int i = 0; i < arrayList.size(); i++) {
            OWLObjectPropertyExpression attribute = dataFactory.getOWLObjectProperty(IRI.create(iri + arrayList.get(i).split("@")[0]));
            String code = arrayList.get(i).split("@")[1];
            OWLClassExpression attributeValue;
            OWLClassExpression attributeRelation = null;
            if(!code.contains("#")) {
                attributeValue = dataFactory.getOWLClass(IRI.create(iri + code));
                attributeRelation = dataFactory.getOWLObjectSomeValuesFrom(attribute, attributeValue);
            } else if(code.contains(".")) {
                OWLLiteral l = dataFactory.getOWLLiteral(Double.parseDouble(code.replace("#", "")));
                OWLDataPropertyExpression a = dataFactory.getOWLDataProperty(IRI.create(iri + arrayList.get(i).split("@")[0]));
                attributeRelation = dataFactory.getOWLDataHasValue(a, l);
            } else if (!code.contains(".")) {
                OWLLiteral l = dataFactory.getOWLLiteral(Integer.parseInt(code.replace("#", "")));
                OWLDataPropertyExpression a = dataFactory.getOWLDataProperty(IRI.create(iri + arrayList.get(i).split("@")[0]));
                attributeRelation = dataFactory.getOWLDataHasValue(a, l);
            }
            arrayListClassExpression.add(attributeRelation);
        }
        return arrayListClassExpression;
    }

}
