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

public class ProductList2 extends ArrayAdapter<Product> {
    private Activity context;
    private List<Product> productList;

    public ProductList2(Activity context, List<Product> productList) {
        super(context,R.layout.search_suggestions_2,productList);
        this.context = context;
        this.productList = productList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater =context.getLayoutInflater();
        View listViewProduct = inflater.inflate(R.layout.search_suggestions_2,null,true);

        TextView textViewResultProduct = (TextView) listViewProduct.findViewById(R.id.textViewResultProduct);
        TextView textViewReorderPoint = (TextView) listViewProduct.findViewById(R.id.textViewReorderPoint);
        TextView textViewEOQ = (TextView) listViewProduct.findViewById(R.id.textViewEOQ);

        Product product = productList.get(position);

        textViewResultProduct.setText(product.getProductName()+" "+product.getPackaging());
        textViewReorderPoint.setText("Reorder Point: "+Integer.toString(product.getReorderPoint()));
        textViewEOQ.setText("Economic Order Quantity: "+Integer.toString(product.getEOQ()));

        return listViewProduct;
    }
}
