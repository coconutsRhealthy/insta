package com.lennart.model;

import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

public class InstaAccountFinder {

    private Connection con;

    public static void main(String[] args) throws Exception {
        InstaAccountFinder instaAccountFinder = new InstaAccountFinder();
        instaAccountFinder.printInfluList();
    }

    private void printInfluList() throws Exception {
        Map<String, Integer> influList = fillInfluList();

        influList.keySet().stream()
            .map(account -> "\"https://www.instagram.com/" + account + "\",\n")
            .forEach(System.out::print);
    }

    private Map<String, Integer> fillInfluList() throws Exception {
        Map<String, Integer> influencersToUse = new LinkedHashMap<>();
        Map<String, Integer> recentDutchInfluencers = getRecentInfluencersFromCountry("Netherlands");
        List<String> influencersToBeRemoved = getInfluencersToBeRemoved();
        influencersToBeRemoved.forEach(recentDutchInfluencers.keySet()::remove);

        int counter = 0;

        for(Map.Entry<String, Integer> entry : recentDutchInfluencers.entrySet()) {
            counter++;

            if(counter <= 295) {
                influencersToUse.put(entry.getKey(), entry.getValue());
            } else {
                break;
            }
        }

        recentDutchInfluencers.keySet().removeAll(influencersToUse.keySet());
        Random random = new Random();
        List<String> stillEligibleKeys = recentDutchInfluencers.entrySet().stream()
                .filter(entry -> entry.getValue() >= 1000)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        while(influencersToUse.size() < 369) {
            if(stillEligibleKeys.isEmpty()) {
                System.out.println("Not enough eligible entries in stillEligibleKeys");
                break;
            }
            String randomKey = stillEligibleKeys.remove(random.nextInt(stillEligibleKeys.size()));
            influencersToUse.put(randomKey, recentDutchInfluencers.get(randomKey));
        }

        influencersToUse = influencersToUse.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue, LinkedHashMap::new));

        return influencersToUse;
    }

    public List<String> getInfluencers(String startDate, String endDate) throws Exception {
        initializeDbConnection();

        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM discounts WHERE date >= '" + startDate + "' AND date < '" + endDate + "';");

        Set<String> influencersSet = new HashSet<>();

        while(rs.next()) {
            String influencer = rs.getString("influencer");

            if(!influencer.contains("_tiktok")) {
                influencersSet.add(rs.getString("influencer"));
            }
        }

        rs.close();
        st.close();

        closeDbConnection();

        List<String> influencers = influencersSet.stream().sorted().collect(Collectors.toList());
        return influencers;
    }

    private Map<String, Integer> getAllInfluencersFromCountry(String country) throws Exception {
        Map<String, Integer> influencersFromCountry = new HashMap<>();

        initializeDbConnection();

        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM influencers WHERE country LIKE '%" + country + "%';");

        while(rs.next()) {
            influencersFromCountry.put(rs.getString("name"), rs.getInt("followers"));
        }

        rs.close();
        st.close();

        closeDbConnection();

        influencersFromCountry = influencersFromCountry.entrySet()
                .stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new));

        return influencersFromCountry;
    }

    private Map<String, Integer> getRecentInfluencersFromCountry(String country) throws Exception {
        Map<String, Integer> allInfluencersFromCountry = getAllInfluencersFromCountry(country);
        List<String> allInfluencers2024 = getInfluencers("2024-01-01", "2024-12-31");

        Map<String, Integer> recentDutchInfluencers = allInfluencersFromCountry.entrySet().stream()
                .filter(entry -> allInfluencers2024.contains(entry.getKey()))
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue,
                        LinkedHashMap::new));

        return recentDutchInfluencers;
    }

    private List<String> getInfluencersToBeRemoved() {
        return Arrays.asList(
                "janedoe",
                "wgk",
                "kleinpepertje",
                "rabattk",
                "honey",
                "asos",
                "rabattkodersverige",
                "chiquelle",
                "otrium",
                "zalando",
                "rabatt.inspo",
                "rentishehu",
                "elle",
                "dunno",
                "loavies",
                "gratispandan",
                "rabattkoder.sverige",
                "nike",
                "meetmethere",
                "billigare_sverige",
                "rabattkode.norge",
                "nakdfashion",
                "rabattkodsidan",
                "d.iscount",
                "zonnebrillen.com",
                "eije",
                "aimnsportswear",
                "azukanl",
                "easytoys",
                "herrvonsmit",
                "korting.code"
        );
    }

    private void initializeDbConnection() throws Exception {
        Class.forName("com.mysql.jdbc.Driver");
        con = DriverManager.getConnection("jdbc:mysql://localhost:3306/diski?&serverTimezone=UTC", "root", "");
    }

    private void closeDbConnection() throws SQLException {
        con.close();
    }
}
