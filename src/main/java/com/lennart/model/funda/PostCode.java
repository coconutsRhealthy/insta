package com.lennart.model.funda;

/**
 * Created by LennartMac on 03/02/2020.
 */
public class PostCode {

    private String city;
    private int numberOfHousesSold;
    private double averageHousePrice;
    private double averageHousePricePerM2;
    private String mostUsedMakelaar;

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

    public double getAverageHousePrice() {
        return averageHousePrice;
    }

    public void setAverageHousePrice(double averageHousePrice) {
        this.averageHousePrice = averageHousePrice;
    }

    public double getAverageHousePricePerM2() {
        return averageHousePricePerM2;
    }

    public void setAverageHousePricePerM2(double averageHousePricePerM2) {
        this.averageHousePricePerM2 = averageHousePricePerM2;
    }

    public String getMostUsedMakelaar() {
        return mostUsedMakelaar;
    }

    public void setMostUsedMakelaar(String mostUsedMakelaar) {
        this.mostUsedMakelaar = mostUsedMakelaar;
    }
}
