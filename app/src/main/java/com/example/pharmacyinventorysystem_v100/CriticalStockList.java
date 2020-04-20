package com.example.pharmacyinventorysystem_v100;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.w3c.dom.Text;

import java.util.List;

public class CriticalStockList extends ArrayAdapter<CriticalStock> {

    private Activity context;
    private List<CriticalStock> criticalStockList;

    public CriticalStockList(Activity context, List<CriticalStock> criticalStockList) {
        super(context,R.layout.search_suggestions_2, criticalStockList);
        this.context = context;
        this.criticalStockList = criticalStockList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater =context.getLayoutInflater();
        View listViewCriticals = inflater.inflate(R.layout.search_suggestions,null,true);

        TextView textViewProduct = (TextView) listViewCriticals.findViewById(R.id.textViewResultProduct);
        TextView textViewStockCount = (TextView) listViewCriticals.findViewById(R.id.textViewResultPackaging);

        CriticalStock thisCriticalStock = criticalStockList.get(position);

        textViewProduct.setText(thisCriticalStock.getProductName()+" "+thisCriticalStock.getPackaging());
        textViewStockCount.setText("Remaining Stock: "+Integer.toString(thisCriticalStock.getQuantity()));

        return listViewCriticals;
    }
}
