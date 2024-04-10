package com.lennart.model;

import java.sql.*;
import java.sql.Date;
import java.util.*;
import java.util.stream.Collectors;

public class CompanyFinder {

    private Connection con;

    public static void main(String[] args) throws Exception {
        //new CompanyFinder().getCodesForCompany("aybl", "2019-01-01");
        //Map<String, Integer> companyFrequencyMap = new CompanyFinder().getCompanyFrequencyMap("2024-01-01");
        //companyFrequencyMap.entrySet().forEach(entry -> System.out.println(entry.getKey() + " " + entry.getValue()));

        //CompanyFinder companyFinder = new CompanyFinder();
        //companyFinder.getAllDatesInDb("2024-01-01");
        //List<String> companiesForDate = new CompanyFinder().getCompaniesForDate("2024-04-05");
        //companyFinder.printBranchDistribution(companiesForDate);

        new CompanyFinder().testMethod();
    }

    private void testMethod() throws Exception {
        List<String> allDatesInDb = getAllDatesInDb("2024-01-01");

        List<List<String>> companiesForDates = new ArrayList<>();

        for(String date : allDatesInDb) {
            List<String> companiesForDate = getCompaniesForDate(date);

            if(companiesForDate.size() > 10) {
                companiesForDates.add(getCompaniesForDate(date));
            }
        }

        printBranchDistribution(companiesForDates);
    }

