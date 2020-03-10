package com.lennart.model.funda;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by LennartMac on 11/02/2020.
 */
public class ConsolePrint {

    public static void main(String[] args) throws Exception {
        new ConsolePrint().testMethod();
    }

    private void testMethod() throws Exception {
        ForSaleGrader forSaleGrader = new ForSaleGrader();
        LinkedHashMap<House, PostCode> cheapestHouses = forSaleGrader.getTop10CheapestHousesAndAveragePrices();

        System.out.println();
        System.out.println();
        System.out.println();
        System.out.println();

        String format = "%-5s%-35s%-20s%-20s%-20s%-20s%-15s%n";
        System.out.printf(format, "", "", "", "", "Gemiddelde", "", "Gemiddelde");
        System.out.printf(format, "", "", "", "", "Vraagprijs", "Vraagprijs", "Vraapgrijs per m2");
        System.out.printf(format, "", "Adres", "Plaats", "Vraagprijs", "Postcodegebied", "per m2", "Postcodegebied");
        System.out.println();

        int counter = 0;

        List<House> weirdHouses = new ArrayList<>();

        for(Map.Entry<House, PostCode> entry : cheapestHouses.entrySet()) {
            double housePrice = entry.getKey().getPrice();
            double housePriceM2 = entry.getKey().getPriceM2();
            double averagePrice = ForSaleGrader.convertPostcodePriceStringToPrice(entry.getValue().getAverageHousePrice_6months());
            double averagePriceM2 = ForSaleGrader.convertPostcodePriceStringToPrice(entry.getValue().getAverageHousePricePerM2_6months());

            if(housePrice > averagePrice * 0.5 && housePriceM2 > averagePriceM2 * 0.5) {
                if(entry.getValue().getNumberOfHousesSold_6months() >= 4) {
                    counter++;

                    House house = entry.getKey();
                    PostCode postCode = entry.getValue();

                    System.out.printf(format, counter, house.getAddress(), house.getCity(), convertPriceToCorrectStringFormat(house.getPrice()),
                            postCode.getAverageHousePrice_6months(), convertPriceToCorrectStringFormat(house.getPriceM2()), postCode.getAverageHousePricePerM2_6months());
                } else {
                    break;
                }
            } else {
                weirdHouses.add(entry.getKey());
            }
        }

        if(!weirdHouses.isEmpty()) {
            for(int i = 0; i < 15; i++) {
                System.out.println();
            }

            for(House house : weirdHouses) {
                System.out.print(house.getAddress());
                System.out.println(house.getCity());
            }
        }
    }

    private String convertPriceToCorrectStringFormat(Double price) {
        int priceInt = price.intValue();
        String priceString = String.format("%,d", priceInt);
        priceString = priceString.replaceAll(",", ".");
        priceString = "€ " + priceString;
        return priceString;
    }
}
