package com.example.myapplication.fomatter;

import android.icu.text.SimpleDateFormat;

import com.github.mikephil.charting.formatter.ValueFormatter;

import java.util.Calendar;
import java.util.Locale;

public class DayAxisValueFormatter extends ValueFormatter {
    private final SimpleDateFormat sdf = new SimpleDateFormat("MM/dd", Locale.getDefault());

    @Override
    public String getFormattedValue(float value) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, (int) value);
        return sdf.format(calendar.getTime());
    }
}
