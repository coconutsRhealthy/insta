package com.lennart.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.*;

/**
 * Created by LennartMac on 31/05/17.
 */
public class HeadlineAnalyst {

    public static Map<Integer, String> getWordsRankedByOccurrence(List<String> allHeadlines) {

        Map<String, Integer> wordsRankedByOccurence = new HashMap<>();

        //make arraylist of all words
        List<String> allWords = new ArrayList<>();

        for(String headline : allHeadlines) {
            headline = headline.toLowerCase();
            allWords.addAll(Arrays.asList(headline.split(" ")));
        }

        //for each of these words count frequency, then put in Map with key the word, value the # of occurrence
        for(String word : allWords) {
            if(wordsRankedByOccurence.get(word) == null) {
                int frequency = Collections.frequency(allWords, word);
                wordsRankedByOccurence.put(word, frequency);
            }
        }

        wordsRankedByOccurence = clearMapOfCommonWords(wordsRankedByOccurence);

        wordsRankedByOccurence = sortByValue(wordsRankedByOccurence);

        System.out.println("wacht");

        return null;
    }

    public static Map<Integer, String> getTwoSubsequentWordsRanksByOccurence(List<String> allHeadlines) {
        Map<String, Integer> twoSubsequentWordsRankedByOccurence = new HashMap<>();

        //make arraylist of all words
        List<String> allTwoSubsequentWords = new ArrayList<>();

        for(String headline : allHeadlines) {
            headline = headline.toLowerCase();

            List<String> headlineWords = Arrays.asList(headline.split(" "));

            for(int i = 0; i < headlineWords.size() - 1; i++) {
                String twoSubsequentWords = headlineWords.get(i) + " " + headlineWords.get(i + 1);
                allTwoSubsequentWords.add(twoSubsequentWords);
            }
        }

        for(String twoSubsequentWords : allTwoSubsequentWords) {
            if(twoSubsequentWordsRankedByOccurence.get(twoSubsequentWords) == null) {
                int frequency = Collections.frequency(allTwoSubsequentWords, twoSubsequentWords);
                twoSubsequentWordsRankedByOccurence.put(twoSubsequentWords, frequency);
            }
        }

        twoSubsequentWordsRankedByOccurence = sortByValue(twoSubsequentWordsRankedByOccurence);

        System.out.println("wacht");

        return null;
    }

    public static void main(String[] args) throws Exception {
        List<String> allHeadlines = retrieveAllHeadlinesFromDatabase("2017-05-30");

//        allHeadlines.add("Sjaak is een koning");
//        allHeadlines.add("Sjaak is de keizer");
//        allHeadlines.add("Sjaak kan een sultan zijn");

        getWordsRankedByOccurrence(allHeadlines);
        getTwoSubsequentWordsRanksByOccurence(allHeadlines);
    }

    private static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
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

    private static List<String> retrieveAllHeadlinesFromDatabase(String date) throws Exception {
        List<String> allHeadlinesFromDatabase = new ArrayList<>();

        Connection con;

        Class.forName("com.mysql.jdbc.Driver").newInstance();
        con = DriverManager.getConnection("jdbc:mysql://localhost:3306/headlines?&serverTimezone=UTC", "root", "");

        Statement st = con.createStatement();
        String sql = ("SELECT * FROM nu_nl_headlines WHERE date LIKE '%" + date + "%' ORDER BY date;");
        ResultSet rs = st.executeQuery(sql);

        while(rs.next()) {
            String retrievedString = rs.getString("headline").replace("'", "''");
            retrievedString = retrievedString.replaceAll("[^A-Za-z0-9 ]", "");
            allHeadlinesFromDatabase.add(retrievedString);
        }

        st = con.createStatement();
        sql = ("SELECT * FROM nos_headlines WHERE date LIKE '%" + date + "%' ORDER BY date;");
        rs = st.executeQuery(sql);

        while(rs.next()) {
            String retrievedString = rs.getString("headline").replace("'", "''");
            retrievedString = retrievedString.replaceAll("[^A-Za-z0-9 ]", "");
            allHeadlinesFromDatabase.add(retrievedString);
        }

        st = con.createStatement();
        sql = ("SELECT * FROM ad_headlines ORDER BY date;");
        rs = st.executeQuery(sql);

        while(rs.next()) {
            String retrievedString = rs.getString("headline").replace("'", "''");
            retrievedString = retrievedString.replaceAll("[^A-Za-z0-9 ]", "");
            allHeadlinesFromDatabase.add(retrievedString);
        }

        st = con.createStatement();
        sql = ("SELECT * FROM telegraaf_headlines WHERE date LIKE '%" + date + "%' ORDER BY date;");
        rs = st.executeQuery(sql);

        while(rs.next()) {
            String retrievedString = rs.getString("headline").replace("'", "''");
            retrievedString = retrievedString.replaceAll("[^A-Za-z0-9 ]", "");
            allHeadlinesFromDatabase.add(retrievedString);
        }

        st = con.createStatement();
        sql = ("SELECT * FROM volkskrant_headlines WHERE date LIKE '%" + date + "%' ORDER BY date;");
        rs = st.executeQuery(sql);

        while(rs.next()) {
            String retrievedString = rs.getString("headline").replace("'", "''");
            retrievedString = retrievedString.replaceAll("[^A-Za-z0-9 ]", "");
            allHeadlinesFromDatabase.add(retrievedString);
        }

        con.close();

        return allHeadlinesFromDatabase;
    }

