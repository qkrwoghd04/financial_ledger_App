package com.example.myapplication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.jsp.DatabaseHelper;

public class LoginActivity extends AppCompatActivity {
    private ProgressDialog progressDialog;
    DatabaseHelper databaseHelper;
    EditText et_username, et_password;
    Button login_button;
    TextView new_user_button;
    ImageView iv_back;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // SharedPreferences에서 로그인 상태 확인
        SharedPreferences sharedPreferences = getSharedPreferences("shared_pref", MODE_PRIVATE);
        boolean isLoggedIn = sharedPreferences.getBoolean("loggedIn", false);
        if (isLoggedIn) {
            // 이미 로그인된 경우, 메인 화면으로 바로 이동
            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
        }
        et_username = findViewById(R.id.username);
        et_password = findViewById(R.id.password);
        login_button = findViewById(R.id.login_button);
        new_user_button = findViewById(R.id.new_user_button);
        databaseHelper = new DatabaseHelper(this);
        iv_back = findViewById(R.id.iv_back);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Logging in...");
        progressDialog.setCancelable(false);

//        if (!getLoggedInUsername().isEmpty()) {
//            startActivity(new Intent(LoginActivity.this, HomeActivity.class));
//            finish();
//            return;
//        }

        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = et_username.getText().toString();
                String password = et_password.getText().toString();

                if(username.isEmpty() || password.isEmpty()){
                    Toast.makeText(getApplicationContext(), "Please fill All details", Toast.LENGTH_SHORT).show();
                    return;
                }
                progressDialog.show();
                boolean isLoggedId = databaseHelper.checkUser(username, password);
                if(isLoggedId){
                    saveLoginSession(username);
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Login Success", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                    finish();
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Invalid Username and Password", Toast.LENGTH_SHORT).show();
                }
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


        //show the register activity when the user does not have an account
        new_user_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });
    }
    private void dismissProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    private void saveLoginSession(String username) {
        SharedPreferences sharedPreferences = getSharedPreferences("shared_pref", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("username", username);
        editor.apply();
    }


}
