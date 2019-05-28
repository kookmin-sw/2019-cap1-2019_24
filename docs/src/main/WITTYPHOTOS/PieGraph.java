package com.example.WITTYPHOTOS;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

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
    private ImageView next;
    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph_dir);

        setupPieChart();

        next = findViewById(R.id.nextbutton);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(PieGraph.this, NetworkGraph.class));
            }
        });
    }
    private void setupPieChart(){
        ArrayList<Float> countNum = new ArrayList<>();
        ArrayList<String> tagName = new ArrayList<>();
        db = App.mDB.getWritableDatabase();
        String sql = "SELECT * FROM Total_Tag_Info ORDER BY tag_freq DESC";
        Cursor cursor;
        cursor = db.rawQuery(sql, null);
        int tmp = 0;
        while(cursor.moveToNext()&&tmp<5){
            String name = cursor.getString(2);
            float c = ((Number)cursor.getInt(4)).floatValue();
            tagName.add(name);
            countNum.add(c);
            tmp++;
        }



        //Populating a list of PieEntries
        List<PieEntry> pieEntries = new ArrayList<PieEntry>(){};
        for (int i = 0; i<countNum.size(); i++){
            pieEntries.add(new PieEntry(countNum.get(i),tagName.get(i)));
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
        description.setText("TAG 통계 - 상위"+countNum.size()+"개 ");
        description.setTextSize(15);
        chart.setDescription(description);

        chart.animateY(1000, Easing.EasingOption.EaseInOutCubic);

        chart.invalidate();

        data.setValueTextSize(10f);
        data.setValueTextColor(Color.BLACK);
        chart.setData(data);

    }
}