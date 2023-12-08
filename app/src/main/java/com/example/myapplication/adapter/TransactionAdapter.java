package com.example.myapplication.adapter;

import android.icu.text.SimpleDateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.model.Transaction;

import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder> {
    private List<Transaction> transactionList;

    public TransactionAdapter(List<Transaction> transactionList) {
        this.transactionList = transactionList;
    }

    @NonNull
    @Override
    public TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.transaction_item, parent, false);

        return new TransactionViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionViewHolder holder, int position) {
        Transaction transaction = transactionList.get(position);
        holder.textViewType.setText(transaction.getType());
        holder.textViewDescription.setText(transaction.getDescription());
        holder.textViewAmount.setText(transaction.getAmount());

        // 날짜 변환
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault());
        String formattedDate = sdf.format(new Date(transaction.getDate()));
        holder.textViewDate.setText(formattedDate);
    }

    @Override
    public int getItemCount() {
        return transactionList.size();
    }

    public static class TransactionViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewType, textViewDescription, textViewAmount, textViewDate;

        public TransactionViewHolder(View view) {
            super(view);
            textViewType = view.findViewById(R.id.textViewType);
            textViewDescription = view.findViewById(R.id.textViewDescription);
            textViewAmount = view.findViewById(R.id.textViewAmount);
            textViewDate = view.findViewById(R.id.textViewDate); // 이 부분이 추가되었는지 확인
        }
    }
}
