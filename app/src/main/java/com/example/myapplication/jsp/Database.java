package com.example.myapplication.jsp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class Database extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "app_database";
    private static final int DATABASE_VERSION = 1;
    private static Database instance;
    // Users Table Columns
    private static final String TABLE_USERS = "users";
    private static final String USER_ID = "id";
    private static final String COLUMN_USERNAME = "username";
    private static final String COLUMN_EMAIL = "email";
    private static final String COLUMN_PASSWORD = "password";

    // Expenses Table Columns
    private static final String TABLE_EXPENSES = "expenses";
    private static final String EXPENSE_ID = "id";
    private static final String COLUMN_USER = "user";
    private static final String COLUMN_AMOUNT = "amount";
    private static final String COLUMN_DESCRIPTION = "description";
    private static final String COLUMN_DATE = "date";


    public static synchronized Database getInstance(Context context) {
        if (instance == null) {
            instance = new Database(context.getApplicationContext(), "Balance Buddy", null, 1);
        }
        return instance;
    }
    public Database(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version){
        super(context, name, factory, version);
    }
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase){
        String createUsersTable = "CREATE TABLE " + TABLE_USERS + "("
                + USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," // 여기에 공백 추가
                + COLUMN_USERNAME + " TEXT,"
                + COLUMN_EMAIL + " TEXT,"
                + COLUMN_PASSWORD + " TEXT" + ")";

        String createExpensesTable = "CREATE TABLE " + TABLE_EXPENSES + "("
                + EXPENSE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," // 여기도 동일하게 공백 추가
                + COLUMN_USER + " TEXT,"
                + COLUMN_AMOUNT + " REAL,"
                + COLUMN_DESCRIPTION + " TEXT,"
                + COLUMN_DATE + " TEXT,"
                + "FOREIGN KEY(" + COLUMN_USER + ") REFERENCES " + TABLE_USERS + "(" + USER_ID + "))";

        sqLiteDatabase.execSQL(createUsersTable);
        sqLiteDatabase.execSQL(createExpensesTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        // Drop older table if existed
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_EXPENSES);
        // Create tables again
        onCreate(sqLiteDatabase);
    }

    // Method to add a record to the expenses table

    public boolean register(String username, String email, String password){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_USERNAME, username);
        cv.put(COLUMN_EMAIL, email);
        cv.put(COLUMN_PASSWORD, password);

        long result = -1;
        try {
            result = db.insert(TABLE_USERS, null, cv);
        } finally {
            db.close();
        }
        return result != -1; // return true if register is successful
    }

    public boolean login(String username, String password){
        SQLiteDatabase db = this.getReadableDatabase();
        String[] selectionArgs = {username, password};
        Cursor cursor = null;
        boolean isLoggedIn = false;
        try {
            cursor = db.rawQuery("SELECT * FROM " + TABLE_USERS + " WHERE " +
                    COLUMN_USERNAME + "=? AND " + COLUMN_PASSWORD + "=?", selectionArgs);

            if (cursor.moveToFirst()) {
                isLoggedIn = true;
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }
        return isLoggedIn; // return true if login is successful
    }

    // Method to get user details
    public Cursor getUserDetails(String userid) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_USERS, null, // null will select all columns
                USER_ID + "=?", new String[]{userid},
                null, null, null);
    }
    public boolean addExpense(String user, double amount, String description, String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_USER, user);
        cv.put(COLUMN_AMOUNT, amount);
        cv.put(COLUMN_DESCRIPTION, description);
        cv.put(COLUMN_DATE, date);

        long result = db.insert(TABLE_EXPENSES, null, cv);
        db.close();
        return result != -1; // return true if insert is successful
    }

    // Method to get all expenses of a particular user
    public Cursor getUserExpenses(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_EXPENSES, new String[]{COLUMN_AMOUNT, COLUMN_DESCRIPTION, COLUMN_DATE},
                COLUMN_USER + "=?", new String[]{username}, null, null, COLUMN_DATE + " DESC");
    }

}
