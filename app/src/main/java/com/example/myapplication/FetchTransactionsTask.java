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
            int idIndex = cursor.getColumnIndex("id");
            int typeIndex = cursor.getColumnIndex("type");
            int descriptionIndex = cursor.getColumnIndex("description");
            int amountIndex = cursor.getColumnIndex("amount");
            int dateIndex = cursor.getColumnIndex("date");

            while (cursor.moveToNext()) {
                int id = idIndex != -1 ? cursor.getInt(idIndex) : -1;
                String type = typeIndex != -1 ? cursor.getString(typeIndex) : "";
                String description = descriptionIndex != -1 ? cursor.getString(descriptionIndex) : "";
                String amount = amountIndex != -1 ? cursor.getString(amountIndex) : "";
                String date = dateIndex != -1 ? cursor.getString(dateIndex) : "";

                transactions.add(new Transaction(id, type, description, amount, date));
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

