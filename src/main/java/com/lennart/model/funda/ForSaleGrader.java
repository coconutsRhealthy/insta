package com.lennart.model.funda;

import com.lennart.model.Aandacht;

import java.io.File;
import java.util.*;

/**
 * Created by LennartMac on 10/02/2020.
 */
public class ForSaleGrader {

    public LinkedHashMap<House, PostCode> getTop10CheapestHousesAndAveragePrices() throws Exception {
        List<File> allHtmlFiles = new HousePersister().getAllHtmlFilesFromDir("/Users/LennartMac/Documents/huizenstuff/vandaagtekoop");
        List<House> allHouses = new ArrayList<>();
        DataFromPageRetriever dataFromPageRetriever = new DataFromPageRetriever();

        for(File input : allHtmlFiles) {
            allHouses.addAll(dataFromPageRetriever.gatherHouseData(input));
        }

        Map<House, PostCode> allHousesBelowAveragePriceMap = getAllHousesBelowAveragePrice(allHouses);
        List<House> allHousesBelowAveragePrice = new ArrayList<>(allHousesBelowAveragePriceMap.keySet());
        Map<String, Double> sortedPriceDiffMap = getSortedMapOfPriceDiff(allHousesBelowAveragePrice, false);
        Map<String, Double> sortedPriceDiffM2Map = getSortedMapOfPriceDiff(allHousesBelowAveragePrice, true);

        Map<String, List<Double>> indexMap = initializeIndexMap(allHousesBelowAveragePrice);

        List<String> priceDiffMapKeyList = new ArrayList<>(sortedPriceDiffMap.keySet());
        List<String> priceDiffMapM2KeyList = new ArrayList<>(sortedPriceDiffM2Map.keySet());

        for(Map.Entry<String, List<Double>> entry : indexMap.entrySet()) {
           entry.getValue().addAll(getIndexListForAddress(entry.getKey(), priceDiffMapKeyList, priceDiffMapM2KeyList));
        }

        Map<String, Double> sortedAverageMap = getSortedAverageMap(indexMap);
        List<House> top10CheapestHouses = convertToHouseList(sortedAverageMap, allHousesBelowAveragePrice);

        LinkedHashMap<House, PostCode> cheapHousePostCodeMap = new LinkedHashMap<>();

        for(House house : top10CheapestHouses) {
            cheapHousePostCodeMap.put(house, allHousesBelowAveragePriceMap.get(house));
        }

        return cheapHousePostCodeMap;
    }

    private List<House> convertToHouseList(Map<String, Double> sortedAverageMap, List<House> allHouses) {
        List<House> houseListToReturn = new ArrayList<>();

        for(Map.Entry<String, Double> entry : sortedAverageMap.entrySet()) {
            String address = entry.getKey();

            for(House house : allHouses) {
                if(house.getAddress().equals(address)) {
                    houseListToReturn.add(house);
                }
            }
        }

        return houseListToReturn;
    }

    private Map<String, List<Double>> initializeIndexMap(List<House> allHousesBelow) {
        Map<String, List<Double>> indexMap = new HashMap<>();

        for(House house : allHousesBelow) {
            indexMap.put(house.getAddress(), new ArrayList<>());
        }

        return indexMap;
    }

    private Map<String, Double> getSortedAverageMap(Map<String, List<Double>> indexMap) {
        Map<String, Double> sortedAverageMap = new HashMap<>();

        for(Map.Entry<String, List<Double>> entry : indexMap.entrySet()) {
            double value1 = entry.getValue().get(0);
            double value2 = entry.getValue().get(1);
            double average = (value1 + value2) / 2;
            sortedAverageMap.put(entry.getKey(), average);
        }

        sortedAverageMap = Aandacht.sortByValueLowToHigh(sortedAverageMap);
        return sortedAverageMap;
    }

    private List<Double> getIndexListForAddress(String address, List<String> priceDiffMapKeyList,
                                                List<String> priceDiffMapM2KeyList) {
        double indexPriceDiff = (double) priceDiffMapKeyList.indexOf(address);
        double indexPriceDiffM2 = (double) priceDiffMapM2KeyList.indexOf(address);

        List<Double> indexList = new ArrayList<>();
        indexList.add(indexPriceDiff);
        indexList.add(indexPriceDiffM2);

        return indexList;
    }

    private Map<House, PostCode> getAllHousesBelowAveragePrice(List<House> allHouses) throws Exception {
        Map<House, PostCode> housesBelowAveragePrice = new HashMap<>();

        for(House house : allHouses) {
            if(house.getOppervlakte() > 0 && house.getPrice() > 0) {
                String postCodeString = house.getPostCode();
                postCodeString = postCodeString.replaceAll("[^\\d.]", "");
                postCodeString = postCodeString.replaceAll(" ", "");

                PostCode postCode = new PostCodeInfoRetriever().getPostCodeData(postCodeString, "12months");

                if(house.getPrice() < convertPostcodePriceStringToPrice(postCode.getAverageHousePrice())) {
                    if(house.getPrice() / house.getOppervlakte() <
                            convertPostcodePriceStringToPrice(postCode.getAverageHousePricePerM2())) {
                        housesBelowAveragePrice.put(house, postCode);
                    }
                }
            }
        }

        return housesBelowAveragePrice;
    }

    private Map<String, Double> getSortedMapOfPriceDiff(List<House> allHouses, boolean m2) throws Exception {
        Map<String, Double> sortedPriceDiffMap = new HashMap<>();

        for(House house : allHouses) {
            String postCodeString = house.getPostCode();
            postCodeString = postCodeString.replaceAll("[^\\d.]", "");
            postCodeString = postCodeString.replaceAll(" ", "");

            PostCode postCode = new PostCodeInfoRetriever().getPostCodeData(postCodeString, "12months");
            double priceDiff;

            if(m2) {
                priceDiff = convertPostcodePriceStringToPrice(postCode.getAverageHousePricePerM2()) -
                        (house.getPrice() / house.getOppervlakte());
            } else {
                priceDiff = convertPostcodePriceStringToPrice(postCode.getAverageHousePrice()) - house.getPrice();
            }

            sortedPriceDiffMap.put(house.getAddress(), priceDiff);
        }

        sortedPriceDiffMap = sortByValueHighToLow(sortedPriceDiffMap);

        return sortedPriceDiffMap;
    }

    public double convertPostcodePriceStringToPrice(String priceString) {
        String workingPriceString = priceString.replaceAll("[^0-9.]", "");
        workingPriceString = workingPriceString.replaceAll("\\.", "");
        double price = Double.valueOf(workingPriceString);
        return price;
    }

    public static <K, V extends Comparable<? super V>> Map<K, V> sortByValueHighToLow(Map<K, V> map) {
        List<Map.Entry<K, V>> list = new LinkedList<>( map.entrySet() );
        Collections.sort(list, new Comparator<Map.Entry<K, V>>() {
            @Override
            public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2) {
                return (o2.getValue() ).compareTo( o1.getValue());
            }
        });

        Map<K, V> result = new LinkedHashMap<>();
        for (Map.Entry<K, V> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }
}
