package com.lennart.model;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.sql.*;
import java.util.*;

/**
 * Created by LPO21630 on 13-6-2017.
 */
public class FinanceWords {

    private Connection con;

    private Document document1;
    private Document document2;
    private Document document3;
    private Document document4;
    private Document document5;
    private Document document6;
    private Document document7;
    private Document document8;
    private Document document9;
    private Document document10;
    private Document document11;
    private Document document12;
    private Document document13;
    private Document document14;
    private Document document15;

    private double numberOfSites = 15.0;

//    public static void main(String[] args) throws Exception {
//        FinanceWords financeWords = new FinanceWords();
//        financeWords.compareCurrentWithLastDbEntry();
//    }

    private void compareCurrentWithLastDbEntry() throws Exception {
        initializeDocuments();
        initializeDbConnection();
        combine(getTop50HighestIncreaseWordCountCurrent(), getTop50HighestIncreaseSiteCountCurrent());
        closeDbConnection();
    }

    private void updateDatabase() throws Exception {
        initializeDocuments();

        Map<String, Integer> occurrenceMapMultiple = getNumberOfWordsPerSite();
        Map<String, Integer> occurrenceMapSingle = getPercentageOccurrenceAcrossSites();

        Map<String, List<Integer>> combinedMap = joinMaps(occurrenceMapMultiple, occurrenceMapSingle);

        for (Map.Entry<String, List<Integer>> entry : combinedMap.entrySet()) {
            double avNoOccurrences = entry.getValue().get(0) / numberOfSites;
            double avPercentageSites = entry.getValue().get(1) / numberOfSites;

            initializeDbConnection();
            storeOrUpdateWordInDatabase("market_words", entry.getKey(), avNoOccurrences, avPercentageSites);
            closeDbConnection();
        }
    }

    private void storeOrUpdateWordInDatabase(String database, String word, double avNoOccurrences, double avNoSites) throws Exception {
        if(isWordInDatabase(database, word)) {
            updateWordInDatabase(database, word, avNoOccurrences, avNoSites);
        } else {
            storeWordInDatabase(database, word, avNoOccurrences, avNoSites);
        }
    }

    private void storeWordInDatabase(String database, String word, double avNoOccurrences, double avNoSites) throws Exception {
        Statement st = con.createStatement();
        st.executeUpdate("INSERT INTO " + database + " (entry, word, av_no_occurr_site, av_no_sites) VALUES ('" + (getHighestIntEntry(database) + 1) + "', '" + word + "', '" + avNoOccurrences + "', '" + avNoSites + "')");
        st.close();
    }

    private boolean isWordInDatabase(String database, String word) throws Exception{
        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM " + database + " WHERE word = '" + word + "';");

        if(rs.next()) {
            rs.close();
            st.close();
            return true;
        }
        rs.close();
        st.close();
        return false;
    }

    private void updateWordInDatabase(String database, String word, double avNoOccurrences, double avNoSites) throws Exception {
        Statement st = con.createStatement();
        st.executeUpdate("UPDATE " + database + " SET av_no_occurr_site = '" + avNoOccurrences + "', av_no_sites = '" + avNoSites + "' WHERE word = '" + word + "'");
        st.close();
    }

    private int getHighestIntEntry(String database) throws Exception {
        Statement st = con.createStatement();
        String sql = ("SELECT * FROM " + database + " ORDER BY entry DESC;");
        ResultSet rs = st.executeQuery(sql);

        if(rs.next()) {
            int highestIntEntry = rs.getInt("entry");
            st.close();
            rs.close();
            return highestIntEntry;
        }
        st.close();
        rs.close();
        return 0;
    }

