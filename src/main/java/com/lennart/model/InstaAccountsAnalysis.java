package com.lennart.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.util.*;

/**
 * Created by LennartMac on 27/12/2022.
 */
public class InstaAccountsAnalysis {

    private static final String DATA_DIRECTIVE_FILE_PATH = "/Users/LennartMac/Documents/Projects/insta/src/main/resources/static/data_directives/data_directive_jan_apr_2023.txt";

    public static void main(String[] args) throws Exception {
        InstaAccountsAnalysis instaAccountsAnalysis = new InstaAccountsAnalysis();
        instaAccountsAnalysis.getInterestingNewAccounts();
        //dummy
    }

    private void getInterestingNewAccounts() throws Exception {
        Map<String, List<String>> brandsPerNewUser = getBrandsPerNewUser();
        Map<String, Double> interestingAccsSum = getInterestingSum(brandsPerNewUser);
        Map<String, Double> interestingAccsUnique = getInterestingUnique(brandsPerNewUser);

        Set<String> interestingAccs = new HashSet<>();
        interestingAccs.addAll(interestingAccsSum.keySet());
        interestingAccs.addAll(interestingAccsUnique.keySet());

        Map<String, List<String>> interestingNewAccs = retainOnlyMapKeysIncludedInSet(brandsPerNewUser, interestingAccs);
    }

    private Map<String, List<String>> retainOnlyMapKeysIncludedInSet(Map<String, List<String>> map, Set<String> set) {
        Map<String, List<String>> retained = new LinkedHashMap<>();

        for(Map.Entry<String, List<String>> entry : map.entrySet()) {
            if(set.contains(entry.getKey())) {
                if(retained.get(entry.getKey()) == null) {
                    retained.put(entry.getKey(), entry.getValue());
                }
            }
        }

        return retained;
    }

    private Map<String, Double> getInterestingSum(Map<String, List<String>> brandsPerNewUser) throws Exception {
        Map<String, Double> interestingUsersWithScore = new HashMap<>();

        for(Map.Entry<String, List<String>> entry : brandsPerNewUser.entrySet()) {
            List<String> brands = entry.getValue();

            if(brands.size() >= 2 || brands.contains("myjewellery")) {
                double score = 0;

                for(String brand : brands) {
                    if(brand.equals("nakdfashion") || brand.equals("loavies") || brand.equals("chiquelle")
                            || brand.equals("gutsgusto") || brand.equals("myjewellery") || brands.contains("veromoda")) {
                        score++;
                    }
                }

                if(score >= 2 || brands.contains("myjewellery")) {
                    interestingUsersWithScore.put(entry.getKey(), score);
                }
            }
        }

        interestingUsersWithScore = sortByValueHighToLow(interestingUsersWithScore);
        return interestingUsersWithScore;
    }

    private Map<String, Double> getInterestingUnique(Map<String, List<String>> brandsPerNewUser) throws Exception {
        Map<String, Double> interestingUsersWithScore = new HashMap<>();

        for(Map.Entry<String, List<String>> entry : brandsPerNewUser.entrySet()) {
            List<String> brands = entry.getValue();

            if(brands.size() >= 2 || brands.contains("myjewellery")) {
                double score = 0;

                if(brands.contains("nakdfashion")) {
                    score++;
                }

                if(brands.contains("loavies")) {
                    score++;
                }

                if(brands.contains("gutsgusto")) {
                    score++;
                }

                if(brands.contains("myjewellery")) {
                    score++;
                }

                if(brands.contains("chiquelle")) {
                    score++;
                }

                if(brands.contains("veromoda")) {
                    score++;
                }

                if(score >= 2 || brands.contains("myjewellery")) {
                    interestingUsersWithScore.put(entry.getKey(), score);
                }
            }
        }

        interestingUsersWithScore = sortByValueHighToLow(interestingUsersWithScore);
        return interestingUsersWithScore;
    }

    private Map<String, List<String>> getBrandsPerNewUser() throws Exception {
        List<String> newUsers = getNewUsernamesInDataDirective();
        Map<String, Double> amountOfCodesByNewUser = getAmountOfCodesByNewUsers(newUsers);
        Map<String, List<String>> brandsPerUser = new LinkedHashMap<>();

        for(Map.Entry<String, Double> entry : amountOfCodesByNewUser.entrySet()) {
            List<String> brandsForUser = getBrandsThatNewUserOffersDiscountFor(entry.getKey());
            brandsPerUser.put(entry.getKey(), brandsForUser);
        }

        return brandsPerUser;
    }

    private Map<String, Double> getAmountOfCodesByNewUsers(List<String> newUsers) throws Exception {
        Map<String, Double> newUserDiscFrequency = new HashMap<>();

        File file = new File(DATA_DIRECTIVE_FILE_PATH);
        List<String> lines = readTheFile(file);

        for(String newUser : newUsers) {
            newUserDiscFrequency.put(newUser, 0.0);

            for(String line : lines) {
                if(line.contains(newUser)) {
                    if(getUsernameFromLine(line).equals(newUser)) {
                        double oldValue = newUserDiscFrequency.get(newUser);
                        double newValue = oldValue + 1.0;
                        newUserDiscFrequency.put(newUser, newValue);
                    }
                }
            }
        }

        newUserDiscFrequency = sortByValueHighToLow(newUserDiscFrequency);
        return newUserDiscFrequency;
    }

