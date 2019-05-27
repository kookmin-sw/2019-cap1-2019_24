package com.example.WITTYPHOTOS;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

public class PieGraph extends AppCompatActivity {

    float countNum[] = {78f, 23f, 11f, 65f, 33f};
    String tagName[] = {"tag1", "tag2", "tag3", "tag4", "tag5"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph_dir);

        setupPieChart();
    }
    private void setupPieChart(){

        //Populating a list of PieEntries
        List<PieEntry> pieEntries = new ArrayList<PieEntry>(){};
        for (int i = 0; i<countNum.length; i++){
            pieEntries.add(new PieEntry(countNum[i],tagName[i]));
        }

        PieDataSet dataSet = new PieDataSet(pieEntries, "");


        // 색상
        dataSet.setColors(ColorTemplate.PASTEL_COLORS);

        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(10f);


        PieData data = new PieData(dataSet);

        //Get a chart
        PieChart chart = (PieChart)findViewById(R.id.piechart);

        Description description = new Description();
        description.setText("TAG 통계 - 상위"+countNum.length+"개 ");
        description.setTextSize(15);
        chart.setDescription(description);

        chart.animateY(1000, Easing.EasingOption.EaseInOutCubic);

        chart.invalidate();

        data.setValueTextSize(10f);
        data.setValueTextColor(Color.BLACK);
        chart.setData(data);

    }
}