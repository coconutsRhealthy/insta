package com.lennart.model.funda;

/**
 * Created by LennartMac on 02/03/2020.
 */
public class CityInfoRetriever extends PostCodeInfoRetriever {

    public static void main(String[] args) throws Exception {
        PostCode hmm = new CityInfoRetriever().getPostCodeData("Amsterdam", "12months");
        System.out.println("wacht");
    }

    @Override
    String getQuery(String city, String searchPeriod) {
        city = city.replace("'", "");

        String query = "SELECT * FROM funda3 WHERE plaats LIKE '%" + city + "%' AND prijs > 0";

        if(searchPeriod.equals("6months")) {
            query = query + " AND datum_op_pagina != 'Langer dan 6 maanden'";
        }

        query = query + ";";

        return query;
    }
}
