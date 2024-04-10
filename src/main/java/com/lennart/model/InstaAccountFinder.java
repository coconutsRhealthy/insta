package com.lennart.model;

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

        new InstaAccountFinder().fillInfluListTest();
    }

    private void fillInfluListTest() throws Exception {
        List<String> influencers2024 = getInfluencers("2024-01-01", "2024-12-31");
        List<String> influencersLast3Days = getInfluencers("2024-04-06", "2024-12-31");

        List<String> eligibleInfluencers = influencers2024.stream()
                .filter(i -> !influencersLast3Days.contains(i))
                .collect(Collectors.toList());

        eligibleInfluencers = removeNotToBeUsedInfluencers(eligibleInfluencers);

        List<String> sectors = Arrays.asList("accessoires", "cosmetics", "erotics", "fashion", "food", "homedecoration",
                "jewellery", "lingerie", "other", "sport");

        Map<String, List<String>> influencersPerSector = sectors.stream()
                .collect(Collectors.toMap(sector -> sector, sector -> getInfluencersForSector(sector, "2024-01-01")));

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

        List<String> influencersWithUnknownBranch = eligibleInfluencers.stream()
                .filter(influencer -> influencersPerSector.values().stream()
                        .flatMap(List::stream)
                        .noneMatch(influencer::equals))
                .collect(Collectors.toList());

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
    }

    private long getMaxNumberOfInfluencersForSector(String sector) {
        long maxNumber = 0L;

        switch (sector) {
            case "accessoires":
                maxNumber = 30L;
                break;
            case "cosmetics":
                maxNumber = 19L;
                break;
            case "erotics":
                maxNumber = 1L;
                break;
            case "fashion":
                maxNumber = 125L;
                break;
            case "food":
                maxNumber = 12L;
                break;
            case "homedecoration":
                maxNumber = 27L;
                break;
            case "jewellery":
                maxNumber = 5L;
                break;
            case "lingerie":
                maxNumber = 6L;
                break;
            case "other":
                maxNumber = 23L;
                break;
            case "sport":
                maxNumber = 86L;
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
                "eije"
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

    private void initializeDbConnection() throws Exception {
        Class.forName("com.mysql.jdbc.Driver");
        con = DriverManager.getConnection("jdbc:mysql://localhost:3306/diski?&serverTimezone=UTC", "root", "");
    }

    private void closeDbConnection() throws SQLException {
        con.close();
    }
}
