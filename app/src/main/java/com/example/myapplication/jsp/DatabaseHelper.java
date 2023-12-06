package com.example.myapplication.jsp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;


public class DatabaseHelper extends SQLiteOpenHelper {

    public static  final String databaseName = "Balance Buddy.db";

    public DatabaseHelper(@Nullable Context context) {
        super(context, databaseName, null, 4);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("create Table users(username TEXT primary key, email TEXT, password TEXT)");
        // 가계부 데이터를 저장할 테이블 생성
        sqLiteDatabase.execSQL("create Table transactions(id INTEGER PRIMARY KEY AUTOINCREMENT, date TEXT, type TEXT, description TEXT, amount REAL, username TEXT, FOREIGN KEY(username) REFERENCES users(username))");
    }
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("drop table if exists users");
        sqLiteDatabase.execSQL("drop table if exists transactions");
        onCreate(sqLiteDatabase);
    }

    public Boolean insertData(String username, String email, String password){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("username", username);
        cv.put("email", email);
        cv.put("password", password);
        long result = sqLiteDatabase.insert("users", null, cv);

        if(result == -1){
            return false;
        } else {
            return true;
        }
    }

    public Boolean checkUsername (String username){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("Select * from users where username = ?", new String[] {username});

        if (cursor.getCount() > 0){
            return true;
        }else {
            return false;
        }
    }

    public  Boolean checkUser (String username, String password){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("Select * from users where username = ? and password = ?", new String[] {username, password});

        if (cursor.getCount() > 0){
            return true;
        }else {
            return false;
        }
    }

    public Boolean insertTransaction(String date, String type, String description, Double amount, String username) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("date", date);
        cv.put("type", type);
        cv.put("description", description);
        cv.put("amount", amount);
        cv.put("username", username);
        long result = sqLiteDatabase.insert("transactions", null, cv);

        return result != -1;
    }
    public Cursor getTransactionsByDate(String date, String username) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("Select * from transactions where date = ? and username = ?", new String[] {date, username});
    }

    public Cursor getTransactionsByUsername(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM transactions WHERE username = ?", new String[]{username});
    }
    public boolean deleteTransaction(int transactionId) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete("transactions", "id = ?", new String[]{""+transactionId}) > 0;
    }
}
