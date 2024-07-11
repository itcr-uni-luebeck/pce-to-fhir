package com.snomedfhir;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class CodeSystemSupplement {

    public CodeSystemSupplement() {

    }


    public static JSONObject getCsSupplements(String url) throws JSONException {
        String urlNew = url + "/CodeSystem?name=SctPceSupplementPceBuilder";
        String jsonResult = "{\"value\":[";

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
                JSONArray entry = json.getJSONArray("entry");
                for(int i = 0; i < entry.length(); i++) {
                    json = (JSONObject) entry.get(i);
                    String fullUrl = json.getString("fullUrl");
                    json = (JSONObject) json.get("resource");
                    String title = json.getString("title");
                    jsonResult = jsonResult + "{\"url\":\"" + fullUrl + "\", \"title\":\"" + title + "\"},";
                }
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

        jsonResult = jsonResult.substring(0, jsonResult.lastIndexOf(",")) + "]}";
        return new JSONObject(jsonResult);
    }
}
