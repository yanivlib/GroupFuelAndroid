package com.mty.groupfuel.datamodel;

import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;

@ParseClassName("Car")
public class Car extends ParseObject {

    private final static String OWNER = "Owner";
    private final static String MODEL = "Model";
    private final static String NUMBER = "CarNumber";
    private final static String MILEAGE = "Mileage";

    private ArrayList<User> drivers;

    public Car () {

    }

    public static ParseQuery<Car> getQuery() {
        return ParseQuery.getQuery(Car.class);
    }

    public ArrayList<User> getDrivers() {
        return drivers;
    }

    public void setDrivers(ArrayList<User> drivers) {
        this.drivers = drivers;
    }

    public User getOwner() {
        User user;
        try {
            user = (User) getParseUser(OWNER).fetchIfNeeded();
        } catch (ParseException e) {
            throw new RuntimeException(e.getMessage());
        }
        return user;
    }

    public void setOwner(User value) {
        put(OWNER, value);
    }

    public CarModel getModel() {
        CarModel model;
        try {
            model = getParseObject(MODEL).fetchIfNeeded();
        } catch (ParseException e) {
            throw new RuntimeException(e.getMessage());
        }
        return model;
    }

    public void setModel(CarModel value) {
        put(MODEL, value);
    }

    public String getCarNumber() {
        return getString(NUMBER);
    }

    public void setCarNumber(String value) {
        put(NUMBER, value);
    }

    public Number getMileage() {
        return getNumber(MILEAGE);
    }

    public void setMileage(Number value) {
        put(MILEAGE, value);
    }

    public String getDisplayName() {
        try {
            return getCarNumber() + " (" + getModel().getMake() + " " + getModel().getModel() + ")";
        } catch (NullPointerException npe) {
            return "null object";
        }

    }

}
