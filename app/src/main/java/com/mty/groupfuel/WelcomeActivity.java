package com.mty.groupfuel;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;

public class WelcomeActivity extends Activity implements View.OnClickListener {

    private ProgressDialog progressDialog;
    private Button loginButton;
    private Button registerButton;
    private Button facebookButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        loginButton = (Button)findViewById(R.id.login_button);
        registerButton = (Button)findViewById(R.id.register_button);
        facebookButton = (Button)findViewById(R.id.facebook_login);

        loginButton.setOnClickListener(this);
        registerButton.setOnClickListener(this);
        facebookButton.setOnClickListener(this);
    }

    public void doLogin (View view) {
        startActivity(new Intent(WelcomeActivity.this, LoginActivity.class));
    }

    public void doRegister (View view) {
        startActivity(new Intent(WelcomeActivity.this, RegisterActivity.class));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ParseFacebookUtils.onActivityResult(requestCode, resultCode, data);
    }

    public void doFacebookLogin(View view) {
        progressDialog = ProgressDialog.show(this, getResources().getString(R.string.wait), getResources().getString(R.string.login_progress));
        ParseFacebookUtils.logInWithReadPermissionsInBackground(this, null, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException err) {
                progressDialog.dismiss();
                if (err != null) {
                    throw new RuntimeException(err.getMessage());
                } else if (user == null) {
                    return;
                } else if (user.isNew()) {
                    // User signed up and logged in through Facebook
                    Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
                    intent.putExtra(Consts.PARENT_ACTIVITY_NAME, RegisterActivity.class.getName());
                    startActivity(intent);
                } else {
                    // User logged in through Facebook
                    startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_button:
                doLogin(v);
                break;
            case R.id.register_button:
                doRegister(v);
                break;
            case R.id.facebook_login:
                doFacebookLogin(v);
        }
    }
}
