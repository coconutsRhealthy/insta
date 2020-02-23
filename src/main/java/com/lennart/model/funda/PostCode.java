package com.lennart.model.funda;

/**
 * Created by LennartMac on 03/02/2020.
 */
public class PostCode {

    private String city;
    private int numberOfHousesSold;
    private String averageHousePrice;
    private String averageHousePricePerM2;
    private String mostUsedMakelaar;

    private String postCodeString;

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public int getNumberOfHousesSold() {
        return numberOfHousesSold;
    }

    public void setNumberOfHousesSold(int numberOfHousesSold) {
        this.numberOfHousesSold = numberOfHousesSold;
    }

    public String getAverageHousePrice() {
        return averageHousePrice;
    }

    public void setAverageHousePrice(String averageHousePrice) {
        this.averageHousePrice = averageHousePrice;
    }

    public String getAverageHousePricePerM2() {
        return averageHousePricePerM2;
    }

    public void setAverageHousePricePerM2(String averageHousePricePerM2) {
        this.averageHousePricePerM2 = averageHousePricePerM2;
    }

    public String getMostUsedMakelaar() {
        return mostUsedMakelaar;
    }

    public void setMostUsedMakelaar(String mostUsedMakelaar) {
        this.mostUsedMakelaar = mostUsedMakelaar;
    }

    public String getPostCodeString() {
        return postCodeString;
    }

    public void setPostCodeString(String postCodeString) {
        this.postCodeString = postCodeString;
    }
}
