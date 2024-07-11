package com.snomedfhir;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;

public class OntoserverRequest {

    public OntoserverRequest() {

    }

    public static JSONArray lookUpNameNormalForm(String url, String code) {
        String urlNew = url + "/CodeSystem/$lookup?system=http://snomed.info/sct&code=" + code.replace("\"", "") + "&property=*";
        try {
            HttpClient httpClient = HttpClient.newBuilder()
                    .version(HttpClient.Version.HTTP_2)
                    .connectTimeout(Duration.ofSeconds(10))
                    .build();
            HttpRequest request = HttpRequest.newBuilder()
                    .GET()
                    .uri(URI.create(urlNew))
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            return new JSONArray("[" + response.body() + "]");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getSubsumesRelation(String url, String version, String codeA, String codeB) {
        String value = "";
        String urlNew = url + "/CodeSystem/$subsumes?" +
                "system=http://snomed.info/sct&codeA=" + codeA + "&codeB=" + codeB + "&version=" + version;

        try {
            HttpClient httpClient = HttpClient.newBuilder()
                    .version(HttpClient.Version.HTTP_2)
                    .connectTimeout(Duration.ofSeconds(10))
                    .build();
            HttpRequest request = HttpRequest.newBuilder()
                    .GET()
                    .uri(URI.create(urlNew))
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            JSONArray jsonArray = new JSONArray("[" + response.body() + "]");
            for (int j = 0; j < jsonArray.length(); j++) {
                JSONObject json = (JSONObject) jsonArray.get(j);
                JSONArray parameter = (JSONArray) json.get("parameter");
                json = (JSONObject) parameter.get(0);
                value = (String) json.get("valueCode");
                break;

            }
        } catch (Exception e) {

        }
        return value;
    }

    public static JSONObject eclWithoutFilter(String url, String expression, Boolean normalform) {
        String urlNew = "";
        if(!normalform) {
            urlNew = url + "/ValueSet/$expand?url=" +
                    "http://snomed.info/sct?fhir_vs=ecl/" + URLEncoder.encode(expression, StandardCharsets.UTF_8);
        } else {
            urlNew = url + "/ValueSet/$expand?property=*&url=" +
                    "http://snomed.info/sct?fhir_vs=ecl/" + URLEncoder.encode(expression, StandardCharsets.UTF_8) + "&count=300";

        }

        JSONObject jsonObject = new JSONObject();

        try {
            HttpClient httpClient = HttpClient.newBuilder()
                    .version(HttpClient.Version.HTTP_2)
                    .connectTimeout(Duration.ofSeconds(10))
                    .build();
            HttpRequest request = HttpRequest.newBuilder()
                    .GET()
                    .uri(URI.create(urlNew))
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            jsonObject = new JSONObject(response.body());

        } catch (Exception e) {
            e.printStackTrace();
        }

        return jsonObject;

    }

    public static ArrayList<String> eclRequest(String url, String version, String expression, Boolean displayCodes) {
        String urlNew = url;
        if(version.length() > 0) {
            urlNew = urlNew + "/ValueSet/$expand?url=" +
                    "http://snomed.info/sct?fhir_vs=ecl/" + URLEncoder.encode(expression, StandardCharsets.UTF_8) + "&version=" + version;
        } else {
            urlNew = urlNew + "/ValueSet/$expand?url=" +
                    "http://snomed.info/sct?fhir_vs=ecl/" + URLEncoder.encode(expression, StandardCharsets.UTF_8);
        }

        ArrayList<String> arrayList = new ArrayList<>();

        try {
            HttpClient httpClient = HttpClient.newBuilder()
                    .version(HttpClient.Version.HTTP_2)
                    .connectTimeout(Duration.ofSeconds(10))
                    .build();
            HttpRequest request = HttpRequest.newBuilder()
                    .GET()
                    .uri(URI.create(urlNew))
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            JSONArray jsonArray = new JSONArray("[" + response.body() + "]");

            for (int j = 0; j < jsonArray.length(); j++) {
                JSONObject json = (JSONObject) jsonArray.get(j);
                JSONObject expansion = (JSONObject) json.get("expansion");
                JSONArray contains = (JSONArray) expansion.get("contains");
                for (int k = 0; k < contains.length(); k++) {
                    JSONObject jsonContains = (JSONObject) contains.get(k);
                    String code = jsonContains.getString("code");
                    String display = jsonContains.getString("display").replace("\"", "´");
                    String sum = "";
                    if(displayCodes) {
                        sum = code + "|" + display + "|";
                    } else {
                        sum = code;
                    }

                    arrayList.add(sum);
                }
            }
        } catch (Exception e) {

        }
        return arrayList;

    }

    public static String lookUpNameFsn(String url, String code) {
        String valueString = "";
        String urlNew = url + "/CodeSystem/$lookup?system=http://snomed.info/sct&code=" + code.replace(" ", "") + "&property=designation";
        try {
            HttpClient httpClient = HttpClient.newBuilder()
                    .version(HttpClient.Version.HTTP_2)
                    .connectTimeout(Duration.ofSeconds(10))
                    .build();
            HttpRequest request = HttpRequest.newBuilder()
                    .GET()
                    .uri(URI.create(urlNew))
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            JSONArray jsonArray = new JSONArray("[" + response.body() + "]");
            for (int j = 0; j < jsonArray.length(); j++) {
                JSONObject json = (JSONObject) jsonArray.get(j);
                JSONArray parameter = (JSONArray) json.get("parameter");
                for(int i = 0; i < parameter.length(); i++) {
                    json = (JSONObject) parameter.get(i);
                    if(json.get("name").toString().equals("designation")) {
                        JSONArray part = (JSONArray) json.get("part");
                        for(int k = 0; k < part.length(); k++) {
                            json = (JSONObject) part.get(k);

                            if(json.toString().contains("valueCoding")) {
                                json = (JSONObject) json.get("valueCoding");
                                if(json.toString().contains("code")) {
                                    if(json.get("code").toString().equals("900000000000003001")) {
                                        json = (JSONObject) part.get(k+1);
                                        valueString = (String) json.get("valueString");
                                    }
                                }
                            }
                        }
                    }
                }
                return valueString;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }

    public String validatePce(String pce, String url) {
        String urlNew = url + "/CodeSystem/$validate-code";
        String jsonResult = "{\"result\":";

        String json = "{\n" +
                "    \"resourceType\": \"Parameters\",\n" +
                "    \"parameter\": [\n" +
                "        {\n" +
                "            \"name\": \"url\",\n" +
                "            \"valueUri\": \"http://snomed.info/sct\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"name\": \"coding\",\n" +
                "            \"valueCoding\": {\n" +
                "                \"system\": \"http://snomed.info/sct\",\n" +
                "                \"code\": \"" + pce + "\"\n" +
                "            }\n" +
                "        }\n" +
                "    ]\n" +
                "}";

        try {
            HttpClient httpClient = HttpClient.newBuilder()
                    .version(HttpClient.Version.HTTP_2)
                    .connectTimeout(Duration.ofSeconds(10))
                    .build();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(urlNew))
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .header("Content-Type", "application/fhir+json")
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            JSONArray jsonArray = new JSONArray("[" + response.body() + "]");
            JSONObject jsonObject = jsonArray.getJSONObject(0);

            JSONArray parameter = jsonObject.getJSONArray("parameter");
            jsonObject = parameter.getJSONObject(0);
            if(jsonObject.toString().contains("\"valueBoolean\":true")) {               // syntax correct
                return  jsonResult + "\"" + "True" + "\"}";
            } else {
                String result = "Expression violates the MRCM: @";

                for(int i = 0; i < parameter.length(); i++) {
                    if(parameter.get(i).toString().contains("\"name\"")) {
                        jsonObject =  parameter.getJSONObject(i);
                        String name = jsonObject.getString("name");
                        if(name.equals("reason")) {
                            String value = jsonObject.getString("valueString");     // semantic error, like wrong attributes
                            result = result + value + "@";
                        } else if(name.equals("message")) {
                            String value = jsonObject.getString("valueString");     // syntax error, like missing }
                            if(!value.equals("Expression violates the MRCM")) {
                                result = value;
                            }

                        }
                    }
                }
                if(result.contains("@")) {
                    result = result.substring(0, result.lastIndexOf("@"));
                }
                return jsonResult + "\"" + result + "\"}";

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonResult + "\"" + "error" + "\"}";
    }

    public static JSONArray getCodeSystemSupplement(String url) {
        JSONArray jsonArray = new JSONArray();

        try {
            HttpClient httpClient = HttpClient.newBuilder()
                    .version(HttpClient.Version.HTTP_2)
                    .connectTimeout(Duration.ofSeconds(10))
                    .build();
            HttpRequest request = HttpRequest.newBuilder()
                    .GET()
                    .uri(URI.create(url))
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            jsonArray = new JSONArray("[" + response.body() + "]");

        } catch (Exception e) {
            e.printStackTrace();
        }

        return jsonArray;
    }
}
