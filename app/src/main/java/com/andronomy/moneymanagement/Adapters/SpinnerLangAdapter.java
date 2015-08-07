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
import java.util.Locale;

/**
 * Created by bagaskara on 7/16/2015.
 * BIF Mobile - Andronomy Studio.
 */
public class SpinnerLangAdapter extends ArrayAdapter<ItemData> {
    int mGroupId;
    ArrayList<ItemData> mList;
    LayoutInflater mInflater;

    public SpinnerLangAdapter(Context context, int groupId, int id, ArrayList<ItemData> list) {
        super(context, groupId, id, list);
        mList = list;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mGroupId = groupId;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View itemView = mInflater.inflate(mGroupId, parent, false);
        ImageView imageView = (ImageView) itemView.findViewById(R.id.spinnerImage);
        TextView textView = (TextView) itemView.findViewById(R.id.spinnerText);

        String[] lang = mList.get(position).getImageId().split("_");
        imageView.setImageResource(getContext().getResources().getIdentifier("ic_flag_" + lang[0] + "_" + lang[1], "drawable", getContext().getPackageName()));
        imageView.setContentDescription(mList.get(position).getId() + "");

        DatabaseAdapter langs = new DatabaseAdapter(getContext());
        Locale.setDefault(new Locale(langs.getActiveLang()));

        Locale locale = new Locale(lang[0], lang[1].toUpperCase());
        String language = locale.getDisplayLanguage();

        char[] array = language.toCharArray();
        array[0] = Character.toUpperCase(array[0]);
        String english = "";
//        if (lang[0].equals("en")) {
//            english = (lang[1].equals("us")) ? " (" + getContext().getString(R.string.eng_us_str) + ")" : " (" + getContext().getString(R.string.eng_uk_str) + ")";
//        }

        textView.setText(new String(array) + english);

        return itemView;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getView(position, convertView, parent);
    }
}