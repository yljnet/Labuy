package com.netsun.labuy.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.netsun.labuy.CommodityInfoActivity;
import com.netsun.labuy.R;
import com.netsun.labuy.db.ShoppingItem;
import com.netsun.labuy.utils.OnRemoveShoppingCartItemListener;
import com.netsun.labuy.utils.OnShoppingCartItemSelectedistener;
import com.netsun.labuy.utils.PublicFunc;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/3/13.
 */

public class ShoppingCartItemAdapter extends RecyclerView.Adapter<ShoppingCartItemAdapter.ShoppingCartItemViewHolder> implements View.OnClickListener {
    public static final int STYLE_EDIT = 1;
    public static final int STYLE_ORDER = 2;
    public static final int STYLE_VIEW = 3;
    Context mContext;
    ArrayList<ShoppingItem> shoppingItems;
    ArrayList<ShoppingCartItemViewHolder> holderList = new ArrayList<ShoppingCartItemViewHolder>();
    OnShoppingCartItemSelectedistener onShoppingCartItemSelectedistener;
    OnRemoveShoppingCartItemListener onRemoveShoppingCartItemListener;
    private boolean canClickListen = true;
    private int style = STYLE_VIEW;
    private boolean onBind;
    int num;

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

    public void setCanClickListen(boolean canClickListen) {
        this.canClickListen = canClickListen;
    }

    @Override
    public ShoppingCartItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        View view = LayoutInflater.from(mContext).inflate(R.layout.shopping_cart_item, parent, false);
        final ShoppingCartItemViewHolder holder = new ShoppingCartItemViewHolder(view);
        holder.deleteBtn.setOnClickListener(this);
        holder.addButton.setOnClickListener(this);
        holder.minusButton.setOnClickListener(this);
        holder.goodsCB.setOnClickListener(this);
        holder.rootView.setOnClickListener(this);
        return holder;
    }

    @Override
    public void onBindViewHolder(final ShoppingCartItemViewHolder holder, int position) {
        onBind = true;
        if (holder != null) {
            ShoppingItem item = shoppingItems.get(position);
            String picUrl;
            picUrl = PublicFunc.host + "Public/Uploads/device/" + item.getPicUrl();
            holder.goodsCB.setChecked(item.isSelected());
            Glide
                    .with(mContext)
                    .load(picUrl)
                    .placeholder(R.drawable.loading)
                    .into(new SimpleTarget<GlideDrawable>() {
                        @Override
                        public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                            holder.goodsImage.setImageDrawable(resource);
                        }
                    });
            if (holderList.size() > position)
                holderList.set(position, holder);
            else
                holderList.add(holder);
            holder.setTag(position);
            holder.goodsCB.setTag(position);
            holder.addButton.setTag(position);
            holder.minusButton.setTag(position);
            holder.deleteBtn.setTag(position);
            holder.editOptionTV.setTag(position);
            holder.rootView.setTag(position);
            if (style != STYLE_EDIT) {
                holder.showView.setVisibility(View.VISIBLE);
                holder.editView.setVisibility(View.GONE);
                holder.showNameTV.setText(item.getGoodsName());
                holder.showPriceTV.setText("¥ " + item.getPrice());
                holder.showNumTV.setText("x" + String.valueOf(item.getNum()));
                holder.showOptionTV.setText(item.getOptions());
                if (style == STYLE_ORDER) holder.goodsCB.setVisibility(View.GONE);
            } else {
                holder.showView.setVisibility(View.GONE);
                holder.editView.setVisibility(View.VISIBLE);
                holder.editNumTV.setText(String.valueOf(item.getNum()));
                if (item.getOptions() == null ||item.getOptions().isEmpty())
                    holder.editOptionTV.setVisibility(View.GONE);
                else
                    holder.editOptionTV.setText(item.getOptions());
            }
        }
        onBind = false;
    }

    @Override
    public int getItemCount() {
        if (shoppingItems != null)
            return shoppingItems.size();
        else
            return 0;
    }

    @Override
    public void onClick(View view) {
        final int position = (Integer) view.getTag();
        ShoppingCartItemViewHolder holder = holderList.get(position);
        ShoppingItem item = shoppingItems.get(position);
        switch (view.getId()) {
            case R.id.id_delete_item:
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle("提示");
                builder.setMessage("确定要删除这件商品吗?");
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (onRemoveShoppingCartItemListener != null)
                            onRemoveShoppingCartItemListener.onRemoveItem(position);
                    }
                });
                builder.setNegativeButton("取消",null);
                builder.show();
                break;
            case R.id.id_add_num:
                num = item.getNum();
                num++;
                item.setNum(num);
                item.saveOrUpdate("id=?", String.valueOf(item.getId()));
                holder.editNumTV.setText(String.valueOf(num));
                holder.showNumTV.setText("x" + String.valueOf(num));
                break;
            case R.id.id_minus_num:
                num = item.getNum();
                if (num > 1) {
                    num--;
                    item.setNum(num);
                    holder.editNumTV.setText(String.valueOf(num));
                    holder.showNumTV.setText("x" + String.valueOf(num));
                } else {
                    holder.deleteBtn.callOnClick();
                }
                break;
            case R.id.id_goods_cb:
                boolean isSelect = ((CheckBox) view).isChecked();
                item.setSelected(isSelect);
                if (onShoppingCartItemSelectedistener != null)
                    onShoppingCartItemSelectedistener.onSelected(position, isSelect);
                break;
            case R.id.root_layout:
                if (style == STYLE_VIEW) {
                    Intent intent = new Intent(mContext, CommodityInfoActivity.class);
                    intent.putExtra("id", item.getGoodsId());
                    mContext.startActivity(intent);
                }
                break;
        }
    }

    class ShoppingCartItemViewHolder extends RecyclerView.ViewHolder {
        LinearLayout rootView;
        RelativeLayout showView;
        RelativeLayout editView;
        CheckBox goodsCB;
        ImageView goodsImage;
        Button deleteBtn;
        ImageButton addButton;
        ImageButton minusButton;
        TextView editOptionTV;
        TextView editNumTV;
        TextView showOptionTV;
        TextView showPriceTV;
        TextView showNumTV;
        TextView showNameTV;
        int tag;

        public ShoppingCartItemViewHolder(View itemView) {
            super(itemView);
            rootView = (LinearLayout) itemView.findViewById(R.id.root_layout);
            showView = (RelativeLayout) itemView.findViewById(R.id.id_goods_show_rl);
            editView = (RelativeLayout) itemView.findViewById(R.id.id_goods_edit_rl);
            goodsCB = (CheckBox) itemView.findViewById(R.id.id_goods_cb);
            goodsImage = (ImageView) itemView.findViewById(R.id.id_product_pic);

            editNumTV = (TextView) itemView.findViewById(R.id.id_edit_num_text_view);
            editOptionTV = (TextView) itemView.findViewById(R.id.id_edit_option_text_view);
            addButton = (ImageButton) itemView.findViewById(R.id.id_add_num);
            minusButton = (ImageButton) itemView.findViewById(R.id.id_minus_num);
            deleteBtn = (Button) itemView.findViewById(R.id.id_delete_item);

            showNameTV = (TextView) itemView.findViewById(R.id.id_show_name_text_view);
            showPriceTV = (TextView) itemView.findViewById(R.id.id_show_price_text_view);
            showNumTV = (TextView) itemView.findViewById(R.id.id_show_num_text_view);
            showOptionTV = (TextView) itemView.findViewById(R.id.id_show_option_text_view);
        }

        public int getTag() {
            return tag;
        }

        public void setTag(int tag) {
            this.tag = tag;
        }
    }
}
