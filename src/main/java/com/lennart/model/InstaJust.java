//package com.lennart.model;
//
//import org.jsoup.Jsoup;
//import org.jsoup.nodes.Document;
//
//import java.util.Comparator;
//import java.util.LinkedHashMap;
//import java.util.Map;
//import java.util.Set;
//import java.util.stream.Collectors;
//
///**
// * Created by LennartMac on 12/12/2020.
// */
//public class InstaJust {
//
//    public static void main(String[] args) throws Exception {
//        new InstaJust().testMethod();
//    }
//
//    private void testMethod() throws Exception {
//        Document document = Jsoup.connect("https://instajust.com/profile/queenofjetlags").get();
//        String fullHtml = document.html();
//
//        Set<String> kortingWords = new Picuki().identifyKortingWordsUsed(fullHtml);
//
//        Map<String, String> kortingWordLastPostTimes = getKortingWordLastPostTimes(fullHtml, kortingWords);
//
//        kortingWordLastPostTimes = kortingWordLastPostTimes.entrySet()
//                .stream()
//                .sorted(Map.Entry.comparingByValue(Picuki.LastPostTimeComparator()))
//                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
//
//        String kortingWord = kortingWordLastPostTimes.keySet().stream().collect(Collectors.toList()).get(0);
//
//        String fullKortingPostText = getFullKortingPostText(fullHtml, kortingWord);
//
//        System.out.println("wacht");
//
//    }
//
//    private String getFullKortingPostText(String fullHtml, String kortingsWord) throws Exception {
//        String partOfHtmlBeforeKortingsWord = fullHtml.substring(0, fullHtml.indexOf(kortingsWord));
//        String partOfHtmlAfterKortingsWord = fullHtml.substring(fullHtml.indexOf(kortingsWord), fullHtml.length());
//
//        String firstHalfOfKortingPostText = partOfHtmlBeforeKortingsWord.substring
//                (partOfHtmlBeforeKortingsWord.lastIndexOf("<span>") + 5, partOfHtmlBeforeKortingsWord.length());
//
//        String secondHalfOfKortingPostText = partOfHtmlAfterKortingsWord.substring
//                (0, partOfHtmlAfterKortingsWord.indexOf("</span>"));
//
//        String fullKortingPostText = firstHalfOfKortingPostText + secondHalfOfKortingPostText;
//        return fullKortingPostText;
//    }
//
//    private Map<String, String> getKortingWordLastPostTimes(String bodyText, Set<String> kortingsWordsOnPage) {
//        return new Picuki().getKortingWordLastPostTimes(bodyText, kortingsWordsOnPage, "<div class=\"article_time\">");
//    }
//}
