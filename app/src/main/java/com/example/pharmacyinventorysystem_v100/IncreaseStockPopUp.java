package com.example.pharmacyinventorysystem_v100;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.DisplayMetrics;

public class IncreaseStockPopUp extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_increase_stock_pop_up);

        setResolution();
    }

    private void setResolution() {
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        Double width = dm.widthPixels*0.8;
        Double height = dm.heightPixels*0.6;
        getWindow().setLayout(width.intValue(),height.intValue());
    }
}
