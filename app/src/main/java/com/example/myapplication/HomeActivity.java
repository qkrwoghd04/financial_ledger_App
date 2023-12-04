package com.example.myapplication;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.myapplication.fragment.AccountFragment;
import com.example.myapplication.fragment.HomeFragment;
import com.example.myapplication.fragment.StatisticFragment;
import com.example.myapplication.fragment.GoalsFragment; // GoalsFragment를 import 해야 합니다.
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import android.view.MenuItem;
import android.view.View;
import android.app.Dialog;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import java.io.FileInputStream;

public class HomeActivity extends AppCompatActivity {
    public String readDay = null;
    public String str = null;
    public CalendarView calendarView;
    public TextView diaryTextView, textView2, textView3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        calendarView = findViewById(R.id.calendarView);
        diaryTextView = findViewById(R.id.diaryTextView);
        textView2 = findViewById(R.id.textView2);
        textView3 = findViewById(R.id.textView3);

        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigationView);
        bottomNav.setOnNavigationItemSelectedListener(navListener);

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener()
        {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth)
            {
                diaryTextView.setVisibility(View.VISIBLE);
                textView2.setVisibility(View.INVISIBLE);
                diaryTextView.setText(String.format("%d / %d / %d", year, month + 1, dayOfMonth));
//                checkDay(year, month, dayOfMonth);
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

            if (item.getItemId() == R.id.home) {
                selectedFragment = new HomeFragment();
            } else if (item.getItemId() == R.id.statistic) {
                selectedFragment = new StatisticFragment();
            } else if (item.getItemId() == R.id.goals) {
                selectedFragment = new GoalsFragment();
            } else if (item.getItemId() == R.id.account_info){
                selectedFragment = new AccountFragment();
            }


            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
            }

            return true;
        }
    };

    private void showDialog() {
        Dialog dialog = new Dialog(this, android.R.style.Theme_Material_Light_Dialog_NoActionBar_MinWidth);
        dialog.setContentView(R.layout.dialog_layout);

        // 대화창 내의 컴포넌트 참조
        EditText editTextDescription = dialog.findViewById(R.id.editTextDescription);
        EditText editTextAmount = dialog.findViewById(R.id.editTextAmount);
        RadioButton radioIncome = dialog.findViewById(R.id.radioIncome);
        RadioButton radioExpense = dialog.findViewById(R.id.radioExpense);
        Button buttonAdd = dialog.findViewById(R.id.bt_Add);

        // "추가" 버튼 클릭 이벤트 처리
        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String type = radioIncome.isChecked() ? "Income" : "Expense";
                String description = editTextDescription.getText().toString();
                String amount = editTextAmount.getText().toString();

                String data = type + ": " + description + ", Amount: " + amount;
                textView2.setText(data);  // HomeActivity의 TextView에 데이터 표시
                textView2.setVisibility(View.VISIBLE);

                dialog.dismiss();  // 대화창 닫기
            }
        });

        dialog.show();
    }

//    public void checkDay(int cYear, int cMonth, int cDay)
//    {
//        readDay = "" + cYear + "-" + (cMonth + 1) + "" + "-" + cDay + ".txt";
//        FileInputStream fis;
//
//        try
//        {
//            fis = openFileInput(readDay);
//
//            byte[] fileData = new byte[fis.available()];
//            fis.read(fileData);
//            fis.close();
//
//            str = new String(fileData);
//
//            contextEditText.setVisibility(View.INVISIBLE);
//            textView2.setVisibility(View.VISIBLE);
//            textView2.setText(str);
//
//            save_Btn.setVisibility(View.INVISIBLE);
//            cha_Btn.setVisibility(View.VISIBLE);
//            del_Btn.setVisibility(View.VISIBLE);
//
//            cha_Btn.setOnClickListener(new View.OnClickListener()
//            {
//                @Override
//                public void onClick(View view)
//                {
//                    contextEditText.setVisibility(View.VISIBLE);
//                    textView2.setVisibility(View.INVISIBLE);
//                    contextEditText.setText(str);
//
//                    save_Btn.setVisibility(View.VISIBLE);
//                    cha_Btn.setVisibility(View.INVISIBLE);
//                    del_Btn.setVisibility(View.INVISIBLE);
//                    textView2.setText(contextEditText.getText());
//                }
//
//            });
//            del_Btn.setOnClickListener(new View.OnClickListener()
//            {
//                @Override
//                public void onClick(View view)
//                {
//                    textView2.setVisibility(View.INVISIBLE);
//                    contextEditText.setText("");
//                    contextEditText.setVisibility(View.VISIBLE);
//                    save_Btn.setVisibility(View.VISIBLE);
//                    cha_Btn.setVisibility(View.INVISIBLE);
//                    del_Btn.setVisibility(View.INVISIBLE);
////                    removeDiary(readDay);
//                }
//            });
//            if (textView2.getText() == null)
//            {
//                textView2.setVisibility(View.INVISIBLE);
//                diaryTextView.setVisibility(View.VISIBLE);
//                save_Btn.setVisibility(View.VISIBLE);
//                cha_Btn.setVisibility(View.INVISIBLE);
//                del_Btn.setVisibility(View.INVISIBLE);
//                contextEditText.setVisibility(View.VISIBLE);
//            }
//
//        }
//        catch (Exception e)
//        {
//            e.printStackTrace();
//        }
//    }

//    @SuppressLint("WrongConstant")
//    public void removeDiary(String readDay)
//    {
//        FileOutputStream fos;
//        try
//        {
//            fos = openFileOutput(readDay, MODE_NO_LOCALIZED_COLLATORS);
//            String content = "";
//            fos.write((content).getBytes());
//            fos.close();
//
//        }
//        catch (Exception e)
//        {
//            e.printStackTrace();
//        }
//    }
//
//    @SuppressLint("WrongConstant")
//    public void saveDiary(String readDay)
//    {
//        FileOutputStream fos;
//        try
//        {
//            fos = openFileOutput(readDay, MODE_NO_LOCALIZED_COLLATORS);
//            String content = contextEditText.getText().toString();
//            fos.write((content).getBytes());
//            fos.close();
//        }
//        catch (Exception e)
//        {
//            e.printStackTrace();
//        }
//    }
}
