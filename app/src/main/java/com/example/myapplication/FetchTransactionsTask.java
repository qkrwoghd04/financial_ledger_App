package com.example.myapplication;

import android.database.Cursor;
import android.os.AsyncTask;

import com.example.myapplication.jsp.DatabaseHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class FetchTransactionsTask extends AsyncTask<String, Void, List<Transaction>> {
    private DatabaseHelper databaseHelper;
    private Consumer<List<Transaction>> onResultListener;

    public FetchTransactionsTask(DatabaseHelper databaseHelper, Consumer<List<Transaction>> onResultListener) {
        this.databaseHelper = databaseHelper;
        this.onResultListener = onResultListener;
    }

    @Override
    protected List<Transaction> doInBackground(String... usernames) {
        String username = usernames[0];
        List<Transaction> transactions = new ArrayList<>();
        Cursor cursor = databaseHelper.getTransactionsByUsername(username);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                int typeIndex = cursor.getColumnIndex("type");
                int descriptionIndex = cursor.getColumnIndex("description");
                int amountIndex = cursor.getColumnIndex("amount");
                int dateIndex = cursor.getColumnIndex("date");

                if (typeIndex != -1 && descriptionIndex != -1 && amountIndex != -1 && dateIndex != -1) {
                    String type = cursor.getString(typeIndex);
                    String description = cursor.getString(descriptionIndex);
                    String amount = cursor.getString(amountIndex);
                    String date = cursor.getString(dateIndex);

                    transactions.add(new Transaction(type, description, amount, date));
                }
            }
            cursor.close();
        }
        return transactions;
    }


    @Override
    protected void onPostExecute(List<Transaction> transactions) {
        onResultListener.accept(transactions);
    }
}

