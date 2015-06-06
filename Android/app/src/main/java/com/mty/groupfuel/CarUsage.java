package com.mty.groupfuel;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class CarUsage  extends RelativeLayout {
    private TextView header;
    private TextView mileage;
    private TextView mpg;
    private TextView dpg;

    public void setHeader(String name) {
        this.header.setText(name);
    }

    public void setMileage(String mileage) {
        this.mileage.setText(mileage);
    }

    public void setMpg(String mpg) {
        this.mpg.setText(mpg);
    }

    public void setDpg(String dpg) {
        this.dpg.setText(dpg);
    }


    public CarUsage(Context context) {
        super(context);
        init();
    }

    public CarUsage(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CarUsage(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.usage_car, this);
        this.header = (TextView)findViewById(R.id.usage_car_title);
        this.mileage = (TextView)findViewById(R.id.usage_mileage);
        this.mpg = (TextView)findViewById(R.id.usage_mpg);
        this.dpg = (TextView)findViewById(R.id.usage_dpg);
    }
}