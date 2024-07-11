package com.snomedfhir;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class PreprocessingPce {

    public PreprocessingPce() {

    }

    public JSONObject init(String pce, ArrayList<String> list) throws JSONException {
        pce = pce.replace("}{", "},{");
        if(pce.length() > 5) {
            String pceCode = getPceCodes(pce).replace("}{", "},{");
//            System.out.println(pceCode);
            String json = createJson(pceCode, list);
            return new JSONObject(json);
        }
        return new JSONObject();
    }


    public String getPceCodes(String pce) {
        if (pce.contains("|")) {
            String pceCode = pce.replaceAll("[a-zA-Z]", "").replace(" ", "").replace("\n", "").replace("()", "");
            ArrayList<Integer> arrayListPosition = new ArrayList<>();
            if(pceCode.contains(":")) {
                for(int i = 0; i < pceCode.length(); i++) {
                    if(pceCode.charAt(i) == '|') {
                        arrayListPosition.add(i);
                    }
                }
                StringBuilder newPce = new StringBuilder();
                for(int i = 0; i < arrayListPosition.size()-1; i++) {
                    if(i % 2 != 0) { // ungerade Zahl sind immer Codes (siehe Pos 0 = Fc)
                        newPce.append(pceCode, arrayListPosition.get(i), arrayListPosition.get(i + 1));
                    }
                    if(i == 0) {
                        newPce.append(pceCode, 0, arrayListPosition.get(i));
                    }
                }
                if(pceCode.substring(arrayListPosition.get(arrayListPosition.size()-1)).contains("=")) {
                    newPce.append(pceCode.substring(arrayListPosition.get(arrayListPosition.size()-1)));
                }
                pceCode = newPce.toString().replace("|", "");

                int countStart = (int) pceCode.chars().filter(ch -> ch == '{').count();
                int countEnd = (int) pceCode.chars().filter(ch -> ch == '}').count();
                if(countStart != countEnd) {
                    pceCode = pceCode + "}";
                }
            }
            return pceCode;
        }
        return pce;
    }



    public String createJson(String pce, ArrayList<String> list) throws JSONException {
        String focusConcept = pce.split(":")[0];
        String[] listFc = focusConcept.split("[+]");
        String json = "{";

        json = json + createJsonFocusConcept(listFc, list);

        if(pce.contains(":")) {
            String refinement = pce.split(":")[1];
            // get position of start and end of rg
            ArrayList<Integer> arrayListPositionStart = new ArrayList<>();
            ArrayList<Integer> arrayListPositionEnd = new ArrayList<>();
            for (int i = 0; i < refinement.length(); i++) {
                if (refinement.charAt(i) == '{') {
                    arrayListPositionStart.add(i);
                } else if (refinement.charAt(i) == '}') {
                    arrayListPositionEnd.add(i);
                }
            }
            json = json + createJsonWithoutRoleGroup(refinement, arrayListPositionStart, arrayListPositionEnd);
            json = json + createJsonWithRoleGroup(refinement, arrayListPositionStart, arrayListPositionEnd);
        }

        json = json + "}";
        return json;
    }

    private String createJsonFocusConcept(String[] array, ArrayList<String> list) {
        String json = "\"focusconcept\" : [";
        if(list.size() == 0) {
            for (String s : array) {
                json = json + "{\"code\" :\"" + s + "\"},";
            }
        } else {
            for (String s : list) {
                json = json + "{\"code\" :\"" + s.split("\\|")[0].replace(" ", "").replace("\"", "") + "\"},";
            }
            for (String s : array) {
                json = json + "{\"code\" :\"" + s + "\"},";
            }
        }
        json = json.substring(0, json.length()-1);
        json = json + "]";
        return json;
    }

    private String createJsonWithoutRoleGroup(String refinement, ArrayList<Integer> arrayListPositionStart, ArrayList<Integer> arrayListPositionEnd) {
        String json = ",\"withoutRoleGroup\" : [";
        if(arrayListPositionStart.size() == 0 && arrayListPositionEnd.size() == 0) {
            String[] attributeRelation = refinement.split(",");
            json = json + helperAttributeRelation(attributeRelation);
        } else {
            if(arrayListPositionStart.get(0) != 0) {    // ungruppierte Attribute am Anfang
                String[] attributeRelation = refinement.substring(0, arrayListPositionStart.get(0)).split(",");
                json = json + helperAttributeRelation(attributeRelation);
            }
            if(arrayListPositionEnd.size()-1 > -1) {
                if (arrayListPositionEnd.get(arrayListPositionEnd.size()-1) != refinement.length()) { // ungruppierte Attribute am Ende
                    String[] attributeRelation = refinement.substring(arrayListPositionEnd.get(arrayListPositionEnd.size()-1)).split(",");
                    json = json + helperAttributeRelation(attributeRelation);
                }
            }

            for (int i = 0; i < arrayListPositionEnd.size()-1; i++) { // dazwischen sind ungruppierte Attribute
                int c = arrayListPositionStart.get(i + 1) - arrayListPositionEnd.get(i);
                if(c != 2) {    // Attribute sind dazwischen und nicht nur ein Komma
                    String[] attributeRelation = refinement.substring(arrayListPositionEnd.get(i) + 2, arrayListPositionStart.get(i + 1)).split(",");
                    json = json + helperAttributeRelation(attributeRelation);
                }
            }
        }
        if(!json.equals(",\"withoutRoleGroup\" : [")) {
            json = json.substring(0, json.length()-1);
        }
        json = json + "]";
        return json;
    }

    private String createJsonWithRoleGroup(String refinement, ArrayList<Integer> arrayListPositionStart, ArrayList<Integer> arrayListPositionEnd) {
        String json = ",\"withRoleGroup\" : [";
        for(int i = 0; i < arrayListPositionStart.size(); i++) {
            if(arrayListPositionStart.get(i) < arrayListPositionEnd.get(i)) {
                String roleGroup = refinement.substring(arrayListPositionStart.get(i) + 1, arrayListPositionEnd.get(i));
                String[] attributeRelation = roleGroup.split(",");
                json = json + "{\"roleGroup\" : [";
                json = json + helperAttributeRelation(attributeRelation);
                json = json.substring(0, json.length()-1);
                json = json + "]},";
            }
        }
        if(arrayListPositionStart.size() > 0) {
            json = json.substring(0, json.length()-1);
        }
        json = json + "]";
//        System.out.println(json);
        return json;
    }


    private String helperAttributeRelation(String[] array) {
        String json = "";
        for (String s : array) {
            if(s.contains("=")) {
                String code = s.split("=")[0];
                String value = s.split("=")[1];
                json = json + "{\"attributecode\" : \"" + code + "\",\"valuecode\" : \"" + value + "\"},";
            }
        }
        return json;
    }

    public JSONObject preprocessingHighlightingPCE(String url, String pce) throws JSONException {
        JSONObject jsonObject= init(pce, new ArrayList<>());

        String focusConceptCode = "";
        String focusConceptName = "";
        ArrayList<String> listSummaryAttributeNamesWithout = new ArrayList<>();
        ArrayList<String> listSummaryAttributeCodesWithout = new ArrayList<>();
        ArrayList<String> listSummaryValueWithout = new ArrayList<>();
        ArrayList<ArrayList<String>> listSummaryAttributeNamesWith = new ArrayList<>();
        ArrayList<ArrayList<String>> listSummaryAttributeCodesWith = new ArrayList<>();
        ArrayList<ArrayList<String>> listSummaryValueWith = new ArrayList<>();

        if(jsonObject.has("focusconcept")) {
            JSONArray fc = jsonObject.getJSONArray("focusconcept");
            for(int i = 0; i < fc.length(); i++) {
                JSONObject j = fc.getJSONObject(i);
                String code = j.getString("code");
                String name = OntoserverRequest.lookUpNameFsn(url, code);
                focusConceptCode = focusConceptCode + code + "+";
                focusConceptName = focusConceptName + name + "+";
            }
            focusConceptCode = "\"" + focusConceptCode.substring(0, focusConceptCode.lastIndexOf("+")) + "\"";
            focusConceptName = "\"" + focusConceptName.substring(0, focusConceptName.lastIndexOf("+")) + "\"";
        }
        if(jsonObject.has("withoutRoleGroup")) {
            JSONArray without = jsonObject.getJSONArray("withoutRoleGroup");
            for(int i = 0; i < without.length(); i++) {
                JSONObject j = without.getJSONObject(i);
                String valueCode = j.getString("valuecode");
                String attributeCode = j.getString("attributecode");
                String nameCode = "";
                if(!valueCode.contains("#")) {
                    nameCode = OntoserverRequest.lookUpNameFsn(url, valueCode);
                    listSummaryValueWithout.add("\"" + valueCode + " |" + nameCode + "|\"");
                } else {
                    listSummaryValueWithout.add("\"" + valueCode + "\"");
                }
                String nameAttribute = OntoserverRequest.lookUpNameFsn(url, attributeCode);
                listSummaryAttributeCodesWithout.add("\"" + attributeCode + "\"");
                listSummaryAttributeNamesWithout.add("\"" + nameAttribute + "\"");
            }
        }
        if(jsonObject.has("withRoleGroup")) {
            JSONArray with = jsonObject.getJSONArray("withRoleGroup");
            for(int i = 0; i < with.length(); i++) {
                listSummaryAttributeNamesWith.add(new ArrayList<>());
                listSummaryAttributeCodesWith.add(new ArrayList<>());
                listSummaryValueWith.add(new ArrayList<>());

                JSONObject jo = with.getJSONObject(i);
                JSONArray rg = jo.getJSONArray("roleGroup");
                for(int j = 0; j < rg.length(); j++) {
                    jo = rg.getJSONObject(j);
                    String valueCode = jo.getString("valuecode");
                    String attributeCode = jo.getString("attributecode");
                    String nameCode = "";
                    if(!valueCode.contains("#")) {
                        nameCode = OntoserverRequest.lookUpNameFsn(url, valueCode);
                        listSummaryValueWith.get(i).add("\"" + valueCode + " |" + nameCode + "|\"");
                    } else {
                        listSummaryValueWith.get(i).add("\"" + valueCode + "\"");
                    }
                    String nameAttribute = OntoserverRequest.lookUpNameFsn(url, attributeCode);
                    listSummaryAttributeCodesWith.get(i).add("\"" + attributeCode + "\"");
                    listSummaryAttributeNamesWith.get(i).add("\"" + nameAttribute + "\"");
                }
            }
        }
        String result = "{\"result\":[" +
                "{\"focusConceptCode\":" + focusConceptCode + "}," +
                "{\"focusConceptName\":" + focusConceptName + "}," +
                "{\"listSummaryAttributeNamesWithout\":" + listSummaryAttributeNamesWithout + "}," +
                "{\"listSummaryAttributeCodesWithout\":" + listSummaryAttributeCodesWithout + "}," +
                "{\"listSummaryValueWithout\":" + listSummaryValueWithout + "}," +
                "{\"listSummaryAttributeNamesWith\":" + listSummaryAttributeNamesWith + "}," +
                "{\"listSummaryAttributeCodesWith\":" + listSummaryAttributeCodesWith + "}," +
                "{\"listSummaryValueWith\":" + listSummaryValueWith + "}]}";

        return new JSONObject(result);
    }

}
