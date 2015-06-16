package com.mty.groupfuel;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.mty.groupfuel.datamodel.Car;
import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UsageFragment extends android.support.v4.app.ListFragment {

    getCarsListener mCallback;

    private Context context;
    private Map<String,Map<String, Number>> datamap;
    private List<Car> cars;

    public UsageFragment() {
    }

    public static UsageFragment newInstance() {
        UsageFragment fragment = new UsageFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public static JSONObject getPointer(String cls, String objectID) {
        JSONObject result = new JSONObject();
        try {
            result.put("__type", "Pointer");
            result.put("className", cls);
            result.put("objectId", objectID);
        } catch (JSONException j) {
            return null;
        }
        return result;
    }

    public static List<JSONObject> getPointers(List<Car> list) {
        List<JSONObject> res = new ArrayList<>();
        for (Car car : list) {
            res.add(getPointer("Car", car.getObjectId()));
        }
        return res;
    }

    public void setCars(List<Car> cars) {
        if (cars != null && !cars.equals(this.cars)) {
            this.cars = cars;
            ((UsageAdapter) getListAdapter()).notifyDataSetChanged();
            if (datamap == null) {
                getUsage(cars);
            }
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallback = (getCarsListener)activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement getCarsListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cars = new ArrayList<>();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        //outState.putParcelableArrayList("cars", cars);
        super.onSaveInstanceState(outState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        context = view.getContext();
        setListAdapter(new UsageAdapter(context, cars));
        if (cars.isEmpty()) {
            setCars(mCallback.getCars());
        }
        if (!cars.isEmpty()) {
            getUsage(cars);
        }
        return view;
    }

    public void getUsage(final List<Car> cars) {
        if (datamap != null) {
            return;
        }
        List<JSONObject> carPointers = new ArrayList<>(getPointers(cars));
        final Map<String, List<JSONObject>> params = new HashMap<>();
        params.put("cars", carPointers);
        ParseCloud.callFunctionInBackground("getUsage", params, new FunctionCallback<Map<String, Map<String, Number>>>() {
            @Override
            public void done(Map<String, Map<String, Number>> result, ParseException e) {
                if (e == null) {
                    datamap = result;
                    setCars(cars);
                } else {
                    System.out.println(e.getMessage());
                }
            }
        });
    }

    public class UsageAdapter extends ArrayAdapter<Car> {
        public UsageAdapter(Context c, List<Car> items) {
            super(c, 0, items);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            CarUsage carUsage = (CarUsage) convertView;
            if (null == carUsage) {
                carUsage = CarUsage.inflate(parent);
            }
            Car car = getItem(position);
            carUsage.setData(car, datamap.get(car.getObjectId()));
            return carUsage;
        }
    }
}