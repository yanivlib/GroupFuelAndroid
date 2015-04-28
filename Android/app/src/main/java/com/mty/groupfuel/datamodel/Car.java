package com.mty.groupfuel.datamodel;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

@ParseClassName("Car")
public class Car extends ParseObject {

    public ParseUser getOwner() {
        return getParseUser("User");
    }

    public void setOwner(ParseUser value) {
        put("User", value);
    }

    public CarModel getModel() {
        return (CarModel) getParseObject("Model");
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

    public static ParseQuery<Car> getQuery() {
        return ParseQuery.getQuery(Car.class);
    }
}
