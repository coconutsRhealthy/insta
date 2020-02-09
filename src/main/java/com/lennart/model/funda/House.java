package com.lennart.model.funda;



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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        House house = (House) o;
        boolean equal = false;

        if(address.equals(house.getAddress()) && price == house.getPrice() && postCode.equals(house.getPostCode())
                && city.equals(house.getCity()) && oppervlakte == house.getOppervlakte() && makelaar.equals(house.getMakelaar())
                && numberOfRooms == house.getNumberOfRooms() && oppervlakte == house.getOppervlakte()) {
            equal = true;
        }

        return equal;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = address.hashCode();
        result = 31 * result + postCode.hashCode();
        result = 31 * result + city.hashCode();
        temp = Double.doubleToLongBits(oppervlakte);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + makelaar.hashCode();
        temp = Double.doubleToLongBits(price);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(numberOfRooms);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }
}
