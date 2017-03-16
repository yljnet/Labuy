package com.netsun.labuy.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.netsun.labuy.CommodityInfoActivity;
import com.netsun.labuy.R;
import com.netsun.labuy.gson.Commodity;
import com.netsun.labuy.utils.LogUtils;

import java.util.List;

/**
 * Created by Administrator on 2017/2/27.
 */

public class CommodityAdapter extends RecyclerView.Adapter<CommodityAdapter.ViewHolder> {
    private Context mContext;
    private List<Commodity> commodityList;

    static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        ImageView image;
        TextView price;
        TextView name;

        public ViewHolder(View itemView) {
            super(itemView);
            cardView = (CardView) itemView;
            image = (ImageView) cardView.findViewById(R.id.commodity_image);
            price = (TextView) cardView.findViewById(R.id.commodity_price);
            name = (TextView) cardView.findViewById(R.id.commodity_name);
        }
    }

    public CommodityAdapter(List<Commodity> commodities) {
        commodityList = commodities;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mContext == null) {
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.commodity_item, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = holder.getAdapterPosition();
                Commodity commodity = commodityList.get(position);
                if (commodity != null) {
                    Intent intent = new Intent(mContext, CommodityInfoActivity.class);
                    intent.putExtra("id",commodity.id);
                    mContext.startActivity(intent);
                }
            }
        };
        holder.price.setOnClickListener(onClickListener);
        holder.name.setOnClickListener(onClickListener);
        holder.image.setOnClickListener(onClickListener);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (commodityList == null) return;
        Commodity commodity = commodityList.get(position);
        holder.name.setText(commodity.name);
        holder.price.setText("¥ "+commodity.price);
        if (LogUtils.level == LogUtils.DEBUG)
            commodity.logo = "http://www.dev.labuy.cn/Public/Uploads/565d4fed470a2.jpg";
        else
            commodity.logo = "http://www.dev.labuy.cn/Public/Uploads/"+commodity.logo;
        Glide.with(mContext).load(commodity.logo).into(holder.image);
    }

    @Override
    public int getItemCount() {
        if (commodityList != null)
            return commodityList.size();
        else
            return 0;
    }


}
