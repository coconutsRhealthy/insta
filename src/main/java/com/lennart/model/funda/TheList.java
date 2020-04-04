package com.lennart.model.funda;

import com.lennart.model.Aandacht;

import java.sql.*;
import java.util.*;

/**
 * Created by LennartMac on 19/02/2020.
 */
public class TheList {

    private Connection con;

    public static void main(String[] args) throws Exception {
        new TheList().shortlistMethod();
    }

    private void shortlistMethod() throws Exception {
        List<String> allCities = getAllAvailablePostCodeNumbersOrCities(true);
        List<PostCode> dataPerCity = getAllPostCodeObjects(allCities, false);
        printJsCodeShortlist(dataPerCity, false, false);

        System.out.println();
        System.out.println("***************");
        System.out.println();
        System.out.println();
        System.out.println();
        System.out.println();
        System.out.println();
        System.out.println();
        System.out.println("***************");
        System.out.println();

        printJsCodeShortlist(dataPerCity, false, true);
    }

    private void printTestMethod() throws Exception {
        List<String> allCities = getAllAvailablePostCodeNumbersOrCities(true);
        List<PostCode> dataPerCity = getAllPostCodeObjects(allCities, false);
        printJsCode(dataPerCity, false);
    }

    public List<String> getAllAvailablePostCodeNumbersOrCities(boolean postCode) throws Exception {
        Set<String> allDataSet = new HashSet<>();

        initializeDbConnection();

        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM funda3;");

        while(rs.next()) {
            String dataString;

            if(postCode) {
                dataString = rs.getString("postcode");
                dataString = dataString.replaceAll("[^\\d.]", "");
            } else {
                dataString = rs.getString("plaats");
            }

            allDataSet.add(dataString);
        }

        rs.close();
        st.close();

        closeDbConnection();

        List<String> allDataList = new ArrayList<>();
        allDataList.addAll(allDataSet);

        Collections.sort(allDataList);

        return allDataList;
    }

    public List<PostCode> getAllPostCodeObjects(List<String> allInputStrings, boolean city) throws Exception {
        List<PostCode> initialPostCodeList = new ArrayList<>();

        int counter = 0;

        for(String inputString : allInputStrings) {
            counter++;

            if(counter % 100 == 0) {
                System.out.println(counter);
            }

            PostCode postCode;

            if(city) {
                postCode = new CityInfoRetriever().getPostCodeData(inputString);
            } else {
                postCode = new PostCodeInfoRetriever().getPostCodeData(inputString);
            }

            if(postCode.getNumberOfHousesSold_6months() > 2) {
                double price = convertPostcodePriceStringToPrice(postCode.getAverageHousePrice_6months());
                double priceM2 = convertPostcodePriceStringToPrice(postCode.getAverageHousePricePerM2_6months());

                if(price > 0 && priceM2 > 0) {
                    initialPostCodeList.add(postCode);
                }
            }
        }

        Map<PostCode, Double> postCodePriceMap = new HashMap<>();

        for(PostCode postCode : initialPostCodeList) {
            postCodePriceMap.put(postCode, convertPostcodePriceStringToPrice(postCode.getAverageHousePrice_12months()));
        }

        postCodePriceMap = Aandacht.sortByValueHighToLow(postCodePriceMap);

        return new ArrayList<>(postCodePriceMap.keySet());
    }

    private void printJsCode(List<PostCode> postCodes, boolean city) {
        if(!city) {
            System.out.println("$scope.allePostcodes = [");
        } else {
            System.out.println("$scope.alleWoonplaatsen = [");
        }

        for(int i = 0; i < postCodes.size(); i++) {
            System.out.println("\t{");

            if(!city) {
                System.out.println("\t\tpostcode: " + postCodes.get(i).getPostCodeString() + ",");
            }

            System.out.println("\t\tplaats: \"" + postCodes.get(i).getCity() + "\",");
            System.out.println("\t\tprijs_6m: " + convertPostcodePriceStringToPrice(postCodes.get(i).getAverageHousePrice_6months()) + ",");
            System.out.println("\t\tprijs_m2_6m: " + convertPostcodePriceStringToPrice(postCodes.get(i).getAverageHousePricePerM2_6months()) + ",");
            System.out.println("\t\tprijs_12m: " + convertPostcodePriceStringToPrice(postCodes.get(i).getAverageHousePrice_12months()) + ",");
            System.out.println("\t\tprijs_m2_12m: " + convertPostcodePriceStringToPrice(postCodes.get(i).getAverageHousePricePerM2_12months()) + ",");
            System.out.println("\t\taantal_6m: " + postCodes.get(i).getNumberOfHousesSold_6months() + ",");
            System.out.println("\t\taantal_12m: " + postCodes.get(i).getNumberOfHousesSold_12months() + ",");
            System.out.println("\t},");
        }

        System.out.println("];");
    }

