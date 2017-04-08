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
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.netsun.labuy.CommodityInfoActivity;
import com.netsun.labuy.R;
import com.netsun.labuy.gson.Commodity;
import com.netsun.labuy.utils.PublicFunc;

import java.util.List;

/**
 * Created by Administrator on 2017/2/27.
 */

public class CommodityAdapter extends RecyclerView.Adapter<CommodityAdapter.ViewHolder> {
    private Context mContext;
    private List<Commodity> commodityList;
    private boolean onBind;

    static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        ImageView image;
        TextView price;
        TextView name;

        public ViewHolder(View itemView) {
            super(itemView);
            cardView = (CardView) itemView;
            image = (ImageView) cardView.findViewById(R.id.goods_image);
            price = (TextView) cardView.findViewById(R.id.goods_price);
            name = (TextView) cardView.findViewById(R.id.goods_name);
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
        View view = LayoutInflater.from(mContext).inflate(R.layout.goods_item, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = holder.getAdapterPosition();
                Commodity commodity = commodityList.get(position);
                if (commodity != null) {
                    Intent intent = new Intent(mContext, CommodityInfoActivity.class);
                    intent.putExtra("id", commodity.id);
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
    public void onBindViewHolder(final ViewHolder holder, int position) {
        onBind = true;
        if (commodityList == null) return;
        final Commodity commodity = commodityList.get(position);
        holder.name.setText(commodity.name);
        holder.price.setText("¥ " + commodity.price);
        String picUrl = PublicFunc.host + "Public/Uploads/device/" + commodity.logo;
        String tag = (String) holder.image.getTag(R.id.imageloader_uri);
        String uri = commodity.logo;
        if (!uri.equals(tag)) {
            holder.image.setScaleType(ImageView.ScaleType.CENTER);
            Glide.with(mContext).load(R.drawable.loading).asGif().into(holder.image);
        }
        holder.image.setScaleType(ImageView.ScaleType.FIT_XY);
        Glide
                .with(mContext)
                .load(picUrl)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.image);
        holder.image.setTag(R.id.imageloader_uri, commodity.logo);
        onBind = false;
    }

    @Override
    public int getItemCount() {
        if (commodityList != null)
            return commodityList.size();
        else
            return 0;
    }


}
