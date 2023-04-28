package com.lennart.model;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Document;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by LennartMac on 13/07/2022.
 */
public class HashTagPicuki {

//    nakdfashion
//    idealofsweden
//    desenio
//    loavies
//    hunkemoller
//    sheingals
//    veromoda
//    icaniwill
//    strongerlabel
//    gutsgusto
//    chiquelle
//    safirashine
//    tessvfashion
//    myjewellery
//    kidsbrandstore
//    romwe_fun
//    famousstore
//    editedofficial
//    bymusthaves
//    airup


    public static void main(String[] args) throws Exception {
        new HashTagPicuki().checkHashTagPage();
    }

    private void checkHashTagPage() throws Exception {
        //Map<String, Map<String, String>> data = getData("/Users/LennartMac/Documents/Projects/insta/src/main/resources/static/picukidummyhashtag.txt");
        Map<String, Map<String, String>> data = getData(new File("/Users/LennartMac/Documents/picuki/rosaliaguzman9.html"));
        printEverything(data.get("kortingPostsWithIdentifier"), data.get("times"), data.get("urls"));
    }

    public Map<String, Map<String, String>> getData(File file) throws Exception {
        String html = getDummyInstaHtml(file);
        Set<String> kortingsWords = identifyKortingsWords(html);
        Map<String, String> kortingPostsWithIdentifier = getKortingPostsAndIdentifiers(kortingsWords, html);
        Map<String, String> times = getTimeStringPerPostIdentifier(kortingPostsWithIdentifier.keySet(), html);
        Map<String, String> urls = getPostUrlPerPostIdentifier(kortingPostsWithIdentifier.keySet(), html);
        Map<String, Map<String, String>> data = new HashMap<>();
        data.put("kortingPostsWithIdentifier", kortingPostsWithIdentifier);
        data.put("times", times);
        data.put("urls", urls);
        return data;
    }

    private Map<String, String> getKortingPostsAndIdentifiers(Set<String> kortingsWords, String fullHtml) throws Exception {
        Set<String> kortingPosts = new HashSet<>();

        for(String word : kortingsWords) {
            String htmlCopy = fullHtml;

            while(htmlCopy.contains(word)) {
                String postText = getFullKortingPostText(htmlCopy, word);
                postText = cleanHtmlFromDiscountPosttext(postText);

                if(!postText.contains("prrt")) {
                    kortingPosts.add(postText);
                }

                htmlCopy = htmlCopy.replaceFirst(word, "prrt");
            }
        }

        Map<String, String> postsWithIdentifier = new HashMap<>();

        for(String post : kortingPosts) {
            String identifierPart = post.substring(0, 60);
            String postWithoutIdentifier = post.substring(70);
            postsWithIdentifier.put(identifierPart, postWithoutIdentifier);
        }

        return postsWithIdentifier;
    }

    private Map<String, String> getTimeStringPerPostIdentifier(Set<String> identifiers, String fullHtml) {
        fullHtml = fullHtml.toLowerCase();
        Map<String, String> timeStringPerIdentifier = new HashMap<>();

        for(String identifier : identifiers) {
            String timeAgoOfPost = fullHtml.substring(fullHtml.indexOf(identifier));
            timeAgoOfPost = timeAgoOfPost.substring(timeAgoOfPost.indexOf("div class=\"time\""));
            timeAgoOfPost = timeAgoOfPost.substring(timeAgoOfPost.indexOf("<span>") + 6, timeAgoOfPost.indexOf("</span>"));
            timeStringPerIdentifier.put(identifier, timeAgoOfPost);
        }

        timeStringPerIdentifier = timeStringPerIdentifier.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue(new LastPostTimeComparator()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));

        return timeStringPerIdentifier;
    }

    private Map<String, String> getPostUrlPerPostIdentifier(Set<String> identifiers, String fullHtml) {
        fullHtml = fullHtml.toLowerCase();
        Map<String, String> urlPerIdentifier = new HashMap<>();

        for(String identifier : identifiers) {
            try {
                String url = fullHtml.substring(0, fullHtml.indexOf(identifier));
                url = url.substring(url.lastIndexOf("<a href"));
                url = url.substring(0, url.indexOf("<img class"));
                url = url.substring(9);
                url = url.replace(" ", "");
                url = url.replace("\">", "");
                urlPerIdentifier.put(identifier, url);
            } catch (Exception e) {
                System.out.println("url identifier exception");
                urlPerIdentifier.put(identifier, "unknown");
            }
        }

        return urlPerIdentifier;
    }

    private void printEverything(Map<String, String> kortingPostsWithIdentifier, Map<String, String> times,
                                 Map<String, String> urls) {
        for(Map.Entry<String, String> entry : times.entrySet()) {
            String postText = kortingPostsWithIdentifier.get(entry.getKey());
            String url = urls.get(entry.getKey());
            String time = entry.getValue();

            System.out.print(time + "   ");
            System.out.println(postText);
            System.out.println(url);
            System.out.println();
            System.out.println();
        }
    }

    private String getFullKortingPostText(String fullHtml, String kortingsWord) throws Exception {
        kortingsWord = kortingsWord.toLowerCase();
        fullHtml = fullHtml.toLowerCase();

        String partOfHtmlBeforeKortingsWord = fullHtml.substring(0, fullHtml.indexOf(kortingsWord));
        String partOfHtmlAfterKortingsWord = fullHtml.substring(fullHtml.indexOf(kortingsWord), fullHtml.length());

        String startPostHtmlIndicator = "alt=";
        String endPostHtmlIndicator = "\">";

        String firstHalfOfKortingPostText = partOfHtmlBeforeKortingsWord.substring
                (partOfHtmlBeforeKortingsWord.lastIndexOf(startPostHtmlIndicator) - 65, partOfHtmlBeforeKortingsWord.length());

        String secondHalfOfKortingPostText = partOfHtmlAfterKortingsWord.substring
                (0, partOfHtmlAfterKortingsWord.indexOf(endPostHtmlIndicator));

        String fullKortingPostText = firstHalfOfKortingPostText + secondHalfOfKortingPostText;

        return fullKortingPostText;
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
            fullHtmlText = fullHtmlText.replaceAll("%offset", "eije");

            if(StringUtils.containsIgnoreCase(fullHtmlText, "%off")) {
                kortingsWordsPresentOnPage.add("%off");
            }
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

        if(StringUtils.containsIgnoreCase(fullHtmlText, "dem code")) {
            kortingsWordsPresentOnPage.add("dem code");
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

    private String cleanHtmlFromDiscountPosttext(String fullDiscountPosttext) {
        String cleanedPostText = fullDiscountPosttext.replaceAll("<.*?>", "");
        return cleanedPostText;
    }

    private String getDummyInstaHtml(File file) throws Exception {
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

    public String getFullHtmlForHashtag(String hashtag) throws Exception {
        Document document = SSLHelper.getConnection("https://www.picuki.com/tag/" + hashtag).get();
        return document.html();
    }
}
