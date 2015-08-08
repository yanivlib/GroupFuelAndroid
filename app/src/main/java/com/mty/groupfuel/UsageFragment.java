package com.mty.groupfuel;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
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

public class UsageFragment extends SwipeRefreshListFragment implements SwipeRefreshLayout.OnRefreshListener{
    private static final String LOG_TAG = UsageFragment.class.getSimpleName();

    getCarsListener mCallback;

    private Context context;
    private Map<String,Map<String, Number>> datamap;
    private List<Car> cars;

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int carAmount = intent.getIntExtra("cars", 0);
            if (carAmount > 0) {
                setCars(mCallback.getCars());
            }
            Log.d(LOG_TAG, "Got message: " + carAmount);
        }
    };

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
                getUsage(cars, this);
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        //outState.putParcelableArrayList("cars", cars);
        super.onSaveInstanceState(outState);
    }

    // Implemented methods

    public void onRefresh() {
        Log.d("a", "onRefresh called from SwipeRefreshLayout");
        getUsage();
        //getView().invalidate();
    }

    // Lifecycle methods

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
        ArrayList<Car> currentCars = (ArrayList<Car>)mCallback.getCars();
        if (currentCars != null) {
            setCars(currentCars);
        } else {
            cars = new ArrayList<>();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = super.onCreateView(inflater, container, savedInstanceState);
        context = container.getContext();
        setListAdapter(new UsageAdapter(context, cars));
        if (cars.isEmpty()) {
            setCars(mCallback.getCars());
        }
        if (!cars.isEmpty()) {
            getUsage();
        }
        setOnRefreshListener(this);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mMessageReceiver,
                new IntentFilter(Consts.BROADCAST_CARS));

    }

    @Override
    public void onPause() {
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mMessageReceiver);
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void getUsage() {
        getUsage(this.cars, this);
    }

    public void getUsage(final List<Car> cars, final Fragment fragment) {
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
                    setRefreshing(false);
                    //notifyDataSetChanged();
                    //getListView().deferNotifyDataSetChanged();
                    //FragmentTransaction tr = getChildFragmentManager().beginTransaction();
                    //tr.replace(R.id.viewpager, fragment);
                    //tr.commit();
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