    private void initializeDocuments() throws IOException {
        document1 = Jsoup.connect("http://money.cnn.com/").get();
        document2 = Jsoup.connect("http://www.thisismoney.co.uk/money/index.html").get();
        document3 = Jsoup.connect("https://www.thestreet.com/").get();
        document4 = Jsoup.connect("http://www.marketwatch.com/").get();
        document5 = Jsoup.connect("https://seekingalpha.com/").get();
        document6 = Jsoup.connect("https://www.bloomberg.com/europe").get();
        document7 = Jsoup.connect("http://www.nytimes.com/pages/business/dealbook/index.html").get();
        document8 = Jsoup.connect("https://www.wsj.com/news/markets").get();
        document9 = Jsoup.connect("https://markets.ft.com/data").get();
        document10 = Jsoup.connect("http://www.japantimes.co.jp/news/financial-markets/").get();
        document11 = Jsoup.connect("http://www.scmp.com/business/markets").get();
        document12 = Jsoup.connect("http://www.theaustralian.com.au/business/markets").get();
        document13 = Jsoup.connect("https://www.theguardian.com/business/stock-markets").get();
        document14 = Jsoup.connect("https://washpost.bloomberg.com/market-news/").get();
        document15 = Jsoup.connect("http://www.reuters.com/finance/markets").get();
    }

    private Map<String, Integer> getPercentageOccurrenceAcrossSites() throws Exception {
        Set<String> set1 = getSetOfWordsFromDocument(document1);
        Set<String> set2 = getSetOfWordsFromDocument(document2);
        Set<String> set3 = getSetOfWordsFromDocument(document3);
        Set<String> set4 = getSetOfWordsFromDocument(document4);
        Set<String> set5 = getSetOfWordsFromDocument(document5);
        Set<String> set6 = getSetOfWordsFromDocument(document6);
        Set<String> set7 = getSetOfWordsFromDocument(document7);
        Set<String> set8 = getSetOfWordsFromDocument(document8);
        Set<String> set9 = getSetOfWordsFromDocument(document9);
        Set<String> set10 = getSetOfWordsFromDocument(document10);
        Set<String> set11 = getSetOfWordsFromDocument(document11);
        Set<String> set12 = getSetOfWordsFromDocument(document12);
        Set<String> set13 = getSetOfWordsFromDocument(document13);
        Set<String> set14 = getSetOfWordsFromDocument(document14);
        Set<String> set15 = getSetOfWordsFromDocument(document15);

        List<String> combinedList = new ArrayList<>();
        combinedList.addAll(set1);
        combinedList.addAll(set2);
        combinedList.addAll(set3);
        combinedList.addAll(set4);
        combinedList.addAll(set5);
        combinedList.addAll(set6);
        combinedList.addAll(set7);
        combinedList.addAll(set8);
        combinedList.addAll(set9);
        combinedList.addAll(set10);
        combinedList.addAll(set11);
        combinedList.addAll(set12);
        combinedList.addAll(set13);
        combinedList.addAll(set14);
        combinedList.addAll(set15);

        Map<String, Integer> occurrenceMapAll = new HashMap<>();

        for(String word : combinedList) {
            if(occurrenceMapAll.get(word) == null) {
                int frequency = Collections.frequency(combinedList, word);
                occurrenceMapAll.put(word, frequency);
            }
        }

        return sortByValue(occurrenceMapAll);
    }

