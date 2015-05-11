package com.mty.groupfuel;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.mty.groupfuel.datamodel.Fuel;
import com.mty.groupfuel.datamodel.Fueling;
import com.mty.groupfuel.datamodel.User;
import com.parse.Parse;
import com.parse.ParseUser;

import java.util.Arrays;

public class FuelingFragment extends Fragment {

    private Fueling fueling;

    private EditText mileageEditText;
    private EditText priceEditText;
    private EditText amountEditText;
    private Spinner fuelSpinner;
    private Spinner carSpinner;
    private EditText locationEditText;

    public FuelingFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_fueling, container, false);
        mileageEditText = (EditText) view.findViewById(R.id.fueling_mileage);
        priceEditText = (EditText) view.findViewById(R.id.fueling_price);
        carSpinner = (Spinner) view.findViewById(R.id.fueling_car);
        amountEditText = (EditText) view.findViewById(R.id.fueling_amount);

        fuelSpinner = (Spinner) view.findViewById(R.id.fueling_type);
        ArrayAdapter<String> fuelSpinnerAdapter = new ArrayAdapter<String>(view.getContext(),
                android.R.layout.simple_spinner_item, Fuel.getNames());
        fuelSpinner.setAdapter(fuelSpinnerAdapter);
        return view;

    }

    public void doSend(View view) {
        Number amount = numberFromEditText(amountEditText);
        Number mileage = numberFromEditText(mileageEditText);
        Number price = numberFromEditText(priceEditText);
        User user = (User) ParseUser.getCurrentUser();



        fueling.setAmount(amount);
        fueling.setMileage(mileage);
        fueling.setPrice(price);
        fueling.setUser(user);
    }

    private static Number numberFromEditText(EditText editText) {
        return Integer.parseInt(editText.getText().toString());
    }
}
