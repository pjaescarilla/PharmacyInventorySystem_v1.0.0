package com.example.pharmacyinventorysystem_v100;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import static android.Manifest.permission_group.CALENDAR;

public class Sell extends AppCompatActivity {

    // View variables
    private EditText editTextSearch, editTextQuantity;
    private Button buttonCancel, buttonSell;
    private TextView textViewSelectedName, textViewSelectedPackaging, textViewSelectedInStock, textViewIndivCount, textViewExpiryDate;
    private Spinner spinnerBranch;
    private ListView listViewProductSearch;
    private ConstraintLayout constraintViewSelectedProduct;

    // Database variables
    private DatabaseReference productsTable, invoiceTable, stocksTable, criticalStockTable;
    private List<Product> productList = new ArrayList<>();
    private List<EditText> validationEditText = new ArrayList<>();
    private Product selectedProduct = new Product();
    private Invoice selectedInvoice;
    private StockDetails selectedStock;

    // Other variables
    private int totalStock, thisStockCount;
    public static final int criticalStockCount = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sell);

        // Initialize variables
        editTextSearch = (EditText) findViewById(R.id.editTextSearch);
        editTextQuantity = (EditText) findViewById(R.id.editTextQuantity);
        //textViewExpiryDate = (EditText) findViewById(R.id.textViewExpiryDate);
        textViewSelectedName = (TextView) findViewById(R.id.textViewSelectedName);
        textViewSelectedPackaging = (TextView) findViewById(R.id.textViewSelectedPackaging);
        textViewSelectedInStock = (TextView) findViewById(R.id.textViewSelectedInStock);
        textViewIndivCount = (TextView) findViewById(R.id.textViewIndivCount);
        textViewExpiryDate = (TextView) findViewById(R.id.textViewExpiryDate);
        buttonSell = (Button) findViewById(R.id.buttonSell);
        buttonCancel = (Button) findViewById(R.id.buttonCancel);
        spinnerBranch = (Spinner) findViewById(R.id.spinnerBranch);
        listViewProductSearch = (ListView) findViewById(R.id.listViewProductSearch);
        constraintViewSelectedProduct = (ConstraintLayout) findViewById(R.id.constraintViewSelectedProduct);

        // Database variables
        productsTable = FirebaseDatabase.getInstance().getReference("Products");
        invoiceTable = FirebaseDatabase.getInstance().getReference("Invoice");
        stocksTable = FirebaseDatabase.getInstance().getReference("StockDetails");
        criticalStockTable = FirebaseDatabase.getInstance().getReference("CriticalStocks");

        initializeValidationField();

        editTextSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                constraintViewSelectedProduct.setVisibility(View.INVISIBLE);
                listViewProductSearch.setVisibility(View.VISIBLE);

                String searchKeyword = editTextSearch.getText().toString();
                Query productQuery = productsTable.orderByChild("productName").startAt(searchKeyword).endAt(searchKeyword+"\uf8ff");
                productQuery.addListenerForSingleValueEvent(showQueryResult);
            }
        });

        listViewProductSearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {                  //  Select item from search result
                clearFields();
                selectedProduct = productList.get(position);
                textViewSelectedName.setText(selectedProduct.getProductName());
                textViewSelectedPackaging.setText(selectedProduct.getPackaging());

                listViewProductSearch.setVisibility(View.INVISIBLE);
                constraintViewSelectedProduct.setVisibility(View.VISIBLE);

            }
        });

        spinnerBranch.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {              // Get total stock from selected branch
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!parent.getItemAtPosition(position).equals("Select Branch")){
                    textViewExpiryDate.setText(R.string.labelSelectDate);
                    editTextQuantity.setText("");
                    String selectedBranch = spinnerBranch.getSelectedItem().toString();
                    Query queryThisBranch = stocksTable.child(selectedBranch).child(selectedProduct.getProductID()).orderByChild("productID").equalTo(selectedProduct.getProductID());
                    queryThisBranch.addValueEventListener(totalStockQuery);
                }
                else {
                    textViewSelectedInStock.setText("");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        buttonSell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userFieldVerified()) {
                    if (thereIsEnoughStock()) {
                        sellStock();
                    }
                    else {
                        Toast.makeText(Sell.this, "There is not enough stock!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Sell.this,MainActivity.class);
                startActivity(intent);
            }
        });

        textViewExpiryDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("TESTING","clicked");

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

                DatePickerDialog datePickerDialog = new DatePickerDialog(Sell.this,android.R.style.Theme_Holo_Light_Dialog_MinWidth,getSelectedDate,year,month,day);
                datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                datePickerDialog.show();
            }
        });

        textViewExpiryDate.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String selectedBranch = spinnerBranch.getSelectedItem().toString();
                String expiryDate = textViewExpiryDate.getText().toString();
                if (selectedProduct.getProductID() != null) {
                    if (!expiryDate.isEmpty() && !expiryDate.equals(getString(R.string.labelSelectDate))) {
                        Query queryThisStock = stocksTable.child(selectedBranch).child(selectedProduct.getProductID()).orderByChild("expiryDate").equalTo(expiryDate).limitToFirst(1);
                        queryThisStock.addListenerForSingleValueEvent(individualStockQuery);
                    }
                    else {
                        textViewIndivCount.setText("");
                    }
                }
            }
        });
    }

    // EVENTS ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    ValueEventListener showQueryResult = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            productList.clear();
            for (DataSnapshot productSnapshot : dataSnapshot.getChildren()) {
                Product product = productSnapshot.getValue(Product.class);
                productList.add(product);
            }

            ProductList searchResults = new ProductList(Sell.this,productList);
            listViewProductSearch.setAdapter(searchResults);
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

    ValueEventListener totalStockQuery = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            totalStock=0;
            for (DataSnapshot stockSnapshot : dataSnapshot.getChildren()) {
                StockDetails thisStock = stockSnapshot.getValue(StockDetails.class);
                totalStock=totalStock+thisStock.getStockCount();
            }
            textViewSelectedInStock.setText(Integer.toString(totalStock));
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

    ValueEventListener individualStockQuery= new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            if (dataSnapshot.getChildrenCount() != 0L)                                              // If there was already an existing stock with the same expiry date
            {
                for (DataSnapshot stockSnapshot : dataSnapshot.getChildren()) {
                    selectedStock = stockSnapshot.getValue(StockDetails.class);
                }
                if (selectedStock.getStockCount() > 0) {
                    textViewIndivCount.setText("Only " + Integer.toString(selectedStock.getStockCount()) + " stocks left with this exp date!");
                }
                else {
                   textViewIndivCount.setText("No stocks left with this exp date!");
                }
            }
            else
            {
                textViewIndivCount.setText("No stocks left with this exp date!");
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

    DatePickerDialog.OnDateSetListener getSelectedDate = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            month+=1;
            String date = month+"/"+dayOfMonth+"/"+year;
            textViewExpiryDate.setText(date);
            Log.d("TESTING",date);
        }
    };

    // METHOD ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    private void initializeValidationField()
    {
        validationEditText.clear();
        //validationEditText.add(textViewExpiryDate);
        validationEditText.add(editTextQuantity);
    }

    private void clearFields() {
        editTextSearch.setText("");
        textViewExpiryDate.setText("");
        editTextQuantity.setText("");
        spinnerBranch.setSelection(0);
    }

    private boolean userFieldVerified ()
    {
        boolean validity=true;
        for (EditText field : validationEditText) {
            String strToValidate = field.getText().toString();
            if (strToValidate.isEmpty() || strToValidate.length()==0 || strToValidate.equals(""))
            {
                validity=false;
                field.setError("Required field");
            }
        }
        if (spinnerBranch.getSelectedItem().toString().equals("Select Branch")) {
            validity=false;
            Toast.makeText(this, "Please select branch", Toast.LENGTH_SHORT).show();
        }

        if (textViewExpiryDate.getText().toString().isEmpty() || textViewExpiryDate.getText().toString().equals(getString(R.string.labelSelectDate))) {
            validity=false;
            Toast.makeText(this, "Please enter expiry date", Toast.LENGTH_SHORT).show();
        }
        return validity;
    }

    private String getDateToday()
    {
        //DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        Date date = new Date();
        String dateToday = dateFormat.format(date);
        return dateToday;
    }

    private boolean thereIsEnoughStock()
    {
        if (Integer.parseInt(editTextQuantity.getText().toString()) > selectedStock.getStockCount()) {
            return false;
        }
        else {
            return true;
        }
    }

    private void sellStock()
    {
        String selectedBranch = spinnerBranch.getSelectedItem().toString();
        String productID = selectedStock.getProductID();
        String stockID = selectedStock.getStockID();
        int quantity = Integer.parseInt(editTextQuantity.getText().toString().trim());
        String invoiceID = invoiceTable.child(selectedBranch).child(productID).child(stockID).push().getKey();
        long invoiceDate = Long.parseLong(getDateToday());

        Invoice newInvoice = new Invoice (productID,stockID,invoiceID,selectedProduct.getProductName(),selectedProduct.getPackaging(),selectedBranch,quantity,invoiceDate);     // Create new invoice
        //invoiceTable.child(selectedBranch).child(productID).child(stockID).child(invoiceID).setValue(newInvoice);
        invoiceTable.child(invoiceID).setValue(newInvoice);

        int newCount = selectedStock.getStockCount() - quantity;
        //StockDetails editedStockDetails = new StockDetails(selectedStock.getExpiryDate(),selectedStock.getStockID(),selectedStock.getProductID(),newCount);
        StockDetails editedStockDetails = new StockDetails(selectedStock.getExpiryDate(),selectedStock.getStockID(),selectedStock.getProductID(),selectedProduct.getProductName(),selectedProduct.getPackaging(),selectedBranch,newCount);
        stocksTable.child(selectedBranch).child(selectedStock.getProductID()).child(selectedStock.getStockID()).setValue(editedStockDetails);       // Update stock inventory count

        int newTotalStock = totalStock-quantity;
        if (newTotalStock <= criticalStockCount) {
            // Insert data to CriticalStock table
            CriticalStock thisCriticalStock = new CriticalStock(selectedBranch,selectedProduct.getProductName(),selectedProduct.getPackaging(),selectedProduct.getProductID(),newTotalStock);
            criticalStockTable.child(selectedBranch).child(selectedStock.getProductID()).setValue(thisCriticalStock);
        }

        Toast.makeText(Sell.this, "Item SOLD!", Toast.LENGTH_SHORT).show();
        clearFields();
    }
}
