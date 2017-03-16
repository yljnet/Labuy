package com.netsun.labuy.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MenuItemAdapter extends BaseAdapter {
    private Context mContext;
    private LayoutInflater mInflater;
    private List<String> mItems;

    public MenuItemAdapter(Context context, ArrayList<String> dataList) {
        mInflater = LayoutInflater.from(context);
        mContext = context;
        mItems = dataList;
    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public Object getItem(int i) {
        return mItems.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        String item = mItems.get(i);
        if (view == null) {
            view = mInflater.inflate(android.R.layout.simple_list_item_1, viewGroup, false);
        }
        TextView textView = (TextView) view;
        textView.setText(item);

        return view;
    }
}
