package com.lennart.model;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.concurrent.CompletionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class CountryIdentifier {

    public static void main(String[] args) throws Exception {
        CountryIdentifier countryIdentifier = new CountryIdentifier();
        countryIdentifier.createGptQuestionString();
    }

    private String createGptQuestionString() throws Exception {
        Map<String, Map<String, List<String>>> allData = getDataForAllUsers("/Users/lennartmac/Downloads/ai_data/all_influencers_including_data_basejson.json");

        int counter = 0;
        OpenAi openAi = new OpenAi();
        InfluencerPersister influencerPersister = new InfluencerPersister();

        for(Map.Entry<String, Map<String, List<String>>> entry : allData.entrySet()) {
            String username = entry.getKey();

            if(influencerPersister.getCountry(username).equals("")) {
                Map<String, List<String>> userDetails = entry.getValue();

                String formattedCaptions = formatList("Recent captions: ", userDetails.get("captions"));
                String formattedHashtags = formatList("Recent hashtags: ", userDetails.get("hashtags"));
                String formattedLocations = formatList("Recent locations: ", userDetails.get("locations"));
                String bio = userDetails.get("bio").get(0);

                String userDetailString = formattedCaptions + formattedHashtags + formattedLocations + "Bio: " + bio;

                String country;

                try {
                    country = openAi.identifyInstaProfileCountry(userDetailString);
                } catch (CompletionException e) {
                    e.printStackTrace();
                    System.out.println("Too many gpt requests...");
                    TimeUnit.SECONDS.sleep(10);
                    country = openAi.identifyInstaProfileCountry(userDetailString);
                }

                influencerPersister.setCountry(username, country);
                String lineToAdd = username + " - " + country + System.lineSeparator();
                Files.write(Paths.get("/Users/lennartmac/Desktop/influencer_countries.txt"), lineToAdd.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
                System.out.println("******* " + counter++ + " *******");
            } else {
                System.out.println("country already set for user: " + username);
            }

//            if(counter >= 5) {
//                break;
//            }
        }

        return null;
    }

    private static String formatList(String header, List<String> items) {
        if (items == null || items.isEmpty()) {
            return header + "None\n";
        }
        String formattedItems = IntStream.range(0, items.size())
                .mapToObj(index -> (index + 1) + ") " + items.get(index))
                .collect(Collectors.joining(" ", header, ""));
        return formattedItems + "\n";
    }

    private Map<String, Map<String, List<String>>> getDataForAllUsers(String filePath) throws Exception {
        Map<String, List<String>> locationsPerUsername = getLocationsPerUsername(filePath);
        Map<String, List<String>> hashtagsUsedPerUsername = getHashtagsUsedPerUsername(filePath);
        Map<String, List<String>> captionsPerUsername = getCaptionsPerUsername(filePath);
        Map<String, String> bioPerUsername = getBioPerUsername(filePath);

        Map<String, Map<String, List<String>>> dataForAllusers = new TreeMap<>();

        for (String username : bioPerUsername.keySet()) {
            Map<String, List<String>> userDetails = new HashMap<>();

            userDetails.put("locations", locationsPerUsername.get(username));
            userDetails.put("hashtags", hashtagsUsedPerUsername.get(username));
            userDetails.put("captions", captionsPerUsername.get(username));
            userDetails.put("bio", bioPerUsername.get(username) != null ? List.of(bioPerUsername.get(username)) : null);  // converting bio string to a single-item list

            dataForAllusers.put(username, userDetails);
        }

        return dataForAllusers;
    }

    private Map<String, List<String>> getLocationsPerUsername(String filePath) throws Exception {
        Map<String, List<String>> locationsPerUsername = new HashMap<>();

        JSONParser jsonParser = new JSONParser();
        JSONArray apifyData = (JSONArray) jsonParser.parse(new FileReader(filePath));

        for(Object apifyDataElement : apifyData) {
            JSONObject influencerJson = (JSONObject) apifyDataElement;
            JSONArray latestPosts = (JSONArray) influencerJson.get("latestPosts");

            if(latestPosts != null) {
                List<String> locations = new ArrayList<>();

                for(Object latestPostElement : latestPosts) {
                    JSONObject latestPostJson = (JSONObject) latestPostElement;
                    String location = (String) latestPostJson.get("locationName");

                    if(location != null) {
                        locations.add(location);
                    }
                }

                if(!locations.isEmpty()) {
                    String username = (String) influencerJson.get("username");
                    locationsPerUsername.put(username, locations);
                }
            }
        }

        return locationsPerUsername;
    }

    private Map<String, List<String>> getHashtagsUsedPerUsername(String filePath) throws Exception {
        Map<String, List<String>> hashtagsPerUsername = new HashMap<>();

        JSONParser jsonParser = new JSONParser();
        JSONArray apifyData = (JSONArray) jsonParser.parse(new FileReader(filePath));

        for(Object apifyDataElement : apifyData) {
            JSONObject influencerJson = (JSONObject) apifyDataElement;
            JSONArray latestPosts = (JSONArray) influencerJson.get("latestPosts");

            if(latestPosts != null) {
                Set<String> hashtags = new HashSet<>();

                for(Object latestPostElement : latestPosts) {
                    JSONObject latestPostJson = (JSONObject) latestPostElement;
                    JSONArray hashtagsJsonArray = (JSONArray) latestPostJson.get("hashtags");

                    for(Object hashtag : hashtagsJsonArray) {
                        hashtags.add((String) hashtag);
                    }
                }

                if(!hashtags.isEmpty()) {
                    List<String> hashTagsList = new ArrayList<>(hashtags);
                    Collections.sort(hashTagsList);
                    String username = (String) influencerJson.get("username");
                    hashtagsPerUsername.put(username, hashTagsList);
                }
            }
        }

        return hashtagsPerUsername;
    }

    private Map<String, List<String>> getCaptionsPerUsername(String filePath) throws Exception {
        Map<String, List<String>> captionsPerUsername = new HashMap<>();

        JSONParser jsonParser = new JSONParser();
        JSONArray apifyData = (JSONArray) jsonParser.parse(new FileReader(filePath));

        for(Object apifyDataElement : apifyData) {
            JSONObject influencerJson = (JSONObject) apifyDataElement;
            JSONArray latestPosts = (JSONArray) influencerJson.get("latestPosts");

            if(latestPosts != null) {
                List<String> captions = new ArrayList<>();

                for(Object latestPostElement : latestPosts) {
                    JSONObject latestPostJson = (JSONObject) latestPostElement;
                    String caption = (String) latestPostJson.get("caption");
                    captions.add(caption);
                }

                if(!captions.isEmpty()) {
                    String username = (String) influencerJson.get("username");
                    captionsPerUsername.put(username, captions);
                }
            }
        }

        return captionsPerUsername;
    }

    private Map<String, String> getBioPerUsername(String filePath) throws Exception {
        Map<String, String> bioPerUsername = new HashMap<>();

        JSONParser jsonParser = new JSONParser();
        JSONArray apifyData = (JSONArray) jsonParser.parse(new FileReader(filePath));

        for(Object apifyDataElement : apifyData) {
            JSONObject influencerJson = (JSONObject) apifyDataElement;
            String bio = (String) influencerJson.get("biography");

            if(bio != null) {
                String username = (String) influencerJson.get("username");
                bioPerUsername.put(username, bio);
            }
        }

        return bioPerUsername;
    }
}
