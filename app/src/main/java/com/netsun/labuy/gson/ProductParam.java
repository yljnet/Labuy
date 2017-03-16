package com.netsun.labuy.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2017/3/8.
 */

public class ProductParam {
    public String name;
    @SerializedName("attr")
    public String value;
}
