package com.mty.groupfuel;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.util.Base64;
import android.util.Log;

import com.facebook.FacebookSdk;
import com.mty.groupfuel.datamodel.Car;
import com.mty.groupfuel.datamodel.CarModel;
import com.mty.groupfuel.datamodel.Fueling;
import com.mty.groupfuel.datamodel.GasStation;
import com.mty.groupfuel.datamodel.User;
import com.parse.Parse;
import com.parse.ParseFacebookUtils;
import com.parse.ParseObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Application extends android.app.Application {

    public static final boolean APPDEBUG = false;
    private static SharedPreferences preferences;

    public Application() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        try {
            getHash(getPackageManager().getPackageInfo(Consts.PACKAGE_NAME, PackageManager.GET_SIGNATURES));
        } catch (PackageManager.NameNotFoundException e) {

        }
        FacebookSdk.sdkInitialize(this);
        ParseObject.registerSubclass(CarModel.class);
        ParseObject.registerSubclass(Car.class);
        ParseObject.registerSubclass(GasStation.class);
        ParseObject.registerSubclass(Fueling.class);
        ParseObject.registerSubclass(User.class);
        Parse.enableLocalDatastore(this);
        Parse.initialize(this, Consts.applicationId, Consts.clientKey);
        ParseFacebookUtils.initialize(this);
        preferences = getSharedPreferences(Consts.PACKAGE_NAME, Context.MODE_PRIVATE);
    }

    static void getHash(PackageInfo info) {
        try {
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (NoSuchAlgorithmException e) {

        }
    }
}