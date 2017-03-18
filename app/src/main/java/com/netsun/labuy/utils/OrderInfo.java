package com.netsun.labuy.utils;

import android.os.Parcel;
import android.os.Parcelable;

import com.netsun.labuy.db.ShoppingItem;

import java.util.List;

/**
 * Created by Administrator on 2017/3/10.
 */

public class OrderInfo implements Parcelable {
    private String id;//订单编号
    private List<ShoppingItem> shoppingItems;
    private int addressId;//收货地址编号
    private String remark;//备注
    public OrderInfo() {
        super();
    }

    protected OrderInfo(Parcel in) {
        id = in.readString();
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

    public List<ShoppingItem> getShoppingItems() {
        return shoppingItems;
    }

    public void setShoppingItems(List<ShoppingItem> shoppingItems) {
        this.shoppingItems = shoppingItems;
    }

    public int getAddressId() {
        return addressId;
    }

    public void setAddressId(int addressId) {
        this.addressId = addressId;
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
        parcel.writeInt(addressId);
        parcel.writeString(remark);
    }
}
