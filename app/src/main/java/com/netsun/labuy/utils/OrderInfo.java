package com.netsun.labuy.utils;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Administrator on 2017/3/10.
 */

public class OrderInfo implements Parcelable {
    private String id;//订单编号
    private String goodsId;//商品编号
    private String pic;
    private int num ;//购买数量
    private String goodsAttr;//商品规格
    private String price;//价格
    private int addressId;//收货地址编号
    private String remark;//备注
    public OrderInfo() {
        super();
    }

    protected OrderInfo(Parcel in) {
        id = in.readString();
        goodsId = in.readString();
        pic = in.readString();
        num = in.readInt();
        goodsAttr = in.readString();
        price = in.readString();
        addressId = in.readInt();
        remark = in.readString();
    }

    public static final Creator<OrderInfo> CREATOR = new Creator<OrderInfo>() {
        @Override
        public OrderInfo createFromParcel(Parcel in) {
            return new OrderInfo(in);
        }

        @Override
        public OrderInfo[] newArray(int size) {
            return new OrderInfo[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(String goodsId) {
        this.goodsId = goodsId;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public String getGoodsAttr() {
        return goodsAttr;
    }

    public void setGoodsAttr(String goodsAttr) {
        this.goodsAttr = goodsAttr;
    }

    public int getAddressId() {
        return addressId;
    }

    public void setAddressId(int addressId) {
        this.addressId = addressId;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(goodsId);
        parcel.writeString(pic);
        parcel.writeInt(num);
        parcel.writeString(goodsAttr);
        parcel.writeString(price);
        parcel.writeInt(addressId);
        parcel.writeString(remark);
    }
}