    private void printJsCodeShortlist(List<PostCode> postCodes, boolean city, boolean sixMonths) {
        List<PostCode> shortList = getShortList(postCodes, sixMonths);

        if(city) {
            if(sixMonths) {
                System.out.println("$scope.alleWoonplaatsenShortlist6months = [");
            } else {
                System.out.println("$scope.alleWoonplaatsenShortlist12months = [");
            }
        } else {
            if(sixMonths) {
                System.out.println("$scope.alleBuurtenShortlist6months = [");
            } else {
                System.out.println("$scope.alleBuurtenShortlist12months = [");
            }
        }

        if(city) {
            for(int i = 0; i < shortList.size(); i++) {
                System.out.println("\t{");
                System.out.println("\t\tplaats: \"" + shortList.get(i).getCity() + "\",");

                if(sixMonths) {
                    System.out.println("\t\tprijs_6m: " + convertPostcodePriceStringToPrice(shortList.get(i).getAverageHousePrice_6months()) + ",");
                    System.out.println("\t\tprijs_m2_6m: " + convertPostcodePriceStringToPrice(shortList.get(i).getAverageHousePricePerM2_6months()) + ",");
                    System.out.println("\t\taantal_6m: " + shortList.get(i).getNumberOfHousesSold_6months() + ",");
                } else {
                    System.out.println("\t\tprijs_12m: " + convertPostcodePriceStringToPrice(shortList.get(i).getAverageHousePrice_12months()) + ",");
                    System.out.println("\t\tprijs_m2_12m: " + convertPostcodePriceStringToPrice(shortList.get(i).getAverageHousePricePerM2_12months()) + ",");
                    System.out.println("\t\taantal_12m: " + shortList.get(i).getNumberOfHousesSold_12months() + ",");
                }

                System.out.println("\t},");
            }
        } else {
            for(int i = 0; i < shortList.size(); i++) {
                System.out.println("\t{");
                System.out.println("\t\tpostcode: " + shortList.get(i).getPostCodeString() + ",");
                System.out.println("\t\tplaats: \"" + shortList.get(i).getCity() + "\",");

                if(sixMonths) {
                    System.out.println("\t\tprijs_6m: " + convertPostcodePriceStringToPrice(shortList.get(i).getAverageHousePrice_6months()) + ",");
                    System.out.println("\t\tprijs_m2_6m: " + convertPostcodePriceStringToPrice(shortList.get(i).getAverageHousePricePerM2_6months()) + ",");
                    System.out.println("\t\taantal_6m: " + shortList.get(i).getNumberOfHousesSold_6months() + ",");
                } else {
                    System.out.println("\t\tprijs_12m: " + convertPostcodePriceStringToPrice(shortList.get(i).getAverageHousePrice_12months()) + ",");
                    System.out.println("\t\tprijs_m2_12m: " + convertPostcodePriceStringToPrice(shortList.get(i).getAverageHousePricePerM2_12months()) + ",");
                    System.out.println("\t\taantal_12m: " + shortList.get(i).getNumberOfHousesSold_12months() + ",");
                }

                System.out.println("\t},");
            }
        }

        System.out.println("];");
    }

