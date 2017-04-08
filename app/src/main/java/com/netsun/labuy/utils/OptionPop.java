package com.netsun.labuy.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.netsun.labuy.R;
import com.netsun.labuy.adapter.OptionItemAdapter;
import com.netsun.labuy.db.ShoppingItem;
import com.netsun.labuy.gson.ProductInfo;
import com.netsun.labuy.gson.ProductOption;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/3/7.
 */

public class OptionPop extends PopupWindow implements View.OnClickListener {
    public static final int STYLE_KEEP = 1;
    public static final int STYLE_BUY = 2;
    public static final int STYLE_SELECT = 3;
    View view;
    Context mContext;
    Button closeButton;
    ImageView littlePicImageView;
    TextView headTextView;
    RecyclerView optionsView;
    ImageButton addButton, minusButton;
    TextView numTextView;
    TextView addShoppingCartBtn, buyBtn, okBtn;
    ProductInfo info;
    private List<ProductOption> validOptions;
    private String unSelect = "", optionSelected = "";
    private OnValueItemClickListener listener;
    private Handler handler;
    int style;
    int num = 1;

    public OptionPop(final Context context, final ProductInfo info, Handler handler, int style, final OnValueItemClickListener listener
    ) {
        super(context);
        mContext = context;
        this.info = info;
        this.style = style;
        this.handler = handler;
        this.listener = listener;
        view = LayoutInflater.from(context).inflate(R.layout.layout_choose_pop, null);
        initView(view);
        if (info != null) {
            String picUrl;
            picUrl = PublicFunc.host + "Public/Uploads/device/" + info.pic;
            Glide
                    .with(mContext)
                    .load(picUrl)
                    .placeholder(R.drawable.loading)
                    .into(new SimpleTarget<GlideDrawable>() {
                        @Override
                        public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                            littlePicImageView.setImageDrawable(resource);
                        }
                    });
        }
        validOptions = getValidOptions();
        if (validOptions != null && validOptions.size() > 0) {
            getUnSelect();
            setHeaderText(unSelect);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
            linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            optionsView.setLayoutManager(linearLayoutManager);
            OptionItemAdapter adapter = new OptionItemAdapter(validOptions, new OnValueItemClickListener() {
                @Override
                public void onClick(int optionIndex, int valueIndex) {
                    unSelect = getUnSelect();
                    LogUtils.d(MyApplication.TAG, unSelect);
                    if (unSelect.isEmpty()) {
                        optionSelected = getOptionSelected();
                        setHeaderText("已选 " + optionSelected);
                    } else
                        setHeaderText("选择 " + unSelect);
                    if (listener != null)
                        listener.onClick(optionIndex, valueIndex);
                }
            });
            optionsView.setAdapter(adapter);
        }
        //设置SelectPicPopupWindow的View
        this.setContentView(view);
        //设置SelectPicPopupWindow弹出窗体的宽
        this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        //设置SelectPicPopupWindow弹出窗体的高
        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        //设置SelectPicPopupWindow弹出窗体可点击
        this.setFocusable(true);
        //设置运行在窗体外点击消失
        this.setOutsideTouchable(true);
        //实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(context.getResources().getColor(R.color.colorLightTrans));
        //设置SelectPicPopupWindow弹出窗体的背景
        this.setBackgroundDrawable(dw);
        // 设置弹出窗体显示时的动画，从底部向上弹出
        this.setAnimationStyle(R.style.take_photo_anim);
    }


    private void initView(View view) {
        closeButton = (Button) view.findViewById(R.id.id_close_button);
        littlePicImageView = (ImageView) view.findViewById(R.id.id_little_pic_image_view);
        headTextView = (TextView) view.findViewById(R.id.id_un_select_option_text);
        addButton = (ImageButton) view.findViewById(R.id.id_add_num);
        minusButton = (ImageButton) view.findViewById(R.id.id_minus_num);
        numTextView = (TextView) view.findViewById(R.id.id_num_text);
        addShoppingCartBtn = (TextView) view.findViewById(R.id.id_add_shopping_cart);
        buyBtn = (TextView) view.findViewById(R.id.id_buy);
        okBtn = (TextView) view.findViewById(R.id.id_ok);
        optionsView = (RecyclerView) view.findViewById(R.id.id_yq_option_list);
        closeButton.setOnClickListener(this);
        addButton.setOnClickListener(this);
        minusButton.setOnClickListener(this);
        addShoppingCartBtn.setOnClickListener(this);
        buyBtn.setOnClickListener(this);
        okBtn.setOnClickListener(this);
    }

    public void setStyle(int style) {
        this.style = style;
        if (STYLE_KEEP == style || STYLE_BUY == style) {
            addShoppingCartBtn.setVisibility(View.GONE);
            buyBtn.setVisibility(View.GONE);
            okBtn.setVisibility(View.VISIBLE);
        } else if (STYLE_SELECT == style) {
            addShoppingCartBtn.setVisibility(View.VISIBLE);
            buyBtn.setVisibility(View.VISIBLE);
            okBtn.setVisibility(View.GONE);
        }
    }

    public List<ProductOption> getValidOptions() {
        List<ProductOption> result = new ArrayList<ProductOption>();
        if (info.options == null) return null;
        for (ProductOption option : info.options) {
            if (option.values != null && option.values.size() > 0) {
                result.add(option);
            }
        }
        return result;
    }

    public String getUnSelect() {
        StringBuilder builder = new StringBuilder("");
        for (ProductOption option : validOptions) {
            if (option.selected == -1) {
                if (builder.length() == 0)
                    builder.append(option.name);
                else
                    builder.append("  ").append(option.name);
            }
        }
        return builder.toString();
    }

    public String getOptionSelected() {
        StringBuilder builder = new StringBuilder("");

        if (validOptions != null) {
            for (ProductOption option : validOptions) {
                if (builder.length() == 0)
                    builder.append(option.name).append(":").append(option.values.get(option.selected));
                else
                    builder.append("|").append(option.name).append(":").append(option.values.get(option.selected));
            }
        } else
            builder.append("数量:" + num + "件");
        return builder.toString();
    }

    public void setHeaderText(String str) {
        headTextView.setText(str);
    }

    public int getNum() {
        return num;
    }

    public void show(View parent) {
        if (!this.isShowing()) {
            showAtLocation(parent, Gravity.BOTTOM, 0, 0);
        } else {
            this.dismiss();
        }
    }

    private void setNumText(String numStr) {
        numTextView.setText(numStr);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.id_close_button:
                show(null);
                break;
            case R.id.id_add_num:
                if (num < Integer.valueOf(info.number)) num++;
                setNumText(String.valueOf(num));
                listener.onClick(-1, -1);
                break;
            case R.id.id_minus_num:
                if (num > 1) num--;
                setNumText(String.valueOf(num));
                listener.onClick(-1, -1);
                break;
            case R.id.id_add_shopping_cart:
                if (validOptions != null && !getUnSelect().isEmpty()) {
                    Toast.makeText(mContext, "请选择 " + unSelect, Toast.LENGTH_SHORT).show();
                } else
                    addProductToShoppingCart();
                break;
            case R.id.id_buy:
                if (validOptions != null && !unSelect.isEmpty()) {
                    Toast.makeText(mContext, "请选择 " + unSelect, Toast.LENGTH_SHORT).show();
                } else {
                    ConfirmOrder();
                }
                break;
            case R.id.id_ok:
                if (validOptions != null && !getUnSelect().isEmpty()) {
                    Toast.makeText(mContext, "请选择 " + unSelect, Toast.LENGTH_SHORT).show();
                } else {
                    if (STYLE_KEEP == style) {
                        addProductToShoppingCart();
                    } else if (STYLE_BUY == style) {
                        ConfirmOrder();
                    }
                }
                break;
            default:
                break;
        }
    }

    private void ConfirmOrder() {
        if (!MyApplication.isLogon) {
            PublicFunc.toLogin((Activity) mContext, 3);
        } else {
            ShoppingItem item = new ShoppingItem();
            item.setGoodsId(info.id);
            item.setOptions(optionSelected);
            item.setNum(num);
            item.setPicUrl(info.pic);
            item.setGoodsName(info.name);
            item.setPrice(info.price);
            ArrayList<ShoppingItem> list = new ArrayList<ShoppingItem>();
            list.add(item);
            Intent intent = new Intent("com.netsun.intent.ACTION_CONFIRM_ORDER");
            Bundle bundle = new Bundle();
            bundle.putParcelableArrayList("shoppinglist", list);
            intent.putExtras(bundle);
            mContext.startActivity(intent);
        }
        show(null);
    }

    public void addProductToShoppingCart() {
        if (!MyApplication.isLogon) {
            PublicFunc.toLogin((Activity) mContext, 3);
        } else {
            ShoppingItem item = DataSupport.where("goodsId=? and options=?",
                    info.id, optionSelected).findFirst(ShoppingItem.class);
            if (item != null) {
                item.setNum(num + item.getNum());
                item.update(item.getId());
            } else {
                item = new ShoppingItem();
                item.setGoodsId(info.id);
                item.setGoodsName(info.name);
                item.setOptions(optionSelected);
                item.setPicUrl(info.pic);
                item.setPrice(info.price);
                item.setNum(num);
                item.setSelected(false);
                item.save();
            }
            show(null);
            Toast.makeText(mContext, "商品成功添加到购物车", Toast.LENGTH_SHORT).show();
        }
    }
}
