package com.mty.groupfuel;

import com.mty.groupfuel.datamodel.Car;
import com.mty.groupfuel.datamodel.GasStation;
import com.parse.ParseGeoPoint;

import java.util.List;

public interface getCarsListener {
    List<Car> getCars();

    void setCars(List<Car> cars);

    List<String> getCities();

    ParseGeoPoint getLocation();

    void getOwnedCars();

    List<GasStation> getStations();
}
