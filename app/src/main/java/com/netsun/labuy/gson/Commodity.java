package com.netsun.labuy.gson;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2017/2/27.
 */

public class Commodity implements Parcelable{
    public String id; //产品编号

    @SerializedName("category")
    public String cate_id; //分类编号

    public String name; //产品名称

    @SerializedName("pic")
    public String logo; //显示图片
    public String brand; //公司名称
    public String price; //价格


    public static final Creator<Commodity> CREATOR = new Creator<Commodity>() {
        @Override
        public Commodity createFromParcel(Parcel source) {
            Commodity commodity = new Commodity();
            commodity.id = source.readString();
            commodity.cate_id = source.readString();
            commodity.name = source.readString();
            commodity.logo = source.readString();
            commodity.brand = source.readString();
            commodity.price = source.readString();
            return commodity;
        }

        @Override
        public Commodity[] newArray(int size) {
            return new Commodity[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(cate_id);
        parcel.writeString(name);
        parcel.writeString(logo);
        parcel.writeString(brand);
        parcel.writeString(price);
    }
}
