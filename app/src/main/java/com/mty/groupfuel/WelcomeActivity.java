package com.mty.groupfuel;


import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;

public class WelcomeActivity extends Activity {

    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
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
                    Intent intent = new Intent(WelcomeActivity.this, PersonalActivity.class);
                    intent.putExtra(Consts.PARENT_ACTIVITY_NAME, RegisterActivity.class.getName());
                    startActivity(intent);
                } else {
                    // User logged in through Facebook
                    startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
                }
            }
        });
    }


}
