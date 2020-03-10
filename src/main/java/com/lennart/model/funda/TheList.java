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


    //get all cities
    //get gegevens per city
    //print de js code

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
                double price = ForSaleGrader.convertPostcodePriceStringToPrice(postCode.getAverageHousePrice_6months());
                double priceM2 =ForSaleGrader.convertPostcodePriceStringToPrice(postCode.getAverageHousePricePerM2_6months());

                if(price > 0 && priceM2 > 0) {
                    initialPostCodeList.add(postCode);
                }
            }
        }

        Map<PostCode, Double> postCodePriceMap = new HashMap<>();

        for(PostCode postCode : initialPostCodeList) {
            postCodePriceMap.put(postCode, ForSaleGrader.convertPostcodePriceStringToPrice(postCode.getAverageHousePrice_6months()));
        }

        postCodePriceMap = Aandacht.sortByValueHighToLow(postCodePriceMap);

        return new ArrayList<>(postCodePriceMap.keySet());
    }

    private void ffTestMethod() {
        PostCode postCode = new PostCode();

        postCode.setPostCodeString("3453");
        postCode.setCity("Leeuwarden");
        postCode.setAverageHousePrice_6months("EUR 34345");

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
                   postCode.getCity(), postCode.getAverageHousePrice_6months(), postCode.getAverageHousePricePerM2_6months());
            System.out.println();
        }
    }

//    public static void main(String[] args) {
//        PostCode postCode1 = new PostCode();
//        postCode1.setPostCodeString("1097");
//        postCode1.setCity("Amsterdam");
//        postCode1.setAverageHousePrice_6months("EUR 109.874");
//        postCode1.setAverageHousePricePerM2_6months("EUR 5400");
//        postCode1.setNumberOfHousesSold_6months(5);
//
//        PostCode postCode2 = new PostCode();
//        postCode2.setPostCodeString("3831");
//        postCode2.setCity("Leusden");
//        postCode2.setAverageHousePrice_6months("EUR 233.653");
//        postCode2.setAverageHousePricePerM2_6months("EUR 2800");
//        postCode2.setNumberOfHousesSold_6months(8);
//
//        List<PostCode> list = new ArrayList<>();
//        list.add(postCode1);
//        list.add(postCode2);
//
//        new TheList().printJsCode(list);
//    }


    //    $scope.alleWoonplaatsen = [
//        {
//            postcode: 1071,
//            plaats: "Amsterdam",
//            prijs: 509443.0,
//            prijs_6m: 80000.0,
//            prijs_m2: 5884.0,
//            prijs_m2_6m: 1400.0,
//            aantal: 4098,
//        },

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
            System.out.println("\t\tprijs_6m: " + ForSaleGrader.convertPostcodePriceStringToPrice(postCodes.get(i).getAverageHousePrice_6months()) + ",");
            System.out.println("\t\tprijs_m2_6m: " + ForSaleGrader.convertPostcodePriceStringToPrice(postCodes.get(i).getAverageHousePricePerM2_6months()) + ",");
            System.out.println("\t\tprijs_12m: " + ForSaleGrader.convertPostcodePriceStringToPrice(postCodes.get(i).getAverageHousePrice_12months()) + ",");
            System.out.println("\t\tprijs_m2_12m: " + ForSaleGrader.convertPostcodePriceStringToPrice(postCodes.get(i).getAverageHousePricePerM2_12months()) + ",");
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
}
