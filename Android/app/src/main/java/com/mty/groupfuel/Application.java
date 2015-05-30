package com.mty.groupfuel;

import android.content.Context;
import android.content.SharedPreferences;

import com.mty.groupfuel.datamodel.Car;
import com.mty.groupfuel.datamodel.CarModel;
import com.mty.groupfuel.datamodel.Fueling;
import com.mty.groupfuel.datamodel.GasStation;
import com.mty.groupfuel.datamodel.User;
import com.parse.Parse;
import com.parse.ParseObject;

public class Application extends android.app.Application {

    public static final boolean APPDEBUG = false;
    private static SharedPreferences preferences;

    public Application() {
    }

    @Override
    public void onCreate() {
        super.onCreate();

        ParseObject.registerSubclass(CarModel.class);
        ParseObject.registerSubclass(Car.class);
        ParseObject.registerSubclass(GasStation.class);
        ParseObject.registerSubclass(Fueling.class);
        ParseObject.registerSubclass(User.class);
        Parse.enableLocalDatastore(this);
        Parse.initialize(this, Consts.applicationId, Consts.clientKey);
        preferences = getSharedPreferences("com.mty.groupfuel", Context.MODE_PRIVATE);
    }
}