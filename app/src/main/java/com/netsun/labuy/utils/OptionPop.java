package com.netsun.labuy.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.netsun.labuy.CommodityInfoActivity;
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
    Context mContext;
    TextView headTextView;
    RecyclerView optionsView;
    ImageButton addButton, minusButton;
    TextView numTextView;
    TextView addShoppingCartBtn, buyBtn, okBtn;
    ProductInfo info;
    private List<Option> selectOption = new ArrayList<Option>();
    private String unSelect, optionSelected = "";
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
        View view = LayoutInflater.from(context).inflate(R.layout.layout_choose_pop, null);
        initView(view);
        if (info.options != null) {
            unSelect = ((CommodityInfoActivity) context).getUnSelect();
            setHeaderText(unSelect);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
            linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            optionsView.setLayoutManager(linearLayoutManager);
            OptionItemAdapter adapter = new OptionItemAdapter(info.options, new OnValueItemClickListener() {
                @Override
                public void onClick(int optionIndex, int valueIndex) {
                    unSelect = ((CommodityInfoActivity) context).getUnSelect();
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
        //实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(context.getResources().getColor(R.color.colorWhiteSmoke));
        //设置SelectPicPopupWindow弹出窗体的背景
        this.setBackgroundDrawable(dw);
        // 设置弹出窗体显示时的动画，从底部向上弹出
        this.setAnimationStyle(R.style.take_photo_anim);
    }

    private void initView(View view) {
        headTextView = (TextView) view.findViewById(R.id.id_un_select_option_text);
        addButton = (ImageButton) view.findViewById(R.id.id_add_num);
        minusButton = (ImageButton) view.findViewById(R.id.id_minus_num);
        numTextView = (TextView) view.findViewById(R.id.id_num_text);
        addShoppingCartBtn = (TextView) view.findViewById(R.id.id_add_shopping_cart);
        buyBtn = (TextView) view.findViewById(R.id.id_buy);
        okBtn = (TextView) view.findViewById(R.id.id_ok);
        optionsView = (RecyclerView) view.findViewById(R.id.id_yq_option_list);
        addButton.setOnClickListener(this);
        minusButton.setOnClickListener(this);
        addShoppingCartBtn.setOnClickListener(this);
        buyBtn.setOnClickListener(this);
        okBtn.setOnClickListener(this);
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

    public String getOptionSelected() {
        StringBuilder builder = new StringBuilder("");
        if (info.options != null) {
            for (ProductOption option : info.options) {
                if (builder.length() == 0)
                    builder.append(option.name).append(":").append(option.values.get(option.selected));
                else
                    builder.append("|").append(option.name).append(":").append(option.values.get(option.selected));
            }
        }
        return builder.toString();
    }

    private void setNumText(String numStr) {
        numTextView.setText(numStr);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.id_add_num:
                if (num < Integer.valueOf(info.number)) num++;
                setNumText(String.valueOf(num));
                listener.onClick(-1,-1);
                break;
            case R.id.id_minus_num:
                if (num > 1) num--;
                setNumText(String.valueOf(num));
                listener.onClick(-1,-1);
                break;
            case R.id.id_add_shopping_cart:
                if (info.options != null && !unSelect.isEmpty()) {
                    Toast.makeText(mContext, "请选择 " + unSelect, Toast.LENGTH_SHORT).show();
                } else
                    addProductToShoppingCart();
                break;
            case R.id.id_buy:
                if (info.options != null && !unSelect.isEmpty()) {
                    Toast.makeText(mContext, "请选择 " + unSelect, Toast.LENGTH_SHORT).show();
                } else {

                }
                break;
            case R.id.id_ok:
                if (info.options != null && !unSelect.isEmpty()) {
                    Toast.makeText(mContext, "请选择 " + unSelect, Toast.LENGTH_SHORT).show();
                } else {
                    if (STYLE_KEEP == style) {
                        addProductToShoppingCart();
                    } else if (STYLE_BUY == style) {

                    }
                }
                break;
            default:
                break;
        }
    }

    public void addProductToShoppingCart() {
        if (!MyApplication.isLogon) {
            PublicFunc.toLogin((Activity) mContext, 3);
        } else {
            ShoppingItem item = DataSupport.where("goodsId=? and options=?",
                    info.id, optionSelected).findFirst(ShoppingItem.class);
            if (item != null) {
                item.setNum(num + item.getNum());
//                item.updateAll("goodsId = ? and options=?", item.goodsId, item.options);
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
