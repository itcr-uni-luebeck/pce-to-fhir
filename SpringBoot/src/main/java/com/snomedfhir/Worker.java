package com.snomedfhir;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class Worker {

    public Worker() {

    }

    String urlOntoserver = "https://ontoserver.imi.uni-luebeck.de/fhir";

    public ArrayList<String> getFocusConcept(JSONObject jsonObject) throws JSONException {
        ArrayList<String> arrayList = new ArrayList<>();
        JSONArray focusConceptArray = jsonObject.getJSONArray("focusconcept");
        for(int i = 0; i < focusConceptArray.length(); i++) {
            JSONObject obj = focusConceptArray.getJSONObject(i);
            String code = obj.getString("code");
            arrayList.add(code);
        }
        return arrayList;
    }

    public ArrayList<ArrayList<String>> getAttributesWithRoleGroup(JSONObject jsonObject) throws JSONException {
        ArrayList<ArrayList<String>> arrayList = new ArrayList<>();
        JSONArray withArray = jsonObject.getJSONArray("withRoleGroup");
        for(int i = 0; i < withArray.length(); i++) {
            arrayList.add(new ArrayList<>());
            JSONObject obj = withArray.getJSONObject(i);
            JSONArray roleGroupArray = obj.getJSONArray("roleGroup");
            for(int j = 0; j < roleGroupArray.length(); j++) {
                obj = roleGroupArray.getJSONObject(j);
                String name = obj.getString("attributecode").replace(" ", "");
                String value = obj.getString("valuecode").replace(" ", "");
                if(value.length() == 0) {
                    value = obj.getString("valuename").replace(" ", "");
                }
                arrayList.get(i).add(name + "@" + value);
            }
        }
        return arrayList;
    }

    public ArrayList<String> getAttributesWithoutRoleGroup(JSONObject jsonObject) throws JSONException {
        ArrayList<String> arrayList = new ArrayList<>();
        JSONArray withoutArray = jsonObject.getJSONArray("withoutRoleGroup");
        for(int i = 0; i < withoutArray.length(); i++) {
            JSONObject obj = withoutArray.getJSONObject(i);
            String name = obj.getString("attributecode").replace(" ", "");
            String value = obj.getString("valuecode").replace(" ", "");
            if(value.length() == 0) {
                value = obj.getString("valuename").replace(" ", "");
            }
            arrayList.add(name + "@" + value);
        }
        return arrayList;
    }

    public ArrayList<ArrayList<String>> getAttributeRelation(JSONArray jsonArray) throws JSONException {
        ArrayList<ArrayList<String>> arrayList = new ArrayList<>();
        for (int j = 0; j < jsonArray.length(); j++) {
            JSONObject json = (JSONObject) jsonArray.get(j);
            JSONArray parameter = (JSONArray) json.get("parameter");
            for(int i = 0; i < parameter.length(); i++) {
                json = (JSONObject) parameter.get(i);
                if(json.toString().contains("property")) {
                    JSONArray part = (JSONArray) json.get("part");
                    json = (JSONObject) part.get(0);
                    if(json.get("valueCode").toString().matches("[0-9]+")) {
                        arrayList.add(new ArrayList<>());
                        for(int k = 0; k < part.length(); k++) {
                            if(part.get(k).toString().contains("part")) {
                                json = (JSONObject) part.get(k);
                                JSONArray part2 = (JSONArray) json.get("part");

                                json = (JSONObject) part2.get(0);
                                String attributeName = (String) json.get("valueCode");
                                json = (JSONObject) part2.get(1);

                                String attributeValue = "";
                                if (json.toString().contains("valueCode")) {
                                    attributeValue = (String) json.get("valueCode");
                                } else if (json.toString().contains("valueDecimal")) {
                                    attributeValue = "#" + json.get("valueDecimal").toString();
                                } else if (json.toString().contains("valueInteger")) {
                                    attributeValue = "#" + json.get("valueInteger").toString();
                                }
                                arrayList.get(arrayList.size() - 1).add(attributeName + "@" + attributeValue);
                            } else if(!part.get(k).toString().contains("609096000") && k < part.length()-2){
                                json = (JSONObject) part.get(0);
                                String attributeName = (String) json.get("valueCode");
                                String s = OntoserverRequest.getSubsumesRelation(urlOntoserver, "20220930", "762705008", json.getString("valueCode"));
                                json = (JSONObject) part.get(1);
                                if(s.equals("subsumes") || s.equals("equivalent")) {
                                    String attributeValue = "";
                                    if (json.toString().contains("valueCode")) {
                                        attributeValue = (String) json.get("valueCode");
                                    } else if (json.toString().contains("valueDecimal")) {
                                        attributeValue = json.get("valueDecimal").toString();
                                    } else if (json.toString().contains("valueInteger")) {
                                        attributeValue = json.get("valueInteger").toString();
                                    }
                                    arrayList.get(arrayList.size() - 1).add(attributeName + "@" + attributeValue);
                                }
                            }
                        }
                    }
                }
            }
        }
        ArrayList<ArrayList<String>> result = new ArrayList<>();
        for(int i = 0; i < arrayList.size(); i++) {
            if(arrayList.get(i).size() > 0) {
                result.add(arrayList.get(i));
            }
        }
        return result;
    }

    public ArrayList<ArrayList<String>> getEclConcepts(JSONObject json, Boolean normalform) throws JSONException {
        ArrayList<String> name = new ArrayList<>();
        ArrayList<String> code = new ArrayList<>();
        ArrayList<String> nf = new ArrayList<>();
        JSONObject expansion = (JSONObject) json.get("expansion");
        JSONArray contains = (JSONArray) expansion.get("contains");
        for (int k = 0; k < contains.length(); k++) {
            JSONObject jsonContains = (JSONObject) contains.get(k);

            code.add(jsonContains.getString("code"));
            name.add(jsonContains.getString("display").replace("\"", "´"));

            if(normalform) {
                JSONArray extension = jsonContains.getJSONArray("extension");
                for(int l = 0; l < extension.length(); l++) {
                    json = extension.getJSONObject(l);
                    JSONArray extensionArray = json.getJSONArray("extension");

                    JSONObject jsonObject0 = extensionArray.getJSONObject(0);

                    if(jsonObject0.getString("valueCode").equals("normalFormTerse")) {
                        JSONObject jsonObject1 = extensionArray.getJSONObject(1);
                        nf.add(jsonObject1.getString("valueString").replace("<<<", "").replace("===", ""));
                    }
                }
            }
        }
        ArrayList<ArrayList<String>> arrayList = new ArrayList<>();
        arrayList.add(code);
        arrayList.add(name);
        arrayList.add(nf);
        return arrayList;
    }

    public static String runPythonScriptSimilarity(String path, String file) {
        String value = "";
        try {
            boolean isWindows = System.getProperty("os.name").toLowerCase().startsWith("windows");
            String command = "python3 " + path + file;
            System.out.println(command);
            ProcessBuilder builder = new ProcessBuilder();
            builder.directory(new File(path));
            if (isWindows) {
                builder.command("cmd.exe", "/c", command);
            } else {
                builder.command("sh", "-c", command);
            }
            Process process = builder.start();
            InputStream inputStream = process.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line = "";
            while ((line = bufferedReader.readLine()) != null) {
                value = line;
            }
            inputStream.close();
            boolean isFinished = process.waitFor(30, TimeUnit.SECONDS);
            if (!isFinished) {
                process.destroyForcibly();
            }
        } catch (Exception e) {
            return "ERROR";
        }
        System.out.println("-->" + value);
        return value;
    }

    public static String runPythonScriptLca(String path, String file) {
        String value = "";
        try {
            boolean isWindows = System.getProperty("os.name").toLowerCase().startsWith("windows");
            String command = "python3 " + path + file;
            System.out.println(command);
            ProcessBuilder builder = new ProcessBuilder();
            builder.directory(new File(path));
            if (isWindows) {
                builder.command("cmd.exe", "/c", command);
            } else {
                builder.command("sh", "-c", command);
            }
            Process process = builder.start();
            InputStream inputStream = process.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line = "";
            while ((line = bufferedReader.readLine()) != null) {
                value = line;
            }
            inputStream.close();
            boolean isFinished = process.waitFor(30, TimeUnit.SECONDS);
            if (!isFinished) {
                process.destroyForcibly();
            }
        } catch (Exception e) {
            return "ERROR";
        }

        return value;
    }

    public static String runPythonScriptDelta(String path, String file, String superConcept) {
        String value = "";
        try {
            boolean isWindows = System.getProperty("os.name").toLowerCase().startsWith("windows");
            String command = "python3 " + path + file + " " + superConcept;
            System.out.println(command);
            ProcessBuilder builder = new ProcessBuilder();
            builder.directory(new File(path));
            if (isWindows) {
                builder.command("cmd.exe", "/c", command);
            } else {
                builder.command("sh", "-c", command);
            }
            Process process = builder.start();
            InputStream inputStream = process.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line = "";
            while ((line = bufferedReader.readLine()) != null) {
                value = line;
            }
            inputStream.close();
            boolean isFinished = process.waitFor(30, TimeUnit.SECONDS);
            if (!isFinished) {
                process.destroyForcibly();
            }
        } catch (Exception e) {
            return "ERROR";
        }

        return value;
    }



}
