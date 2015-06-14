package com.mty.groupfuel;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;

import com.mty.groupfuel.datamodel.Fueling;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;


public class FuelLogFragment extends Fragment {
    private TableLayout logTable;
    private Context context;


    // TODO: Rename and change types and number of parameters
    public static FuelLogFragment newInstance() {
        FuelLogFragment fragment = new FuelLogFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public FuelLogFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_fuel_log, container, false);
        logTable = (TableLayout)view.findViewById(R.id.logTable);
        context = view.getContext();
        getFuelings(context);
        return view;
    }

    private void populateLogTable(Context context, List<Fueling> list, TableLayout tl) {
        if (tl == null) {
            tl = this.logTable;
        }
        for (Fueling fueling : list) {
            tl.addView(new FuelingUsage(context, fueling));
        }
    }

    private void getFuelings(final Context context) {
        ParseQuery<Fueling> query = Fueling.getQuery();
        query.whereEqualTo("User", ParseUser.getCurrentUser());
        query.include("Car");
        query.findInBackground(new FindCallback<Fueling>() {
            @Override
            public void done(List<Fueling> list, ParseException e) {
                if (e == null) {
                    populateLogTable(context, list, null);
                } else {
                    throw new RuntimeException(e.getMessage());
                }
            }
        });
    }

}
