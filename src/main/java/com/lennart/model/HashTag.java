package com.lennart.model;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Document;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

/**
 * Created by LennartMac on 08/07/2022.
 */
public class HashTag {

    public static void main(String[] args) throws Exception {
        HashTag hashTag = new HashTag();
        hashTag.checkHashTagPage("tag", 24);
    }

    private void checkHashTagPage(String hashTag, int maxHoursOld) throws Exception {
        String html = getFullHtmlForHashtag(hashTag);
        Set<String> kortingsWords = identifyKortingsWords(html);
        Map<String, String> timestampMap = identifyTimeStampPerKortingsWord(kortingsWords, html);
        Map<LocalDateTime, String> postMap = identifyPostsPerDateTime(timestampMap, html);
        Map<LocalDateTime, String> dateFilteredPostMap = filterOutOldPosts(postMap, getOldestAllowedDateTime(maxHoursOld));
        printData(dateFilteredPostMap);
    }

    private Set<String> identifyKortingsWords(String fullHtmlText) throws Exception {
        Set<String> kortingsWordsPresentOnPage = new HashSet<>();

        if(StringUtils.containsIgnoreCase(fullHtmlText, "korting")) {
            kortingsWordsPresentOnPage.add("korting");
        }

        if(StringUtils.containsIgnoreCase(fullHtmlText, "discount")) {
            kortingsWordsPresentOnPage.add("discount");
        }

        if(StringUtils.containsIgnoreCase(fullHtmlText, "% off")) {
            kortingsWordsPresentOnPage.add("% off");
        }

        if(StringUtils.containsIgnoreCase(fullHtmlText, "%off")) {
            kortingsWordsPresentOnPage.add("%off");
        }

        if(StringUtils.containsIgnoreCase(fullHtmlText, "my code")) {
            kortingsWordsPresentOnPage.add("my code");
        }

        if(StringUtils.containsIgnoreCase(fullHtmlText, "mijn code")) {
            kortingsWordsPresentOnPage.add("mijn code");
        }

        if(StringUtils.containsIgnoreCase(fullHtmlText, "de code")) {
            kortingsWordsPresentOnPage.add("de code");
        }

        if(StringUtils.containsIgnoreCase(fullHtmlText, "code:")) {
            kortingsWordsPresentOnPage.add("code:");
        }

        if(StringUtils.containsIgnoreCase(fullHtmlText, "with code")) {
            kortingsWordsPresentOnPage.add("with code");
        }

        if(StringUtils.containsIgnoreCase(fullHtmlText, "met code")) {
            kortingsWordsPresentOnPage.add("met code");
        }

        if(StringUtils.containsIgnoreCase(fullHtmlText, "use code")) {
            kortingsWordsPresentOnPage.add("use code");
        }

        if(StringUtils.containsIgnoreCase(fullHtmlText, "gebruik code")) {
            kortingsWordsPresentOnPage.add("gebruik code");
        }

        if(StringUtils.containsIgnoreCase(fullHtmlText, "werbung")) {
            kortingsWordsPresentOnPage.add("werbung");
        }

        if(StringUtils.containsIgnoreCase(fullHtmlText, "anzeige")) {
            kortingsWordsPresentOnPage.add("anzeige");
        }

        if(StringUtils.containsIgnoreCase(fullHtmlText, "rabatt")) {
            kortingsWordsPresentOnPage.add("rabatt");
        }

        if(StringUtils.containsIgnoreCase(fullHtmlText, "le code")) {
            kortingsWordsPresentOnPage.add("le code");
        }

        if(StringUtils.containsIgnoreCase(fullHtmlText, "remise")) {
            kortingsWordsPresentOnPage.add("remise");
        }

        if(StringUtils.containsIgnoreCase(fullHtmlText, "réduction")) {
            kortingsWordsPresentOnPage.add("réduction");
        }

        if(StringUtils.containsIgnoreCase(fullHtmlText, "reduction")) {
            kortingsWordsPresentOnPage.add("reduction");
        }

        return kortingsWordsPresentOnPage;
    }

    private Map<String, String> identifyTimeStampPerKortingsWord(Set<String> kortingsWords, String fullHtmlText) {
        Map<String, String> wordsWithTimeStamp = new HashMap<>();

        for(String kortingWord : kortingsWords) {
            String fullHtmlCopy = fullHtmlText;

            while(fullHtmlCopy.contains(kortingWord)) {
                String timeStamp = fullHtmlCopy.toLowerCase();
                timeStamp = timeStamp.substring(timeStamp.indexOf(kortingWord));
                timeStamp = timeStamp.substring(timeStamp.indexOf("taken_at_timestamp") + 20, timeStamp.indexOf("taken_at_timestamp") + 30);

                if(StringUtils.isNumeric(timeStamp)) {
                    wordsWithTimeStamp.put(timeStamp, kortingWord);
                }

                fullHtmlCopy = fullHtmlCopy.replaceFirst(kortingWord, "prrt");
            }
        }

        return wordsWithTimeStamp;
    }

