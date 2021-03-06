package com.netsun.labuy.utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.view.inputmethod.InputMethodManager;

import com.netsun.labuy.activity.LoginActivity;
import com.netsun.labuy.db.Goods;
import com.netsun.labuy.db.ReceiveAddress;
import com.netsun.labuy.gson.Merchandise;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.netsun.labuy.utils.MyApplication.isLogon;
import static com.netsun.labuy.utils.MyApplication.token;

/**
 * Created by Administrator on 2017/2/27.
 */

public class PublicFunc {
    public static final int HANDLER_CODE_EDIT_OPTION = 1; //编辑商品属性的handler标识
    public static final int HANDLER_CODE_REFRESH_SHOPPINGCART = 2;//刷新购物车
    public static String work = "search_yq_by_cate_id";
    private static ProgressDialog progressDialog;
    public static String host = "http://www.dev.labuy.cn/";

    //分类检索预定义
    public static final int LEVEL_FIRST = 0;
    public static final int LEVEL_SECOND = 1;
    public static final int LEVEL_THIRD = 2;

    //商品类型
    public static final String DEVICER = "device";
    public static final String PART = "parts";
    public static final String METHOD = "method";
    //查询商品列表类型预定义
    public static final int SEARCH_BY_ID = 1;
    public static final int SEARCH_BY_KEY = 2;

    //
    public static final int REQUEST_SETTINGS = 5;

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

