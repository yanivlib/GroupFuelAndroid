package com.mty.groupfuel;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

import com.mty.groupfuel.datamodel.Car;
import com.mty.groupfuel.datamodel.CarModel;
import com.mty.groupfuel.datamodel.Fuel;
import com.mty.groupfuel.datamodel.Gear;
import com.mty.groupfuel.datamodel.User;
import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class AddCarActivity extends ActionBarActivity {

    private Spinner maker;
    private Spinner model;
    private Spinner engine;
    private Spinner year;
    private Spinner gear;
    private Spinner fuel;
    private EditText number;
    private Button button;
    private CheckBox hybrid;

    private Spinner spinners[];
    private ArrayList<CarModel> modelList;

    private String pleaseSelect;

    public void setModelList(ArrayList<CarModel> modelList) {
        this.modelList = modelList;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_car);
        pleaseSelect = getString(R.string.please_select);
        findViewsById();
        spinners = new Spinner[] {maker, model, engine, year, gear, fuel};
        attachAdapters();
        disableAll();
        getMakers();
        button.setOnClickListener(new AddCarListener());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_car, menu);
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

    private void findViewsById() {
        maker = (Spinner)findViewById(R.id.add_car_maker);
        model = (Spinner)findViewById(R.id.add_car_model);
        engine = (Spinner)findViewById(R.id.add_car_engine);
        year = (Spinner)findViewById(R.id.add_car_year);
        number = (EditText)findViewById(R.id.add_car_number);
        button = (Button)findViewById(R.id.add_car_button);
        hybrid = (CheckBox)findViewById(R.id.add_car_hybrid);
        gear = (Spinner)findViewById(R.id.add_car_gear);
        fuel = (Spinner)findViewById(R.id.add_car_fuel);
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

    private void updateSpinnerList(Spinner spinner, ArrayList<Object> array) {
        ArrayAdapter<Object> adapter = (ArrayAdapter<Object>)spinner.getAdapter();
        adapter.clear();
        adapter.add(pleaseSelect);
        adapter.addAll(array);
        spinner.setSelection(0);
        spinner.setEnabled(true);
        disableFollowing(spinner);
    }

    private void attachAdapters() {
        for (Spinner spinner : spinners) {
            ArrayList<String> array = new ArrayList<>();
            array.add(pleaseSelect);
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, array);
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

    private void getMakers () {
        ParseCloud.callFunctionInBackground(Consts.GET_CAR_MAKES, new HashMap<String, Object>(), new FunctionCallback<ArrayList<Object>>() {
            @Override
            public void done(ArrayList<Object> result, ParseException e) {
                System.out.println("getmakers print");
                if (e == null) {
                    updateSpinnerList(maker, result);
                } else {
                    switch (e.getCode()) {
                        case 141:
                            System.out.println(e.getMessage());
                            break;
                        default:
                            throw new RuntimeException(e.getMessage());
                    }
                }
            }
        });
    }

    private void getModels (String make) {
        System.out.println("getModels started");
        final HashMap<String, Object> params = new HashMap<>();
        params.put(Consts.PARAMS_MAKE, make);
        ParseCloud.callFunctionInBackground(Consts.GET_CAR_MODELS, params, new FunctionCallback<HashMap>() {
            @Override
            public void done(HashMap result, ParseException e) {
                System.out.println("getModels returned");
                if (e == null) {
                    System.out.println(result.toString());
                    ArrayList<CarModel> resultSet = (ArrayList<CarModel>) result.get("resultSet");
                    ArrayList<Object> distinctModels = (ArrayList<Object>) result.get("distinctModels");
                    setModelList(resultSet);
                    updateSpinnerList(model, distinctModels);
                } else {
                    switch (e.getCode()) {
                        case 141:
                            System.out.println(e.getMessage());
                            break;
                        default:
                            throw new RuntimeException(e.getMessage());
                    }
                }
            }
        });
    }

    private enum SpinnerType {
        maker, model, engine, year, gear, fuel;

        Spinner toSpinner(AddCarActivity addCarActivity) {
            switch (this) {
                case maker: return addCarActivity.maker;
                case model: return addCarActivity.model;
                case engine: return addCarActivity.engine;
                case year: return addCarActivity.year;
                case gear: return addCarActivity.gear;
                case fuel: return addCarActivity.fuel;
                default: return null;
            }
        }

        static SpinnerType fromSpinner(Spinner spinner, final AddCarActivity addCarActivity) {
            if (spinner == addCarActivity.maker) {
                return maker;
            } else if (spinner == addCarActivity.model) {
                return model;
            } else if (spinner == addCarActivity.engine) {
                return engine;
            } else if (spinner == addCarActivity.year) {
                return year;
            } else if (spinner == addCarActivity.gear) {
                return gear;
            } else if (spinner == addCarActivity.fuel) {
                return fuel;
            } else {
                return null;
            }
        }
    }

    private AdapterView.OnItemSelectedListener getListener(final SpinnerType type) {
        return new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                Object value = adapterView.getItemAtPosition(position);
                disableFollowing(type.toSpinner(AddCarActivity.this));
                if (position > 0) {
                    switch (type) {
                        case maker:
                            getModels(value.toString());
                            break;
                        case model:
                            getEngines(value.toString());
                            break;
                        case engine:
                            getYears(value.toString(), model.getSelectedItem().toString());
                            break;
                        case year:
                            getGears(value.toString());
                            break;
                        case gear:
                            getFuels(value.toString());
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

    public void getEngines(String model) {
        ArrayList<Object> engines = new ArrayList<>();
        for (CarModel currentModel : modelList) {
            if (currentModel.getModel().equals(model)) {
                engines.add(currentModel.getVolume());
            }
        }
        updateSpinnerList(engine, engines);
    }

    public void getYears(String engine, String model) {
        ArrayList<Object> years = new ArrayList<>();
        for (CarModel currentModel : modelList) {
            if (currentModel.getModel().equals(model) && currentModel.getVolume().toString().equals(engine)) {
                years.add(currentModel.getYear());
            }
        }
        updateSpinnerList(year, years);
    }

    public void getFuels(String model) {
        ArrayList<Object> fuels = new ArrayList<>();
        fuels.addAll(Arrays.asList(Fuel.values()));
        updateSpinnerList(fuel, fuels);
    }

    public void getGears(String model) {
        ArrayList<Object> gears = new ArrayList<>();
        gears.addAll(Arrays.asList(Gear.values()));
        updateSpinnerList(gear, gears);
    }

    private class AddCarListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            CarModel carModel = new CarModel();
            carModel.setMake((String) maker.getSelectedItem());
            carModel.setModel((String) maker.getSelectedItem());
            carModel.setVolume((Number) engine.getSelectedItem());
            carModel.setYear((Number) year.getSelectedItem());
            carModel.setGear((Gear) gear.getSelectedItem());
            carModel.setHybrid(hybrid.isChecked());
            carModel.setFuel((Fuel) fuel.getSelectedItem());

            Car car = new Car();
            car.setModel(carModel);
            car.setOwner((User) ParseUser.getCurrentUser());
            car.setCarNumber(number.getText().toString().trim());
            car.setMileage(0);

            car.saveEventually(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        disableAll();
                    } else {
                        throw new RuntimeException(e.getMessage());
                    }
                }
            });
        }
    }
}
