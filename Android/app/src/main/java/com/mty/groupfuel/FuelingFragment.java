package com.mty.groupfuel;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.mty.groupfuel.datamodel.Car;
import com.mty.groupfuel.datamodel.Fuel;
import com.mty.groupfuel.datamodel.Fueling;
import com.mty.groupfuel.datamodel.User;
import com.parse.Parse;
import com.parse.ParseUser;

import java.util.Arrays;

public class FuelingFragment extends Fragment {
    private static final String CARS_KEY = "cars_key";

    private EditText mileageEditText;
    private EditText priceEditText;
    private EditText amountEditText;
    private EditText locationEditText;
    private String selectedFuel;
    private Car selectedCar;
    private Button sendButton;

    public FuelingFragment() {
        // Required empty public constructor
    }

    public static FuelingFragment newInstance(Car[] cars) {
        FuelingFragment fuelingFragment = new FuelingFragment();

        Bundle args = new Bundle();
        args.putSerializable(CARS_KEY, cars);
        fuelingFragment.setArguments(args);

        return fuelingFragment;
    }

    private class SpinnerSelect implements AdapterView.OnItemSelectedListener {

        private Class spinnerType;
        SpinnerSelect(Class type) {
            spinnerType = type;
        }
        public void onItemSelected(AdapterView<?> parent, View view,
                                   int pos, long id) {
            if (spinnerType == Fuel.class) {
                selectedFuel = (String) parent.getItemAtPosition(pos);
            } else if (spinnerType == Car.class) {
                Car[] cars = (Car[]) getArguments().getSerializable(CARS_KEY);
                selectedCar = cars[pos];
            }
        }
        public void onNothingSelected(AdapterView<?> parent) {
            // Another interface callback
        }
    }

    private class SendListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            Number amount = numberFromEditText(amountEditText);
            Number mileage = numberFromEditText(mileageEditText);
            Number price = numberFromEditText(priceEditText);
            User user = (User) ParseUser.getCurrentUser();

            Fueling fueling = new Fueling();

            fueling.setAmount(amount);
            fueling.setMileage(mileage);
            fueling.setPrice(price);
            fueling.setUser(user);
            fueling.setFuelType(Fuel.fromString(selectedFuel));
            fueling.setCar(selectedCar);

            fueling.saveInBackground();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        Car[] cars = (Car[]) getArguments().getSerializable(CARS_KEY);
        String[] carNames = new String[cars.length];
        for(int i = 0; i < cars.length; i++) {
            carNames[i] = cars[i].getDisplayName();
        }
//        Fuel defaultFuel = cars[0].getModel().getFuelType();

        View view = inflater.inflate(R.layout.fragment_fueling, container, false);
        ArrayAdapter<String> carSpinnerAdapter = new ArrayAdapter<String>(view.getContext(),
                android.R.layout.simple_spinner_item, carNames);
        carSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner carSpinner = (Spinner) view.findViewById(R.id.fueling_car);
        carSpinner.setAdapter(carSpinnerAdapter);
        carSpinner.setOnItemSelectedListener(new SpinnerSelect(Car.class));

        mileageEditText = (EditText) view.findViewById(R.id.fueling_mileage);
        priceEditText = (EditText) view.findViewById(R.id.fueling_price);
        amountEditText = (EditText) view.findViewById(R.id.fueling_amount);

        Spinner fuelSpinner = (Spinner) view.findViewById(R.id.fueling_type);
        ArrayAdapter<String> fuelSpinnerAdapter = new ArrayAdapter<String>(view.getContext(),
                android.R.layout.simple_spinner_item, Fuel.getNames());
        fuelSpinner.setAdapter(fuelSpinnerAdapter);
        fuelSpinner.setOnItemSelectedListener(new SpinnerSelect(Fuel.class));

        sendButton = (Button) view.findViewById(R.id.fueling_send);
        sendButton.setOnClickListener(new SendListener());
        return view;

    }

    private static Number numberFromEditText(EditText editText) {
        return Integer.parseInt(editText.getText().toString());
    }
}