    private Map<String, Integer> getNumberOfWordsPerSite() throws Exception {
        List<String> list1 = getListOfWordsFromDocument(document1);
        List<String> list2 = getListOfWordsFromDocument(document2);
        List<String> list3 = getListOfWordsFromDocument(document3);
        List<String> list4 = getListOfWordsFromDocument(document4);
        List<String> list5 = getListOfWordsFromDocument(document5);
        List<String> list6 = getListOfWordsFromDocument(document6);
        List<String> list7 = getListOfWordsFromDocument(document7);
        List<String> list8 = getListOfWordsFromDocument(document8);
        List<String> list9 = getListOfWordsFromDocument(document9);
        List<String> list10 = getListOfWordsFromDocument(document10);
        List<String> list11 = getListOfWordsFromDocument(document11);
        List<String> list12 = getListOfWordsFromDocument(document12);
        List<String> list13 = getListOfWordsFromDocument(document13);
        List<String> list14 = getListOfWordsFromDocument(document14);
        List<String> list15 = getListOfWordsFromDocument(document15);

        List<String> combinedList = new ArrayList<>();
        combinedList.addAll(list1);
        combinedList.addAll(list2);
        combinedList.addAll(list3);
        combinedList.addAll(list4);
        combinedList.addAll(list5);
        combinedList.addAll(list6);
        combinedList.addAll(list7);
        combinedList.addAll(list8);
        combinedList.addAll(list9);
        combinedList.addAll(list10);
        combinedList.addAll(list11);
        combinedList.addAll(list12);
        combinedList.addAll(list13);
        combinedList.addAll(list14);
        combinedList.addAll(list15);

        Map<String, Integer> occurrenceMapAll = new HashMap<>();

        for(String word : combinedList) {
            if(occurrenceMapAll.get(word) == null) {
                int frequency = Collections.frequency(combinedList, word);
                occurrenceMapAll.put(word, frequency);
            }
        }

        return sortByValue(occurrenceMapAll);
    }

