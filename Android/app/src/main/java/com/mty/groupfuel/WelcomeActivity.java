package com.mty.groupfuel;


import android.app.Activity;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class WelcomeActivity extends Activity {

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

}
