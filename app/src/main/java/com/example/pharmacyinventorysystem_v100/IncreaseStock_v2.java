package com.example.pharmacyinventorysystem_v100;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class IncreaseStock_v2 extends AppCompatActivity {

    // View variables
    private EditText editTextSearch, editTextQuantity;
    private Button buttonIncreaseStock, buttonCancel;
    private TextView textViewSelectedName, textViewSelectedPackaging, textViewSelectedInStock, textViewExpiryDate;
    private Spinner spinnerBranch;
    private ListView listViewProductSearch;
    private ConstraintLayout constraintViewSelectedProduct;

    // Database variables
    private DatabaseReference productsTable, criticalStockTable, stocksTable;
    private List<Product> productList = new ArrayList<>();
    private List<EditText> validationEditText = new ArrayList<>();
    private Product selectedProduct;
    private StockDetails selectedStock;

    // Other variables
    private int totalStock;
    private boolean doesStockExist;
    public static final int criticalStockCount = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_increase_stock_v2);

        // Initialize variables
        editTextSearch = (EditText) findViewById(R.id.editTextSearch);
        editTextQuantity = (EditText) findViewById(R.id.editTextQuantity);
        textViewExpiryDate = (TextView) findViewById(R.id.textViewExpiryDate);
        textViewSelectedName = (TextView) findViewById(R.id.textViewSelectedName);
        textViewSelectedPackaging = (TextView) findViewById(R.id.textViewSelectedPackaging);
        textViewSelectedInStock = (TextView) findViewById(R.id.textViewSelectedInStock);
        buttonIncreaseStock = (Button) findViewById(R.id.buttonIncreaseStock);
        buttonCancel = (Button) findViewById(R.id.buttonCancel);
        spinnerBranch = (Spinner) findViewById(R.id.spinnerBranch);
        listViewProductSearch = (ListView) findViewById(R.id.listViewProductSearch);
        constraintViewSelectedProduct = (ConstraintLayout) findViewById(R.id.constraintViewSelectedProduct);

        // Database variables
        productsTable = FirebaseDatabase.getInstance().getReference("Products");
        criticalStockTable = FirebaseDatabase.getInstance().getReference("CriticalStocks");
        stocksTable = FirebaseDatabase.getInstance().getReference("StockDetails");

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
        });                                                                                             // SEARCH

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
                    textViewExpiryDate.setText(getString(R.string.labelSelectDate));
                    String selectedBranch = spinnerBranch.getSelectedItem().toString();
                    //Query queryThisBranch = pharmaciesTable.child(selectedBranch).orderByChild("totalStock").startAt(0);
                    //queryThisBranch.addListenerForSingleValueEvent(pharmacyQueryResult);
                    //textViewSelectedInStock.setText(Integer.toString(selectedPharmacy.getTotalStock()));
                    Query queryThisBranch = stocksTable.child(selectedBranch).child(selectedProduct.getProductID()).orderByChild("productID").equalTo(selectedProduct.getProductID());
                    queryThisBranch.addListenerForSingleValueEvent(totalStockQuery);
                }
                else {
                    textViewSelectedInStock.setText("");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        buttonIncreaseStock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userFieldVerified()) {
                    String expiryDate = textViewExpiryDate.getText().toString();
                    String selectedBranch = spinnerBranch.getSelectedItem().toString();
                    increaseStock3(expiryDate,selectedBranch);
                }
            }
        });

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(IncreaseStock_v2.this,MainActivity.class);
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

                DatePickerDialog datePickerDialog = new DatePickerDialog(IncreaseStock_v2.this,android.R.style.Theme_Holo_Light_Dialog_MinWidth,getSelectedDate,year,month,day);
                datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                datePickerDialog.show();
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

            ProductList searchResults = new ProductList(IncreaseStock_v2.this,productList);
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

    DatePickerDialog.OnDateSetListener getSelectedDate = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            month+=1;
            String date = month+"/"+dayOfMonth+"/"+year;
            textViewExpiryDate.setText(date);
            Log.d("TESTING",date);
        }
    };

    // METHODS ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    private void initializeValidationField()
    {
        validationEditText.clear();
        //validationEditText.add(editTextExpiryDate);
        validationEditText.add(editTextQuantity);
    }

    private void XincreaseStock()
    {
        int quantity = Integer.parseInt(editTextQuantity.getText().toString());
        String expiryDate = textViewExpiryDate.getText().toString();
        String selectedBranch = spinnerBranch.getSelectedItem().toString();

        if (stockExists(selectedBranch,selectedProduct.getProductID(),expiryDate))
        {
            int newCount = quantity+selectedStock.getStockCount();
            //StockDetails editedStockDetails = new StockDetails(expiryDate,selectedStock.getStockID(),selectedProduct.getProductID(),newCount);
            StockDetails editedStockDetails = new StockDetails(expiryDate,selectedStock.getStockID(),selectedProduct.getProductID(),selectedProduct.getProductName(),selectedProduct.getPackaging(),selectedBranch,newCount);
            stocksTable.child(selectedBranch).child(selectedStock.getProductID()).child(selectedStock.getStockID()).setValue(editedStockDetails);
            Toast.makeText(this, "Stock edited", Toast.LENGTH_SHORT).show();
        }
        else
        {
            String stockID = stocksTable.push().getKey();
            //StockDetails newStock = new StockDetails(expiryDate,stockID,selectedProduct.getProductID(),quantity);
            StockDetails newStock = new StockDetails(expiryDate,stockID,selectedProduct.getProductID(),selectedProduct.getProductName(),selectedProduct.getPackaging(),selectedBranch,quantity);
            stocksTable.child(selectedBranch).child(selectedProduct.getProductID()).child(stockID).setValue(newStock);
            Toast.makeText(this, "Stock added", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean stockExists(String branch, String productID, String expiryDate)
    {
        Query queryThisStock = stocksTable.child(branch).child(productID).orderByChild("expiryDate").equalTo(expiryDate);
        queryThisStock.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Toast.makeText(IncreaseStock_v2.this, Long.toString(dataSnapshot.getChildrenCount()), Toast.LENGTH_SHORT).show();
                if (dataSnapshot.getChildren().equals(0L)) {
                //if (Long.toString(dataSnapshot.getChildrenCount()).equals("0")) {
                    doesStockExist = false;
                }
                else {
                    doesStockExist = true;
                    Toast.makeText(IncreaseStock_v2.this, "Existing stock found", Toast.LENGTH_SHORT).show();
                    for (DataSnapshot thisSnapshot : dataSnapshot.getChildren()) {
                        selectedStock = thisSnapshot.getValue(StockDetails.class);
                        Toast.makeText(IncreaseStock_v2.this, selectedStock.getStockID(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return doesStockExist;
    }

    private void increaseStock()
    {
        String expiryDate = textViewExpiryDate.getText().toString();
        String selectedBranch = spinnerBranch.getSelectedItem().toString();

        // Query if there is already a stock with the same expiry date at selected branch
        Query queryThisStock = stocksTable.child(selectedBranch).child(selectedProduct.getProductID()).orderByChild("expiryDate").equalTo(expiryDate);
        queryThisStock.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int quantity = Integer.parseInt(editTextQuantity.getText().toString());
                String selectedBranch = spinnerBranch.getSelectedItem().toString();
                // If there were no previous stock with the same expiry date as entered
                if (Long.toString(dataSnapshot.getChildrenCount()).equals("0")) {
                    String expiryDate = textViewExpiryDate.getText().toString();
                    String stockID = stocksTable.push().getKey();
                    StockDetails newStock = new StockDetails(expiryDate,stockID,selectedProduct.getProductID(),quantity);
                    stocksTable.child(selectedBranch).child(selectedProduct.getProductID()).child(stockID).setValue(newStock);
                    Toast.makeText(IncreaseStock_v2.this, "Added new stock", Toast.LENGTH_SHORT).show();
                }
                // If there was already an existing stock with the same expiry date
                else {
                    for (DataSnapshot thisSnapshot : dataSnapshot.getChildren()) {
                        selectedStock = thisSnapshot.getValue(StockDetails.class);
                    }
                    int newCount = quantity+selectedStock.getStockCount();
                    StockDetails editedStockDetails = new StockDetails(selectedStock.getExpiryDate(),selectedStock.getStockID(),selectedStock.getProductID(),newCount);
                    stocksTable.child(selectedBranch).child(selectedStock.getProductID()).child(selectedStock.getStockID()).setValue(editedStockDetails);
                    Toast.makeText(IncreaseStock_v2.this, "Added to existing stock", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void increaseStock2()
    {
        String expiryDate = textViewExpiryDate.getText().toString();
        String selectedBranch = spinnerBranch.getSelectedItem().toString();

        // Query if there is already a stock with the same expiry date at selected branch
        Query queryThisStock = stocksTable.child(selectedBranch).child(selectedProduct.getProductID()).orderByChild("expiryDate").equalTo(expiryDate);
        queryThisStock.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Toast.makeText(IncreaseStock_v2.this, Long.toString(dataSnapshot.getChildrenCount()), Toast.LENGTH_SHORT).show();
                selectedStock=dataSnapshot.getValue(StockDetails.class);
                if (!selectedStock.getStockID().isEmpty()) {
                //if (selectedStock == null) {
                    Toast.makeText(IncreaseStock_v2.this, selectedStock.getStockID(),Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

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

    private void increaseStock3(final String expiryDate, String selectedBranch)
    {
        // Query if there is already a stock with the same expiry date at selected branch
        Query queryThisStock = stocksTable.child(selectedBranch).child(selectedProduct.getProductID()).orderByChild("expiryDate").equalTo(expiryDate).limitToFirst(1);
        queryThisStock.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String expiryDate = textViewExpiryDate.getText().toString();
                String selectedBranch = spinnerBranch.getSelectedItem().toString();
                int quantity = Integer.parseInt(editTextQuantity.getText().toString());
                if (dataSnapshot.getChildrenCount() != 0L)                                              // If there was already an existing stock with the same expiry date
                {
                    for (DataSnapshot stockSnapshot : dataSnapshot.getChildren()) {
                        selectedStock = stockSnapshot.getValue(StockDetails.class);
                    }
                    int newCount = quantity+selectedStock.getStockCount();
                    //StockDetails editedStockDetails = new StockDetails(selectedStock.getExpiryDate(),selectedStock.getStockID(),selectedStock.getProductID(),newCount);
                    StockDetails editedStockDetails = new StockDetails(selectedStock.getExpiryDate(),selectedStock.getStockID(),selectedProduct.getProductID(),selectedProduct.getProductName(),selectedProduct.getPackaging(),selectedBranch,newCount);
                    stocksTable.child(selectedBranch).child(selectedStock.getProductID()).child(selectedStock.getStockID()).setValue(editedStockDetails);
                    Toast.makeText(IncreaseStock_v2.this, "Added to existing stock", Toast.LENGTH_SHORT).show();
                }
                else                                                                                    // If there were no previous stock with the same expiry date as entered
                {
                    String stockID = stocksTable.push().getKey();
                    //StockDetails newStock = new StockDetails(expiryDate,stockID,selectedProduct.getProductID(),quantity);
                    StockDetails newStock = new StockDetails(expiryDate,stockID,selectedProduct.getProductID(),selectedProduct.getProductName(),selectedProduct.getPackaging(),selectedBranch,quantity);
                    stocksTable.child(selectedBranch).child(selectedProduct.getProductID()).child(stockID).setValue(newStock);
                    Toast.makeText(IncreaseStock_v2.this, "Added new stock", Toast.LENGTH_SHORT).show();
                }

                Log.d("TESTING",Integer.toString(totalStock+quantity));
                if ((totalStock+quantity) > criticalStockCount) {
                    criticalStockTable.child(selectedBranch).child(selectedProduct.getProductID()).removeValue();
                }
                clearFields();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
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
}
