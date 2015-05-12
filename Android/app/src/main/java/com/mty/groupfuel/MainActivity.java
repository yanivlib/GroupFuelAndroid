package com.mty.groupfuel;

import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.TextView;
import android.support.v4.app.FragmentActivity;

import com.mty.groupfuel.datamodel.Car;
import com.mty.groupfuel.datamodel.User;
import com.parse.FindCallback;
import com.parse.FunctionCallback;
import com.parse.Parse;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class MainActivity extends ActionBarActivity {

    static private ParseUser user;
    static private Car[] cars;

    public static String getUserName () {
        return user.getUsername();
    }
    public Car[] getCars(){
        return cars;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager(), MainActivity.this));

        SlidingTabLayout slidingTabLayout = (SlidingTabLayout) findViewById(R.id.sliding_tabs);
        slidingTabLayout.setDistributeEvenly(true);
        slidingTabLayout.setViewPager(viewPager);

//        getOwnedCars();
        queryOwnedCars();
        user = ParseUser.getCurrentUser();
        System.out.println("Current user is " + getUserName());
//        setContentView(R.layout.activity_main);

        //if (savedInstanceState == null) {
        //    getSupportFragmentManager().beginTransaction()
        //            .add(R.id.container, new PlaceholderFragment())
        //            .commit();
        //}
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
    private void queryOwnedCars() {
        ParseQuery<Car> query = Car.getQuery().whereEqualTo("Owner", user);
        query.findInBackground(new FindCallback<Car>() {
            @Override
            public void done(List<Car> result, ParseException e) {
                if (e == null) {
                    System.out.println("Parse query, result size is " + result.toString() + "size is " + result.size());
                    cars = new Car[result.size()];
                    for (int i = 0; i < result.size(); i++) {
                        cars[i] = result.get(i);
                        System.out.println(cars[i].getDisplayName());
                    }
                } else {
                    switch (e.getCode()) {
                        case 141:
                            System.out.println("Failed to find cars.");
                            break;
                        default:
                            throw new RuntimeException(e.getMessage());
                    }
                }
            }
        });
    }
    private static void getOwnedCars() {
        ParseCloud.callFunctionInBackground("getOwnedCars", new HashMap<String, Object>(), new FunctionCallback<ArrayList>() {
            @Override
            public void done(ArrayList result, ParseException e) {
                if (e == null) {
                    cars = new Car[result.size()];
                    for (int i = 0; i < result.size(); i++) {
                        cars[i] = (Car) result.get(i);
                    }
                } else {
                    switch (e.getCode()) {
                        case 141:
                            System.out.println("Failed to find cars.");
                            break;
                        default:
                            throw new RuntimeException(e.getMessage());
                    }
                }
            }
        });
    }
}
