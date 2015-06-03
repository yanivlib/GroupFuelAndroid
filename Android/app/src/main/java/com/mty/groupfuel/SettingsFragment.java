package com.mty.groupfuel;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import com.mty.groupfuel.datamodel.Car;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SettingsFragment extends android.support.v4.app.Fragment {

    SettingsListAdapter listAdapter;
    ExpandableListView expListView;
    List<String> listDataHeader;
    Map<String, List<Object>> listDataChild;
    List<Car> cars;

    List<Object> carList;
    List<Object> personalList;
    List<Object> accountList;

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
        cars = new ArrayList<>();
        prepareListData();
        setCars(((MainActivity) getActivity()).getCars());
    }

    private void findViewsById(View view) {
        expListView = (ExpandableListView)view.findViewById(R.id.lvExp);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        findViewsById(view);
        listAdapter = new SettingsListAdapter(getActivity(), listDataHeader, listDataChild);
        expListView.setAdapter(listAdapter);
        return view;
    }

    public void setCars(List<Car> cars) {
        if (this.cars == null) {
            this.cars = new ArrayList<>();
        }
        this.cars.clear();
        this.cars.addAll(cars);
        updateCarList();
    }

    public void updateCarList() {
        carList.clear();
        carList.addAll(this.cars);
        carList.add(Consts.BUTTON_ADDREMOVE);
    }

    private void updatePersonalList() {
        personalList.add(getString(R.string.change_password));
        personalList.add(getString(R.string.change_notifications));
    }

    private void updateAccountList() {
        accountList.add(Consts.BUTTON_UPDATE);
        accountList.add(Consts.BUTTON_LOGOUT);
        if (!ParseFacebookUtils.isLinked(ParseUser.getCurrentUser())) {
            accountList.add(Consts.BUTTON_FACEBOOK);
        }
    }

    private void prepareListData() {
        listDataHeader= new ArrayList<>();
        listDataChild = new HashMap<>();

        listDataHeader.add(getString(R.string.manage_cars));
        listDataHeader.add(getString(R.string.change_personal));
        listDataHeader.add(getString(R.string.account_settings));

        carList = new ArrayList<>();
        personalList = new ArrayList<>();
        accountList = new ArrayList<>();

        updateCarList();
        updatePersonalList();
        updateAccountList();

        listDataChild.put(listDataHeader.get(0), carList);
        listDataChild.put(listDataHeader.get(1), personalList);
        listDataChild.put(listDataHeader.get(2), accountList);
    }
}
