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
        new TheList().printTestMethod();
    }

    private void printTestMethod() throws Exception {
        List<String> allCities = getAllAvailablePostCodeNumbersOrCities(true);
        List<PostCode> dataPerCity = getAllPostCodeObjects(allCities, false);
        printJsCode(dataPerCity, false);
    }

    private List<String> getAllAvailablePostCodeNumbersOrCities(boolean postCode) throws Exception {
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

    private List<PostCode> getAllPostCodeObjects(List<String> allInputStrings, boolean city) throws Exception {
        List<PostCode> initialPostCodeList = new ArrayList<>();

        int counter = 0;

        for(String inputString : allInputStrings) {
            if(counter == 100) {
                break;
            }

            counter++;
            if(counter % 100 == 0) {
                System.out.println(counter);
            }

            PostCode postCode;

            if(city) {
                postCode = new CityInfoRetriever().getPostCodeData(inputString, "12months");
            } else {
                postCode = new PostCodeInfoRetriever().getPostCodeData(inputString, "12months");
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
            postCodePriceMap.put(postCode, convertPostcodePriceStringToPrice(postCode.getAverageHousePrice_6months()));
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

    private void initializeDbConnection() throws Exception {
        Class.forName("com.mysql.jdbc.Driver").newInstance();
        con = DriverManager.getConnection("jdbc:mysql://localhost:3306/insta?&serverTimezone=UTC", "root", "");
    }

    private void closeDbConnection() throws SQLException {
        con.close();
    }

    private static double convertPostcodePriceStringToPrice(String priceString) {
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
