package com.lennart.model.funda;

/**
 * Created by LennartMac on 02/03/2020.
 */
public class CityInfoRetriever extends PostCodeInfoRetriever {

    public static void main(String[] args) throws Exception {
        PostCode hmm = new CityInfoRetriever().getPostCodeData("Amsterdam");
        System.out.println("wacht");
    }

    @Override
    String getQuery(String city) {
        city = city.replace("'", "");

        String query = "SELECT * FROM funda3 WHERE plaats = '" + city + "' AND prijs > 0;";

        return query;
    }
}
