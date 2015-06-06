package com.mty.groupfuel.datamodel;

import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

@ParseClassName("Car")
public class Car extends ParseObject {

    public Car () {

    }

    public static ParseQuery<Car> getQuery() {
        return ParseQuery.getQuery(Car.class);
    }

    public User getOwner() {
        return (User)getParseUser("Owner");
    }

    public void setOwner(User value) {
        put("Owner", value);
    }

    public CarModel getModel() {
        CarModel model;
        try {
            model = getParseObject("Model").fetchIfNeeded();
        } catch (ParseException e) {
            throw new RuntimeException(e.getMessage());
        }
        return model;
    }

    public void setModel(CarModel value) {
        put("Model", value);
    }

    public String getCarNumber() {
        return getString("CarNumber");
    }

    public void setCarNumber(String value) {
        put("CarNumber", value);
    }

    public Number getMileage() {
        return getNumber("Mileage");
    }

    public void setMileage(Number value) {
        put("Mileage", value);
    }

    public String getDisplayName() {
        try {
            return getCarNumber() + " (" + getModel().getMake() + " " + getModel().getModel() + ")";
        } catch (NullPointerException npe) {
            return "null object";
        }

    }

    public List<Fueling> getFuelingEvents() {
        JSONArray array = getJSONArray("FuelEvent");
        List<Fueling> list = new ArrayList<>();
        for (int i = 0; i < array.length(); i++) {
            Fueling fueling;
            try {
                fueling = (Fueling) array.get(i);
                list.add((Fueling) fueling.fetchIfNeeded());
            } catch (JSONException | ParseException e) {
                throw new RuntimeException(e.getMessage());
            }
        }
        return list;
    }
}
