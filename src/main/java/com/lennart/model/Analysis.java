package com.lennart.model;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.sql.*;
import java.util.*;

public class Analysis {

    private Connection con;

//    public static void main(String[] args) throws Exception {
//        new Analysis().getDataDirectiveNonDiscountGivers();
//    }

    private void removeNonGiverAccountsFromFullUserList() throws Exception {
        List<String> nonGivers = getDataDirectiveNonDiscountGivers();
        Collections.sort(nonGivers);

        System.out.println("NonGivers complete size: " + nonGivers.size());

        //nonGivers.forEach(username -> System.out.println(username));

        List<String> nonGiversToRetain = new ArrayList<>();

        nonGiversToRetain.add("anoukmaasofficial");
        nonGiversToRetain.add("beaupotman");
        nonGiversToRetain.add("bibibreijman");
        nonGiversToRetain.add("bobbieden");
        nonGiversToRetain.add("daniquebossers");
        nonGiversToRetain.add("fienvermeulen");
        nonGiversToRetain.add("ginasingels.official");
        nonGiversToRetain.add("girlys_blog");
        nonGiversToRetain.add("ikbensaske");
        nonGiversToRetain.add("jaim_x");
        nonGiversToRetain.add("jenniefromtheblog");
        nonGiversToRetain.add("jolielot");
        nonGiversToRetain.add("k2im");
        nonGiversToRetain.add("kelly_weekers");
        nonGiversToRetain.add("liekemartens");
        nonGiversToRetain.add("lisamberr");
        nonGiversToRetain.add("lizasips");
        nonGiversToRetain.add("lizekorpie");
        nonGiversToRetain.add("lizzyperridon");
        nonGiversToRetain.add("lizzyvdligt");
        nonGiversToRetain.add("marijezuurveld");
        nonGiversToRetain.add("mrskeizer");
        nonGiversToRetain.add("ninawarink");
        nonGiversToRetain.add("onnedi");
        nonGiversToRetain.add("roooom");
        nonGiversToRetain.add("roxeannehazes");
        nonGiversToRetain.add("saarkoningsberger");
        nonGiversToRetain.add("stephsa");
        nonGiversToRetain.add("sterrekoning");
        nonGiversToRetain.add("sylvana");
        nonGiversToRetain.add("tassiejs");
        nonGiversToRetain.add("teskedeschepper");
        nonGiversToRetain.add("vivianhoorn");
        nonGiversToRetain.add("xies___");
        nonGiversToRetain.add("yolanthecabau");

        System.out.println("NonGivers to retain size: " + nonGiversToRetain.size());

        nonGivers.removeAll(nonGiversToRetain);

        System.out.println("NonGivers to remove size: " + nonGivers.size());

        List<String> allAccounts = new InstaAccounts().getAllInstaAccounts();

        System.out.println("AllAcconts size: " + allAccounts.size());

        allAccounts.removeAll(nonGivers);

        System.out.println("AllAccounts nongivers removed size: " + allAccounts.size());

        Collections.sort(allAccounts);

        System.out.println();
        System.out.println();
        System.out.println();
        System.out.println();
        System.out.println();

        allAccounts.forEach(user -> System.out.println("users.add(\"" + user + "\");"));


        //System.out.println(new InstaAccounts().getAllInstaAccounts().size() - new InstaAccounts().getAllInstaAccountsNew().size());
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

        String fullHtml = kortingIdentifierPersister.getFullHtmlForUsername(username);
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

    private void getDataDirectiveFreqMap() throws Exception {
        File file = new File("/Users/LennartMac/Documents/Projects/insta/src/main/resources/static/data_directive_okt_jan_2022.txt");

        List<String> lines = readTheFile(file);

        List<String> kortingGivers = new ArrayList<>();

        for(String line : lines) {
            List<String> lineList = Arrays.asList(line.split(","));

            if(lineList.size() > 2) {
                kortingGivers.add(lineList.get(2));
            }
        }

        Set<String> kortingGiversAsSet = new HashSet<>();
        kortingGiversAsSet.addAll(kortingGivers);

        Map<String, Integer> usernameAndFreq = new HashMap<>();

        for(String username : kortingGiversAsSet) {
            usernameAndFreq.put(username, Collections.frequency(kortingGivers, username));
        }

        usernameAndFreq = sortByValueHighToLow(usernameAndFreq);

        System.out.println("wacht");
    }

    private List<String> getDataDirectiveNonDiscountGivers() throws Exception {
        File file = new File("/Users/LennartMac/Documents/Projects/insta/src/main/resources/static/data_directive_feb_may_2022.txt");

        List<String> lines = readTheFile(file);

        List<String> kortingGivers = new ArrayList<>();

        for(String line : lines) {
            List<String> lineList = Arrays.asList(line.split(","));

            if(lineList.size() > 2) {
                String userName = lineList.get(2);
                userName = userName.replaceFirst(" ", "");
                kortingGivers.add(userName);
            }
        }

        Set<String> kortingGiversAsSet = new HashSet<>();
        kortingGiversAsSet.addAll(kortingGivers);

        List<String> allAccounts = new InstaAccounts().getAllInstaAccounts();
        allAccounts.removeAll(kortingGiversAsSet);
        Collections.sort(allAccounts);

        allAccounts.stream().forEach(account -> System.out.println(account));

        return allAccounts;
    }

    private void getCompanyFrequencies() throws Exception {
        File file = new File("/Users/LennartMac/Documents/Projects/insta/src/main/resources/static/data_directive_nov_feb_2022.txt");

        List<String> lines = readTheFile(file);
        Map<String, Double> companiesFrequency = new HashMap<>();

        for(String line : lines) {
            if(!line.isEmpty()) {
                String company = line.substring(2, line.indexOf(","));

                if(companiesFrequency.get(company) == null) {
                    companiesFrequency.put(company, 1.0);
                } else {
                    double currentValue = companiesFrequency.get(company);
                    double newValue = currentValue + 1;
                    companiesFrequency.put(company, newValue);
                }
            }
        }

        companiesFrequency = sortByValueHighToLow(companiesFrequency);

        companiesFrequency.entrySet().stream().forEach(entry -> System.out.println("" + entry.getKey() + "  " + entry.getValue()));

        //System.out.println("wacht");
    }

    private void analysiseCodeAndUsernameLength() throws Exception {
        File file = new File("/Users/LennartMac/Documents/toinvestigate/discount_codes_size_analysis.txt");

        List<String> lines = readTheFile(file);
        Map<String, Integer> companies = new HashMap<>();
        Map<String, Integer> codes = new HashMap<>();

        List<String> linesToPrint = new ArrayList<>();

        for(String line : lines) {
            if(!line.isEmpty()) {
                String company = line.substring(2, line.indexOf(","));
                companies.put(company, company.length());

                String code = line.substring(line.indexOf(",") + 2);
                code = code.substring(0, code.indexOf(","));
                codes.put(code, code.length());

                //if(company.length() < 22 && code.length() < 22) {
                if(company.length() < 21 && code.length() < 25) {
                    linesToPrint.add(line);
                } else {
                    //System.out.println(line);
                }
            }
        }

        companies = sortByValueHighToLow(companies);
        codes = sortByValueHighToLow(codes);

        //linesToPrint.forEach(line -> System.out.println(line));

        System.out.println("wacht");
    }

    private void analyseNewUsernamesInDataDirective() throws Exception {
        File file = new File("/Users/LennartMac/Documents/Projects/insta/src/main/resources/static/data_directive_feb_may_2022.txt");

        List<String> lines = readTheFile(file);
        List<String> userNamesInDataDirective = new ArrayList<>();

        for(String line : lines) {
            if(!line.isEmpty()) {
                String username = line.substring(line.indexOf(",") + 1);
                username = username.substring(username.indexOf(",") + 1);
                username = username.substring(1, username.indexOf(","));
                userNamesInDataDirective.add(username);
            }
        }

        InstaAccounts instaAccounts = new InstaAccounts();
        List<String> allExistingAccounts = instaAccounts.getAllInstaAccounts();

        userNamesInDataDirective.removeAll(allExistingAccounts);

        Set<String> newUserNamesSet = new HashSet<>();
        newUserNamesSet.addAll(userNamesInDataDirective);

        List<String> sortableNewUsernames = new ArrayList<>();
        sortableNewUsernames.addAll(newUserNamesSet);
        Collections.sort(sortableNewUsernames);

        sortableNewUsernames.forEach(username -> System.out.println(username));
    }

    private void addNewUsersToInstaList() {
        List<String> newUsers = Arrays.asList(
            "xxx",
            "yyy"
        );

        InstaAccounts instaAccounts = new InstaAccounts();
        List<String> allExistingAccounts = instaAccounts.getAllInstaAccounts();

        allExistingAccounts.addAll(newUsers);

        Set<String> asSet = new HashSet<>();
        asSet.addAll(allExistingAccounts);

        List<String> newUserList = new ArrayList<>();
        newUserList.addAll(asSet);
        Collections.sort(newUserList);

        newUserList.forEach(user -> System.out.println("users.add(\"" + user + "\");"));
    }

    private List<String> readTheFile(File file) throws Exception {
        List<String> textLines;
        try (Reader fileReader = new FileReader(file)) {
            BufferedReader bufReader = new BufferedReader(fileReader);

            String line = bufReader.readLine();

            textLines = new ArrayList<>();

            while (line != null) {
                textLines.add(line);
                line = bufReader.readLine();
            }

            bufReader.close();
            fileReader.close();
        }

        return textLines;
    }

    private void initializeDbConnection() throws Exception {
        Class.forName("com.mysql.jdbc.Driver").newInstance();
        con = DriverManager.getConnection("jdbc:mysql://localhost:3306/influencers?&serverTimezone=UTC", "root", "");
    }

    private void closeDbConnection() throws SQLException {
        con.close();
    }
}
