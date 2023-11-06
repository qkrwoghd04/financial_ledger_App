package com.example.myapplication;

import static java.security.AccessController.getContext;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity {

    //선 그래프
    private LineChart lineChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        lineChart = (LineChart) findViewById(R.id.chart);
        configureChartAppearance(lineChart);
        prepareChartData(createChartData(),lineChart);
        MyMarkerView mv = new MyMarkerView(getApplicationContext(), R.layout.custom_marker_view);
        mv.setChartView(lineChart);
        lineChart.setMarker(mv);
    }

    private void configureChartAppearance(LineChart lineChart){
        // x axis design
        // Customization for X-axis
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false); // No grid lines
        xAxis.setDrawAxisLine(false); // No axis line
        xAxis.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD)); // Bold labels
        xAxis.setSpaceMin(0.3f);
        xAxis.setSpaceMax(0.3f);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(new String[]{"S", "M", "T", "W", "T", "F", "S"}));

        // y axis design
        YAxis yAxisLeft = lineChart.getAxisLeft();
        yAxisLeft.setDrawGridLines(false); // Keep the grid lines
        yAxisLeft.enableAxisLineDashedLine(10f, 10f, 0f);
        yAxisLeft.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD)); // Bold labels
        yAxisLeft.setGranularity(20f); // Set interval to 20
        yAxisLeft.setAxisMinimum(0f); // Set the minimum value
        yAxisLeft.setSpaceMin(0.3f);

        YAxis yAxisRight = lineChart.getAxisRight();
        yAxisRight.setEnabled(false); // 오른쪽 Y축 비활성화
    }
    private LineData createChartData(){
        ArrayList<Entry> entry_chart1 = new ArrayList<>(); // 데이터를 담을 Arraylist

        LineData chartData = new LineData(); // 차트에 담길 데이터

        entry_chart1.add(new Entry(0, 98)); //entry_chart1에 좌표 데이터를 담는다.
        entry_chart1.add(new Entry(1, 21));
        entry_chart1.add(new Entry(2, 86));
        entry_chart1.add(new Entry(3, 122));
        entry_chart1.add(new Entry(4, 8));
        entry_chart1.add(new Entry(5, 46));
        entry_chart1.add(new Entry(6, 41));

        LineDataSet lineDataSet1 = new LineDataSet(entry_chart1, "This Week"); // 데이터가 담긴 Arraylist 를 LineDataSet 으로 변환한다.
//        LineDataSet lineDataSet2 = new LineDataSet(entry_chart2, "Last Week");

        lineDataSet1.setColor(Color.BLACK); // 해당 LineDataSet의 색 설정 :: 각 Line 과 관련된 세팅은 여기서 설정한다.
//        lineDataSet2.setColor(Color.BLUE);
        lineDataSet1.setDrawValues(false);

        lineDataSet1.setLineWidth(3.5f); // 라인의 두께 설정
//        lineDataSet2.setLineWidth(2.5f);

        chartData.addDataSet(lineDataSet1); // 해당 LineDataSet 을 적용될 차트에 들어갈 DataSet 에 넣는다.
//        chartData.addDataSet(lineDataSet2);

        lineChart.setTouchEnabled(true); // 차트 터치 disable
        lineChart.getDescription().setEnabled(false); // description text 비활성화
        lineChart.getLegend().setEnabled(true); // legend deactivate

        return chartData;
    }

    private void prepareChartData(LineData data, LineChart lineChart){
        lineChart.setData(data);
        lineChart.invalidate();
    }
}