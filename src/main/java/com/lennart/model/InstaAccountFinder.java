package com.lennart.model;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.FileReader;
import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

public class InstaAccountFinder {

    private Connection con;

    public static void main(String[] args) throws Exception {
        //List<String> influencersForApify = new InstaAccountFinder().getInfluencersForApify();

        //for(String influencer : influencersForApify) {
        //    System.out.println("\"https://www.instagram.com/" + influencer + "\",");
        //}

        //for(int i = 0; i < 10; i++) {
            InstaAccountFinder instaAccountFinder = new InstaAccountFinder();

            List<String> list1 = instaAccountFinder.fillInfluListTest();
//            List<String> list2 = instaAccountFinder.fillInfluListTest();
//
//            list1.retainAll(list2);
//            System.out.println(list1.size());
        //}

        //new InstaAccountFinder().fillInfluListTest();

        //new InstaAccountFinder().initializeInfluencerDb();
    }

    private List<String> fillInfluListTest() throws Exception {
        List<String> influencersLast3Days = getInfluencers("2024-04-10", "2024-12-31");

        List<String> sectors = Arrays.asList("accessoires", "cosmetics", "fashion", "food", "homedecoration",
                "jewellery", "lingerie", "other", "sport");

        Map<String, List<String>> influencersPerSector = sectors.stream()
                .collect(Collectors.toMap(sector -> sector, sector -> getInfluencersForSector(sector, "2024-01-01")));

        for (List<String> influencerList : influencersPerSector.values()) {
            influencerList.removeAll(influencersLast3Days);
        }

        Collections.shuffle(sectors);

        List<String> influencersToUse = new ArrayList<>();

        for(String sector : sectors) {
            List<String> influencersForSector = influencersPerSector.get(sector);
            Collections.shuffle(influencersForSector);
            List<String> selectedInfluencersForSector = influencersForSector.stream()
                    .filter(influencer -> !influencersToUse.contains(influencer))
                    .limit(getMaxNumberOfInfluencersForSector(sector))
                    .collect(Collectors.toList());
            influencersToUse.addAll(selectedInfluencersForSector);
        }

        List<String> influencers2024 = getInfluencers("2024-01-01", "2024-12-31");

        List<String> eligibleInfluencers = influencers2024.stream()
                .filter(i -> !influencersLast3Days.contains(i))
                .collect(Collectors.toList());

        eligibleInfluencers = removeNotToBeUsedInfluencers(eligibleInfluencers);

        List<String> influencersWithUnknownBranch = eligibleInfluencers.stream()
                .filter(influencer -> influencersPerSector.values().stream()
                        .flatMap(List::stream)
                        .noneMatch(influencer::equals))
                .collect(Collectors.toList());

        influencersWithUnknownBranch = removeNotToBeUsedInfluencers(influencersWithUnknownBranch);
        Collections.shuffle(influencersWithUnknownBranch);

        int targetSize = 369;

        for (String influencer : influencersWithUnknownBranch) {
            if (influencersToUse.size() >= targetSize) {
                break;
            }

            if (!influencersToUse.contains(influencer)) {
                influencersToUse.add(influencer);
            }
        }

        Collections.sort(influencersToUse);

        influencersToUse.stream()
                .map(account -> "\"https://www.instagram.com/" + account + "\",\n")
                .forEach(System.out::print);

        return influencersToUse;
    }

    private long getMaxNumberOfInfluencersForSector(String sector) {
        long maxNumber = 0L;

        switch (sector) {
            case "accessoires":
                maxNumber = 60L;
                break;
            case "cosmetics":
                maxNumber = 40L;
                break;
            case "fashion":
                maxNumber = 100L;
                break;
            case "food":
                maxNumber = 30L;
                break;
            case "homedecoration":
                maxNumber = 40L;
                break;
            case "jewellery":
                maxNumber = 15L;
                break;
            case "lingerie":
                maxNumber = 15L;
                break;
            case "other":
                maxNumber = 30L;
                break;
            case "sport":
                maxNumber = 10L;
                break;
        }

        return maxNumber;
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

    private List<String> getInfluencers(String startDate, String endDate) throws Exception {
        initializeDbConnection();

        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM discounts WHERE date >= '" + startDate + "' AND date < '" + endDate + "';");

        Set<String> influencersSet = new HashSet<>();

        while(rs.next()) {
            influencersSet.add(rs.getString("influencer"));
        }

        List<String> influencers = influencersSet.stream().sorted().collect(Collectors.toList());
        return influencers;
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

    private List<String> getInfluencersForSector(String sector, String dateLimit) {
        try {
            List<String> companiesWithinSector = CompanyFinder.getCompaniesForBranch(sector);

            initializeDbConnection();

            Statement st = con.createStatement();

            String companiesCondition = companiesWithinSector.stream()
                    .map(company -> "company = '" + company + "'")
                    .collect(Collectors.joining(" OR ", "(", ")"));

            String query = "SELECT * FROM discounts WHERE " + companiesCondition + " AND date >= '" + dateLimit + "';";

            ResultSet rs = st.executeQuery(query);

            Set<String> influencersForSectorAsSet = new HashSet<>();

            while(rs.next()) {
                influencersForSectorAsSet.add(rs.getString("influencer"));
            }

            rs.close();
            st.close();

            closeDbConnection();

            List<String> influencersForSector = influencersForSectorAsSet.stream().sorted().collect(Collectors.toList());
            influencersForSector = removeNotToBeUsedInfluencers(influencersForSector);
            return influencersForSector;
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    private List<String> removeNotToBeUsedInfluencers(List<String> currentInfluList) {
        List<String> cleanedInfluList = new ArrayList<>(currentInfluList);

        List<String> usernamesToBeRemoved = Arrays.asList(
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

        cleanedInfluList.removeAll(usernamesToBeRemoved);

        return cleanedInfluList;
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

    private void initializeInfluencerDb() throws Exception {
        List<String> influencers = getInfluencers("2019-01-01", "2025-12-31");

        initializeDbConnection();

        int counter = 0;

        for(String influencer : influencers) {
            int followers = getFollowersForUsernameFromJson(influencer);

            if(followers != -1) {
                Statement st = con.createStatement();

                st.executeUpdate("INSERT INTO influencers (" +
                        "name, " +
                        "followers) " +
                        "VALUES ('" +
                        influencer + "', '" +
                        followers + "'" +
                        ")");
                st.close();
            }

            System.out.println(counter++);
        }

        closeDbConnection();
    }

    private int getFollowersForUsernameFromJson(String username) throws Exception {
        JSONParser jsonParser = new JSONParser();

        JSONArray apifyData = (JSONArray) jsonParser.parse(
                new FileReader("/Users/lennartmac/Downloads/follower_count.json"));

        for(Object apifyDataElement : apifyData) {
            JSONObject followersJson = (JSONObject) apifyDataElement;
            String jsonUsername = (String) followersJson.get("userName");

            if(jsonUsername != null && jsonUsername.equals(username)) {
                Long followersCountLong = (Long) followersJson.get("followersCount");
                return Math.toIntExact(followersCountLong);
            }
        }

        return -1;
    }

    private void initializeDbConnection() throws Exception {
        Class.forName("com.mysql.jdbc.Driver");
        con = DriverManager.getConnection("jdbc:mysql://localhost:3306/diski?&serverTimezone=UTC", "root", "");
    }

    private void closeDbConnection() throws SQLException {
        con.close();
    }
}
