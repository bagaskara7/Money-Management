package com.andronomy.moneymanagement.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.andronomy.moneymanagement.Lists.ItemData;
import com.andronomy.moneymanagement.R;
import com.rey.material.widget.TextView;

import java.util.ArrayList;
import java.util.Currency;
import java.util.Locale;

/**
 * Created by bagaskara on 7/16/2015.
 */
public class SpinnerCurrAdapter extends ArrayAdapter<ItemData> {
    int mGroupId;
    ArrayList<ItemData> mList;
    LayoutInflater mInflater;

    public SpinnerCurrAdapter(Context context, int groupId, int id, ArrayList<ItemData> list) {
        super(context, groupId, id, list);
        mList = list;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mGroupId = groupId;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View itemView = mInflater.inflate(mGroupId, parent, false);
        ImageView imageView = (ImageView) itemView.findViewById(R.id.spinnerImage);
        TextView textView = (TextView) itemView.findViewById(R.id.spinnerText);

        imageView.setImageResource(getContext().getResources().getIdentifier("ic_currency_" + mList.get(position).getText(), "drawable", getContext().getPackageName()));
        imageView.setContentDescription(mList.get(position).getId() + "");

//        DatabaseAdapter lang = new DatabaseAdapter(getContext());
//        Locale.setDefault(new Locale(lang.getActiveLang()));
        int curr = getContext().getResources().getIdentifier("curr_" + mList.get(position).getText(), "string", getContext().getPackageName());
        textView.setText(getContext().getString(curr));


        return itemView;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getView(position, convertView, parent);
    }
}
