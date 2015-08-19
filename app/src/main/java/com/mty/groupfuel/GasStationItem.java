package com.mty.groupfuel;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mty.groupfuel.datamodel.GasStation;
import com.parse.ParseGeoPoint;

public class GasStationItem extends RelativeLayout {

    private GasStation gasStation;

    private TextView title;
    private TextView distance;
    private TextView price;

    public GasStationItem(Context context) {
        this(context, null);
    }

    public GasStationItem(Context context, AttributeSet attributes) {
        this(context, attributes, 0);
    }

    public GasStationItem(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        LayoutInflater.from(context).inflate(R.layout.gas_station_item_children, this, true);
        setupChildren();
    }

    public static GasStationItem inflate(ViewGroup parent) {
        GasStationItem gasStationItem = (GasStationItem) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.gas_station_item, parent, false);
        return gasStationItem;
    }

    //public ParseGeoPoint getLocation() {
    //    return location;
    //}

    void setupChildren() {
        this.title = (TextView) findViewById(R.id.title);
        this.distance = (TextView) findViewById(R.id.distance);
        this.price = (TextView) findViewById(R.id.price);
    }

    public void setData(GasStation gasStation, ParseGeoPoint currentLocation) {
        this.gasStation = gasStation;
        ParseGeoPoint gasStationLocation = gasStation.getLocation();
        this.title.setText(gasStation.getDisplayName());

        this.price.setText("2.5 nis");

        if (currentLocation == null || gasStationLocation == null) {
            this.distance.setText("a lot");
        } else {
            //System.out.println("current location: " + currentLocation);
            //System.out.println("gas station location: " + location);
            double distance = gasStationLocation.distanceInKilometersTo(currentLocation);
            //System.out.println("distance between them: " + distance);
            String distnaceString;
            if (distance < 1) {
                distnaceString = "Less then 1";
            } else {
                distnaceString = String.valueOf(distance).substring(0, 4);
            }
            this.distance.setText(distnaceString + " km");
        }
    }
}
