package com.example.pharmacyinventorysystem_v100;

public class HighDemand {
    String productName, packaging;
    double demand;

    public HighDemand() {

    }

    public HighDemand(String productName, String packaging, double demand) {
        this.productName = productName;
        this.packaging = packaging;
        this.demand = demand;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getPackaging() {
        return packaging;
    }

    public void setPackaging(String packaging) {
        this.packaging = packaging;
    }

    public double getDemand() {
        return demand;
    }

    public void setDemand(double demand) {
        this.demand = demand;
    }
}
