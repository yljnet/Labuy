package com.netsun.labuy.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.netsun.labuy.CommodityInfoActivity;
import com.netsun.labuy.R;
import com.netsun.labuy.utils.LogUtils;
import com.netsun.labuy.utils.OrderInfo;

import java.util.List;

/**
 * Created by Administrator on 2017/3/17.
 */

public class OrderInfoAdapter extends RecyclerView.Adapter<OrderInfoAdapter.ViewHolder> {
    List<OrderInfo> orderInfoList;
    Context mContext;

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_order_item, parent,false);
        final ViewHolder holder = new ViewHolder(view);
        holder.productInfoRL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OrderInfo item = orderInfoList.get(holder.getAdapterPosition());
                Intent intent = new Intent(mContext, CommodityInfoActivity.class);
                intent.putExtra("id",item.getGoodsId());
                mContext.startActivity(intent);
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (holder == null) return;
        OrderInfo orderInfo = orderInfoList.get(position);
        if (null==orderInfo)return;
        String picUrl;
        if (LogUtils.level == LogUtils.DEBUG) {
            picUrl = "http://www.dev.labuy.cn/Public/Uploads/565d4fed470a2.jpg";
        } else {
            picUrl = "http://www.dev.labuy.cn/Public/Uploads/" + orderInfo.getPic();
        }
        Glide.with(mContext).load(picUrl).into(holder.productPicIMG);
        holder.productNameTV.setText(orderInfo.getGoodsName());
        holder.productInfoTV.setText(orderInfo.getGoodsAttr());
        holder.productPriceTV.setText(orderInfo.getPrice());
        holder.productNumTV.setText(String.valueOf(orderInfo.getNum()));
    }

    @Override
    public int getItemCount() {
        return orderInfoList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout productInfoRL;
        ImageView productPicIMG;
        TextView productPriceTV;
        TextView productNameTV;
        TextView productInfoTV;
        TextView productNumTV;
        EditText remarkEDIT;

        public ViewHolder(View itemView) {
            super(itemView);
            productInfoRL = (RelativeLayout) itemView.findViewById(R.id.id_product_info_rl);
            productPicIMG = (ImageView) itemView.findViewById(R.id.id_product_pic);
            productPriceTV = (TextView) itemView.findViewById(R.id.id_product_price);
            productNameTV = (TextView) itemView.findViewById(R.id.id_product_name);
            productInfoTV = (TextView) itemView.findViewById(R.id.id_product_info);
            productNumTV = (TextView) itemView.findViewById(R.id.id_num_text);
            remarkEDIT = (EditText) itemView.findViewById(R.id.id_remark_edit_text);
        }
    }
    public OrderInfoAdapter(List<OrderInfo> orderInfos) {
        this.orderInfoList = orderInfos;
    }
}
