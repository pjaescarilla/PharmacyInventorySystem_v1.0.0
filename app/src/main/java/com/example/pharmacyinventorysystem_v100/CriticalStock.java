package com.example.pharmacyinventorysystem_v100;

public class CriticalStock {
    String branchName,productName,packaging,productID;
    int quantity;

    public CriticalStock() {
    }

    public CriticalStock(String branchName, String productName, String packaging, String productID, int quantity) {
        this.branchName = branchName;
        this.productName = productName;
        this.packaging = packaging;
        this.productID = productID;
        this.quantity = quantity;
    }

    public CriticalStock(String productName, String packaging, int quantity) {
        this.productName = productName;
        this.packaging = packaging;
        this.quantity = quantity;
    }

    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
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

    public String getProductID() {
        return productID;
    }

    public void setProductID(String productID) {
        this.productID = productID;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
