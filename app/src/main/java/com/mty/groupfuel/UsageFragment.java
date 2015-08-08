package com.mty.groupfuel;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

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

    private Button button;

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
                getUsage(cars);
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
        Log.d(LOG_TAG, "onRefresh called from SwipeRefreshLayout");
        getUsage();
        setRefreshing(false);
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
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_usage, container, false);
        context = container.getContext();
        setListAdapter(new UsageAdapter(context, cars));
        if (cars.isEmpty()) {
            setCars(mCallback.getCars());
        }
        if (!cars.isEmpty()) {
            getUsage();
        }
        setOnRefreshListener(this);

        button = (Button) view.findViewById(R.id.footer);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.content_frame, new AddCarFragment(), AddCarFragment.class.getSimpleName());
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mMessageReceiver,
                new IntentFilter(Consts.BROADCAST_CARS));
        final ListView lv = getListView();
        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1, final int pos, long id) {
                new AlertDialog.Builder(context)
                        .setTitle("Delete car")
                        .setMessage("Are you sure you want to delete this car?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Car car = (Car) lv.getItemAtPosition(pos);
                                Map<String, String> params = new HashMap<>();
                                params.put("carNumber", car.getCarNumber());
                                ParseCloud.callFunctionInBackground("removeCar", params);
                                mCallback.getOwnedCars();
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .show();
                return true;
            }
        });
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
        getUsage(this.cars);
    }

    public void getUsage(final List<Car> cars) {
        if (datamap != null) {
            return;
        }
        final UsageFragment fragment = this;
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
                    getActivity().getSupportFragmentManager().beginTransaction().detach(fragment).attach(fragment).commit();
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