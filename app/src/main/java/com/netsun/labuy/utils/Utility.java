package com.netsun.labuy.utils;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.netsun.labuy.db.City;
import com.netsun.labuy.db.County;
import com.netsun.labuy.db.First;
import com.netsun.labuy.db.Province;
import com.netsun.labuy.db.ReceiveAddress;
import com.netsun.labuy.db.Second;
import com.netsun.labuy.db.Third;
import com.netsun.labuy.gson.Commodity;
import com.netsun.labuy.gson.ProductInfo;
import com.netsun.labuy.gson.YQList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.crud.DataSupport;

import java.util.ArrayList;

import static com.netsun.labuy.utils.MyApplication.token;

/**
 * Created by Administrator on 2017/1/24.
 * 解析和处理json数据
 */

public class Utility {
    /*
    * 解析和处理服务器返回的省级数据
    */
    public static boolean handleProvinceResponse(String response) {
        if (!TextUtils.isEmpty(response)) {
            try {
                JSONObject root = new JSONObject(response);
                if (root.getInt("code") == 200 && root.has("data")) {
                    JSONArray allProvinces = root.getJSONArray("data");
                    for (int i = 0; i < allProvinces.length(); i++) {
                        JSONObject provinceObject = allProvinces.getJSONObject(i);
                        Province province = new Province();
                        province.setProvinceName(provinceObject.getString("name"));
                        province.setProvinceCode(provinceObject.getString("cate_id"));
                        province.saveOrUpdate("provinceCode=?", province.getProvinceCode());
                    }
                    return true;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /*
    *解析和处理服务器返回的市级数据
    */
    public static boolean handleCityResponse(String response, Province province) {
        if (!TextUtils.isEmpty(response)) {
            try {
                JSONObject root = new JSONObject(response);
                if (root.getInt("code") == 200 && root.has("data")) {
                    JSONArray allCities = root.getJSONArray("data");
                    for (int i = 0; i < allCities.length(); i++) {
                        JSONObject cityObject = allCities.getJSONObject(i);
                        City city = new City();
                        city.setCityName(cityObject.getString("name"));
                        city.setCityCode(cityObject.getString("cate_id"));
                        city.setProvinceCode(province.getProvinceCode());
                        city.saveOrUpdate("cityCode=?", city.getCityCode());
                    }
                    return true;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /*
    * 解析和处理服务器返回的县级数据
    */

    public static boolean handleCountyResponse(String response, City city) {
        if (!TextUtils.isEmpty(response)) {
            try {
                JSONObject root = new JSONObject(response);
                if (root.getInt("code") == 200) {
                    if (root.has("data")) {
                        JSONArray allCounties = root.getJSONArray("data");
                        for (int i = 0; i < allCounties.length(); i++) {
                            JSONObject countyObject = allCounties.getJSONObject(i);
                            County county = new County();
                            county.setCountyName(countyObject.getString("name"));
                            county.setCountyCode(countyObject.getString("cate_id"));
                            if (city != null)
                                county.setCityCode(city.getCityCode());
                            county.saveOrUpdate("countyCode=?", county.getCountyCode());//将数据存储到数据库
                        }
                    } else {
                        County county = new County();
                        county.setCountyName(city.getCityName());
                        county.setCountyCode(city.getCityCode());
                        county.setCityCode(city.getCityCode());
                        county.saveOrUpdate("countyCode=?", county.getCountyCode());
                    }
                    return true;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 处理获取收货地址返回信息
     *
     * @param response
     * @return
     */
    public static void handleAddressResponse(String response) {
        try {
            JSONObject root = new JSONObject(response);
            if (root.getInt("code") != 200) return;
            DataSupport.deleteAll(ReceiveAddress.class);
            JSONArray list = root.getJSONArray("data");
            for (int i = 0; i < list.length(); i++) {
                Gson gson = new Gson();
                ReceiveAddress address = gson.fromJson(list.getJSONObject(i).toString(), ReceiveAddress.class);
                address.saveOrUpdate("addrId=?", address.getAddrId());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 处理服务器返回账号登录结果
     *
     * @param response
     */
    public static boolean handleLoginResponse(String response) {
        LogUtils.d(MyApplication.TAG, "response=" + response);
        try {
            JSONObject jsonObject = new JSONObject(response);
            if (jsonObject.getInt("code") == 200) {
                MyApplication.token = jsonObject.getJSONObject("data").getString("token");
                return true;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 处理服务器返回账号登出结果
     *
     * @param response
     */
    public static boolean handleLogoutResponse(String response) {
        LogUtils.d(MyApplication.TAG, response);
        try {
            JSONObject root = new JSONObject(response);
            if (root.getInt("code") == 200) {
                MyApplication.token = null;
                MyApplication.isLogon = false;
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext());
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("token", token);
                editor.commit();
                return true;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 处理服务器返回商品分类第一级分类的返回信息
     *
     * @param response
     * @return
     */
    public static boolean handleFirstResponse(String response) {
        if (!TextUtils.isEmpty(response)) {
            try {
                JSONObject root = new JSONObject(response);
                if (200 == root.getInt("code")) {
                    JSONArray allFirsts = root.getJSONArray("data");
                    for (int i = 0; i < allFirsts.length(); i++) {
                        JSONObject node = allFirsts.getJSONObject(i);
                        First first = new First();
                        first.setId(node.getString("cate_id"));
                        first.setName(node.getString("name"));
                        first.save();
                    }
                    return true;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 处理服务器返回商品分类第二级分类信息
     *
     * @param response
     * @param first_id
     * @return
     */
    public static boolean handleSecondResponse(String response, String first_id) {
        if (!TextUtils.isEmpty(response) && !TextUtils.isEmpty(first_id)) {
            try {
                JSONObject root = new JSONObject(response);
                if (200 == root.getInt("code")) {
                    JSONArray allSeconds = root.getJSONArray("data");
                    for (int i = 0; i < allSeconds.length(); i++) {
                        JSONObject node = allSeconds.getJSONObject(i);
                        Second second = new Second();
                        second.setId(node.getString("cate_id"));
                        second.setParentId(first_id);
                        second.setName(node.getString("name"));
                        second.save();
                    }
                    return true;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 处理服务器返回商品分类第三级分类返回信息
     *
     * @param response
     * @param second_id
     * @return
     */
    public static boolean handleThirdResponse(String response, String second_id) {
        if (!TextUtils.isEmpty(response) && !TextUtils.isEmpty(second_id)) {
            try {
                JSONObject root = new JSONObject(response);
                if (200 == root.getInt("code")) {
                    JSONArray allThirds = root.getJSONArray("data");
                    for (int i = 0; i < allThirds.length(); i++) {
                        JSONObject node = allThirds.getJSONObject(i);
                        Third third = new Third();
                        third.setId(node.getString("cate_id"));
                        third.setParentId(second_id);
                        third.setName(node.getString("name"));
                        third.save();
                    }
                    return true;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 处理服务器返回商品列表信息
     *
     * @param response
     * @return
     */
    public static YQList handleYQListResponse(String response) {
        YQList yqList = new YQList();
        try {
            JSONObject root = new JSONObject(response);
            yqList.code = root.getInt("code");
            yqList.info = root.getString("info");
            if (!root.isNull("data")) {
                JSONArray commodities = root.getJSONArray("data");
                for (int i = 0; i < commodities.length(); i++) {
                    JSONObject node = commodities.getJSONObject(i);
                    Commodity commodity = new Commodity();
                    commodity.id = node.getString("id");
                    commodity.cate_id = node.getString("category");
                    commodity.name = node.getString("name");
                    commodity.brand = node.getString("poster");
                    commodity.logo = node.getString("pic");
                    commodity.price = node.getString("price");
                    if (null == yqList.commodities)
                        yqList.commodities = new ArrayList<Commodity>();
                    yqList.commodities.add(commodity);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return yqList;
    }

    /**
     * 处理请求商品详细信息的响应
     *
     * @param response
     * @return
     */
    public static ProductInfo handleYQInfoResponse(String response) {
        ProductInfo info = null;
        try {
            JSONObject root = new JSONObject(response);
            String dataStr = root.getString("data");
            Gson gson = new Gson();
            info = gson.fromJson(dataStr, ProductInfo.class);
            if (info != null) {
                LogUtils.d(MyApplication.TAG, info.name);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return info;
    }

    /**
     * 处理服务器返回订单信息数据
     *
     * @param response
     * @return
     */
    public static OrderInfo handleOrderInfoResponse(String response) {
        LogUtils.d(MyApplication.TAG, response);
        OrderInfo orderInfo = null;
        try {
            JSONObject root = new JSONObject(response);
            if (root.getInt("code") == 200) {
                String data = root.getString("data");
                if (data != null && !data.isEmpty()) {
                    Gson gson = new Gson();
                    orderInfo = gson.fromJson(data, OrderInfo.class);
                }
            }
        } catch (JSONException e) {
            return null;
        }
        return orderInfo;
    }

    /**
     * 处理服务器返回的订单列表信息
     *
     * @param response
     * @return
     */
    public static ArrayList<OrderInfo> handleOrderListResponse(String response) {
        ArrayList<OrderInfo> orderInfos = null;
        try {
            JSONObject root = new JSONObject(response);
            if (root.getInt("code") == 200) {
                orderInfos = new ArrayList<OrderInfo>();
                JSONArray list = root.getJSONArray("data");
                for (int i = 0; i < list.length(); i++) {
                    OrderInfo orderInfo = new Gson().fromJson(list.getString(i), OrderInfo.class);
                    orderInfos.add(orderInfo);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return orderInfos;
    }
}
