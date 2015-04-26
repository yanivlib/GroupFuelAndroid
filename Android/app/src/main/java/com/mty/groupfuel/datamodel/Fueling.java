package com.mty.groupfuel.datamodel;

import com.parse.ParseClassName;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

@ParseClassName("Fueling")
public class Fueling extends ParseObject {

    public Car getCar() {
        return (Car) getParseObject("Car");
    }

    public void setCar(Car value) {
        put("Car", value);
    }

    public ParseUser getUser() {
        return getParseUser("User");
    }

    public void setUser(ParseUser value) {
        put("User", value);
    }

    public GasStation getGasStation() {
        return (GasStation) getParseObject("GasStation");
    }

    public void setGasStation(GasStation value) {
        put("GasStation", value);
    }

    public Number getMileage() {
        return getNumber("Mileage");

    }

    public void setMileage(Number value) {
        put("Mileage", value);
    }

    public Number getAmount() {
        return getNumber("Amount");

    }

    public void setAmount(Number value) {
        put("Amount", value);
    }

    public Number getPrice() {
        return getNumber("Price");

    }

    public void setPrice(Number value) {
        put("Price", value);
    }

    public CarModel.Fuel getFuelType() {
        return CarModel.Fuel.fromString(getString("Type"));
    }

    public ParseGeoPoint getLocation() {
        return getParseGeoPoint("Location");
    }

    public void setLocation(ParseGeoPoint value) {
        put("Location", value);
    }

    public static ParseQuery<Fueling> getQuery() {
        return ParseQuery.getQuery(Fueling.class);
    }
}
