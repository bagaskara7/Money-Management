package com.andronomy.moneymanagement.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
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
 * Created by bagaskara on 7/13/2015.
 */
public class HistoryTransferAdapter extends RecyclerView.Adapter<HistoryTransferAdapter.MyViewHolder> {
    private final LayoutInflater inflater;
    private static DatabaseAdapter databaseAdapter;
    List<Histories> data = Collections.emptyList();

    public HistoryTransferAdapter(Context context, List<Histories> data) {
        inflater = LayoutInflater.from(context);
        this.data = data;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.transfer_history_layout, parent, false);
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

        char[] acc_to = current.acc_to.toCharArray();
        acc_to[0] = Character.toUpperCase(acc_to[0]);

        char[] acc_from = current.acc_from.toCharArray();
        acc_from[0] = Character.toUpperCase(acc_from[0]);

        holder.icon_from.setImageResource(inflater.getContext().getResources().getIdentifier(current.iconId, "drawable", inflater.getContext().getPackageName()));
        holder.icon_to.setImageResource(inflater.getContext().getResources().getIdentifier(current.iconIdTo, "drawable", inflater.getContext().getPackageName()));
        holder.acc_from.setText(new String(acc_from));
        holder.acc_to.setText(new String(acc_to));
        holder.amount.setText(NumberFormat.getCurrencyInstance().format(current.amount));
        holder.date.setText(dateStr);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView icon_from;
        ImageView icon_to;
        TextView acc_from;
        TextView acc_to;
        TextView amount;
        TextView date;

        public MyViewHolder(View itemView) {
            super(itemView);
            icon_from = (ImageView) itemView.findViewById(R.id.history_transfer_image_header);
            icon_to = (ImageView) itemView.findViewById(R.id.history_transfer_image_header_to);
            acc_from = (TextView) itemView.findViewById(R.id.history_transfer_acc);
            acc_to = (TextView) itemView.findViewById(R.id.history_transfer_acc_to);
            amount = (TextView) itemView.findViewById(R.id.history_transfer_amount);
            date = (TextView) itemView.findViewById(R.id.history_transfer_date);
        }
    }
}
