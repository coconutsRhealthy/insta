package com.lennart.model.funda;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by LennartMac on 16/01/2020.
 */
public class DataFromPageRetriever {

    public List<House> gatherHouseData(File input) throws Exception {
        List<House> houseData = new ArrayList<>();

        Document document = Jsoup.parse(input, "UTF-8", "http://www.eije.com/");

        Elements allSearchResults = document.select("div.search-result-main");

        for(Element searchResult : allSearchResults) {
            House house = new House();

            house.setAddress(getAddress(searchResult));
            house.setPostCode(getPostCode(searchResult));
            house.setCity(getCity(searchResult));
            house.setOppervlakte(getOppervlakte(searchResult));
            house.setMakelaar(getMakelaar(searchResult));
            house.setPrice(getPrice(searchResult));
            house.setPriceM2(getPriceM2(house.getPrice(), house.getOppervlakte()));
            house.setDateAtPage(document.select("div.result-separator").first().text());
            house.setCurrentDate(getCurrentDate());
            house.setNumberOfRooms(getAantalKamers(searchResult));

            houseData.add(house);
        }

        return houseData;
    }

    private String getAddress(Element element) {
        String address = element.select("a > h3").text();
        address = address.replaceAll("'", "''");
        return address;
    }

    private String getPostCode(Element element) {
        String postcode;

        String baseString = element.select("a > h4").text();
        String[] parts = baseString.split(" ");

        if(parts.length == 2) {
            postcode = parts[0];
        } else if(parts.length > 2) {
            if(parts[1].length() == 2) {
                postcode = parts[0] + " " + parts[1];
            } else {
                postcode = parts[0];
            }
        } else {
            postcode = "-1";
        }

        while(postcode.startsWith(" ")) {
            postcode = postcode.substring(1, postcode.length());
        }

        while(postcode.endsWith(" ")) {
            postcode = postcode.substring(0, postcode.length() - 1);
        }

        postcode = postcode.replaceAll("'", "''");

        return postcode;
    }

    private String getCity(Element element) {
        String baseString = element.select("a > h4").text();
        String partToRemove = getPostCode(element);

        String city = baseString.replace(partToRemove, "");

        while(city.startsWith(" ")) {
            city = city.substring(1, city.length());
        }

        while(city.endsWith(" ")) {
            city = city.substring(0, city.length() - 1);
        }

        city = city.replaceAll("'", "''");

        return city;
    }

    private double getOppervlakte(Element element) {
        String oppervlakteString = element.select("span[title~=Gebruiksoppervlakte wonen]").text();
        oppervlakteString = removeAllNonNumericCharacters(oppervlakteString);

        double oppervlakte;

        try {
            oppervlakte = Double.valueOf(oppervlakteString);

            if(oppervlakteString.contains(".")) {
                oppervlakte = oppervlakte * 1000;
            }
        } catch (NumberFormatException e) {
            oppervlakte = -1;
        }

        return oppervlakte;
    }

    private double getAantalKamers(Element element) {
        String kamersString = element.select("ul.search-result-kenmerken > li:nth-child(2)").text();

        double aantalKamers;

        if(!kamersString.isEmpty()) {
            aantalKamers = Double.valueOf(kamersString.substring(0, kamersString.indexOf(" ")));
        } else {
            aantalKamers = -1;
        }

        return aantalKamers;
    }

    private double getPrice(Element element) {
        String priceString = element.select("span.search-result-price").text();
        priceString = removeAllNonNumericCharacters(priceString);
        priceString = priceString.replaceAll("\\.", "");

        double price;

        try {
            price = Double.valueOf(priceString);
        } catch (NumberFormatException e) {
            price = -1;
        }

        return price;
    }

    private String getMakelaar(Element element) {
        String makelaar = element.select("span.search-result-makelaar-name").text();
        makelaar = makelaar.replaceAll("'", "''");
        return makelaar;
    }

    private double getPriceM2(double price, double m2) {
        double priceM2;

        if(price < 0 || m2 < 0) {
            priceM2 = -1;
        } else {
            priceM2 = price / m2;
        }

        return priceM2;
    }

    private String removeAllNonNumericCharacters(String string) {
        String stringToReturn = string.replaceAll("[^\\d.]", "");

        if(stringToReturn.startsWith(".")) {
            stringToReturn = "0" + stringToReturn;
        }

        return stringToReturn;
    }

    private String getCurrentDate() {
        java.util.Date date = new java.util.Date();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return dateFormat.format(date);
    }
}
