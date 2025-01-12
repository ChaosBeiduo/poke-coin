package com.chaos.pokecoin.adapter;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import android.graphics.Color;

import com.chaos.pokecoin.R;
import com.chaos.pokecoin.model.Transaction;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.ViewHolder> {
    private List<Transaction> transactions;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());

    public TransactionAdapter() {
        this.transactions = new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_transaction, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Transaction transaction = transactions.get(position);
        holder.textType.setText(transaction.getType());
        holder.textDate.setText(dateFormat.format(transaction.getDate()));
        
        String amountText = String.format("￥%.2f", transaction.getAmount());
        if (transaction.getType().equals("支出")) {
            holder.textAmount.setTextColor(Color.parseColor("#EE6352"));
            amountText = "-" + amountText;
        } else {
            holder.textAmount.setTextColor(Color.parseColor("#59CD90"));
            amountText = "+" + amountText;
        }
        holder.textAmount.setText(amountText);
    }

    @Override
    public int getItemCount() {
        return transactions.size();
    }

    public void addTransaction(Transaction transaction) {
        transactions.add(0, transaction); // 添加到列表开头
        notifyItemInserted(0);
    }

    public void clearTransactions() {
        transactions.clear();
        notifyDataSetChanged();
    }

    public void addTransactions(List<Transaction> newTransactions) {
        transactions.addAll(newTransactions);
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textType;
        TextView textDate;
        TextView textAmount;

        ViewHolder(View itemView) {
            super(itemView);
            textType = itemView.findViewById(R.id.textTransactionType);
            textDate = itemView.findViewById(R.id.textTransactionDate);
            textAmount = itemView.findViewById(R.id.textTransactionAmount);
        }
    }
} 