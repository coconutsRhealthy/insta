package com.lennart.model;

import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class TikTokProfileFinder {

    public static void main(String[] args) throws Exception {
        new TikTokProfileFinder().getAllTiktokUsers();
    }

    private void testGetRecentDiscountPostsTiktok() throws Exception {
        Map<String, Integer> tiktokUsers = new HashMap<>();

        JSONParser jsonParser = new JSONParser();
        JSONArray apifyData = (JSONArray) jsonParser.parse(new FileReader("/Users/lennartmac/Documents/Projects/insta/src/main/resources/static/apify/tiktok_test/users_test/tiktok_users_20mei.json"));

        for(Object apifyDataElement : apifyData) {
            JSONObject recentTiktokPostJson = (JSONObject) apifyDataElement;
            String caption = (String) recentTiktokPostJson.get("text");

            if(captionContainsDiscountWords(caption)) {
                System.out.println((String) recentTiktokPostJson.get("webVideoUrl"));
                System.out.println(caption);
                System.out.println();
                System.out.println();
                System.out.println("*************");
                System.out.println();
                System.out.println();
            }
        }

    }

    private void getAllTiktokUsers() throws Exception {
        Map<String, Integer> allTiktokUsers = new HashMap<>();

        List<String> filePaths = Files.list(Paths.get("/Users/lennartmac/Documents/Projects/insta/src/main/resources/static/apify/tiktok_test"))
                .filter(Files::isRegularFile)
                .map(Path::toString)
                .collect(Collectors.toList());

        for(String filepath : filePaths) {
            allTiktokUsers.putAll(getTiktokUsersFromJson(filepath));
        }

        allTiktokUsers = allTiktokUsers.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));

        allTiktokUsers.keySet().forEach(key -> System.out.println("\"" + key + "\","));
    }

    private Map<String, Integer> getTiktokUsersFromJson(String jsonPath) throws Exception {
        Map<String, Integer> tiktokUsers = new HashMap<>();

        JSONParser jsonParser = new JSONParser();
        JSONArray apifyData = (JSONArray) jsonParser.parse(new FileReader(jsonPath));

        for(Object apifyDataElement : apifyData) {
            JSONObject searchJson = (JSONObject) apifyDataElement;

            String caption = (String) searchJson.get("text");

            if(captionContainsDiscountWords(caption)) {
                JSONObject authorMeta = (JSONObject) searchJson.get("authorMeta");

                String username = (String) authorMeta.get("name");
                int followers = ((Long) authorMeta.get("fans")).intValue();

                tiktokUsers.put(username, followers);
            }
        }

        tiktokUsers = tiktokUsers.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));

        return tiktokUsers;
    }

    private boolean captionContainsDiscountWords(String caption) {
        boolean containsDiscountWords =
                StringUtils.containsIgnoreCase(caption, "korting") ||
                        StringUtils.containsIgnoreCase(caption, "discount") ||
                        StringUtils.containsIgnoreCase(caption, "% off") ||
                        StringUtils.containsIgnoreCase(caption, "%off") ||
                        StringUtils.containsIgnoreCase(caption, "% of") ||
                        StringUtils.containsIgnoreCase(caption, "%of") ||
                        StringUtils.containsIgnoreCase(caption, "my code") ||
                        StringUtils.containsIgnoreCase(caption, "mijn code") ||
                        StringUtils.containsIgnoreCase(caption, "de code") ||
                        StringUtils.containsIgnoreCase(caption, "code:") ||
                        StringUtils.containsIgnoreCase(caption, "code") ||
                        StringUtils.containsIgnoreCase(caption, "with code") ||
                        StringUtils.containsIgnoreCase(caption, "met code") ||
                        StringUtils.containsIgnoreCase(caption, "use code") ||
                        StringUtils.containsIgnoreCase(caption, "gebruik code") ||
                        StringUtils.containsIgnoreCase(caption, "werbung") ||
                        StringUtils.containsIgnoreCase(caption, "anzeige") ||
                        StringUtils.containsIgnoreCase(caption, "rabatt") ||
                        StringUtils.containsIgnoreCase(caption, "dem code") ||
                        StringUtils.containsIgnoreCase(caption, "le code") ||
                        StringUtils.containsIgnoreCase(caption, "remise") ||
                        StringUtils.containsIgnoreCase(caption, "réduction") ||
                        StringUtils.containsIgnoreCase(caption, "reduction");
        return containsDiscountWords;
    }
}
