package com.netsun.labuy.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.netsun.labuy.CommodityInfoActivity;
import com.netsun.labuy.R;
import com.netsun.labuy.db.ShoppingItem;
import com.netsun.labuy.utils.LogUtils;
import com.netsun.labuy.utils.OnRemoveShoppingCartItemListener;
import com.netsun.labuy.utils.OnShoppingCartItemSelectedistener;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/3/13.
 */

public class ShoppingCartItemAdapter extends RecyclerView.Adapter<ShoppingCartItemAdapter.ShoppingCartItemViewHolder> {
    public static final int STYLE_EDIT = 1;
    public static final int STYLE_ORDER = 2;
    public static final int STYLE_VIEW = 3;
    Context mContext;
    ArrayList<ShoppingItem> shoppingItems;
    OnShoppingCartItemSelectedistener onShoppingCartItemSelectedistener;
    OnRemoveShoppingCartItemListener onRemoveShoppingCartItemListener;
    private int style = STYLE_VIEW;

    public void setStyle(int style) {
        this.style = style;
    }

    public void setOnRemoveShoppingCartItemListener(OnRemoveShoppingCartItemListener onRemoveShoppingCartItemListener) {
        this.onRemoveShoppingCartItemListener = onRemoveShoppingCartItemListener;
    }

    public void setOnShoppingCartItemSelectedistener(OnShoppingCartItemSelectedistener onShoppingCartItemSelectedistener) {
        this.onShoppingCartItemSelectedistener = onShoppingCartItemSelectedistener;
    }

    public ShoppingCartItemAdapter(ArrayList<ShoppingItem> shoppingItems) {
        this.shoppingItems = shoppingItems;
    }

    @Override
    public ShoppingCartItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        View view = LayoutInflater.from(mContext).inflate(R.layout.shopping_cart_item, parent, false);
        final ShoppingCartItemViewHolder holder = new ShoppingCartItemViewHolder(view);
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "确定要删除这件商品吗?", Snackbar.LENGTH_LONG).
                        setAction("确定", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                int pos = holder.getAdapterPosition();
                                if (onRemoveShoppingCartItemListener != null)
                                    onRemoveShoppingCartItemListener.onRemoveItem(pos);
                            }
                        }).show();
            }
        });
        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (onShoppingCartItemSelectedistener != null)
                    onShoppingCartItemSelectedistener.onSelected(holder.getAdapterPosition(),b);
            }
        });
        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShoppingItem item = shoppingItems.get(holder.getAdapterPosition());
                Intent intent = new Intent(mContext, CommodityInfoActivity.class);
                intent.putExtra("id",item.getGoodsId());
                mContext.startActivity(intent);
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(ShoppingCartItemViewHolder holder, int position) {
        if (holder != null) {
            ShoppingItem item = shoppingItems.get(position);
            String picUrl;
            if (LogUtils.level == LogUtils.DEBUG) {
                picUrl = "http://www.dev.labuy.cn/Public/Uploads/565d4fed470a2.jpg";
            } else {
                picUrl = "http://www.dev.labuy.cn/Public/Uploads/" + item.getPicUrl();
            }
            holder.checkBox.setChecked(item.isSelected());
            Glide.with(mContext).load(picUrl).into(holder.pic);
            holder.info.setText(item.getGoodsName());
            holder.price.setText("¥ " + item.getPrice());
            holder.num.setText("x" + String.valueOf(item.getNum()));
            if (STYLE_EDIT == style) {
                holder.price.setVisibility(View.GONE);
                holder.num.setVisibility(View.GONE);
                holder.delete.setVisibility(View.VISIBLE);
            } else if (STYLE_EDIT == style){
                holder.price.setVisibility(View.VISIBLE);
                holder.num.setVisibility(View.VISIBLE);
                holder.delete.setVisibility(View.GONE);
            } else if (STYLE_ORDER == style){
                holder.checkBox.setVisibility(View.GONE);
                holder.price.setVisibility(View.VISIBLE);
                holder.num.setVisibility(View.VISIBLE);
                holder.delete.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public int getItemCount() {
        if (shoppingItems != null)
            return shoppingItems.size();
        else
            return 0;
    }

    class ShoppingCartItemViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout relativeLayout;
        CheckBox checkBox;
        ImageView pic;
        Button delete;
        TextView info;
        TextView price;
        TextView num;

        public ShoppingCartItemViewHolder(View itemView) {
            super(itemView);
            relativeLayout = (RelativeLayout)itemView.findViewById(R.id.shopping_item_rl);
            checkBox = (CheckBox) itemView.findViewById(R.id.id_select_cb);
            pic = (ImageView) itemView.findViewById(R.id.id_product_pic);
            delete = (Button) itemView.findViewById(R.id.id_delete_item);
            info = (TextView) itemView.findViewById(R.id.id_product_info);
            price = (TextView) itemView.findViewById(R.id.id_product_price);
            num = (TextView) itemView.findViewById(R.id.id_num_text);
        }
    }
}
