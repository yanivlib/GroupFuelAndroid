package com.mty.groupfuel;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.mty.groupfuel.datamodel.User;
import com.parse.ParseException;
import com.parse.SignUpCallback;

public class RegisterActivity extends Activity {

    private EditText usernameET;
    private EditText passwordET;
    private EditText passwordAgainET;
    private ProgressDialog progressDialog;

    private void findViewsById() {
        usernameET = (EditText) findViewById(R.id.usernameText);
        passwordET = (EditText) findViewById(R.id.passwordText);
        passwordAgainET = (EditText) findViewById(R.id.passwordTextAgain);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        findViewsById();
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
        String error = "";
        String username = usernameET.getText().toString().trim();
        String password = passwordET.getText().toString().trim();
        String passwordAgain = passwordAgainET.getText().toString().trim();

        if (username.isEmpty()) {
            error += getString(R.string.username_empty);
        }
        if (password.isEmpty()) {
            error += getString(R.string.password_empty);
        }
        if (!password.equals(passwordAgain)) {
            error += getString(R.string.passwords_diff);
        }
        if (error.length() > 0) {
            MainActivity.createErrorAlert(error, getString(R.string.signup_error_title), this).show();
            return;
        }
        //ParseUser user = new ParseUser();
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        progressDialog = ProgressDialog.show(this, getResources().getString(R.string.wait), getResources().getString(R.string.signup_progress));
        user.signUpInBackground(new SignUpCallback() {
            public void done(ParseException e) {
                progressDialog.dismiss();
                if (e == null) {
                    Intent intent = new Intent(RegisterActivity.this, PersonalActivity.class);
                    intent.putExtra(Consts.PARENT_ACTIVITY_NAME, RegisterActivity.class.getName());
                    startActivity(intent);
                } else {
                    MainActivity.createErrorAlert(e.getMessage(), getString(R.string.signup_error_title), RegisterActivity.this).show();
                }
            }
        });
    }
}
