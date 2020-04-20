package com.example.pharmacyinventorysystem_v100;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.gesture.GestureLibraries;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class BranchToMove extends AppCompatActivity {

    // View variables
    private ListView listViewOtherBranch;

    // Firebase variables
    private DatabaseReference DistanceAndTimeTable, StockDetailsTable;

    // Other variables
    private List<String> branchNames = new ArrayList<>();
    private List<TimeAndDistance> otherBranchesTD = new ArrayList<>();
    private Product selectedProduct;
    private StockDetails stockToMove, selectedStock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_branch_to_move);

        setResolution();
        initializeVariables();
        DistanceAndTimeTable.addListenerForSingleValueEvent(listOtherBranches);
        listViewOtherBranch.setOnItemClickListener(moveProductToOtherBranch);
    }

    // EVENTS ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    ValueEventListener listOtherBranches = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            branchNames.clear();
            otherBranchesTD.clear();
            for (DataSnapshot thisBranch : dataSnapshot.getChildren()) {
                branchNames.add(thisBranch.getKey());
                TimeAndDistance thisBranchTD = thisBranch.getValue(TimeAndDistance.class);
                otherBranchesTD.add(thisBranchTD);
            }

            BranchList allOtherBranches = new BranchList(BranchToMove.this,otherBranchesTD,branchNames);
            listViewOtherBranch.setAdapter(allOtherBranches);
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

    AdapterView.OnItemClickListener moveProductToOtherBranch = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            moveStock(branchNames.get(position));
            finish();
        }
    };

    // METHODS ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    private void setResolution() {
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        Double width = dm.widthPixels*0.8;
        Double height = dm.heightPixels*0.8;
        getWindow().setLayout(width.intValue(),height.intValue());
    }

    private void initializeVariables() {
        listViewOtherBranch = findViewById(R.id.listViewOtherBranch);
        DistanceAndTimeTable = FirebaseDatabase.getInstance().getReference().child("DistanceAndTime").child(((GlobalVariables)getApplication()).getThisBranch());
        selectedProduct = ((GlobalVariables)getApplication()).getSelectedProduct();
        stockToMove = ((GlobalVariables)getApplication()).getStockToMove();
    }

    private void moveStock(final String branchToMoveTo) {
        StockDetailsTable = FirebaseDatabase.getInstance().getReference("StockDetails").child(branchToMoveTo);

        // Determine if stock with same prod date and exp date exists
        Query theseStocks = StockDetailsTable.orderByChild("productID").equalTo(selectedProduct.getProductID());
        theseStocks.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                selectedStock=null;
                for (DataSnapshot thisStockSnap : dataSnapshot.getChildren()) {
                    StockDetails thisStock = thisStockSnap.getValue(StockDetails.class);
                    if (thisStock.getProductionDate().equals(stockToMove.getProductionDate()) && thisStock.getExpiryDate().equals(stockToMove.getExpiryDate())) {
                        selectedStock=thisStock;
                        break;
                    }
                }

                if (selectedStock == null) {
                    String newStockID = StockDetailsTable.push().getKey();
                    StockDetails newStock = new StockDetails(stockToMove.getExpiryDate(),newStockID,stockToMove.getProductID(),stockToMove.getProductName(),stockToMove.getPackaging(),branchToMoveTo,((GlobalVariables)getApplication()).getStockCountChange(),stockToMove.getProductionDate());
                    StockDetailsTable.child(newStockID).setValue(newStock);

                    ModifyStockPopUp newMod = new ModifyStockPopUp();
                    newMod.checkForCriticalStockCount(selectedProduct,branchToMoveTo);
                }
                else {
                    int newCount = selectedStock.getStockCount() + ((GlobalVariables)getApplication()).getStockCountChange();
                    selectedStock.setStockCount(newCount);
                    StockDetailsTable.child(selectedStock.getStockID()).setValue(selectedStock);

                    ModifyStockPopUp newMod = new ModifyStockPopUp();
                    newMod.checkForCriticalStockCount(selectedProduct,branchToMoveTo);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
