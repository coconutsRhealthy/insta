package com.lennart.model;

import com.lennart.controller.Controller;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by LennartMac on 05/06/17.
 */
public class Words30 {

    private Connection con;
    private double numberOfSites = 59.0;

    public static void main(String[] args) throws Exception {




        Words30 words30 = new Words30();

        words30.initializeDbConnection();
        words30.combine(words30.getTop50HighestIncreaseWordCount(), words30.getTop50HighestIncreaseSiteCount());
        words30.closeDbConnection();


        //words30.updateDatabase();




        //words30.copyValueOfColumnsToLeft();
    }

    private void updateDatabase() throws Exception {
        Controller controller = new Controller();

        controller.initializeDocuments();

        Map<String, Integer> occurrenceMapMultiple = controller.testCompareMethodeWordMultiplePerSite();
        Map<String, Integer> occurrenceMapSingle = controller.testCompareMethodeWordOncePerSite();

        Map<String, List<Integer>> combinedMap = joinMaps(occurrenceMapMultiple, occurrenceMapSingle);

        copyValueOfColumnsToLeft();

        for (Map.Entry<String, List<Integer>> entry : combinedMap.entrySet()) {
            double avNoOccurrences = entry.getValue().get(0) / numberOfSites;
            double avPercentageSites = entry.getValue().get(1) / numberOfSites;

            initializeDbConnection();
            storeOrUpdateWordInDatabase("30_words_english", entry.getKey(), avNoOccurrences, avPercentageSites);
            closeDbConnection();
        }

        calculateAndSetNewAverages(combinedMap);
    }

    private void copyValueOfColumnsToLeft() throws Exception {
        initializeDbConnection();

        //delete column1
        Statement st = con.createStatement();
        st.executeUpdate("ALTER TABLE 30_words_english DROP COLUMN 1_av_no_occurr_site;");
        st.executeUpdate("ALTER TABLE 30_words_english DROP COLUMN 1_av_no_sites;");
        st.close();

        //hernoem alle andere columns naar 1 lager

        //st.executeUpdate("ALTER TABLE 30_words_english CHANGE 2_av_no_sites 1_av_no_sites double(10,4);");

        for(int i = 2; i < 31; i++) {
            st = con.createStatement();
            st.executeUpdate("ALTER TABLE 30_words_english CHANGE " + i + "_av_no_occurr_site " + (i - 1) + "_av_no_occurr_site decimal(10,2) DEFAULT '0.00'");
            st.executeUpdate("ALTER TABLE 30_words_english CHANGE " + i + "_av_no_sites " + (i - 1) + "_av_no_sites decimal(10,4) DEFAULT '0.0000'");
            st.close();
        }

        //voeg nieuwe column 30 toe
        st = con.createStatement();
        st.executeUpdate("ALTER TABLE 30_words_english ADD 30_av_no_occurr_site DECIMAL(10,2) DEFAULT '0.00'");
        st.executeUpdate("ALTER TABLE 30_words_english ADD 30_av_no_sites DECIMAL(10,4) DEFAULT '0.0000'");
        st.close();

        closeDbConnection();
    }

    private void calculateAndSetNewAverages(Map<String, List<Integer>> combinedMap) throws Exception {
        for (Map.Entry<String, List<Integer>> entry : combinedMap.entrySet()) {
            initializeDbConnection();
            List<Double> avNoOccurrList = new ArrayList<>();
            List<Double> avNoSitesList = new ArrayList<>();

            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM english_words_new WHERE word = '" + entry.getKey() + "';");

            while(rs.next()) {
                for(int i = 1; i < 31; i++) {
                    double avNoOccur = rs.getDouble(i + "_av_no_occurr_site");
                    avNoOccurrList.add(avNoOccur);

                    double avNoSites = rs.getDouble(i + "_av_no_sites");
                    avNoSitesList.add(avNoSites);
                }
            }

            double avNoOccur = calculateAverageOfList(avNoOccurrList);
            double avNoSites = calculateAverageOfList(avNoSitesList);

            rs.close();
            st.close();

            st = con.createStatement();
            st.executeUpdate("UPDATE 30_words_english SET tot_av_no_occurr_site = '" + avNoOccur + "', tot_av_no_sites = '" + avNoSites + "' WHERE word = '" + entry.getKey() + "'");
            st.close();
            closeDbConnection();
        }
    }

    private double calculateAverageOfList(List<Double> list) {
        double sum = 0;

        for(double d : list) {
            sum = sum + d;
        }
        return sum / list.size();
    }

