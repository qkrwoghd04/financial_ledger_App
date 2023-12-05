package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.jsp.DatabaseHelper;

public class RegisterActivity extends AppCompatActivity {
    EditText et_username, et_password, et_email, et_confirmation_password;

    Button btn_register;

    TextView btn_goto_login;

    DatabaseHelper databaseHelper;

    ImageView iv_back;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        et_username = findViewById(R.id.username);
        et_email = findViewById(R.id.email_address);
        et_password = findViewById(R.id.password);
        et_confirmation_password = findViewById(R.id.confirmation_password);
        btn_register = findViewById(R.id.register_button);
        btn_goto_login = findViewById(R.id.login_page_button);
        iv_back = findViewById(R.id.iv_back); // ImageView 초기화 추가

        databaseHelper = new DatabaseHelper(this);

        btn_goto_login.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            }
        });

        iv_back.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(getApplicationContext(), WelcomeActivity.class);
                startActivity(intent);
                finish();
            }
        });

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("RegisterActivity", "Button clicked");
                String username = et_username.getText().toString();
                String email = et_email.getText().toString();
                String password = et_password.getText().toString();
                String conf_password = et_confirmation_password.getText().toString();

                if (username.isEmpty() || email.isEmpty() || password.isEmpty() || conf_password.isEmpty()){
                    Toast.makeText(RegisterActivity.this, "Please fill all details", Toast.LENGTH_LONG).show();
                } else if (!password.equals(conf_password)) {
                    Toast.makeText(RegisterActivity.this, "Password do not match", Toast.LENGTH_LONG).show();
                } else if (!isValidPassword(password)) {
                    Toast.makeText(RegisterActivity.this, "Invalid password format", Toast.LENGTH_LONG).show();
                } else if (databaseHelper.checkUsername(username)) {
                    Toast.makeText(RegisterActivity.this, "User Already Exists", Toast.LENGTH_LONG).show();
                } else {
                    boolean registeredSuccess = databaseHelper.insertData(username, email, password);
                    if (registeredSuccess){
                        Toast.makeText(RegisterActivity.this, "User Registered Successfully", Toast.LENGTH_LONG).show();
                        // 등록 성공 후 LoginActivity로 화면 전환
                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(intent);
                        finish(); // 현재 액티비티 종료
                    } else {
                        Toast.makeText(getApplicationContext(), "Registration failed, try again", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

    }

    public static boolean isValidPassword(String password) {
        int hasLetter = 0, hasDigit = 0, hasSpecialChar = 0;

        if (password.length() < 8) {
            return false; // Password length should be at least 8 characters
        } else {
            for (int i = 0; i < password.length(); i++) {
                char c = password.charAt(i);
                if (Character.isLetter(c)) hasLetter = 1;
                if (Character.isDigit(c)) hasDigit = 1;
                if ((c >= 33 && c <= 46) || c == 64) hasSpecialChar = 1;
            }
        }

        return hasLetter == 1 && hasDigit == 1 && hasSpecialChar == 1;
    }
}