    private Map<String, Integer> getCompanyFrequencyMap(String dateLimit) throws Exception {
        Map<String, Integer> companyFrequencyMap = new HashMap<>();

        initializeDbConnection();

        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM discounts WHERE date >= '" + dateLimit + "';");

        while(rs.next()) {
            String company = rs.getString("company");
            companyFrequencyMap.put(company, companyFrequencyMap.getOrDefault(company, 0) + 1);
        }

        rs.close();
        st.close();

        closeDbConnection();

        companyFrequencyMap = companyFrequencyMap.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (oldValue, newValue) -> oldValue, LinkedHashMap::new));

        return companyFrequencyMap;
    }

    private Map<String, Date> getCodesForCompany(String company, String dateLimit) throws Exception {
        Map<String, Date> codesForCompany = new HashMap<>();

        initializeDbConnection();

        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM discounts WHERE company = '" + company + "' AND date >= '" + dateLimit + "';");

        while(rs.next()) {
            String discountCode = rs.getString("discount_code");
            Date date = rs.getDate("date");

            if(codesForCompany.containsKey(discountCode)) {
                Date existingDate = codesForCompany.get(discountCode);
                if(date.after(existingDate)) {
                    codesForCompany.put(discountCode, date);
                }
            } else {
                codesForCompany.put(discountCode, date);
            }
        }

        rs.close();
        st.close();

        closeDbConnection();

        codesForCompany = codesForCompany.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));

        return codesForCompany;
    }

    private void printBranchDistribution(List<List<String>> companiesForDates) {
        Map<String, Double> branchCounts = new HashMap<>();

        branchCounts.put("accessoires", 0.0);
        branchCounts.put("cosmetics", 0.0);
        branchCounts.put("erotics", 0.0);
        branchCounts.put("fashion", 0.0);
        branchCounts.put("food", 0.0);
        branchCounts.put("homedecoration", 0.0);
        branchCounts.put("jewellery", 0.0);
        branchCounts.put("lingerie", 0.0);
        branchCounts.put("other", 0.0);
        branchCounts.put("sport", 0.0);
        branchCounts.put("unknown", 0.0);

        for(List<String> companiesForDate : companiesForDates) {
            for(String company : companiesForDate) {
                String branch = getBranchForCompany(company);
                Double count = branchCounts.get(branch);
                count++;
                branchCounts.put(branch, count);
            }
        }

        double totalCount = branchCounts.values().stream().mapToDouble(Double::doubleValue).sum();

        List<Map.Entry<String, Double>> sortedEntries = new ArrayList<>(branchCounts.entrySet());
        sortedEntries.sort(Map.Entry.<String, Double>comparingByValue().reversed());

        sortedEntries.forEach(entry -> {
            double percentage = (entry.getValue() / totalCount) * 100;
            System.out.printf("%s: %.1f%%\n", entry.getKey(), percentage);
        });
    }

    private List<String> getAllDatesInDb(String dateLimit) throws Exception {
        Set<String> allDates = new HashSet<>();

        initializeDbConnection();

        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM discounts WHERE date >= '" + dateLimit + "';");

        while(rs.next()) {
            String date = rs.getString("date");
            allDates.add(date);
        }

        rs.close();
        st.close();

        closeDbConnection();

        return new ArrayList<>(allDates);
    }

    private List<String> getCompaniesForDate(String dateString) throws Exception {
        List<String> companiesForDate = new ArrayList<>();

        initializeDbConnection();

        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM discounts WHERE date = '" + dateString + "';");

        while(rs.next()) {
            companiesForDate.add(rs.getString("company"));
        }

        Collections.sort(companiesForDate);
        return companiesForDate;
    }

    private static Map<String, String> initializeCompanyToBranchMap() {
        Map<String, String> companyToBranchMap = new HashMap<>();

        companyToBranchMap.put("shein", "fashion");
        companyToBranchMap.put("ginatricot", "fashion");
        companyToBranchMap.put("gutsgusto", "fashion");
        companyToBranchMap.put("burga", "accessoires");
        companyToBranchMap.put("loavies", "fashion");
        companyToBranchMap.put("stronger", "sport");
        companyToBranchMap.put("gymshark", "sport");
        companyToBranchMap.put("aybl", "sport");
        companyToBranchMap.put("esn", "sport");
        companyToBranchMap.put("nakdfashion", "fashion");
        companyToBranchMap.put("bjornborg", "sport");
        companyToBranchMap.put("myproteinnl", "sport");
        companyToBranchMap.put("hellofresh.nl", "food");
        companyToBranchMap.put("geurwolkje", "homedecoration");
        companyToBranchMap.put("desenio", "homedecoration");
        companyToBranchMap.put("snuggs", "other");
        companyToBranchMap.put("idealofsweden", "accessoires");
        companyToBranchMap.put("otrium", "fashion");
        companyToBranchMap.put("zonnebrillen.com", "accessoires");
        companyToBranchMap.put("maniacnails", "cosmetics");
        companyToBranchMap.put("emmasleepnl", "homedecoration");
        companyToBranchMap.put("hunkemoller", "lingerie");
        companyToBranchMap.put("asos", "fashion");
        companyToBranchMap.put("sellpy", "fashion");
        companyToBranchMap.put("leolive", "other");
        companyToBranchMap.put("icaniwill", "sport");
        companyToBranchMap.put("only", "fashion");
        companyToBranchMap.put("prozis", "sport");
        companyToBranchMap.put("stevemaddeneu", "fashion");
        companyToBranchMap.put("zalando", "fashion");
        companyToBranchMap.put("terstal", "fashion");
        companyToBranchMap.put("bodyandfit.com", "sport");
        companyToBranchMap.put("lookfantastic", "cosmetics");
        companyToBranchMap.put("lyko", "cosmetics");
        companyToBranchMap.put("famousstore", "fashion");
        companyToBranchMap.put("pinkgellac", "cosmetics");
        companyToBranchMap.put("airup", "other");
        companyToBranchMap.put("madlady", "fashion");
        companyToBranchMap.put("arket", "fashion");
        companyToBranchMap.put("meetmethere", "fashion");
        companyToBranchMap.put("esuals.nl", "fashion");
        companyToBranchMap.put("womensbest", "sport");
        companyToBranchMap.put("photowall_sweden", "homedecoration");
        companyToBranchMap.put("superdry", "fashion");
        companyToBranchMap.put("easytoys", "erotics");
        companyToBranchMap.put("loopearplugs", "other");
        companyToBranchMap.put("paulie__pocket", "jewellery");
        companyToBranchMap.put("edgardcooper", "other");
        companyToBranchMap.put("greenchef.nl", "food");
        companyToBranchMap.put("loungeunderwear", "lingerie");
        companyToBranchMap.put("drsmile.nl", "other");
        companyToBranchMap.put("factormeals_nl", "food");
        companyToBranchMap.put("paulvalentine", "jewellery");
        companyToBranchMap.put("isabelbernard", "jewellery");
        companyToBranchMap.put("bellabarnett", "fashion");
        companyToBranchMap.put("gustileder", "other");
        companyToBranchMap.put("begoldennl", "jewellery");
        companyToBranchMap.put("fittwear_official", "sport");
        companyToBranchMap.put("wildrefill", "other");
        companyToBranchMap.put("aliexpress", "other");
        companyToBranchMap.put("pranamat_nl", "other");
        companyToBranchMap.put("nike", "sport");
        companyToBranchMap.put("bangerhead", "cosmetics");
        companyToBranchMap.put("cocunat", "cosmetics");
        companyToBranchMap.put("lakkiegellak", "cosmetics");
        companyToBranchMap.put("haarspullen.nl", "cosmetics");
        companyToBranchMap.put("icon.amsterdam", "fashion");
        companyToBranchMap.put("nelly", "fashion");
        companyToBranchMap.put("vitakruid", "other");
        companyToBranchMap.put("fitshe.com", "sport");
        companyToBranchMap.put("albelli", "homedecoration");
        companyToBranchMap.put("safira", "jewellery");
        companyToBranchMap.put("optimalprint", "homedecoration");
        companyToBranchMap.put("foodforskin.care", "cosmetics");
        companyToBranchMap.put("myfavourites.nl", "fashion");
        companyToBranchMap.put("flink", "food");
        companyToBranchMap.put("jotex", "homedecoration");
        companyToBranchMap.put("livialune.nl", "fashion");
        companyToBranchMap.put("quotrell", "fashion");
        companyToBranchMap.put("squla", "other");
        companyToBranchMap.put("onceuponapp", "homedecoration");
        companyToBranchMap.put("getdrezzed", "fashion");
        companyToBranchMap.put("doonails", "cosmetics");
        companyToBranchMap.put("prettycurlygirl.com", "cosmetics");
        companyToBranchMap.put("stylevana", "cosmetics");
        companyToBranchMap.put("shampoobars.nl", "cosmetics");
        companyToBranchMap.put("reginajewelry", "jewellery");
        companyToBranchMap.put("veromoda", "fashion");
        companyToBranchMap.put("luamaya", "jewellery");
        companyToBranchMap.put("aestheticwolf", "sport");
        companyToBranchMap.put("secretsales.nl", "fashion");
        companyToBranchMap.put("junglueck_nl", "cosmetics");

        return companyToBranchMap;
    }

    private String getBranchForCompany(String company) {
        Map<String, String> companyToBranchMap = initializeCompanyToBranchMap();
        String branch = companyToBranchMap.getOrDefault(company, "unknown");
        return branch;
    }

    public static List<String> getCompaniesForBranch(String branch) {
        Map<String, String> companyToBranchMap = initializeCompanyToBranchMap();

        return companyToBranchMap.entrySet().stream()
                .filter(entry -> entry.getValue().equals(branch))
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
