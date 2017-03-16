package com.netsun.labuy.db;

import com.google.gson.annotations.SerializedName;

import org.litepal.crud.DataSupport;

/**
 * Created by Administrator on 2017/3/16.
 */

public class ReceiveAddress extends DataSupport {
    @SerializedName("id")
    public String addrId;//地址编号
    @SerializedName("name")
    public String consignee;//收件人
    public String mobile;//手机
    public String tel; //固定电话
    public String email;//邮箱地址
    public String regional;//地区对应编码
    public String address;
    public String zip;//邮政编码
    @SerializedName("moren")
    public boolean defaulted;//是否设为默认
    @SerializedName("DataID")
    private int id;//数据表主键

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
}
