package com.netsun.labuy.gson;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Administrator on 2017/3/2.
 */

public class YQList {
    public int code;
    public String info;
    @SerializedName("data")
    public List<Commodity> commodities;
}