    private List<String> getBrandsThatNewUserOffersDiscountFor(String newUser) throws Exception {
        List<String> brands = new ArrayList<>();

        File file = new File(DATA_DIRECTIVE_FILE_PATH);
        List<String> lines = readTheFile(file);

        for(String line : lines) {
            if(line.contains(newUser)) {
                if(getUsernameFromLine(line).equals(newUser)) {
                    brands.add(getCompanyFromLine(line));
                }
            }
        }

        Collections.sort(brands);
        return brands;
    }

    private List<String> getNewUsernamesInDataDirective() throws Exception {
        File file = new File(DATA_DIRECTIVE_FILE_PATH);

        List<String> lines = readTheFile(file);
        List<String> userNamesInDataDirective = new ArrayList<>();

        for(String line : lines) {
            if(!line.isEmpty()) {
                String username = getUsernameFromLine(line);

                if(!username.equals("unknown")) {
                    userNamesInDataDirective.add(username);
                }
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
        return sortableNewUsernames;
    }

    private List<String> getDataDirectiveNonDiscountGivers() throws Exception {
        File file = new File(DATA_DIRECTIVE_FILE_PATH);

        List<String> lines = readTheFile(file);

        List<String> kortingGivers = new ArrayList<>();

        for(String line : lines) {
            String username = getUsernameFromLine(line);

            if(!username.equals("unknown")) {
                kortingGivers.add(username);
            }
        }

        Set<String> kortingGiversAsSet = new HashSet<>();
        kortingGiversAsSet.addAll(kortingGivers);

        List<String> allAccounts = new InstaAccounts().getAllInstaAccounts();
        allAccounts.removeAll(kortingGiversAsSet);
        Collections.sort(allAccounts);

        return allAccounts;
    }

    private String getUsernameFromLine(String line) {
        String username = "unknown";
        List<String> lineList = Arrays.asList(line.split(","));

        if(lineList.size() > 2) {
            if(lineList.size() == 4) {
                username = lineList.get(2);
            } else {
                username = lineList.get(3);
            }

            username = username.replaceFirst(" ", "");
        }

        return username;
    }

    private String getCompanyFromLine(String line) {
        String company = "unknown";
        List<String> lineList = Arrays.asList(line.split(","));

        if(lineList.size() > 2) {
            company = lineList.get(0);
            company = company.replace("@", "");
            company = company.replace("\"", "");
        }

        return company;
    }

    private void print(List<String> toPrintList, Map<String, Double> toPrintMap) {
        int counter = 0;

        if(toPrintList != null) {
            for(String listString : toPrintList) {
                System.out.println("" + counter++ + ") " + listString);
            }
        }

        if(toPrintMap != null) {
            for(Map.Entry<String, Double> entry : toPrintMap.entrySet()) {
                System.out.println("" + counter++ + ") " + entry.getKey() + "      " + entry.getValue());
            }
        }
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

    private List<String> getUsernamesThatSharedCodesOfLoaviesAndNakd() throws Exception {
        List<String> loaviesDiscountGivers = new ArrayList<>();
        List<String> nakdDiscountGivers = new ArrayList<>();


        File file = new File(DATA_DIRECTIVE_FILE_PATH);
        List<String> lines = readTheFile(file);

        for(String line : lines) {
            String companyInLine = getCompanyFromLine(line);

            if(companyInLine.equals("loavies")) {
                loaviesDiscountGivers.add(getUsernameFromLine(line));
            }

            if(companyInLine.equals("nakdfashion")) {
                nakdDiscountGivers.add(getUsernameFromLine(line));
            }
        }

        Set<String> bothLoaviesAndNakdGiversSet = new HashSet<>();
        bothLoaviesAndNakdGiversSet.addAll(loaviesDiscountGivers);
        bothLoaviesAndNakdGiversSet.retainAll(nakdDiscountGivers);
        List<String> bothLoaviesAndNakdGivers = new ArrayList<>();
        bothLoaviesAndNakdGivers.addAll(bothLoaviesAndNakdGiversSet);

        //for(int i = 0; i < bothLoaviesAndNakdGivers.size(); i++) {
        //    System.out.println("" + i + ") " + bothLoaviesAndNakdGivers.get(i));
        //}

        return bothLoaviesAndNakdGivers;
    }

    private void printCompaniesThatUserGivesCodesFor(String user) throws Exception {
        List<String> companies = getBrandsThatNewUserOffersDiscountFor(user);
        Set<String> asSet = new HashSet<>();
        asSet.addAll(companies);

        System.out.println("user: " + user);
        for(String company : asSet) {
            System.out.println(company);
        }
        System.out.println();
    }


}
