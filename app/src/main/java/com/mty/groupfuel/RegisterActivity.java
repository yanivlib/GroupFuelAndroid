package com.mty.groupfuel;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.mty.groupfuel.datamodel.User;
import com.parse.ParseException;
import com.parse.SignUpCallback;

public class RegisterActivity extends Activity implements View.OnClickListener {

    private EditText username;
    private EditText password;
    private EditText passwordAgain;
    private EditText email;
    private Button button;
    private ProgressDialog progressDialog;

    private void findViewsById() {
        username = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);
        passwordAgain = (EditText) findViewById(R.id.password_again);
        email = (EditText) findViewById(R.id.email);
        button = (Button) findViewById(R.id.register);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        findViewsById();

        button.setOnClickListener(this);
    }

    public void doRegister(View view) {
        button.setEnabled(false);
        StringBuilder error = new StringBuilder();
        String username = this.username.getText().toString().trim();
        String email = this.email.getText().toString().trim();
        String password = this.password.getText().toString().trim();
        String passwordAgain = this.passwordAgain.getText().toString().trim();

        if (username.isEmpty()) {
            error.append(getString(R.string.username_empty));
            error.append('\n');
        }
        if (password.isEmpty()) {
            error.append(getString(R.string.password_empty));
            error.append('\n');
        }
        if (!password.equals(passwordAgain)) {
            error.append(getString(R.string.passwords_diff));
            error.append('\n');
        }
        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            error.append("Please provide legal email");
            error.append('\n');
        }
        if (error.length() > 0) {
            Alerter.createErrorAlert(error.toString(), getString(R.string.signup_error_title), this).show();
            button.setEnabled(true);
            return;
        }
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setEmail(email);
        progressDialog = ProgressDialog.show(this, getResources().getString(R.string.wait), getResources().getString(R.string.signup_progress));
        user.signUpInBackground(new SignUpCallback() {
            public void done(ParseException e) {
                progressDialog.dismiss();
                if (e == null) {
                    startActivity(new Intent(RegisterActivity.this, MainActivity.class)
                            .putExtra(Consts.PARENT_ACTIVITY_NAME, RegisterActivity.class.getName()));
                } else {
                    Alerter.createErrorAlert(e.getMessage(), getString(R.string.signup_error_title), RegisterActivity.this).show();
                    button.setEnabled(true);
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        doRegister(v);
    }
}
