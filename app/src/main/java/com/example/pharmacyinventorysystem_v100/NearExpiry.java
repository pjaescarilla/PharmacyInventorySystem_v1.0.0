package com.example.pharmacyinventorysystem_v100;

public class NearExpiry {
    String stockID,expiryDate,productName,packaging;
    int stockCount;

    public NearExpiry() {

    }

    public NearExpiry(String stockID, String expiryDate, String productName, String packaging, int stockCount) {
        this.stockID = stockID;
        this.expiryDate = expiryDate;
        this.productName = productName;
        this.packaging = packaging;
        this.stockCount = stockCount;
    }

    public String getStockID() {
        return stockID;
    }

    public void setStockID(String stockID) {
        this.stockID = stockID;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
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

    public int getStockCount() {
        return stockCount;
    }

    public void setStockCount(int stockCount) {
        this.stockCount = stockCount;
    }
}
