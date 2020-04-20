package com.example.pharmacyinventorysystem_v100;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Toast;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity {

    // View Variables
    private Button buttonAddItem, buttonIncreaseStock, buttonSell;
    private DatabaseReference lastCheckedDate = FirebaseDatabase.getInstance().getReference("LastChecked:");
    private DatabaseReference stocksTable = FirebaseDatabase.getInstance().getReference("StockDetails");
    private DatabaseReference highDemandTable = FirebaseDatabase.getInstance().getReference("HighDemand");
    private DatabaseReference invoiceTable = FirebaseDatabase.getInstance().getReference("Invoice");
    private Table<String,String,Integer> localInvoiceTable = HashBasedTable.create();
    private Table<String,String,String> productDetailsTable = HashBasedTable.create();
    private final String CHANNEL_ID = "PISv1";
    private final String CHANNEL_NAME = "Pharmacy Inventory System";
    private final String CHANNEL_DESC = "Pharmacy Inventory System for Thesis";

    private final static Long dateRangeForDemand = 7L;
    private final static double highDemandQuota = 11.0;
    private final static long daysBeforeExpiryToNotif = 30L;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize view variables
        buttonAddItem = (Button) findViewById(R.id.buttonAddItem);
        buttonIncreaseStock = (Button) findViewById(R.id.buttonIncreaseStock);
        buttonSell = (Button) findViewById(R.id.buttonSell);

        initializeNotificationChannel();
        //checkStocksNearExpiry();
        //initialNotification();
        notifyCriticalStockLevel();
        //checkStocksHighDemand();
        initialNotification();

        buttonAddItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAddNewItemActivity();
            }
        });

        buttonIncreaseStock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openIncreaseStockActivity();
            }
        });

        buttonSell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSellActivity();
            }
        });
    }


    // METHODS ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    public void openAddNewItemActivity() {
        Intent intent = new Intent(this,AddNewItem.class);
        startActivity(intent);
    }

    public void openIncreaseStockActivity() {
        Intent intent = new Intent (this,IncreaseStock_v2.class);
        startActivity(intent);
    }

    public void openSellActivity() {
        Intent intent = new Intent (this,Sell.class);
        startActivity(intent);
    }

    private void initializeNotificationChannel()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription(CHANNEL_DESC);
            NotificationManager mgr = getSystemService(NotificationManager.class);
            mgr.createNotificationChannel(channel);
        }
    }

    public void notifyCriticalStockLevel ()
    {
        DatabaseReference criticalStocks = FirebaseDatabase.getInstance().getReference("CriticalStocks");
        criticalStocks.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                /*String notifTitle,notifMessage;
                int notifID;
                if (!((GlobalVariables)getApplication()).thisBranchNotified(dataSnapshot.getKey()))
                {
                    ((GlobalVariables) getApplication()).addNotifiedBranch(dataSnapshot.getKey());
                    for (DataSnapshot productSnap : dataSnapshot.getChildren()) {
                        CriticalStock thisCriticalStock = productSnap.getValue(CriticalStock.class);
                        if (((GlobalVariables) getApplication()).alreadyNotifiedThis(thisCriticalStock.getBranchName(),thisCriticalStock.getProductID())) {
                            notifID=((GlobalVariables) getApplication()).getExistingNotifID(thisCriticalStock.getBranchName(),thisCriticalStock.getProductID());
                        }
                        else {
                            notifID = ((GlobalVariables) getApplication()).getNewNotifID();
                        }
                        notifMessage=thisCriticalStock.getProductName()+" ("+thisCriticalStock.getPackaging()+")";
                        if (thisCriticalStock.getQuantity()> 0) {
                            notifTitle="STOCK ALMOST OUT ("+thisCriticalStock.getBranchName()+")";
                        }
                        else {
                            notifTitle="OUT OF STOCK ("+thisCriticalStock.getBranchName()+")";
                        }
                        Log.d("TESTING",notifTitle+": "+notifMessage);
                        displayNotif(notifTitle,notifMessage,notifID);
                    }
                }*/
                if (!((GlobalVariables)getApplication()).thisBranchNotified(dataSnapshot.getKey())) {
                    ((GlobalVariables) getApplication()).addNotifiedBranch(dataSnapshot.getKey());
                    critStockNotificationProcess(dataSnapshot);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                critStockNotificationProcess(dataSnapshot);
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                Log.d("TESTING","CHILD REMOVED");
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Log.d("TESTING","CHILD MOVED");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void critStockNotificationProcess(DataSnapshot dataSnapshot)
    {
        String notifTitle,notifMessage;
        int notifID;

        for (DataSnapshot productSnap : dataSnapshot.getChildren()) {
            CriticalStock thisCriticalStock = productSnap.getValue(CriticalStock.class);
            if (((GlobalVariables) getApplication()).alreadyNotifiedThis(thisCriticalStock.getBranchName(),thisCriticalStock.getProductID())) {
                notifID=((GlobalVariables) getApplication()).getExistingNotifID(thisCriticalStock.getBranchName(),thisCriticalStock.getProductID());
            }
            else {
                notifID = ((GlobalVariables) getApplication()).getNewNotifID();
                ((GlobalVariables) getApplication()).addToNotifMapping(thisCriticalStock.getBranchName(),thisCriticalStock.getProductID(),notifID);
            }
            notifMessage=thisCriticalStock.getProductName()+" ("+thisCriticalStock.getPackaging()+") - "+Integer.toString(thisCriticalStock.getQuantity())+" remaining";
            if (thisCriticalStock.getQuantity()> 0) {
                notifTitle="STOCK ALMOST OUT ("+thisCriticalStock.getBranchName()+")";
            }
            else {
                notifTitle="OUT OF STOCK ("+thisCriticalStock.getBranchName()+")";
            }
            Log.d("TESTING",notifTitle+": "+notifMessage+" Notif ID: "+Integer.toString(notifID));
            displayNotif(notifTitle,notifMessage,notifID);
        }
    }

    private void displayNotif(String notifTitle, String notifMessage, int notifID)
    {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this,CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_supplier)
                .setContentTitle(notifTitle)
                .setContentText(notifMessage)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
        notificationManagerCompat.notify(notifID,mBuilder.build());
    }

    private void displayNotif(Activity context, String notifTitle, String notifMessage, int notifID)
    {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context,CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_supplier)
                .setContentTitle(notifTitle)
                .setContentText(notifMessage)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
        notificationManagerCompat.notify(notifID,mBuilder.build());
    }

    private void checkStocksNearExpiry()
    {
        stocksTable.addListenerForSingleValueEvent(new ValueEventListener() {
        //stocksTable.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int notifID=1000;
                for (DataSnapshot inThisBranch : dataSnapshot.getChildren()) {
                    for (DataSnapshot thisProduct : inThisBranch.getChildren()) {
                        for (DataSnapshot thisStockSnap : thisProduct.getChildren()) {
                            StockDetails thisStock = thisStockSnap.getValue(StockDetails.class);
                            Long expDateToLong = getDateLong(thisStock.getExpiryDate());
                            Long dateNowToLong = getDateLong(getDateToday());
                            long daysBeforeExpiry = (expDateToLong-dateNowToLong)/(1000*60*60*24);
                            if (daysBeforeExpiry < 0) {
                                String notifTitle="PRODUCT EXPIRED! ("+thisStock.getBranchName()+")";
                                String notifMessage="ExpDate: "+thisStock.getExpiryDate()+" - "+thisStock.getProductName()+" ("+thisStock.getPackaging()+"): "+Integer.toString(thisStock.getStockCount())+" pc";
                                Log.d("TESTING",notifTitle+ " " +notifMessage);
                                Log.d("TESTING",Long.toString(daysBeforeExpiry));
                                displayNotif(notifTitle,notifMessage,notifID);
                                notifID=notifID+1;
                            }
                            else if (daysBeforeExpiry < daysBeforeExpiryToNotif) {
                                String notifTitle="Product Almost Expired ("+thisStock.getBranchName()+")";
                                String notifMessage="ExpDate: "+thisStock.getExpiryDate()+" - "+thisStock.getProductName()+" ("+thisStock.getPackaging()+"): "+Integer.toString(thisStock.getStockCount())+" pc";
                                Log.d("TESTING",notifTitle+ " " +notifMessage);
                                Log.d("TESTING",Long.toString(daysBeforeExpiry));
                                displayNotif(MainActivity.this,notifTitle,notifMessage,notifID);
                                notifID=notifID+1;
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void checkStocksHighDemand()
    {
        highDemandTable.removeValue();
        Long dateLongToday=getDateLong(getDateToday());
        Long dateLongStartAt=dateLongToday-(1000*60*60*24*dateRangeForDemand);
        invoiceTable.orderByChild("invoiceDate").startAt(getDateFromLong(dateLongStartAt)).endAt(getDateFromLong(dateLongToday)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                localInvoiceTable.clear();
                productDetailsTable.clear();
                Integer totalBuyCount=0;
                String productName="",packaging="",branch="";

                for (DataSnapshot thisInvoiceSnap : dataSnapshot.getChildren())
                {
                    Invoice thisInvoice = thisInvoiceSnap.getValue(Invoice.class);

                    // Create local table of invoice
                    if (localInvoiceTable.contains(thisInvoice.getBranchName(),thisInvoice.getProductID())) {
                        totalBuyCount=thisInvoice.quantity+localInvoiceTable.get(thisInvoice.getBranchName(),thisInvoice.getProductID());
                        localInvoiceTable.remove(thisInvoice.getBranchName(),thisInvoice.getProductID());
                    }
                    else {
                        totalBuyCount=thisInvoice.getQuantity();
                        if (!productDetailsTable.containsColumn(thisInvoice.getProductID())) {
                            productDetailsTable.put(thisInvoice.getProductName(),thisInvoice.getProductID(),thisInvoice.getPackaging());
                        }
                    }
                    localInvoiceTable.put(thisInvoice.getBranchName(),thisInvoice.getProductID(),totalBuyCount);
                }

                Set<String> PIDs = productDetailsTable.columnKeySet();
                for (String PID : PIDs)
                {
                    Log.d("HighDem",PID);
                    Map<String,Integer> thisProductInvoices = localInvoiceTable.column(PID);
                    Map<String,String> thisProductDetails = productDetailsTable.column(PID);
                    for (Map.Entry<String,String> prd : thisProductDetails.entrySet()) {
                        productName = prd.getKey();
                        packaging = prd.getValue();
                    }

                    for(Map.Entry<String,Integer> inv : thisProductInvoices.entrySet()) {
                        totalBuyCount = inv.getValue();
                        branch = inv.getKey();

                        DecimalFormat df = new DecimalFormat("#.####");
                        double count = totalBuyCount;
                        double demand = count / dateRangeForDemand.doubleValue();
                        if (demand > highDemandQuota) {
                            Log.d("HighDem", branch + ": " + productName + "(" + packaging + ") - " + df.format(demand) + " HIGH DEMAND");
                            HighDemand newHighDemand = new HighDemand(productName, packaging, Double.parseDouble(df.format(demand)));
                            highDemandTable.child(branch).child(PID).setValue(newHighDemand);
                        } else {
                            Log.d("HighDem", branch + ": " + productName + "(" + packaging + ") - " + df.format(demand) + " NOT HIGH DEMAND");
                        }
                    }
                }

                notifyHighDemand();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void notifyHighDemand()
    {
        highDemandTable.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int notifID=2000;
                for (DataSnapshot branchSnap : dataSnapshot.getChildren()) {
                    for (DataSnapshot highDemandSnap : branchSnap.getChildren()) {
                        HighDemand thisHighDemandProd = highDemandSnap.getValue(HighDemand.class);
                        String notifTitle="HIGH DEMAND FOR PRODUCT! ("+highDemandSnap.getRef().getParent().getKey()+")";
                        String notifMessage=thisHighDemandProd.getProductName() + " ("+thisHighDemandProd.getPackaging()+")";
                        displayNotif(notifTitle,notifMessage,notifID);
                        notifID = notifID+1;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void initialNotification()
    {
        if (((GlobalVariables)this.getApplication()).getLastCheckedDate().isEmpty()) {
            ((GlobalVariables)this.getApplication()).setLastCheckedDate(getDateToday());
            checkStocksNearExpiry();
            checkStocksHighDemand();
        }
    }


    public String getDateToday()
    {
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

    public Long getDateFromLong(Long dateNumber)
    {
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        //dateFormat.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        Date date = new Date(dateNumber);
        Long convertedDate = Long.parseLong(dateFormat.format(date));
        return convertedDate;
    }

}
