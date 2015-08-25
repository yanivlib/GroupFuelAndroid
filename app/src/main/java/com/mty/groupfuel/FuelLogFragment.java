package com.mty.groupfuel;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.mty.groupfuel.datamodel.Car;
import com.mty.groupfuel.datamodel.Fueling;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;


public class FuelLogFragment extends SwipeRefreshListFragment implements SwipeRefreshLayout.OnRefreshListener{
    private static final String LOG_TAG = FuelLogFragment.class.getSimpleName();
    private static final String FUELING_LIST = "fueling_list";
    private static List<Fueling> fuelingList;

    getCarsListener mCallback;
    private ListView listView;
    private List<Car> cars;
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int carAmount = intent.getIntExtra(Consts.BROADCAST_CARS, 0);
            if (carAmount > 0) {
                setCars(mCallback.getOwnedCars());
                getFuelings();
            }
            Log.d(LOG_TAG, "Got message: " + carAmount);
        }
    };

    public FuelLogFragment() {
        // Required empty public constructor
    }

    public static FuelLogFragment newInstance() {
        FuelLogFragment fragment = new FuelLogFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public void setCars(List<Car> cars) {
        this.cars = cars;
    }

    public void setFuelingList(List<Fueling> fuelingList) {
        FuelLogFragment.fuelingList = fuelingList;
        ((FuelingAdapter) getListAdapter()).notifyDataSetChanged();
    }

    private void updateView() {
        getActivity().getSupportFragmentManager().beginTransaction().detach(this).attach(this).commit();

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(FUELING_LIST, (ArrayList) fuelingList);
    }


    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            ArrayList<Fueling> fuelingList = savedInstanceState.getParcelableArrayList(FUELING_LIST);
            setFuelingList(fuelingList);
        }
    }

    private void getFuelings() {
        if (cars == null) {
            setCars(mCallback.getOwnedCars());
        }
        if (cars == null) {
            return;
        }
        final FuelLogFragment fragment = this;
        ParseQuery<Fueling> query = Fueling.getQuery();
        //query.whereEqualTo("User", ParseUser.getCurrentUser());
        query.whereContainedIn("Car", UsageFragment.getPointers(cars));
        query.include("Car");
        Log.i(LOG_TAG, "querying for Fueling list");
        query.findInBackground(new FindCallback<Fueling>() {
            @Override
            public void done(List<Fueling> list, ParseException e) {
                setRefreshing(false);
                if (e == null) {
                    Log.i(LOG_TAG, "query completed successfully");
                    setFuelingList(list);
                    updateView();
                } else {
                    Log.e(LOG_TAG, "query failed", e);
                    throw new RuntimeException(e.getMessage());
                }
            }
        });
    }

    // Lifecycle methods

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallback = (getCarsListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement getCarsListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fuelingList = new ArrayList<>();
        cars = new ArrayList<>();
        setCars(mCallback.getOwnedCars());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        setListAdapter(new FuelingAdapter(view.getContext(), fuelingList));
        if (savedInstanceState != null) {
            ArrayList<Fueling> fuelingList = savedInstanceState.getParcelableArrayList(FUELING_LIST);
            setFuelingList(fuelingList);
        }
        listView = (ListView) view.findViewById(android.R.id.list);
        for (int i = 0; i < container.getChildCount(); i++) {
            View v = container.getChildAt(i);
            if (v.getTag() != null && v.getTag().equals("NO_CARS")) {
                container.removeViewAt(i);
            }
        }
        setOnRefreshListener(this);
        ViewGroup viewGroup = (ViewGroup) listView.getParent();

        if (cars == null || cars.isEmpty()) {
            listView.setVisibility(View.INVISIBLE);
            NoCarsView noCarsView = new NoCarsView(getActivity());
            noCarsView.setTag("NO_CARS");
            viewGroup.addView(noCarsView);
        } else if (fuelingList.isEmpty()) {
            NoCarsView noCarsView = new NoCarsView(getActivity());
            noCarsView.setTag("NO_CARS");
            noCarsView.setButtonText("Add fueling");
            noCarsView.setButtonListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.content_frame, FuelingFragment.newInstance(), FuelingFragment.class.getSimpleName());
                    transaction.addToBackStack(null);
                    transaction.commit();
                }
            });
            //noCarsView.setVisibility(View.INVISIBLE);
            noCarsView.setText("No fuelings available. Add one now!");
            viewGroup.addView(noCarsView);
        }
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


    // Implemented methods

    @Override
    public void onRefresh() {
        Log.d(LOG_TAG, "start refreshing");
        //getFuelings();
        Log.d(LOG_TAG, "end refreshing");
    }

    // Subclasses

    public class FuelingAdapter extends ArrayAdapter<Fueling> {
        public FuelingAdapter(Context c, List<Fueling> items) {
            super(c, 0, items);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            FuelLogItem fuelLogItem = (FuelLogItem) convertView;
            if (null == fuelLogItem) {
                fuelLogItem = FuelLogItem.inflate(parent);
            }
            fuelLogItem.setFueling(getItem(position));
            return fuelLogItem;
        }
    }
}
