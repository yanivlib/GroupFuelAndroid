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

    //private GasStation gasStation;

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

    public void setPrice(Number price) {
        if (price == null) {
            this.price.setText("No price provided");
        } else {
            this.price.setText(price.toString() + " NIS");
        }
    }

    public void setLocation(ParseGeoPoint currentLocation, ParseGeoPoint gasStationLocation) {
        if (currentLocation == null || gasStationLocation == null) {
            this.distance.setText("a lot");
        } else {
            double distance = gasStationLocation.distanceInKilometersTo(currentLocation);
            String distnaceString;
            if (distance < 1) {
                distnaceString = "Less then 1";
            } else {
                distnaceString = String.valueOf(distance).substring(0, 4);
            }
            this.distance.setText(distnaceString + " km");
        }
    }

    void setupChildren() {
        this.title = (TextView) findViewById(R.id.title);
        this.distance = (TextView) findViewById(R.id.distance);
        this.price = (TextView) findViewById(R.id.price);
    }

    public void setData(GasStation gasStation, ParseGeoPoint currentLocation) {
        this.title.setText(gasStation.getDisplayName());
        setPrice(gasStation.getPrice());
        setLocation(currentLocation, gasStation.getLocation());
    }
}
