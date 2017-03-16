package com.netsun.labuy.gson;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Administrator on 2017/3/8.
 */

public class ProductInfo{
    public String id;//产品编号
    public String name;//产品名称
    public String category;//所属类名
    public String poster;//厂家
    public String price;//价格
    public String pic;//图片信息
    public String country;//国家
    public String number;//库存
    @SerializedName("param")
    public List<ProductParam> params;//产品主要参数
    public List<ProductOption> options;
    public String intro;//产品介绍
    public List<ProductMethod> appMethods;//应用方法
}
