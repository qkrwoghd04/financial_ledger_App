package com.example.myapplication.fragment;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.myapplication.R;
import com.example.myapplication.fomatter.DayAxisValueFormatter;
import com.example.myapplication.jsp.DatabaseHelper;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class StatisticFragment extends Fragment {

    private LineChart lineChart;
    private TextView textViewDateRange;
    private Calendar currentWeekStart;
    private Calendar currentWeekEnd;
    private SimpleDateFormat dateFormat;
    private ImageButton buttonPreviousWeek;
    private ImageButton buttonNextWeek;
    private DatabaseHelper databaseHelper;
    private String currentUser;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault());



    public StatisticFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: 시작됨");
        View view = inflater.inflate(R.layout.fragment_statistic, container, false);
        databaseHelper = new DatabaseHelper(getActivity());
        currentUser = getLoggedInUsername();

        lineChart = (LineChart) view.findViewById(R.id.chart);
        prepareChartData(createChartData(),lineChart);

        buttonPreviousWeek = (ImageButton) view.findViewById(R.id.bt_prev);
        buttonNextWeek = (ImageButton) view.findViewById(R.id.bt_next);
        textViewDateRange = view.findViewById(R.id.tv_weekly);
        dateFormat = new SimpleDateFormat("MM/dd", Locale.getDefault());

        // set the start & end of a week
        currentWeekStart = Calendar.getInstance();
        currentWeekEnd = (Calendar) currentWeekStart.clone();

        currentWeekStart.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        currentWeekEnd.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);

        updateDateRange();
        prepareChartData(createChartData(), lineChart);


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


        return view;
    }
    private void updateDateRange() {
        Log.d(TAG, "updateDateRange: 시작됨");
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
        xAxis.setValueFormatter(new DayAxisValueFormatter());

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
    private LineData createChartData() {
        configureChartAppearance(lineChart);
        ArrayList<Entry> entriesIncome = new ArrayList<>();
        ArrayList<Entry> entriesExpense = new ArrayList<>();

        Calendar calendar = Calendar.getInstance();


        for (int i = 6; i >= 0; i--) {
            if(calendar != null){
                calendar.add(Calendar.DAY_OF_YEAR, -i);
                String dateStr = sdf.format(calendar.getTime());
                Cursor cursor = databaseHelper.getTransactionsByDate(dateStr, currentUser);

                if (cursor != null && cursor.moveToFirst()) {
                    float totalIncome = 0;
                    float totalExpense = 0;

                    do {
                        int typeIndex = cursor.getColumnIndex("type");
                        int amountIndex = cursor.getColumnIndex("amount");

                        if (typeIndex != -1 && amountIndex != -1) {
                            String type = cursor.getString(typeIndex);
                            float amount = cursor.getFloat(amountIndex);

                            if ("Income".equals(type)) {
                                totalIncome += amount;
                            } else if ("Expense".equals(type)) {
                                totalExpense += amount;
                            }
                        }
                    } while (cursor.moveToNext());
                    cursor.close();

                    entriesIncome.add(new Entry(6 - i, totalIncome));
                    entriesExpense.add(new Entry(6 - i, totalExpense));
                }
                calendar.add(Calendar.DAY_OF_YEAR, i);
            }
        }
        LineDataSet dataSetIncome = new LineDataSet(entriesIncome, "Income");
        dataSetIncome.setColor(Color.GREEN);
        LineDataSet dataSetExpense = new LineDataSet(entriesExpense, "Expense");
        dataSetExpense.setColor(Color.RED);

        LineData lineData = new LineData(dataSetIncome, dataSetExpense);
        return lineData;
    }


    private void prepareChartData(LineData data, LineChart lineChart){
        lineChart.setData(data);
        lineChart.invalidate();
    }
    private String getLoggedInUsername() {
        if (getActivity() != null) {
            SharedPreferences sharedPreferences = getActivity().getSharedPreferences("shared_pref", Context.MODE_PRIVATE);
            return sharedPreferences.getString("username", null);
        }
        return null; // 또는 기본값
    }
}