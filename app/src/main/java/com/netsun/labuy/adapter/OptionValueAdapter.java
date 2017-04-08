package com.netsun.labuy.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.netsun.labuy.R;
import com.netsun.labuy.utils.OnValueItemClickListener;

import java.util.List;

/**
 * Created by Administrator on 2017/3/7.
 */

public class OptionValueAdapter extends RecyclerView.Adapter<OptionValueAdapter.MyViewHolder> {
    OnValueItemClickListener valueItemClickListener;
    int index;//选项序号
    int selected;//选项值序号
    List<String> values;

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView value;

        public MyViewHolder(View view) {
            super(view);
            value = (TextView) view.findViewById(R.id.id_value_item_text);
        }
    }

    public OptionValueAdapter(int index, List<String> list, int selected, OnValueItemClickListener listener) {
        this.index = index;
        this.selected = selected;
        values = list;
        valueItemClickListener = listener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.option_value_item, parent, false);
        final MyViewHolder holder = new MyViewHolder(view);
        holder.value.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = holder.getAdapterPosition();
                if (valueItemClickListener != null) {
                    if (selected == position) {
                        position = -1;
                    }
                    selected = position;
                    valueItemClickListener.onClick(index, position);
                }
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        if (holder.value == null)
            holder.value = (TextView) holder.itemView.findViewById(R.id.id_value_item_text);
        holder.value.setText(values.get(position));
        if (position == selected)
            holder.value.setBackgroundResource(R.drawable.button_bg_red);
        else
            holder.value.setBackgroundResource(R.drawable.value_normor);
    }

    @Override
    public int getItemCount() {
        if (values != null)
            return values.size();
        else return 0;
    }
}
