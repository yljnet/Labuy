package com.netsun.labuy.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.netsun.labuy.R;
import com.netsun.labuy.db.SearchHistory;
import com.netsun.labuy.utils.PublicFunc;

import java.util.List;

/**
 * Created by Administrator on 2017/4/20.
 */

public class SearchHistoryAdapter extends BaseAdapter {
    Context mContext;
    List<SearchHistory> dataList;

    public SearchHistoryAdapter(Context context, List<SearchHistory> list) {
        mContext = context;
        dataList = list;
    }

    @Override
    public int getCount() {
        if (dataList != null)
            return dataList.size();
        else return 0;
    }

    @Override
    public Object getItem(int i) {
        if (dataList != null)
            return dataList.get(i);
        else return null;
    }

    @Override
    public long getItemId(int i) {
        if (dataList != null)
            return i;
        else return 0;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.search_history_item, null, false);
            holder = new ViewHolder();
            holder.messageTV = (TextView) convertView.findViewById(R.id.message_tv);
            holder.modeTV = (TextView) convertView.findViewById(R.id.mode_tv);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        if (dataList != null && dataList.size() > position) {
            SearchHistory item = dataList.get(position);
            holder.messageTV.setText(item.getMessage());
            if (item.getMode().equals(PublicFunc.DEVICER)) {
                holder.modeTV.setText("仪器");
            } else if (item.getMode().equals(PublicFunc.PART)) {
                holder.modeTV.setText("配件耗材");
            } else holder.modeTV.setText("应用方法");
        }
        return convertView;
    }

    class ViewHolder {
        private TextView messageTV;
        private TextView modeTV;
    }
}
