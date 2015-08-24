package com.mty.groupfuel;

import android.app.DatePickerDialog;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
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


public class PersonalFragment extends Fragment implements View.OnClickListener {

    private static ProgressDialog progressDialog;
    final private Calendar myCalendar = Calendar.getInstance();
    final private User user = (User) ParseUser.getCurrentUser();
    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(Consts.DATE_FORMAT, Locale.US);
    private EditText birth;
    private EditText firstName;
    private EditText lastName;
    private RadioGroup gender;
    private Button applyButton;

    private void findViewsById(View view) {
        birth = (EditText) view.findViewById(R.id.birth);
        firstName = (EditText) view.findViewById(R.id.first_name);
        lastName = (EditText) view.findViewById(R.id.last_name);
        gender = (RadioGroup) view.findViewById(R.id.gender);
        applyButton = (Button) view.findViewById(R.id.button);
    }

    private void getDefaultsFromUser() {
        if (user.getBirthDate() != null) {
            birth.setText(user.getBirthDate().toString());
        }
        if (user.getFirstName() != null) {
            firstName.setText(user.getFirstName());
        }
        if (user.getLastName() != null) {
            lastName.setText(user.getLastName());
        }
        if (user.getGender()) {
            gender.check(R.id.male);
        } else {
            gender.check(R.id.female);
        }

        if (user.getBirthDate() != null) {
            birth.setText(simpleDateFormat.format(user.getBirthDate()));
        }
    }

    private void updateLabel() {
        birth.setText(simpleDateFormat.format(myCalendar.getTime()));
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
        birth.setKeyListener(null);
        birth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(getActivity(), date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        getDefaultsFromUser();
        applyButton.setOnClickListener(this);
        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onClick(final View view) {
        applyButton.setEnabled(false);
        user.setFirstName(firstName.getText().toString());
        user.setLastName(lastName.getText().toString());
        user.setBirthDate(myCalendar.getTime());
        if (gender.getCheckedRadioButtonId() == R.id.male) {
            user.setGender(true);
        } else {
            user.setGender(false);
        }
        progressDialog = ProgressDialog.show(getActivity(), getResources().getString(R.string.wait), getResources().getString(R.string.personal_progress));
        user.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                progressDialog.dismiss();
                if (e == null) {
                    Toast.makeText(view.getContext(), "Personal details updated", Toast.LENGTH_LONG).show();
                    final FragmentManager fm = getActivity().getSupportFragmentManager();
                    if (fm.findFragmentByTag(ViewPagerContainerFragment.class.getSimpleName()) == null){
                        android.support.v4.app.FragmentTransaction transaction = fm.beginTransaction();
                        transaction.replace(R.id.content_frame, new ViewPagerContainerFragment(), ViewPagerContainerFragment.class.getSimpleName());
                        transaction.addToBackStack(null);
                        transaction.commit();
                    } else {
                        fm.popBackStack();
                    }
                } else {
                    Alerter.createErrorAlert(e, getActivity()).show();
                    applyButton.setEnabled(true);
                }
            }
        });
    }
}
