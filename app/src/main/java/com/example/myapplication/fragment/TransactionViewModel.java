package com.example.myapplication.fragment;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.myapplication.model.Transaction;
import com.example.myapplication.jsp.DatabaseHelper;

import java.util.ArrayList;
import java.util.List;

public class TransactionViewModel extends ViewModel {
    private MutableLiveData<List<Transaction>> transactions = new MutableLiveData<>();
    private MutableLiveData<Double> totalIncome = new MutableLiveData<>();
    private MutableLiveData<Double> totalExpense = new MutableLiveData<>();
    private DatabaseHelper databaseHelper;


    public TransactionViewModel(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
        loadTransactions();
    }

    // LiveData를 반환하는 메소드
    public LiveData<Double> getTotalIncome() {
        return totalIncome;
    }

    public LiveData<Double> getTotalExpense() {
        return totalExpense;
    }
    // 데이터베이스에서 트랜잭션을 로드하고 LiveData를 업데이트하는 메소드
    private void loadTransactions() {
        new Thread(() -> {
            List<Transaction> transactionList = databaseHelper.getAllTransactions();
            double income = 0.0;
            double expense = 0.0;
            for (Transaction transaction : transactionList) {
                if (transaction.getType().equals("Income")) {
                    income += Double.parseDouble(transaction.getAmount());
                } else if (transaction.getType().equals("Expense")) {
                    expense += Double.parseDouble(transaction.getAmount());
                }
            }
            transactions.postValue(transactionList);
            totalIncome.postValue(income);
            totalExpense.postValue(expense);
        }).start();
    }
    public void deleteTransaction(Transaction transaction) {
        new Thread(() -> {
            if (databaseHelper.deleteTransaction(transaction.getId())) {
                // 성공적으로 삭제되었다면, LiveData를 업데이트
                updateLiveDataWithDeletedTransaction(transaction);
            }
        }).start();
    }
    private void updateLiveDataWithNewTransaction(Transaction transaction) {
        List<Transaction> currentTransactions = transactions.getValue();
        if (currentTransactions == null) {
            currentTransactions = new ArrayList<>();
        }
        currentTransactions.add(transaction);
        transactions.postValue(currentTransactions);

        // totalIncome과 totalExpense 업데이트
        updateTotals();
    }
    private void updateLiveDataWithDeletedTransaction(Transaction transaction) {
        List<Transaction> currentTransactions = transactions.getValue();
        if (currentTransactions != null) {
            currentTransactions.remove(transaction);
            transactions.postValue(currentTransactions);

            // totalIncome과 totalExpense 업데이트
            updateTotals();
        }
    }

    // totalIncome과 totalExpense를 계산하여 업데이트하는 메소드
    private void updateTotals() {
        double income = 0.0;
        double expense = 0.0;
        List<Transaction> currentTransactions = transactions.getValue();
        if (currentTransactions != null) {
            for (Transaction transaction : currentTransactions) {
                if (transaction.getType().equals("Income")) {
                    income += Double.parseDouble(transaction.getAmount());
                } else if (transaction.getType().equals("Expense")) {
                    expense += Double.parseDouble(transaction.getAmount());
                }
            }
        }
        totalIncome.postValue(income);
        totalExpense.postValue(expense);
    }

}