    private void storeOrUpdateWordInDatabase(String database, String word, double avNoOccurrences, double avNoSites) throws Exception {
        if(isWordInDatabase(database, word)) {
            updateWordInDatabase(database, word, avNoOccurrences, avNoSites);
        } else {
            storeWordInDatabase(database, word, avNoOccurrences, avNoSites);
        }
    }

    public void storeWordInDatabase(String database, String word, double avNoOccurrences, double avNoSites) throws Exception {
        Statement st = con.createStatement();
        st.executeUpdate("INSERT INTO " + database + " (entry, word, 30_av_no_occurr_site, 30_av_no_sites) VALUES ('" + (getHighestIntEntry(database) + 1) + "', '" + word + "', '" + avNoOccurrences + "', '" + avNoSites + "')");
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
        st.executeUpdate("UPDATE " + database + " SET 30_av_no_occurr_site = '" + avNoOccurrences + "', 30_av_no_sites = '" + avNoSites + "' WHERE word = '" + word + "'");
        st.close();
    }

    private void initializeDbConnection() throws Exception {
        Class.forName("com.mysql.jdbc.Driver").newInstance();
        con = DriverManager.getConnection("jdbc:mysql://localhost:3306/words?&serverTimezone=UTC", "root", "");
    }

    private void closeDbConnection() throws SQLException {
        con.close();
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

    private Map<String, List<Integer>> joinMaps(Map<String, Integer> noOccurrences, Map<String, Integer> noSites) {
        Map<String, List<Integer>> joinedMaps = new HashMap<>();

        for (Map.Entry<String, Integer> entry : noOccurrences.entrySet()) {
            List<Integer> listOccurrencesAndSites = new ArrayList<>();

            if(noSites.get(entry.getKey()) != null) {
                listOccurrencesAndSites.add(entry.getValue());
                listOccurrencesAndSites.add(noSites.get(entry.getKey()));
            }

            if(listOccurrencesAndSites.get(0) == null || listOccurrencesAndSites.get(1) == null) {
                System.out.println("wacht");
            }

            joinedMaps.put(entry.getKey(), listOccurrencesAndSites);
        }
        return joinedMaps;
    }

    private Map<String, Double> getTop50HighestIncreaseWordCount() throws Exception {
        Map<String, Double> wordIncreaseMap = new HashMap<>();

        ResultSet rs = getResultSetFromQuery("SELECT * FROM 30_words_english;");

        Map<String, Double> map1 = new HashMap<>();
        Map<String, Double> map2 = new HashMap<>();

        while(rs.next()) {
            map1.put(rs.getString("word"), rs.getDouble("29_av_no_occurr_site"));
            map2.put(rs.getString("word"), rs.getDouble("30_av_no_occurr_site"));
        }

        for (Map.Entry<String, Double> entry : map1.entrySet()) {
            String word = entry.getKey();

            if(map2.get(word) != null) {
                double oldWordPerSite = entry.getValue();
                double newWordPerSite = map2.get(word);

                if(oldWordPerSite >= 0.2 || newWordPerSite >= 0.2) {
                    wordIncreaseMap.put(word, newWordPerSite / oldWordPerSite);
                }
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

    private Map<String, Double> getTop50HighestIncreaseSiteCount() throws SQLException {
        Map<String, Double> wordIncreaseMap = new HashMap<>();

        ResultSet rs = getResultSetFromQuery("SELECT * FROM 30_words_english;");

        Map<String, Double> map1 = new HashMap<>();
        Map<String, Double> map2 = new HashMap<>();

        while(rs.next()) {
            map1.put(rs.getString("word"), rs.getDouble("29_av_no_sites"));
            map2.put(rs.getString("word"), rs.getDouble("30_av_no_sites"));
        }

        for (Map.Entry<String, Double> entry : map1.entrySet()) {
            String word = entry.getKey();

            if(map2.get(word) != null) {
                double oldWordPerSite = entry.getValue();
                double newWordPerSite = map2.get(word);

                if(oldWordPerSite >= 0.04 || newWordPerSite >= 0.04) {
                    wordIncreaseMap.put(word, newWordPerSite / oldWordPerSite);
                }
            }
        }

        Map<String, Double> filteredMap = new HashMap<>();

        for (Map.Entry<String, Double> entry : wordIncreaseMap.entrySet()) {
            if(entry.getValue() >= 2 ) {
                filteredMap.put(entry.getKey(), entry.getValue());
            }
        }

        return HeadlineAnalyst.sortByValue(filteredMap);
        //System.out.println("wacht");

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

    private ResultSet getResultSetFromQuery(String query) throws SQLException {
        Statement st = con.createStatement();
        return st.executeQuery(query);
    }
}
