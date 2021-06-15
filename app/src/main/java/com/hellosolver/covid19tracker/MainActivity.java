package com.hellosolver.covid19tracker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.hbb20.CountryCodePicker;

import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    CountryCodePicker countryCodePicker;
    TextView mTodayTotal,mTotal,mActive,mTodayActive,mRecovered,mTodayRecovered,mDeaths,mTodayDeaths;
    String country;
    TextView mFilter;
    Spinner spinner;
    String[] types={"cases","deaths","recovered","active"};
    private List<ModelClass> modelClassList;
    private List<ModelClass>modelClassList2;
    PieChart mpieChart;
    private RecyclerView recyclerView;
    com.hellosolver.covid19tracker.Adapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        countryCodePicker=findViewById(R.id.ccp);
        mTodayActive=findViewById(R.id.todayactive);
        mActive=findViewById(R.id.activecase);
        mDeaths=findViewById(R.id.totaldeaths);
        mTodayDeaths=findViewById(R.id.todaydeaths);
        mRecovered=findViewById(R.id.recoveredcase);
        mTodayRecovered=findViewById(R.id.todayrecovered);
        mTotal=findViewById(R.id.totalcase);
        mpieChart=findViewById(R.id.pichart);
        mTodayTotal=findViewById(R.id.todaytotal);
        spinner=findViewById(R.id.spinner);
        mFilter=findViewById(R.id.filter);
        recyclerView=findViewById(R.id.recycler_view);
        modelClassList=new ArrayList<>();
        modelClassList2=new ArrayList<>();

        spinner.setOnItemSelectedListener(this);
        ArrayAdapter arrayAdapter=new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item,types);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(arrayAdapter);

        ApiUtilities.getAPIInterface().getcountrydata().enqueue(new Callback<List<ModelClass>>() {
            @Override
            public void onResponse(Call<List<ModelClass>> call, Response<List<ModelClass>> response) {
                modelClassList2.addAll(response.body());
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<List<ModelClass>> call, Throwable t) {

            }
        });
        adapter=new Adapter(getApplicationContext(),modelClassList2);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);

        countryCodePicker.setAutoDetectedCountry(true);
        country=countryCodePicker.getSelectedCountryName();
        countryCodePicker.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {
            @Override
            public void onCountrySelected() {
                country=countryCodePicker.getSelectedCountryName();
                fetchData();
            }
        });
        fetchData();

    }

    private void fetchData() {
        ApiUtilities.getAPIInterface().getcountrydata().enqueue(new Callback<List<ModelClass>>() {
            @Override
            public void onResponse(Call<List<ModelClass>> call, Response<List<ModelClass>> response) {
                modelClassList.addAll(response.body());
                for(int i=0;i<modelClassList.size();i++){
                    if(modelClassList.get(i).getCountry().equals(country)){
                        mTodayActive.setText((modelClassList.get(i).getActive()));
                        mTodayDeaths.setText((modelClassList.get(i).getTodayDeaths()));
                        mTodayRecovered.setText((modelClassList.get(i).getTodayRecovered()));
                        mTodayTotal.setText((modelClassList.get(i).getTodayCases()));
                        mTotal.setText((modelClassList.get(i).getCases()));
                        mDeaths.setText((modelClassList.get(i).getDeaths()));
                        mActive.setText((modelClassList.get(i).getActive()));
                        mRecovered.setText((modelClassList.get(i).getRecovered()));

                        int active,total,recovered,deaths;

                        active=Integer.parseInt(modelClassList.get(i).getActive());
                        total=Integer.parseInt(modelClassList.get(i).getCases());
                        recovered=Integer.parseInt(modelClassList.get(i).getRecovered());
                        deaths=Integer.parseInt(modelClassList.get(i).getDeaths());
                        updateGraph(active,total,recovered,deaths);

                    }
                }
            }

            @Override
            public void onFailure(Call<List<ModelClass>> call, Throwable t) {

            }
        });
    }

    private void updateGraph(int active, int total, int recovered, int deaths) {
        mpieChart.clearChart();
        mpieChart.addPieSlice(new PieModel("Confirm",total, Color.parseColor("#FFB701")));
        mpieChart.addPieSlice(new PieModel("Active",total, Color.parseColor("#FF4CAF50")));
        mpieChart.addPieSlice(new PieModel("Recovered",total, Color.parseColor("#38ACCD")));
        mpieChart.addPieSlice(new PieModel("Deaths",total, Color.parseColor("#F55C47")));
        mpieChart.startAnimation();
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        String item=types[position];
        mFilter.setText(item);
        adapter.filter(item);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}