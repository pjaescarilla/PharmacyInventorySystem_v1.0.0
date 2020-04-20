package com.example.pharmacyinventorysystem_v100;

import android.app.Application;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

import java.util.ArrayList;
import java.util.List;

public class GlobalVariables extends Application {
    private String lastCheckedDate="";
    private String thisBranch ="Philam";
    private String toDoToStock="";
    private String branchToMoveTo="";
    private List<String> notifiedBranchesForCritLevel = new ArrayList<>();
    private List<String> notifiedProductForCritLevel = new ArrayList<>();
    private Table<String,String,Integer> notifIDMapping = HashBasedTable.create();
    private int notifID=3000;
    private int stockCountChange=0;
    private Product selectedProduct;
    private StockDetails stockToMove;

    public String getLastCheckedDate() {
        return lastCheckedDate;
    }

    public void setLastCheckedDate(String lastCheckedDate) {
        this.lastCheckedDate = lastCheckedDate;
    }

    public void addNotifiedBranch (String branch) {
        notifiedBranchesForCritLevel.add(branch);
    }

    public void removeFromNotifiedList (String branch) {
        notifiedBranchesForCritLevel.remove(branch);
    }

    public void clearNotifiedBranchList () {
        notifiedBranchesForCritLevel.clear();
    }

    public boolean thisBranchNotified (String branch) {
        return notifiedBranchesForCritLevel.contains(branch);
    }

    public int getNewNotifID(){
        int thisNotifID=notifID;
        notifID=notifID+1;
        return thisNotifID;
    }

    public boolean alreadyNotifiedThis(String branchName, String productID) {
        return notifIDMapping.contains(branchName,productID);
    }

    public void addToNotifMapping(String branch, String productID, int notifID) {
        notifIDMapping.put(branch,productID,notifID);
    }

    public void removeFromMapping(String branch, String productID, int notifID) {
        notifIDMapping.remove(branch,productID);
    }

    public int getExistingNotifID(String branch, String productID) {
        return notifIDMapping.get(branch,productID);
    }

    public String getThisBranch() {
        return thisBranch;
    }

    public void setThisBranch(String thisBranch) {
        this.thisBranch = thisBranch;
    }

    public Product getSelectedProduct() {
        return selectedProduct;
    }

    public void setSelectedProduct(Product selectedProduct) {
        this.selectedProduct = selectedProduct;
    }

    public String getToDoToStock() {
        return toDoToStock;
    }

    public void setToDoToStock(String toDoToStock) {
        this.toDoToStock = toDoToStock;
    }

    public void addNotifiedProduct (String product) {
        notifiedProductForCritLevel.add(product);
    }

    public boolean thisProductNotified (String branch) {
        return notifiedProductForCritLevel.contains(branch);
    }

    public String getBranchToMoveTo() {
        return branchToMoveTo;
    }

    public void setBranchToMoveTo(String branchToMoveTo) {
        this.branchToMoveTo = branchToMoveTo;
    }

    public StockDetails getStockToMove() {
        return stockToMove;
    }

    public void setStockToMove(StockDetails stockToMove) {
        this.stockToMove = stockToMove;
    }

    public int getStockCountChange() {
        return stockCountChange;
    }

    public void setStockCountChange(int stockCountChange) {
        this.stockCountChange = stockCountChange;
    }
}
