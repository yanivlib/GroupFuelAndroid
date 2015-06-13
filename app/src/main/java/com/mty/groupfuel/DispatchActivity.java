package com.mty.groupfuel;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.mty.groupfuel.datamodel.User;
import com.parse.Parse;
import com.parse.ParseUser;



public class DispatchActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        User user = (User) User.getCurrentUser();
//        setContentView(R.layout.activity_login);
        if (user != null) {
//            System.out.println(" --- YL : user is logged in.");
//            System.out.println(user.getUsername());
            startActivity(new Intent(this, MainActivity.class));
        } else {
//            System.out.println(" --- YL : user is not logged in.");
            startActivity(new Intent(this, WelcomeActivity.class));
        }
    }
/*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
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
    }*/
}
