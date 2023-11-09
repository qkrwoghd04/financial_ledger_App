package com.example.myapplication.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.myapplication.HomeActivity;
import com.example.myapplication.R;
import com.example.myapplication.jsp.Database;

import java.util.Date;
import java.util.Locale;


public class AddEntryFragment extends Fragment {
    private Button bt_save;
    private EditText et_amount;
    private EditText et_description;
    private Database database;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_entry, container, false);

        // Find the save button by its ID
        bt_save = view.findViewById(R.id.bt_save);
        et_amount = view.findViewById(R.id.et_amount);
        et_description = view.findViewById(R.id.et_description);
        database = Database.getInstance(getContext());
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("shared_pref_name", Context.MODE_PRIVATE);
        String currentUsername = sharedPreferences.getString("username", null); // "username"은 로그인 시 사용한 key




        // Set a click listener for the save button
        bt_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Save the entry
                saveEntry();

                // Return to the HomeActivity
                returnToHomeActivity();
            }
        });

        return view;
    }

    private void saveEntry() {
        // Retrieve value from the EditText
        String expenseValue = et_amount.getText().toString();
        String description = et_description.getText().toString();
        // Validate and convert to the desired format (e.g., double)
        if (!expenseValue.isEmpty()) {
            double expense = Double.parseDouble(expenseValue);
            SharedPreferences sharedPreferences = getActivity().getSharedPreferences("shared_pref_name", Context.MODE_PRIVATE);
            String username = sharedPreferences.getString("username", "");
            boolean isSuccess = database.addExpense(username,expense,description,getCurrentDate());

            // TODO: Save the expense to your database or wherever you need to save it
        } else {
            // TODO: exception
        }
    }
    private String getCurrentDate() {
        // Format the current date to your desired format
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(new Date());
    }

    private void returnToHomeActivity() {
        // Assuming you are using an AppCompatActivity and the context is available
        if(getActivity() != null) {
            Intent intent = new Intent(getActivity(), HomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            getActivity().finish(); // If you want to close the current activity
        }
    }

}

