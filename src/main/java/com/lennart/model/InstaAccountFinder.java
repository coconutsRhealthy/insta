package com.lennart.model;

import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

public class InstaAccountFinder {

    private Connection con;

    public static void main(String[] args) throws Exception {
        List<String> influencersForApify = new InstaAccountFinder().getInfluencersForApify();

        for(String influencer : influencersForApify) {
            System.out.println("\"https://www.instagram.com/" + influencer + "\",");
        }
    }

    private List<String> getInfluencersForApify() throws Exception {
        List<String> premiumInfluencers = getInfluencers(
                Arrays.asList("nakdfashion", "gutsgusto", "burga", "sellpy", "bjornborg", "stronger",
                        "geurwolkje", "leolive", "hellofresh.nl", "emmasleepnl", "ginatricot", "loavies", "esuals.nl",
                        "maniacnails", "begoldennl", "zonnebrillen.com", "bodyandfit.com", "gymshark", "aybl", "snuggs",
                        "myproteinnl", "shein", "albelli", "terstal", "photowall_sweden", "loungeunderwear", "stevemaddeneu"),
                "2023-04-01",
                2
        );

        List<String> recentInfluencers = getInfluencers(
                Arrays.asList("nakdfashion", "gutsgusto", "burga", "sellpy", "bjornborg", "stronger",
                        "geurwolkje", "leolive", "hellofresh.nl", "emmasleepnl", "ginatricot", "loavies", "snuggs"),
                "2024-02-01",
                1
        );

        Collections.shuffle(recentInfluencers);

        List<String> influencersToUse = new ArrayList<>(premiumInfluencers);

        for(String recentInfluencer : recentInfluencers) {
            if(influencersToUse.size() == 369) {
                break;
            }

            if(!influencersToUse.contains(recentInfluencer)) {
                influencersToUse.add(recentInfluencer);
            }
        }

        Collections.sort(influencersToUse);
        return influencersToUse;
    }

    private List<String> getInfluencers(List<String> brands, String dateLimit, int minimumPresenceInLists) throws Exception {
        List<List<String>> companyInfluencersLists = new ArrayList<>();

        for(String company : brands) {
            companyInfluencersLists.add(getInfluencersForBrand(company, dateLimit));
        }

        List<String> selectedInfluencers = getInfluencersPresentInMultipleLists(companyInfluencersLists, minimumPresenceInLists);
        return selectedInfluencers;
    }

    private List<String> getInfluencersForBrand(String brand, String dateLimit) throws Exception {
        Set<String> influencersSet = new HashSet<>();

        initializeDbConnection();

        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM discounts WHERE company = '" + brand + "' AND date >= '" + dateLimit + "';");

        while(rs.next()) {
            influencersSet.add(rs.getString("influencer"));
        }

        rs.close();
        st.close();

        closeDbConnection();

        List<String> influencers = new ArrayList<>(influencersSet);
        Collections.sort(influencers);
        return influencers;
    }

    public List<String> getInfluencersPresentInMultipleLists(List<List<String>> companyInfluencersLists, int minimumPresenceInLists) {
        return companyInfluencersLists.stream()
                .flatMap(List::stream)
                .collect(Collectors.groupingBy(e -> e, Collectors.counting()))
                .entrySet().stream()
                .filter(entry -> entry.getValue() >= minimumPresenceInLists)
                .map(Map.Entry::getKey)
                .sorted()
                .collect(Collectors.toList());
    }

    private void initializeDbConnection() throws Exception {
        Class.forName("com.mysql.jdbc.Driver");
        con = DriverManager.getConnection("jdbc:mysql://localhost:3306/diski?&serverTimezone=UTC", "root", "");
    }

    private void closeDbConnection() throws SQLException {
        con.close();
    }
}
