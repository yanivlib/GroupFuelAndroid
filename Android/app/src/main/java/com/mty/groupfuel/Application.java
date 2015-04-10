package com.mty.groupfuel;

/**
 * Created by yanivlib on 4/10/15.
 */
import android.content.Context;
import android.content.SharedPreferences;

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

        Parse.initialize(this, "LkuUmj7OE1C9BzsbhkpMZEgeAT1A0ZACqTUZgN2f", "E6rm9orzoHeg4O36SSm7kToum9I4nb9lUwhlyFjY");

        preferences = getSharedPreferences("com.mty.groupfuel", Context.MODE_PRIVATE);
    }
}