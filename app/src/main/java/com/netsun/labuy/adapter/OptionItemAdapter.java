package com.netsun.labuy.adapter;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.netsun.labuy.R;
import com.netsun.labuy.gson.ProductOption;
import com.netsun.labuy.utils.MyApplication;
import com.netsun.labuy.utils.OnValueItemClickListener;
import com.netsun.labuy.utils.SpaceItemDecoration;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/3/7.
 */

public class OptionItemAdapter extends RecyclerView.Adapter<OptionItemAdapter.ViewHolder> {
    private Context mContext;
    private List<ProductOption> optionList;
    private OptionValueAdapter adapter;
    private OnValueItemClickListener listener;
    List<OptionValueAdapter> adapterList;

    public OptionItemAdapter(List<ProductOption> list, OnValueItemClickListener listener) {
        optionList = list;
        this.listener = listener;
        adapterList = new ArrayList<OptionValueAdapter>();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameView;
        RecyclerView valueView;

        public ViewHolder(View itemView) {
            super(itemView);
            nameView = (TextView) itemView.findViewById(R.id.id_choose_item_name_text);
            valueView = (RecyclerView) itemView.findViewById(R.id.id_choose_item_value_list_view);
        }
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.choose_item, parent, false);
        ViewHolder holder = new ViewHolder(view);
        if (holder.nameView == null)
            holder.nameView = (TextView) holder.itemView.findViewById(R.id.id_choose_item_name_text);
        if (holder.valueView == null)
            holder.valueView = (RecyclerView) holder.itemView.findViewById(R.id.id_choose_item_value_list_view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final ProductOption option = optionList.get(position);
        int selected = -1;
        if (option != null) {
            holder.nameView.setText(option.name);
            adapter = new OptionValueAdapter(position, option.values, option.selected, new OnValueItemClickListener() {
                @Override
                public void onClick(int optionIndex, int valueIndex) {
                    option.selected = valueIndex;
                    listener.onClick(optionIndex, valueIndex);
                    adapterList.get(position).notifyDataSetChanged();
                }
            });
            adapterList.add(adapter);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
            linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);

            int spacingInPixels = MyApplication.getContext().getResources().getDimensionPixelSize(R.dimen.space);
            holder.valueView.setLayoutManager(linearLayoutManager);
            holder.valueView.setAdapter(adapter);
            holder.valueView.addItemDecoration(new SpaceItemDecoration(spacingInPixels));
        }
    }

    @Override
    public int getItemCount() {
        if (optionList != null)
            return optionList.size();
        else return 0;
    }


}
