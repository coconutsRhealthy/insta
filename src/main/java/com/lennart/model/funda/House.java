package com.lennart.model.funda;

import java.util.Date;

/**
 * Created by LennartMac on 23/01/2020.
 */
public class House {

    String address;
    String postCode;
    String city;
    double oppervlakte;
    String makelaar;
    double price;
    String dateAtPage;
    String currentDate;
    double priceM2;
    double numberOfRooms;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPostCode() {
        return postCode;
    }

    public void setPostCode(String postCode) {
        this.postCode = postCode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public double getOppervlakte() {
        return oppervlakte;
    }

    public void setOppervlakte(double oppervlakte) {
        this.oppervlakte = oppervlakte;
    }

    public String getMakelaar() {
        return makelaar;
    }

    public void setMakelaar(String makelaar) {
        this.makelaar = makelaar;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getDateAtPage() {
        return dateAtPage;
    }

    public void setDateAtPage(String dateAtPage) {
        this.dateAtPage = dateAtPage;
    }

    public String getCurrentDate() {
        return currentDate;
    }

    public void setCurrentDate(String currentDate) {
        this.currentDate = currentDate;
    }

    public double getPriceM2() {
        return priceM2;
    }

    public void setPriceM2(double priceM2) {
        this.priceM2 = priceM2;
    }

    public double getNumberOfRooms() {
        return numberOfRooms;
    }

    public void setNumberOfRooms(double numberOfRooms) {
        this.numberOfRooms = numberOfRooms;
    }
}
