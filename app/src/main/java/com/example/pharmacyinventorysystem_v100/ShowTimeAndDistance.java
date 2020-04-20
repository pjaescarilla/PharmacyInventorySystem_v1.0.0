package com.example.pharmacyinventorysystem_v100;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ShowTimeAndDistance extends AppCompatActivity {

    // View variables
    private ListView listViewOtherBranch;
    private TextView textViewBack;

    // Firebase variables
    private DatabaseReference DistanceAndTimeTable;

    // Other variables
    private List<String> branchNames = new ArrayList<>();
    private List<TimeAndDistance> otherBranchesTD = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_time_and_distance);

        initializeVariables();
        DistanceAndTimeTable.addListenerForSingleValueEvent(listOtherBranches);
        textViewBack.setOnClickListener(backToDisplayProduct);

    }

    // Events ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
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

            BranchList allOtherBranches = new BranchList(ShowTimeAndDistance.this,otherBranchesTD,branchNames);
            listViewOtherBranch.setAdapter(allOtherBranches);
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

    View.OnClickListener backToDisplayProduct = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            startActivity(new Intent(ShowTimeAndDistance.this,displayProducts.class));
        }
    };

    // Methods ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    private void initializeVariables() {
        listViewOtherBranch = findViewById(R.id.listViewOtherBranch);
        textViewBack = findViewById(R.id.textViewBack);
        DistanceAndTimeTable = FirebaseDatabase.getInstance().getReference().child("DistanceAndTime").child(((GlobalVariables)getApplication()).getThisBranch());
    }
}
