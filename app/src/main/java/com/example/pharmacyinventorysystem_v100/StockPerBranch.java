package com.example.pharmacyinventorysystem_v100;

public class StockPerBranch {

    private int totalStock;

    public StockPerBranch() {

    }
    public StockPerBranch(int totalStock) {
        this.totalStock = totalStock;
    }

    public int getTotalStock() {
        return totalStock;
    }

    public void setTotalStock(int totalStock) {
        this.totalStock = totalStock;
    }
}
