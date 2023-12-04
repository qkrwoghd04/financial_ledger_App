package com.example.myapplication.jsp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;


public class DatabaseHelper extends SQLiteOpenHelper {

    public static  final String databaseName = "Signup.db";

    public DatabaseHelper(@Nullable Context context) {
        super(context, databaseName, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("create Table allusers(username TEXT primary key, email TEXT, password TEXT)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("drop table if exists allusers");
    }

    public Boolean insertData(String username, String email, String password){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("username", username);
        cv.put("email", email);
        cv.put("password", password);
        long result = sqLiteDatabase.insert("allusers", null, cv);

        if(result == -1){
            return false;
        } else {
            return true;
        }
    }

    public Boolean checkUsername (String username){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("Select * from allusers where username = ?", new String[] {username});

        if (cursor.getCount() > 0){
            return true;
        }else {
            return false;
        }
    }

    public  Boolean checkUser (String username, String password){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("Select * from allusers where username = ? and password = ?", new String[] {username, password});

        if (cursor.getCount() > 0){
            return true;
        }else {
            return false;
        }
    }
}
