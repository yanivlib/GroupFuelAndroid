package com.mty.groupfuel;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.mty.groupfuel.datamodel.Fueling;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;


public class FuelLogFragment extends ListFragment {
    private static final String FUELING_LIST = "fueling_list";
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

    public void setFuelingList(List<Fueling> fuelingList) {
        FuelLogFragment.fuelingList = fuelingList;
        ((FuelingAdapter) getListAdapter()).notifyDataSetChanged();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(FUELING_LIST, (ArrayList) fuelingList);
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            ArrayList<Fueling> fuelingList = savedInstanceState.getParcelableArrayList(FUELING_LIST);
            setFuelingList(fuelingList);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fuelingList = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        setListAdapter(new FuelingAdapter(view.getContext(), fuelingList));
        if (savedInstanceState != null) {
            ArrayList<Fueling> fuelingList = savedInstanceState.getParcelableArrayList(FUELING_LIST);
            setFuelingList(fuelingList);
        }
        if (fuelingList.isEmpty()) {
            getFuelings();
        }
        return view;
    }

    private void getFuelings() {
        System.out.println("got to getFueling");
        ParseQuery<Fueling> query = Fueling.getQuery();
        query.whereEqualTo("User", ParseUser.getCurrentUser());
        query.include("Car");
        query.findInBackground(new FindCallback<Fueling>() {
            @Override
            public void done(List<Fueling> list, ParseException e) {
                System.out.println("got answer");
                if (e == null) {
                    System.out.println("updating getFueling");
                    setFuelingList(list);
                } else {
                    throw new RuntimeException(e.getMessage());
                }
            }
        });
    }


    public class FuelingAdapter extends ArrayAdapter<Fueling> {
        public FuelingAdapter(Context c, List<Fueling> items) {
            super(c, 0, items);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            FuelLogItem fuelLogItem = (FuelLogItem) convertView;
            if (null == fuelLogItem) {
                fuelLogItem = FuelLogItem.inflate(parent);
            }
            fuelLogItem.setFueling(getItem(position));
            return fuelLogItem;
        }
    }
}
