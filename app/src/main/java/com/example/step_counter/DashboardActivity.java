package com.example.step_counter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

public class DashboardActivity extends AppCompatActivity implements subscribeSteps {

    TextView Header;
    TextView textView1,textView2,textView3;
    MyDbHelper db;
    CombinedChart barChart;
    ArrayList<Entry> Entries;
    BarData barData;
    BarDataSet barDataSet;
    ArrayList<BarEntry> barEntries;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_dashboard);

        Header=findViewById(R.id.header_text);
        Header.setText("Step Counter");

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION)== PackageManager.PERMISSION_DENIED){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                requestPermissions(new String[]{Manifest.permission.ACTIVITY_RECOGNITION},10);
            }
        }
        barChart = (CombinedChart)findViewById(R.id.barchart);

        textView1 = (TextView) findViewById(R.id.TV_STEPS);
        textView2 = (TextView) findViewById(R.id.TV_CALORIES);
        textView3 = (TextView) findViewById(R.id.TV_DISTANCE);


        Intent intent = new Intent(this, StepDetectorService.class);
        startService(intent);

        StepDetectorService.subscribe.register(this);
        db = new MyDbHelper(this);
        Cursor cursor = db.allData();
        if (cursor == null) {
            Toast.makeText(getApplicationContext(),"NO DATA",Toast.LENGTH_SHORT).show();
        }
        addDataToGraph();
        barChart.invalidate();
    }

    @Override
    public void subscribeSteps(int steps) {
        textView1.setText(String.valueOf(steps));
        textView2.setText(String.valueOf (steps * 0.045));
        int feet = (int) (steps * 2.5);
        double distance = feet/3.281;
        @SuppressLint("DefaultLocale") String finalDistance= String.format("%.2f", distance);
        textView3.setText(finalDistance);
    }

    public void addDataToGraph(){
        db = new MyDbHelper(this);
        Entries = new ArrayList<>();
        Entries.add(new Entry(0,0));
        barEntries = new ArrayList<>();
        ArrayList<String> labels=new ArrayList<>();
        final ArrayList<String> yData = db.queryYData();

        for(int i=1; i<db.queryYData().size(); i++){
            BarEntry barEntry = new BarEntry(i, Integer.parseInt(db.queryYData().get(i)));
            if(Integer.parseInt(db.queryYData().get(i))>0){
                Entries.add(barEntry);
                barEntries.add(barEntry);
                System.out.println(barEntry);
            }
        }

        final ArrayList<String> xData = db.queryXData();

        for(int i=1; i<db.queryXData().size(); i++){
            if(!labels.contains(xData.get(i))){
                labels.add(xData.get(i));
                System.out.println(xData.get(i));
            }
        }

        Entries.add(new Entry(Entries.size()+1,0));
        Entries.add(new Entry(Entries.size()+2,0));
        Entries.add(new Entry(Entries.size()+3,0));
        LineDataSet set = new LineDataSet(Entries, "");
        set.setDrawValues(false);
        barDataSet = new BarDataSet(barEntries, "Data Set");
        barData = new BarData(barDataSet);
        set.setFillAlpha(110);
        set.setColor(Color.RED);
        set.setLineWidth(3f);
        set.setValueTextColor(Color.GREEN);
        ArrayList<ILineDataSet> dataSets=new ArrayList<>();
        dataSets.add(set);
        LineData lineData=new LineData(dataSets);
        CombinedData data=new CombinedData();
        data.setData(barData);
        data.setData(lineData);
        barChart.setData(data);
        XAxis xAxis=barChart.getXAxis();
        labels.add(0,"");
        barChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labels));
        xAxis.setGranularity(1);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        barDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        barDataSet.setValueTextColor(Color.BLACK);
        barDataSet.setValueTextSize(16f);
        barChart.animateY(2000);
    }
}