package com.mty.groupfuel;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.mty.groupfuel.datamodel.User;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;


public class PersonalFragment extends Fragment {

    final Calendar myCalendar = Calendar.getInstance();
    final private User user = (User) ParseUser.getCurrentUser();
    private final SimpleDateFormat sdf = new SimpleDateFormat(Consts.DATE_FORMAT, Locale.US);
    Context context;
    private EditText editTextBirth;
    private EditText editTextFirst;
    private EditText editTextLast;
    private EditText editTextCountry;
    private RadioGroup radioGroupGender;
    private Button buttonApply;

    private void findViewsById(View view) {
        editTextBirth = (EditText) view.findViewById(R.id.update_birth);
        editTextFirst = (EditText) view.findViewById(R.id.update_first);
        editTextLast = (EditText) view.findViewById(R.id.update_last);
        editTextCountry = (EditText) view.findViewById(R.id.update_country);
        radioGroupGender = (RadioGroup) view.findViewById(R.id.update_gender);
        buttonApply = (Button) view.findViewById(R.id.update_button);
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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_personal, container, false);
        findViewsById(view);
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
                new DatePickerDialog(context, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        getDefaultsFromUser();
        buttonApply.setOnClickListener(createApplyListener());
        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
                user.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            Toast.makeText(context, context.getString(R.string.fueling_updated), Toast.LENGTH_LONG).show();
                            getActivity().getSupportFragmentManager().popBackStack();
                        } else {
                            throw new RuntimeException(e.getMessage());
                        }
                    }
                });
            }
        };
    }
}
