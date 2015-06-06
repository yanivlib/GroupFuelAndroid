package com.mty.groupfuel;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.mty.groupfuel.datamodel.Car;
import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UsageFragment extends android.support.v4.app.Fragment {

    private final static String STARTING_MILEAGE = "startingMileage";
    private final static String TOTAL_PRICE = "totalPrice";
    private final static String TOTAL_AMOUNT = "totalAmount";
    private final static String NUM_OF_EVENTS = "numOfEvents";
    private final static String CURRENT_MILEAGE = "currentMileage";

    private TableLayout tl;
    private Context context;

    private Map<String,Map<String, Number>> datamap;
    private List<Car> cars;

    public void updateCars(List<Car> cars) {
        this.cars = cars;
        getUsage(cars);
    }

    private OnFragmentInteractionListener mListener;

    public static UsageFragment newInstance() {
        UsageFragment fragment = new UsageFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public UsageFragment() {
        // Required empty public constructor
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
        tl = (TableLayout) view.findViewById(R.id.displayTable);
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

//    @Override
    //   public void onAttach(Activity activity) {
//        super.onAttach(activity);
//        try {
//            mListener = (OnFragmentInteractionListener) activity;
//        } catch (ClassCastException e) {
//            throw new ClassCastException(activity.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
//    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
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

    public void getUsage(final List<Car> cars) {
        System.out.println("cars length is " + cars.size());
        //ArrayList<Car> carPointers = new ArrayList<>();
        JSONArray carPointers = new JSONArray();
        for (Car car : cars) {
            carPointers.put(getPointer("Car", car.getObjectId()));;
        }
        //final Map<String, ArrayList<Car>> params = new HashMap<>();
        final Map<String, Object> params = new HashMap<>();
        params.put("cars", carPointers);
        ParseCloud.callFunctionInBackground("getUsage", params, new FunctionCallback<Map<String, Map<String, Number>>>() {
            @Override
            public void done(Map<String, Map<String, Number>> result, ParseException e) {
                if (e == null) {
                    datamap = result;
                    populateTable(context, result, cars, tl);
                } else {
                    System.out.println(e.getMessage());
                }
            }
        });
    }

    private static TextView getTextView(Context context, String text) {
        TextView result = new TextView(context);
        result.setText(text);
        return result;
    }

    private static TableRow getRow(Context context, List<String> list) {
        TableRow row = new TableRow(context);
        row.setLayoutParams(new TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT));
        for (int i = 0; i < list.size(); i++) {
            row.addView(getTextView(context,list.get(i)), i);
        }
        return row;
    }

    private static void populateTable(Context context, Map<String,Map<String, Number>> datamap, List<Car> cars, TableLayout tl) {
        for (Car car : cars) {
            Map<String, Number> map = datamap.get(car.getObjectId());
            ArrayList<String> list = new ArrayList<>(
                    Arrays.asList(
                            car.getDisplayName(),
                            map.get(TOTAL_PRICE).toString(),
                            map.get(TOTAL_AMOUNT).toString(),
                            map.get(STARTING_MILEAGE).toString(),
                            map.get(CURRENT_MILEAGE).toString()
                    )
            );
            tl.addView(getRow(context, list));
        }
        Map<String, Number> map = datamap.get("total");
        ArrayList<String> list = new ArrayList<>(Arrays.asList("Total",
                map.get(TOTAL_PRICE).toString(),
                map.get(TOTAL_AMOUNT).toString(),
                map.get(STARTING_MILEAGE).toString(),
                map.get(CURRENT_MILEAGE).toString()));
        tl.addView(getRow(context, list));
    }
}