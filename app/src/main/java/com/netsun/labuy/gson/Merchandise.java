package com.netsun.labuy.gson;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * 商品信息
 * Created by Administrator on 2017/3/8.
 */

public class Merchandise implements Parcelable {
    public String id;//产品编号
    public String name;//产品名称
    @SerializedName("category")
    public String category_name;//所属类名
    public String poster;//产商
    public String price;//价格
    public String pic;//图片信息
    public String country;//国家
    public String number;//库存
    public String caterory_type;//产品类型
    @SerializedName("param")
    public List<ProductParam> params;//产品主要参数
    public List<ProductOption> options;
    public String intro;//产品介绍
    public List<ProductMethod> appMethods;//应用方法

    public Merchandise() {
    }


    protected Merchandise(Parcel in) {
        id = in.readString();
        name = in.readString();
        category_name = in.readString();
        poster = in.readString();
        price = in.readString();
        pic = in.readString();
        country = in.readString();
        number = in.readString();
        caterory_type = in.readString();
        intro = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(category_name);
        dest.writeString(poster);
        dest.writeString(price);
        dest.writeString(pic);
        dest.writeString(country);
        dest.writeString(number);
        dest.writeString(caterory_type);
        dest.writeString(intro);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Merchandise> CREATOR = new Creator<Merchandise>() {
        @Override
        public Merchandise createFromParcel(Parcel in) {
            return new Merchandise(in);
        }

        @Override
        public Merchandise[] newArray(int size) {
            return new Merchandise[size];
        }
    };
}
