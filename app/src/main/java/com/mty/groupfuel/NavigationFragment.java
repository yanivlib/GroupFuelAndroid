package com.mty.groupfuel;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.mty.groupfuel.datamodel.GasStation;
import com.parse.ParseGeoPoint;

import java.util.ArrayList;
import java.util.Iterator;
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
        getActivity()
                .getSupportFragmentManager()
                .beginTransaction()
                .detach(this)
                .attach(this)
                .commit();
    }

    public void setLocation(ParseGeoPoint location) {
        this.location = location;
    }

    public void setStations(List<GasStation> stations) {
        if (stations == null) {
            return;
        }
        Iterator<GasStation> it = stations.iterator();
        while (it.hasNext()) {
            GasStation station = it.next();
            if (station.getLocation() == null) {
                stations.remove(station);
            }
        }
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
        setLocation(mCallback.getLocation());

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Consts.BROADCAST_LOCATION);
        filter.addAction(Consts.BROADCAST_STATIONS);
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mMessageReceiver, filter);
        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //GasStation station = (GasStation)parent.getItemAtPosition(position);
                GasStation station = (GasStation) getListView().getItemAtPosition(position);
                //GasStationItem item = (GasStationItem) v;
                ParseGeoPoint location = station.getLocation();
                final String uri = String.format("geo:%f,%f", location.getLatitude(),
                        location.getLongitude());
                final Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(uri));
                new AlertDialog.Builder(getActivity())
                        .setTitle("Navigate to destination")
                        .setMessage("Are you sure you want to navigate to " + station.getDisplayName() + "?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                startActivity(intent);
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_map)
                        .show();
            }
        });
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
            //Log.d(LOG_TAG, "generating view for position " + position);
            GasStationItem gasStationItem = (GasStationItem) convertView;
            if (null == gasStationItem) {
                gasStationItem = GasStationItem.inflate(parent);
            }
            gasStationItem.setData(getItem(position), location);
            return gasStationItem;
        }

        @Override
        public void notifyDataSetChanged() {
            super.notifyDataSetChanged();
            updateView();
        }
    }

}