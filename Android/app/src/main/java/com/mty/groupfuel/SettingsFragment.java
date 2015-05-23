package com.mty.groupfuel;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import com.mty.groupfuel.datamodel.Car;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SettingsFragment extends android.support.v4.app.Fragment {

    SettingsListAdapter listAdapter;
    ExpandableListView expListView;
    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;

    public static SettingsFragment newInstance() {
        SettingsFragment fragment = new SettingsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prepareListData();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        expListView = (ExpandableListView)view.findViewById(R.id.lvExp);
        listAdapter = new SettingsListAdapter(getActivity(), listDataHeader, listDataChild);
        expListView.setAdapter(listAdapter);
        return view;
    }

    private void prepareListData() {
        listDataHeader= new ArrayList<>();
        listDataChild = new HashMap<>();

        listDataHeader.add("Manage Cars");
        listDataHeader.add("Change personal settings");
        listDataHeader.add("More settings");

        List<String> carList = new ArrayList<>();
        Car[] cars = ((MainActivity) getActivity()).getCars();
        for (Car car : cars) {
            carList.add(car.getDisplayName());
        }
        carList.add("");

        List<String> b = new ArrayList<>();
        b.add("Change password");
        b.add("Change notification settings ");

        List<String> c = new ArrayList<>();
        c.add("...");

        listDataChild.put(listDataHeader.get(0), carList);
        listDataChild.put(listDataHeader.get(1), b);
        listDataChild.put(listDataHeader.get(2), c);
    }
}
