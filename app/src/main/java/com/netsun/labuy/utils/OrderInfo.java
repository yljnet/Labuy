package com.netsun.labuy.utils;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.netsun.labuy.db.ShoppingItem;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/3/10.
 */

public class OrderInfo extends DataSupport implements Parcelable{
    //{"code":200,"info":"success","data":{"addrId":"42","token":"b0ba9643d20065f68de40a7d23424636","list":[{"goodsAttr":"","goodsNum":"1","goodsId":"47"}],"orderId":1490086647}}
    @SerializedName("order_sn")
    private String orderId;//订单编号
    @SerializedName("list")
    private ArrayList<ShoppingItem> shoppingItems;
    @SerializedName("address_id")
    private int addressId;//收货地址编号
    private String remark;//备注
    @SerializedName("pay_status")
    private int pay_statue;

    public OrderInfo() {
        super();
    }


    protected OrderInfo(Parcel in) {
        orderId = in.readString();
        shoppingItems = in.createTypedArrayList(ShoppingItem.CREATOR);
        addressId = in.readInt();
        remark = in.readString();
        pay_statue = in.readInt();
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

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public ArrayList<ShoppingItem> getShoppingItems() {
        return shoppingItems;
    }

    public void setShoppingItems(ArrayList<ShoppingItem> shoppingItems) {
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

    public int getPayStatue() {
        return pay_statue;
    }

    public void setPayStatue(int statue) {
        this.pay_statue = statue;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(orderId);
        parcel.writeTypedList(shoppingItems);
        parcel.writeInt(addressId);
        parcel.writeString(remark);
        parcel.writeInt(pay_statue);
    }
}
