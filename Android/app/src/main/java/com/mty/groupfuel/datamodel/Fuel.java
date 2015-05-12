package com.mty.groupfuel.datamodel;

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