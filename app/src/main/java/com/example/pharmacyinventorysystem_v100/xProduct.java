package com.example.pharmacyinventorysystem_v100;

public class xProduct {
    String ProductName, Packaging, ProductID;

    public xProduct() {

    }

    public xProduct(String productName, String packaging, String productID) {
        ProductName = productName;
        Packaging = packaging;
        ProductID = productID;
    }

    public String getProductName() {
        return ProductName;
    }

    public String getPackaging() {
        return Packaging;
    }

    public String getProductID() {
        return ProductID;
    }

    public void setProductName(String productName) {
        ProductName = productName;
    }

    public void setPackaging(String packaging) {
        Packaging = packaging;
    }

    public void setProductID(String productID) {
        ProductID = productID;
    }
}
