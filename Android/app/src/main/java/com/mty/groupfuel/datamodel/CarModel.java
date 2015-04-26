package com.mty.groupfuel.datamodel;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;

@ParseClassName("CarModel")
public class CarModel extends ParseObject {
    public enum Gear {
        AUTO("Auto"), MANUAL("Manual"), ROBOTIC("Robotic");
        private String string;
        Gear(String name){string = name;}

        public String toString() {
            return string;
        }

        public static Gear fromString(String text) {
            if (text != null) {
                for (Gear gear : Gear.values()) {
                    if (text.equalsIgnoreCase(gear.string)) {
                        return gear;
                    }
                }
            }
            throw new IllegalArgumentException("No Gear of type " + text + " found");
        }
    }

    public enum Fuel {
        GASOLINE("Gasoline"), DIESEL("Diesel");
        private String string;
        Fuel(String name){string = name;}
        public String toString() {
            return string;
        }

        public static Fuel fromString(String text) {
            if (text != null) {
                for (Fuel fuel : Fuel.values()) {
                    if (text.equalsIgnoreCase(fuel.string)) {
                        return fuel;
                    }
                }
            }
            throw new IllegalArgumentException("No Gear of type " + text + " found");
        }
    }

    public String getMake() {
        return getString("Make");
    }

    public void setMake(String value) {
        put("Make", value);
    }

    public String getModel() {
        return getString("Model");
    }

    public void setModel(String value) {
        put("Model", value);
    }

    public Number getVolume() {
        return getNumber("Volume");
    }

    public void setVolume(Number value) {
        put("Volume", value);
    }

    public Gear getGear() {
        return Gear.fromString(getString("Gear"));
    }

    public void setGear(Gear value) {
        put("Gear", value.toString());
    }

    public Number getYear() {
        return getNumber("Year");
    }

    public void setYear(Number value) {
        put("Year", value);
    }

    public Fuel getFuelType() {
        return Fuel.fromString(getString("FuelType"));
    }

    public void setFuel(Fuel value) {
        put("FuelType", value.toString());
    }

    public boolean getHybrid() {
        return getBoolean("Hybrid");
    }

    public void setHybrid(boolean value) {
        put("Hybrid", value);
    }

    public static ParseQuery<CarModel> getQuery() {
        return ParseQuery.getQuery(CarModel.class);
    }

}