    private static Map<String, Integer> clearMapOfCommonWords(Map<String, Integer> unClearedMap) {
        Map<String, Integer> clearedMap = new HashMap<>();
        List<String> commonWords = new ArrayList<>();

        commonWords.add("in");
        commonWords.add("van");
        commonWords.add("op");
        commonWords.add("voor");
        commonWords.add("en");
        commonWords.add("met");
        commonWords.add("de");
        commonWords.add("bij");
        commonWords.add("om");
        commonWords.add("door");
        commonWords.add("na");
        commonWords.add("is");
        commonWords.add("het");
        commonWords.add("nieuwe");
        commonWords.add("uit");
        commonWords.add("zich");
        commonWords.add("wil");
        commonWords.add("dood");
        commonWords.add("aan");
        commonWords.add("niet");
        commonWords.add("vs");
        commonWords.add("te");
        commonWords.add("cel");
        commonWords.add("jaar");
        commonWords.add("een");
        commonWords.add("voorkomen");
        commonWords.add("zaak");
        commonWords.add("man");
        commonWords.add("meer");
        commonWords.add("dat");
        commonWords.add("krijgt");
        commonWords.add("geen");
        commonWords.add("moet");
        commonWords.add("tegen");
        commonWords.add("als");
        commonWords.add("naar");
        commonWords.add("weer");
        commonWords.add("eigen");
        commonWords.add("dit");
        commonWords.add("nederlandse");
        commonWords.add("gaat");
        commonWords.add("maar");
        commonWords.add("gewonden");
        commonWords.add("doden");
        commonWords.add("over");
        commonWords.add("er");
        commonWords.add("af");
        commonWords.add("den");
        commonWords.add("opnieuw");
        commonWords.add("zonder");
        commonWords.add("gevonden");
        commonWords.add("witte");
        commonWords.add("deze");
        commonWords.add("zeggen");
        commonWords.add("ook");
        commonWords.add("nu");
        commonWords.add("nodig");
        commonWords.add("die");
        commonWords.add("wordt");
        commonWords.add("eerst");
        commonWords.add("grote");
        commonWords.add("alsnog");
        commonWords.add("kost");
        commonWords.add("wat");
        commonWords.add("schokt");
        commonWords.add("alle");
        commonWords.add("nog");
        commonWords.add("dode");
        commonWords.add("stapt");
        commonWords.add("tijd");
        commonWords.add("doet");
        commonWords.add("eist");
        commonWords.add("vrouw");
        commonWords.add("eis");
        commonWords.add("toch");
        commonWords.add("schept");
        commonWords.add("trekt");
        commonWords.add("dicht");
        commonWords.add("aantal");
        commonWords.add("vindt");
        commonWords.add("ze");
        commonWords.add("dan");
        commonWords.add("");
        commonWords.add("liveblog");
        commonWords.add("onderzoek");
        commonWords.add("verdachte");
        commonWords.add("duitsland");
        commonWords.add("nederland");
        commonWords.add("eu");
        commonWords.add("politie");
        commonWords.add("crisis");

        boolean entryShouldBeRemoved = false;

        for (Map.Entry<String, Integer> entry : unClearedMap.entrySet()) {
            for(String commonWord : commonWords) {
                if(entry.getKey().equals(commonWord)) {
                    entryShouldBeRemoved = true;
                    break;
                }
            }

            if(!entryShouldBeRemoved) {
                clearedMap.put(entry.getKey(), entry.getValue());
            }

            entryShouldBeRemoved = false;
        }
        return clearedMap;
    }
}
