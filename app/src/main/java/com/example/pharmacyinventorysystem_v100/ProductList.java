package com.example.pharmacyinventorysystem_v100;

import android.app.Activity;
import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class ProductList extends ArrayAdapter<Product> {
    private Activity context;
    private List<Product> productList;

    public ProductList (Activity context, List<Product> productList) {
        super(context,R.layout.search_suggestions,productList);
        this.context = context;
        this.productList = productList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater =context.getLayoutInflater();
        View listViewProduct = inflater.inflate(R.layout.search_suggestions,null,true);

        TextView textViewResultProduct = (TextView) listViewProduct.findViewById(R.id.textViewResultProduct);
        TextView textViewResultPackaging = (TextView) listViewProduct.findViewById(R.id.textViewResultPackaging);

        Product product = productList.get(position);

        textViewResultProduct.setText(product.getProductName());
        textViewResultPackaging.setText(product.getPackaging());

        return listViewProduct;
    }
}
