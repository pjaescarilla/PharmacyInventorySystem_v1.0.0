package com.example.pharmacyinventorysystem_v100;

public class Product {
    int EOQ, LTOO, reorderPoint;
    double annualDemand,AUPD;
    String drugGroup,holdingCost,orderCost,packaging,productID,productName;

    public Product() {

    }

    public Product(String productName, String packaging, String productID) {
        this.packaging = packaging;
        this.productID = productID;
        this.productName = productName;
    }

    public Product(double AUPD, int EOQ, int LTOO, int reorderPoint, double annualDemand, String drugGroup, String holdingCost, String orderCost, String packaging, String productID, String productName) {
        this.AUPD = AUPD;
        this.EOQ = EOQ;
        this.LTOO = LTOO;
        this.reorderPoint = reorderPoint;
        this.annualDemand = annualDemand;
        this.drugGroup = drugGroup;
        this.holdingCost = holdingCost;
        this.orderCost = orderCost;
        this.packaging = packaging;
        this.productID = productID;
        this.productName = productName;
    }

    public double getAUPD() {
        return AUPD;
    }

    public void setAUPD(int AUPD) {
        this.AUPD = AUPD;
    }

    public int getEOQ() {
        return EOQ;
    }

    public void setEOQ(int EOQ) {
        this.EOQ = EOQ;
    }

    public int getLTOO() {
        return LTOO;
    }

    public void setLTOO(int LTOO) {
        this.LTOO = LTOO;
    }

    public int getReorderPoint() {
        return reorderPoint;
    }

    public void setReorderPoint(int reorderPoint) {
        this.reorderPoint = reorderPoint;
    }

    public double getAnnualDemand() {
        return annualDemand;
    }

    public void setAnnualDemand(double annualDemand) {
        this.annualDemand = annualDemand;
    }

    public String getDrugGroup() {
        return drugGroup;
    }

    public void setDrugGroup(String drugGroup) {
        this.drugGroup = drugGroup;
    }

    public String getHoldingCost() {
        return holdingCost;
    }

    public void setHoldingCost(String holdingCost) {
        this.holdingCost = holdingCost;
    }

    public String getOrderCost() {
        return orderCost;
    }

    public void setOrderCost(String orderCost) {
        this.orderCost = orderCost;
    }

    public String getPackaging() {
        return packaging;
    }

    public void setPackaging(String packaging) {
        this.packaging = packaging;
    }

    public String getProductID() {
        return productID;
    }

    public void setProductID(String productID) {
        this.productID = productID;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }
}
