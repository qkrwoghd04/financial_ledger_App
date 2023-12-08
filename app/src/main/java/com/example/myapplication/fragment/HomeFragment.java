package com.example.myapplication.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
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

public class HomeFragment extends Fragment {

    private DatabaseHelper databaseHelper;
    private TextView incomeTextView, expenseTextView;
    private String currentDate = "";
    public HomeFragment() {
        // Required empty public constructor
    }

    // Factory method and other initialization methods removed for brevity

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
        Log.d("HomeFragment", "Updating UI - Income: " + income + ", Expense: " + expense);
        incomeTextView.setText(String.format("%.2f", income));
        expenseTextView.setText(String.format("%.2f", expense));
    }

    // Method to get the logged-in username
    private String getLoggedInUsername() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("shared_pref", Context.MODE_PRIVATE);
        return sharedPreferences.getString("username", "");
    }
}
