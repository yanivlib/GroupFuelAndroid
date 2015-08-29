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

import java.util.ArrayList;
import java.util.List;


public class FuelLogFragment extends SwipeRefreshListFragment implements SwipeRefreshLayout.OnRefreshListener{
    private static final String LOG_TAG = FuelLogFragment.class.getSimpleName();
    private static final String FUELING_LIST = "fueling_list";
    CarsListener carsListener;
    FuelingsListener fuelingsListener;
    private List<Fueling> fuelingList;
    private ListView listView;
    private List<Car> cars;
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            final int message = intent.getIntExtra(action, 0);
            switch (action) {
                case Consts.BROADCAST_CARS:
                    if (message > 0) {
                        setCars(carsListener.getOwnedCars());
                    }
                    break;
                case Consts.BROADCAST_FUELINGS:
                    if (message > 0) {
                        setFuelingList(fuelingsListener.getFuelings());
                        updateView();
                    }
                    break;
            }
            Log.d(LOG_TAG, "Got message: " + intent.toString());
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
        if (fuelingList == null) {
            return;
        }
        this.fuelingList = fuelingList;
        try {
            ((FuelingAdapter) getListAdapter()).notifyDataSetChanged();
        } catch (NullPointerException e) {
            Log.e(LOG_TAG, e.toString(), e);
        }
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

    // Lifecycle methods

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            carsListener = (CarsListener) activity;
            fuelingsListener = (FuelingsListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement CarsListener, FuelingsListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fuelingList = new ArrayList<>();
        cars = new ArrayList<>();
        setCars(carsListener.getOwnedCars());
        setFuelingList(fuelingsListener.getFuelings());
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
        ViewGroup viewGroup = (ViewGroup) listView.getParent();
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            View v = viewGroup.getChildAt(i);
            if (v.getTag() != null && v.getTag().equals("NO_CARS")) {
                viewGroup.removeViewAt(i);
            }
        }
        setOnRefreshListener(this);

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
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Consts.BROADCAST_CARS);
        intentFilter.addAction(Consts.BROADCAST_FUELINGS);
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mMessageReceiver, intentFilter);

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
