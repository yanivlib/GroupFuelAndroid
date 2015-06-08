package com.mty.groupfuel;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TableRow;
import android.widget.TextView;

import com.mty.groupfuel.datamodel.Car;
import com.mty.groupfuel.datamodel.Fueling;

import java.text.SimpleDateFormat;
import java.util.Date;


public class FuelingUsage extends TableRow {
    private TextView carName;
    private TextView date;
    private TextView amount;
    private TextView price;

    FuelingUsage(Context context) {
        super(context);
        init();
    }

    FuelingUsage(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init();
    }

    FuelingUsage(Context context, Fueling fueling) {
        super(context);
        init();
        setCarName(fueling.getCar().getDisplayName());
        setDate(fueling.getCreatedAt());
        setAmount(fueling.getAmount().toString());
        setPrice(fueling.getPrice().toString());
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

    private void init() {
        inflate(getContext(), R.layout.usage_fueling, this);
        this.carName = (TextView) findViewById(R.id.usage_log_car);
        this.date = (TextView) findViewById(R.id.usage_log_date);
        this.amount = (TextView) findViewById(R.id.usage_log_amount);
        this.price = (TextView) findViewById(R.id.usage_log_price);
    }
}
