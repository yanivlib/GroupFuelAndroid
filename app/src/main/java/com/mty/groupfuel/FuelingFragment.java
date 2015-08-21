package com.mty.groupfuel;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.mty.groupfuel.datamodel.Car;
import com.mty.groupfuel.datamodel.Fueling;
import com.mty.groupfuel.datamodel.GasStation;
import com.mty.groupfuel.datamodel.User;
import com.parse.FindCallback;
import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FuelingFragment extends android.support.v4.app.Fragment implements View.OnClickListener {
    private final static String LOG_TAG = FuelingFragment.class.getSimpleName();
    private static ProgressDialog progressDialog;
    Context context;
    // Interfaces
    getCarsListener mCallback;
    // Views
    private EditText mileageEditText;
    private EditText priceEditText;
    private EditText amountEditText;
    private Spinner carSpinner;
    private Button sendButton;
    private String pleaseSelect;
    private Spinner citySpinner;
    private Spinner stationsSpinner;
    private RadioGroup radioGroup;
    private TextView cityText;
    // Fields
    private Map<String, List<GasStation>> cityToStation;
    private List<Car> cars;
    private List<String> cities;
    private List<GasStation> stationsNearby;
    private List<GasStation> stationsInCity;
    private ParseGeoPoint location;
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case Consts.BROADCAST_CARS:
                    int carAmount = intent.getIntExtra(Consts.BROADCAST_CARS, 0);
                    if (carAmount > 0) {
                        setCars(mCallback.getCars());
                    }
                    break;
                case Consts.BROADCAST_LOCATION:
                    if (intent.getIntExtra(Consts.BROADCAST_LOCATION, 0) != 0) {
                        setLocation(mCallback.getLocation());
                    }
            }
            Log.d(LOG_TAG, "Got message: " + intent.toString());
        }
    };

    //Contructors
    public FuelingFragment() {
    }

    // Factory methods
    public static FuelingFragment newInstance() {
        Bundle args = new Bundle();
        FuelingFragment fuelingFragment = new FuelingFragment();
        fuelingFragment.setArguments(args);
        return fuelingFragment;
    }

    //Static methods
    private static Number numberFromEditText(EditText editText) {
        try {
            return Integer.parseInt(editText.getText().toString());
        } catch (NumberFormatException e) {
            return 0;
        }

    }

    // Getters and Setters
    public void setStationsNearby(List<GasStation> stations) {
        this.stationsNearby = stations;
    }

    public void setStationsInCity(List<GasStation> stations) {
        this.stationsInCity = stations;
    }

    public void setCities(List<String> cities) {
        this.cities = cities;
    }

    public void setCars(List<Car> cars) {
        if (cars == null) {
            return;
        }
        this.cars.clear();
        this.cars.addAll(cars);
    }

    public void setLocation(ParseGeoPoint location) {
        this.location = location;
        if (location != null) {
            RadioButton button = (RadioButton) radioGroup.getChildAt(0);
            button.setEnabled(true);
        }
    }

    private void setStationsInSpinner(List<GasStation> list) {
        ArrayAdapter<Object> adapter = (ArrayAdapter<Object>) stationsSpinner.getAdapter();
        adapter.clear();
        adapter.add(pleaseSelect);
        adapter.addAll(list);
    }

    private void setCitiesInSpinner(List<String> list) {
        ArrayAdapter<Object> adapter = (ArrayAdapter<Object>) citySpinner.getAdapter();
        adapter.clear();
        adapter.add(pleaseSelect);
        adapter.addAll(list);
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
        this.cars = new ArrayList<>();
        setCars(mCallback.getCars());
        setCities(new ArrayList<String>());
        location = mCallback.getLocation();
        if (location != null) {
            getStationsByLocation(mCallback.getLocation());
        }
        pleaseSelect = getString(R.string.please_select);
        cityToStation = new HashMap<>();
        stationsNearby = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fueling, container, false);
        findViewsById(view);
        this.context = view.getContext();
        attachAdapter(view, cars.toArray(), carSpinner, new CarSelect());
        attachAdapter(view, cities.toArray(), citySpinner, new CitySelect());
        attachAdapter(view, new GasStation[]{}, stationsSpinner, new StationSelect());
        syncCities();

        MainActivity.fab.setVisibility(View.INVISIBLE);
        radioGroup.setOnCheckedChangeListener(new OnRadioButtonClicked());
        if (location == null) {
            RadioButton button = (RadioButton) radioGroup.getChildAt(0);
            button.setEnabled(false);
        }
        citySpinner.setEnabled(false);
        citySpinner.setVisibility(View.INVISIBLE);
        cityText.setVisibility(View.INVISIBLE);
        sendButton.setOnClickListener(this);
        sendButton.setEnabled(false);
        return view;
    }

    @Override
    public void onPause() {
        MainActivity.fab.setVisibility(View.VISIBLE);
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mMessageReceiver);
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Consts.BROADCAST_CARS);
        filter.addAction(Consts.BROADCAST_LOCATION);
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mMessageReceiver, filter);

    }

    private void findViewsById(View view) {
        carSpinner = (Spinner)view.findViewById(R.id.fueling_car);
        mileageEditText = (EditText)view.findViewById(R.id.fueling_mileage);
        priceEditText = (EditText)view.findViewById(R.id.fueling_price);
        amountEditText = (EditText)view.findViewById(R.id.fueling_amount);
        citySpinner = (Spinner) view.findViewById(R.id.fueling_city);
        stationsSpinner = (Spinner) view.findViewById(R.id.fueling_stations);
        sendButton = (Button)view.findViewById(R.id.fueling_send);
        radioGroup = (RadioGroup) view.findViewById(R.id.fueling_rg);
        cityText = (TextView) view.findViewById(R.id.city_text);
    }

    private void attachAdapter(View view, Object[] array, Spinner spinner, AdapterView.OnItemSelectedListener onItemSelectedListener) {
        List<Object> arrayList = new ArrayList<>();
        arrayList.add(pleaseSelect);
        arrayList.addAll(Arrays.asList(array));
        ArrayAdapter<Object> adapter = new ArrayAdapter<Object>(view.getContext(),
                android.R.layout.simple_spinner_item, arrayList) {
            private View getViewAux(int position, View convertView, ViewGroup parent) {
                TextView textView = (TextView) super.getView(position, convertView, parent);
                Object object = getItem(position);
                for (Method method : object.getClass().getMethods()) {
                    if (method.getName().equals("getDisplayName")) {
                        try {
                            String result = (String) method.invoke(object, null);
                            textView.setText(result);
                        } catch (IllegalAccessException iae) {
                            break;
                        } catch (InvocationTargetException ite) {
                            break;
                        }
                    }
                }
                return textView;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                return getViewAux(position, convertView, parent);
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                return getViewAux(position, convertView, parent);
            }
        };
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(onItemSelectedListener);
    }

    void getStationsByCity(final String city) {
        if (cityToStation.get(city) != null) {
            setStationsInCity(cityToStation.get(city));
        }
        ParseQuery<GasStation> query = GasStation.getQuery();
        query.whereEqualTo("Municipality", city);
        query.findInBackground(new FindCallback<GasStation>() {
            @Override
            public void done(List<GasStation> result, ParseException e) {
                if (e == null) {
                    cityToStation.put(city, result);
                    setStationsInCity(result);
                    setStationsInSpinner(stationsInCity);
                } else {
                    throw new RuntimeException(e.getMessage());
                }
            }
        });
    }

    void getStationsByLocation(final ParseGeoPoint point) {
        ParseQuery<GasStation> query = GasStation.getQuery();
        query.whereNear("Location", point);
        query.findInBackground(new FindCallback<GasStation>() {
            @Override
            public void done(List<GasStation> list, ParseException e) {
                if (e == null) {
                    setStationsNearby(list);
                } else {
                    throw new RuntimeException(e.getMessage());
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        sendButton.setEnabled(false);
        StringBuilder error = new StringBuilder();
        Number amount = numberFromEditText(amountEditText);
        Number mileage = numberFromEditText(mileageEditText);
        Number price = numberFromEditText(priceEditText);
        User user = (User) ParseUser.getCurrentUser();
        if (carSpinner.getSelectedItemPosition() == 0) {
            Alerter.createErrorAlert("No car selected. Please select car to continue", context).show();
            sendButton.setEnabled(true);
            return;
        }
        Car car = (Car) carSpinner.getSelectedItem();
        GasStation station = null;
        if (stationsSpinner.getSelectedItemPosition() > 0) {
            station = (GasStation) stationsSpinner.getSelectedItem();
        }
        if (amount == 0) {
            error.append(getString(R.string.amount_empty));
        }
        if (mileage == 0) {
            error.append(getString(R.string.mileage_empty));
        }
        if (price == 0) {
            error.append(getString(R.string.price_empty));
        }
        if (car.getObjectId().equals(Consts.OBJECTID_NULL)) {
            error.append("Illegal car");
        }
        if (error.length() > 0) {
            Alerter.createErrorAlert(error.toString(), context).show();
            sendButton.setEnabled(true);
            return;
        }
        Fueling fueling = new Fueling();
        fueling.setAmount(amount);
        fueling.setMileage(mileage);
        fueling.setPrice(price);
        fueling.setUser(user);
        fueling.setFuelType(car.getModel().getFuelType());
        fueling.setGasStation(station);
        fueling.put("Car", ParseObject.createWithoutData("Car", car.getObjectId()));
        progressDialog = ProgressDialog.show(context, getResources().getString(R.string.wait), getResources().getString(R.string.fueling_progress));
        fueling.saveEventually(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                progressDialog.dismiss();
                if (e != null) {
                    Alerter.createErrorAlert(e, context).show();
                } else {
                    Toast.makeText(context, context.getString(R.string.fueling_updated), Toast.LENGTH_LONG).show();
                }
            }
        });
        getActivity().getSupportFragmentManager().popBackStack();
    }

    void syncCities() {
        if (!cities.isEmpty()) {
            return;
        }
        Log.d(LOG_TAG, "fetching cities...");
        ParseCloud.callFunctionInBackground("getCities", new HashMap<String, Object>(), new FunctionCallback<ArrayList<String>>() {
            @Override
            public void done(ArrayList<String> cities, ParseException e) {
                if (e == null) {
                    setCities(cities);
                    setCitiesInSpinner(cities);

                    Log.d(LOG_TAG, "got " + cities.size() + " cities");
                } else {
                    Alerter.createErrorAlert(e, context);
                }
            }
        });
    }

    private class StationSelect implements AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            stationsSpinner.setEnabled(true);
            if (position > 0) {
                sendButton.setEnabled(true);
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }

    private class CitySelect implements AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            stationsSpinner.setEnabled(true);
            if (position > 0) {
                String city = (String) parent.getItemAtPosition(position);
                getStationsByCity(city);
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }

    private class CarSelect implements AdapterView.OnItemSelectedListener {
        CarSelect() {
        }

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
            if (pos > 0) {
                sendButton.setEnabled(true);
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
            // Do Nothing
        }
    }

    private class OnRadioButtonClicked implements RadioGroup.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            switch (checkedId) {
                case -1:
                    // Unchecked
                case R.id.radio_location:
                    setStationsInSpinner(stationsNearby);
                    citySpinner.setEnabled(false);
                    citySpinner.setVisibility(View.INVISIBLE);
                    cityText.setVisibility(View.INVISIBLE);
                    break;
                case R.id.radio_city:
                    citySpinner.setVisibility(View.VISIBLE);
                    cityText.setVisibility(View.VISIBLE);
                    citySpinner.setEnabled(true);
                    break;
            }
        }
    }
}