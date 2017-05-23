package com.netsun.labuy.bean;

/**
 * 分类检索
 * Created by Administrator on 2017/4/13.
 */

public class Category {
    private String name;
    private String logo;
    private String cate_id;
    private String mode;

    public Category(String name, String logo, String goods_id, String mode) {
        setName(name);
        setLogo(logo);
        setGoods_id(goods_id);
        setMode(mode);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getGoods_id() {
        return cate_id;
    }

    public void setGoods_id(String goods_id) {
        this.cate_id = goods_id;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

}
