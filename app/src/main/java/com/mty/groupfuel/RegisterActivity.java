package com.mty.groupfuel;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.mty.groupfuel.datamodel.User;
import com.parse.ParseException;
import com.parse.SignUpCallback;

public class RegisterActivity extends Activity implements View.OnClickListener {

    private EditText usernameET;
    private EditText passwordET;
    private EditText passwordAgainET;
    private Button registerButton;
    private ProgressDialog progressDialog;

    private void findViewsById() {
        usernameET = (EditText) findViewById(R.id.usernameText);
        passwordET = (EditText) findViewById(R.id.passwordText);
        passwordAgainET = (EditText) findViewById(R.id.passwordTextAgain);
        registerButton = (Button) findViewById(R.id.register);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        findViewsById();

        registerButton.setOnClickListener(this);
    }

    public void doRegister(View view) {
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
                    Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                    intent.putExtra(Consts.PARENT_ACTIVITY_NAME, RegisterActivity.class.getName());
                    startActivity(intent);
                } else {
                    MainActivity.createErrorAlert(e.getMessage(), getString(R.string.signup_error_title), RegisterActivity.this).show();
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        doRegister(v);
    }
}
