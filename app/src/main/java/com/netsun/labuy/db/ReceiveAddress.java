package com.netsun.labuy.db;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import org.litepal.crud.DataSupport;

/**
 * Created by Administrator on 2017/3/16.
 */

public class ReceiveAddress extends DataSupport implements Parcelable {
    @SerializedName("id")
    private String addrId;//地址编号
    @SerializedName("name")
    private String consignee;//收件人
    private String mobile;//手机
    private String tel; //固定电话
    private String email;//邮箱地址
    private String regional;//地区对应编码
    private String address;
    private String zip;//邮政编码
    @SerializedName("moren")
    private boolean defaulted;//是否设为默认
    @SerializedName("DataID")
    private long id;//数据表主键

    public ReceiveAddress() {
    }

    protected ReceiveAddress(Parcel in) {
        addrId = in.readString();
        consignee = in.readString();
        mobile = in.readString();
        tel = in.readString();
        email = in.readString();
        regional = in.readString();
        address = in.readString();
        zip = in.readString();
        defaulted = in.readByte() != 0;
        id = in.readLong();
    }

    public static final Creator<ReceiveAddress> CREATOR = new Creator<ReceiveAddress>() {
        @Override
        public ReceiveAddress createFromParcel(Parcel in) {
            return new ReceiveAddress(in);
        }

        @Override
        public ReceiveAddress[] newArray(int size) {
            return new ReceiveAddress[size];
        }
    };

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getAddrId() {
        return addrId;
    }

    public void setAddrId(String addrId) {
        this.addrId = addrId;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAddress() {
        return address;
    }

    public String getConsignee() {
        return consignee;
    }

    public void setConsignee(String consignee) {
        this.consignee = consignee;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isDefaulted() {
        return defaulted;
    }

    public void setDefaulted(boolean defaulted) {
        this.defaulted = defaulted;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getRegional() {
        return regional;
    }

    public void setRegional(String regional) {
        this.regional = regional;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(addrId);
        parcel.writeString(consignee);
        parcel.writeString(mobile);
        parcel.writeString(tel);
        parcel.writeString(email);
        parcel.writeString(regional);
        parcel.writeString(address);
        parcel.writeString(zip);
        parcel.writeByte((byte) (defaulted ? 1 : 0));
        parcel.writeLong(id);
    }
}
