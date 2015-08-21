package com.mty.groupfuel;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.mty.groupfuel.datamodel.User;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;


public class LoginActivity extends Activity implements View.OnClickListener {

    private ProgressDialog progressDialog;
    private EditText username;
    private EditText password;
    private Button loginButton;

    private void findViewsById() {
        username = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);
        loginButton = (Button) findViewById(R.id.login);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        findViewsById();

        loginButton.setOnClickListener(this);
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

    public void doLogin(View view) {
        loginButton.setEnabled(false);
        progressDialog = ProgressDialog.show(this, getResources().getString(R.string.wait), getResources().getString(R.string.login_progress));
        String username = this.username.getText().toString().trim();
        String password = this.password.getText().toString().trim();
        this.password.setText("");
        final StringBuilder error = new StringBuilder();
        if (username.isEmpty()) {
            error.append(getResources().getString(R.string.username_empty));
            error.append('\n');
        }
        if (password.isEmpty()) {
            error.append(getResources().getString(R.string.password_empty));
            error.append('\n');
        }
        //TODO add more checks.
        if (error.length() > 0) {
            progressDialog.dismiss();
            Alerter.createErrorAlert(error.toString(), getString(R.string.login_error_title), this).show();
            loginButton.setEnabled(true);
            return;
        }
        User.logInInBackground(username, password, new LogInCallback() {
            public void done(ParseUser user, ParseException e) {
                progressDialog.dismiss();
                if (user != null) {
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                } else {
                    Alerter.createErrorAlert(e.getMessage(), getString(R.string.login_error_title), LoginActivity.this).show();
                    loginButton.setEnabled(true);
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        doLogin(v);
    }
}
