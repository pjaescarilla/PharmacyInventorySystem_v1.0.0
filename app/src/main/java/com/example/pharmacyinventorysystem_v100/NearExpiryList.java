package com.example.pharmacyinventorysystem_v100;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class NearExpiryList extends ArrayAdapter<NearExpiry> {

    private Activity context;
    private List<NearExpiry> nearExpiryList;

    public NearExpiryList(Activity context, List<NearExpiry> nearExpiryList) {
        super(context,R.layout.search_suggestions_2, nearExpiryList);
        this.context = context;
        this.nearExpiryList = nearExpiryList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater =context.getLayoutInflater();
        View listViewCriticals = inflater.inflate(R.layout.search_suggestions_2,null,true);

        TextView textViewProduct = listViewCriticals.findViewById(R.id.textViewResultProduct);
        TextView textViewExpDate = listViewCriticals.findViewById(R.id.textViewReorderPoint);
        TextView textViewInStock = listViewCriticals.findViewById(R.id.textViewEOQ);

        NearExpiry thisNearlyExpiredProduct = nearExpiryList.get(position);

        textViewProduct.setText(thisNearlyExpiredProduct.getProductName()+ " " + thisNearlyExpiredProduct.getPackaging());
        textViewExpDate.setText("Expiry Date: "+thisNearlyExpiredProduct.getExpiryDate());
        textViewInStock.setText("In stock: "+Integer.toString(thisNearlyExpiredProduct.getStockCount()));

        return listViewCriticals;
    }
}
