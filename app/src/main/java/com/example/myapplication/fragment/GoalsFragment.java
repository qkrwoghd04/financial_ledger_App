package com.example.myapplication.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.example.myapplication.R;
import com.example.myapplication.adapter.TransactionAdapter;
import com.example.myapplication.jsp.DatabaseHelper;
import com.example.myapplication.model.Transaction;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class GoalsFragment extends Fragment {

    private EditText etGoalAmount;
    private TextView tvdisplay;
    private RecyclerView recyclerView;
    private TransactionAdapter adapter;
    private List<Transaction> transactionList;
    private DatabaseHelper databaseHelper;
    private TextView tvToday;

    @Override
    public void onResume() {
        super.onResume();
        checkRecentTransaction();
        updateDisplayWithSavedGoalAmount(); // 이 메소드를 추가
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_goals, container, false);

        etGoalAmount = view.findViewById(R.id.tvActivityMainRemainMoney);
        etGoalAmount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etGoalAmount.setText(""); // 텍스트 비우기
            }
        });
        tvdisplay = view.findViewById(R.id.tv_display);
        tvToday = view.findViewById(R.id.tv_today); // 오늘 날짜 TextView 찾기
        setCurrentDate();

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("shared_pref", Context.MODE_PRIVATE);
        float savedGoalAmount = sharedPreferences.getFloat("originalGoalAmount", 0.0f);
        if (savedGoalAmount != 0.0f) {
            etGoalAmount.setText(String.valueOf(savedGoalAmount));
        }

        recyclerView = view.findViewById(R.id.rvActivityMain);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        transactionList = new ArrayList<>();
        adapter = new TransactionAdapter(transactionList);
        recyclerView.setAdapter(adapter);

        databaseHelper = new DatabaseHelper(getContext());
        loadTodayTransactions();

        checkRecentTransaction();


        etGoalAmount.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    String goalAmountStr = etGoalAmount.getText().toString();
                    if (!goalAmountStr.isEmpty()) {
                        float goalAmount = Float.parseFloat(goalAmountStr);
                        saveOriginalGoalAmount(goalAmount); // 원래 목표 금액 저장
                        updateRemainingAmountDisplay(goalAmount); // 남은 금액 업데이트
                        // 키보드 숨기기
                        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(etGoalAmount.getWindowToken(), 0);
                    }
                    return true;
                }
                return false;
            }
        });



        return view;
    }


    private String formatAmount(double amount) {
        if (amount == (long) amount) {
            return String.format(Locale.getDefault(), "%d", (long) amount);
        } else {
            return String.format(Locale.getDefault(), "%.2f", amount);
        }
    }

    private void loadTodayTransactions() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("shared_pref", Context.MODE_PRIVATE);
        String username = sharedPreferences.getString("username", null);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault());
        String today = sdf.format(new Date());

        Cursor cursor = databaseHelper.getTransactionsByDate(today, username);
        transactionList.clear();

        if (cursor != null && cursor.moveToFirst()) {
            do {
                int idIndex = cursor.getColumnIndex("id");
                int dateIndex = cursor.getColumnIndex("date");
                int typeIndex = cursor.getColumnIndex("type");
                int descriptionIndex = cursor.getColumnIndex("description");
                int amountIndex = cursor.getColumnIndex("amount");

                if (idIndex != -1 && dateIndex != -1 && typeIndex != -1 && descriptionIndex != -1 && amountIndex != -1) {
                    int id = cursor.getInt(idIndex);
                    String date = cursor.getString(dateIndex);
                    String type = cursor.getString(typeIndex);
                    String description = cursor.getString(descriptionIndex);
                    double amount = cursor.getDouble(amountIndex);
                    Log.d("DatabaseHelper", "Transaction Data: Type=" + type + ", Amount=" + amount);

                    Transaction transaction = new Transaction(id, type, description, String.valueOf(amount), date);
                    transactionList.add(transaction);
                }
            } while (cursor.moveToNext());
            cursor.close();
        }

        adapter.notifyDataSetChanged();
    }

    private double calculateRemainingAmount(double goalAmount) {
        double totalExpense = 0.0;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault());
        String today = sdf.format(new Date());
        Cursor cursor = databaseHelper.getTransactionsByDate(today, getLoggedInUsername());

        if (cursor != null && cursor.moveToFirst()) {
            do {
                int typeIndex = cursor.getColumnIndex("type");
                int amountIndex = cursor.getColumnIndex("amount");
                if (typeIndex != -1 && amountIndex != -1) {
                    String type = cursor.getString(typeIndex);
                    double amount = cursor.getDouble(amountIndex);

                    Log.d("GoalsFragment", "Transaction Type: " + type + ", Amount: " + amount); // 로그 추가

                    if ("Expense".equals(type)) {
                        totalExpense += amount;
                    }
                }
            } while (cursor.moveToNext());
            cursor.close();
        }

        double remainingAmount = goalAmount - totalExpense;

        return remainingAmount;
    }
    private void updateDisplayWithSavedGoalAmount() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("shared_pref", Context.MODE_PRIVATE);
        float savedGoalAmount = sharedPreferences.getFloat("originalGoalAmount", 0.0f);
        if (savedGoalAmount != 0.0f) {
            updateRemainingAmountDisplay(savedGoalAmount); // 저장된 목표 금액으로 남은 금액 업데이트
        }
    }

    private void saveOriginalGoalAmount(float goalAmount) {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("shared_pref", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putFloat("originalGoalAmount", goalAmount);
        editor.apply();
    }

    private void updateRemainingAmountDisplay(float goalAmount) {
        double remainingAmount = calculateRemainingAmount(goalAmount);
        String displayText = String.format(Locale.getDefault(), "%.2f", remainingAmount);
        if (remainingAmount == (long) remainingAmount) {
            displayText = String.format(Locale.getDefault(), "%.0f", remainingAmount); // 소수점 제거
        }
        tvdisplay.setText(displayText);
    }



    private String getLoggedInUsername() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("shared_pref", Context.MODE_PRIVATE);
        return sharedPreferences.getString("username", null);
    }

    private void setCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault());
        String today = sdf.format(new Date()); // 현재 날짜를 yyyy/MM/dd 형식으로 변환
        tvToday.setText(today); // TextView에 날짜 설정
    }

    private void checkRecentTransaction() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("shared_pref", Context.MODE_PRIVATE);
        String lastTransactionDate = sharedPreferences.getString("lastTransactionDate", "");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault());
        String today = sdf.format(new Date());

        // 오늘 날짜와 최근 트랜잭션 날짜가 동일한 경우
        if (today.equals(lastTransactionDate)) {
            float originalGoalAmount = sharedPreferences.getFloat("originalGoalAmount", 0.0f); // 원래 목표 금액 불러오기

            if (originalGoalAmount != 0.0f) {
                double remainingAmount = calculateRemainingAmount(originalGoalAmount); // 계산된 남은 금액 업데이트
                tvdisplay.setText(String.format(Locale.getDefault(), "%.2f", remainingAmount));
            }
        }
    }


}