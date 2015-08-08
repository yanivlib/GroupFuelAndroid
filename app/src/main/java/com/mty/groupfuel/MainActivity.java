package com.mty.groupfuel;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Criteria;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.mty.groupfuel.datamodel.Car;
import com.mty.groupfuel.datamodel.User;
import com.parse.FunctionCallback;
import com.parse.LocationCallback;
import com.parse.LogOutCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements getCarsListener {

    private static final String CURRENT_FRAGMENT = "current_fragment";
    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    static FloatingActionButton fab;
    static private ParseUser user;
    private static ProgressDialog progress;
    private List<Car> cars;
    private Toolbar toolbar;
    private Fragment mContent;
    private ParseGeoPoint location;

    public static AlertDialog.Builder createErrorAlert(String message, String title, Context context) {
        return new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert);
    }

    public static AlertDialog.Builder createErrorAlert(List<String> list, String title, Context context) {
        return createErrorAlert(catString(list), title, context);
    }

    public static AlertDialog.Builder createErrorAlert(List<String> list, Context context) {
        return createErrorAlert(catString(list), context);
    }

    public static AlertDialog.Builder createErrorAlert(String message, Context context) {
        return new AlertDialog.Builder(context)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert);
    }

    private static String catString(List<String> list) {
        String result = "";
        for (String string : list) {
            result += string;
            result += "\n";
        }
        return result;
    }


    private static void logOut(final Context context) {
        new AlertDialog.Builder(context)
                .setMessage("Are you sure you want to log out?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        progress = ProgressDialog.show(context, "Logging you out", "Please Wait...");
                        ParseUser.logOutInBackground(new LogOutCallback() {
                            @Override
                            public void done(ParseException e) {
                                progress.dismiss();
                                context.startActivity(new Intent(context, DispatchActivity.class));
                            }
                        });
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    public User getUser() {
        return (User) user;
    }

    public List<Car> getCars() {
        return this.cars;
    }

    public void setCars(List<Car> cars) {
        this.cars = cars;
    }

    public void broadcastCarList() {
        Log.d(LOG_TAG, "Broadcasting message: " + cars.toString());
        Intent intent = new Intent(Consts.BROADCAST_CARS);
        intent.putExtra("cars", cars.size());
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    public void broadcastLocation(boolean b) {
        Log.d(LOG_TAG, "Broadcasting message: " + (location != null));
        Intent intent = new Intent(Consts.BROADCAST_LOCATION);
        intent.putExtra("location", b);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    @Override
    public List<String> getCities() {
        return new ArrayList<>(Arrays.asList("הרצליה", "טבריה", "באר שבע"));
    }

    private void findViewsByid() {
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        fab = (FloatingActionButton) findViewById(R.id.fab);
    }

    @Override
    protected void onStart() {
        super.onStart();

        Fragment f = getSupportFragmentManager().findFragmentById(R.id.content_frame);
        //ViewPagerContainerFragment viewPagerContainerFragment = (ViewPagerContainerFragment) getSupportFragmentManager().findFragmentById(R.id.content_frame);
        if (f == null) {
            ViewPagerContainerFragment viewPagerContainerFragment = new ViewPagerContainerFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, viewPagerContainerFragment, viewPagerContainerFragment.getClass().getSimpleName()).commit();
            getSupportFragmentManager().executePendingTransactions();
        }
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewsByid();

        setSupportActionBar(toolbar);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.content_frame, FuelingFragment.newInstance(), FuelingFragment.class.getSimpleName());
                transaction.addToBackStack(null);

                transaction.commit();
            }
        });
        getOwnedCars();
        getCurrentLocation();
        user = ParseUser.getCurrentUser();
        //if (savedInstanceState != null) {
        //    mContent = getSupportFragmentManager().getFragment(savedInstanceState, CURRENT_FRAGMENT);
        //    System.out.println("recoverd, found fragment to be" + mContent.getClass().getSimpleName());
        //}
    }
    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        Log.d(LOG_TAG, "resuming fragments...");
        // YOUR STUFF IS HERE
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_settings:
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.content_frame, SettingsFragment.newInstance(), SettingsFragment.class.getSimpleName());
                transaction.addToBackStack(null);
                transaction.commit();
                return true;
            case R.id.action_logout:
                logOut(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void getOwnedCars() {
        ParseCloud.callFunctionInBackground("getOwnedCars", new HashMap<String, Object>(), new FunctionCallback<ArrayList<Car>>() {
            @Override
            public void done(ArrayList<Car> result, ParseException e) {
                if (e == null) {
                    if (result.size() == 0) {
                        new AlertDialog.Builder(MainActivity.this)
                                .setTitle("No cars found")
                                .setMessage("You need at least one car to access this function. Would you want to add one now?")
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                                        transaction.replace(R.id.content_frame, SettingsFragment.newInstance(), SettingsFragment.class.getSimpleName());
                                        transaction.addToBackStack(null);
                                        transaction.commit();
                                    }
                                })
                                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        // do nothing
                                    }
                                })
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .show();
                    }
                    setCars(result);
                    broadcastCarList();
                } else {
                    switch (e.getCode()) {
                        case 141:
                            System.out.println(e.getMessage());
                            break;
                        default:
                            throw new RuntimeException(e.getMessage());
                    }
                }
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //    mContent = getSupportFragmentManager().findFragmentByTag(ViewPagerContainerFragment.class.getSimpleName());
        //    System.out.println("Going out, found fragment to be" + mContent.getClass().getSimpleName());
        //    getSupportFragmentManager().putFragment(outState, CURRENT_FRAGMENT, mContent);

    }

    public ParseGeoPoint getLocation() {
        return location;
    }

    public void setLocation(ParseGeoPoint location) {
        this.location = location;
    }

    private void getCurrentLocation() {
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_LOW);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setCostAllowed(true);
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        ParseGeoPoint.getCurrentLocationInBackground(20000, criteria, new LocationCallback() {
            @Override
            public void done(ParseGeoPoint parseGeoPoint, ParseException e) {
                if (e == null) {
                    setLocation(parseGeoPoint);
                    broadcastLocation(true);
                } else {
                    broadcastLocation(false);
                }
            }
        });
    }
}