package com.mty.groupfuel;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.mty.groupfuel.datamodel.CarModel;
import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;


public class AddCarActivity extends ActionBarActivity {

    private Spinner maker;
    private Spinner model;
    private Spinner engine;
    private Spinner year;

    private Spinner spinners[];
    private ArrayList<CarModel> modelList;

    private Button button;

    public void setModelList(ArrayList<CarModel> modelList) {
        this.modelList = modelList;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_car);
        findViewsById();
        spinners = new Spinner[] {maker, model, engine, year};
        attachAdapters();
        disableAll();
        getMakers();
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
        button = (Button)findViewById(R.id.add_car_button);
    }

    private void updateSpinnerList(Spinner spinner, String[] array) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, array);
        //new CustomSpinnerAdapter(this, R.layout.spinner_row, arrayForSpinner, defaultTextForSpinner);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    private void attachAdapters() {
        for (Spinner spinner : spinners) {
            updateSpinnerList(spinner, new String[0]);
            if (spinner == maker) {
                spinner.setOnItemSelectedListener(new makerSelectedListener());
            } else if (spinner == model) {
                spinner.setOnItemSelectedListener(new modelSelectedListener());
            } else if (spinner == engine) {
                spinner.setOnItemSelectedListener(new engineSelectedListener());
            } else {
                spinner.setOnItemSelectedListener(new yearSelectedListener());
            }
        }
    }

    private void disableAll() {
        for (Spinner spinner : spinners) {
            spinner.setEnabled(false);
        }
    }

    private void getMakers () {
        ParseCloud.callFunctionInBackground(Consts.GET_CAR_MAKES, new HashMap<String, Object>(), new FunctionCallback<ArrayList>() {
            @Override
            public void done(ArrayList result, ParseException e) {
                if (e == null) {
                    String[] makes = Arrays.copyOf(result.toArray(), result.size(), String[].class);
                    updateSpinnerList(maker, makes);
                    maker.setEnabled(true);
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
        final HashMap<String, Object> params = new HashMap<>();
        params.put(Consts.PARAMS_MAKE, make);
        ParseCloud.callFunctionInBackground(Consts.GET_CAR_MODELS, params, new FunctionCallback<HashMap>() {
            @Override
            public void done(HashMap result, ParseException e) {
                if (e == null) {
                    System.out.println(result.toString());
                    ArrayList<CarModel> resultSet = (ArrayList<CarModel>)result.get("resultSet");
                    ArrayList<String> distinctModels = (ArrayList<String>)result.get("distinctModels");
                    String[] models = new String[distinctModels.size()];
                    models = distinctModels.toArray(models);
                    setModelList(resultSet);
                    updateSpinnerList(model, models);
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

    private class makerSelectedListener implements AdapterView.OnItemSelectedListener {
        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {
            //
        }

        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
            String maker = adapterView.getItemAtPosition(position).toString();
            getModels(maker);
            model.setEnabled(true);
        }
    }

    private class modelSelectedListener implements AdapterView.OnItemSelectedListener {
        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {
            //
        }

        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
            String model = adapterView.getItemAtPosition(position).toString();
            getEngines(model);
            engine.setEnabled(true);
        }
    }

    private class engineSelectedListener implements AdapterView.OnItemSelectedListener {
        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {
            //
        }

        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
            String engine = adapterView.getItemAtPosition(position).toString();
            getYears(engine, model.getSelectedItem().toString());
            year.setEnabled(true);
        }
    }

    private class yearSelectedListener implements AdapterView.OnItemSelectedListener {
        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {
            //
        }

        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
            String year = adapterView.getItemAtPosition(position).toString();
            //
        }
    }

    public void getEngines(String model) {
        ArrayList<String> engines = new ArrayList<>();
        for (CarModel currentModel : modelList) {
            if (currentModel.getModel().equals(model)) {
                engines.add(currentModel.getVolume().toString());
            }
        }
        String[] enginesArray = new String[engines.size()];
        enginesArray = engines.toArray(enginesArray);
        updateSpinnerList(engine, enginesArray);
    }

    public void getYears(String engine, String model) {
        ArrayList<String> years = new ArrayList<>();
        for (CarModel currentModel : modelList) {
            if (currentModel.getModel().equals(model) && currentModel.getVolume().toString().equals(engine)) {
                years.add(currentModel.getYear().toString());
            }
        }
        String[] yearsArray = new String[years.size()];
        yearsArray = years.toArray(yearsArray);
        updateSpinnerList(year, yearsArray);
    }

}
