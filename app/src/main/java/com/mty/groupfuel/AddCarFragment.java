package com.mty.groupfuel;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.mty.groupfuel.datamodel.Car;
import com.mty.groupfuel.datamodel.CarModel;
import com.mty.groupfuel.datamodel.Fuel;
import com.mty.groupfuel.datamodel.Gear;
import com.mty.groupfuel.datamodel.User;
import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddCarFragment extends Fragment implements View.OnClickListener{

    private static ProgressDialog progressDialog;
    CarsListener mCallback;
    private Spinner maker;
    private Spinner model;
    private Spinner engine;
    private Spinner year;
    private Spinner gear;
    private Spinner fuel;
    private EditText number;
    private EditText mileage;
    private Button button;
    private CheckBox hybrid;
    private Spinner spinners[];
    private List<CarModel> modelList;
    private Map<String, List<CarModel>> makerModels;
    private Map<String, List<Object>> makerModelNames;
    private String pleaseSelect;
    private Context context;

    private static List<Object> getEngines(String model, List<CarModel> modelList) {
        List<Object> engines = new ArrayList<>();
        for (CarModel currentModel : modelList) {
            Number volume = currentModel.getVolume();
            if (currentModel.getModel().equals(model) && !engines.contains(volume)) {
                engines.add(volume);
            }
        }
        return engines;
    }

    private static List<Object> getYears(String model, Number engine, List<CarModel> modelList) {
        List<Object> years = new ArrayList<>();
        for (CarModel currentModel : modelList) {
            Number year = currentModel.getYear();
            if (currentModel.getModel().equals(model)
                    && currentModel.getVolume().equals(engine)
                    && !years.contains(year)) {
                years.add(year);
            }
        }
        return years;

    }

    private static List<Object> getGears(String model, Number engine, Number year, List<CarModel> modelList) {
        List<Object> gears = new ArrayList<>();
        for (CarModel currentModel : modelList) {
            Gear gear = currentModel.getGear();
            if (currentModel.getModel().equals(model)
                    && currentModel.getVolume().equals(engine)
                    && currentModel.getYear().equals(year)
                    && !gears.contains(gear)) {
                gears.add(gear);
            }
        }
        return gears;
    }

    private static List<Object> getFuels(String model, Number engine, Number year, Gear gear, List<CarModel> modelList) {
        List<Object> fuels = new ArrayList<>();
        for (CarModel currentModel : modelList) {
            Fuel fuel = currentModel.getFuelType();
            if (currentModel.getModel().equals(model)
                    && currentModel.getVolume().equals(engine)
                    && currentModel.getYear().equals(year)
                    && currentModel.getGear().equals(gear)
                    && !fuels.contains(fuel)) {
                fuels.add(fuel);
            }
        }
        return fuels;
    }

    private static CarModel getModel(String maker, String model, Number volume, Number year, Gear gear, Fuel fuel, List<CarModel> modelList) {
        for (CarModel carModel : modelList) {
            if (carModel.getMake().equals(maker)
                    && carModel.getModel().equals(model)
                    && carModel.getVolume().equals(volume)
                    && carModel.getYear().equals(year)
                    && carModel.getGear().equals(gear)
                    && carModel.getFuelType().equals(fuel)) {
                return ParseObject.createWithoutData(CarModel.class, carModel.getObjectId());
            }
        }
        return ParseObject.createWithoutData(CarModel.class, Consts.OBJECTID_NULL);
    }

    public void setModelList(List<CarModel> modelList) {
        this.modelList = modelList;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallback = (CarsListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement CarsListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_car, container, false);
        findViewsById(view);
        context = view.getContext();
        spinners = new Spinner[]{maker, model, engine, year, gear, fuel};
        attachAdapters();
        disableAll();
        getMakers();
        button.setOnClickListener(this);
        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        makerModels = new HashMap<>();
        makerModelNames = new HashMap<>();
        pleaseSelect = getString(R.string.please_select);
    }

    private void findViewsById(View view) {
        maker = (Spinner) view.findViewById(R.id.add_car_maker);
        model = (Spinner) view.findViewById(R.id.add_car_model);
        engine = (Spinner) view.findViewById(R.id.add_car_engine);
        year = (Spinner) view.findViewById(R.id.add_car_year);
        number = (EditText) view.findViewById(R.id.add_car_number);
        button = (Button) view.findViewById(R.id.add_car_button);
        hybrid = (CheckBox) view.findViewById(R.id.add_car_hybrid);
        gear = (Spinner) view.findViewById(R.id.add_car_gear);
        fuel = (Spinner) view.findViewById(R.id.add_car_fuel);
        mileage = (EditText)view.findViewById(R.id.mileage);
    }

    private void disableFollowing(Spinner spinner) {
        if (spinner == maker) {
            model.setEnabled(false);
            model.setSelection(0);
        }
        if (spinner == model) {
            engine.setEnabled(false);
            engine.setSelection(0);
        }
        if (spinner == engine) {
            year.setEnabled(false);
            year.setSelection(0);
        }
        if (spinner == year) {
            gear.setEnabled(false);
            gear.setSelection(0);
        }
        if (spinner == gear) {
            fuel.setEnabled(false);
            fuel.setSelection(0);
        }
        if (spinner == fuel) {
            button.setEnabled(false);
        }
    }

    private void updateSpinnerList(Spinner spinner, List<Object> array) {
        ArrayAdapter<Object> adapter = (ArrayAdapter<Object>) spinner.getAdapter();
        adapter.clear();
        adapter.add(pleaseSelect);
        adapter.addAll(array);
        spinner.setSelection(0);
        spinner.setEnabled(true);
        disableFollowing(spinner);
    }

    private void attachAdapters() {
        for (Spinner spinner : spinners) {
            List<String> array = new ArrayList<>();
            array.add(pleaseSelect);
            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                    context, android.R.layout.simple_spinner_item, array);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(adapter);
            spinner.setSelection(0);
            spinner.setOnItemSelectedListener(getListener(SpinnerType.fromSpinner(spinner, this)));
        }
    }

    private void disableAll() {
        for (Spinner spinner : spinners) {
            spinner.setEnabled(false);
        }
        button.setEnabled(false);
    }

    private void getMakers() {
        ParseCloud.callFunctionInBackground(Consts.GET_CAR_MAKES, new HashMap<String, Object>(),
                new FunctionCallback<ArrayList<Object>>() {
                    @Override
                    public void done(ArrayList<Object> result, ParseException e) {
                        if (e == null) {
                            updateSpinnerList(maker, result);
                        } else {
                            Alerter.createErrorAlert(e, context).show();
                        }
                    }
                });
    }

    private void getModels(final String make) {
        if (makerModels.containsKey(make) && makerModelNames.containsKey(make)) {
            setModelList(makerModels.get(make));
            updateSpinnerList(model, makerModelNames.get(make));
            System.out.println("getModels retrieved from memory");
            return;
        }
        final Map<String, Object> params = new HashMap<>();
        params.put(Consts.PARAMS_MAKE, make);
        System.out.println("getModels calls cloud function");
        ParseCloud.callFunctionInBackground(Consts.GET_CAR_MODELS, params, new FunctionCallback<HashMap<String, ArrayList>>() {
            @Override
            public void done(HashMap<String, ArrayList> result, ParseException e) {
                System.out.println("getModels returned from cloud function");
                if (e == null) {
                    System.out.println(result.toString());
                    ArrayList<CarModel> resultSet = (ArrayList<CarModel>) result.get("resultSet");
                    ArrayList<Object> distinctModels = (ArrayList<Object>) result.get("distinctModels");
                    setModelList(resultSet);
                    updateSpinnerList(model, distinctModels);
                    if (!makerModels.containsKey(make)) {
                        makerModels.put(make, resultSet);
                    }
                    if (!makerModelNames.containsKey(make)) {
                        makerModelNames.put(make, distinctModels);
                    }
                } else {
                    Alerter.createErrorAlert(e, context).show();
                }
            }
        });
    }

    private String getCurrentMaker() {
        return maker.getSelectedItem().toString();
    }

    private String getCurrentModel() {
        return model.getSelectedItem().toString();
    }

    private Number getCurrentEngine() {
        return (Number) engine.getSelectedItem();
    }

    private Number getCurrentYear() {
        return (Number) year.getSelectedItem();
    }

    private Fuel getCurrentFuel() {
        return (Fuel) fuel.getSelectedItem();
    }

    private Gear getCurrentGear() {
        return (Gear) gear.getSelectedItem();
    }

    private String getCarNumber() {
        return number.getText().toString().trim();
    }

    private AdapterView.OnItemSelectedListener getListener(final SpinnerType type) {
        return new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                Object value = adapterView.getItemAtPosition(position);
                disableFollowing(type.toSpinner(AddCarFragment.this));
                if (position > 0) {
                    switch (type) {
                        case maker:
                            String maker = value.toString();
                            getModels(maker);
                            break;
                        case model:
                            String model = value.toString();
                            List<Object> engines = getEngines(model, modelList);
                            updateSpinnerList(engine, engines);
                            break;
                        case engine:
                            Number engine = (Number) value;
                            List<Object> years = getYears(getCurrentModel(), engine, modelList);
                            updateSpinnerList(year, years);
                            break;
                        case year:
                            Number year = (Number) value;
                            getCurrentEngine();
                            List<Object> gears = getGears(getCurrentModel(), getCurrentEngine(), year, modelList);
                            updateSpinnerList(gear, gears);
                            break;
                        case gear:
                            Gear gear = (Gear) value;
                            List<Object> fuels = getFuels(getCurrentModel(), getCurrentEngine(), getCurrentYear(), gear, modelList);
                            updateSpinnerList(fuel, fuels);
                            break;
                        case fuel:
                            button.setEnabled(true);
                            break;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // Do nothing
            }
        };
    }

    private CarModel getModel() {
        return getModel(getCurrentMaker(), getCurrentModel(), getCurrentEngine(),
                getCurrentYear(), getCurrentGear(), getCurrentFuel(), modelList);
    }

    //private class AddCarListener implements View.OnClickListener {
    @Override
    public void onClick(View v) {
        button.setEnabled(false);
        StringBuilder error = new StringBuilder();
        CarModel model = getModel();
        String number = getCarNumber();
        User user = (User) ParseUser.getCurrentUser();
        if (model.getObjectId().equals(Consts.OBJECTID_NULL)) {
            error.append("Invalid car model");
            error.append('\n');
        }
        if (number.length() != 7) {
            error.append("Car number must be exactly 7 digits long");
            error.append('\n');
        }
        if (user == null) {
            error.append("You must be logged in to add a new car");
            error.append('\n');
        }
        if (mileage.getText().toString().equals("")) {
            error.append("Mileage cannot be empty");
            error.append('\n');
        }
        if (error.length() > 0) {
            Alerter.createErrorAlert(error.toString(), context).show();
            button.setEnabled(true);
            return;
        }
        Number mileage = Integer.parseInt(this.mileage.getText().toString());
        Car car = new Car();
        car.setModel(model);
        car.setOwner(user);
        car.setCarNumber(number);
        car.setMileage(mileage);
        car.setInitialMileage(mileage);
        progressDialog = ProgressDialog.show(getActivity(), getResources().getString(R.string.wait), getResources().getString(R.string.addcar_progress));
        car.saveEventually(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                progressDialog.dismiss();
                if (e == null) {
                    disableAll();
                    Toast.makeText(context, "New car successfully added!", Toast.LENGTH_LONG).show();
                    mCallback.syncOwnedCars();
                    getActivity().getSupportFragmentManager().popBackStack();
                } else {
                    Alerter.createErrorAlert(e, context).show();
                }
            }
        });
    }

    private enum SpinnerType {
        maker, model, engine, year, gear, fuel;

        static SpinnerType fromSpinner(Spinner spinner, final AddCarFragment addCarFragment) {
            if (spinner == addCarFragment.maker) {
                return maker;
            } else if (spinner == addCarFragment.model) {
                return model;
            } else if (spinner == addCarFragment.engine) {
                return engine;
            } else if (spinner == addCarFragment.year) {
                return year;
            } else if (spinner == addCarFragment.gear) {
                return gear;
            } else if (spinner == addCarFragment.fuel) {
                return fuel;
            } else {
                return null;
            }
        }

        Spinner toSpinner(AddCarFragment addCarActivity) {
            switch (this) {
                case maker:
                    return addCarActivity.maker;
                case model:
                    return addCarActivity.model;
                case engine:
                    return addCarActivity.engine;
                case year:
                    return addCarActivity.year;
                case gear:
                    return addCarActivity.gear;
                case fuel:
                    return addCarActivity.fuel;
                default:
                    return null;
            }
        }
    }
}
