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
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.mty.groupfuel.datamodel.Car;
import com.mty.groupfuel.datamodel.User;
import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseUser;

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
            int carAmount = intent.getIntExtra(Consts.BROADCAST_CARS, 0);
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

        Button button = (Button) view.findViewById(R.id.footer);
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
        getListView().setOnItemLongClickListener(new CarListener());
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

    private void updateView() {
        getActivity().getSupportFragmentManager().beginTransaction().detach(this).attach(this).commit();
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
                    updateView();
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
            CarItem carItem = (CarItem) convertView;
            if (null == carItem) {
                carItem = CarItem.inflate(parent);
            }
            Car car = getItem(position);
            if (datamap.get(car.getObjectId()) != null) {
                carItem.setData(car, datamap.get(car.getObjectId()));
            } else {
                Log.d(LOG_TAG, "skipping car" + car.getDisplayName());
            }
            return carItem;
        }
    }

    private class CarListener implements AdapterView.OnItemLongClickListener {
        public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int pos, long id) {
            final Car car = (Car) getListView().getItemAtPosition(pos);
            final boolean owner = car.getOwner().equals(ParseUser.getCurrentUser());
            CharSequence options[];
            if (owner) {
                options = new CharSequence[]{"Add driver", "Remove driver", "Remove car"};
            } else {
                options = new CharSequence[]{"Remove from my cars"};
            }
            AlertDialog.Builder parentBuilder = new AlertDialog.Builder(context);
            parentBuilder.setItems(options, new DialogInterface.OnClickListener() {
                private static final String EMAIL = "email";
                private static final String NUMBER = "carNumber";
                private final Map<String, Object> params = new HashMap<>();
                private final AlertDialog.Builder builder = new AlertDialog.Builder(context);

                private void removeDriver() {
                    final ArrayList<User> drivers = car.getDrivers();
                    if (drivers.isEmpty()) {
                        builder.setMessage("There are no drivers to remove");
                        return;
                    }
                    ArrayList<String> names = new ArrayList<>(drivers.size());
                    for (User driver : drivers) {
                        names.add(driver.getDisplayName());
                    }
                    builder.setTitle("Please select driver");
                    builder.setItems(names.toArray(new String[names.size()]), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            params.put(EMAIL, drivers.get(which).getEmail());
                            ParseCloud.callFunctionInBackground("removeDriver", params, new FunctionCallback<Object>() {
                                @Override
                                public void done(Object o, ParseException e) {
                                    if (e == null) {
                                        mCallback.syncOwnedCars();
                                        Toast.makeText(context, "Driver removed", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Alerter.createErrorAlert(e, context).show();
                                    }
                                }
                            });
                        }
                    });
                }

                private void addDriver() {
                    final EditText input = new EditText(context);
                    input.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                    builder.setTitle("Please enter email:");
                    builder.setView(input);
                    builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            params.put(EMAIL, input.getText().toString());
                            ParseCloud.callFunctionInBackground("addDriver", params, new FunctionCallback<Object>() {
                                @Override
                                public void done(Object o, ParseException e) {
                                    if (e == null) {
                                        mCallback.syncOwnedCars();
                                        Toast.makeText(context, "Driver added", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Alerter.createErrorAlert(e, context).show();
                                    }
                                }
                            });

                        }
                    });
                    builder.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                }

                private void removeCar() {
                    builder.setMessage("Are you sure you want to remove this car?");
                    builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ParseCloud.callFunctionInBackground("removeCar", params, new FunctionCallback<Object>() {
                                @Override
                                public void done(Object o, ParseException e) {
                                    if (e == null) {
                                        mCallback.syncOwnedCars();
                                        Toast.makeText(context, "Car removed", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Alerter.createErrorAlert(e, context).show();
                                    }
                                }
                            });
                        }
                    });
                    builder.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                }

                private void onClickOwner(int which) {
                    switch (which) {
                        case 0:
                            addDriver();
                            break;
                        case 1:
                            removeDriver();
                            break;
                        case 2:
                            removeCar();
                            break;
                    }
                }

                private void onClickDriver() {
                    removeCar();
                }

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    params.put(NUMBER, car.getCarNumber());
                    dialog.dismiss();
                    if (owner) {
                        onClickOwner(which);
                    } else {
                        onClickDriver();
                    }
                    builder.show();
                }
            });
            parentBuilder.show();
            return true;
        }
    }

}