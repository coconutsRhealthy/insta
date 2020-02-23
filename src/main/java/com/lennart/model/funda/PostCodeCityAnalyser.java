package com.lennart.model.funda;

import java.sql.*;
import java.util.*;

/**
 * Created by LennartMac on 14/02/2020.
 */
public class PostCodeCityAnalyser {

    private Connection con;

    public static void main(String[] args) throws Exception {
        new PostCodeCityAnalyser().getMostExpensivePostCodeOfCity();
    }

    private String getMostExpensivePostCodeOfCity() throws Exception {
        List<String> allPostcodesOfCity = getAllPostcodesOfCity("Amsterdam");
        Map<String, Double> postCodePriceMap = getPostCodePriceMap(allPostcodesOfCity, false);
        Map<String, Double> postCodePriceMapM2 = getPostCodePriceMap(allPostcodesOfCity, true);

        return "a";
    }

    private Map<String, Double> getPostCodePriceMap(List<String> allPostcodesOfCity, boolean m2) throws Exception {
        Map<String, Double> postCodePriceMap = new HashMap<>();

        for(String postCodeString : allPostcodesOfCity) {
            PostCode postCode = new PostCodeInfoRetriever().getPostCodeData(postCodeString, "12months");

            double priceToUse;

            if(m2) {
                priceToUse = ForSaleGrader.convertPostcodePriceStringToPrice(postCode.getAverageHousePricePerM2());
            } else {
                priceToUse = ForSaleGrader.convertPostcodePriceStringToPrice(postCode.getAverageHousePrice());
            }

            postCodePriceMap.put(postCodeString, priceToUse);
        }

        postCodePriceMap = ForSaleGrader.sortByValueHighToLow(postCodePriceMap);
        return postCodePriceMap;
    }

    private List<String> getAllPostcodesOfCity(String city) throws Exception {
        List<String> allPostcodes = new ArrayList<>();

        initializeDbConnection();

        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM funda3 WHERE plaats = '" + city + "';");

        while(rs.next()) {
            String postCodeString = rs.getString("postcode");
            postCodeString = postCodeString.replaceAll("[^\\d.]", "");
            postCodeString = postCodeString.replaceAll(" ", "");

            allPostcodes.add(postCodeString);
        }

        rs.close();
        st.close();

        Set<String> allPostCodesAsSet = new HashSet<>();
        allPostCodesAsSet.addAll(allPostcodes);

        allPostcodes.clear();
        allPostcodes.addAll(allPostCodesAsSet);

        Collections.sort(allPostcodes);

        return allPostcodes;
    }

    private void initializeDbConnection() throws Exception {
        Class.forName("com.mysql.jdbc.Driver").newInstance();
        con = DriverManager.getConnection("jdbc:mysql://localhost:3306/insta?&serverTimezone=UTC", "root", "");
    }

    private void closeDbConnection() throws SQLException {
        con.close();
    }
}
