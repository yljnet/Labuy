package com.netsun.labuy.adapter;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.netsun.labuy.R;
import com.netsun.labuy.utils.OrderInfo;
import com.netsun.labuy.utils.SpaceItemDecoration;

import java.util.List;

/**
 * Created by Administrator on 2017/3/24.
 */

public class OrderManagerItemAdapter extends RecyclerView.Adapter<OrderManagerItemAdapter.OrderManagerItemViewHolder> {
    Context mContext;
    List<OrderInfo> orderInfoList;
    Handler handler;

    public OrderManagerItemAdapter(List<OrderInfo> orderInfoList) {
        this.orderInfoList = orderInfoList;
    }

    public void setHandler(Handler handler) {
        this.handler = handler;
    }

    @Override
    public OrderManagerItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_manager_item, parent, false);
        final OrderManagerItemViewHolder holder = new OrderManagerItemViewHolder(view);
        holder.rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (handler != null) {
                    Message msg = new Message();
                    msg.what = 1;
                    msg.arg1 = holder.getAdapterPosition();
                    handler.sendMessage(msg);
                }
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(OrderManagerItemViewHolder holder, int position) {
        OrderInfo orderInfo = orderInfoList.get(position);
        if (orderInfo != null && holder != null) {
            holder.orderIdTextView.setText("订单编号  " + orderInfo.getOrderId());
            if (orderInfo.getPayStatue() == 1) {
                holder.orderStatusTextView.setText("已支付");
            } else {
                holder.orderStatusTextView.setText("未支付");
            }
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
            ShoppingCartItemAdapter shoppingCartItemAdapter = new ShoppingCartItemAdapter(orderInfo.getShoppingItems());
            shoppingCartItemAdapter.setStyle(ShoppingCartItemAdapter.STYLE_ORDER);
            shoppingCartItemAdapter.setCanClickListen(false);
            holder.goodsRecyclerView.setLayoutManager(linearLayoutManager);

            holder.goodsRecyclerView.setAdapter(shoppingCartItemAdapter);
            int space = mContext.getResources().getDimensionPixelSize(R.dimen.space_two);
            holder.goodsRecyclerView.addItemDecoration(new SpaceItemDecoration(space));
        }
    }

    @Override
    public int getItemCount() {
        return orderInfoList.size();
    }

    public class OrderManagerItemViewHolder extends RecyclerView.ViewHolder {
        private RelativeLayout rootView;
        private TextView orderIdTextView;
        private TextView orderStatusTextView;
        private RecyclerView goodsRecyclerView;

        public OrderManagerItemViewHolder(View itemView) {
            super(itemView);
            rootView = (RelativeLayout) itemView.findViewById(R.id.id_order_manager_item_rl);
            orderIdTextView = (TextView) itemView.findViewById(R.id.id_order_id_text_view);
            orderStatusTextView = (TextView) itemView.findViewById(R.id.id_order_status_text_view);
            goodsRecyclerView = (RecyclerView) itemView.findViewById(R.id.id_goods_recycler);
        }
    }
}
