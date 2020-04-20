package com.example.pharmacyinventorysystem_v100;

public class StockDetails_backup {
    String expiryDate, stockID, productID;
    int stockCount;

    public StockDetails_backup() {
    }

    public StockDetails_backup(String expiryDate, String stockID, String productID, int stockCount) {
        this.expiryDate = expiryDate;
        this.stockID = stockID;
        this.productID = productID;
        this.stockCount = stockCount;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public String getStockID() {
        return stockID;
    }

    public String getProductID() {
        return productID;
    }

    public int getStockCount() {
        return stockCount;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }

    public void setStockID(String stockID) {
        this.stockID = stockID;
    }

    public void setProductID(String productID) {
        this.productID = productID;
    }

    public void setStockCount(int stockCount) {
        this.stockCount = stockCount;
    }
}
