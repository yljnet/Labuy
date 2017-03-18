package com.netsun.labuy.db;

import android.os.Parcel;
import android.os.Parcelable;

import org.litepal.crud.DataSupport;

/**
 * Created by Administrator on 2017/3/13.
 */

public class ShoppingItem extends DataSupport implements Parcelable{
    private long id;//数据表主键
    private String goodsId;
    private String goodsName;
    private String picUrl;
    private String options;
    private String price;
    private int num;
    private boolean selected;

    public ShoppingItem(){}

    protected ShoppingItem(Parcel in) {
        id = in.readLong();
        goodsId = in.readString();
        goodsName = in.readString();
        picUrl = in.readString();
        options = in.readString();
        price = in.readString();
        num = in.readInt();
        selected = in.readByte() != 0;
    }

    public static final Creator<ShoppingItem> CREATOR = new Creator<ShoppingItem>() {
        @Override
        public ShoppingItem createFromParcel(Parcel in) {
            return new ShoppingItem(in);
        }

        @Override
        public ShoppingItem[] newArray(int size) {
            return new ShoppingItem[size];
        }
    };

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

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(id);
        parcel.writeString(goodsId);
        parcel.writeString(goodsName);
        parcel.writeString(picUrl);
        parcel.writeString(options);
        parcel.writeString(price);
        parcel.writeInt(num);
        parcel.writeByte((byte) (selected ? 1 : 0));
    }
}
