package com.mty.groupfuel;

import com.parse.ParseGeoPoint;

import java.util.List;

public interface LocationListener {
    ParseGeoPoint getLocation();

    void syncCities();

    List<String> getCities();
}
