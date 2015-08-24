package com.mty.groupfuel;

import com.mty.groupfuel.datamodel.Car;
import com.mty.groupfuel.datamodel.GasStation;
import com.parse.ParseGeoPoint;

import java.util.List;

public interface getCarsListener {
    List<Car> getOwnedCars();

    void setOwnedCars(List<Car> cars);

    //List<String> getCities();

    ParseGeoPoint getLocation();

    void syncOwnedCars();

    List<GasStation> getStations();

    List<Car> getCars();
}
