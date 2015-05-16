package com.mty.groupfuel;


import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.mty.groupfuel.datamodel.Car;
import com.mty.groupfuel.datamodel.Fuel;
import com.mty.groupfuel.datamodel.Fueling;
import com.mty.groupfuel.datamodel.User;
import com.parse.ParseObject;
import com.parse.ParseUser;

public class FuelingFragment extends android.support.v4.app.Fragment {
//    private static final String CARS_KEY = "cars_key";

    private Car[] cars;
    Context context;

    private EditText mileageEditText;
    private EditText priceEditText;
    private EditText amountEditText;
    private EditText locationEditText;
    private Spinner carSpinner;
    private String selectedFuel;
    private Car selectedCar;

    public FuelingFragment() {
    }

    public static FuelingFragment newInstance() {
        Bundle args = new Bundle();
//        args.putSerializable(CARS_KEY, cars);
        FuelingFragment fuelingFragment = new FuelingFragment();
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
                try {
                    selectedCar = cars[pos];
                } catch (NullPointerException e) {
                    selectedCar = new Car();
                }
            }
        }
        public void onNothingSelected(AdapterView<?> parent) {
            // Do Nothing
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
            fueling.put("Car", ParseObject.createWithoutData("Car", selectedCar.getObjectId()));
            fueling.saveEventually();

            Toast.makeText(context,"Fueling updated",Toast.LENGTH_LONG).show();
            amountEditText.setText(null);
            mileageEditText.setText(null);
            priceEditText.setText(null);
            locationEditText.setText(null);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cars = ((MainActivity) getActivity()).getCars();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fueling, container, false);
        this.context = view.getContext();
        String[] carNames;
        try {
            carNames = new String[cars.length];
        } catch (java.lang.NullPointerException e) {
            carNames = new String[0];
        }

        for(int i = 0; i < carNames.length; i++) {
            carNames[i] = cars[i].getDisplayName();
        }
        ArrayAdapter<String> carSpinnerAdapter = new ArrayAdapter<>(view.getContext(),
                android.R.layout.simple_spinner_item, carNames);
        carSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        carSpinner = (Spinner) view.findViewById(R.id.fueling_car);
        carSpinner.setAdapter(carSpinnerAdapter);
        carSpinner.setOnItemSelectedListener(new SpinnerSelect(Car.class));

        mileageEditText = (EditText) view.findViewById(R.id.fueling_mileage);
        priceEditText = (EditText) view.findViewById(R.id.fueling_price);
        amountEditText = (EditText) view.findViewById(R.id.fueling_amount);
        locationEditText = (EditText) view.findViewById(R.id.fueling_location);

        Spinner fuelSpinner = (Spinner) view.findViewById(R.id.fueling_type);
        ArrayAdapter<String> fuelSpinnerAdapter = new ArrayAdapter<>(view.getContext(),
                android.R.layout.simple_spinner_item, Fuel.getNames());
        fuelSpinner.setAdapter(fuelSpinnerAdapter);
        fuelSpinner.setOnItemSelectedListener(new SpinnerSelect(Fuel.class));

        Button sendButton = (Button) view.findViewById(R.id.fueling_send);
        sendButton.setOnClickListener(new SendListener());
        return view;

    }

    private static Number numberFromEditText(EditText editText) {
        return Integer.parseInt(editText.getText().toString());
    }

    public void updateCars(Car[] cars) {
        String[] carNames = new String[cars.length];
        this.cars = new Car[cars.length];
        for(int i = 0; i < cars.length; i++) {
            carNames[i] = cars[i].getDisplayName();
            this.cars[i] = cars[i];
        }
        ArrayAdapter<String> carSpinnerAdapter = new ArrayAdapter<>(this.context,
                android.R.layout.simple_spinner_item, carNames);
        carSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        carSpinner.setAdapter(carSpinnerAdapter);
    }
}