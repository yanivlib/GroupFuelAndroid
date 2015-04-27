package com.mty.groupfuel.datamodel;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;

@ParseClassName("CarModel")
public class CarModel extends ParseObject {

    public String getMake() {
        return getString("Make");
    }

    public void setMake(String value) {
        put("Make", value);
    }

    public String getModel() {
        return getString("Model");
    }

    public void setModel(String value) {
        put("Model", value);
    }

    public Number getVolume() {
        return getNumber("Volume");
    }

    public void setVolume(Number value) {
        put("Volume", value);
    }

    public Gear getGear() {
        return Gear.fromString(getString("Gear"));
    }

    public void setGear(Gear value) {
        put("Gear", value.toString());
    }

    public Number getYear() {
        return getNumber("Year");
    }

    public void setYear(Number value) {
        put("Year", value);
    }

    public Fuel getFuelType() {
        return Fuel.fromString(getString("FuelType"));
    }

    public void setFuel(Fuel value) {
        put("FuelType", value.toString());
    }

    public boolean getHybrid() {
        return getBoolean("Hybrid");
    }

    public void setHybrid(boolean value) {
        put("Hybrid", value);
    }

    public static ParseQuery<CarModel> getQuery() {
        return ParseQuery.getQuery(CarModel.class);
    }

}