    private List<PostCode> getShortList(List<PostCode> longList, boolean sixMonths) {
        List<PostCode> shortList = new ArrayList<>();

        if(!sixMonths) {
            for(int i = 0; i < 10; i++) {
                shortList.add(longList.get(i));
            }
        } else {
            Map<PostCode, Double> price6monthsMap = new HashMap<>();

            for(PostCode postCode : longList) {
                price6monthsMap.put(postCode, convertPostcodePriceStringToPrice(postCode.getAverageHousePrice_6months()));
            }

            price6monthsMap = Aandacht.sortByValueHighToLow(price6monthsMap);
            List<PostCode> longListSortedBySixMonthsPrice = new ArrayList<>(price6monthsMap.keySet());


            for(int i = 0; i < 10; i++) {
                shortList.add(longListSortedBySixMonthsPrice.get(i));
            }
        }

        return shortList;
    }

    private void htmlMethod() throws Exception {
        List<String> allPostcodes = getAllAvailablePostCodeNumbersOrCities(false);
        List<PostCode> postCodeObjects = getAllPostCodeObjects(allPostcodes, true);
        printHtml(postCodeObjects, false);
    }

    private void printHtml(List<PostCode> postCodes, boolean postcode) {
        System.out.println("<table id=\"example\" class=\"display nowrap\" width=\"100%\">");
        System.out.println("\t<thead>");
        System.out.println("\t\t<tr>");

        if(postcode) {
            System.out.println("\t\t\t<th>Postcode</th>");
        }

        System.out.println("\t\t\t<th>Plaats</th>");
        System.out.println("\t\t\t<th>Prijs</th>");
        System.out.println("\t\t\t<th>Prijs m2</th>");
        System.out.println("\t\t\t<th>Transacties</th>");
        System.out.println("\t\t</tr>");
        System.out.println("\t</thead>");

        System.out.println("\t<tbody>");

        for(PostCode postCode : postCodes) {
            System.out.println("\t\t<tr>");

            if(postcode) {
                System.out.println("\t\t\t<td>" + postCode.getPostCodeString() + "</td>");
            }

            System.out.println("\t\t\t<td>" + postCode.getCity() + "</td>");
            System.out.println("\t\t\t<td>" + postCode.getAverageHousePrice_12months() + "</td>");
            System.out.println("\t\t\t<td>" + postCode.getAverageHousePricePerM2_12months() + "</td>");
            System.out.println("\t\t\t<td>" + postCode.getNumberOfHousesSold_12months() + "</td>");
            System.out.println("\t\t</tr>");
        }

        System.out.println("\t</tbody>");
        System.out.println("</table>");
    }

    private void jsArrayMethod() throws Exception {
        List<String> allPostcodes = getAllAvailablePostCodeNumbersOrCities(false);
        List<PostCode> postCodeObjects = getAllPostCodeObjects(allPostcodes, true);
        printJsArrayCities(postCodeObjects);
    }

    private void printJsArray(List<PostCode> postCodes) {
        for(PostCode postCode : postCodes) {
            System.out.println("[ \"\", \"" + postCode.getPostCodeString() + "\", \"" + postCode.getCity() + "\", \"" +
                postCode.getAverageHousePrice_12months() + "\", \"" + postCode.getAverageHousePricePerM2_12months() + "\", \"" +
                    postCode.getNumberOfHousesSold_12months() + "\" ],");
        }
    }

    private void printJsArrayCities(List<PostCode> cities) {
        for(PostCode city : cities) {
            System.out.println("[ \"\", \"" + city.getCity() + "\", \"" + city.getAverageHousePrice_12months() + "\", \"" +
                    city.getAverageHousePricePerM2_12months() + "\", \"" + city.getNumberOfHousesSold_12months() + "\" ],");
        }
    }

    private void initializeDbConnection() throws Exception {
        Class.forName("com.mysql.jdbc.Driver").newInstance();
        con = DriverManager.getConnection("jdbc:mysql://localhost:3306/insta?&serverTimezone=UTC", "root", "");
    }

    private void closeDbConnection() throws SQLException {
        con.close();
    }

    public static double convertPostcodePriceStringToPrice(String priceString) {
        String workingPriceString = priceString.replaceAll("[^0-9.]", "");
        workingPriceString = workingPriceString.replaceAll("\\.", "");
        double price = Double.valueOf(workingPriceString);
        return price;
    }

    public static <K, V extends Comparable<? super V>> Map<K, V> sortByValueHighToLow(Map<K, V> map) {
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
}
