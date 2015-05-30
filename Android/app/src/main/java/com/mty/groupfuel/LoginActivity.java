package com.mty.groupfuel;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.mty.groupfuel.datamodel.User;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import android.content.Intent;

import java.util.ArrayList;


public class LoginActivity extends ActionBarActivity {

    private ProgressDialog progressDialog;
    private EditText usernameET;
    private EditText passwordET;

    private void findViewsById() {
        usernameET = (EditText) findViewById(R.id.usernameText);
        passwordET = (EditText) findViewById(R.id.passwordText);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        findViewsById();
    }

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
    }

    public void login (View view) {
        progressDialog = ProgressDialog.show(this, getResources().getString(R.string.wait), getResources().getString(R.string.login_progress));
        String username = usernameET.getText().toString().trim();
        String password = passwordET.getText().toString().trim();
        usernameET.setText("");
        passwordET.setText("");
        ArrayList<String> error = new ArrayList<>();
        if (username.isEmpty()) {
            error.add(getResources().getString(R.string.username_empty));
        }
        if (password.isEmpty()) {
            error.add(getResources().getString(R.string.password_empty));
        }
        //TODO add more checks.
        if (!error.isEmpty()) {
            progressDialog.dismiss();
            MainActivity.createErrorAlert(error, getString(R.string.login_error_title),this).show();
            return;
        }
        User.logInInBackground(username, password, new LogInCallback() {
            public void done(ParseUser user, ParseException e) {
                progressDialog.dismiss();
                if (user != null) {
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                } else {
                    MainActivity.createErrorAlert(e.getMessage(), getString(R.string.login_error_title), LoginActivity.this).show();
                }
            }
        });
    }
    
}
