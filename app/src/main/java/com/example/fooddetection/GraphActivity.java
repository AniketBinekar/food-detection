package com.example.fooddetection;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

public class GraphActivity extends AppCompatActivity {
    private PieChart pieChart;
    ArrayList<PieEntry> entries;
    ArrayList<FoodModal> foodModalArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);
        entries = new ArrayList<>();
        foodModalArrayList = new ArrayList<>();
        pieChart = findViewById(R.id.graph);
        foodModalArrayList = (ArrayList<FoodModal>) getIntent().getSerializableExtra("foodlist");
        for (int i = 0; i < foodModalArrayList.size(); i++) {
            entries.add(new PieEntry(foodModalArrayList.get(i).getCalories(), foodModalArrayList.get(i).getFoodName()));
        }
        setupPieChart();
        loadPieChart();
    }

    private void setupPieChart() {
        pieChart.setDrawHoleEnabled(true);
        pieChart.setUsePercentValues(true);
        pieChart.setEntryLabelTextSize(12);
        pieChart.setEntryLabelColor(Color.BLACK);
        pieChart.setCenterText("Calorie Estimation");
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

        Log.e("TAG", "ENTRIES IS " + entries.toString());

//        entries.add(new PieEntry(354, "Food & Dining"));
//        entries.add(new PieEntry(200, "Food"));
//        entries.add(new PieEntry(120, "Dining"));
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
        PieDataSet dataSet = new PieDataSet(entries, "Food Category");
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
}