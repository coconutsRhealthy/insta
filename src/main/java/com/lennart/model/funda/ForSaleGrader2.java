package com.lennart.model.funda;

import com.lennart.model.Aandacht;

import java.io.File;
import java.util.*;

/**
 * Created by LennartMac on 16/02/2020.
 */
public class ForSaleGrader2 {

    public static void main(String[] args) throws Exception {
        List<House> interestingHouses = new ForSaleGrader2().getInterestingHouses();
        Map<House, Double> ranked = new ForSaleGrader2().rankInterestingHouses(interestingHouses);
        System.out.println("wachtff");
    }

    private Map<House, Double> rankInterestingHouses(List<House> interestingHouses) throws Exception {
        Map<House, Double> interestingHousesRanked = new HashMap<>();

        for(House house : interestingHouses) {
            //String postCodeString = house.getPostCode().substring(0, house.getPostCode().length() - 1);
            String postCodeString = house.getPostCode();
            List<House> relatedHouses = new SimilarHousesFinder().getSimilarHouses(postCodeString);

            double totalPrice = 0;
            double priceCount = 0;

            for(House relatedHouse : relatedHouses) {
                totalPrice = totalPrice + relatedHouse.getPriceM2();
                priceCount++;
            }

            double average = totalPrice / priceCount;
            double priceDiff = average - house.getPriceM2();

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
        //List<Double> grades = new ArrayList<>();
        //List<Double> gradesM2 = new ArrayList<>();

        for(House house : allHouses) {
            System.out.println("A");
            if(houseMeetsCriteriaOfPostCode(house)) {
                System.out.println("B");
                //String postCodeStringToUse = house.getPostCode().substring(0, house.getPostCode().length() - 1);
                String postCodeStringToUse = house.getPostCode();
                List<House> similarHouses = new SimilarHousesFinder().getSimilarHouses(postCodeStringToUse);

                if(similarHouses.size() >= 1) {
                    //hier moet check komen of jouw huis geen outlier is... tov similar houses
                    double averagePrice = new SimilarHousesFinder().getAveragePriceOrM2OfHouseList(similarHouses, false);
                    double averageM2 = new SimilarHousesFinder().getAveragePriceOrM2OfHouseList(similarHouses, true);

                    if(house.getPrice() > 0.68 * averagePrice) {
                        if(house.getOppervlakte() >= 0.7 * averageM2 && house.getOppervlakte() <= 1.3 * averageM2) {
                            double houseGrade = getHouseGrade(house, similarHouses, false);
                            double houseGradeM2 = getHouseGrade(house, similarHouses, true);

                            if(houseGrade <= 0.5 && houseGradeM2 <= 0.5) {
                                interestingHouses.add(house);
                            }
                        }
                    }

                    //grades.add(houseGrade);
                    //gradesM2.add(houseGradeM2);
                }
            }
        }

        //Collections.sort(grades);
        //Collections.sort(gradesM2);
        return interestingHouses;
    }

    private double getHouseGrade(House newHouse, List<House> similarHouses, boolean m2) {
        Map<House, Double> housePriceMap = new HashMap<>();

        if(!m2) {
            housePriceMap.put(newHouse, newHouse.getPrice());

            for(House house : similarHouses) {
                housePriceMap.put(house, house.getPrice());
            }
        } else {
            housePriceMap.put(newHouse, newHouse.getPriceM2());

            for(House house : similarHouses) {
                housePriceMap.put(house, house.getPriceM2());
            }
        }

        housePriceMap = Aandacht.sortByValueLowToHigh(housePriceMap);

        double indexOfHouse = 1;

        for(Map.Entry<House, Double> entry : housePriceMap.entrySet()) {
            if(entry.getKey().equals(newHouse)) {
                break;
            }
            indexOfHouse++;
        }

        double houseGrade = indexOfHouse / (0.0 + housePriceMap.size());
        return houseGrade;
    }

    private boolean houseMeetsCriteriaOfPostCode(House house) throws Exception {
        //String postCodeStringToUse = house.getPostCode().substring(0, house.getPostCode().length() - 1);
        String postCodeStringToUse = house.getPostCode();

        SimilarHousesFinder similarHousesFinder = new SimilarHousesFinder();

        List<House> allHousesOfPostcode = similarHousesFinder.getAllHousesOfPostCode(postCodeStringToUse);
        double averagePrice = similarHousesFinder.getAveragePriceOrM2OfHouseList(allHousesOfPostcode, false);
        double averageM2 = similarHousesFinder.getAveragePriceOrM2OfHouseList(allHousesOfPostcode, true);

        boolean criteriaMet = false;

        if(house.getPrice() > 0.68 * averagePrice) {
            if(house.getOppervlakte() >= 0.7 * averageM2 && house.getOppervlakte() <= 1.3 * averageM2) {
                criteriaMet = true;
            }
        }

        return criteriaMet;
    }



}

