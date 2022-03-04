package com.example.fooddetection;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    public static MainActivity mainActivity;
    private static final String calorieInfoFile = "calorie_info.txt";
    private static HashMap<String, Integer> calorieInfo = new HashMap<>();
    private PieChart pieChart;
    ArrayList<PieEntry> entries;
    ArrayList<FoodModal> foodModalArrayList;
    FloatingActionButton graphFab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainActivity = this;
        try {
            calorieInfo = loadCalorieInfo(calorieInfoFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        FloatingActionButton fab = findViewById(R.id.fab);
        graphFab = findViewById(R.id.fabGraph);
        pieChart = findViewById(R.id.graph);
        entries = new ArrayList<>();
        foodModalArrayList = new ArrayList<>();
        setupPieChart();
        loadPieChart();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), DetectorActivity.class));
            }
        });
        graphFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, GraphActivity.class);
                Log.e("TAG","FOOD LIST IS "+foodModalArrayList);
                i.putExtra("foodlist", foodModalArrayList);
                startActivity(i);
            }
        });
        ImageView logoutBtn = findViewById(R.id.idIVLogOut);
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                firebaseAuth.signOut();
                Intent i = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(i);
                finish();
            }
        });
    }

    public static MainActivity getInstance() {
        return mainActivity;
    }

    public void addFood(String food) {
        int calories = getCalorie(food);
        TextView total = (TextView) findViewById(R.id.total);
        TextView progressBar_total = (TextView) findViewById(R.id.progressBar_total);
        int cur = Integer.parseInt(total.getText().toString());
        cur += calories;
        total.setText(cur + "");
        progressBar_total.setText("Current calories: " + cur + "");
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar3);
        progressBar.setProgress(cur);
        TableLayout t1 = (TableLayout) findViewById(R.id.tablelayout);
        TableRow tr = new TableRow(this);
        tr.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT));
        TextView date = new TextView(this);
        TextView foodItem = new TextView(this);
        TextView cals = new TextView(this);

        long tsLong = (long) (System.currentTimeMillis() / 1000);
        java.util.Date d = new java.util.Date(tsLong * 1000L);
        String ts = new SimpleDateFormat("h:mm a").format(d);
        date.setText(ts);
        foodItem.setText(food);
        cals.setText(calories + "");
        date.setGravity(Gravity.CENTER);
        foodItem.setGravity(Gravity.CENTER);
        cals.setGravity(Gravity.CENTER);

        date.setLayoutParams(new TableRow.LayoutParams(0));
        foodItem.setLayoutParams(new TableRow.LayoutParams(1));
        cals.setLayoutParams(new TableRow.LayoutParams(2));
        date.getLayoutParams().width = 0;
        foodItem.getLayoutParams().width = 0;
        cals.getLayoutParams().width = 0;
        tr.addView(date);
        tr.addView(foodItem);
        tr.addView(cals);
        t1.addView(tr);

        entries.add(new PieEntry(calories, food));
        foodModalArrayList.add(new FoodModal(food, calories));
        graphFab.setVisibility(View.VISIBLE);
    }

    private void setupPieChart() {
        pieChart.setDrawHoleEnabled(true);
        pieChart.setUsePercentValues(true);
        pieChart.setEntryLabelTextSize(12);
        pieChart.setEntryLabelColor(Color.BLACK);
        pieChart.setCenterText("Spending by Category");
        pieChart.setCenterTextSize(24);
        pieChart.getDescription().setEnabled(false);

        Legend l = pieChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setDrawInside(false);
        l.setEnabled(true);
    }

    private void loadPieChart() {

//        entries.add(new PieEntry(0.2f, "Food & Dining"));
//        entries.add(new PieEntry(0.15f, "Medical"));
//        entries.add(new PieEntry(0.10f, "Entertainment"));
//        entries.add(new PieEntry(0.25f, "Electricity and Gas"));
//        entries.add(new PieEntry(0.3f, "Housing"));


        ArrayList<Integer> colors = new ArrayList<>();
        for (int color : ColorTemplate.MATERIAL_COLORS) {
            colors.add(color);
        }

        for (int color : ColorTemplate.VORDIPLOM_COLORS) {
            colors.add(color);
        }
        PieDataSet dataSet = new PieDataSet(entries, "Expense Category");
        dataSet.setColors(colors);


        PieData data = new PieData(dataSet);
        data.setDrawValues(true);
        data.setValueFormatter(new PercentFormatter(pieChart));
        data.setValueTextSize(12f);
        data.setValueTextColor(Color.BLACK);

        pieChart.setData(data);
        pieChart.invalidate();

        pieChart.animateY(1400, Easing.EaseInOutQuad);
    }

    public int getCalorie(String food) {
        return calorieInfo.get(food);
    }

    private HashMap loadCalorieInfo(String filename) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(getAssets().open(filename)));
        HashMap<String, Integer> calCounts = new HashMap<>();

        String line;
        while ((line = reader.readLine()) != null) {
            calCounts.put(line.split(": ")[0], Integer.parseInt(line.split(": ")[1]));
        }
        return calCounts;
    }
}