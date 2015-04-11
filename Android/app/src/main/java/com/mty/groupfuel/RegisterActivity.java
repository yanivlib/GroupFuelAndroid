package com.mty.groupfuel;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.parse.SignUpCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

public class RegisterActivity extends ActionBarActivity {

    private EditText usernameET;
    private EditText passwordET;
    private EditText passwordAgainET;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        usernameET = (EditText) findViewById(R.id.usernameText);
        passwordET = (EditText) findViewById(R.id.passwordText);
        passwordAgainET = (EditText) findViewById(R.id.passwordTextAgain);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_register, menu);
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

    public void register (View view) {
        String username = usernameET.getText().toString().trim();
        String password = passwordET.getText().toString().trim();
        String passwordAgain = passwordAgainET.getText().toString().trim();

        //TODO: check that values are legal and non-empty.

        ParseUser user = new ParseUser();
        user.setUsername(username);
        user.setPassword(password);
//        user.setEmail("email@example.com");

        user.signUpInBackground(new SignUpCallback() {
            public void done(ParseException e) {
                if (e == null) {
                    // Hooray! Let them use the app now.
                    startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                } else {
                    // Sign up didn't succeed.
                    // TODO: Look at the ParseException to figure out what went wrong
                }
            }
        });
    }
}
