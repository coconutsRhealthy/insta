package com.lennart.model.funda;

import com.lennart.model.Aandacht;

import java.sql.*;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by LennartMac on 02/04/2020.
 */
public class PostableCityIdentifier {

    private Connection con;

    //steden waar
        //duurste postcode prijs gelijk is aan duurste postcode prijs_m2

    public static void main(String[] args) throws Exception {
        new PostableCityIdentifier().analysePriceDiffMostExpensiveAndCheapPostcode("Amsterdam");

    }

    private List<String> identifyCitiesWithSameMostExpensivePostcodePricePriceM2() throws Exception {
        TheList theList = new TheList();

        List<String> citiesToReturn = new ArrayList<>();
        List<String> allCities = theList.getAllAvailablePostCodeNumbersOrCities(false);

        for(String city : allCities) {
            if(!city.contains("'")) {
                List<String> postCodeStringsOfCity = getPostCodesOfCity(city);

                if(postCodeStringsOfCity.size() > 1) {
                    List<PostCode> postCodes = theList.getAllPostCodeObjects(postCodeStringsOfCity, false);
                    String mostExpensivePostcode = getMostExpensiveOrCheapPostcode(postCodes, false, true);
                    String mostExpensivePostcodeM2 = getMostExpensiveOrCheapPostcode(postCodes, true, true);

                    if(mostExpensivePostcode.equals(mostExpensivePostcodeM2)) {
                        citiesToReturn.add(city);
                    }
                }
            }
        }

        Collections.sort(citiesToReturn);
        return citiesToReturn;
    }

    private void analysePriceDiffMostExpensiveAndCheapPostcode(String city) throws Exception {
        List<String> postCodeStringsOfCity = getPostCodesOfCity(city);

        List<PostCode> postCodes = new TheList().getAllPostCodeObjects(postCodeStringsOfCity, false);

        String mostExpensivePostcodeString = getMostExpensiveOrCheapPostcode(postCodes, false, true);
        String cheapestPostcodeString = getMostExpensiveOrCheapPostcode(postCodes, false, false);

        PostCode expensivePostcode = new PostCodeInfoRetriever().getPostCodeData(mostExpensivePostcodeString);
        PostCode cheapestPostcode = new PostCodeInfoRetriever().getPostCodeData(cheapestPostcodeString);

        double priceDiff = TheList.convertPostcodePriceStringToPrice(expensivePostcode.getAverageHousePrice_12months())
                - TheList.convertPostcodePriceStringToPrice(cheapestPostcode.getAverageHousePrice_12months());

        System.out.println(priceDiff);
    }

    private List<String> getPostCodesOfCity(String city) throws Exception {
        initializeDbConnection();

        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM funda3 WHERE plaats LIKE '%" + city + "%';");

        Set<String> allPostcodesSet = new HashSet<>();

        while(rs.next()) {
            String postCodeString = rs.getString("postcode");

            if(postCodeString.contains(" ")) {
                allPostcodesSet.add(postCodeString.substring(0, postCodeString.indexOf(" ")));
            }
        }

        List<String> allPostcodes = new ArrayList<>();
        allPostcodes.addAll(allPostcodesSet);

        Collections.sort(allPostcodes);

        return allPostcodes;
    }

    private String getMostExpensiveOrCheapPostcode(List<PostCode> postCodesOfCity, boolean m2, boolean expensive) {
        Map<String, Double> pricesMap;

        if(!m2) {
            pricesMap = postCodesOfCity.stream()
                    .collect
                            (Collectors.toMap(postCode -> postCode.getPostCodeString(),
                                    postCode -> TheList.convertPostcodePriceStringToPrice
                                            (postCode.getAverageHousePrice_12months())));
        } else {
            pricesMap = postCodesOfCity.stream()
                    .collect
                            (Collectors.toMap(postCode -> postCode.getPostCodeString(),
                                    postCode -> TheList.convertPostcodePriceStringToPrice
                                            (postCode.getAverageHousePricePerM2_12months())));
        }

        if(expensive) {
            pricesMap = Aandacht.sortByValueHighToLow(pricesMap);
        } else {
            pricesMap = Aandacht.sortByValueLowToHigh(pricesMap);
        }

        String mostExpensivePostcode = pricesMap.entrySet().iterator().next().getKey();
        return mostExpensivePostcode;
    }





    private void initializeDbConnection() throws Exception {
        Class.forName("com.mysql.jdbc.Driver").newInstance();
        con = DriverManager.getConnection("jdbc:mysql://localhost:3306/insta?&serverTimezone=UTC", "root", "");
    }

    private void closeDbConnection() throws SQLException {
        con.close();
    }

}
