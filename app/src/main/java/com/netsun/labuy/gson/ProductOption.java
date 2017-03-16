package com.netsun.labuy.gson;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/3/7.
 */

public class ProductOption {
    public String name;
    @SerializedName("attr")
    public ArrayList<String> values;
    public int selected = -1;
}