package com.lennart.model;


import java.sql.*;
import java.util.*;

public class Analysis {

    private Connection con;

    public static void main(String[] args) throws Exception {
        System.out.println(new InstaAccounts().getAllInstaAccounts().size() - new InstaAccounts().getAllInstaAccountsNew().size());
    }

    private void overallMethod() throws Exception {
        List<String> allKortingGiversFromDb = getListOfAllKortingGivers();

        for(String kortingGiver : allKortingGiversFromDb) {
            System.out.println("users.add(\"" + kortingGiver + "\");");
        }

        //List<String> allKortingGiversFromSite = getListOfAllKortingGiversFromSite();

        //System.out.println();

    }

    private List<String> getListOfAllKortingGiversFromSite() throws Exception {
        List<String> listOfAllKortingGiversFromSite = new ArrayList<>();

        InstaAccounts instaAccounts = new InstaAccounts();
        List<String> allAccounts = instaAccounts.getAllInstaAccounts();

        for(String user : allAccounts) {
            if(isKortingGiver(user)) {
                listOfAllKortingGiversFromSite.add(user);
            }
        }

        return listOfAllKortingGiversFromSite;
    }

    private boolean isKortingGiver(String username) throws Exception {
        KortingIdentifierPersister kortingIdentifierPersister = new KortingIdentifierPersister();

        String fullHtml = kortingIdentifierPersister.getFullHtmlForUsername("picuki", username);
        Set<String> kortingWords = kortingIdentifierPersister.identifyKortingWordsUsed(fullHtml);

        return !kortingWords.isEmpty();
    }

    private List<String> getListOfAllKortingGivers() throws Exception {
        Set<String> kortingGivers = new HashSet<>();

        initializeDbConnection();

        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM all_korting;");

        while(rs.next()) {
            kortingGivers.add(rs.getString("username"));
        }

        rs.close();
        st.close();

        closeDbConnection();

        List<String> kortingGiversAsList = new ArrayList<>();

        kortingGiversAsList.addAll(kortingGivers);

        Collections.sort(kortingGiversAsList);

        return kortingGiversAsList;
    }

    private List<String> getAccountsThatDidntGiveKorting() throws Exception {
        List<String> kortingGivers = getListOfAllKortingGivers();
        List<String> allAccounts = new InstaAccounts().getAllInstaAccounts();

        allAccounts.removeAll(kortingGivers);

        Collections.sort(allAccounts);

        System.out.println("wacht");

        return null;
    }

    private void getKortingGiverFrequencyMap() throws Exception {
        List<String> kortingGivers = new ArrayList<>();

        initializeDbConnection();

        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM all_korting;");

        while(rs.next()) {
            kortingGivers.add(rs.getString("username"));
        }

        rs.close();
        st.close();

        closeDbConnection();

        /////

        Set<String> kortingGiversAsSet = new HashSet<>();
        kortingGiversAsSet.addAll(kortingGivers);

        Map<String, Integer> usernameAndFreq = new HashMap<>();

        for(String username : kortingGiversAsSet) {
            usernameAndFreq.put(username, Collections.frequency(kortingGivers, username));
        }

        usernameAndFreq = sortByValueHighToLow(usernameAndFreq);

        System.out.println("wacht");
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

    private void initializeDbConnection() throws Exception {
        Class.forName("com.mysql.jdbc.Driver").newInstance();
        con = DriverManager.getConnection("jdbc:mysql://localhost:3306/influencers?&serverTimezone=UTC", "root", "");
    }

    private void closeDbConnection() throws SQLException {
        con.close();
    }
}
