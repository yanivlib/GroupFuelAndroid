package com.mty.groupfuel;

import android.app.DatePickerDialog;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.provider.SyncStateContract;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.app.DatePickerDialog.OnDateSetListener;

import com.mty.groupfuel.datamodel.User;
import com.parse.ParseUser;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;


public class PersonalActivity extends ActionBarActivity {

    private EditText editTextBirth;
    private EditText editTextFirst;
    private EditText editTextLast;
    private EditText editTextCountry;
    private RadioGroup radioGroupGender;
    private Button buttonApply;

    final Calendar myCalendar = Calendar.getInstance();

    final private User user = (User)ParseUser.getCurrentUser();

    private String prevActivity;

    private final SimpleDateFormat sdf = new SimpleDateFormat(Consts.DATE_FORMAT, Locale.US);

    private void findViewsById() {
        editTextBirth = (EditText)findViewById(R.id.update_birth);
        editTextFirst = (EditText)findViewById(R.id.update_first);
        editTextLast = (EditText)findViewById(R.id.update_last);
        editTextCountry = (EditText)findViewById(R.id.update_country);
        radioGroupGender = (RadioGroup)findViewById(R.id.update_gender);
        buttonApply = (Button)findViewById(R.id.update_button);
    }

    private void getDefaultsFromUser() {
        if (user.getBirthDate() != null) {
            editTextBirth.setText(user.getBirthDate().toString());
        }
        if (user.getFirstName() != null) {
            editTextFirst.setText(user.getFirstName());
        }
        if (user.getLastName() != null) {
            editTextLast.setText(user.getLastName());
        }
        if (user.getCountry() != null) {
            editTextCountry.setText(user.getCountry());
        }
        if (user.getGender()) {
            radioGroupGender.check(R.id.update_male);
        } else {
            radioGroupGender.check(R.id.update_female);
        }

        if (user.getBirthDate() != null) {
            editTextBirth.setText(sdf.format(user.getBirthDate()));
        }
    }

    private void updateLabel() {
        editTextBirth.setText(sdf.format(myCalendar.getTime()));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal);
        prevActivity = getIntent().getExtras().getString(Consts.PARENT_ACTIVITY_NAME);
        findViewsById();
        getDefaultsFromUser();
        buttonApply.setOnClickListener(createApplyListener());

        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }

        };

        editTextBirth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(PersonalActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
    }

    public View.OnClickListener createApplyListener() {
        return  new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                user.setFirstName(editTextFirst.getText().toString());
                user.setLastName(editTextLast.getText().toString());
                user.setCountry(editTextCountry.getText().toString());
                user.setBirthDate(myCalendar.getTime());
                if (radioGroupGender.getCheckedRadioButtonId() == R.id.update_male) {
                    user.setGender(true);
                } else {
                    user.setGender(false);
                }
            }
        };
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_personal, menu);
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

}
