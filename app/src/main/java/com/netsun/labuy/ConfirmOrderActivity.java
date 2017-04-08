package com.netsun.labuy;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.netsun.labuy.db.ReceiveAddress;
import com.netsun.labuy.db.ShoppingItem;
import com.netsun.labuy.fragment.OrderInfoFragment;
import com.netsun.labuy.utils.HandleDataBase;
import com.netsun.labuy.utils.LogUtils;
import com.netsun.labuy.utils.MyApplication;
import com.netsun.labuy.utils.OrderInfo;
import com.netsun.labuy.utils.PublicFunc;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ConfirmOrderActivity extends AppCompatActivity {
    Toolbar toolbar;
    TextView titleTV;
    Button submitOrderBtn;
    TextView countTextView;
    LinearLayout orderInfoLinearLayout;
    OrderInfo order;

    OrderInfoFragment fragment;
    ArrayList<ShoppingItem> shoppings = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_order);
        initView();
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        Bundle bundle = getIntent().getExtras();
        shoppings = bundle.getParcelableArrayList("shoppinglist");
        titleTV.setText("确认订单");

        fragment = OrderInfoFragment.newInstance();
        fragment.setShoppingList(shoppings);
        fragment.setAddrInfo(HandleDataBase.getDefaultReceiveAddress());
        fragment.setCurrentType(OrderInfoFragment.TYPE_CORFIRM_ORDER);
        getFragmentManager().beginTransaction().replace(R.id.ll_content, fragment).commit();
        int shoppingCount = getCount();
        String soueceStr = "共 " + shoppingCount + " 件";
        String spanStr = String.valueOf(shoppingCount);
        SpannableStringBuilder builder = setSpannableStringBuilder(soueceStr, spanStr);
        countTextView.setText(builder);
        submitOrderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final OrderInfo orderInfo = new OrderInfo();
                orderInfo.setAddressId(Integer.valueOf(fragment.getCurrentAddress().getAddrId()));
                orderInfo.setShoppingItems(shoppings);
                orderInfo.setRemark(fragment.getRemark());
                PublicFunc.submitOrder(MyApplication.token, orderInfo, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String resStr = response.body().string();
                        LogUtils.d(MyApplication.TAG, resStr);
                        try {
                            JSONObject root = new JSONObject(resStr);
                            if (root.getInt("code") == 200) {
                                String orderId = String.valueOf(root.getLong("data"));
                                Intent intent = new Intent(getBaseContext(), OrderInfoActivity.class);
                                intent.putExtra("order_id", orderId);
                                startActivity(intent);
                                for (ShoppingItem item : shoppings) {
                                    HandleDataBase.deleteShoppingCartItem(item.getGoodsId());
                                }
                                finish();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }

    @NonNull

    private SpannableStringBuilder setSpannableStringBuilder(final String source, final String spanStr) {
        SpannableStringBuilder builder = new SpannableStringBuilder(source);
        int start = TextUtils.indexOf(source, spanStr);
        int end = start + spanStr.length();
        if (start >= 0 && end > start)
            builder.setSpan(new ForegroundColorSpan(Color.RED), start, end, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        return builder;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == RESULT_OK) {
            ReceiveAddress address = data.getParcelableExtra("address");
            if (address != null) {
                fragment.setAddrInfo(address);
                getFragmentManager().beginTransaction().detach(fragment).attach(fragment).commit();
            }
        }
    }


    private void initView() {
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        titleTV = (TextView) findViewById(R.id.tool_bar_title_text);
        submitOrderBtn = (Button) findViewById(R.id.id_submit_order_button);
        countTextView = (TextView) findViewById(R.id.id_order_count_text_view);
        orderInfoLinearLayout = (LinearLayout) findViewById(R.id.ll_content);

    }

    public int getCount() {
        int count = 0;
        for (ShoppingItem item : shoppings)
            count += item.getNum();
        return count;
    }
}
