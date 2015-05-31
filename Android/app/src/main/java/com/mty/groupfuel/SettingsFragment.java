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

public class SettingsFragment extends android.support.v4.app.Fragment {

    SettingsListAdapter listAdapter;
    ExpandableListView expListView;
    List<String> listDataHeader;
    HashMap<String, List<Object>> listDataChild;
    Car[] cars;

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
        setCars(((MainActivity) getActivity()).getCars());
        prepareListData();

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

    public void setCars(Car[] cars) {
       this.cars = cars;
    }

    public void updateCarList(Car[] cars) {
        //setCars(((MainActivity) getActivity()).getCars());
        setCars(cars);
        List<Object> carList = listDataChild.get(getString(R.string.manage_cars));
        if (carList == null) {
            return;
        }
        carList.clear();
        for (Car car : cars) {
            carList.add(car);
        }
    }

    private void prepareListData() {
        listDataHeader= new ArrayList<>();
        listDataChild = new HashMap<>();

        listDataHeader.add(getString(R.string.manage_cars));
        listDataHeader.add(getString(R.string.change_personal));
        listDataHeader.add(getString(R.string.account_settings));

        List<Object> carList = new ArrayList<>();
        updateCarList(cars);
        if (cars != null) {
            for (Car car : cars) {
                carList.add(car);
            }
        }
        carList.add(Consts.BUTTON_ADDREMOVE);

        List<Object> b = new ArrayList<>();
        b.add(getString(R.string.change_password));
        b.add(getString(R.string.change_notifications));

        List<Object> c = new ArrayList<>();
        c.add(Consts.BUTTON_UPDATE);
        c.add(Consts.BUTTON_LOGOUT);
        if (!ParseFacebookUtils.isLinked(ParseUser.getCurrentUser())) {
            c.add(Consts.BUTTON_FACEBOOK);
        }

        listDataChild.put(listDataHeader.get(0), carList);
        listDataChild.put(listDataHeader.get(1), b);
        listDataChild.put(listDataHeader.get(2), c);
    }
}
