package com.mty.groupfuel.datamodel;

import com.parse.ParseClassName;
import com.parse.ParseException;
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
        CarModel model;
        try {
            model = getParseObject("Model").fetch();
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

    public static ParseQuery<Car> getQuery() {
        return ParseQuery.getQuery(Car.class);
    }

    public String getDisplayName() {
        return getCarNumber() + "(" + getModel().getMake() + ")";
    }
}
