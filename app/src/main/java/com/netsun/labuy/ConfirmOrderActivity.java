package com.netsun.labuy;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.netsun.labuy.adapter.OrderInfoAdapter;
import com.netsun.labuy.db.ReceiveAddress;
import com.netsun.labuy.utils.OrderInfo;
import com.netsun.labuy.utils.PublicFunc;
import com.netsun.labuy.utils.SpaceItemDecoration;

import java.util.ArrayList;

public class ConfirmOrderActivity extends AppCompatActivity {
    RelativeLayout defaultAddrLayout;
    TextView consigneeTextView;//收件人
    TextView mobileTextView;//手机
    TextView addressTextView;
    TextView countTextView;
    Button submitOrderBtn;
    ImageView nextImageView;
    RecyclerView orderListView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_order);
        initView();
        setAddrInfo(PublicFunc.getDefaultReceiveAddress());

        Bundle bundle = getIntent().getExtras();
        ArrayList<OrderInfo> orders = bundle.getParcelableArrayList("order");
        String soueceStr = "共 " + orders.size() + " 件";
        String spanStr = String.valueOf(orders.size());
        SpannableStringBuilder builder = setSpannableStringBuilder(soueceStr, spanStr);
        countTextView.setText(builder);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        orderListView.setLayoutManager(layoutManager);
        OrderInfoAdapter adapter = new OrderInfoAdapter(orders);
        orderListView.setAdapter(adapter);
        int space = getResources().getDimensionPixelSize(R.dimen.space_two);
        orderListView.addItemDecoration(new SpaceItemDecoration(space));

        defaultAddrLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //选择默认收件地址
                Intent intent = new Intent(getBaseContext(), ChangeDefaultAddrActivity.class);
                startActivityForResult(intent, 1);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == RESULT_OK) {
            ReceiveAddress address = data.getParcelableExtra("address");
            setAddrInfo(address);
        }
    }

    private void setAddrInfo(ReceiveAddress address) {
        if (address != null) {
            consigneeTextView.setText(address.getConsignee());
            if (address.getMobile() != null && !address.getMobile().isEmpty())
                mobileTextView.setText(address.getMobile());
            else
                mobileTextView.setText(address.getTel());
            addressTextView.setText(address.getAddress());
        }
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

    private void initView() {
        defaultAddrLayout = (RelativeLayout) findViewById(R.id.id_default_address_rl);
        consigneeTextView = (TextView) findViewById(R.id.id_consignee_text_view);
        mobileTextView = (TextView) findViewById(R.id.id_mobile_text_view);
        addressTextView = (TextView) findViewById(R.id.id_address_text_view);
        countTextView = (TextView) findViewById(R.id.id_order_count_text_view);
        submitOrderBtn = (Button) findViewById(R.id.id_submit_order_button);
        nextImageView = (ImageView) findViewById(R.id.id_next_pic);
        orderListView = (RecyclerView) findViewById(R.id.id_order_list_view);
    }
}
