package com.example.johnrarui.tss;

/**
 * Created by john rarui on 10/11/2017.
 */

public class PriceCalc {
    double distance;
    private double unitPrice;
    double price;

    public double getDistance() {
        return distance;
    }


    public double getUnitPrice() {
        return unitPrice;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public Double calcPrice(double dist,double unitPrice) {

        this.price = dist*unitPrice;
        return price;
    }

    public double getPrice() {
        return price;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }

}
