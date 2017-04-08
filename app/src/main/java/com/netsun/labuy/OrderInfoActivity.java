package com.netsun.labuy;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.netsun.labuy.db.ReceiveAddress;
import com.netsun.labuy.fragment.OrderInfoFragment;
import com.netsun.labuy.utils.HandleDataBase;
import com.netsun.labuy.utils.OrderInfo;
import com.netsun.labuy.utils.PublicFunc;
import com.netsun.labuy.utils.Utility;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class OrderInfoActivity extends AppCompatActivity {
    Toolbar toolbar;
    TextView titleTV;
    RelativeLayout bottomLayout;
    OrderInfoFragment fragment;
    ReceiveAddress address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_order);
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        titleTV = (TextView) findViewById(R.id.tool_bar_title_text);
        titleTV.setText("订单详情");
        bottomLayout = (RelativeLayout) findViewById(R.id.bottom_layout);
        bottomLayout.setVisibility(View.GONE);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        final String orderId = getIntent().getStringExtra("order_id");
        PublicFunc.showProgress(this);
        if (orderId != null && !orderId.isEmpty()) {
            PublicFunc.getOrderInfoById(orderId, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    OrderInfo orderInfo = Utility.handleOrderInfoResponse(response.body().string());
                    fragment = OrderInfoFragment.newInstance();
                    if (orderInfo != null) {
                        fragment.setShoppingList(orderInfo.getShoppingItems());
                        fragment.setCurrentType(OrderInfoFragment.TYPE_VIEW_ORDER);
                        fragment.setAddrInfo(HandleDataBase.getAddrInfoById(orderInfo.getAddressId()));
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            PublicFunc.closeProgress();
                            getFragmentManager().beginTransaction().replace(R.id.ll_content, fragment).commit();
                        }
                    });
                }
            });
        }

    }
}
