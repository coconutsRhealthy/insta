package com.lennart.model;

import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.stream.Collectors;

public class TikTokProfileFinder {

    public static void main(String[] args) throws Exception {
        new TikTokProfileFinder().askOpenAiWhichCountry();
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

    private Map<String, Integer> getAllTiktokUsers() throws Exception {
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

        return allTiktokUsers;
    }

    private Map<String, JSONArray> getAllPostsForAllTiktokUsers() throws Exception {
        Map<String, JSONArray> allPostsForAllTiktokUsers = new HashMap<>();

        List<String> filePaths = Files.list(Paths.get("/Users/lennartmac/Documents/Projects/insta/src/main/resources/static/apify/tiktok_test"))
                .filter(Files::isRegularFile)
                .map(Path::toString)
                .collect(Collectors.toList());

        for(String filepath : filePaths) {
            allPostsForAllTiktokUsers.putAll(getAllPostsForUser(filepath));
        }

        allPostsForAllTiktokUsers = allPostsForAllTiktokUsers.entrySet().stream()
                .sorted((e1, e2) -> Integer.compare(e2.getValue().size(), e1.getValue().size()))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));

        return allPostsForAllTiktokUsers;
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

    private Map<String, JSONArray> getAllPostsForUser(String jsonPath) throws Exception {
        Map<String, JSONArray> postsPerTiktokUser = new HashMap<>();

        JSONParser jsonParser = new JSONParser();
        JSONArray apifyData = (JSONArray) jsonParser.parse(new FileReader(jsonPath));

        for(Object apifyDataElement : apifyData) {
            JSONObject videoJson = (JSONObject) apifyDataElement;
            JSONObject authorMeta = (JSONObject) videoJson.get("authorMeta");
            String username = (String) authorMeta.get("name");
            postsPerTiktokUser.putIfAbsent(username, new JSONArray());
            postsPerTiktokUser.get(username).add(videoJson);
        }

        return postsPerTiktokUser;
    }

    private void askOpenAiWhichCountry() throws Exception {
        Map<String, String> openAiData = prepareOpenAiData();
        OpenAi openAi = new OpenAi();
        InfluencerPersister influencerPersister = new InfluencerPersister();
        int counter = 0;

        System.out.println("openAiData: " + openAiData.size());

        for(Map.Entry<String, String> entry : openAiData.entrySet()) {
            String country = openAi.isTiktokProfileDutch(entry.getValue());
            String lineToAdd = entry.getKey() + " - " + country + System.lineSeparator();
            influencerPersister.addTiktokUserToDb(entry.getKey(), -1, country);
            Files.write(Paths.get("/Users/lennartmac/Desktop/influencer_persister_stuff/tiktok_users.txt"), lineToAdd.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            System.out.println("******* " + counter++ + " *******");
        }
    }

    private Map<String, String> prepareOpenAiData() throws Exception {
        Map<String, String> openAiData = new HashMap<>();
        Map<String, Map<String, List<String>>> bioAndCaptionsForAllUsers = getBioAndAllCaptionsOfTiktokUsers();

        for(Map.Entry<String, Map<String, List<String>>> entry : bioAndCaptionsForAllUsers.entrySet()) {
            String openAiQuestionString = prepareOpenAiQuestionString(entry.getKey(), entry.getValue());
            openAiData.put(entry.getKey(), openAiQuestionString);
        }

        return openAiData;
    }

    private Map<String, Map<String, List<String>>> getBioAndAllCaptionsOfTiktokUsers() throws Exception {
        Map<String, Map<String, List<String>>> bioAndCaptionsPerUser = new HashMap<>();
        Map<String, JSONArray> allPostsForTiktokUsers = getAllPostsForAllTiktokUsers();

        for(Map.Entry<String,JSONArray> entry : allPostsForTiktokUsers.entrySet()) {
            for(Object tiktokVideoObject : entry.getValue()) {
                JSONObject tiktokVideo = (JSONObject) tiktokVideoObject;
                String username = (String) ((JSONObject) tiktokVideo.get("authorMeta")).get("name");
                String bio = (String) ((JSONObject) tiktokVideo.get("authorMeta")).get("signature");
                String caption = (String) tiktokVideo.get("text");

                if(captionContainsDiscountWords(caption)) {
                    if(bioAndCaptionsPerUser.get(username) == null) {
                        bioAndCaptionsPerUser.put(username, new HashMap<>());
                        bioAndCaptionsPerUser.get(username).put("bio", new ArrayList<>());
                        bioAndCaptionsPerUser.get(username).put("captions", new ArrayList<>());
                    }

                    if(bioAndCaptionsPerUser.get(username).get("bio").isEmpty()) {
                        bioAndCaptionsPerUser.get(username).get("bio").add(bio);
                    }

                    bioAndCaptionsPerUser.get(username).get("captions").add(caption);
                }
            }
        }

        return bioAndCaptionsPerUser;
    }

    private String prepareOpenAiQuestionString(String username, Map<String, List<String>> bioAndCaptions) {
        List<String> bioList = bioAndCaptions.get("bio");
        List<String> captionsList = bioAndCaptions.get("captions");
        String bio = bioList.get(0);

        StringBuilder result = new StringBuilder();
        result.append("username:\n");
        result.append(username).append("\n");
        result.append("bio:\n");
        result.append(bio).append("\n");

        result.append("captions:\n");
        for (int i = 0; i < captionsList.size(); i++) {
            result.append(i + 1).append(") ").append(captionsList.get(i)).append("\n");
        }

        String openAiQuestionString = result.toString();
        return openAiQuestionString;
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
