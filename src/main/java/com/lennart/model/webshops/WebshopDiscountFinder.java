package com.lennart.model.webshops;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.*;

public class WebshopDiscountFinder {

    public static void main(String[] args) throws Exception {
        new WebshopDiscountFinder().testMethod();
    }

    private void testMethod() throws Exception {
        String html = getHtmlForShop("https://www.ikea.com/nl/nl/");

        Map<String, List<String>> result = extractDiscountSnippets(html, 20);

        for (Map.Entry<String, List<String>> entry : result.entrySet()) {
            System.out.println("Term: " + entry.getKey());
            for (String snippet : entry.getValue()) {
                System.out.println(" - " + snippet);
            }
        }
    }

    public Map<String, List<String>> extractDiscountSnippets(String shopHtmlText, int contextLength) {
        Map<String, List<String>> result = new HashMap<>();
        List<String> searchTerms = getDiscountSearchTerms();
        String lowerShopHtmlText = shopHtmlText.toLowerCase();

        for (String term : searchTerms) {
            List<String> snippets = new ArrayList<>();
            String lowerTerm = term.toLowerCase();
            int index = 0;

            while ((index = lowerShopHtmlText.indexOf(lowerTerm, index)) != -1) {
                int start = Math.max(0, index - contextLength);
                int end = Math.min(shopHtmlText.length(), index + term.length() + contextLength);
                String snippet = shopHtmlText.substring(start, end);
                snippets.add(snippet);
                index = index + term.length();
            }

            if(!snippets.isEmpty()) {
                result.put(term, snippets);
            }
        }

        return result;
    }

    private List<String> getDiscountSearchTerms() {
        return Arrays.asList(
                "korting",
                "discount",
                "sale",
                "% off",
                "%off",
                "met code",
                "with code",
                "code:"
                );
    }

    private String getHtmlForShop(String url) throws Exception {
        Document document = Jsoup.connect(url).get();
        document.select("script, style, noscript, head, meta, link").remove();
        return document.body().text();
    }
}
