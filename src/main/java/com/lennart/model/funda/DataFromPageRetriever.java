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
            house.setDateAtPage(document.select("div.result-separator").text());
            house.setCurrentDate(getCurrentDate());

            houseData.add(house);
        }

        return houseData;
    }

    private String getAddress(Element element) {
        String address = element.select("a > h3").text();
        address = address.replaceAll("'", "");
        return address;
    }

    private String getPostCode(Element element) {
        String postCode = element.select("a > h4").text();
        postCode = postCode.substring(0, postCode.lastIndexOf(" "));
        postCode = postCode.replaceAll("'", "");
        return postCode;
    }

    private String getCity(Element element) {
        String city = element.select("a > h4").text();
        city = city.substring(city.lastIndexOf(" ") + 1);
        city = city.replaceAll("'", "");
        return city;
    }

    private double getOppervlakte(Element element) {
        String oppervlakteString = element.select("span[title~=Gebruiksoppervlakte wonen]").text();
        oppervlakteString = removeAllNonNumericCharacters(oppervlakteString);

        double oppervlakte;

        try {
            oppervlakte = Double.valueOf(oppervlakteString);
        } catch (NumberFormatException e) {
            oppervlakte = -1;
        }

        return oppervlakte;
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
        makelaar = makelaar.replaceAll("'", "");
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









    private void printAddresses() throws Exception {
        //initializeDbConnection();

        //for(int z = 2; z < 693; z++) {
            //Document document = Jsoup.connect("https://www.funda.nl/koop/amsterdam/verkocht/sorteer-afmelddatum-af/p" + z + "/").get();
            File input = new File("/Users/LennartMac/Documents/aapje.htm");
            Document document = Jsoup.parse(input, "UTF-8", "http://www.eije.com/");

            Elements addressElements = document.select("a > h3");

            List<String> addressList = new ArrayList<>();
            List<String> postCodeList = new ArrayList<>();
            List<String> oppervlakteList = new ArrayList<>();
            List<String> makelaarList = new ArrayList<>();
            List<String> priceList = new ArrayList<>();

            for(Element addressElement : addressElements) {
                addressList.add(addressElement.text());
            }

            Elements postCodes = document.select("a > h4");

            for(Element postCode : postCodes) {
                postCodeList.add(postCode.text());
            }

            Elements oppervlaktes = document.select("span[title~=Gebruiksoppervlakte wonen]");

            for(Element oppervlak : oppervlaktes) {
                oppervlakteList.add(oppervlak.text());
            }

            Elements makelaars = document.select("span.search-result-makelaar-name");

            for(Element makelaar : makelaars) {
                makelaarList.add(makelaar.text());
            }

            Elements prices = document.select("span.search-result-price");

            for(Element price : prices) {
                priceList.add(price.text());
            }

            ///////

            System.out.println("wacht");

//            initializeDbConnection();
//            Statement st = con.createStatement();
//
//            for(int i = 0; i < addressList.size(); i++) {
//                st.executeUpdate("INSERT INTO funda (" +
//                        "straat, " +
//                        "postcode, " +
//                        "oppervlakte, " +
//                        "prijs, " +
//                        "makelaar) " +
//                        "VALUES ('" +
//                        addressList.get(i) + "', '" +
//                        postCodeList.get(i) + "', '" +
//                        oppervlakteList.get(i) + "', '" +
//                        priceList.get(i) + "', '" +
//                        makelaarList.get(i) + "'" +
//                        ")");
//            }
//
//            st.close();
//
//            //System.out.println(z);
//        //}
//
//        closeDbConnection();
    }
}
