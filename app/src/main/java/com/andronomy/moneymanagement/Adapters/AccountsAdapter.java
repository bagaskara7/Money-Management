package com.andronomy.moneymanagement.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.andronomy.moneymanagement.Lists.Accounts;
import com.andronomy.moneymanagement.Activities.AddIncomeActivity;
import com.andronomy.moneymanagement.Activities.AddOutcomeActivity;
import com.andronomy.moneymanagement.R;
import com.rey.material.widget.TextView;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by bagaskara on 7/10/2015.
 */
public class AccountsAdapter extends RecyclerView.Adapter<AccountsAdapter.MyViewHolder> {
    private LayoutInflater inflater;
    private DatabaseAdapter databaseAdapter;
    View view;
    List<Accounts> data = Collections.emptyList();

    public AccountsAdapter (Context context, List<Accounts> data) {
        inflater = LayoutInflater.from(context);
        this.data = data;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.account_layout, parent, false);
        databaseAdapter = new DatabaseAdapter(view.getContext());
        this.view = view;
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        final Accounts current = data.get(position);
        Locale.setDefault(new Locale(databaseAdapter.getActiveCurr()));

        String dateStr = current.date;
        if (dateStr != inflater.getContext().getString(R.string.never_str)) {
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date newDate = null;
            try {
                newDate = format.parse(dateStr);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            format = SimpleDateFormat.getDateTimeInstance(SimpleDateFormat.DEFAULT, SimpleDateFormat.SHORT, new Locale(databaseAdapter.getActiveLang()));//new SimpleDateFormat("dd/MMM/yyyy HH:mm.");
            dateStr = format.format(newDate);
        }

        String account = "";
        if (current.acc_id == 1) {
            account = view.getContext().getString(R.string.account_cash);
        } else if (current.acc_id == 2) {
            account = view.getContext().getString(R.string.account_payment_card);
        }

        holder.icon.setImageResource(inflater.getContext().getResources().getIdentifier(current.image, "drawable", inflater.getContext().getPackageName()));
        holder.icon.setContentDescription(current.acc_id + "");
        holder.name.setText(inflater.getContext().getString(R.string.account) + " " + account);
        holder.date.setText(inflater.getContext().getString(R.string.last_used) + ": " + dateStr);
        if (current.balance < 0) holder.balance.setTextColor(inflater.getContext().getResources().getColor(R.color.error_color));
        holder.balance.setText(inflater.getContext().getString(R.string.cur_balance) + ": " + NumberFormat.getCurrencyInstance().format(current.balance));
        holder.addIncome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(inflater.getContext(), AddIncomeActivity.class);
                i.putExtra("account_id", current.acc_id);
                inflater.getContext().startActivity(i);
            }
        });
        holder.addExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(inflater.getContext(), AddOutcomeActivity.class);
                i.putExtra("account_id", current.acc_id);
                inflater.getContext().startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView icon;
        TextView name;
        TextView balance;
        TextView date;
        ImageView addIncome;
        ImageView addExpense;

        public MyViewHolder(View itemView) {
            super(itemView);
            icon = (ImageView) itemView.findViewById(R.id.accountImage);
            name = (TextView) itemView.findViewById(R.id.accountName);
            balance = (TextView) itemView.findViewById(R.id.accountBalance);
            date = (TextView) itemView.findViewById(R.id.accountLastUsed);
            addIncome = (ImageView) itemView.findViewById(R.id.addIncome);
            addExpense = (ImageView) itemView.findViewById(R.id.addExpense);
        }
    }
}
