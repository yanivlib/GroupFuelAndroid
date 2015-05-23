package com.mty.groupfuel;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.mty.groupfuel.datamodel.Car;
import com.mty.groupfuel.datamodel.CarModel;
import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainFragment extends android.support.v4.app.Fragment {
    public static final String ARG_PAGE = "ARG_PAGE";

    private int mPage;

    public static MainFragment newInstance(int page) {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        MainFragment fragment = new MainFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPage = getArguments().getInt(ARG_PAGE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        TextView textView = (TextView) view;
        textView.setText("Fragment #" + mPage);
        return view;
    }

    static void getCarMakes() {
        ParseCloud.callFunctionInBackground("getCarMakes", new HashMap<String, Object>(), new FunctionCallback<ArrayList>() {
            public void done(ArrayList result, ParseException e) {
                if (e == null) {
                    for (int i = 0; i < result.size(); i++) {
                        System.out.println(" -- getCarMakes result[" + i + "] = " + result.get(i).toString());
                    }
                } else {
                    System.out.println(" -- getCarMakes exception: " + e.getMessage());
                }
            }
        });
    }

    static void getCarModels(String make) {
        final HashMap<String, String> params = new HashMap<>();
        params.put("make", make);
        ParseCloud.callFunctionInBackground("getCarModels", params, new FunctionCallback<ArrayList>() {
            public void done(ArrayList result, ParseException e) {
                if (e == null) {
                    for (int i = 0; i < result.size(); i++) {
                        System.out.println(" -- getCarModels result[" + i + "] = " + result.get(i).toString());
                        CarModel carModel = (CarModel) result.get(i);
                        System.out.println("Model is: " + carModel.getModel());
                    }
                } else {
                    System.out.println(" -- getCarModels exception: " + e.getMessage());
                }
            }
        });
    }

    static void getOwnedCars() {
        ParseCloud.callFunctionInBackground("getOwnedCars", new HashMap<String, Object>(), new FunctionCallback<ArrayList>() {
            @Override
            public void done(ArrayList result, ParseException e) {
                if (e == null) {
                    for (int i = 0; i < result.size(); i++) {
                        System.out.println(" -- getOwnedCars result[" + i + "] = " + result.get(i).toString());
                        Car car = (Car)result.get(i);
                        System.out.println(" -- getOwnedCars car.getCarNumber() = " + car.getCarNumber());
                    }
                }
            }
        });
    }
}