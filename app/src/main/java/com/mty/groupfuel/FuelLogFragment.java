package com.mty.groupfuel;

import android.content.Context;
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

import java.util.ArrayList;
import java.util.List;


public class FuelLogFragment extends Fragment {
    private static final String FUELING_LIST = "fueling_list";
    private static TableLayout logTable;
    private static Context context;
    private static List<Fueling> fuelingList;

    public FuelLogFragment() {
        // Required empty public constructor
    }

    public static FuelLogFragment newInstance() {
        FuelLogFragment fragment = new FuelLogFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    private static void populateLogTable(Context context, List<Fueling> list, TableLayout tl) {
        for (Fueling fueling : list) {
            tl.addView(new FuelingUsage(context, fueling));
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(FUELING_LIST, (ArrayList) fuelingList);
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            fuelingList = savedInstanceState.getParcelableArrayList(FUELING_LIST);
        }
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
        if (savedInstanceState != null) {
            fuelingList = savedInstanceState.getParcelableArrayList(FUELING_LIST);
        }
        if (fuelingList == null) {
            getFuelings(context);
        } else {
            populateLogTable(context, fuelingList, logTable);
        }
        return view;
    }

    private void getFuelings(final Context context) {
        if (fuelingList != null) {
            return;
        }
        ParseQuery<Fueling> query = Fueling.getQuery();
        query.whereEqualTo("User", ParseUser.getCurrentUser());
        query.include("Car");
        query.findInBackground(new FindCallback<Fueling>() {
            @Override
            public void done(List<Fueling> list, ParseException e) {
                if (e == null) {
                    fuelingList = list;
                    populateLogTable(context, list, FuelLogFragment.logTable);
                } else {
                    throw new RuntimeException(e.getMessage());
                }
            }
        });
    }

}
