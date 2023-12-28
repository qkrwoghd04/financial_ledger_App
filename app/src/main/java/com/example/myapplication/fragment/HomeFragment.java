package com.example.myapplication.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;


import com.example.myapplication.R;
import com.example.myapplication.jsp.DatabaseHelper;

import java.util.Calendar;
import java.util.Locale;

public class HomeFragment extends Fragment {

    private DatabaseHelper databaseHelper;
    private TextView incomeTextView, expenseTextView;
    private String currentDate = "";
    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("HomeFragment", "onResume: 시작");

        // 현재 달의 첫날과 마지막 날 계산
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault());
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        String startDate = dateFormat.format(calendar.getTime());
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        String endDate = dateFormat.format(calendar.getTime());

        Log.d("HomeFragment", "조회 기간: " + startDate + " ~ " + endDate);


        Pair<Double, Double> incomeExpense = databaseHelper.getMonthlyIncomeExpense(getLoggedInUsername(), startDate, endDate);
        updateIncomeExpenseUI(incomeExpense.first, incomeExpense.second);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        databaseHelper = new DatabaseHelper(getActivity());

        // Additional initialization if needed

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Initialize TextViews
        incomeTextView = view.findViewById(R.id.income_set_result);
        expenseTextView = view.findViewById(R.id.expense_set_result);

        return view;
    }

    // Method to load transactions for a specific date

    // Method to update UI with the calculated income and expense values
    private void updateIncomeExpenseUI(double income, double expense) {
        incomeTextView.setText(String.format(Locale.getDefault(), "%.2f", income));
        expenseTextView.setText(String.format(Locale.getDefault(), "%.2f", expense));
    }

    // Method to get the logged-in username
    private String getLoggedInUsername() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("shared_pref", Context.MODE_PRIVATE);
        return sharedPreferences.getString("username", "");
    }
}
