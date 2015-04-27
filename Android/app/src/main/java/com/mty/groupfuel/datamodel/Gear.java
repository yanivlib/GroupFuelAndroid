package com.mty.groupfuel.datamodel;

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