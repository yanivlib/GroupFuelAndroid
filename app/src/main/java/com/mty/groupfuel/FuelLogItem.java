package com.mty.groupfuel;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mty.groupfuel.datamodel.Car;
import com.mty.groupfuel.datamodel.Fueling;
import com.mty.groupfuel.datamodel.GasStation;
import com.mty.groupfuel.datamodel.User;
import com.parse.ParseException;
import com.parse.ParseUser;

import java.text.SimpleDateFormat;
import java.util.Date;


public class FuelLogItem extends RelativeLayout {
    private TextView carName;
    private TextView date;
    private TextView amount;
    private TextView price;
    private TextView station;
    private TextView driver;

    public FuelLogItem(Context context) {
        this(context, null);
    }

    public FuelLogItem(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FuelLogItem(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        LayoutInflater.from(context).inflate(R.layout.fueling_log_item_children, this, true);
        setupChildren();
    }

    public static FuelLogItem inflate(ViewGroup parent) {
        FuelLogItem fuelLogItem = (FuelLogItem) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fueling_log_item, parent, false);
        return fuelLogItem;
    }

    void setFueling(Fueling fueling) {
        setCarName(fueling.getCar().getDisplayName());
        setDate(fueling.getCreatedAt());
        setAmount(fueling.getAmount().toString());
        setPrice(fueling.getPrice().toString());
        setDriver(fueling.getUser());
        GasStation gasStation = fueling.getGasStation();
        if (gasStation != null) {
            try {
                gasStation.fetchIfNeeded();
            } catch (ParseException pe) {
                gasStation = null;
            }
            setStation(fueling.getGasStation());
        }
        if (gasStation != null) {
            setStation(fueling.getGasStation());
        } else {
            setStation("Not provided");
        }
    }

    public void setDriver(User driver) {
        if (driver.equals(ParseUser.getCurrentUser())) {
            this.driver.setText(R.string.me);
        } else {
            this.driver.setText(driver.getDisplayName());
        }
    }

    public void setCarName(String name) {
        this.carName.setText(name);
    }

    public void setDate(Date date) {
        this.date.setText(new SimpleDateFormat(Consts.DATE_TIME_FORMAT).format(date));
    }

    public void setDate(String date) {
        this.date.setText(date);
    }

    public void setAmount(String amount) {
        this.amount.setText(amount);
    }

    public void setPrice(String price) {
        this.price.setText(price);
    }

    public void setCarName(Car car) {
        this.carName.setText(car.getDisplayName());
    }

    public void setStation(GasStation station) {
        this.station.setText(station.getDisplayName());
    }

    public void setStation(String station) {
        this.station.setText(station);
    }


    private void setupChildren() {
        this.carName = (TextView) findViewById(R.id.usage_log_car);
        this.date = (TextView) findViewById(R.id.usage_log_date);
        this.amount = (TextView) findViewById(R.id.usage_log_amount);
        this.price = (TextView) findViewById(R.id.usage_log_price);
        this.station = (TextView) findViewById(R.id.usage_log_station);
        this.driver = (TextView) findViewById(R.id.usage_driver);
    }
}