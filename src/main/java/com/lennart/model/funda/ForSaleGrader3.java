package com.lennart.model.funda;

import com.lennart.model.Aandacht;

import java.io.File;
import java.util.*;

/**
 * Created by LennartMac on 17/02/2020.
 */
public class ForSaleGrader3 {

    //get all houses sold with same postcode

    //bereken daar gemiddelde prijs van

    //en de gemiddelde m2 prijs

    //als jouw prijs lager is, maar niet lager dan 88% van de gemiddelde prijs, dan is hij interessant

    public static void main(String[] args) throws Exception {
        List<House> interestingHouses = new ForSaleGrader3().getInterestingHouses();
        Map<House, Double> rankedPrice = new ForSaleGrader3().rankInterestingHouses(interestingHouses, false);
        Map<House, Double> rankedPriceM2 = new ForSaleGrader3().rankInterestingHouses(interestingHouses, true);

        //via m2 is het best...

        System.out.println("wacht");
    }

    private Map<House, Double> rankInterestingHouses(List<House> interestingHouses, boolean m2) throws Exception {
        Map<House, Double> interestingHousesRanked = new HashMap<>();

        for(House house : interestingHouses) {
            String postCodeString = house.getPostCode();
            List<House> samePostCodeHouses = new SimilarHousesFinder().getAllHousesOfPostCode(postCodeString);

            double totalPrice = 0;
            double priceCount = 0;

            for(House samePostCodeHouse : samePostCodeHouses) {
                if(m2) {
                    totalPrice = totalPrice + samePostCodeHouse.getPriceM2();
                } else {
                    totalPrice = totalPrice + samePostCodeHouse.getPrice();
                }

                priceCount++;
            }

            double average = totalPrice / priceCount;

            double priceDiff;

            if(m2) {
                priceDiff = average - house.getPriceM2();
            } else {
                priceDiff = average - house.getPrice();
            }

            interestingHousesRanked.put(house, priceDiff);
        }

        interestingHousesRanked = Aandacht.sortByValueHighToLow(interestingHousesRanked);
        return interestingHousesRanked;
    }

    private List<House> getInterestingHouses() throws Exception {
        List<File> allHtmlFiles = new HousePersister().getAllHtmlFilesFromDir("/Users/LennartMac/Documents/huizenstuff/vandaagtekoop2");
        List<House> allHouses = new ArrayList<>();
        DataFromPageRetriever dataFromPageRetriever = new DataFromPageRetriever();

        for(File input : allHtmlFiles) {
            allHouses.addAll(dataFromPageRetriever.gatherHouseData(input, false));
        }

        List<House> interestingHouses = new ArrayList<>();

        int counter = 0;

        for(House house : allHouses) {
            System.out.println(counter++);
            List<House> allHousesOfPostCode = getAllHousesOfPostCode(house.getPostCode());

            if(!allHousesOfPostCode.isEmpty()) {
                double averagePrice = getAverageHousePrice(allHousesOfPostCode, false);
                double averagePriceM2 = getAverageHousePrice(allHousesOfPostCode, true);
                double cheapestHouse = getCheapestHouseOfList(allHousesOfPostCode);

                if(house.getPrice() < averagePrice && house.getPriceM2() < averagePriceM2) {
                    if(house.getPrice() > 0 && house.getPriceM2() > 0) {
                        if(house.getPrice() >= (0.85 * averagePrice)) {
                            if(house.getPrice() < cheapestHouse) {
                                interestingHouses.add(house);
                            }
                        }
                    }
                }
            }
        }

        return interestingHouses;
    }

    private List<House> getAllHousesOfPostCode(String postCode) throws Exception {
        return new SimilarHousesFinder().getAllHousesOfPostCode(postCode);
    }

    private double getAverageHousePrice(List<House> allHousesOfPostCode, boolean m2) {
        double average = 0;
        double totalCount = 0;

        for(House house : allHousesOfPostCode) {
            if(m2) {
                average = average + house.getPriceM2();
            } else {
                average = average + house.getPrice();
            }

            totalCount++;
        }

        return average / totalCount;
    }

    private double getCheapestHouseOfList(List<House> houseList) {
        List<Double> prices = new ArrayList<>();

        for(House house : houseList) {
            prices.add(house.getPrice());
        }

        Collections.sort(prices);

        return prices.get(0);
    }




}
