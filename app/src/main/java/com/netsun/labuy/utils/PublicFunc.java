package com.netsun.labuy.utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Base64;

import com.google.gson.Gson;
import com.netsun.labuy.LoginActivity;
import com.netsun.labuy.db.First;
import com.netsun.labuy.db.ReceiveAddress;
import com.netsun.labuy.db.Second;
import com.netsun.labuy.db.ShoppingItem;
import com.netsun.labuy.db.Third;
import com.netsun.labuy.gson.Commodity;
import com.netsun.labuy.gson.ProductInfo;
import com.netsun.labuy.gson.YQList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.netsun.labuy.utils.MyApplication.TAG;
import static com.netsun.labuy.utils.MyApplication.getContext;
import static com.netsun.labuy.utils.MyApplication.isLogon;
import static com.netsun.labuy.utils.MyApplication.token;

/**
 * Created by Administrator on 2017/2/27.
 */

public class PublicFunc {
    public static String work = "search_yq_by_cate_id";
    private static ProgressDialog progressDialog;

    /**
     * 显示进度对话框
     */

    public static void showProgress(Context context) {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("正在加载...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
    }

    /**
     * 关闭进度对话框
     */
    public static void closeProgress() {
        if (progressDialog != null)
            progressDialog.dismiss();
    }

    public static void toLogin(Activity activity, int index) {
        Intent intent = new Intent(activity, LoginActivity.class);
        intent.putExtra("index", index);
        activity.startActivityForResult(intent, 1);
    }

    public static void saveData(String account, String password, String aToken) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("account", account);
        editor.putString("password", password);
        editor.putString("token", aToken);
        editor.commit();
    }

    public static String getToken() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        String s = preferences.getString("token", "");
        if (s != null) {
            return new String(Base64.decode(s.getBytes(), Base64.DEFAULT));
        } else
            return null;
    }

    public static String[] getUserAndPassword() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        String account = preferences.getString("account", "");
        String password = preferences.getString("password", "");
        if (account != null && password != null) {
            String[] arr = {new String(Base64.decode(account.getBytes(), Base64.DEFAULT)),
                    new String(Base64.decode(password.getBytes(), Base64.DEFAULT))};
            return arr;
        } else {
            return null;
        }
    }

    /**
     * 提交订单
     *
     * @param token
     * @param orderInfo
     * @param callback
     */
    public static void submitOrder(String token, OrderInfo orderInfo, Callback callback) {
        JSONObject root = new JSONObject();
        try {
            JSONArray list = new JSONArray();
            for (ShoppingItem item : orderInfo.getShoppingItems()) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("goodsId", item.getGoodsId())
                        .put("goodsNum", item.getNum())
                        .put("goodsAttr", item.getOptions());
                list.put(jsonObject);
            }
            root.put("addrId", orderInfo.getAddressId())
                    .put("token", MyApplication.token)
                    .put("list", list);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        LogUtils.d(MyApplication.TAG, root.toString());
        String url = "http://www.dev.labuy.cn/app.php/Order";
        RequestBody body = new FormBody.Builder().add("data", root.toString()).build();
        HttpUtils.post(url, body, callback);
    }

    /**
     * 账号登录
     *
     * @param account
     * @param password
     * @param listener
     */
    public static void login(final String account, final String password, final LogonListener listener) {
        String url = "http://www.dev.labuy.cn/app.php/Auth";
        RequestBody body = new FormBody.Builder().add("name", account)
                .add("password", Md5Utils.encode(password)).build();
        HttpUtils.post(url, body, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (listener != null) {
                    listener.LogonResult(false);
                }
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                boolean result = false;
                String resText = response.body().string();
                LogUtils.d(TAG, "response=" + resText);
                try {
                    JSONObject jsonObject = new JSONObject(resText);
                    if (jsonObject.getInt("code") == 200) {
                        String token = jsonObject.getJSONObject("data").getString("token");
                        MyApplication.token = token;
                        saveData(Base64.encodeToString(account.getBytes(), Base64.DEFAULT),
                                Base64.encodeToString(password.getBytes(), Base64.DEFAULT),
                                Base64.encodeToString(token.getBytes(), Base64.DEFAULT));
                        isLogon = true;
                        result = true;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (listener != null) listener.LogonResult(result);
            }
        });
    }

    /**
     * 账号登出
     *
     * @param listener
     */
    public static void logout(final LogoutListener listener) {

        String[] arr = getUserAndPassword();
        if (arr == null) return;
        String url = "http://www.dev.labuy.cn/app.php/Auth?name=" + arr[0] + "&token=" + token;
        LogUtils.d(TAG, url);
        HttpUtils.get(url, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (listener != null)
                    listener.onLogoutResult(false);
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                boolean logoutResult = false;
                String str = response.body().string();
                LogUtils.d(TAG, str);
                try {
                    JSONObject jsonObject = new JSONObject(str);
                    if (jsonObject.getString("info").equals("success")) {
                        token = null;
                        isLogon = false;
                        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("token", token);
                        editor.commit();
                        logoutResult = true;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (listener != null) {
                    listener.onLogoutResult(logoutResult);
                }
            }
        });

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
     * 处理第一级分类的返回信息
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
     * 处理第二级分类信息
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
     * 处理第三级分类返回信息
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
     * 处理请求仪器列表返回信息
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

    public static void getReceiveAddressList(String url) {
        HttpUtils.get(url, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String resStr = response.body().string();
                handleAddressResponse(resStr);
            }
        });
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
                Gson gson = new Gson();;
                ReceiveAddress address =gson.fromJson(list.getJSONObject(i).toString(), ReceiveAddress.class);
                address.save();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void editReceiverAddr(ReceiveAddress address) {
        String url = " http://www.dev.labuy.cn/app.php/Address";
        RequestBody body = new FormBody.Builder().add("id",address.getAddrId()).add("token",MyApplication.token)
                .add("name","").add("consignee",address.getConsignee()).add("email",address.getEmail())
                .add("mobile",address.getMobile()).add("regional",address.getRegional()).add("tel",address.getTel())
                .add("address",address.getAddress()).add("zip", address.getZip())
                .add("default",String.valueOf( address.isDefaulted())).build();
        HttpUtils.post(url, body, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String resStr = response.body().string();
                LogUtils.d(MyApplication.TAG,"edit_address_response="+resStr);
            }
        });
    }

    /**
     * 获取默认收件地址
     * @return
     */
    public static ReceiveAddress getDefaultReceiveAddress() {
        ReceiveAddress result = null;
        List<ReceiveAddress> allAddrs = DataSupport.findAll(ReceiveAddress.class);
        for (ReceiveAddress address : allAddrs) {
            if (address.isDefaulted())
                return address;
        }
        if (allAddrs.size() > 0) {
            result = allAddrs.get(0);
            result.setDefaulted(true);
        }
        return result;
    }

}
