package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.myapplication.fragment.AddEntryFragment;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class HomeActivity extends AppCompatActivity {

    //선 그래프
    private LineChart lineChart;
    private TextView textViewDateRange;
    private Calendar currentWeekStart;
    private Calendar currentWeekEnd;
    private SimpleDateFormat dateFormat;
    private ImageButton buttonPreviousWeek;
    private ImageButton buttonNextWeek;
    private ImageButton btAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        btAdd = findViewById(R.id.bt_add);
        lineChart = (LineChart) findViewById(R.id.chart);
        configureChartAppearance(lineChart);
        prepareChartData(createChartData(),lineChart);
        MyMarkerView mv = new MyMarkerView(getApplicationContext(), R.layout.custom_marker_view);
        mv.setChartView(lineChart);
        lineChart.setMarker(mv);
        buttonPreviousWeek = (ImageButton) findViewById(R.id.bt_prev);
        buttonNextWeek = (ImageButton) findViewById(R.id.bt_next);
        textViewDateRange = findViewById(R.id.tv_calender);
        dateFormat = new SimpleDateFormat("MM-dd", Locale.getDefault());

        // set the start & end of a week
        currentWeekStart = Calendar.getInstance();
        currentWeekEnd = (Calendar) currentWeekStart.clone();

        currentWeekStart.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        currentWeekEnd.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);

        updateDateRange();


        buttonPreviousWeek.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPreviousWeekData();
            }
        });


        buttonNextWeek.setVisibility(View.GONE);
        buttonNextWeek.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNextWeekData();
            }
        });
        btAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddEntryFragment();
            }
        });

    }

    private void showAddEntryFragment() {
        setContentView(R.layout.fragment_add_entry);
        AddEntryFragment addEntryFragment = new AddEntryFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, addEntryFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void updateDateRange() {
        String startDate = dateFormat.format(currentWeekStart.getTime());
        String endDate = dateFormat.format(currentWeekEnd.getTime());
        textViewDateRange.setText(String.format("%s ~ %s", startDate, endDate));

        // 현재 날짜가 현재 주를 넘지 않는 경우 다음 주 버튼 숨기기
        Calendar today = Calendar.getInstance();
        if (today.get(Calendar.YEAR) == currentWeekStart.get(Calendar.YEAR) &&
                today.get(Calendar.WEEK_OF_YEAR) == currentWeekStart.get(Calendar.WEEK_OF_YEAR)) {
            buttonNextWeek.setVisibility(View.GONE);
        } else {
            buttonNextWeek.setVisibility(View.VISIBLE);
        }
    }

    private void showPreviousWeekData() {
        // 한 주를 뺀다
        currentWeekStart.add(Calendar.WEEK_OF_YEAR, -1);
        currentWeekEnd.add(Calendar.WEEK_OF_YEAR, -1);

        // update
        updateDateRange();

        // 이전 주 데이터 로드 및 차트 데이터 설정
        prepareChartData(createChartData(), lineChart);

        // 버튼 상태 갱신
        buttonNextWeek.setVisibility(View.VISIBLE);
    }

    private void showNextWeekData() {

        currentWeekStart.add(Calendar.WEEK_OF_YEAR, 1);
        currentWeekEnd.add(Calendar.WEEK_OF_YEAR, 1);

        updateDateRange();

        prepareChartData(createChartData(), lineChart);
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
        yAxisRight.setEnabled(false);
    }
    private LineData createChartData(){
        ArrayList<Entry> entry_chart1 = new ArrayList<>();

        LineData chartData = new LineData(); // The data in the line chart

        // Sunday to Saturday
        entry_chart1.add(new Entry(0, 98));
        entry_chart1.add(new Entry(1, 21));
        entry_chart1.add(new Entry(2, 86));
        entry_chart1.add(new Entry(3, 122));
        entry_chart1.add(new Entry(4, 8));
        entry_chart1.add(new Entry(5, 46));
        entry_chart1.add(new Entry(6, 41));

        LineDataSet lineDataSet1 = new LineDataSet(entry_chart1, "This Week"); // Arraylist convert to LineDataSet
//        LineDataSet lineDataSet2 = new LineDataSet(entry_chart2, "Last Week");

        lineDataSet1.setColor(Color.BLACK);
//        lineDataSet2.setColor(Color.BLUE);
        lineDataSet1.setDrawValues(false);

        lineDataSet1.setLineWidth(3.5f);
//        lineDataSet2.setLineWidth(2.5f);

        chartData.addDataSet(lineDataSet1); // LineDataSet add in chartData
//        chartData.addDataSet(lineDataSet2);

        lineChart.setTouchEnabled(true); // chart touch disable
        lineChart.getDescription().setEnabled(false); // description text deactivated
        lineChart.getLegend().setEnabled(true); // legend deactivate

        return chartData;
    }

    private void prepareChartData(LineData data, LineChart lineChart){
        lineChart.setData(data);
        lineChart.invalidate();
    }
}