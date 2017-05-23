package com.netsun.labuy.db;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import org.litepal.crud.DataSupport;

/**
 * Created by Administrator on 2017/3/13.
 */

public class Goods extends DataSupport implements Parcelable{
    private long id;//数据表主键
    @SerializedName("goods_id")
    private String goodsId;
    @SerializedName("goods_name")
    private String goodsName;
    @SerializedName("goods_pic")
    private String picUrl;
    @SerializedName("goods_attr")
    private String options;
    @SerializedName("goods_price")
    private String price;
    @SerializedName("goods_number")
    private int num;//数量
    @SerializedName("cate")
    private String goods_type;//商品类型
    private boolean selected;

    public Goods(){}


    protected Goods(Parcel in) {
        id = in.readLong();
        goodsId = in.readString();
        goodsName = in.readString();
        picUrl = in.readString();
        options = in.readString();
        price = in.readString();
        num = in.readInt();
        goods_type = in.readString();
        selected = in.readByte() != 0;
    }

    public static final Creator<Goods> CREATOR = new Creator<Goods>() {
        @Override
        public Goods createFromParcel(Parcel in) {
            return new Goods(in);
        }

        @Override
        public Goods[] newArray(int size) {
            return new Goods[size];
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

    public String getGoods_type() {
        return goods_type;
    }

    public void setGoods_type(String goods_type) {
        this.goods_type = goods_type;
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
        parcel.writeString(goods_type);
        parcel.writeByte((byte) (selected ? 1 : 0));
    }
}
