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
import com.netsun.labuy.activity.MerchandiseDisplayActivity;
import com.netsun.labuy.R;
import com.netsun.labuy.gson.Merchandise;
import com.netsun.labuy.utils.PublicFunc;

import java.util.List;

/**
 * 商品列表适配器
 * Created by Administrator on 2017/2/27.
 */

public class MerchandiseAdapter extends RecyclerView.Adapter<MerchandiseAdapter.MerchandiseViewHolder> {
    private Context mContext;
    private List<Merchandise> merchandiseList;
    private boolean onBind;

    static class MerchandiseViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        ImageView image;
        TextView price;
        TextView name;

        public MerchandiseViewHolder(View itemView) {
            super(itemView);
            cardView = (CardView) itemView;
            image = (ImageView) cardView.findViewById(R.id.goods_image);
            price = (TextView) cardView.findViewById(R.id.goods_price);
            name = (TextView) cardView.findViewById(R.id.goods_name);
        }
    }

    public MerchandiseAdapter(List<Merchandise> merchandises) {
        merchandiseList = merchandises;
    }

    @Override
    public MerchandiseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mContext == null) {
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.goods_item, parent, false);
        final MerchandiseViewHolder holder = new MerchandiseViewHolder(view);
        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = holder.getAdapterPosition();
                Merchandise merchandise = merchandiseList.get(position);
                if (merchandise != null) {
                    Intent intent = new Intent(mContext, MerchandiseDisplayActivity.class);
                    intent.putExtra("mode", merchandise.caterory_type);
                    intent.putExtra("id", merchandise.id);
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
    public void onBindViewHolder(final MerchandiseViewHolder holder, int position) {
        onBind = true;
        if (merchandiseList == null) return;
        final Merchandise merchandise = merchandiseList.get(position);
        String tag = (String) holder.image.getTag(R.id.imageloader_uri);
        String uri = merchandise.pic;
        holder.name.setText(merchandise.name);
        holder.price.setText("¥ " + merchandise.price);
        String picUrl = null;
        if (merchandise.caterory_type.equals(PublicFunc.DEVICER)) {
            picUrl = PublicFunc.host + "Public/Uploads/device/" + uri;
        } else if (merchandise.caterory_type.equals(PublicFunc.PART)) {
            picUrl = PublicFunc.host + "Public/Uploads/parts/" + uri;
        }
        if (!uri.equals(tag)) {
            Glide.with(mContext).load(R.drawable.placeholder).into(holder.image);
        }
        Glide.with(mContext)
                .load(picUrl)
                .placeholder(R.drawable.placeholder)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.image);
        holder.image.setTag(R.id.imageloader_uri, uri);
        onBind = false;
    }

    @Override
    public int getItemCount() {
        if (merchandiseList != null)
            return merchandiseList.size();
        else
            return 0;
    }


}
