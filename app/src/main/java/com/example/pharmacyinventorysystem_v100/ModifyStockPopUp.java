package com.example.pharmacyinventorysystem_v100;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

public class ModifyStockPopUp extends AppCompatActivity {

    // View variables
    private TextView thisTextView, textViewProduct,textViewProductionDate,textViewExpiryDate,textViewIndivCount,textViewLabelProdDate;
    private Button buttonSubmit;
    private EditText editTextQuantity;

    // Database Variables
    private DatabaseReference StockDetailsTable, CriticalStockTable;

    // Other variables
    private Product selectedProduct;
    private StockDetails selectedStock;
    private final int criticalCountQuota = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_stock_pop_up);

        setResolution();
        initializeVariables();

        textViewExpiryDate.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                //Log.d("TESTING","Changed expiry date: "+getString(R.string.labelSelectDate)+"~"+textViewProductionDate.getText());
                if (!textViewProductionDate.getText().equals(getString(R.string.labelSelectDate))) {
                    getSelectedStockDetails();
                    //Log.d("TESTING","Checking stock");
                }
            }
        });

        textViewProductionDate.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                //Log.d("TESTING","changed prod date");
                if (!textViewExpiryDate.getText().equals(getString(R.string.labelSelectDate))) {
                    getSelectedStockDetails();
                    //Log.d("TESTING","Checking stock");
                }
            }
        });

        textViewProductionDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int day,month,year;
                if (textViewExpiryDate.getText().toString().equals(getString(R.string.labelSelectDate))) {
                    Calendar calendar = Calendar.getInstance();
                    year = calendar.get(Calendar.YEAR);
                    month = calendar.get(Calendar.MONTH);
                    day = calendar.get(Calendar.DAY_OF_MONTH);
                }
                else {
                    String[] prevDate = textViewExpiryDate.getText().toString().split("/");
                    year=Integer.parseInt(prevDate[2]);
                    month=Integer.parseInt(prevDate[0])-1;
                    day=Integer.parseInt(prevDate[1]);
                }

                thisTextView=textViewProductionDate;
                DatePickerDialog datePickerDialog = new DatePickerDialog(ModifyStockPopUp.this,android.R.style.Theme_Holo_Light_Dialog_MinWidth,getSelectedDate,year,month,day);
                datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                datePickerDialog.show();
            }
        });

        textViewExpiryDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int day,month,year;
                if (textViewExpiryDate.getText().toString().equals(getString(R.string.labelSelectDate))) {
                    Calendar calendar = Calendar.getInstance();
                    year = calendar.get(Calendar.YEAR);
                    month = calendar.get(Calendar.MONTH);
                    day = calendar.get(Calendar.DAY_OF_MONTH);
                }
                else {
                    String[] prevDate = textViewExpiryDate.getText().toString().split("/");
                    year=Integer.parseInt(prevDate[2]);
                    month=Integer.parseInt(prevDate[0])-1;
                    day=Integer.parseInt(prevDate[1]);
                }

                thisTextView=textViewExpiryDate;
                DatePickerDialog datePickerDialog = new DatePickerDialog(ModifyStockPopUp.this,android.R.style.Theme_Holo_Light_Dialog_MinWidth,getSelectedDate,year,month,day);
                datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                datePickerDialog.show();
            }
        });

        buttonSubmit.setOnClickListener(modifyStock);
    }

    // Events ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    DatePickerDialog.OnDateSetListener getSelectedDate = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            month+=1;
            String date = month+"/"+dayOfMonth+"/"+year;
            thisTextView.setText(date);
        }
    };

    View.OnClickListener modifyStock = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (allFieldsValidated()) {
                if (((GlobalVariables) getApplication()).getToDoToStock().equals("Increase")) {
                    if (selectedStock != null) {
                        int newStockCount = selectedStock.getStockCount() + Integer.parseInt(editTextQuantity.getText().toString());
                        selectedStock.setStockCount(newStockCount);
                        StockDetailsTable.child(selectedStock.getStockID()).setValue(selectedStock);
                        checkForCriticalStockCount(selectedProduct);
                        finish();
                    } else {
                        String newStockID = StockDetailsTable.push().getKey();
                        StockDetails newStock = new StockDetails(textViewExpiryDate.getText().toString(), newStockID, selectedProduct.getProductID(), selectedProduct.getProductName(), selectedProduct.getPackaging(), ((GlobalVariables) getApplication()).getThisBranch(), Integer.parseInt(editTextQuantity.getText().toString()), textViewProductionDate.getText().toString());
                        StockDetailsTable.child(newStockID).setValue(newStock);
                        checkForCriticalStockCount(selectedProduct);
                        finish();
                    }
                }
                else {
                    if (selectedStock != null) {
                        if (Integer.parseInt(editTextQuantity.getText().toString()) > selectedStock.getStockCount()) {
                            Toast.makeText(ModifyStockPopUp.this, "There is not enough stock!", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            int newCount = selectedStock.getStockCount() - Integer.parseInt(editTextQuantity.getText().toString());
                            selectedStock.setStockCount(newCount);
                            StockDetailsTable.child(selectedStock.getStockID()).setValue(selectedStock);
                            checkForCriticalStockCount(selectedProduct);

                            ((GlobalVariables)getApplication()).setStockToMove(selectedStock);
                            ((GlobalVariables)getApplication()).setStockCountChange(Integer.parseInt(editTextQuantity.getText().toString()));
                            startActivity(new Intent(ModifyStockPopUp.this,BranchToMove.class));
                            finish();
                        }
                    } else {
                        Toast.makeText(ModifyStockPopUp.this, "No existing stock found", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    };

    // Functions ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    private void setResolution() {
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        Double width = dm.widthPixels*0.8;
        Double height = dm.heightPixels*0.6;
        getWindow().setLayout(width.intValue(),height.intValue());
    }
    private void initializeVariables() {
        selectedProduct = ((GlobalVariables)getApplication()).getSelectedProduct();
        StockDetailsTable = FirebaseDatabase.getInstance().getReference().child("StockDetails").child(((GlobalVariables)getApplication()).getThisBranch());
        CriticalStockTable = FirebaseDatabase.getInstance().getReference().child("CriticalStocks").child(((GlobalVariables)getApplication()).getThisBranch());

        textViewProduct = (TextView)findViewById(R.id.textViewProduct);
        textViewProductionDate = (TextView)findViewById(R.id.textViewProductionDate);
        textViewExpiryDate = (TextView) findViewById(R.id.textViewExpiryDate);
        textViewIndivCount = (TextView) findViewById(R.id.textViewIndivCount);
        textViewLabelProdDate = (TextView) findViewById(R.id.textViewLabelProdDate);
        buttonSubmit = (Button) findViewById(R.id.buttonSubmit);
        editTextQuantity = (EditText) findViewById(R.id.editTextQuantity);

        textViewProduct.setText(selectedProduct.getProductName()+" "+selectedProduct.getPackaging());
    }

    private void getSelectedStockDetails() {
        Query theseStocks = StockDetailsTable.orderByChild("productID").equalTo(selectedProduct.getProductID());
        theseStocks.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                selectedStock=null;
                for (DataSnapshot thisStockSnap : dataSnapshot.getChildren()) {
                    StockDetails thisStock = thisStockSnap.getValue(StockDetails.class);
                    if (thisStock.getProductionDate().equals(textViewProductionDate.getText().toString()) && thisStock.getExpiryDate().equals(textViewExpiryDate.getText().toString())) {
                        selectedStock=thisStock;
                        //Log.d("TESTING",Integer.toString(thisStock.getStockCount()));
                        break;
                    }
                }

                if (selectedStock == null) {
                    textViewIndivCount.setText("No existing stock found with these dates");
                }
                else {
                    textViewIndivCount.setText(Integer.toString(selectedStock.getStockCount())+" currently in stock");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private boolean allFieldsValidated() {
        boolean validation=true;
        if (textViewProductionDate.getText().toString().equals(getString(R.string.labelSelectDate)) || textViewExpiryDate.getText().toString().equals(getString(R.string.labelSelectDate))) {
            validation = false;
            Toast.makeText(this, "Please select date", Toast.LENGTH_SHORT).show();
        }
        if (editTextQuantity.getText().toString().isEmpty()) {
            validation=false;
            editTextQuantity.setError("Required field");
        }
        return validation;
    }

    private void checkForCriticalStockCount(final Product thisProduct) {
        Query theseStocks = StockDetailsTable.orderByChild("productID").equalTo(thisProduct.getProductID());
        theseStocks.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int stockCount=0;
                for (DataSnapshot thisStockSnap : dataSnapshot.getChildren())
                {
                    StockDetails thisStock = thisStockSnap.getValue(StockDetails.class);
                    stockCount+=thisStock.getStockCount();
                }

                if (stockCount <= criticalCountQuota) {
                    CriticalStock thisCriticalStock = new CriticalStock(thisProduct.getProductName(),thisProduct.getPackaging(),stockCount);
                    CriticalStockTable.child(thisProduct.getProductID()).setValue(thisCriticalStock);
                }
                else {
                    CriticalStockTable.child(thisProduct.getProductID()).removeValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void checkForCriticalStockCount(final Product thisProduct, final String specifiedBranch) {
        Query theseStocks = FirebaseDatabase.getInstance().getReference().child("StockDetails").child(specifiedBranch).orderByChild("productID").equalTo(thisProduct.getProductID());
        theseStocks.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                DatabaseReference SpecifiedCriticalStocksTable = FirebaseDatabase.getInstance().getReference("CriticalStocks").child(specifiedBranch);
                int stockCount=0;
                for (DataSnapshot thisStockSnap : dataSnapshot.getChildren())
                {
                    StockDetails thisStock = thisStockSnap.getValue(StockDetails.class);
                    stockCount+=thisStock.getStockCount();
                }

                if (stockCount <= criticalCountQuota) {
                    CriticalStock thisCriticalStock = new CriticalStock(thisProduct.getProductName(),thisProduct.getPackaging(),stockCount);
                    SpecifiedCriticalStocksTable.child(thisProduct.getProductID()).setValue(thisCriticalStock);
                }
                else {
                    SpecifiedCriticalStocksTable.child(thisProduct.getProductID()).removeValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
