package com.lennart.model.funda;

import java.util.LinkedHashMap;
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

        String format = "%-5s%-40s%-35s%-25s%-22s%-22s%-15s%n";
        System.out.printf(format, "", "", "", "", "Gemiddelde", "", "Gemiddelde");
        System.out.printf(format, "", "", "", "", "Vraagprijs", "", "Vraapgrijs per m2");
        System.out.printf(format, "", "Adres", "Plaats", "Vraagprijs", "Postcode", "Vraagprijs per m2", "Postcode");
        System.out.println();

        int counter = 0;

        for(Map.Entry<House, PostCode> entry : cheapestHouses.entrySet()) {
            double housePrice = entry.getKey().getPrice();
            double housePriceM2 = entry.getKey().getPriceM2();
            double averagePrice = forSaleGrader.convertPostcodePriceStringToPrice(entry.getValue().getAverageHousePrice());
            double averagePriceM2 = forSaleGrader.convertPostcodePriceStringToPrice(entry.getValue().getAverageHousePricePerM2());

            if(housePrice > averagePrice * 0.5 && housePriceM2 > averagePriceM2 * 0.5) {
                if(counter < 10) {
                    counter++;

                    House house = entry.getKey();
                    PostCode postCode = entry.getValue();

                    System.out.printf(format, counter, house.getAddress(), house.getCity(), convertPriceToCorrectStringFormat(house.getPrice()),
                            postCode.getAverageHousePrice(), convertPriceToCorrectStringFormat(house.getPriceM2()), postCode.getAverageHousePricePerM2());
                } else {
                    break;
                }
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
