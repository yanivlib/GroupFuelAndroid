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
import com.mty.groupfuel.datamodel.CarModel;
import com.mty.groupfuel.datamodel.Fueling;
import com.mty.groupfuel.datamodel.User;
import com.parse.FindCallback;
import com.parse.FunctionCallback;
import com.parse.Parse;
import com.parse.ParseObject;
import com.parse.ParseUser;


public class MainActivity extends ActionBarActivity {

    static private ParseUser user;
    private Car[] cars;
    private FragmentPagerAdapter fragmentPagerAdapter;

    public static String getUserName () {
        return user.getUsername();
    }
    public User getUser () {
        return (User) user;
    }

    public Car[] getCars(){
        return this.cars;
    }

    public void setCars(Car[] cars) {
        this.cars = cars;
        for (Car car : cars) {
            System.out.println(car.getDisplayName());
        }
        FuelingFragment fuelingFragment = (FuelingFragment) fragmentPagerAdapter.getRegisteredFragment(1);
        fuelingFragment.updateCars(cars);
    }
    public void setfragmentPagerAdapter(FragmentPagerAdapter adapter) {
        this.fragmentPagerAdapter = adapter;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        FragmentPagerAdapter adapter = new FragmentPagerAdapter(getSupportFragmentManager(), MainActivity.this);
        viewPager.setAdapter(adapter);
        setfragmentPagerAdapter(adapter);

        SlidingTabLayout slidingTabLayout = (SlidingTabLayout) findViewById(R.id.sliding_tabs);
        slidingTabLayout.setDistributeEvenly(true);
        slidingTabLayout.setViewPager(viewPager);
		
		getOwnedCars();
        User user = (User)User.getCurrentUser();
        username = user.getUsername();
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

    private void getOwnedCars() {
        ParseCloud.callFunctionInBackground("getOwnedCars", new HashMap<String, Object>(), new FunctionCallback<ArrayList>() {
            @Override
            public void done(ArrayList result, ParseException e) {
                System.out.println("getOwnedCars done.");
                if (e == null) {
                    Car[] new_cars = new Car[result.size()];
                    for (int i = 0; i < result.size(); i++) {
                        new_cars[i] = (Car) result.get(i);
                    }
                    setCars(new_cars);
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
