package com.mty.groupfuel.datamodel;

import android.os.Parcel;
import android.os.Parcelable;

import com.parse.ParseClassName;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;

@ParseClassName("Fueling")
public class Fueling extends ParseObject implements Parcelable {
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Fueling createFromParcel(Parcel in) {
            return new Fueling(in);
        }

        public Fueling[] newArray(int size) {
            return new Fueling[size];
        }
    };

    public Fueling() {

    }

    public Fueling(Parcel in) {

    }

    public static ParseQuery<Fueling> getQuery() {
        return ParseQuery.getQuery(Fueling.class);
    }

    public Car getCar() {
        return (Car) getParseObject("Car");
    }

    public void setCar(Car value) {
        put("Car", value);
    }

    public User getUser() {
        return (User) getParseUser("User");
    }

    public void setUser(User value) {
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

    public Fuel getFuelType() {
        return Fuel.fromString(getString("Type"));
    }

    public void setFuelType(Fuel value) {
        put("FuelType", value.toString());
    }

    public ParseGeoPoint getLocation() {
        return getParseGeoPoint("Location");
    }

    public void setLocation(ParseGeoPoint value) {
        put("Location", value);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }


}