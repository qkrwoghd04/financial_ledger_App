package com.example.myapplication.fragment;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.myapplication.jsp.DatabaseHelper;

public class TransactionViewModelFactory implements ViewModelProvider.Factory {
    private final DatabaseHelper databaseHelper;

    public TransactionViewModelFactory(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(TransactionViewModel.class)) {
            return (T) new TransactionViewModel(databaseHelper);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
