package com.example.pharmacyinventorysystem_v100;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.accessibility.AccessibilityViewCommand;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EventListener;
import java.util.List;

public class AddNewItem extends AppCompatActivity {

    // View variables
    private Button buttonSubmit, buttonCancel;
    private EditText editTextProductName, editTextOtherPackaging;
    private Spinner spinnerPackaging, spinnerBranch;

    // Database variables
    private DatabaseReference productsTable;

    // Other variables
    private String packaging, productName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_item);

        // Initialize Variables
        buttonSubmit = (Button) findViewById(R.id.buttonSubmit);
        buttonCancel = (Button) findViewById(R.id.buttonCancel);
        editTextOtherPackaging = (EditText) findViewById(R.id.editTextOtherPackaging);
        editTextProductName = (EditText) findViewById(R.id.editTextProductName);
        editTextOtherPackaging = (EditText) findViewById(R.id.editTextOtherPackaging);
        spinnerPackaging = (Spinner) findViewById(R.id.spinnerPackaging);
        spinnerBranch = (Spinner) findViewById(R.id.spinnerBranch);

        // Database variables
        productsTable = FirebaseDatabase.getInstance().getReference("Products");

        // Events
        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewItem();
            }
        });

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBackHome();
            }
        });

        spinnerPackaging.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                determineToShowOther();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    // Events ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    ValueEventListener validateExistingPackagingThenAdd = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            if (dataSnapshot.getChildrenCount()==0L) {
                Toast.makeText(AddNewItem.this, "Add this new item", Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(AddNewItem.this, "Item already exists", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

    // Functions ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    private void goBackHome()
    {
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
    }

    private void determineToShowOther()
    {
        String selectedPackaging = spinnerPackaging.getSelectedItem().toString();
        if (selectedPackaging.equals("Others"))
        {
            editTextOtherPackaging.setVisibility(View.VISIBLE);
        }
        else
        {
            editTextOtherPackaging.setVisibility(View.INVISIBLE);
        }
    }

    private void clearFields()
    {
        editTextProductName.setText("");
        spinnerPackaging.setSelection(0);
    }

    private boolean userFieldValidated ()
    {
        if (productName.isEmpty() || packaging.isEmpty()) {
            return false;
        }
        else {
            return true;
        }
    }

    private void validateIfRepeatedThenAdd()
    {
        Query queryThisProduct = productsTable.orderByChild("productName").equalTo(productName);
        queryThisProduct.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount()==0L) {
                    writeToTable(true);
                    //Toast.makeText(AddNewItem.this, "This is a new product", Toast.LENGTH_SHORT).show();
                }
                else {
                    boolean productIsNew=true;
                    for (DataSnapshot thisSnapshot : dataSnapshot.getChildren())
                    {
                        Product thisProduct = thisSnapshot.getValue(Product.class);
                        if (thisProduct.getPackaging().equals(packaging)){
                            productIsNew=false;
                            break;
                        }
                    }
                    writeToTable(productIsNew);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void addNewItem()
    {
        productName = editTextProductName.getText().toString().trim();

        if (editTextOtherPackaging.getVisibility()==View.INVISIBLE) {                                   // When Other packaging is selected, get value of edit text
            packaging = spinnerPackaging.getSelectedItem().toString();
        }
        else {
            packaging = editTextOtherPackaging.getText().toString().trim();
        }

        if (spinnerPackaging.getSelectedItemPosition() != 0 && userFieldValidated()) {
            validateIfRepeatedThenAdd();
        }
        else {
            Toast.makeText(AddNewItem.this, "Please fill in the required fields", Toast.LENGTH_SHORT).show();
        }
    }

    private void writeToTable(boolean productIsNew)
    {
        if (productIsNew)
        {
            String productID = productsTable.push().getKey();
            Product newProduct = new Product(productName,packaging,productID);
            productsTable.child(productID).setValue(newProduct);

            Toast.makeText(this,"New product added successfully",Toast.LENGTH_LONG).show();
            clearFields();
        }
        else {
            Toast.makeText(this, "Product already exists!", Toast.LENGTH_SHORT).show();
        }
    }
}
