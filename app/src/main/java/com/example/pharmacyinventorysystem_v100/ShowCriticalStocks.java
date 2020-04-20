package com.example.pharmacyinventorysystem_v100;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ShowCriticalStocks extends AppCompatActivity {

    // View variables
    private TabItem tabItemCriticalLevel, tabItemNearExpiry;
    private TabLayout tabLayout;
    private TextView textViewBackToMain;
    private ListView listViewCriticals;

    // Firebase variables
    private DatabaseReference CriticalStocksTable, NearExpiryTable;

    // Other variables
    private List<CriticalStock> criticalStockList = new ArrayList<CriticalStock>();
    private List<NearExpiry> nearExpiryList = new ArrayList<NearExpiry>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_critical_stocks);

        initalizeVariables();
        showStocksAtCritLevel();
        tabLayout.addOnTabSelectedListener(new TabLayout.BaseOnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tabLayout.getSelectedTabPosition() == 0) {
                    showStocksAtCritLevel();
                }
                else {
                    showNearlyExpiredProducts();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        textViewBackToMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ShowCriticalStocks.this,displayProducts.class));
            }
        });
    }


    // Methods ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    private void initalizeVariables() {
        tabItemCriticalLevel = findViewById(R.id.tabItemCriticalLevel);
        tabItemNearExpiry = findViewById(R.id.tabItemNearExpiry);
        tabLayout = findViewById(R.id.tabLayout);
        textViewBackToMain = findViewById(R.id.textViewBackToMain);
        listViewCriticals = findViewById(R.id.listViewCriticals);

        CriticalStocksTable = FirebaseDatabase.getInstance().getReference().child("CriticalStocks").child(((GlobalVariables)getApplication()).getThisBranch());
        NearExpiryTable = FirebaseDatabase.getInstance().getReference().child("NearExpiry").child(((GlobalVariables)getApplication()).getThisBranch());
    }

    private void showStocksAtCritLevel() {
        CriticalStocksTable.orderByChild("productName").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                criticalStockList.clear();
                for (DataSnapshot criticalStockSnap : dataSnapshot.getChildren())
                {
                    CriticalStock thisCriticalStock = criticalStockSnap.getValue(CriticalStock.class);
                    criticalStockList.add(thisCriticalStock);
                }

                CriticalStockList critStockAdapter = new CriticalStockList(ShowCriticalStocks.this,criticalStockList);
                listViewCriticals.setAdapter(critStockAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void showNearlyExpiredProducts() {
        NearExpiryTable.orderByChild("productName").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                nearExpiryList.clear();
                for (DataSnapshot nearExpirySnap : dataSnapshot.getChildren())
                {
                    NearExpiry thisNearExpiry = nearExpirySnap.getValue(NearExpiry.class);
                    nearExpiryList.add(thisNearExpiry);
                }

                NearExpiryList nearExpiryAdapter = new NearExpiryList(ShowCriticalStocks.this,nearExpiryList);
                listViewCriticals.setAdapter(nearExpiryAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
