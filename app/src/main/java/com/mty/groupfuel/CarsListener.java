package com.mty.groupfuel;

import com.mty.groupfuel.datamodel.Car;

import java.util.List;

public interface CarsListener {
    List<Car> getOwnedCars();

    void setOwnedCars(List<Car> cars);

    void syncOwnedCars();

    List<Car> getCars();

    void removeCar(Car car);

    void updateMileage(Car car, Number Mileage);
}
