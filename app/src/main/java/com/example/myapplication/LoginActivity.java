package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.jsp.Database;

public class LoginActivity extends AppCompatActivity {

    EditText et_password, et_username;
    Button bt_login;
    TextView tv_newuser;
    private Database database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        et_username = findViewById(R.id.et_reg_email);
        et_password = findViewById(R.id.et_confirm_password);
        bt_login = findViewById(R.id.bt_reg);
        tv_newuser = findViewById(R.id.tv_newuser);

        bt_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = et_username.getText().toString();
                String password = et_password.getText().toString();
                database = Database.getInstance(getApplicationContext());
                if(username.length() == 0 || password.length() == 0){
                    Toast.makeText(getApplicationContext(), "Please fill All details", Toast.LENGTH_SHORT).show();
                }else{
                    if(database.login(username, password) == true){
                        Toast.makeText(getApplicationContext(), "Login Success", Toast.LENGTH_SHORT).show();
                        SharedPreferences sharedPreferences = getSharedPreferences("shared_pref", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("username", username);
                        editor.apply();
                        startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                    }else{
                        Toast.makeText(getApplicationContext(), "Invaild Username and Password", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });

        //show the register activity when the user does not have an account
        tv_newuser.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });

    }
}