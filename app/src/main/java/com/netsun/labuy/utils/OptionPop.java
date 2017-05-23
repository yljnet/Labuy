package com.netsun.labuy.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.netsun.labuy.db.Goods;
import com.netsun.labuy.gson.Merchandise;
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
    ImageButton closeButton;
    ImageView littlePicImageView;
    TextView headTextView;
    RecyclerView optionsView;
    ImageButton addButton, minusButton;
    TextView numTextView;
    TextView addShoppingCartBtn, buyBtn, okBtn;
    Merchandise merchandise;
    private List<ProductOption> validOptions;
    private String unSelect = "", optionSelected = "";
    private OnValueItemClickListener listener;
    private Handler handler;
    int style;
    int num = 1;
    private Goods goods;

    public OptionPop(final Context context, final Merchandise merchandise, Handler handler, int style, final OnValueItemClickListener listener
    ) {
        super(context);
        mContext = context;
        this.merchandise = merchandise;
        this.style = style;
        this.handler = handler;
        this.listener = listener;
        view = LayoutInflater.from(context).inflate(R.layout.layout_choose_pop, null);
        initView(view);
        if (merchandise != null) {
            String picUrl = "";
            if (merchandise.caterory_type.equals(PublicFunc.DEVICER))
            picUrl = PublicFunc.host + "Public/Uploads/device/" + merchandise.pic;
            else if (merchandise.caterory_type.equals(PublicFunc.PART))
                picUrl = PublicFunc.host + "Public/Uploads/parts/" + merchandise.pic;
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
            unSelect = getUnSelect();
            if (unSelect.isEmpty()) {
                optionSelected = getOptionSelected();
                setHeaderText("已选 " + optionSelected);
                goods = DataSupport.where("options=?",optionSelected).findFirst(Goods.class);
                if (goods != null)
                    num = goods.getNum();
            } else
                setHeaderText("选择 " + unSelect);
            numTextView.setText(String.valueOf(num));
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
        closeButton = (ImageButton) view.findViewById(R.id.id_close_button);
        littlePicImageView = (ImageView) view.findViewById(R.id.id_little_pic_image_view);
        headTextView = (TextView) view.findViewById(R.id.id_un_select_option_text);
        addButton = (ImageButton) view.findViewById(R.id.id_add_num);
        minusButton = (ImageButton) view.findViewById(R.id.id_minus_num);
        numTextView = (TextView) view.findViewById(R.id.id_num_text);
        addShoppingCartBtn = (TextView) view.findViewById(R.id.id_add_shopping_cart);
        buyBtn = (TextView) view.findViewById(R.id.id_buy);
        okBtn = (TextView) view.findViewById(R.id.id_ok);
        optionsView = (RecyclerView) view.findViewById(R.id.id_yq_option_list);
        setStyle(style);
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
        if (merchandise.options == null) return null;
        for (ProductOption option : merchandise.options) {
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
        if (!this.isShowing() && parent != null) {
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
                if (num < Integer.valueOf(merchandise.number)) num++;
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
                    MerchandiseToShoppingCart();
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
                        MerchandiseToShoppingCart();
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
            Goods item = new Goods();
            item.setGoodsId(merchandise.id);
            item.setOptions(optionSelected);
            item.setNum(num);
            item.setPicUrl(merchandise.pic);
            item.setGoodsName(merchandise.name);
            item.setGoods_type(merchandise.caterory_type);
            item.setPrice(merchandise.price);
            ArrayList<Goods> list = new ArrayList<Goods>();
            list.add(item);
            Intent intent = new Intent("com.netsun.intent.ACTION_CONFIRM_ORDER");
            Bundle bundle = new Bundle();
            bundle.putParcelableArrayList("shoppinglist", list);
            intent.putExtras(bundle);
            mContext.startActivity(intent);
        }
        show(null);
    }

    //添加商品到购物车
    public void MerchandiseToShoppingCart() {
        if (!MyApplication.isLogon) {
            PublicFunc.toLogin((Activity) mContext, 3);
        } else {
            if (goods != null) {
                goods.setNum(num);
                goods.setOptions(optionSelected);
                goods.update(goods.getId());
                Message msg = new Message();
                msg.what = PublicFunc.HANDLER_CODE_REFRESH_SHOPPINGCART;
                handler.sendMessage(msg);
            } else {
                goods = new Goods();
                goods.setGoodsId(merchandise.id);
                goods.setGoodsName(merchandise.name);
                goods.setOptions(optionSelected);
                goods.setPicUrl(merchandise.pic);
                goods.setPrice(merchandise.price);
                goods.setNum(num);
                goods.setGoods_type(merchandise.caterory_type);
                goods.setSelected(false);
                goods.save();
                Toast.makeText(mContext, "商品成功添加到购物车", Toast.LENGTH_SHORT).show();
            }
            show(null);
        }
    }
}