    private Map<LocalDateTime, String> identifyPostsPerDateTime(Map<String, String> timestampMap, String fullHtml) {
        Map<LocalDateTime, String> postsDateTime = new TreeMap<>(Collections.reverseOrder());

        for(Map.Entry<String, String> entry : timestampMap.entrySet()) {
            String fullPostText = getFullPostText(fullHtml, entry.getKey());
            String url = getUrlForTimestamp(fullHtml, entry.getKey());
            LocalDateTime postDateTime = convertEpochToDate(entry.getKey() + "000");
            String fullPostTextPlusUrl = fullPostText + "\n" + url;
            postsDateTime.put(postDateTime, fullPostTextPlusUrl);
        }

        return postsDateTime;
    }

    private String getFullPostText(String fullHtml, String timeStamp) {
        String fullPostText = fullHtml.substring(0, fullHtml.indexOf(timeStamp));
        fullPostText = fullPostText.substring(fullPostText.lastIndexOf("\"text\":") + 8);
        fullPostText = fullPostText.substring(0, fullPostText.indexOf("\"shortcode\":") - 6);

        fullPostText = fullPostText.replace("\\n", "");

        while(fullPostText.contains("\\u")) {
            int index = fullPostText.indexOf("\\u");
            fullPostText = changeCharInPosition(index, ' ', fullPostText);
            fullPostText = changeCharInPosition(index + 1, ' ', fullPostText);
            fullPostText = changeCharInPosition(index + 2, ' ', fullPostText);
            fullPostText = changeCharInPosition(index + 3, ' ', fullPostText);
            fullPostText = changeCharInPosition(index + 4, ' ', fullPostText);
            fullPostText = changeCharInPosition(index + 5, ' ', fullPostText);
        }

        fullPostText = fullPostText.trim().replaceAll(" +", " ");
        return fullPostText;
    }

    private String getUrlForTimestamp(String fullHtml, String timeStamp) {
        String url = fullHtml;

        url = url.substring(0, url.indexOf(timeStamp));
        url = url.substring(url.lastIndexOf("shortcode"));
        url = url.substring(12, url.indexOf("edge_") - 3);
        url = "https://www.instagram.com/p/" + url + "/";

        return url;
    }

    private Map<LocalDateTime, String> filterOutOldPosts(Map<LocalDateTime, String> unfiltered,
                                                         LocalDateTime oldestAllowedDateTime) {
        Map<LocalDateTime, String> dateFilteredPostsDateTime = new TreeMap<>(Collections.reverseOrder());

        for(Map.Entry<LocalDateTime, String> entry : unfiltered.entrySet()) {
            if(entry.getKey().isAfter(oldestAllowedDateTime)) {
                dateFilteredPostsDateTime.put(entry.getKey(), entry.getValue());
            }
        }

        return dateFilteredPostsDateTime;
    }

    private LocalDateTime getOldestAllowedDateTime(int hoursOld) {
        long currentTimeStamp = new Date().getTime();
        int millisecondsToDistract = hoursOld * 3_600_000;
        long oldestAllowedTimestamp = currentTimeStamp - millisecondsToDistract;
        String stampAsString = String.valueOf(oldestAllowedTimestamp);
        LocalDateTime oldestAllowedLdt = convertEpochToDate(stampAsString);
        return oldestAllowedLdt;
    }

    private void printData(Map<LocalDateTime, String> postDateTimeMap) {
        for(Map.Entry<LocalDateTime, String> entry : postDateTimeMap.entrySet()) {
            System.out.println(entry.getKey());
            System.out.println(entry.getValue());
            System.out.println();
            System.out.println();
        }
    }

    public String changeCharInPosition(int position, char ch, String str){
        char[] charArray = str.toCharArray();
        charArray[position] = ch;
        return new String(charArray);
    }

    private LocalDateTime convertEpochToDate(String epochString) {
        long epoch = Long.valueOf(epochString);

        LocalDateTime ldt = Instant.ofEpochMilli(epoch)
                .atZone(ZoneId.systemDefault()).toLocalDateTime();

        return ldt;
    }

    public String getFullHtmlForHashtag(String hashtag) throws Exception {
        Document document = SSLHelper.getConnection("https://www.instagram.com/explore/tags/" + hashtag).get();
        return document.html();
    }

    private String getDummyInstaHtml() throws Exception {
        File file = new File("/Users/LennartMac/Documents/Projects/insta/src/main/resources/static/dummyhashtaghtml.txt");

        String fileText = "";
        try (Reader fileReader = new FileReader(file)) {
            BufferedReader bufReader = new BufferedReader(fileReader);

            String line = bufReader.readLine();

            while (line != null) {
                fileText = fileText + line;
                line = bufReader.readLine();
            }

            bufReader.close();
            fileReader.close();
        }

        return fileText;
    }

    private <K, V extends Comparable<? super V>> Map<K, V> sortByValueHighToLow(Map<K, V> map) {
        List<Map.Entry<K, V>> list = new LinkedList<>( map.entrySet() );
        Collections.sort(list, new Comparator<Map.Entry<K, V>>() {
            @Override
            public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2) {
                return (o2.getValue() ).compareTo( o1.getValue());
            }
        });

        Map<K, V> result = new LinkedHashMap<>();
        for (Map.Entry<K, V> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }
}
