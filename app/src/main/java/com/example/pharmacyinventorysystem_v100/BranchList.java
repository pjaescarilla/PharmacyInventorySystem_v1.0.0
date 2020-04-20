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

public class BranchList extends ArrayAdapter<TimeAndDistance> {

    private Activity context;
    private List<TimeAndDistance> branchList;
    private List<String> branchNames;

    public BranchList(Activity context, List<TimeAndDistance> branchList, List<String> branchName) {
        super(context,R.layout.search_suggestions_2, branchList);
        this.context = context;
        this.branchList = branchList;
        this.branchNames = branchName;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater =context.getLayoutInflater();
        View listViewBranches = inflater.inflate(R.layout.search_suggestions_2,null,true);

        TextView textViewBranchName = (TextView) listViewBranches.findViewById(R.id.textViewResultProduct);
        TextView textViewDistance = (TextView) listViewBranches.findViewById(R.id.textViewReorderPoint);
        TextView textViewTime = (TextView) listViewBranches.findViewById(R.id.textViewEOQ);

        TimeAndDistance thisBranch = branchList.get(position);
        String thisBranchName = branchNames.get(position);

        textViewBranchName.setText(thisBranchName);
        textViewDistance.setText("Distance: " + thisBranch.getDistance());
        textViewTime.setText("Time: "+thisBranch.getTime()+" away");

        return listViewBranches;
    }
}
