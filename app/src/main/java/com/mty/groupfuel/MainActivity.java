package com.mty.groupfuel;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;

import com.mty.groupfuel.datamodel.Car;
import com.mty.groupfuel.datamodel.User;
import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class MainActivity extends FragmentActivity {

    static private ParseUser user;
    private List<Car> cars;
    private FragmentPagerAdapter fragmentPagerAdapter;
    private ViewPager viewPager;
    private SlidingTabLayout slidingTabLayout;

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
        System.out.println("calling broadcastCarList, while cars length is " + this.cars.size());
        UsageFragment usageFragment = (UsageFragment) fragmentPagerAdapter.getRegisteredFragment(0);
        if (usageFragment != null) {
            usageFragment.updateCars(cars);
        }
        FuelingFragment fuelingFragment = (FuelingFragment) fragmentPagerAdapter.getRegisteredFragment(1);
        if (fuelingFragment != null) {
            fuelingFragment.updateCars(cars);
        }
        SettingsFragment settingsFragment = (SettingsFragment) fragmentPagerAdapter.getRegisteredFragment(2);
        if (settingsFragment != null) {
            settingsFragment.setCars(cars);
        }
    }

    public void setfragmentPagerAdapter(FragmentPagerAdapter adapter) {
        this.fragmentPagerAdapter = adapter;
    }

    private void findViewsByid() {
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        slidingTabLayout = (SlidingTabLayout) findViewById(R.id.sliding_tabs);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewsByid();

        FragmentPagerAdapter adapter = new FragmentPagerAdapter(getSupportFragmentManager(), MainActivity.this);
        viewPager.setAdapter(adapter);
        setfragmentPagerAdapter(adapter);
        slidingTabLayout.setDistributeEvenly(true);
        slidingTabLayout.setViewPager(viewPager);

        getOwnedCars();
        user = ParseUser.getCurrentUser();

        String action = getIntent().getAction();
        if (action != null) {
            if (action.equals(Consts.OPEN_TAB_SETTINGS)) {
                viewPager.setCurrentItem(2);
            } else if (action.equals(Consts.OPEN_TAB_USAGE)) {
                viewPager.setCurrentItem(0);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
    }

    private void getOwnedCars() {
        ParseCloud.callFunctionInBackground("getOwnedCars", new HashMap<String, Object>(), new FunctionCallback<ArrayList>() {
            @Override
            public void done(ArrayList result, ParseException e) {
                if (e == null) {
                    if (result.size() == 0) {
                        new AlertDialog.Builder(MainActivity.this)
                                .setTitle("No cars found")
                                .setMessage("You need at least one car to access this function. Would you want to add one now?")
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        startActivity(new Intent(MainActivity.this, AddCarActivity.class));
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
}