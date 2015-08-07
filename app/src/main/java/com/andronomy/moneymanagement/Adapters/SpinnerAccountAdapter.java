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
 * Created by bagaskara on 7/18/2015.
 */
public class SpinnerAccountAdapter extends ArrayAdapter<ItemData> {
    int mGroupId;
    ArrayList<ItemData> mList;
    LayoutInflater mInflater;

    public SpinnerAccountAdapter(Context context, int groupId, int id, ArrayList<ItemData> list) {
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
        String title;
        if (mList.get(position).getId() < 20) {
            int account = itemView.getContext().getResources().getIdentifier("account_" + mList.get(position).getText(), "string", itemView.getContext().getPackageName());
            title = itemView.getContext().getString(account);
        } else {
            char[] array = mList.get(position).getText().toCharArray();
            array[0] = Character.toUpperCase(array[0]);
            title = new String(array);
        }        textView.setText(title);

        return itemView;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getView(position, convertView, parent);
    }
}