    public static void saveUserData(String account, String password, String aToken) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("account", account);
        editor.putString("password", password);
        editor.putString("token", aToken);
        editor.commit();
    }

    public static String getToken() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext());
        String s = preferences.getString("token", "");
        if (s != null) {
            return new String(Base64.decode(s.getBytes(), Base64.DEFAULT));
        } else
            return null;
    }

    public static String[] getUserAndPassword() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext());
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
            for (Goods item : orderInfo.getShoppingItems()) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("goodsId", item.getGoodsId())
                        .put("goodsNum", item.getNum())
                        .put("goodsAttr", item.getOptions());
                if (item.getGoods_type() == PublicFunc.DEVICER)
                    jsonObject.put("goodsType","device");
                else
                    jsonObject.put("goodsType","parts");

                list.put(jsonObject);
            }
            root.put("addrId", orderInfo.getAddressId())
                    .put("token", token)
                    .put("remark", orderInfo.getRemark())
                    .put("list", list);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        LogUtils.d(MyApplication.TAG, root.toString());
        String url = PublicFunc.host + "app.php/Order";
        RequestBody body = new FormBody.Builder().add("data", root.toString()).build();
        HttpUtils.post(url, body, callback);
    }

    /**
     * 账号登录
     *
     * @param account
     * @param password
     */
    public static boolean login(final String account, final String password) {
        boolean result = false;
        String url = PublicFunc.host + "app.php/Auth";
        try {
            OkHttpClient client = new OkHttpClient();
            RequestBody body = new FormBody.Builder().add("name", account)
                    .add("password", Md5Utils.encode(password)).build();
            Request request = new Request.Builder().url(url).post(body).build();
            Response response = client.newCall(request).execute();
            if (response != null) {
                result = Utility.handleLoginResponse(response.body().string());
                if (result) {
                    saveUserData(Base64.encodeToString(account.getBytes(), Base64.DEFAULT),
                            Base64.encodeToString(password.getBytes(), Base64.DEFAULT),
                            Base64.encodeToString(token.getBytes(), Base64.DEFAULT));

                }
                isLogon = result;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 账号登出
     */
    public static void logout(final LogoutListener listener) {
        String[] arr = getUserAndPassword();
        if (arr == null) return;
        final String url = PublicFunc.host + "app.php/Auth?name=" + arr[0] + "&token=" + token;
        LogUtils.d(MyApplication.TAG, url);
        HttpUtils.get(url, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (listener != null)
                    listener.onLogoutResult(false);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String sResponse = response.body().string();
                boolean result = Utility.handleLogoutResponse(sResponse);
                if (result) {
                    MyApplication.token = null;
                    MyApplication.isLogon = false;
                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext());
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("token", token);
                    editor.commit();
                }
                if (listener != null)
                    listener.onLogoutResult(result);
            }
        });

    }

    /**
     * 向服务器获取收件地址列表
     */
    public static void getReceiveAddressList() {
        String url = host + "app.php/Address?token=" + MyApplication.token + "&type=address";
        HttpUtils.get(url, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String resStr = response.body().string();
                LogUtils.d(MyApplication.TAG, resStr);
                Utility.handleAddressResponse(resStr);
            }
        });
    }

    /**
     * 向服务器请求更新收件地址
     *
     * @param address
     * @param callback
     */
    public static void editReceiverAddr(ReceiveAddress address, Callback callback) {
        String url = PublicFunc.host + "app.php/Address";
        RequestBody body = null;
        int defaulted;
        if (address.getAddrId() != null && !address.getAddrId().isEmpty()) {
            body = new FormBody.Builder()
                    .add("token", token)
                    .add("name", PublicFunc.getUserAndPassword()[0])
                    .add("id", address.getAddrId())
                    .add("consignee", address.getConsignee())
                    .add("mobile", address.getMobile())
                    .add("tel", address.getTel())
                    .add("email", address.getEmail())
                    .add("regional", address.getRegional())
                    .add("address", address.getAddress())
                    .add("zip", address.getZip())
                    .add("default", String.valueOf(address.getDefaulted())).build();

        } else {
            body = new FormBody.Builder()
                    .add("token", token)
                    .add("name", PublicFunc.getUserAndPassword()[0])
                    .add("consignee", address.getConsignee())
                    .add("mobile", address.getMobile())
                    .add("tel", address.getTel())
                    .add("email", address.getEmail())
                    .add("regional", address.getRegional())
                    .add("address", address.getAddress())
                    .add("zip", address.getZip())
                    .add("default", String.valueOf(address.getDefaulted())).build();
        }
        HttpUtils.post(url, body, callback);
    }

    /**
     * 删除收件地址
     *
     * @param addrId
     * @param callback
     */
    public static void deleteAddressById(String addrId, Callback callback) {
        String url = PublicFunc.host + "app.php/Address";
        RequestBody body = new FormBody.Builder().add("token", token)
                .add("delete", addrId)
                .build();
        HttpUtils.post(url, body, callback);
    }


    /**
     * 获取订单详情
     *
     * @param orderId  订单号
     * @param callback 回调方法
     * @return
     */
    public static void getOrderInfoById(String orderId, Callback callback) {
        String url = PublicFunc.host + "app.php/Order?token=" + token + "&orderId=" + orderId;
        if (callback != null) {
            HttpUtils.get(url, callback);
        }
    }

    /**
     * 向服务器请求地区信息
     *
     * @param area_id
     */
    public static boolean getAreaInfoFromServer(String area_id) {
        String url = host + "app.php/Address?type=regional&id=" + area_id;
        Response response = HttpUtils.get(url);
        if (response != null) {
            String sRes = null;
            try {
                sRes = response.body().string();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return Utility.handleCountyResponse(sRes, null);
        }
        return false;
    }

    /**
     * 获取商品详细信息
     *
     * @param yq_id
     * @param handler
     */
    public static void getMerchandise(final String mode, final String yq_id, final Handler handler) {

        String url = null;
        if (mode.equals(DEVICER))
            url = PublicFunc.host + "app.php/Yq?model=device&id=" + yq_id;
        else if (mode.equals(PART))
            url = PublicFunc.host + "app.php/Parts?model=parts&id=" + yq_id;
        else  url = "";
        LogUtils.d(MyApplication.TAG, url);
        HttpUtils.get(url, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String resText = response.body().string();
                Merchandise merchandise = Utility.handleMerchandiseResponse(resText);
                if (merchandise != null) {
                    merchandise.caterory_type = mode;
                    merchandise.id = yq_id;
                    if (handler != null) {
                        Message msg = new Message();
                        msg.what = 1;
                        msg.obj = merchandise;
                        handler.sendMessage(msg);
                    }
                }
            }
        });
    }

    public static void closeKeyBoard(Activity activity) {
        if (activity == null) return;
        ;
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(activity.getWindow().getDecorView().getWindowToken(), 0);
        }
    }
}