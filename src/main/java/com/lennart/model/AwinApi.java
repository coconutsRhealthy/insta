package com.lennart.model;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class AwinApi {

    private static final String API_TOKEN = "af150887-1156-4193-aeff-ccf83d7cdb0b";
    private static final String PUBLISHER_ID = "1870794";
    private static final String OUTPUT_FILE = "src/main/resources/static/awin/awin-promotionsbig";

    public static void main(String[] args) throws Exception {
        new AwinApi().printIdentifiedDiscountposts();
    }

    private void saveAwinPromotionsJson() {
        JSONParser parser = new JSONParser();

        for(int i = 57; i < 5000; i++) {
            try {
                String endpoint = "https://api.awin.com/publisher/" + PUBLISHER_ID + "/promotions";
                URL url = new URL(endpoint);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                // Set request method and headers
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Authorization", "Bearer " + API_TOKEN);
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setDoOutput(true);

                // Request body
                String jsonInputString = "{"
                        + "\"filters\": {"
                        +     "\"membership\": \"all\","
                        +     "\"status\": \"active\","
                        +     "\"type\": \"voucher\""
                        + "},"
                        + "\"pagination\": {"
                        +     "\"page\": " + i + ","
                        +     "\"pageSize\": 100"
                        + "}"
                        + "}";

                try (OutputStream os = connection.getOutputStream()) {
                    byte[] input = jsonInputString.getBytes("utf-8");
                    os.write(input, 0, input.length);
                }

                BufferedReader in = new BufferedReader(
                        new InputStreamReader(connection.getInputStream(), "utf-8"));

                StringBuilder response = new StringBuilder();
                String line;
                while ((line = in.readLine()) != null) {
                    response.append(line).append(System.lineSeparator());
                }
                in.close();

                // Parse de JSON response om te checken of er resultaten zijn
                Object obj = parser.parse(response.toString());
                JSONObject awinFullDataObject = (JSONObject) obj; // Awin returned array directly
                JSONArray awinDataArray = (JSONArray) awinFullDataObject.get("data");

                if(awinDataArray.size() > 0) {
                    // Save to file
                    try (BufferedWriter writer = new BufferedWriter(new FileWriter(OUTPUT_FILE + "_" + i + ".json"))) {
                        writer.write(response.toString());
                        System.out.println("Response saved to: " + OUTPUT_FILE + "_" + i + ".json");
                    }
                } else {
                    System.out.println("Stopping, cause data array is empty. Stopping at: " + i);
                    break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void printIdentifiedDiscountposts() throws Exception {
        JSONParser jsonParser = new JSONParser();

        int counter = 1;

        for(int i = 1; i <= 100; i++) {
            JSONObject awinFullDataObject = (JSONObject) jsonParser.parse(
                    new FileReader("/Users/lennartmac/Documents/Projects/insta/src/main/resources/static/awin/awin-promotionsbig_" + i + ".json"));

            JSONArray awinDataArray = (JSONArray) awinFullDataObject.get("data");

            for(Object awinDataElement : awinDataArray) {
                JSONObject advertiserVoucherJson = (JSONObject) awinDataElement;

                Map<String, String> regionsMap = getRegionsMap(advertiserVoucherJson);

                if(regionsMap.keySet().contains("NL") || regionsMap.values().contains("Netherlands")) {
                    JSONObject advertiserJson = (JSONObject) advertiserVoucherJson.get("advertiser");
                    JSONObject voucherJson = (JSONObject) advertiserVoucherJson.get("voucher");

                    String advertiser = (String) advertiserJson.get("name");
                    String code = (String) voucherJson.get("code");
                    String url = (String) advertiserVoucherJson.get("url");
                    String title = (String) advertiserVoucherJson.get("title");
                    String startDate = (String) advertiserVoucherJson.get("startDate");
                    String endDate = (String) advertiserVoucherJson.get("endDate");
                    String region = regionsMap.get("NL");

                    if(wasStartDateRecent(startDate) && isEndDateSoon(endDate)) {
                        print(advertiser, code, url, title, startDate, endDate, region, counter++);
                    }
                }
            }
        }
    }

    private void print(String advertiser, String code, String url, String title, String startDate,
                                              String endDate, String region, int counter) {
        System.out.println("****************************************************************");
        System.out.println(counter);
        System.out.println(advertiser);
        System.out.println(title);
        System.out.println("Code: " + code);
        System.out.println("Startdatum: " + startDate.substring(0, startDate.indexOf('T')));
        System.out.println("Einddatum: " + endDate.substring(0, startDate.indexOf('T')));
        System.out.println("Land: " + region);
        System.out.println();
        System.out.println(url);
        System.out.println("****************************************************************");
        System.out.println("x");
        System.out.println("x");
        System.out.println("x");
        System.out.println("x");
    }

    private Map<String, String> getRegionsMap(JSONObject advertiserVoucherJson) {
        Map<String, String> countryMap = new HashMap<>();

        JSONObject regions = (JSONObject) advertiserVoucherJson.get("regions");

        try {
            JSONArray list = (JSONArray) regions.get("list");

            for(Object regionObject : list) {
                JSONObject regionJson = (JSONObject) regionObject;
                String countryCode = (String) regionJson.get("countryCode");
                String countryName = (String) regionJson.get("name");
                countryMap.put(countryCode, countryName);
            }

            return countryMap;
        } catch (Exception e) {
            return countryMap;
        }
    }

    private boolean wasStartDateRecent(String startDate) {
        ZonedDateTime start = ZonedDateTime.parse(startDate);
        ZonedDateTime fourWeeksAgo = ZonedDateTime.now().minusWeeks(4);
        return start.isAfter(fourWeeksAgo);
    }

    private boolean isEndDateSoon(String endDate) {
        ZonedDateTime end = ZonedDateTime.parse(endDate);
        ZonedDateTime fourWeeksFromNow = ZonedDateTime.now().plusWeeks(4);
        return end.isBefore(fourWeeksFromNow);
    }
}


