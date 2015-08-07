package com.andronomy.moneymanagement.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import com.andronomy.moneymanagement.Lists.ItemData;
import com.andronomy.moneymanagement.R;

import java.util.ArrayList;

/**
 * Created by bagaskara on 7/2/2015.
 */
public class SpinnerAdapter extends ArrayAdapter<ItemData> {
    int mGroupId;
    ArrayList<ItemData> mList;
    LayoutInflater mInflater;

    public SpinnerAdapter(Context context, int groupId, int id, ArrayList<ItemData> list) {
        super(context, groupId, id, list);
        mList = list;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mGroupId = groupId;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View itemView = mInflater.inflate(mGroupId, parent, false);
        ImageView imageView = (ImageView) itemView.findViewById(R.id.spinnerImage);
        TextView textView = (TextView) itemView.findViewById(R.id.spinnerText);
        imageView.setImageResource(getContext().getResources().getIdentifier(mList.get(position).getImageId(), "drawable", getContext().getPackageName()));
        imageView.setContentDescription(mList.get(position).getId() + "");
        char[] array = mList.get(position).getText().toCharArray();
        array[0] = Character.toUpperCase(array[0]);
        textView.setText(new String(array));

        return itemView;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getView(position, convertView, parent);
    }
}
