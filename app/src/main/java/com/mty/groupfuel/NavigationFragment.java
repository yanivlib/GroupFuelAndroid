package com.mty.groupfuel;


import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.mty.groupfuel.datamodel.GasStation;
import com.parse.ParseGeoPoint;

import java.util.ArrayList;
import java.util.List;


public class NavigationFragment extends ListFragment {

    private static final String LOG_TAG = NavigationFragment.class.getSimpleName();
    getCarsListener mCallback;
    private List<GasStation> stations;
    private ParseGeoPoint location = new ParseGeoPoint();
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            int message = intent.getIntExtra(action, 0);
            switch (action) {
                case Consts.BROADCAST_LOCATION:
                    if (message > 0) {
                        setLocation(mCallback.getLocation());
                    }
                    break;
                case Consts.BROADCAST_STATIONS:
                    if (message > 0) {
                        setStations(mCallback.getStations());
                    }
            }
            Log.d(LOG_TAG, "Got message: " + action + " " + message);
        }
    };

    public NavigationFragment() {
        // Required empty public constructor
    }

    public static NavigationFragment newInstance() {
        NavigationFragment fragment = new NavigationFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    private void updateView() {
        getActivity().getSupportFragmentManager().beginTransaction().detach(this).attach(this).commit();
    }

    public void setLocation(ParseGeoPoint location) {
        this.location = location;
    }

    public void setStations(List<GasStation> stations) {
        Log.d(LOG_TAG, "setting stations, there are " + (stations == null ? -1 : stations.size()));
        this.stations = stations;
        try {
            ((StationAdapter) getListAdapter()).notifyDataSetChanged();
        } catch (NullPointerException npe) {
            Log.d(LOG_TAG, "recovered from NPE when trying to get the list adapter");
        }
        //updateView();
    }

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
        stations = new ArrayList<>();
        ArrayList<GasStation> currentStations = (ArrayList<GasStation>) mCallback.getStations();
        if (currentStations != null) {
            setStations(currentStations);
        } else {
            setStations(new ArrayList<GasStation>());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_navigation, container, false);

        setListAdapter(new StationAdapter(container.getContext(), stations));

        if (stations.isEmpty()) {
            setStations(mCallback.getStations());
        }
        if (location == null) {
            setLocation(mCallback.getLocation());
        }

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Consts.BROADCAST_LOCATION);
        filter.addAction(Consts.BROADCAST_STATIONS);
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mMessageReceiver, filter);
    }

    @Override
    public void onPause() {
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mMessageReceiver);
        super.onPause();
    }


    public class StationAdapter extends ArrayAdapter<GasStation> {
        public StationAdapter(Context c, List<GasStation> items) {
            super(c, 0, items);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Log.d(LOG_TAG, "generating view for position " + position);
            GasStationItem gasStationItem = (GasStationItem) convertView;
            if (null == gasStationItem) {
                gasStationItem = GasStationItem.inflate(parent);
            }
            //GasStation gasStation = getItem(position);
            gasStationItem.setData(getItem(position), location);
            return gasStationItem;
            //return super.getView(position, convertView, parent);
        }
    }

}