    private <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
        List<Map.Entry<K, V>> list = new LinkedList<>( map.entrySet() );
        Collections.sort(list, new Comparator<Map.Entry<K, V>>() {
            @Override
            public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2) {
                return (o1.getValue() ).compareTo( o2.getValue());
            }
        });

        Map<K, V> result = new LinkedHashMap<>();
        for (Map.Entry<K, V> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }

    private List<String> getListOfWordsFromDocument(Document document) {
        String allText = document.text();
        allText = allText.replaceAll("[^A-Za-z0-9 ]", "");
        allText = allText.toLowerCase();

        List<String> listOfWordsTemp = Arrays.asList(allText.split(" "));
        List<String> listOfWords = new ArrayList<>();

        listOfWords.addAll(listOfWordsTemp);

        return listOfWords;
    }

    private Set<String> getSetOfWordsFromDocument(Document document) {
        String allText = document.text();
        allText = allText.replaceAll("[^A-Za-z0-9 ]", "");
        allText = allText.toLowerCase();

        List<String> listOfWordsTemp = Arrays.asList(allText.split(" "));
        List<String> listOfWords = new ArrayList<>();

        listOfWords.addAll(listOfWordsTemp);

        Set<String> setOfWords = new HashSet<>();
        setOfWords.addAll(listOfWords);

        return setOfWords;
    }

    private Map<String, List<Integer>> joinMaps(Map<String, Integer> noOccurrences, Map<String, Integer> noSites) {
        Map<String, List<Integer>> joinedMaps = new HashMap<>();

        for (Map.Entry<String, Integer> entry : noOccurrences.entrySet()) {
            List<Integer> listOccurrencesAndSites = new ArrayList<>();

            if(noSites.get(entry.getKey()) != null) {
                listOccurrencesAndSites.add(entry.getValue());
                listOccurrencesAndSites.add(noSites.get(entry.getKey()));
            }

            joinedMaps.put(entry.getKey(), listOccurrencesAndSites);
        }
        return joinedMaps;
    }

    private Map<String, Double> getTop50HighestIncreaseWordCountCurrent() throws Exception {
        Map<String, Double> wordIncreaseMap = new HashMap<>();

        ResultSet rs = getResultSetFromQuery("SELECT * FROM market_words;");

        Map<String, Double> map1 = new HashMap<>();
        Map<String, Double> map2 = convertIntegerMapToDoubleWithSiteDivide(getNumberOfWordsPerSite());

        while(rs.next()) {
            map1.put(rs.getString("word"), rs.getDouble("av_no_occurr_site"));
        }

        for (Map.Entry<String, Double> entry : map2.entrySet()) {
            String word = entry.getKey();

            double oldWordPerSite;

            if(map1.get(entry.getKey()) != null) {
                oldWordPerSite = map1.get(entry.getKey());
            } else {
                oldWordPerSite = 0;
            }

            double newWordPerSite = entry.getValue();

            if(oldWordPerSite >= 0.2 || newWordPerSite >= 0.2) {
                wordIncreaseMap.put(word, newWordPerSite / oldWordPerSite);
            }
        }

        Map<String, Double> filteredMap = new HashMap<>();

        for (Map.Entry<String, Double> entry : wordIncreaseMap.entrySet()) {
            if(entry.getValue() >= 2) {
                filteredMap.put(entry.getKey(), entry.getValue());
            }
        }

        return HeadlineAnalyst.sortByValue(filteredMap);
    }

    private Map<String, Double> getTop50HighestIncreaseSiteCountCurrent() throws Exception {
        Map<String, Double> wordIncreaseMap = new HashMap<>();

        ResultSet rs = getResultSetFromQuery("SELECT * FROM market_words;");

        Map<String, Double> map1 = new HashMap<>();
        Map<String, Double> map2 = convertIntegerMapToDoubleWithSiteDivide(getPercentageOccurrenceAcrossSites());

        while(rs.next()) {
            map1.put(rs.getString("word"), rs.getDouble("av_no_sites"));
        }

        for (Map.Entry<String, Double> entry : map2.entrySet()) {
            String word = entry.getKey();

            double oldWordPerSite;

            if(map1.get(entry.getKey()) != null) {
                oldWordPerSite = map1.get(entry.getKey());
            } else {
                oldWordPerSite = 0;
            }

            double newWordPerSite = entry.getValue();

            if(oldWordPerSite >= 0.07 || newWordPerSite >= 0.07) {
                wordIncreaseMap.put(word, newWordPerSite / oldWordPerSite);
            }
        }

        Map<String, Double> filteredMap = new HashMap<>();

        for (Map.Entry<String, Double> entry : wordIncreaseMap.entrySet()) {
            if(entry.getValue() >= 2 ) {
                filteredMap.put(entry.getKey(), entry.getValue());
            }
        }

        return HeadlineAnalyst.sortByValue(filteredMap);
    }

    private void initializeDbConnection() throws Exception {
        Class.forName("com.mysql.jdbc.Driver").newInstance();
        con = DriverManager.getConnection("jdbc:mysql://localhost:3306/words?&serverTimezone=UTC", "root", "");
    }

    private void closeDbConnection() throws SQLException {
        con.close();
    }

    private Map<String, Double> convertIntegerMapToDoubleWithSiteDivide(Map<String, Integer> integerMap) {
        Map<String, Double> doubleMap = new HashMap<>();

        for (Map.Entry<String, Integer> entry : integerMap.entrySet()) {
            double d = (double) entry.getValue();
            doubleMap.put(entry.getKey(), d / numberOfSites);
        }
        return doubleMap;
    }

    private ResultSet getResultSetFromQuery(String query) throws SQLException {
        Statement st = con.createStatement();
        return st.executeQuery(query);
    }

    private void combine(Map<String, Double> wordMap, Map<String, Double> siteMap) {
        Map<String, Double> combined = new HashMap<>();

        for (Map.Entry<String, Double> entry : wordMap.entrySet()) {
            if(siteMap.get(entry.getKey()) != null) {
                combined.put(entry.getKey(), entry.getValue());
            }
        }

        for (Map.Entry<String, Double> entry : siteMap.entrySet()) {
            if(combined.get(entry.getKey()) == null) {
                if(wordMap.get(entry.getKey()) != null) {
                    combined.put(entry.getKey(), entry.getValue());
                }
            }
        }

        combined = HeadlineAnalyst.sortByValue(combined);
        System.out.println("wacht");
    }
}
