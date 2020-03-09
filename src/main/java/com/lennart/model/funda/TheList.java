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
        new TheList().overallMethod();
    }

    private void overallMethod() throws Exception {
        List<String> allAvailablePostCodes = getAllAvailablePostCodeNumbersOrCities(false);
        List<PostCode> allPostCodeObjects = getAllPostCodeObjects(allAvailablePostCodes);
        printJsCode(allPostCodeObjects);
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

    private List<PostCode> getAllPostCodeObjects(List<String> allPostCodeStrings) throws Exception {
        List<PostCode> initialPostCodeList = new ArrayList<>();

        int counter = 0;

        for(String postCodeString : allPostCodeStrings) {
            counter++;
            if(counter % 100 == 0) {
                System.out.println(counter);
            }

            PostCode postCode = new PostCodeInfoRetriever().getPostCodeData(postCodeString, "12months");

            if(postCode.getNumberOfHousesSold() > 2) {
                double price = ForSaleGrader.convertPostcodePriceStringToPrice(postCode.getAverageHousePrice());
                double priceM2 =ForSaleGrader.convertPostcodePriceStringToPrice(postCode.getAverageHousePricePerM2());

                if(price > 0 && priceM2 > 0) {
                    initialPostCodeList.add(postCode);
                }
            }
        }

        Map<PostCode, Double> postCodePriceMap = new HashMap<>();

        for(PostCode postCode : initialPostCodeList) {
            postCodePriceMap.put(postCode, ForSaleGrader.convertPostcodePriceStringToPrice(postCode.getAverageHousePrice()));
        }

        postCodePriceMap = Aandacht.sortByValueHighToLow(postCodePriceMap);

        return new ArrayList<>(postCodePriceMap.keySet());
    }

    private void ffTestMethod() {
        PostCode postCode = new PostCode();

        postCode.setPostCodeString("3453");
        postCode.setCity("Leeuwarden");
        postCode.setAverageHousePrice("EUR 34345");

        List<PostCode> eije = new ArrayList<>();

        eije.add(postCode);

        printTheList(eije);
    }

    private void printTheList(List<PostCode> postCodes) {
        //rank, postcode, city, price

        String format = "%-15s%-14s%-38s%-20s%-20s";

        int counter = 0;

        for(PostCode postCode : postCodes) {
            System.out.printf(format, counter++, postCode.getPostCodeString(),
                   postCode.getCity(), postCode.getAverageHousePrice(), postCode.getAverageHousePricePerM2());
            System.out.println();
        }
    }

//    public static void main(String[] args) {
//        PostCode postCode1 = new PostCode();
//        postCode1.setPostCodeString("1097");
//        postCode1.setCity("Amsterdam");
//        postCode1.setAverageHousePrice("EUR 109.874");
//        postCode1.setAverageHousePricePerM2("EUR 5400");
//        postCode1.setNumberOfHousesSold(5);
//
//        PostCode postCode2 = new PostCode();
//        postCode2.setPostCodeString("3831");
//        postCode2.setCity("Leusden");
//        postCode2.setAverageHousePrice("EUR 233.653");
//        postCode2.setAverageHousePricePerM2("EUR 2800");
//        postCode2.setNumberOfHousesSold(8);
//
//        List<PostCode> list = new ArrayList<>();
//        list.add(postCode1);
//        list.add(postCode2);
//
//        new TheList().printJsCode(list);
//    }

    private void printJsCode(List<PostCode> postCodes) {
        System.out.println("$scope.allePostcodes = [");

        for(int i = 0; i < postCodes.size(); i++) {
            System.out.println("\t{");
            System.out.println("\t\tpostcode: " + postCodes.get(i).getPostCodeString() + ",");
            System.out.println("\t\tplaats: \"" + postCodes.get(i).getCity() + "\",");
            System.out.println("\t\tprijs: " + ForSaleGrader.convertPostcodePriceStringToPrice(postCodes.get(i).getAverageHousePrice()) + ",");
            System.out.println("\t\tprijs_m2: " + ForSaleGrader.convertPostcodePriceStringToPrice(postCodes.get(i).getAverageHousePricePerM2()) + ",");
            System.out.println("\t\taantal: " + postCodes.get(i).getNumberOfHousesSold() + ",");
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
}
