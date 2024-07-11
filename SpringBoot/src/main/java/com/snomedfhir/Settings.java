package com.snomedfhir;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Scanner;

public class Settings {

    public Settings() {

    }

    public JSONObject init(String url) throws JSONException {
        boolean booleanCs = testUrl(url, "CodeSystem");
        boolean booleanVs = testUrl(url, "ValueSet");
        String json = "";

        if(booleanCs && booleanVs) {
            json = "{\"result\":\"true\"}";
        } else {
            json = "{\"result\":\"false\"}";
        }
        return new JSONObject(json);
    }

    public boolean testUrl(String url, String value) {
        String urlNew = url + "/" + value;

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
            return response.toString().contains("200");
        } catch (Exception e) {
            return false;
        }
    }

    public JSONObject getSnomedVersions(String url) throws JSONException {
        System.out.println(url);
        String urlNew = url + "/CodeSystem"; // ?url:below=http://snomed.info
        ArrayList<String> arrayList = new ArrayList<>();
        String result = "{\"value\": [";

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

            System.out.println("----> ");
            System.out.println(jsonArray);

            for (int j = 0; j < jsonArray.length(); j++) {
                JSONObject json = (JSONObject) jsonArray.get(j);
                JSONArray entry = (JSONArray) json.get("entry");

                for (int k = 0; k < entry.length(); k++) {
                    json = (JSONObject) entry.get(k);
                    json = (JSONObject) json.get("resource");
                    if(json.toString().contains("\"version\"")) {
                        String version = json.getString("version");
                        if(!version.contains("/xsct/") && version.contains("snomed.info/sct")) {
                            System.out.println("-----");
                            System.out.println(version);
                            result = result + "{\"result\":\"" + version + "\"},";
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        result = result.substring(0, result.lastIndexOf(","));
        System.out.println(result);
        return new JSONObject(result + "]}");
    }


    public JSONArray getServer() throws IOException, JSONException {
        String s = readJsonFile();
        return new JSONArray(s);
    }

    public String readJsonFile() throws IOException {
        String pathUserHome = System.getProperty("user.home").replaceAll("\\\\", "/") + "/";
        String folderPath = pathUserHome + "/desktop/";
        String templateNameJson = "server.json";
        File file = new File(folderPath + templateNameJson);

        StringBuilder input = new StringBuilder();
        try {
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                String x = scanner.nextLine();
                input.append("\n" + x);
            }
            return input.toString();
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
            return input.toString();
        }
    }
}
