package com.mty.groupfuel;


import android.app.Activity;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class WelcomeActivity extends Activity {

//    Button loginButton;
//    Button registerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

//        loginButton = (Button) findViewById(R.id.login_button);
//        registerButton = (Button) findViewById(R.id.register_button);

    }

    public void doLogin (View view) {
        startActivity(new Intent(WelcomeActivity.this, LoginActivity.class));
    }

    public void doRegister (View view) {
        startActivity(new Intent(WelcomeActivity.this, RegisterActivity.class));
    }

}
