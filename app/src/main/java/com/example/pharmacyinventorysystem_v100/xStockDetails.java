
package com.example.pharmacyinventorysystem_v100;

public class xStockDetails {
    String expiryDate, stockID, productID, productName, packaging, branchName;
    int stockCount;

    public xStockDetails() {
    }

    public xStockDetails(String expiryDate, String stockID, String productID, int stockCount) {
        this.expiryDate = expiryDate;
        this.stockID = stockID;
        this.productID = productID;
        this.stockCount = stockCount;
    }

    public xStockDetails(String expiryDate, String stockID, String productID, String productName, String packaging, String branchName, int stockCount) {
        this.expiryDate = expiryDate;
        this.stockID = stockID;
        this.productID = productID;
        this.productName = productName;
        this.packaging = packaging;
        this.branchName = branchName;
        this.stockCount = stockCount;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getStockID() {
        return stockID;
    }

    public void setStockID(String stockID) {
        this.stockID = stockID;
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

    public String getPackaging() {
        return packaging;
    }

    public void setPackaging(String packaging) {
        this.packaging = packaging;
    }

    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }

    public int getStockCount() {
        return stockCount;
    }

    public void setStockCount(int stockCount) {
        this.stockCount = stockCount;
    }


}
