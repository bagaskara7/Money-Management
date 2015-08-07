package com.andronomy.moneymanagement.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.andronomy.moneymanagement.Lists.Histories;
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
 * Created by bagaskara on 7/7/2015.
 */
public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.MyViewHolder> {
    private final LayoutInflater inflater;
    private DatabaseAdapter databaseAdapter;
    List<Histories> data = Collections.emptyList();

    public HistoryAdapter(Context context, List<Histories> data) {
        inflater = LayoutInflater.from(context);
        this.data = data;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.history_layout, parent, false);
        databaseAdapter = new DatabaseAdapter(view.getContext());
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Histories current = data.get(position);

        String dateStr = current.date;
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date newDate = null;
        try {
            newDate = format.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        format = SimpleDateFormat.getDateTimeInstance(SimpleDateFormat.DEFAULT, SimpleDateFormat.SHORT, new Locale(databaseAdapter.getActiveLang()));//new SimpleDateFormat("EEE, dd/MMM/yy. HH:mm.");
        dateStr = format.format(newDate);

        char[] category = current.category.toCharArray();
        category[0] = Character.toUpperCase(category[0]);

        char[] account = current.account.toCharArray();
        account[0] = Character.toUpperCase(account[0]);

        holder.icon.setImageResource(inflater.getContext().getResources().getIdentifier(current.iconId, "drawable", inflater.getContext().getPackageName()));
        holder.notes.setText(current.notes);
        holder.account.setText(inflater.getContext().getString(R.string.account) + " " + new String(account));
        holder.category.setText(new String(category));
        holder.amount.setText(NumberFormat.getCurrencyInstance().format(current.amount));
        holder.date.setText(dateStr);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView icon;
        TextView amount;
        TextView category;
        TextView account;
        TextView notes;
        TextView date;

        public MyViewHolder(View itemView) {
            super(itemView);
            icon = (ImageView) itemView.findViewById(R.id.historyImage);
            amount = (TextView) itemView.findViewById(R.id.historyAmount);
            account = (TextView) itemView.findViewById(R.id.historyAccount);
            category = (TextView) itemView.findViewById(R.id.historyCategory);
            notes = (TextView) itemView.findViewById(R.id.historyText);
            date = (TextView) itemView.findViewById(R.id.historyDate);
        }
    }
}
