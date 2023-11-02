package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class RegisterActivity extends AppCompatActivity {

    EditText et_username, et_email, et_password, et_confirm_pw;
    Button bt_reg;

    TextView tv_existingUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        et_username = findViewById(R.id.et_reg_username);
        et_email = findViewById(R.id.et_reg_email);
        et_password = findViewById(R.id.et_reg_password);
        et_confirm_pw = findViewById(R.id.et_confirm_password);
        bt_reg = findViewById(R.id.bt_reg);
        tv_existingUser = findViewById(R.id.tv_existinguser);

        //show the register activity when the user does not have an account
        tv_existingUser.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            }
        });

        bt_reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = et_username.getText().toString();
                String email = et_email.getText().toString();
                String password = et_password.getText().toString();
                String confirm_pw = et_confirm_pw.getText().toString();
                Database db = new Database(getApplicationContext(), "vitaScan", null, 1);

                if(username.length() == 0 || email.length() == 0 || password.length() == 0 || confirm_pw.length() == 0){
                    Toast.makeText(getApplicationContext(), "Please fill All details", Toast.LENGTH_SHORT).show();
                }else{
                    if(password.compareTo(confirm_pw) == 0){
                        if(isValid(password)){
                            db.register(username, email, password);
                            Toast.makeText(getApplicationContext(), "Record Inserted", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                        }else{
                            Toast.makeText(getApplicationContext(), "Password must contain at least 8 characters, having letter,digit and special symbol", Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        Toast.makeText(getApplicationContext(), "Password and Confirm password didn't match", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
    public static boolean isValid(String pw){
        int f1=0, f2=0, f3=3;
        if(pw.length() < 8 || pw.length() > 20){
            return false;
        }else{
            for(int p = 0; p < pw.length(); p++){
                if(Character.isLetter(pw.charAt(p))){
                    f1=1;
                }
            }
            for(int r = 0; r < pw.length(); r++){
                if(Character.isDigit(pw.charAt(r))){
                    f2=1;
                }
            }
            // 특수 문자
            for(int s = 0; s < pw.length(); s++){
                char c = pw.charAt(s);
                if(c>=33 && c<=46 || c==64){
                    f3=1;
                }
            }
        }
        if(f1==1 && f2==1 && f2==1){
            return true;
        }
        return false;
    }
}