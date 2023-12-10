package com.example.myapplication.jsp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.util.Pair;

import androidx.annotation.Nullable;

import com.example.myapplication.model.Transaction;

import java.util.ArrayList;
import java.util.List;


public class DatabaseHelper extends SQLiteOpenHelper {

    public static  final String databaseName = "Balance Buddy.db";

    public DatabaseHelper(@Nullable Context context) {
        super(context, databaseName, null, 10);
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

    public long insertTransaction(String date, String type, String description, Double amount, String username) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("date", date);
        cv.put("type", type);
        cv.put("description", description);
        cv.put("amount", amount);
        cv.put("username", username);
        return sqLiteDatabase.insert("transactions", null, cv);
    }

    public Cursor getTransactionsByDate(String date, String username) {
        SQLiteDatabase db = this.getWritableDatabase();
        Log.d("DatabaseHelper", "getTransactionsByDate: date = " + date + ", username = " + username);
        Cursor cursor = db.rawQuery("Select * from transactions where date = ? and username = ?", new String[] {date, username});
        if (cursor != null) {
            Log.d("DatabaseHelper", "Cursor count: " + cursor.getCount());
        } else {
            Log.d("DatabaseHelper", "Cursor is null");
        }
        return cursor;
    }


    public Cursor getTransactionsByUsername(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM transactions WHERE username = ?", new String[]{username});
    }
    public boolean deleteTransaction(long transactionId) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete("transactions", "id = ?", new String[]{""+transactionId}) > 0;
    }

    public List<Transaction> getAllTransactions() {
        List<Transaction> transactionList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM transactions", null);

        if (cursor.moveToFirst()) {
            do {
                int idIndex = cursor.getColumnIndex("id");
                int dateIndex = cursor.getColumnIndex("date");
                int typeIndex = cursor.getColumnIndex("type");
                int descriptionIndex = cursor.getColumnIndex("description");
                int amountIndex = cursor.getColumnIndex("amount");
                int usernameIndex = cursor.getColumnIndex("username");

                if (idIndex != -1 && dateIndex != -1 && typeIndex != -1 && descriptionIndex != -1 && amountIndex != -1 && usernameIndex != -1) {
                    int id = cursor.getInt(idIndex);
                    String date = cursor.getString(dateIndex);
                    String type = cursor.getString(typeIndex);
                    String description = cursor.getString(descriptionIndex);
                    double amount = cursor.getDouble(amountIndex);
                    String username = cursor.getString(usernameIndex);

                    Transaction transaction = new Transaction(id, type, description, String.valueOf(amount), date);
                    transactionList.add(transaction);
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        return transactionList;
    }
    // 한 달 동안의 수입과 지출을 합산하는 메소드
    public Pair<Double, Double> getMonthlyIncomeExpense(String username, String startDate, String endDate) {
        SQLiteDatabase db = this.getReadableDatabase();
        double totalIncome = 0.0;
        double totalExpense = 0.0;

        String query = "SELECT type, SUM(amount) as total FROM transactions WHERE username = ? AND date BETWEEN ? AND ? GROUP BY type";
        Cursor cursor = db.rawQuery(query, new String[]{username, startDate, endDate});

        if (cursor.moveToFirst()) {
            do {
                int typeIndex = cursor.getColumnIndex("type");
                int amountIndex = cursor.getColumnIndex("total");
                if (typeIndex != -1 && amountIndex != -1) {
                    String type = cursor.getString(typeIndex);
                    double amount = cursor.getDouble(amountIndex);

                    Log.d("DatabaseHelper", "Type: " + type + ", Amount: " + amount);

                    if (type.equals("Income")) {
                        totalIncome += amount;
                    } else if (type.equals("Expense")) {
                        totalExpense += amount;
                    }
                }
            } while (cursor.moveToNext());
        } else {
            Log.d("DatabaseHelper", "커서가 비어 있음");
        }
        cursor.close();

        return new Pair<>(totalIncome, totalExpense);
    }

}
