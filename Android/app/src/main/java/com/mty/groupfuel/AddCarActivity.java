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
import android.widget.Toast;

import com.mty.groupfuel.datamodel.Car;
import com.parse.FunctionCallback;
import com.parse.Parse;
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

    private Button button;

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

    private class SpinnerSelect implements AdapterView.OnItemSelectedListener {
        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {
            //
        }

        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
            String item = adapterView.getItemAtPosition(position).toString();
            Toast.makeText(adapterView.getContext(), "Selected: " + item, Toast.LENGTH_LONG).show();
            if (maker.getSelectedItem() != null) {
                getModels(maker.getSelectedItem().toString());
            }
        }

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
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    private void attachAdapters() {
        for (Spinner spinner : spinners) {
            updateSpinnerList(spinner, new String[0]);
            spinner.setOnItemSelectedListener(new SpinnerSelect());
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
        ParseCloud.callFunctionInBackground(Consts.GET_CAR_MODELS, params, new FunctionCallback<ArrayList>() {
            @Override
            public void done(ArrayList result, ParseException e) {
                if (e == null) {
                    System.out.println(result.toString());
                    String[] models = Arrays.copyOf(result.toArray(), result.size(), String[].class);
                    updateSpinnerList(model, models);
                    model.setEnabled(true);
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
}
