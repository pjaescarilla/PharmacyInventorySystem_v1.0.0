package com.example.pharmacyinventorysystem_v100;

public class Invoice {
    String productID, stockID, invoiceID, productName, packaging, branchName;
    int quantity;
    long invoiceDate;

    public Invoice() {

    }

    public Invoice(String productID, String stockID, String invoiceID, String productName, String packaging, String branchName, int quantity, long invoiceDate) {
        this.productID = productID;
        this.stockID = stockID;
        this.invoiceID = invoiceID;
        this.productName = productName;
        this.packaging = packaging;
        this.branchName = branchName;
        this.quantity = quantity;
        this.invoiceDate = invoiceDate;
    }


    public String getProductID() {
        return productID;
    }

    public void setProductID(String productID) {
        this.productID = productID;
    }

    public String getStockID() {
        return stockID;
    }

    public void setStockID(String stockID) {
        this.stockID = stockID;
    }

    public String getInvoiceID() {
        return invoiceID;
    }

    public void setInvoiceID(String invoiceID) {
        this.invoiceID = invoiceID;
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

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public long getInvoiceDate() {
        return invoiceDate;
    }

    public void setInvoiceDate(long invoiceDate) {
        this.invoiceDate = invoiceDate;
    }
}
