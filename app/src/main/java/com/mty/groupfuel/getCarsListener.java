package com.mty.groupfuel;

import com.mty.groupfuel.datamodel.Car;

import java.util.List;

public interface getCarsListener {
    List<Car> getCars();
    void setCars(List<Car> cars);
}
