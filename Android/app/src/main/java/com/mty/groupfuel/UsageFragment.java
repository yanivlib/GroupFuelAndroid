package com.mty.groupfuel;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;

import com.mty.groupfuel.datamodel.Car;
import com.mty.groupfuel.datamodel.Fueling;
import com.parse.FindCallback;
import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UsageFragment extends android.support.v4.app.Fragment {

    private final static String STARTING_MILEAGE = "startingMileage";
    private final static String TOTAL_PRICE = "totalPrice";
    private final static String TOTAL_AMOUNT = "totalAmount";
    private final static String NUM_OF_EVENTS = "numOfEvents";
    private final static String CURRENT_MILEAGE = "currentMileage";

    private TableLayout displayTable;
    private TableLayout logTable;
    private Context context;

    private Map<String,Map<String, Number>> datamap;
    private List<Car> cars;

    public UsageFragment() {
        // Required empty public constructor
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

    private static List<Fueling> mergeFuelings(List<Car> cars) {
        List<Fueling> fuelings = new ArrayList<>();
        for (Car car : cars) {
            //fuelings.addAll(car.getFuelingEvents());
        }
        return fuelings;
    }

    private static CarUsage getCarView(Context context, String name, String mpg, String mileage, String dpg) {
        CarUsage usage = new CarUsage(context);
        usage.setDpg(dpg);
        usage.setHeader(name);
        usage.setMileage(mileage);
        usage.setMpg(mpg);
        return usage;
    }

    private static void populateDisplayTable(Context context, Map<String, Map<String, Number>> datamap, List<Car> cars, TableLayout tl) {
        for (Car car : cars) {
            Map<String, Number> map = datamap.get(car.getObjectId());
            String name = car.getDisplayName();
            Number price = map.get(TOTAL_PRICE);
            Number amount = map.get(TOTAL_AMOUNT);
            Number starting = map.get(STARTING_MILEAGE);
            Number mileage = map.get(CURRENT_MILEAGE);

            int miles = (mileage.intValue() - starting.intValue());
            String dpg = String.valueOf(miles / price.intValue());
            String mpg = String.valueOf(miles / amount.intValue());

            tl.addView(getCarView(context, name, mpg, mileage.toString(), dpg));
        }
    }

    public void updateCars(List<Car> cars) {
        if (!cars.equals(this.cars)) {
            this.cars = cars;
            getUsage(cars);
            //populateLogTable(context, mergeFuelings(cars), logTable);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.cars = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_usage, container, false);
        context = view.getContext();
        displayTable = (TableLayout) view.findViewById(R.id.displayTable);
        logTable = (TableLayout) view.findViewById(R.id.logTable);
        getFuelings(context);

        return view;
    }

    public void getUsage(final List<Car> cars) {
        if ((this.datamap != null) && (this.datamap.size() == cars.size() + 1)) {
            return;
        }
        if (cars.size() == 0) {
            new AlertDialog.Builder(context)
                    .setTitle(context.getString(R.string.no_cars))
                    .setMessage(context.getString(R.string.add_car_q))
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            startActivity(new Intent(context, AddCarActivity.class));
                        }
                    })
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // do nothing
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }
        List<JSONObject> carPointers = new ArrayList<>(getPointers(cars));
        final Map<String, List<JSONObject>> params = new HashMap<>();
        params.put("cars", carPointers);
        ParseCloud.callFunctionInBackground("getUsage", params, new FunctionCallback<Map<String, Map<String, Number>>>() {
            @Override
            public void done(Map<String, Map<String, Number>> result, ParseException e) {
                if (e == null) {
                    datamap = result;
                    populateDisplayTable(context, result, cars, displayTable);
                } else {
                    System.out.println(e.getMessage());
                }
            }
        });
    }

    private void populateLogTable(Context context, List<Fueling> list, TableLayout tl) {
        if (tl == null) {
            tl = this.logTable;
        }
        for (Fueling fueling : list) {
            tl.addView(new FuelingUsage(context, fueling));
        }
    }

    private void getFuelings(final Context context) {
        ParseQuery<Fueling> query = Fueling.getQuery();
        query.whereEqualTo("User", ParseUser.getCurrentUser());
        query.include("Car");
        query.findInBackground(new FindCallback<Fueling>() {
            @Override
            public void done(List<Fueling> list, ParseException e) {
                if (e == null) {
                    populateLogTable(context, list, null);
                } else {
                    throw new RuntimeException(e.getMessage());
                }
            }
        });
    }

}