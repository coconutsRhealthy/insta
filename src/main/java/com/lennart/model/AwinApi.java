package com.lennart.model;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class AwinApi {

    private static final String API_TOKEN = "af150887-1156-4193-aeff-ccf83d7cdb0b";
    private static final String PUBLISHER_ID = "1870794";
    private static final String OUTPUT_FILE = "src/main/resources/static/awin/awin-promotions.json";

    public static void main(String[] args) throws Exception {
        new AwinApi().doStuff();
    }

    private void saveAwinPromotionsJson() {
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
                    +     "\"membership\": \"joined\","
                    +     "\"status\": \"active\","
                    +     "\"type\": \"voucher\""
                    + "},"
                    + "\"pagination\": {"
                    +     "\"page\": 1,"
                    +     "\"pageSize\": 10"
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

            // Save to file
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(OUTPUT_FILE))) {
                writer.write(response.toString());
                System.out.println("Response saved to: " + OUTPUT_FILE);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void doStuff() throws Exception {
        JSONParser jsonParser = new JSONParser();

        JSONObject awinFullDataObject = (JSONObject) jsonParser.parse(
                new FileReader("/Users/lennartmac/Documents/Projects/insta/src/main/resources/static/awin/awin-promotions.json"));

        JSONArray awinDataArray = (JSONArray) awinFullDataObject.get("data");

        int counter = 1;

        for(Object awinDataElement : awinDataArray) {
            JSONObject advertiserVoucherJson = (JSONObject) awinDataElement;
            JSONObject advertiserJson = (JSONObject) advertiserVoucherJson.get("advertiser");
            JSONObject voucherJson = (JSONObject) advertiserVoucherJson.get("voucher");

            String advertiser = (String) advertiserJson.get("name");
            String code = (String) voucherJson.get("code");
            String url = (String) advertiserVoucherJson.get("url");
            String title = (String) advertiserVoucherJson.get("title");
            String startDate = (String) advertiserVoucherJson.get("startDate");
            String endDate = (String) advertiserVoucherJson.get("endDate");
            String region = "NL";

            printIdentifiedDiscountposts(advertiser, code, url, title, startDate, endDate, region, counter++);

        }



    }

    private void printIdentifiedDiscountposts(String advertiser, String code, String url, String title, String startDate,
                                              String endDate, String region, int counter) {
        System.out.println("****************************************************************");
        System.out.println(counter);
        System.out.println(advertiser);
        System.out.println(title);
        System.out.println("Code: " + code);
        System.out.println("Startdatum: " + startDate.substring(0, startDate.indexOf('T')));
        System.out.println("Einddatum: " + endDate.substring(0, startDate.indexOf('T')));
        //System.out.println("Land: " + region);
        System.out.println();
        System.out.println(url);
        System.out.println("****************************************************************");
        System.out.println("x");
        System.out.println("x");
        System.out.println("x");
        System.out.println("x");
    }





    ////////////////// save all pages pagination


    public void saveAllAwinPromotionsJson() {
        int page = 1;
        int pageSize = 10;
        boolean morePages = true;
        JSONParser parser = new JSONParser();

        while (morePages) {
            try {
                String endpoint = "https://api.awin.com/publisher/" + PUBLISHER_ID + "/promotions";
                URL url = new URL(endpoint);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                // Headers
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Authorization", "Bearer " + API_TOKEN);
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setDoOutput(true);

                // JSON body met dynamische pagina
                String jsonInputString = "{"
                        + "\"filters\": {"
                        +     "\"membership\": \"joined\","
                        +     "\"status\": \"active\","
                        +     "\"type\": \"voucher\""
                        + "},"
                        + "\"pagination\": {"
                        +     "\"page\": " + page + ","
                        +     "\"pageSize\": " + pageSize
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
                JSONObject promotionsArray = (JSONObject) obj; // Awin returned array directly

                if (promotionsArray.isEmpty()) {
                    morePages = false;
                    System.out.println("No more pages. Stopping.");
                } else {
                    // Save response to file
                    String outputFileName = "awin-promotions-page-" + page + ".json";
                    try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFileName))) {
                        writer.write(response.toString());
                        System.out.println("Saved page " + page + " to file: " + outputFileName);
                    }

                    // Go to next page
                    page++;
                }

            } catch (Exception e) {
                e.printStackTrace();
                break; // stop bij fout
            }
        }
    }
}


