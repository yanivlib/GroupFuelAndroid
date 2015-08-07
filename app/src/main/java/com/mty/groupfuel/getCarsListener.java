package com.mty.groupfuel;

import com.mty.groupfuel.datamodel.Car;
import com.parse.ParseGeoPoint;

import java.util.List;

public interface getCarsListener {
    List<Car> getCars();

    void setCars(List<Car> cars);

    List<String> getCities();

    ParseGeoPoint getLocation();
}
