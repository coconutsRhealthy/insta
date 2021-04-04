package com.lennart.model.funda;

import com.lennart.model.Aandacht;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.lennart.model.funda.TheList.convertPostcodePriceStringToPrice;

/**
 * Created by LennartMac on 08/04/2020.
 */
public class HouseRarityChecker {

    private Connection con;

//    public static void main(String[] args) throws Exception {
//        new HouseRarityChecker().effiesHier();
//    }


    private void effiesHier() throws Exception {
        //DataFromPageRetriever dataFromPageRetriever = new DataFromPageRetriever();

        List<House> newHouses = gatherData();
        theMethod(newHouses);
    }

    private List<House> gatherData() throws Exception {
        String path = "/Users/LennartMac/Documents/huizenstuff/tekoop_17_apr";

        List<File> allNewHtmlFiles = new HousePersister().getAllHtmlFilesFromDir(path);

        List<House> newHouses = new ArrayList<>();

        int counter = 0;

        for(File file : allNewHtmlFiles) {
            System.out.println(counter++);
            newHouses.addAll(new DataFromPageRetriever().gatherHouseData(file, false));
        }

//        Map<House, PostCode> housePostCodeMap = theMethod2(newHouses);
//        Map<House, Double> priceDiffMap = filter(housePostCodeMap, false);
//        Map<House, Double> priceDiffMapM2 = filter(housePostCodeMap, true);
//
//        System.out.println("wacht");

        return newHouses;
    }






    private void theMethod(List<House> allNewHouses) throws Exception {

        Map<House, Integer> houseAndNumberOfHousesSoldInPostcode = new HashMap<>();

        initializeDbConnection();

        int tracker = 0;

        for(House house : allNewHouses) {
            System.out.println(tracker++);

            String postCodeString = house.getPostCode();

            if(postCodeString.contains(" ")) {
                postCodeString = postCodeString.substring(0, postCodeString.indexOf(" "));

                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery("SELECT * FROM funda3 WHERE postcode LIKE '%" + postCodeString + "%';");

                rs.last();
                int counter = rs.getRow();

                rs.close();
                st.close();

                houseAndNumberOfHousesSoldInPostcode.put(house, counter);
            }
        }

        closeDbConnection();

        houseAndNumberOfHousesSoldInPostcode = Aandacht.sortByValueLowToHigh(houseAndNumberOfHousesSoldInPostcode);

        System.out.println("wacht");
    }




    private Map<House, PostCode> theMethod2(List<House> allNewHouses) throws Exception {
        Map<House, PostCode> housePostcodeMap = new HashMap<>();

        initializeDbConnection();

        int tracker = 0;

        for(House house : allNewHouses) {
            System.out.println(tracker++);

            String postCodeString = house.getPostCode();

            if(postCodeString.contains(" ")) {
                postCodeString = postCodeString.substring(0, postCodeString.indexOf(" "));
                PostCode postCode = new PostCodeInfoRetriever().getPostCodeData(postCodeString);
                housePostcodeMap.put(house, postCode);
            }
        }

        closeDbConnection();

        return housePostcodeMap;
    }

    private Map<House, Double> filter(Map<House, PostCode> input, boolean m2diff) {
        Map<House, Double> relevantHouses = new HashMap<>();

        for (Map.Entry<House, PostCode> entry : input.entrySet()) {
            House house = entry.getKey();
            PostCode postCode = entry.getValue();

            if(house.getPrice() > 0 && house.getPriceM2() > 0) {
                if(postCode.getNumberOfHousesSold_12months() > 150) {
                    if(house.getPrice() < convertPostcodePriceStringToPrice(postCode.getAverageHousePrice_12months())) {
                        if(house.getPriceM2() < convertPostcodePriceStringToPrice(postCode.getAverageHousePricePerM2_12months())) {
                            if(!m2diff) {
                                double priceDiff = convertPostcodePriceStringToPrice(postCode.getAverageHousePrice_12months())
                                        - house.getPrice();

                                relevantHouses.put(house, priceDiff);
                            } else {
                                double priceDiff = convertPostcodePriceStringToPrice(postCode.getAverageHousePricePerM2_12months())
                                        - house.getPriceM2();

                                relevantHouses.put(house, priceDiff);
                            }
                        }
                    }
                }
            }
        }

        relevantHouses = Aandacht.sortByValueHighToLow(relevantHouses);
        return relevantHouses;
    }







    /////////////////
    private void identifyMostTransactionedPostcode() throws Exception {
        initializeDbConnection();

        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM funda3;");

        Map<String, Integer> postcodeCountMap = new HashMap<>();

        while(rs.next()) {
            String postcodeString = rs.getString("postcode");

            if(postcodeString.contains(" ") && !rs.getString("adres").contains("Bouw")) {
//                if(postcodeString.equals("1031 KG")) {
//                    System.out.println(rs.getString("adres"));
//                }

                if(postcodeCountMap.get(postcodeString) == null) {
                    postcodeCountMap.put(postcodeString, 1);
                } else {
                    int currentCount = postcodeCountMap.get(postcodeString);
                    postcodeCountMap.put(postcodeString, currentCount + 1);
                }
            }
        }

        rs.close();
        st.close();

        closeDbConnection();

        postcodeCountMap = Aandacht.sortByValueHighToLow(postcodeCountMap);

        System.out.println("wachffiez");
    }


    //////////////////


    private void initializeDbConnection() throws Exception {
        Class.forName("com.mysql.jdbc.Driver").newInstance();
        con = DriverManager.getConnection("jdbc:mysql://localhost:3306/insta?&serverTimezone=UTC", "root", "");
    }

    private void closeDbConnection() throws SQLException {
        con.close();
    }

}
