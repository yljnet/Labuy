package com.netsun.labuy.db;

import org.litepal.crud.DataSupport;

/**
 * Created by Administrator on 2017/3/13.
 */

public class ShoppingItem extends DataSupport {
    private long id;//数据表主键
    public String goodsId;
    public String goodsName;
    public String picUrl;
    public String options;
    public String price;
    public int num;
    public boolean selected;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(String goodsId) {
        this.goodsId = goodsId;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public String getOptions() {
        return options;
    }

    public void setOptions(String options) {
        this.options = options;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public boolean isSelected(){
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
