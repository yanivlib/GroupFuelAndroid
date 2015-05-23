package com.mty.groupfuel;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        expListView = (ExpandableListView)getActivity().findViewById(R.id.lvExp);
        prepareListData();
        listAdapter = new SettingsListAdapter(getActivity(), listDataHeader, listDataChild);
        expListView.setAdapter(listAdapter);
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    private void prepareListData() {
        listDataHeader= new ArrayList<>();
        listDataChild = new HashMap<>();

        listDataHeader.add("a");
        listDataHeader.add("b");
    }
}
