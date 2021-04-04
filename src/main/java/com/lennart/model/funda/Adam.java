package com.lennart.model.funda;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by LennartMac on 28/06/2020.
 */
public class Adam {

//    public static void main(String[] args) throws Exception {
//        new Adam().urlMethod(null);
//    }

    private void firstMethod(List<String> letters) throws Exception {
        Map<String, String> companyNameArticleMap = new LinkedHashMap<>();

        for(String letter : letters) {
            Document document = Jsoup.connect("http://news.bio-based.eu/suppliers/?nl=" + letter).get();

            Elements companies = document.select("ul.content > li");

            for(Element company : companies) {
                String text = company.text();

                String companyString = text.substring(0, text.lastIndexOf(" "));

                String numberOfArticles = text.substring(text.lastIndexOf(" "), text.length());
                numberOfArticles = removeAllNonNumericCharacters(numberOfArticles);

                companyNameArticleMap.put(companyString, numberOfArticles);
            }

            System.out.println(letter);
        }

        System.out.println();
        System.out.println();

        companyNameArticleMap.entrySet().stream().forEach(entry -> {
            System.out.println(entry.getKey());
        });

        System.out.println();
        System.out.println();
        System.out.println();
        System.out.println();

        companyNameArticleMap.entrySet().stream().forEach(entry -> {
            System.out.println(entry.getValue());
        });
    }

    private void urlMethod(List<String> letters) throws Exception {
        Document document = Jsoup.connect("http://news.bio-based.eu/suppliers/?nl=B").get();
        Elements companies = document.select("ul.content > li > a");

        int counter = 0;
        boolean toStart = true;

        for(Element company : companies) {
//            if(!toStart) {
//                System.out.println(counter++);
//            }

            String url = company.attr("href");

//            if(url.contains("algae-biomass-organization-abo")) {
//                toStart = true;
//            }

            if(toStart) {
                try {
                    Document innnerDocument = Jsoup.connect(url).get();

                    Elements h2Elements = innnerDocument.select("h2 > a");

                    String companyWebsite = h2Elements.attr("href");

                    if(companyWebsite == null || companyWebsite.isEmpty() || StringUtils.isBlank(companyWebsite)) {
                        companyWebsite = "empty";
                    }

                    System.out.println(companyWebsite);
                } catch (Exception e) {
                    System.out.println("error");
                }
            }
        }
    }


    private List<String> getAllLettersOfAlphabeth() {
        return Arrays.asList(
                "A",
                "B",
                "C",
                "D",
                "E",
                "F",
                "G",
                "H",
                "I",
                "J",
                "K",
                "L",
                "M",
                "N",
                "O",
                "P",
                "Q",
                "R",
                "S",
                "T",
                "U",
                "V",
                "W",
                "X",
                "Y",
                "Z"
        );
    }

    private String removeAllNonNumericCharacters(String string) {
        String stringToReturn = string.replaceAll("[^\\d.]", "");

        if(stringToReturn.startsWith(".")) {
            stringToReturn = "0" + stringToReturn;
        }

        return stringToReturn;
    }
}
