package com.example.pharmacyinventorysystem_v100;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

public class displayProducts extends AppCompatActivity {

    // View variables
    private TextView textViewSelectedProduct,textViewSelectedDrugGroup,textViewSelectedLTOO,textViewSignOut,
            textViewSelectedAUPD,textViewSelectedOrderingCost,textViewSelectedHoldCost, textViewInStock,
            textViewSelectedAnnualDemand,textViewSelectedEOQ,textViewSelectedReorderPoint,textViewShowAll,
            textViewActiveBranch,textViewOtherBranches,textViewCriticalStocks;
    private Button buttonIncreaseStock,buttonDecreaseStock;
    private EditText editTextSearch;
    private ConstraintLayout constraintViewSelectedProduct;
    private ListView listViewProducts;

    // Database Variables
    private DatabaseReference StockDetailsTable, ProductsTable, CriticalStocksTable, NearExpiryTable;

    // Temporary Variables
    private Table<String,String,String> productDetailMapping = HashBasedTable.create();
    private Table<String,String,Integer> stockDetailMapping = HashBasedTable.create();

    // Other Variables
    private List<Product> productList = new ArrayList<>();
    private Product selectedProduct = new Product();
    private final String CHANNEL_ID = "PISv1.2";
    private final String CHANNEL_NAME = "Pharmacy Inventory System";
    private final String CHANNEL_DESC = "Pharmacy Inventory System for Thesis";
    private final static long daysBeforeExpiryToNotif = 30L;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_products);

        initializeVariables();
        initializeNotificationChannel();
        //initializeDatabaseData();
        notifyCriticalStockLevel();
        checkStocksNearExpiry();

        textViewActiveBranch.setText(((GlobalVariables)getApplication()).getThisBranch());

        ProductsTable.orderByChild("productName").addValueEventListener(displayAllProducts);
        editTextSearch.addTextChangedListener(searchThisProduct);
        listViewProducts.setOnItemClickListener(showThisProductDetails);
        buttonIncreaseStock.setOnClickListener(openIncreaseStock);
        buttonDecreaseStock.setOnClickListener(openDecreaseStock);
        textViewShowAll.setOnClickListener(showAll);
        textViewSignOut.setOnClickListener(signOut);

        textViewCriticalStocks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(displayProducts.this,ShowCriticalStocks.class));
            }
        });
        textViewOtherBranches.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(displayProducts.this,ShowTimeAndDistance.class));
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        FirebaseAuth.getInstance().signOut();
    }

    // Events ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    ValueEventListener displayAllProducts = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            productList.clear();
            for (DataSnapshot productSnap : dataSnapshot.getChildren()) {
                Product thisProduct = productSnap.getValue(Product.class);
                productList.add(thisProduct);
                //Log.d("TESTING",thisProduct.getProductName()+" "+thisProduct.getPackaging()+" "+Integer.toString(thisProduct.getReorderPoint()));
            }

            ProductList2 allProducts = new ProductList2(displayProducts.this,productList);
            //ProductList allProducts = new ProductList(displayProducts.this,productList);
            listViewProducts.setAdapter(allProducts);
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

    TextWatcher searchThisProduct = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            listViewProducts.setVisibility(View.VISIBLE);
            constraintViewSelectedProduct.setVisibility(View.INVISIBLE);
            buttonDecreaseStock.setVisibility(View.INVISIBLE);
            buttonIncreaseStock.setVisibility(View.INVISIBLE);
            textViewShowAll.setVisibility(View.INVISIBLE);

            String searchKeyword = editTextSearch.getText().toString();
            Query productQuery = ProductsTable.orderByChild("productName").startAt(searchKeyword).endAt(searchKeyword+"\uf8ff");
            productQuery.addListenerForSingleValueEvent(showSearchResult);
        }
    };

    ValueEventListener showSearchResult = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            productList.clear();
            for (DataSnapshot productSnapshot : dataSnapshot.getChildren()) {
                Product product = productSnapshot.getValue(Product.class);
                productList.add(product);
            }

            ProductList2 searchResults = new ProductList2(displayProducts.this,productList);
            listViewProducts.setAdapter(searchResults);
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

    View.OnClickListener openIncreaseStock = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ((GlobalVariables)getApplication()).setToDoToStock("Increase");
            startActivity(new Intent(displayProducts.this,ModifyStockPopUp.class));
        }
    };

    View.OnClickListener openDecreaseStock = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ((GlobalVariables)getApplication()).setToDoToStock("Decrease");
            startActivity(new Intent(displayProducts.this,ModifyStockPopUp.class));
        }
    };

    View.OnClickListener showAll = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            editTextSearch.setText("");
        }
    };

    View.OnClickListener signOut = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(displayProducts.this,Login.class));
        }
    };

    AdapterView.OnItemClickListener showThisProductDetails = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            clearFields();
            selectedProduct = productList.get(position);
            ((GlobalVariables)getApplication()).setSelectedProduct(selectedProduct);

            setTextViewInStock(selectedProduct);
            textViewSelectedAnnualDemand.setText(Double.toString(selectedProduct.getAnnualDemand()));
            textViewSelectedAUPD.setText(Double.toString(selectedProduct.getAUPD()));
            textViewSelectedDrugGroup.setText(selectedProduct.getDrugGroup());
            textViewSelectedEOQ.setText(Integer.toString(selectedProduct.getEOQ()));
            textViewSelectedHoldCost.setText(selectedProduct.getHoldingCost());
            textViewSelectedLTOO.setText(Integer.toString(selectedProduct.getLTOO()));
            textViewSelectedOrderingCost.setText(selectedProduct.getOrderCost());
            textViewSelectedProduct.setText(selectedProduct.getProductName() + " " + selectedProduct.getPackaging());
            textViewSelectedReorderPoint.setText(Integer.toString(selectedProduct.getReorderPoint()));

            listViewProducts.setVisibility(View.INVISIBLE);
            constraintViewSelectedProduct.setVisibility(View.VISIBLE);
            buttonDecreaseStock.setVisibility(View.VISIBLE);
            buttonIncreaseStock.setVisibility(View.VISIBLE);
            textViewShowAll.setVisibility(View.VISIBLE);
        }
    };

    // Methods ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    private void initializeVariables() {
        textViewSelectedProduct = (TextView) findViewById(R.id.textViewSelectedProduct);
        textViewSelectedDrugGroup = (TextView) findViewById(R.id.textViewSelectedDrugGroup);
        textViewSelectedLTOO = (TextView) findViewById(R.id.textViewSelectedLTOO);
        textViewSelectedAUPD = (TextView) findViewById(R.id.textViewSelectedAUPD);
        textViewSelectedOrderingCost = (TextView) findViewById(R.id.textViewSelectedOrderingCost);
        textViewSelectedHoldCost = (TextView) findViewById(R.id.textViewSelectedHoldCost);
        textViewSelectedAnnualDemand = (TextView) findViewById(R.id.textViewSelectedAnnualDemand);
        textViewSelectedEOQ = (TextView) findViewById(R.id.textViewSelectedEOQ);
        textViewSelectedReorderPoint = (TextView) findViewById(R.id.textViewSelectedReorderPoint);
        textViewInStock = (TextView) findViewById(R.id.textViewInStock);
        textViewShowAll = (TextView) findViewById(R.id.textViewShowAll);
        textViewSignOut = (TextView) findViewById(R.id.textViewSignOut);
        textViewActiveBranch = (TextView) findViewById(R.id.textViewActiveBranch);
        textViewOtherBranches = findViewById(R.id.textViewOtherBranches);
        textViewCriticalStocks = findViewById(R.id.textViewCriticalProducts);
        buttonIncreaseStock = (Button) findViewById(R.id.buttonIncreaseStock);
        buttonDecreaseStock = (Button) findViewById(R.id.buttonDecreaseStock);
        editTextSearch = (EditText) findViewById(R.id.editTextSearch);

        constraintViewSelectedProduct = (ConstraintLayout) findViewById(R.id.constraintViewSelectedProduct);
        listViewProducts = (ListView) findViewById(R.id.listViewProductSearch);

        StockDetailsTable = FirebaseDatabase.getInstance().getReference().child("StockDetails").child(((GlobalVariables)getApplication()).getThisBranch());
        ProductsTable = FirebaseDatabase.getInstance().getReference().child("Products");
        CriticalStocksTable = FirebaseDatabase.getInstance().getReference().child("CriticalStocks").child(((GlobalVariables)getApplication()).getThisBranch());
        NearExpiryTable = FirebaseDatabase.getInstance().getReference().child("NearExpiry").child(((GlobalVariables)getApplication()).getThisBranch());
    }

    private void clearFields() {
        editTextSearch.setText("");
    }

    private void setTextViewInStock(Product thisProduct) {
        textViewInStock.setText("");
        Query theseStocks = StockDetailsTable.orderByChild("productID").equalTo(thisProduct.getProductID());
        theseStocks.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //Log.d("TESTING",dataSnapshot.getKey());
                int stockCount=0;
                for (DataSnapshot thisStockSnap : dataSnapshot.getChildren())
                {
                    //Log.d("TESTING",thisStockSnap.getKey());
                    StockDetails thisStock = thisStockSnap.getValue(StockDetails.class);
                    stockCount+=thisStock.getStockCount();
                }
                textViewInStock.setText(Integer.toString(stockCount));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void initializeNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription(CHANNEL_DESC);
            NotificationManager mgr = getSystemService(NotificationManager.class);
            mgr.createNotificationChannel(channel);
        }
    }

    public String getDateToday() {
        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        Date date = new Date();
        String dateToday = dateFormat.format(date);
        return dateToday;
    }

    public Long getDateLong(String stringToConvert)
    {
        Date dateNow = new SimpleDateFormat("MM/dd/yyyy").parse(stringToConvert,new ParsePosition(0));
        return dateNow.getTime();
    }


    // DATABASE INITIALIZATIONS ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    private void initializeDatabaseData(){
        populateProductDetailsTable();
    }

    private void populateProductDetailsTable() {
        ProductsTable.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot thisProductSnap : dataSnapshot.getChildren())
                {
                    Product thisProduct = thisProductSnap.getValue(Product.class);
                    productDetailMapping.put(thisProduct.getProductName(),thisProduct.getProductID(),thisProduct.getPackaging());
                    //Log.d("TESTING",thisProduct.getProductID()+" "+thisProduct.getPackaging());
                }
                populateStockDetailsTable();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void populateStockDetailsTable() {
        DatabaseReference StocksTableTemp = FirebaseDatabase.getInstance().getReference().child("StockDetails");
        StocksTableTemp.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot branchSnap : dataSnapshot.getChildren())
                {
                    String branchName = branchSnap.getKey();
                    Log.d("TESTING",branchName);
                    for (DataSnapshot stockSnap : branchSnap.getChildren()) {
                        StockDetails thisStock = stockSnap.getValue(StockDetails.class);
                        stockDetailMapping.put(branchName, thisStock.getProductID(), thisStock.getStockCount());
                    }
                }
                populateCriticalStocksTable();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void populateCriticalStocksTable() {
        int criticalCountQuota =5;
        int stockCount;
        String productName = new String();
        String packaging = new String();
        Set<String> productIDs = productDetailMapping.columnKeySet();
        Set<String> branches = stockDetailMapping.rowKeySet();
        DatabaseReference CriticalStocksTableTemp = FirebaseDatabase.getInstance().getReference().child("CriticalStocks");
        for (String PID : productIDs) {
            for (String branch : branches) {
                stockCount = stockDetailMapping.get(branch,PID);
                if (stockCount < criticalCountQuota)
                {
                    Map<String,String> productMap = productDetailMapping.column(PID);
                    for (Map.Entry<String,String> prd : productMap.entrySet()){
                        productName = prd.getKey();
                        packaging = prd.getValue();
                    }
                    CriticalStock newCriticalStock = new CriticalStock(productName,packaging,stockCount);
                    CriticalStocksTableTemp.child(branch).child(PID).setValue(newCriticalStock);
                    //Log.d("TESTING",productName+" "+packaging+" "+Integer.toString(stockCount));
                }
            }
        }
    }

    private void populateNearExpiryTable(String stockID, String expiryDate, String productName, String packaging, int stockCount) {
        NearExpiry newNearExpiryProduct = new NearExpiry(stockID,expiryDate,productName,packaging,stockCount);
        NearExpiryTable.child(stockID).setValue(newNearExpiryProduct);
    }


    // NOTIFICATIONS ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    public void notifyCriticalStockLevel ()
    {
        CriticalStocksTable.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                /*if (!((GlobalVariables)getApplication()).thisBranchNotified(dataSnapshot.getKey())) {
                    ((GlobalVariables) getApplication()).addNotifiedBranch(dataSnapshot.getKey());
                    critStockNotificationProcess(dataSnapshot);
                }*/
                if (!((GlobalVariables)getApplication()).thisProductNotified(dataSnapshot.getKey())) {
                    ((GlobalVariables)getApplication()).addNotifiedProduct(dataSnapshot.getKey());
                    critStockNotificationProcess(dataSnapshot);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                critStockNotificationProcess(dataSnapshot);
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void critStockNotificationProcess(DataSnapshot dataSnapshot)
    {
        String notifTitle,notifMessage;
        String branchName = ((GlobalVariables)getApplication()).getThisBranch();
        String productID = dataSnapshot.getKey();
        int notifID;

        CriticalStock thisCriticalStock = dataSnapshot.getValue(CriticalStock.class);

        // Get notif ID
        if (((GlobalVariables) getApplication()).alreadyNotifiedThis(branchName,productID)) {
            notifID=((GlobalVariables) getApplication()).getExistingNotifID(branchName,productID);
        }
        else {
            notifID = ((GlobalVariables) getApplication()).getNewNotifID();
            ((GlobalVariables) getApplication()).addToNotifMapping(branchName,productID,notifID);
        }

        // Generate notification message and notification title

        if (thisCriticalStock.getQuantity()> 0) {
            notifTitle="STOCK ALMOST OUT ("+branchName+")";
            notifMessage=thisCriticalStock.getProductName()+" ("+thisCriticalStock.getPackaging()+") - "+Integer.toString(thisCriticalStock.getQuantity())+" remaining";
        }
        else {
            notifTitle="OUT OF STOCK ("+branchName+")";
            notifMessage=thisCriticalStock.getProductName()+" ("+thisCriticalStock.getPackaging()+")";
        }

        //Log.d("TESTING",notifTitle+": "+notifMessage+" Notif ID: "+Integer.toString(notifID));
        displayNotif(notifTitle,notifMessage,notifID);
    }

    private void checkStocksNearExpiry() {
        if (((GlobalVariables)this.getApplication()).getLastCheckedDate().isEmpty()) {
            ((GlobalVariables)this.getApplication()).setLastCheckedDate(getDateToday());

            NearExpiryTable.removeValue();
            StockDetailsTable.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    int notifID=1000;

                    for (DataSnapshot thisStockSnap : dataSnapshot.getChildren()) {
                        StockDetails thisStock = thisStockSnap.getValue(StockDetails.class);
                        Long expDateToLong = getDateLong(thisStock.getExpiryDate());
                        Long dateNowToLong = getDateLong(getDateToday());
                        long daysBeforeExpiry = (expDateToLong-dateNowToLong)/(1000*60*60*24);
                        if (daysBeforeExpiry < 0) {
                            String notifTitle="PRODUCT EXPIRED! ("+thisStock.getBranchName()+")";
                            String notifMessage="ExpDate: "+thisStock.getExpiryDate()+" - "+thisStock.getProductName()+" ("+thisStock.getPackaging()+"): "+Integer.toString(thisStock.getStockCount())+" pc";
                            displayNotif(notifTitle,notifMessage,notifID);
                            notifID=notifID+1;
                            populateNearExpiryTable(thisStock.getProductID(),thisStock.getExpiryDate(),thisStock.getProductName(),thisStock.getPackaging(),thisStock.getStockCount());
                        }
                        else if (daysBeforeExpiry < daysBeforeExpiryToNotif) {
                            String notifTitle="Product Almost Expired ("+thisStock.getBranchName()+")";
                            String notifMessage="ExpDate: "+thisStock.getExpiryDate()+" - "+thisStock.getProductName()+" ("+thisStock.getPackaging()+"): "+Integer.toString(thisStock.getStockCount())+" pc";
                            displayNotif(notifTitle,notifMessage,notifID);
                            notifID=notifID+1;
                            populateNearExpiryTable(thisStock.getProductID(),thisStock.getExpiryDate(),thisStock.getProductName(),thisStock.getPackaging(),thisStock.getStockCount());
                        }
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    private void displayNotif(String notifTitle, String notifMessage, int notifID) {
        Intent resultIntent = new Intent (this, ShowTimeAndDistance.class);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(this,1,resultIntent,PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this,CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_supplier)
                .setContentTitle(notifTitle)
                .setContentText(notifMessage)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .setContentIntent(resultPendingIntent);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
        notificationManagerCompat.notify(notifID,mBuilder.build());
    }

}
