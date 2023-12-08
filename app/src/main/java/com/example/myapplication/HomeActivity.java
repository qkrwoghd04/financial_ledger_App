package com.example.myapplication;

import android.content.SharedPreferences;
import android.icu.text.SimpleDateFormat;
import android.text.format.DateFormat;
import android.util.Log; // 로그를 위한 임포트
import android.database.Cursor;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.adapter.TransactionAdapter;
import com.example.myapplication.fragment.AccountFragment;
import com.example.myapplication.fragment.HomeFragment;
import com.example.myapplication.fragment.StatisticFragment;
import com.example.myapplication.fragment.GoalsFragment; // GoalsFragment를 import 해야 합니다.
import com.example.myapplication.model.Transaction;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.example.myapplication.jsp.DatabaseHelper;

import android.view.MenuItem;
import android.view.View;
import android.app.Dialog;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class HomeActivity extends AppCompatActivity {
    public CalendarView calendarView;
    public TextView diaryTextView, textView3;
    private RecyclerView recyclerView;
    private TransactionAdapter adapter;
    private List<Transaction> transactionList;
    private DatabaseHelper databaseHelper;
    private Map<String, List<Transaction>> transactionsCache = new HashMap<>();
    private Map<String, Long> lastLoadTime = new HashMap<>();
    private final long CACHE_VALID_DURATION = TimeUnit.MINUTES.toMillis(30);
    private String selectedDate = ""; // 현재 선택된 날짜를 저장하는 변수

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        calendarView = findViewById(R.id.calendarView);
        diaryTextView = findViewById(R.id.diaryTextView);
        textView3 = findViewById(R.id.textView3);
        recyclerView = findViewById(R.id.recyclerView);
        transactionList = new ArrayList<>();
        adapter = new TransactionAdapter(transactionList);
        recyclerView.setAdapter(adapter);
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigationView);
        bottomNav.setOnNavigationItemSelectedListener(navListener);
        databaseHelper = new DatabaseHelper(this);


        setupRecyclerView();
        transactionsCache.clear();
        lastLoadTime.clear();

        // 현재 날짜 설정
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        selectedDate = sdf.format(calendar.getTime());


        // 현재 날짜에 해당하는 트랜잭션만 로드
        loadTransactionsForDate(selectedDate);
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                // 날짜 형식 변환
                selectedDate = String.format(Locale.getDefault(), "%d/%02d/%02d", year, month + 1, dayOfMonth);
                loadTransactionsForDate(selectedDate);
            }
        });


        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();
            }
        });

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
        }
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment selectedFragment = null;
            boolean isHomeFragment = false;

            if (item.getItemId() == R.id.home) {
                selectedFragment = new HomeFragment();
                calendarView.setVisibility(View.VISIBLE);
                isHomeFragment = true;
            } else if (item.getItemId() == R.id.statistic) {
                selectedFragment = new StatisticFragment();
                calendarView.setVisibility(View.GONE);
            } else if (item.getItemId() == R.id.goals) {
                selectedFragment = new GoalsFragment();
                calendarView.setVisibility(View.GONE);
            } else if (item.getItemId() == R.id.account_info){
                selectedFragment = new AccountFragment();
                calendarView.setVisibility(View.GONE);
            }
            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
            }
            recyclerView.setVisibility(isHomeFragment ? View.VISIBLE : View.GONE);

            return true;
        }
    };

    private void showDialog() {
        Dialog dialog = new Dialog(this, android.R.style.Theme_Material_Light_Dialog_NoActionBar_MinWidth);
        dialog.setContentView(R.layout.dialog_layout);

        // 대화창 내의 컴포넌트 참조
        TextView selectedDateText = dialog.findViewById(R.id.selectedDate);
        selectedDateText.setText(selectedDate);

        EditText editTextDescription = dialog.findViewById(R.id.editTextDescription);
        EditText editTextAmount = dialog.findViewById(R.id.editTextAmount);
        RadioButton radioIncome = dialog.findViewById(R.id.radioIncome);
        RadioButton radioExpense = dialog.findViewById(R.id.radioExpense);
        Button buttonAdd = dialog.findViewById(R.id.bt_Add);

        // "추가" 버튼 클릭 이벤트 처리
        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String type = radioIncome.isChecked() ? "Income" : "Expense";
                    String description = editTextDescription.getText().toString();
                    String amountStr = editTextAmount.getText().toString().trim();
                    if (description.isEmpty() || amountStr.isEmpty()) {
                        Toast.makeText(HomeActivity.this, "Description and amount are required", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    double amount = Double.parseDouble(amountStr);

                    // 현재 선택된 날짜를 가져오기
                    long selectedDateMillis = calendarView.getDate();
                    Log.d("" + selectedDateMillis, "View Date");
                    Log.d(""+ selectedDateMillis, "selected Date");



                    // 로그인한 사용자의 이름 가져오기
                    String loggedInUsername = getLoggedInUsername();

                    // 데이터베이스에 트랜잭션 저장 후 id 반환
                    long transactionId = databaseHelper.insertTransaction(selectedDate, type, description, amount, loggedInUsername);

                    if (transactionId != -1) {
                        Transaction newTransaction = new Transaction(
                                transactionId, type, description, String.valueOf(amount), selectedDate);
                        transactionList.add(newTransaction);
                        updateCacheWithNewTransaction(selectedDate, newTransaction);
                        adapter.notifyDataSetChanged();
                    }

                } catch (NumberFormatException e) {
                    Toast.makeText(HomeActivity.this, "Invalid amount", Toast.LENGTH_SHORT).show();
                }

                dialog.dismiss();
            }
        });


        dialog.show();
    }

    private void loadTransactionsForDate(String date) {

        if (transactionsCache.containsKey(date)) {
            transactionList.clear();
            transactionList.addAll(transactionsCache.get(date));
            adapter.notifyDataSetChanged();
        } else {
            loadTransactionsFromDatabase(date);
        }
    }



    private void loadTransactionsFromDatabase(String date) {
        Cursor cursor = databaseHelper.getTransactionsByDate(date, getLoggedInUsername());

        List<Transaction> transactionsForDate = new ArrayList<>();
        if (cursor != null) {
            while (cursor.moveToNext()) {
                int typeIndex = cursor.getColumnIndex("type");
                int descriptionIndex = cursor.getColumnIndex("description");
                int amountIndex = cursor.getColumnIndex("amount");
                int dateIndex = cursor.getColumnIndex("date"); // 날짜 인덱스 추가
                int idIndex = cursor.getColumnIndex("id");


                if (idIndex != -1 && typeIndex != -1 && descriptionIndex != -1 && amountIndex != -1 && dateIndex != -1) {
                    int id = cursor.getInt(idIndex);
                    String type = cursor.getString(typeIndex);
                    String description = cursor.getString(descriptionIndex);
                    String amount = cursor.getString(amountIndex);
                    String transactionDate = cursor.getString(dateIndex);

                    transactionsForDate.add(new Transaction(id, type, description, amount, transactionDate));

                }
            }
            cursor.close();
        }

        transactionsCache.put(""+date, transactionsForDate);
        transactionList.clear();
        transactionList.addAll(transactionsForDate);
        adapter.notifyDataSetChanged();
        lastLoadTime.put(""+date, System.currentTimeMillis());
    }

    private void updateCacheWithNewTransaction(String date, Transaction transaction) {
        List<Transaction> transactionsForDate = transactionsCache.get(date);
        if (transactionsForDate == null) {
            transactionsForDate = new ArrayList<>();
            transactionsCache.put(""+date, transactionsForDate);
        }
        transactionsForDate.add(transaction);
    }
    private String getLoggedInUsername() {
        SharedPreferences sharedPreferences = getSharedPreferences("shared_pref", MODE_PRIVATE);
        return sharedPreferences.getString("username", null);
    }
    private void setupRecyclerView() {
        recyclerView = findViewById(R.id.recyclerView);
        transactionList = new ArrayList<>();
        adapter = new TransactionAdapter(transactionList);
        recyclerView.setAdapter(adapter);

        // 스와이프 기능 추가
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                int position = viewHolder.getAdapterPosition();
                Transaction transaction = transactionList.get(position);
                deleteTransactionFromDatabase(transaction);
                transactionList.remove(position);
                adapter.notifyItemRemoved(position);
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }
    private void deleteTransactionFromDatabase(Transaction transaction) {
        if (databaseHelper.deleteTransaction(transaction.getId())) {
            Toast.makeText(this, "Transaction deleted", Toast.LENGTH_SHORT).show();

            // 현재 선택된 날짜의 트랜잭션 목록에서 해당 트랜잭션 제거
            List<Transaction> transactionsForDate = transactionsCache.get(selectedDate);
            if (transactionsForDate != null) {
                for (int i = 0; i < transactionsForDate.size(); i++) {
                    if (transactionsForDate.get(i).getId() == transaction.getId()) {
                        transactionsForDate.remove(i);
                        break;
                    }
                }
            }
            adapter.notifyDataSetChanged();
        } else {
            Toast.makeText(this, "Failed to delete transaction", Toast.LENGTH_SHORT).show();
        }
    }

}
