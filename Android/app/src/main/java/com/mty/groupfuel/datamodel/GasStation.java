package com.mty.groupfuel.datamodel;

import com.parse.ParseClassName;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;

@ParseClassName("GasStation")
public class GasStation extends ParseObject {

    public String getAddress() {
        return getString("Address");

    }

    public void setAddress(String value) {
        put("Address", value);
    }

    public String getBrand() {
        return getString("Brand");

    }

    public void setBrand(String value) {
        put("Brand", value);
    }

    public ParseGeoPoint getLocation() {
        return getParseGeoPoint("Location");
    }

    public void setLocation(ParseGeoPoint value) {
        put("Location", value);
    }

    public Number getStationId() {
        return getNumber("StationId");

    }

    public void setStationId(Number value) {
        put("StationId", value);
    }

    public static ParseQuery<GasStation> getQuery() {
        return ParseQuery.getQuery(GasStation.class);
    }
}